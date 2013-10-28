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
import com.google.common.base.Strings;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.json.DatabagParameter;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.aws.ec2.Instance;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.InstanceBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.json.RDSConfigDatabagItem;
import com.msi.tough.rds.json.RDSDatabag;
import com.msi.tough.rds.json.RDSParameterGroupDatabagItem;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.InstanceUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.RDSMessage.DBParameterGroupStatus;
import com.transcend.rds.message.RDSMessage.DBSecurityGroupMembership;
import com.transcend.rds.message.RDSMessage.Endpoint;
import com.transcend.rds.message.RebootDBInstanceActionMessage.RebootDBInstanceActionRequestMessage;
import com.transcend.rds.message.RebootDBInstanceActionMessage.RebootDBInstanceActionResultMessage;

/**
 * @author tdhite
 */
public class RebootDBInstanceActionWorker extends
		AbstractWorker<RebootDBInstanceActionRequestMessage, RebootDBInstanceActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(RebootDBInstanceActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public RebootDBInstanceActionResultMessage doWork(
           RebootDBInstanceActionRequestMessage req) throws Exception {
       logger.debug("Performing work for RebootDBInstanceAction.");
       return super.doWork(req, getSession());
   }


	/**
	 * rebootDBInstance ********************************************************
	 * This Operation reboots a previously provisioned RDS instance. It results
	 * in the application of modified DBParameterGroup parameters with
	 * ApplyStatus of pending-reboot to the RDS instance. This action is taken
	 * as soon as possible, and results in a momentary outage to the RDS
	 * instance during which the RDS instance status is set to rebooting A
	 * DBInstance event is created when the reboot is completed. Request:
	 * DBInstanceIdentifier (R). Response: Full DBInstance record for rebooted
	 * instance Exceptions: DBInstanceNotFound InvaladDBInstance - can only
	 * reboot 1. If DBInstance does not exist return error 2. If DBInstance
	 * status not available return error 3. Call the
	 * instanceManager.rebootInstance method 4. Return response giving details
	 * of the rebooted DBInstance.
	 */
	@Override
	protected RebootDBInstanceActionResultMessage doWork0(RebootDBInstanceActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		RebootDBInstanceActionResultMessage resp = null;
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userId = ac.getId();
			final String DBInstanceId = req
					.getDbInstanceIdentifier();
			// TODO handle ForceFailOver parameter once SDK version allows it to
			// be unmarshalled
			// final boolean forceFailOver =
			// rebootDBInstance.getForceFailOver();
			logger.debug("Input parameters for RebootDBInstance action: DBInstanceIdentifier="
					+ DBInstanceId + "; Account #=" + userId);

			if (DBInstanceId == null || DBInstanceId.equals("")) {
				throw QueryFaults
						.MissingParameter("DBInstanceIdentifier has to be supplied for RebootDBInstance action.");
			}
			final RdsDbinstance inst = InstanceEntity.selectDBInstance(sess,
					DBInstanceId, userId);
			if (inst == null) {
				throw RDSQueryFaults.DBInstanceNotFound();
			}
			if (!inst.getDbinstanceStatus().equals("available")) {
				throw RDSQueryFaults.InvalidDBInstanceState();
			}

			// check if there is any DBParameter set with pending-reboot
			// ApplyMethod
			final List<RdsParameter> parameters = inst
					.getPendingRebootParameters();
			if (parameters != null && parameters.size() > 0) {
				// modify the databag
				final String databagName = "rds-" + ac.getId() + "-"
						+ inst.getDbinstanceId();

				logger.debug("Deleting the databag " + databagName);
				ChefUtil.deleteDatabagItem(databagName, "config");

				final String postWaitUrl = (String) ConfigurationUtil
						.getConfiguration(Arrays.asList(new String[] {
								"TRANSCEND_URL", inst.getAvailabilityZone() }));
				final String servletUrl = (String) ConfigurationUtil
						.getConfiguration(Arrays.asList(new String[] {
								"SERVLET_URL", inst.getAvailabilityZone() }));
				final RDSConfigDatabagItem configDataBagItem = new RDSConfigDatabagItem(
						"config", inst.getAllocatedStorage().toString(),
						inst.getMasterUsername(), inst.getMasterUserPassword(),
						inst.getAutoMinorVersionUpgrade(), inst.getEngine(),
						inst.getEngineVersion(), inst.getDbName(), inst
								.getBackupRetentionPeriod().toString(),
						inst.getPreferredBackupWindow(),
						inst.getPreferredMaintenanceWindow(), inst.getPort()
								.toString(), postWaitUrl, servletUrl,
						inst.getDbinstanceId(), "rds." + ac.getId() + "."
								+ inst.getDbinstanceId(), ac.getId(),
						inst.getDbinstanceClass(), "false");
				final RdsDbparameterGroup pGrpRec = ParameterGroupEntity
						.getParameterGroup(sess, inst.getDbParameterGroup(),
								ac.getId());
				if (pGrpRec == null) {
					throw RDSQueryFaults.InternalFailure();
				}
				final RDSParameterGroupDatabagItem parameterGroupDatabagItem = new RDSParameterGroupDatabagItem(
						"parameters", pGrpRec);
				parameterGroupDatabagItem.getParameters().remove("read_only");
				parameterGroupDatabagItem.getParameters().put(
						"read_only",
						DatabagParameter.factory("boolean",
								"" + inst.getRead_only(), true, "dynamic"));
				parameterGroupDatabagItem.getParameters().remove("port");
				parameterGroupDatabagItem.getParameters().put(
						"port",
						DatabagParameter.factory("integer",
								"" + inst.getPort(), false, "static"));
				final RDSDatabag bag = new RDSDatabag(configDataBagItem,
						parameterGroupDatabagItem);
				logger.debug("Databag: "
						+ JsonUtil.toJsonPrettyPrintString(bag));

				logger.debug("Regenerating the databag " + databagName);
				ChefUtil.createDatabagItem(databagName, "config", bag.toJson());

				inst.setPendingRebootParameters(null);
				sess.save(inst);
			}

			List<ResourcesBean> res = CFUtil.selectResourceRecords(sess,
					userId, "rds." + userId + "." + DBInstanceId, null,
					DBInstanceId, false);
			if (res.size() != 1) {
				throw RDSQueryFaults.InternalFailure();
			} else {
				// set NoWait to -1, so that the PostWait call can be executed
				final ResourcesBean dbinstanceRes = res.get(0);
				dbinstanceRes.setNoWait(-1);
				sess.save(dbinstanceRes);
			}
			res = CFUtil.selectResourceRecordsOfType(sess, userId, "rds."
					+ userId + "." + DBInstanceId, DBInstanceId, Instance.TYPE);
			if (res.size() == 1) {
				final String instanceId = res.get(0).getPhysicalId();
				final InstanceBean ib = InstanceUtil.getInstance(sess,
						instanceId);
				if (ib == null) {
					throw RDSQueryFaults
							.InternalFailure("Could not find the target instance. Try again later.");
				}
				final Instance instEngine = new Instance();
				final CallStruct call = new CallStruct();
				call.setAvailabilityZone(ib.getAvzone());
				call.setPhysicalId(instanceId);
				final AccountType at = new AccountType();
				at.setTenant(ac.getTenant());
				at.setAccessKey(ac.getAccessKey());
				at.setSecretKey(ac.getSecretKey());
				call.setAc(at);
				instEngine.reboot(call);

			} else {
				logger.error("UNKNOWN ERROR HAS OCCURED!!!");
				throw RDSQueryFaults.InternalFailure();
			}
			inst.setDbinstanceStatus("rebooting");

			final String postWaitUrl = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"TRANSCEND_URL", inst.getAvailabilityZone() }));
			if (postWaitUrl == null) {
				throw RDSQueryFaults.InternalFailure();
			}
			final String s1 = "{\"Reboot\":\"" + DBInstanceId
					+ "\", \"PostWaitUrl\":\"" + postWaitUrl + "\"}";
			ChefUtil.createDatabagItem(
					"rds-" + ac.getId() + "-" + DBInstanceId, "Reboot", s1);

			// prepare the response message
			resp = buildResponse(inst, ac);

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			final String msg = "AuthorizeDBSecurirtGoupIngress: Class: "
					+ e.getClass() + "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp;
	}

	public static RebootDBInstanceActionResultMessage buildResponse(final RdsDbinstance b,
			final AccountBean ac) {
		final RebootDBInstanceActionResultMessage.Builder instRec = RebootDBInstanceActionResultMessage.newBuilder();
		instRec.setDbInstanceIdentifier(b.getDbinstanceId());
		instRec.setReadReplicaSourceDBInstanceIdentifier(Strings.nullToEmpty(b
				.getSourceDbinstanceId()));
		instRec.addAllReadReplicaDBInstanceIdentifiers(b.getReplicas());
		instRec.setDbInstanceClass(b.getDbinstanceClass());
		instRec.setAllocatedStorage(Integer.valueOf(b.getAllocatedStorage()));
		instRec.setInstanceCreateTime(DateHelper.getISO8601Date(b.getInstanceCreateTime()));
		instRec.setDbInstanceStatus(b.getDbinstanceStatus());
		instRec.setEngine(b.getEngine());
		instRec.setEngineVersion(b.getEngineVersion());
		instRec.setAvailabilityZone(b.getAvailabilityZone());
		instRec.setMultiAZ(b.getMultiAz());
		instRec.setMasterUsername(b.getMasterUsername());
		instRec.setDbName(b.getDbName());
		instRec.setAutoMinorVersionUpgrade(b.getAutoMinorVersionUpgrade());
		instRec.setBackupRetentionPeriod(Integer.valueOf(b
				.getBackupRetentionPeriod()));
		instRec.setLatestRestorableTime(DateHelper.getISO8601Date(b.getLatestRestorableTime()));
		instRec.setPreferredBackupWindow(b.getPreferredBackupWindow());
		instRec.setPreferredMaintenanceWindow(b.getPreferredMaintenanceWindow());
		instRec.setLicenseModel(b.getLicenseModel());
		final Endpoint.Builder endpoint = Endpoint.newBuilder();
		endpoint.setAddress(b.getAddress());
		endpoint.setPort(Integer.valueOf(b.getPort()));
		instRec.setEndpoint(endpoint);
		String status = b.getDbinstanceStatus();
		if(status.equals("restoring")){
			status = "creating";
		}
		instRec.setDbInstanceStatus(status);
		final String dbparamName = b.getDbParameterGroup();
		final Collection<DBParameterGroupStatus> dBParameterGroups = new LinkedList<DBParameterGroupStatus>();
		final DBParameterGroupStatus.Builder dbparamStatus = DBParameterGroupStatus.newBuilder();
		dbparamStatus.setDbParameterGroupName(dbparamName);
		if (b.getPendingRebootParameters() == null
				|| b.getPendingRebootParameters().size() == 0) {
			dbparamStatus.setParameterApplyStatus(RDSUtilities.STATUS_IN_SYNC);
		} else {
			dbparamStatus
					.setParameterApplyStatus(RDSUtilities.STATUS_PENDING_REBOOT);
		}
		dBParameterGroups.add(dbparamStatus.buildPartial());
		instRec.addAllDbParameterGroups(dBParameterGroups);

		final Collection<DBSecurityGroupMembership> dBSecurityGroups = new LinkedList<DBSecurityGroupMembership>();
		for (final RdsDbsecurityGroup secGrp : b.getSecurityGroups()) {
			final DBSecurityGroupMembership.Builder membership = DBSecurityGroupMembership.newBuilder();
			membership.setDbSecurityGroupName(secGrp.getDbsecurityGroupName());
			membership.setStatus(secGrp.getStatus());
			dBSecurityGroups.add(membership.buildPartial());
		}
		instRec.addAllDbSecurityGroups(dBSecurityGroups);
		return instRec.buildPartial();
	}
}
