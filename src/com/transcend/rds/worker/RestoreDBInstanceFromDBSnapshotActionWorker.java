/**
 *
 */
package com.transcend.rds.worker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.aws.ec2.Volume;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbengine;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.EngineEntity;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionRequestMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionRequestMessage;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionResultMessage;

/**
 * @author tdhite
 */
public class RestoreDBInstanceFromDBSnapshotActionWorker extends
		AbstractWorker<RestoreDBInstanceFromDBSnapshotActionRequestMessage, RestoreDBInstanceFromDBSnapshotActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(RestoreDBInstanceFromDBSnapshotActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public RestoreDBInstanceFromDBSnapshotActionResultMessage doWork(
           RestoreDBInstanceFromDBSnapshotActionRequestMessage req) throws Exception {
       logger.debug("Performing work for RestoreDBInstanceFromDBSnapshotAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * restoreDBInstanceFromDBSnapshot ****************************************
	 * This Operation creates a new DBInstance from an existing snapshot. Note:
	 * There is limited documentation on Amazon RDS and it appears that the
	 * description of restoreDBInstanceFromDBSnapshot and
	 * restoreDBInstanceToPointInTime have been reversed. The target database is
	 * created from the source database with the same configuration as the
	 * original database except that the DB instance is created with the default
	 * DB security group. Request: DBSnapshotIdentifier(R)
	 * DBInstanceIdentifier(R) The identifier of new instance DBInstanceClass
	 * AvailabilityZone Port MultiAZ AutoMinorVersionUpgrade Response:
	 * DBInstance record giving details of instance that was restored
	 * Exceptions: DBSnapshotNotFound InvalidDBSnapshotState
	 * DBInstanceAlreadyExists InstanceQuotaExceeded
	 * InsufficientDBInstanceCapacity StorageQuotaExceeded Processing 1.
	 * Validate and insert the newInstance record 2. call the BackupManager to
	 * restore to this new instance 3. Select and return the new instance
	 * details to the user.
	 */
	@Override
	protected RestoreDBInstanceFromDBSnapshotActionResultMessage doWork0(RestoreDBInstanceFromDBSnapshotActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		String msg = "";
		DBInstance dbInst = null;
		final Session sess = HibernateUtil.newSession();

		try {
			sess.beginTransaction();

			// pull the record of the snapshot; make sure it exists
			final AccountBean ac = context.getAccountBean();
			final String dbSnapshotId = req.getDbSnapshotIdentifier();
			final String dbInstanceId = req.getDbInstanceIdentifier();
			if(dbSnapshotId == null){
				throw RDSQueryFaults.MissingParameter("DBSnapshotId must be supplied for RestoreDBInstanceFromDBSnapshot action.");
			}
			if(dbInstanceId == null){
				throw RDSQueryFaults.MissingParameter("DBInstanceIdentifier must be supplied for RestoreDBInstanceFromDBSnapshot action.");
			}
			final RdsSnapshot snapshot = SnapshotEntity.selectSnapshot(sess,
					null, dbSnapshotId, ac.getId());
			if(snapshot == null){
				logger.debug("DBSnapshot is not found.");
				throw RDSQueryFaults.DBSnapshotNotFound();
			}

			// pull out the parameters from the request obj; don't bother with
			// DBName till Oracle DB is being used
			final boolean autoMinorUpgrade = req
					.getAutoMinorVersionUpgrade();
			String avZone = req.getAvailabilityZone();
			if (avZone == null || "".equals(avZone)) {
				avZone = snapshot.getAvailabilityZone();
			}
			String DBInstClass = req.getDbInstanceClass();
			if (DBInstClass == null || "".equals(DBInstClass)) {
				DBInstClass = snapshot.getDbinstanceClass();
			}
			// String DBName;
			String engine = req.getEngine();
			if (engine == null || "".equals(engine)) {
				engine = snapshot.getEngine();
				logger.debug("No Engine is selected by the user; the Engine of the previous DBInstance is chosen as default.");
			}
			String license = req.getLicenseModel();
			if (license == null || "".equals(license)) {
				license = "general-public-license";
				logger.debug("No LicenseModel is selected by the user; general-public-license is chosen as default.");
			}
			final boolean multiAZ = req.getMultiAZ();
			if(multiAZ && (avZone != null && !"".equals(avZone))){
				throw QueryFaults.InvalidParameterCombination();
			}
			int port = req.getPort();
			if (port == -1) {
				port = snapshot.getPort();
			}

			// check to make sure AvailabilityZone is not passed while MultiAZ
			// is set to true
			if(multiAZ && avZone != null && !"".equals(avZone)){
				logger.debug("MultiAZ is set to true while AvailabilityZone parameter is also passed! Error! Abort!");
				throw RDSQueryFaults.InvalidParameterCombination("MultiAZ parameter and AvailabilityZone parameter cannot be used together.");
			}else if(!multiAZ && (avZone == null || "".equals(avZone))){
				//avZone = ac.getDefZone();
				avZone = snapshot.getAvailabilityZone();
				logger.debug("MultiAZ is set to false, but AvailabilityZone is not selected. nova-essex is set as the default for the AvailabilityZone");
			}

			final RdsDbinstance check = InstanceEntity.selectDBInstance(sess,
					dbInstanceId, ac.getId());
			if(check != null){
				throw RDSQueryFaults.DBInstanceAlreadyExists();
			}

			final String dbSecurityGroupName = "default";
			final String version = snapshot.getEngineVersion();
			final String majorVersion = version.substring(0, 3);
			String dbParamGrpFamily = null;
			//TODO check if engine matches the engine registered with the snapshot
			if(engine.toLowerCase().equals("mysql")){
				dbParamGrpFamily = engine.toLowerCase() + majorVersion;
			}else if(engine.toLowerCase().equals("oracle")){
				// TODO
			}
			if(dbParamGrpFamily == null){
				throw RDSQueryFaults.InternalFailure();
			}
			List<RdsDbengine> engines = EngineEntity.selectDBEngineVersions(sess, null, dbParamGrpFamily, engine.toLowerCase(), majorVersion, true, null, -1);
			if(engines == null || engines.size() != 1){
				throw RDSQueryFaults.InternalFailure();
			}
			final String dbParamGrpName = "default." + dbParamGrpFamily;

			// first create a DBInstance; EngineVersion, PreferredBakcupWindow,
			// and PreferredMaintenanceWindow are set to default
			logger.debug("Calling CreateDBInstance with saved information from the snapshot...");
			final CreateDBInstanceActionRequestMessage.Builder createDBInstanceReq =
					CreateDBInstanceActionRequestMessage.newBuilder();
			createDBInstanceReq
					.setAllocatedStorage(snapshot.getAllocatedStorage());
			createDBInstanceReq.setAutoMinorVersionUpgrade(autoMinorUpgrade);
			createDBInstanceReq.setAvailabilityZone(avZone);
			createDBInstanceReq.setMultiAZ(multiAZ);
			createDBInstanceReq.setDbInstanceClass(snapshot.getDbinstanceClass());
			createDBInstanceReq.setDbInstanceIdentifier(dbInstanceId);
			createDBInstanceReq.addAllDbSecurityGroups(Arrays.asList(new String[] { dbSecurityGroupName }));
			createDBInstanceReq.setEngine(engine);
			createDBInstanceReq.setEngineVersion("5.5.20");
			createDBInstanceReq.setLicenseModel(license);
			createDBInstanceReq.setMasterUsername(snapshot.getMasterUsername());
			createDBInstanceReq.setMasterUserPassword(snapshot.getMasterPasswd());
			createDBInstanceReq.setBackupRetentionPeriod(1);
			createDBInstanceReq.setPreferredBackupWindow("05:30-06:00");
			createDBInstanceReq
					.setPreferredMaintenanceWindow("sat:05:00-sat:05:30");
			createDBInstanceReq.setPort(port);
			createDBInstanceReq.setDbParameterGroupName(dbParamGrpName);
			final CreateDBInstanceActionWorker createAction = new CreateDBInstanceActionWorker();
			dbInst = createAction.createDBInstance(createDBInstanceReq.buildPartial(),
					context, true);
			logger.debug("DBInstance creating: " + dbInst.toString());

			// start a new thread to do the processing while this one finishes
			// and returns the response message
			final String avZone0 = avZone;

			final Executable r = new ExecutorHelper.Executable() {
				@Override
				public void run() {
					HibernateUtil
							.withNewSession(new HibernateUtil.Operation<Object>() {
								@Override
								public Object ex(final Session s,
										final Object... as) throws Exception {
									restoreHelper(s, ac, dbSnapshotId,
											dbInstanceId, snapshot, avZone0);
									return null;
								}
							});
				}
			};
			ExecutorHelper.execute(r);

		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "CreateInstance: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		RestoreDBInstanceFromDBSnapshotActionResultMessage.Builder resp =
				RestoreDBInstanceFromDBSnapshotActionResultMessage.newBuilder();
		resp.setDbInstance(dbInst);
		return resp.buildPartial();
	}

	@SuppressWarnings("static-access")
	private Session restoreHelper(Session sess, final AccountBean ac,
			final String dbSnapshotId, final String dbInstanceId,
			final RdsSnapshot snapshot, final String avZone)
			throws InterruptedException, Exception {
		// get the physicalId of the DBInstance
		logger.debug("Locking before accessing the critical database table...");
		Thread.sleep(180000);
		boolean wait = true;
		int count = 0;
		while (wait) {
			logger.debug("Waiting for Resource(s) to be created... " + count
					+ "th trial!");
			Thread.sleep(30000);
			logger.debug("Renewing the session...");
			sess.close();
			sess = HibernateUtil.newSession();
			sess.beginTransaction();
			final RdsDbinstance inst = InstanceEntity.selectDBInstance(sess,
					dbInstanceId, ac.getId());
			String stat = null;
			if (inst != null) {
				if(count > 15){
					// handle cases where DBInstance ended up with error status or got deleted before actual restoring work
					wait = false;
					break;
				}
				stat = inst.getDbinstanceStatus();
			}
			logger.debug("DBInstance existence: " + inst
					+ "; DBInstanceStatus: " + stat);
			if (inst != null) {
				if(stat.equals("restoring")){
					inst.setDbinstanceStatus("creating");
					sess.save(inst);
					sess.getTransaction().commit();
					wait = false;
					break;
				}
				if(stat.equals("error")){
					// break away if the DBInstance ended up with "error" status before restoration process begin
					wait = false;
					break;
				}
			}

			++count;
		}
		final List<ResourcesBean> resources = CFUtil.selectResourceRecords(
				sess, ac.getId(), "rds." + ac.getId() + "." + dbInstanceId,
				dbInstanceId, null, false);
		final ResourcesBean instanceRes = resources.get(0);
		final String physicalId = instanceRes.getPhysicalId();
		logger.debug("Unlocking after accessing the critical database table...");

		// modify the databag to signal restoration recipe to run
		logger.debug("Adding another data bag item to singal the restoration to chef-client");
		final String databagName = "rds-" + ac.getId() + "-" + dbInstanceId;
		final String s1 = "{\"DBSnapshotIdentifier\":\"" + dbSnapshotId
				+ "\", " + "\"OriginalDBInstanceIdentifier\":\""
				+ snapshot.getDbinstanceId() + "\"}";
		logger.debug("RestoreDBInstance data bag item: " + s1);

		ChefUtil.createDatabagItem(databagName, "RestoreDBInstance", s1);
		return sess;
	}

	public static class AttachDBSnapshot extends UnsecuredAction {

		@SuppressWarnings("static-access")
		@Override
		public String process0(Session session, HttpServletRequest req,
				HttpServletResponse resp, Map<String, String[]> map)
				throws Exception {
			final long acId = QueryUtil.getLong(map, "AcId");
			final String stackId = QueryUtil.getString(map, "StackId");
			final String device = QueryUtil.getString(map, "Device");
			final String snapshotId = QueryUtil.getString(map, "Snapshot");
			logger.debug("Attach DB Snapshot: " + "AccountId = " + acId + "; StackId = " + stackId);

			AccountBean ac = AccountUtil.readAccount(session, acId);
			AccountType at = AccountUtil.toAccount(ac);

			String instanceId = null;
			List<ResourcesBean> rList = CFUtil.selectResourceRecordsOfType(session, acId, stackId, null, com.msi.tough.engine.aws.rds.DBInstance.TYPE);
			if(rList == null || rList.size() != 1){
				logger.debug("There should be only one resource of this type with the given stack id.");
				// TODO signal fail hook for the stackID
			}
			ResourcesBean r = rList.get(0);
			String dbInstId = r.getPhysicalId();

			RdsDbinstance instance = InstanceEntity.selectDBInstance(session, dbInstId, acId);
			if(instance == null){
				logger.debug("DBInstance record could not be found!");
				// TODO signal fail hook for the stackID
			}
			instanceId = instance.getInstanceId();

			if(snapshotId == null){
				// TODO signal fail hook for the stackID
			}
			RdsSnapshot snapshot = SnapshotEntity.selectSnapshot(session, null, snapshotId, ac.getId());
			String targetVolId = snapshot.getVolumeId();

			logger.debug("Attach DB Snapshot: Target volume = " + targetVolId + "; Instance = " + instanceId + "; Device = " + device);
			Volume vol = new Volume();
			CallStruct call = new CallStruct();
			call.setAc(at);
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("InstanceId", instanceId);
			props.put("VolumeId", targetVolId);
			props.put("Device", device);
			call.setProperties(props);
			vol.attach(call);
			return "ACK 200";
		}

	}
}
