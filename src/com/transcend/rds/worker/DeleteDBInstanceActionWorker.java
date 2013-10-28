/**
 *
 */
package com.transcend.rds.worker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.aws.ec2.SecurityGroupIngress;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DeleteDBInstanceActionMessage.DeleteDBInstanceActionRequestMessage;
import com.transcend.rds.message.DeleteDBInstanceActionMessage.DeleteDBInstanceActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RDSMessage.DBParameterGroupStatus;
import com.transcend.rds.message.RDSMessage.DBSecurityGroupMembership;
import com.transcend.rds.message.RDSMessage.Endpoint;

/**
 * @author tdhite
 */
public class DeleteDBInstanceActionWorker extends
		AbstractWorker<DeleteDBInstanceActionRequestMessage, DeleteDBInstanceActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DeleteDBInstanceActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DeleteDBInstanceActionResultMessage doWork(
           DeleteDBInstanceActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DeleteDBInstanceAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * deleteDBInstance ******************************************************
	 * Deletes a previously provisioned RDS instance. If a final DBSnapshot is
	 * requested the status of the RDS instance will be "deleting" until the
	 * DBSnapshot is created. Delete DBInstance and any associated
	 * DBParameterGroupStatus, DBSecurityGroupMembership records Finally
	 * deCommission DBInstance Request: DBInstanceIdentifier (R)
	 * SkipFinalSnapshot FinalDBSnapshotIdentifier Response: Full Details of the
	 * DBInstance deleted Exceptions: DBInstanceNotFound DBSnapshorAlreadyExists
	 * InvalidDBInstanceState SnapshotQuotaExceeded Processing 1. Confirm that
	 * source DBInstance exists and is in correct state 2. If snapshot requested
	 * confirm that that required snapshot does not already exist and then make
	 * snapshot 3. DeCommission the DBInstance by calling
	 * deCommissionInstance(DBInstanceID) 4. Delete DBInstance record 5. Return
	 * response giving details of deleted DBInstance.
	 *
	 * @throws Exception
	 */
	@Override
	protected DeleteDBInstanceActionResultMessage doWork0(DeleteDBInstanceActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		final String instanceID = req.getDbInstanceIdentifier();
		final String snapshotID = req
				.getFinalDBSnapshotIdentifier();
		DBInstance dbInst = null;
		DeleteDBInstanceActionResultMessage.Builder resp = DeleteDBInstanceActionResultMessage.newBuilder();
		String msg = "";
		AccountBean ac = null;
		String stackId = null;
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			ac = context.getAccountBean();
			final long userID = ac.getId();
			final boolean skipSnapshot = RDSUtilities
					.defaultFalse(req.getSkipFinalSnapshot());

			logger.info("DeleteDBInstance: " + " UserID = " + userID
					+ " InstanceID = " + instanceID + " SkipSnapshot = "
					+ skipSnapshot + " SnapshotID = " + snapshotID);

			// check the required parameter
			if (instanceID == null || "".equals(instanceID)) {
				throw QueryFaults
						.MissingParameter("DBInstanceIdentifier has to be supplied for DeleteDBInstance request.");
			}

			// check that instance exists
			final RdsDbinstance instRec = RDSUtil.getInstance(sess, instanceID,
					ac.getId());
			if (instRec == null) {
				throw RDSQueryFaults.DBInstanceNotFound();
			}
			stackId = "rds." + ac.getId() + "." + instRec.getDbinstanceId();

			// check to make sure that DBInstance is in a state that permits this operation
			String status = instRec.getDbinstanceStatus();
			if(status.equals(RDSUtilities.STATUS_REBOORING) || status.equals(RDSUtilities.STATUS_BACKING_UP)
					|| status.equals(RDSUtilities.STATUS_DELETED) || status.equals(RDSUtilities.STATUS_DELETING)
					|| status.equals(RDSUtilities.STATUS_MODIFYING)){
				throw RDSQueryFaults.InvalidDBInstanceState("DBInstance cannot be deleted while it's in " + status + " state.");
			}

			// check if SkipFinalSnapshot is set to false and
			// FinalDBSnapshotIdentifier is null
			if (!skipSnapshot && snapshotID == null) {
				throw QueryFaults
						.MissingParameter("FinalDBSnapshotIdentifier has to be supplied when SkipFinalSnapshot"
								+ " is set to false.");
			}

			// make the snapshot if requested
			if (!skipSnapshot && snapshotID != null) {
				final RdsSnapshot snapshot0 = SnapshotEntity.selectSnapshot(
						sess, null, snapshotID, userID);
				if (snapshot0 != null) {
					throw RDSQueryFaults.DBSnapshotAlreadyExists();
				}
				if (instRec.getSourceDbinstanceId() != null) {
					throw RDSQueryFaults
							.InvalidDBInstanceState("Cannot create a snapshot because the DB Instance is a read replica.");
				}
			}

			// modify the DBInstanceStatus of the target DBInstance
			logger.debug("Deletion of DBInstance started.");
			instRec.setDbinstanceStatus(RDSUtilities.STATUS_DELETING);
			instRec.setFinalSnapshotId(snapshotID);
			sess.save(instRec);

			EventUtil.addEvent(sess, ac.getId(), "Database instance shutdown",
					"db-instance", new String[] { instanceID });
			dbInst = toDBInstance(instRec, ac);
			final String message = "Database instance deleted";
			EventUtil.addEvent(sess, userID, message, "db-instance",
					new String[] { instanceID });
			logger.debug("DeleteDBInstance completed!");
			sess.getTransaction().commit();

			final AccountBean ac0 = ac;
			final String stackId0 = stackId;
			final ExecutorService exsrv = Appctx.getExecutorService();
			final Runnable r = new Runnable() {
				@Override
				public void run() {
					HibernateUtil
							.withNewSession(new HibernateUtil.Operation<Object>() {
								@Override
								public Object ex(final Session s,
										final Object... as) throws Exception {
									AccountType daseinAt = new AccountType();
									daseinAt.setAccessKey(ac0.getAccessKey());
									daseinAt.setSecretKey(ac0.getSecretKey());
									daseinAt.setTenant(ac0.getTenant());
									daseinAt.setId(ac0.getId());
									CFUtil.deleteStackResources(daseinAt, stackId0, null,
											null);
									return null;
								}
							});
					HibernateUtil
					.withNewSession(new HibernateUtil.Operation<Object>() {
						@Override
						public Object ex(final Session s0,
								final Object... as) throws Exception {
							// finally clean up any resources that hasn't been cleared by CFUtil.deleteStackResources()
							// if security group is delete before removing security group ingresses, their records remain
							logger.debug("Looking for the remaining resources under the stack: " + stackId0);
							List<ResourcesBean> remaining = CFUtil.selectResourceRecordsOfType(s0, userID, stackId0,
									null, SecurityGroupIngress.TYPE);

							logger.debug("Remaining ResourcesBean list: " + remaining);
							if(remaining != null && remaining.size() > 0){
								logger.debug("Additional resources found to be cleaned up; size = " + remaining.size());
								for(ResourcesBean r : remaining){
									s0.delete(r);
								}
							}
							return null;
						}
					});
				}
			};
			exsrv.submit(r);
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DeleteInstance: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}

		resp.setDbInstance(dbInst);
		return resp.buildPartial();
	}

	public static DBInstance toDBInstance(final RdsDbinstance b,
			final AccountBean ac) {
		final DBInstance.Builder instRec = DBInstance.newBuilder();
		instRec.setDbInstanceIdentifier(b.getDbinstanceId());
		instRec.setReadReplicaSourceDBInstanceIdentifier(Strings.nullToEmpty(b
				.getSourceDbinstanceId()));
		if(b.getReplicas() != null && b.getReplicas().size() > 0){
			instRec.addAllReadReplicaDBInstanceIdentifiers(b.getReplicas());
		}
		instRec.setDbInstanceClass(Strings.nullToEmpty(b.getDbinstanceClass()));
		instRec.setAllocatedStorage(Integer.valueOf(b.getAllocatedStorage()));
		instRec.setInstanceCreateTime(DateHelper.getISO8601Date(b.getInstanceCreateTime()));
		instRec.setDbInstanceStatus(Strings.nullToEmpty(b.getDbinstanceStatus()));
		instRec.setEngine(Strings.nullToEmpty(b.getEngine()));
		instRec.setEngineVersion(Strings.nullToEmpty(b.getEngineVersion()));
		instRec.setAvailabilityZone(Strings.nullToEmpty(b.getAvailabilityZone()));
		instRec.setMultiAZ(b.getMultiAz());
		instRec.setMasterUsername(Strings.nullToEmpty(b.getMasterUsername()));
		instRec.setDbName(Strings.nullToEmpty(b.getDbName()));
		instRec.setAutoMinorVersionUpgrade(b.getAutoMinorVersionUpgrade());
		instRec.setBackupRetentionPeriod(Integer.valueOf(b
				.getBackupRetentionPeriod()));
		instRec.setLatestRestorableTime(DateHelper.getISO8601Date(b.getLatestRestorableTime()));
		instRec.setPreferredBackupWindow(b.getPreferredBackupWindow());
		instRec.setPreferredMaintenanceWindow(b.getPreferredMaintenanceWindow());
		instRec.setLicenseModel(b.getLicenseModel());
		final Endpoint.Builder endpoint = Endpoint.newBuilder();
		endpoint.setAddress(Strings.nullToEmpty(b.getAddress()));
		endpoint.setPort(Integer.valueOf(b.getPort()));
		instRec.setEndpoint(endpoint);
		String status = (Strings.nullToEmpty(b.getDbinstanceStatus()));
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
