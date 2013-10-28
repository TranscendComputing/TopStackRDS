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
import org.slf4j.LoggerFactory;
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
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.RDSMessage.Parameter;
import com.transcend.rds.message.ResetDBParameterGroupActionMessage.ResetDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.ResetDBParameterGroupActionMessage.ResetDBParameterGroupActionResultMessage;

/**
 * @author tdhite
 */
public class ResetDBParameterGroupActionWorker extends
		AbstractWorker<ResetDBParameterGroupActionRequestMessage, ResetDBParameterGroupActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(ResetDBParameterGroupActionWorker.class.getName());


    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public ResetDBParameterGroupActionResultMessage doWork(
           ResetDBParameterGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for ResetDBParameterGroupAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * resetDBParameterGroup **************************************************
	 * This Operation resets the parameters associated with the named
	 * DBParameterGroup to the engine/system default value. To reset specific
	 * parameters submit a list of parameters containing at least the
	 * ParameterName and ApplyMethod. To reset the entire DBParameterGroup
	 * ResetAllParameters. When resetting the entire group, dynamic parameters
	 * are updated immediately and static parameters are set to pending-reboot
	 * to take effect on the next MySQL reboot or RebootDBInstance request.
	 * Request: DBParameterGroupName (R) ResetAllParameters Array of Parameter
	 * records Response: DBParameterGroupName Exceptions:
	 * DBParameterGroupNotFound InvalidDBParameterGroupState Processing 1.
	 * Confirm that DBParameterGroup exists and is in the appropriate state 2.
	 * scroll through all of the parameters and call resetParameter 3. Return
	 * response
	 */
	@Override
	protected ResetDBParameterGroupActionResultMessage doWork0(ResetDBParameterGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		ResetDBParameterGroupActionResultMessage.Builder resp = ResetDBParameterGroupActionResultMessage.newBuilder();
		final Logger logger = LoggerFactory
				.getLogger(this.getClass().getName());
		String msg = "";
		final Session sess = HibernateUtil.newSession();

		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userId = ac.getId();
			final String grpName0 = req.getDbParameterGroupName();
			if (grpName0 == null || "".equals(grpName0)) {
				throw QueryFaults
						.MissingParameter("DBParameterGroupName must be supplied for ResetDBParameterGroup request.");
			}
			final String grpName = ValidationManager.validateIdentifier(
					grpName0, 255, true);
			final List<Parameter> pList = req.getParametersList();
			final boolean resetAll = req.getResetAllParameters();
			final int pListLen = pList.size();

			logger.info("resetDBParameterGroup: " + " UserID = " + userId
					+ " ParameterGroupName = " + grpName
					+ " ResetAllParameters + " + resetAll
					+ " Total Number of Listed Parameters = " + pListLen);

			if (grpName == null || "".equals(grpName)) {
				throw QueryFaults
						.MissingParameter("DBParameterGroupName has to be passed for ResetDBParameterGroup request.");
			}

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

			// confirm that either resetAll or ParameterList is greater than 1
			if (resetAll == false && pListLen < 1) {
				msg = "You must either set ResetAllParameters or provide a list of parameters to be reset.";
				throw QueryFaults.MissingParameter(msg);
			}

			// check if conflicting parameters exist
			if (resetAll == true && pListLen > 0) {
				msg = "You cannot set ResetAllParameters true while providing a list of parameters to be reset.";
				throw QueryFaults.InvalidParameterCombination(msg);
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
			RdsDbparameterGroup parent = null;
			List<RdsParameter> forRebootPending = null;
			if (resetAll) {
				final List<RdsParameter> params = pGrpRec.getParameters();
				/*
				 * for(RdsParameter param : params){
				 * if(param.getApplyType().equals
				 * (RDSUtilities.PARM_APPTYPE_DYNAMIC)){ sess.delete(param); } }
				 */
				final String paramGrpFamily = pGrpRec
						.getDbparameterGroupFamily();
				final AccountBean sac = AccountUtil.readAccount(sess, 1L);
				if (paramGrpFamily.toUpperCase().equals("MYSQL5.1")) {
					logger.debug("Inserting Parameters into " + grpName
							+ " with MySQL5.1 family Parameters");
					parent = ParameterGroupEntity.getParameterGroup(sess,
							"default.mysql5.1", sac.getId());
					logger.debug("There are " + parent.getParameters().size()
							+ " parameters to copy.");
					ParameterGroupEntity.copyAndSetDynamicParamGroup(sess,
							pGrpRec, parent.getParameters(), userId);
				} else if (paramGrpFamily.toUpperCase().equals("MYSQL5.5")) {
					logger.debug("Inserting Parameters into " + grpName
							+ " with MySQL5.5 family Parameters");
					parent = ParameterGroupEntity.getParameterGroup(sess,
							"default.mysql5.5", sac.getId());
					logger.debug("There are " + parent.getParameters().size()
							+ " parameters to copy.");
					ParameterGroupEntity.copyAndSetDynamicParamGroup(sess,
							pGrpRec, parent.getParameters(), userId);
				}
			} else {
				final String paramGrpFamily = pGrpRec
						.getDbparameterGroupFamily();
				final AccountBean sac = AccountUtil.readAccount(sess, 1L);
				if (paramGrpFamily.toUpperCase().equals("MYSQL5.1")) {
					parent = ParameterGroupEntity.getParameterGroup(sess,
							"default.mysql5.1", sac.getId());
				} else if (paramGrpFamily.toUpperCase().equals("MYSQL5.5")) {
					parent = ParameterGroupEntity.getParameterGroup(sess,
							"default.mysql5.5", sac.getId());
				}
				if (parent == null) {
					throw RDSQueryFaults.InternalFailure();
				}
				forRebootPending = new LinkedList<RdsParameter>();
				for (final Parameter p : pList) {
					final RdsParameter target = ParameterGroupEntity
							.getParameter(sess, grpName, p.getParameterName(),
									userId);
					if (target == null) {
						throw RDSQueryFaults.InvalidParameterValue(p
								.getParameterName()
								+ " parameter does not exist.");
					}
					logger.debug("Current target parameter: "
							+ target.toString());
					if (!target.getIsModifiable()) {
						throw RDSQueryFaults.InvalidParameterValue(p
								.getParameterName()
								+ " is not modifiable parameter.");
					} else if (p.getApplyMethod().equals(
							RDSUtilities.PARM_APPMETHOD_IMMEDIATE)) {
						if (target.getApplyType().equals(
								RDSUtilities.PARM_APPTYPE_STATIC)) {
							throw QueryFaults
									.InvalidParameterCombination(target
											.getParameterName()
											+ " is not dynamic. You can only"
											+ " use \"pending-reboot\" as valid ApplyMethod for this parameter.");
						}
						final RdsParameter original = ParameterGroupEntity
								.getParameter(sess,
										parent.getDbparameterGroupName(),
										p.getParameterName(), 1L);
						if (original == null) {
							throw RDSQueryFaults.InvalidParameterValue(p
									.getParameterName()
									+ " could not be found in " + grpName);
						}
						target.setParameterValue(original.getParameterValue());
						target.setSource(original.getSource());
						sess.save(target);
					} else if (p.getApplyMethod().equals(
							RDSUtilities.PARM_APPMETHOD_PENDING)) {
						final RdsParameter original = ParameterGroupEntity
								.getParameter(sess,
										parent.getDbparameterGroupName(),
										p.getParameterName(), 1L);
						if (original == null) {
							throw RDSQueryFaults.InvalidParameterValue(p
									.getParameterName()
									+ " could not be found in " + grpName);
						}
						final RdsParameter temp = new RdsParameter();
						temp.setParameterName(p.getParameterName());
						temp.setApplyMethod(p.getApplyMethod());
						temp.setParameterValue(original.getParameterValue());
						forRebootPending.add(temp);
					}
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

			if (resetAll) {
				// update the database record and get the copy of the list
				forRebootPending = ParameterGroupEntity
						.copyAndSetStaticParamGroup(sess, pGrpRec,
								parent.getParameters(), userId);
			} else {
				// forRebootPending is now a list of static parameters and
				// dynamic parameters with pending-reboot ApplyMethod
				forRebootPending = ParameterGroupEntity
						.resetParamGroupWithPartialList(sess, pGrpRec,
								forRebootPending, userId);
			}

			for (final RdsDbinstance instance : dbInstances) {
				final List<RdsParameter> alreadyPending = instance
						.getPendingRebootParameters();
				if (alreadyPending == null) {
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

			// build response document - returns DBParameterGroupName
			resp.setDbParameterGroupName(grpName);

			// commit this current transaction
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "ResetDBSecurityGroup: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();

	}
}
