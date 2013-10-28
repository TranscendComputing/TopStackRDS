package com.transcend.rds.worker;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.msi.tough.cf.AccountType;
import com.msi.tough.cf.json.DatabagParameter;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.aws.ec2.Volume;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.UnsecuredAction;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.rds.json.RDSConfigDatabagItem;
import com.msi.tough.rds.json.RDSCreateTemplate;
import com.msi.tough.rds.json.RDSDatabag;
import com.msi.tough.rds.json.RDSParameterGroupDatabagItem;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionRequestMessage;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RDSMessage.DBParameterGroupStatus;
import com.transcend.rds.message.RDSMessage.DBSecurityGroupMembership;
import com.transcend.rds.message.RDSMessage.Endpoint;

/**
 * @author dkim@momenumsi.com
 */
public class CreateDBInstanceActionWorker extends
		AbstractWorker<CreateDBInstanceActionRequestMessage, CreateDBInstanceActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(CreateDBInstanceActionWorker.class.getName());
	
	   
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public CreateDBInstanceActionResultMessage doWork(
           CreateDBInstanceActionRequestMessage req) throws Exception {
       logger.debug("Performing work for CreateDBInstanceAction.");
       return super.doWork(req, getSession());
   }
   
	@Override
	protected CreateDBInstanceActionResultMessage doWork0(CreateDBInstanceActionRequestMessage req,
			ServiceRequestContext context) throws Exception {

		final DBInstance dbInst = createDBInstance(req , context, false);
		CreateDBInstanceActionResultMessage.Builder resp = 
				CreateDBInstanceActionResultMessage.newBuilder();
		resp.setDbInstance(dbInst);
		return resp.buildPartial();
	}
   
   
   public DBInstance createDBInstance(final CreateDBInstanceActionRequestMessage req,
		   ServiceRequestContext context, boolean restored) {
		DBInstance resp;
		String msg = "";

		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			AccountBean ac = context.getAccountBean();
			// InstanceID is required
			
			if(ac == null){
				// if CreateDBInstance is called from RestoreDBInstance/ReplicateDBInstance calls, read the account now!
				throw RDSQueryFaults.InternalFailure("Account not found");
			}
			
			
			final String instID = ValidationManager.validateIdentifier(
					req.getDbInstanceIdentifier(), 64, true);

			logger.info("CreateInstance: UserID = " + ac.getId()
					+ " InstanceID = " + instID);

			{
				final RdsDbinstance eb = RDSUtil.getInstance(sess, instID,
						ac.getId());
				if (eb != null) {
					throw RDSQueryFaults.DBInstanceAlreadyExists();
				}
				final String avz = req.getAvailabilityZone();
				if (avz != null && !("".equals(avz)) && !Appctx.getConfiguration().containsKey(avz)) {
					throw RDSQueryFaults.ProvisionedIopsNotAvailableInAZ();
				}
			}

			// check to see if DBSecurityGroup exists
			final List<String> dbsecgrpsNames = req.getDbSecurityGroupsList();
			if (dbsecgrpsNames != null) {
				for (final String name : dbsecgrpsNames) {
					final RdsDbsecurityGroup r = SecurityGroupEntity
							.getSecurityGroup(sess, name, ac.getId());
					if (r == null) {
						throw RDSQueryFaults.DBSecurityGroupNotFound();
					}
				}
			}

			/*
			 * The following fully validates the request and insert the
			 * necessary database records. DBInstance record is created with
			 * status of creating. It runs as a single transaction
			 */
			final RdsDbinstance newInstRec = insertDBInstance(
					sess, req, ac);
			newInstRec.setDbinstanceStatus("creating");

			logger.debug("Creating a new databag with following inputs...\n"
					+ "AllocatedStorage: "
					+ newInstRec.getAllocatedStorage()
					+ "\n"
					+ "MasterUsername: "
					+ newInstRec.getMasterUsername()
					+ "\n"
					+ "MasterPassword: "
					+ newInstRec.getMasterUserPassword()
					+ "\n"
					+ "AutoMinorVersionUpgrade: "
					+ newInstRec.getAutoMinorVersionUpgrade()
					+ "\n"
					+ "Engine: "
					+ newInstRec.getEngine()
					+ "\n"
					+ "EngineVersion: "
					+ newInstRec.getEngineVersion()
					+ "\n"
					+ "DBName: "
					+ newInstRec.getDbName()
					+ "\n"
					+ "BackupRetentionPeriod: "
					+ newInstRec.getBackupRetentionPeriod()
					+ "\n"
					+ "PreferredBackupWindow: "
					+ newInstRec.getPreferredBackupWindow()
					+ "\n"
					+ "PreferredMaintenanceWindow: "
					+ newInstRec.getPreferredMaintenanceWindow()
					+ "\n"
					+ "LicenseModel: " + newInstRec.getLicenseModel());

			final String postWaitUrl = (String) ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"TRANSCEND_URL", newInstRec.getAvailabilityZone() }));

			
			final String servletUrl = ConfigurationUtil
					.getConfiguration(Arrays.asList(new String[] {
							"SERVLET_URL", newInstRec.getAvailabilityZone() })).toString();

	
			final RDSConfigDatabagItem configDataBagItem = new RDSConfigDatabagItem(
					"config", newInstRec.getAllocatedStorage().toString(),
					newInstRec.getMasterUsername(),
					newInstRec.getMasterUserPassword(),
					newInstRec.getAutoMinorVersionUpgrade(),
					newInstRec.getEngine(), newInstRec.getEngineVersion(),
					newInstRec.getDbName(), newInstRec
							.getBackupRetentionPeriod().toString(),
					newInstRec.getPreferredBackupWindow(),
					newInstRec.getPreferredMaintenanceWindow(), newInstRec
							.getPort().toString(), postWaitUrl, servletUrl,
					newInstRec.getDbinstanceId(), "rds." + ac.getId() + "."
							+ newInstRec.getDbinstanceId(), ac.getId(), newInstRec.getDbinstanceClass(), "" + restored);

			final String dbParameterGrpName = req.getDbParameterGroupName();
			logger.debug("Looking for DBParameterGroup: " + dbParameterGrpName);
			final RdsDbparameterGroup grpRec = ParameterGroupEntity
					.getParameterGroup(sess, dbParameterGrpName, ac.getId());

			if (grpRec == null) {
				throw RDSQueryFaults.DBParameterGroupNotFound();
			}

			logger.debug("There are (is)" + grpRec.getParameters().size()
					+ " parameters in the DBParameterGroup selected.");
			final RDSParameterGroupDatabagItem parameterGroupDatabagItem = new RDSParameterGroupDatabagItem(
					"parameters", grpRec);
			// since this call is made for CreateDBInstance, set read_only
			// parameter to false
			parameterGroupDatabagItem.getParameters().remove("read_only");
			parameterGroupDatabagItem.getParameters().put(
					"read_only",
					DatabagParameter.factory("boolean", "false", true,
							"dynamic"));
			parameterGroupDatabagItem.getParameters().remove("port");
			parameterGroupDatabagItem.getParameters().put(
					"port",
					DatabagParameter.factory("integer",
							"" + newInstRec.getPort(), false, "static"));

			final RDSDatabag bag = new RDSDatabag(configDataBagItem,
					parameterGroupDatabagItem);
			logger.debug("Databag: " + JsonUtil.toJsonPrettyPrintString(bag));
			ChefUtil.createDatabagItem("rds-" + ac.getId() + "-"
					+ req.getDbInstanceIdentifier(), "config",
					bag.toJson());

			// Put parameter values into a hash table
			final Map<String, Object> parameterValues = RDSUtilities
					.PutParametersToHash(newInstRec, instID, ac);
			String instClass = (String)parameterValues.get("DBInstanceClass");
			if(instClass == null){
				throw RDSQueryFaults.MissingParameter("DBInstanceClass parameter is required for CreateDBInstance action.");
			}else{
				parameterValues.remove("DBInstanceClass");
				final String ec2instClass = (String)ConfigurationUtil.getConfiguration(Arrays
						.asList(new String[] { instClass, newInstRec.getAvailabilityZone() }));
				if (ec2instClass == null) {
					throw RDSQueryFaults.InvalidParameterValue(instClass
							+ " is not one of the valid DBInstance classes.");
				}
				parameterValues.put("DBInstanceClass", ec2instClass);
				logger.debug("Replaced the value of DBInstanceClass to be " + ec2instClass + " from " + instClass);
			}
			
			String logInfo = "Map of RDS Parameters=> \n";
			for (final String s : parameterValues.keySet()) {
				logInfo += "Key: " + s + ", Value: " + parameterValues.get(s)
						+ "\n";
			}
			logger.debug(logInfo);

			// Create RDS cloud formation JSON template
			final String jsonTemplate = RDSCreateTemplate.getTemplate(sess,
					newInstRec);

			// Print template to log
			try {
				final JsonNode node = JsonUtil.load(jsonTemplate);
				logger.debug(JsonUtil.toJsonPrettyPrintString(node));
			} catch (final Exception e) {
				e.printStackTrace();
				throw RDSQueryFaults.InternalFailure();
			}

			// Pass template and parameter values to cloud formation
			logger.debug("Calling CFUtil.runAsyncAWSScript()");
			final TemplateContext tc = new TemplateContext(null);
			tc.putAll(parameterValues);
			final String stackId = "rds." + ac.getId() + "."
					+ req.getDbInstanceIdentifier();
			CFUtil.deleteResourceRecords(sess, ac.getId(), stackId, null, null);
			CFUtil.runAsyncAWSScript(stackId, ac.getId(), jsonTemplate, tc);
			logger.debug("CFUtil.runAsyncAWSScript() is called.");

			// create event
			final String message = "Database instance created";
			EventUtil.addEvent(sess, ac.getId(), message, "db-instance",
					new String[] { instID });

			// Build response message
			resp = toDBInstance(newInstRec, ac);
			logger.debug("Response component: " + resp.toString());

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "CreateDBInstance: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp;
	}
	
	public static class MountDBVolume extends UnsecuredAction {

		@SuppressWarnings("static-access")
		@Override
		public String process0(Session session, HttpServletRequest req,
				HttpServletResponse resp, Map<String, String[]> map)
				throws Exception {
			final long acId = QueryUtil.getLong(map, "AcId");
			final String stackId = QueryUtil.getString(map, "StackId");
			final String device = QueryUtil.getString(map, "Device");
			final String snapshotId = QueryUtil.getString(map, "Snapshot");
			logger.debug("MOUNT DB VOLUME: " + "AccountId = " + acId + "; StackId = " + stackId);
			AccountBean ac = AccountUtil.readAccount(session, acId);
			AccountType at = AccountUtil.toAccount(ac);
			
			String targetVolId = null;
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
				targetVolId = instance.getVolumeId();
			}else{
				RdsSnapshot snapshot = SnapshotEntity.selectSnapshot(session, dbInstId, snapshotId, acId);
				if(snapshot == null){
					// TODO signal fail hook for the DBSnapshot
				}
				targetVolId = snapshot.getVolumeId();
			}
			
			logger.debug("MOUNT DB VOLUME: Target volume = " + targetVolId + "; Instance = " + instanceId + "; Device = " + device);
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
	
	
	
	/**************************************************************************
	 * Inserts records into the following tables rds_dbinstance
	 * rds_security_group_membership rds_dbparameter_group_status This method
	 * also checks quota's
	 *
	 * @param createRec
	 * @param userID
	 * @param address
	 * @param readReplica
	 * @throws BaseException
	 */
	public static RdsDbinstance insertDBInstance(final Session sess,
			final CreateDBInstanceActionRequestMessage createRec, final AccountBean ac) {
		final String instID = createRec.getDbInstanceIdentifier();
		final RdsDbinstance dbrec = RDSUtil.getInstance(sess, instID,
				ac.getId());
		if (dbrec != null) {
			throw RDSQueryFaults.DBInstanceAlreadyExists();
		}

		final String paramGrpName = createRec.getDbParameterGroupName();

		// check that ParameterGroup exists
		RdsDbparameterGroup pgRec = ParameterGroupEntity
				.getParameterGroup(sess, paramGrpName, ac.getId());
		if (pgRec == null) {
			List<RdsDbparameterGroup> paramGrps = ParameterGroupEntity.selectDBParameterGroups(sess, paramGrpName, 1, null, null);
			if(paramGrps == null || paramGrps.size() != 1){
				throw RDSQueryFaults.DBParameterGroupNotFound();
			}
			else{
				// default parameter group is being used; copy the group and parameters for this user
				ParameterGroupEntity.createDefaultDBParameterGroup(sess, ac, paramGrpName);
				pgRec = ParameterGroupEntity
						.getParameterGroup(sess, paramGrpName, ac.getId());
			}
		}

		/*
		 * DBSecurityGroup ************************************************* The
		 * create request contains a list of DBSecurityGroupNames we need to
		 * process each one
		 */
		final RdsDbinstance newInst = new RdsDbinstance(ac, pgRec);
		if (createRec.getDbSecurityGroupsCount() == 0) {
			// this line is never reached because "default" is set by default as DBSecurityGroup
		} else {
			// Process the array of SecurityGroup Names
			final String[] secGrpName = createRec.getDbSecurityGroupsList()
					.toArray(new String[0]);
			final int len = secGrpName.length;
			final List<RdsDbsecurityGroup> groups = newInst.getSecurityGroups();

			for (int i = 0; i < len; i++) {
				// check that the security group exists
				final RdsDbsecurityGroup sgmemRec = SecurityGroupEntity
						.getSecurityGroup(sess, secGrpName[i], ac.getId());
				if (sgmemRec == null && !secGrpName[i].equals("default")) {
					throw RDSQueryFaults.DBSecurityGroupNotFound();
				}
				groups.add(sgmemRec);
			}
		}
		newInst.setInstanceCreateTime(new Date());
		newInst.setLatestRestorableTime(new Date());
		newInst.setAllocatedStorage(createRec.getAllocatedStorage());
		newInst.setAutoMinorVersionUpgrade(createRec
				.getAutoMinorVersionUpgrade());
		newInst.setAvailabilityZone(createRec.getAvailabilityZone());
		newInst.setBackupRetentionPeriod(createRec.getBackupRetentionPeriod());
		final String dbInstClass = createRec.getDbInstanceClass();
		final String instClass = (String)ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { dbInstClass, createRec.getAvailabilityZone() }));
		if (instClass == null) {
			throw RDSQueryFaults.InvalidParameterValue(dbInstClass
					+ " is not one of the valid DBInstance classes.");
		}
		newInst.setDbinstanceClass(dbInstClass);
		newInst.setDbinstanceId(createRec.getDbInstanceIdentifier());
		newInst.setDbName(createRec.getDbName());
		newInst.setDbParameterGroup(paramGrpName);
		newInst.setEngine(createRec.getEngine());
		newInst.setEngineVersion(createRec.getEngineVersion());
		newInst.setMasterUsername(createRec.getMasterUsername());
		newInst.setMasterUserPassword(createRec.getMasterUserPassword());
		newInst.setMultiAz(createRec.getMultiAZ());
		newInst.setPort(createRec.getPort());
		newInst.setPreferredBackupWindow(createRec.getPreferredBackupWindow());
		newInst.setPreferredMaintenanceWindow(createRec
				.getPreferredMaintenanceWindow());
		newInst.setLicenseModel(createRec.getLicenseModel());
		newInst.setDbinstanceStatus("creating");
		final List<String> DBSecurityGroupNames = createRec.getDbSecurityGroupsList();
		final List<RdsDbsecurityGroup> dbsecgrps = new LinkedList<RdsDbsecurityGroup>();
		for (final String dbSecGrpname : DBSecurityGroupNames) {
			final RdsDbsecurityGroup temp = SecurityGroupEntity
					.getSecurityGroup(sess, dbSecGrpname, ac.getId());
			if (temp == null) {
				throw RDSQueryFaults.DBSecurityGroupNotFound();
			}
			dbsecgrps.add(temp);
		}
		newInst.setSecurityGroups(dbsecgrps);

		// save / create the instance record
		sess.save(newInst);

		logger.info("insertDBInstance: Successfully Inserted Instance "
				+ "into the Database");
		return newInst;
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
