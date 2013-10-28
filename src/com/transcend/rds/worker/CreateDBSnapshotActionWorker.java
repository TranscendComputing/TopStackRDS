/**
 *
 */
package com.transcend.rds.worker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.core.BaseProvider;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBSnapshotActionMessage.CreateDBSnapshotActionRequestMessage;
import com.transcend.rds.message.CreateDBSnapshotActionMessage.CreateDBSnapshotActionResultMessage;

/**
 * @author rarora
 */
public class CreateDBSnapshotActionWorker extends
		AbstractWorker<CreateDBSnapshotActionRequestMessage, CreateDBSnapshotActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(CreateDBSnapshotActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public CreateDBSnapshotActionResultMessage doWork(
           CreateDBSnapshotActionRequestMessage req) throws Exception {
       logger.debug("Performing work for CreateDBSnapshotAction.");
       return super.doWork(req, getSession());
   }


	/**
	 * createDBSnapshot *******************************************************
	 * This Operation is used to create a DBSnapshot. The source DBInstance must
	 * be in an "available" state. Request: DBInstanceIdentifier(R) The
	 * identifier of an existing Instance to be snapshot DBSnapshotIdentifier(R)
	 * The identifier to be used for the new snapshot Response: DBSnapshot
	 * Record giving full details of snapshot created Exceptions:
	 * SnapshotQuotaExceeded InvalidDBInstanceState DBSnapshotAlreadyExists
	 * DBInstanceNotFound Processing 1. Confirm that DBInstance exists and is in
	 * appropriate state 2. Check quotas 3. Create snapshot 4. Create DBSnapshot
	 * database record 5. return response to user
	 */
	@Override
	protected CreateDBSnapshotActionResultMessage doWork0(CreateDBSnapshotActionRequestMessage req,
ServiceRequestContext context) throws Exception {


		final Session sess = HibernateUtil.newSession();
		CreateDBSnapshotActionResultMessage.Builder result = CreateDBSnapshotActionResultMessage.newBuilder();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final String dbIdentifier = req.getDbInstanceIdentifier();
			final String snapshotIdentifier = req
					.getDbSnapshotIdentifier();

			final RdsDbinstance dbInstance = InstanceEntity.selectDBInstance(
					sess, dbIdentifier, ac.getId());
			if (dbInstance == null) {
				logger.debug("No DBInstance found with identifier="
						+ dbIdentifier);
				throw RDSQueryFaults.DBInstanceNotFound();
			}

			if (dbInstance.getSourceDbinstanceId() != null) {
				throw RDSQueryFaults
						.InvalidDBInstanceState("Cannot create a snapshot because the target DB Instance is a read replica.");
			}

			final RdsSnapshot snapshot = SnapshotEntity.selectSnapshot(sess,
					dbIdentifier, snapshotIdentifier, ac.getId());
			if (snapshot != null) {
				logger.debug("DBSnapshot with the same identifier already exists: "
						+ snapshotIdentifier);
				throw RDSQueryFaults.DBSnapshotAlreadyExists();
			}

			if (!dbInstance.getDbinstanceStatus().equals("available")) {
				logger.debug("DBInstanceStatus is not set to available.");
				throw RDSQueryFaults.InvalidDBInstanceState();
			}

			dbInstance.setDbinstanceStatus(RDSUtilities.STATUS_BACKING_UP);
			sess.save(dbInstance);

			// prepare and send response
			result.setAllocatedStorage(dbInstance.getAllocatedStorage());
			result.setAvailabilityZone(dbInstance.getAvailabilityZone());
			result.setDbInstanceIdentifier(dbIdentifier);
			result.setDbSnapshotIdentifier(snapshotIdentifier);
			result.setEngine(dbInstance.getEngine());
			result.setEngineVersion(dbInstance.getEngineVersion());
			result.setInstanceCreateTime(DateHelper.getISO8601Date(dbInstance.getInstanceCreateTime()));
			result.setLicenseModel(dbInstance.getLicenseModel());
			result.setMasterUsername(dbInstance.getMasterUsername());
			result.setPort(dbInstance.getPort());
			result.setSnapshotCreateTime(DateHelper.getISO8601Date(new Date()));
			result.setStatus("creating");

			EventUtil.addEvent(sess, ac.getId(),
					"Beginning user snapshot creation", "db-snapshot",
					new String[] { snapshotIdentifier });

			final Executable r = new ExecutorHelper.Executable() {
				@Override
				public void run() {
					HibernateUtil
							.withNewSession(new HibernateUtil.Operation<Object>() {
								@Override
								public Object ex(final Session s,
										final Object... as) throws Exception {
									final BaseProvider snapshotEngine = new com.msi.tough.engine.aws.rds.DBSnapshot();
									final CallStruct call = new CallStruct();
									call.setName(snapshotIdentifier);
									call.setStackId(snapshotIdentifier);
									call.setAvailabilityZone(dbInstance.getAvailabilityZone());
									final AccountType at = AccountUtil.toAccount(ac);
									call.setAc(at);
									final Map<String, Object> properties = new HashMap<String, Object>();
									properties.put("DBInstanceIdentifier",
											dbIdentifier);
									properties.put("DBSnapshotIdentifier",
											snapshotIdentifier);
									call.setProperties(properties);
									call.setType(com.msi.tough.engine.aws.rds.DBSnapshot.TYPE);
									return snapshotEngine.create(call);
								}
							});
				}
			};
			ExecutorHelper.execute(r);

			sess.getTransaction().commit();
			// the rest of the CreateDBSnapshot (detaching volume, modifying
			// DBSnapshot status) will take place when chef-client
			// finishes running and SignalCreateDBSnapshot is called
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			final String msg = "CreateDBSnapshot: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}

		return result.buildPartial();
	}
}
