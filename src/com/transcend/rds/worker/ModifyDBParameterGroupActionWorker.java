/**
 * 
 */
package com.transcend.rds.worker;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.json.DatabagParameter;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.rds.json.RDSConfigDatabagItem;
import com.msi.tough.rds.json.RDSDatabag;
import com.msi.tough.rds.json.RDSParameterGroupDatabagItem;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.ModifyDBParameterGroupActionMessage.ModifyDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.ModifyDBParameterGroupActionMessage.ModifyDBParameterGroupActionResultMessage;
import com.transcend.rds.message.RDSMessage.Parameter;

/**
 * @author tdhite
 */
public class ModifyDBParameterGroupActionWorker extends 
		AbstractWorker<ModifyDBParameterGroupActionRequestMessage, ModifyDBParameterGroupActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(ModifyDBParameterGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public ModifyDBParameterGroupActionResultMessage doWork(
           ModifyDBParameterGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for ModifyDBParameterGroupAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * modifyDBParameterGroup ************************************************
	 * This Operation modifies the parameters associated with the named
	 * DBParameterGroup. It essentially adds/updates parameters associated with
	 * a DBParameterGroup If parameter exists then update if parameter doesn't
	 * exist then insert Request: DBParameterGroupName(R) List of Parameter
	 * records(R) Parameters: List of up to 20 parameter records Response:
	 * DBParameterGroup Exceptions: DBParameterGroupNotFound
	 * InvalidDBParameterGroupState Processing 1. Confirm that ParamaterGroup
	 * exists and is in the appropriate state 2. Update the Parameter records by
	 * inserting or updating new parameter 3. Return response
	 */
	@Override
	protected ModifyDBParameterGroupActionResultMessage doWork0(ModifyDBParameterGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		logger.debug("ModifyDBParameterGroup action is called.");
		final Session sess = HibernateUtil.newSession();
		final AccountBean ac = context.getAccountBean();
		final ModifyDBParameterGroupActionResultMessage.Builder resp = ModifyDBParameterGroupActionResultMessage.newBuilder();

		try {
			sess.beginTransaction();

			final long userId = ac.getId();
			final String grpName = ValidationManager.validateIdentifier(
					req.getDbParameterGroupName(), 255, true);
			final List<Parameter> pList = req.getParametersList();
			final int pListLen = pList.size();

			logger.info("ModifyDBParameterGroup: " + " UserID = " + userId
					+ " ParameterGroupName = " + grpName
					+ " Total Number of Listed Parameters = " + pListLen);

			if (grpName.equals("default.mysql5.5")) {
				throw RDSQueryFaults
						.InvalidClientTokenId("You do not have privilege to modify default DBParameterGroup.");
			}

			// check that DBParameterGroup exists
			final RdsDbparameterGroup pGrpRec = ParameterGroupEntity
					.getParameterGroup(sess, grpName, ac.getId());
			if (pGrpRec == null) {
				throw RDSQueryFaults.DBParameterGroupNotFound();
			}

			final Collection<RdsDbinstance> dbInstances = InstanceEntity
					.selectDBInstancesByParameterGroup(sess, grpName, -1, ac);

			// make sure that all DBInstances using this DBParameterGroup are in
			// available state
			for (final RdsDbinstance dbinstance : dbInstances) {
				if (!dbinstance.getDbinstanceStatus().equals(
						RDSUtilities.STATUS_AVAILABLE)) {
					throw RDSQueryFaults
							.InvalidDBParameterGroupState("Currently there are DBInstance(s) that use this DBParameterGroup and it"
									+ " is not in available state.");
				}
			}

			// reset the parameters in the DB
			List<RdsParameter> forRebootPending = new LinkedList<RdsParameter>();
			final String paramGrpFamily = pGrpRec.getDbparameterGroupFamily();
			final AccountBean sac = AccountUtil.readAccount(sess, 1L);
			for (final Parameter p : pList) {
				final RdsParameter target = ParameterGroupEntity.getParameter(
						sess, grpName, p.getParameterName(), userId);
				if (target == null) {
					throw RDSQueryFaults.InvalidParameterValue(p
							.getParameterName() + " parameter does not exist.");
				}
				logger.debug("Current target parameter: " + target.toString());
				if (!target.getIsModifiable()) {
					throw RDSQueryFaults.InvalidParameterValue(p
							.getParameterName()
							+ " is not modifiable parameter.");
				}
				// TODO validate p.getParameterValue along with
				// p.getParameterName to ensure the value is allowed
				else if (p.getApplyMethod().equals(
						RDSUtilities.PARM_APPMETHOD_IMMEDIATE)) {
					if (target.getApplyType().equals(
							RDSUtilities.PARM_APPTYPE_STATIC)) {
						throw QueryFaults
								.InvalidParameterCombination(target
										.getParameterName()
										+ " is not dynamic. You can only"
										+ " use \"pending-reboot\" as valid ApplyMethod for this parameter.");
					}
					target.setParameterValue(p.getParameterValue());
					target.setSource(Constants.USER);
					sess.save(target);
				} else if (p.getApplyMethod().equals(
						RDSUtilities.PARM_APPMETHOD_PENDING)) {
					final RdsParameter temp = new RdsParameter();
					temp.setParameterName(p.getParameterName());
					temp.setApplyMethod(p.getApplyMethod());
					temp.setParameterValue(p.getParameterValue());
					forRebootPending.add(temp);
				}
			}

			// Delete and regenerate the Databag
			logger.debug("There are " + dbInstances.size()
					+ " databags to modify.");
			for (final RdsDbinstance instance : dbInstances) {
				logger.debug("Currently updating the databag for DBInstance "
						+ instance.getDbinstanceId());
				final String databagName = "rds-" + ac.getId() + "-"
						+ instance.getDbinstanceId();
				logger.debug("Deleting the databag " + databagName);
				ChefUtil.deleteDatabagItem(databagName, "config");

				final String postWaitUrl = (String) ConfigurationUtil
						.getConfiguration(Arrays.asList(new String[] {
								"TRANSCEND_URL", instance.getAvailabilityZone() }));
				final String servletUrl = (String) ConfigurationUtil
						.getConfiguration(Arrays.asList(new String[] {
								"SERVLET_URL", instance.getAvailabilityZone() }));
				final RDSConfigDatabagItem configDataBagItem = new RDSConfigDatabagItem(
						"config", instance.getAllocatedStorage().toString(),
						instance.getMasterUsername(),
						instance.getMasterUserPassword(),
						instance.getAutoMinorVersionUpgrade(),
						instance.getEngine(), instance.getEngineVersion(),
						instance.getDbName(), instance
								.getBackupRetentionPeriod().toString(),
						instance.getPreferredBackupWindow(),
						instance.getPreferredMaintenanceWindow(), instance
								.getPort().toString(), postWaitUrl, servletUrl,
						instance.getDbinstanceId(), "rds." + ac.getId() + "."
								+ instance.getDbinstanceId(), ac.getId(), instance.getDbinstanceClass(), "false");
				final RDSParameterGroupDatabagItem parameterGroupDatabagItem = new RDSParameterGroupDatabagItem(
						"parameters", pGrpRec);
				parameterGroupDatabagItem.getParameters().remove("read_only");
				parameterGroupDatabagItem.getParameters().put(
						"read_only",
						DatabagParameter.factory("boolean",
								"" + instance.getRead_only(), true, "dynamic"));
				parameterGroupDatabagItem.getParameters().remove("port");
				parameterGroupDatabagItem.getParameters().put(
						"port",
						DatabagParameter.factory("integer",
								"" + instance.getPort(), false, "static"));
				final RDSDatabag bag = new RDSDatabag(configDataBagItem,
						parameterGroupDatabagItem);
				logger.debug("Databag: "
						+ JsonUtil.toJsonPrettyPrintString(bag));

				logger.debug("Regenerating the databag " + databagName);
				ChefUtil.createDatabagItem(databagName, "config", bag.toJson());
			}

			if (forRebootPending != null && forRebootPending.size() > 0) {
				// forRebootPending is now a list of static parameters and
				// dynamic parameters with pending-reboot ApplyMethod
				forRebootPending = ParameterGroupEntity
						.modifyParamGroupWithPartialList(sess, pGrpRec,
								forRebootPending, userId);

				// code below may need to be rewritten for better performance;
				// Hibernate may be useful to improve the snippet below
				for (final RdsDbinstance instance : dbInstances) {
					final List<RdsParameter> alreadyPending = instance
							.getPendingRebootParameters();
					if (alreadyPending == null || alreadyPending.size() == 0) {
						instance.setPendingRebootParameters(forRebootPending);
						// instance.setDbinstanceStatus(RDSUtilities.STATUS_MODIFYING);
						sess.save(instance);
					} else {
						for (final RdsParameter newParam : forRebootPending) {
							boolean found = false;
							int i = 0;
							while (!found && i < alreadyPending.size()) {
								if (alreadyPending.get(i).getParameterName()
										.equals(newParam.getParameterName())) {
									alreadyPending.get(i).setParameterValue(
											newParam.getParameterValue());
									found = true;
								}
								++i;
							}
							if (!found) {
								alreadyPending.add(newParam);
							}
						}
					}
				}
			}

			// build response document - returns DBParameterGroupName
			resp.setDbParameterGroupName(grpName);

			logger.debug("Committing all the changes...");
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			final String msg = "CreateInstance: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
}
