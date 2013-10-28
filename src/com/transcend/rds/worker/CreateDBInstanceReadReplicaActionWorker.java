/**
 * 
 */
package com.transcend.rds.worker;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.jackson.JsonNode;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.ExecutorHelper;
import com.msi.tough.core.ExecutorHelper.Executable;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.core.JsonUtil;
import com.msi.tough.engine.aws.ec2.SecurityGroupIngress;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.engine.core.TemplateContext;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rdsquery.RDS_Constants;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.ChefUtil;
import com.msi.tough.utils.ConfigurationUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionRequestMessage;
import com.transcend.rds.message.CreateDBInstanceReadReplicaActionMessage.CreateDBInstanceReadReplicaActionRequestMessage;
import com.transcend.rds.message.CreateDBInstanceReadReplicaActionMessage.CreateDBInstanceReadReplicaActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;

/**
 * @author tdhite
 */
public class CreateDBInstanceReadReplicaActionWorker extends
		AbstractWorker<CreateDBInstanceReadReplicaActionRequestMessage, CreateDBInstanceReadReplicaActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(CreateDBInstanceReadReplicaActionWorker.class.getName());
    /**
     * 
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public CreateDBInstanceReadReplicaActionResultMessage doWork(
           CreateDBInstanceReadReplicaActionRequestMessage req) throws Exception {
       logger.debug("Performing work for CreateDBInstanceReadReplicaAction.");
       return super.doWork(req, getSession());
   }
   
	private Connection getConnection(final String user, final String passwd,
			final String engine, final String hostname, final int port)
			throws SQLException {
		logger.debug("Setting up a database connection to " + hostname + ":"
				+ port + " targetting " + engine + " engine using " + user
				+ " user and " + passwd + " password.");
		Connection conn = null;
		final Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", passwd);
		final String connectionUrl = "jdbc:" + engine + "://" + hostname + ":"
				+ port + "/";
		logger.debug("Connection Url: " + connectionUrl);
		conn = DriverManager.getConnection(connectionUrl, connectionProps);
		return conn;
	}

	/**
	 * createDBInstanceReadReplica *******************************************
	 * Creates a read replica for a named master database instance All of the
	 * characteristics of the read replica default to the characteristics of the
	 * the master instance with the exception of InstanceClass,
	 * AvailabilityZone,Port and AutoMinorVersionUpgrade that can be provided by
	 * the user. Request: SourceDBInstanceIdentifier (R) DBInstanceIdentifier
	 * for the read replica (R) DBInstanceClass AvailabilityZone Port
	 * AutoMinorVersionUpgrade Response: Full details of new DBInstance created
	 * Exceptions: DBIInstanceAlreadyExists DBInstanceNotFound
	 * DBParameterGroupNotFound DBSecurityGroupNotFound InstanceQuotaExceeded
	 * InsufficientDBInstanceCapacity InvalidDBInstanceState
	 * StorageQuotaExceeded Processing 1. Confirm that source DBInstance exists
	 * 2. Determined that requested DBInstance replica doesn't already exist for
	 * that user 3. Confirm quotas have not been exceeded (instance,
	 * availabilityZone, storage) 4. Validate and insert the DBInstance and
	 * associated records 5. Call the instance manager to provision the read
	 * replica 6. Return response giving details of newly created replica
	 * instance including end point.
	 */
	@Override
	protected CreateDBInstanceReadReplicaActionResultMessage doWork0(CreateDBInstanceReadReplicaActionRequestMessage req,
ServiceRequestContext context) throws Exception {
		final Session sess = HibernateUtil.newSession();
		DBInstance dbInst = null;

		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userId = ac.getId();
			final boolean autoUpgrade = req.getAutoMinorVersionUpgrade();
			String avZone = req.getAvailabilityZone();
			String DBInstanceClass = req.getDbInstanceClass();
			final String DBInstanceId = req.getDbInstanceIdentifier();
			int port = req.getPort();
			final String sourceDBInstanceId = req
					.getSourceDBInstanceIdentifier();

			if (sourceDBInstanceId == null || "".equals(sourceDBInstanceId)) {
				throw QueryFaults
						.MissingParameter("SourceDBInstanceIdentifier must be supplied for CreateDBInstanceReadReplica request.");
			}
			if (DBInstanceId == null || "".equals(DBInstanceId)) {
				throw QueryFaults
						.MissingParameter("DBInstanceIdentifier must be supplied for CreateDBInstanceReadReplica request.");
			}

			final RdsDbinstance source = InstanceEntity.selectDBInstance(sess,
					sourceDBInstanceId, userId);
			if (source == null || "".equals(source)) {
				throw RDSQueryFaults.DBInstanceNotFound(sourceDBInstanceId
						+ " does not exist.");
			}
			if (!source.getDbinstanceStatus().equals(
					RDSUtilities.STATUS_AVAILABLE)) {
				throw RDSQueryFaults.InvalidDBInstanceState();
			}
			if (port == -1) {
				logger.debug("request did not include port; port value is set with "
						+ source.getPort() + " from the source DBInstance.");
				port = source.getPort();
			}
			if (DBInstanceClass == null || "".equals(DBInstanceClass)) {
				logger.debug("request did not include DBInstanceClass; DBInstanceClass value is set with "
						+ source.getDbinstanceClass()
						+ " from the source DBInstance.");
				DBInstanceClass = source.getDbinstanceClass();
			}
			if (avZone == null || "".equals(avZone)) {
				logger.debug("AvailabilityZone is not included in the request; it is set to the default zone of the account: "
						+ ac.getDefZone());
				avZone = ac.getDefZone();
			}

			logger.debug("Preparing the request for CreateDBInstance");
			final CreateDBInstanceActionRequestMessage.Builder createDBInstanceReq = 
					CreateDBInstanceActionRequestMessage.newBuilder();
			createDBInstanceReq.setAllocatedStorage(source.getAllocatedStorage());
			createDBInstanceReq.setAutoMinorVersionUpgrade(autoUpgrade);
			createDBInstanceReq.setAvailabilityZone(avZone);
			createDBInstanceReq.setBackupRetentionPeriod(source
					.getBackupRetentionPeriod());
			createDBInstanceReq.setDbInstanceClass(DBInstanceClass);
			createDBInstanceReq.setDbInstanceIdentifier(DBInstanceId);
			createDBInstanceReq.setDbParameterGroupName(source
					.getDbParameterGroup());
			final List<RdsDbsecurityGroup> dbSecGrps = source
					.getSecurityGroups();
			final LinkedList<String> dbSecGrpNames = new LinkedList<String>();
			for (final RdsDbsecurityGroup secGrp : dbSecGrps) {
				dbSecGrpNames.add(secGrp.getDbsecurityGroupName());
			}
			createDBInstanceReq.addAllDbSecurityGroups(dbSecGrpNames);
			createDBInstanceReq.setEngine(source.getEngine());
			createDBInstanceReq.setEngineVersion(source.getEngineVersion());
			createDBInstanceReq.setLicenseModel(source.getLicenseModel());
			createDBInstanceReq.setMasterUsername(source.getMasterUsername());
			createDBInstanceReq.setMasterUserPassword(source
					.getMasterUserPassword());
			createDBInstanceReq.setMultiAZ(false);
			createDBInstanceReq.setPort(port);
			createDBInstanceReq.setPreferredBackupWindow(source
					.getPreferredBackupWindow());
			createDBInstanceReq.setPreferredMaintenanceWindow(source
					.getPreferredMaintenanceWindow());
			logger.debug("Request: " + createDBInstanceReq.toString());

			logger.debug("Calling CreateDBInstance...");
			final CreateDBInstanceActionWorker createAction = new CreateDBInstanceActionWorker();
			dbInst = createAction.createDBInstance(createDBInstanceReq.buildPartial(), context, true);

			logger.debug("Adding another authorization to the underlying ec2 security group");
			final String internal = "rds-" + ac.getId() + "-"
					+ source.getDbinstanceId() + "-" + source.getPort();
			final List<RdsDbsecurityGroup> secGrps = SecurityGroupEntity
					.selectAllSecurityGroups(sess, internal, ac.getId(), null,
							0);
			if (secGrps.size() != 1) {
				throw RDSQueryFaults.InternalFailure();
			}

			final String rds_host = Appctx.getBean("internalServiceIp");

			final String RdsServerCidrip = rds_host + "/32";
			final RdsDbsecurityGroup masterSecGrp = secGrps.get(0);
			final List<RdsIPRangeBean> ips = masterSecGrp.getIPRange(sess);
			boolean authorized = false;
			for (final RdsIPRangeBean ip : ips) {
				if (ip.getCidrip().equals(RdsServerCidrip)) {
					authorized = true;
					logger.debug("Authorization already exists for "
							+ RdsServerCidrip);
				}
			}
			final int port0 = source.getPort();
			if (!authorized) {
				logger.debug("Authorizing ingress for " + RdsServerCidrip
						+ " to access the source DBInstance.");
				final CallStruct callEc2SecGrp = new CallStruct();
				callEc2SecGrp.setAc(AccountUtil.toAccount(ac));
				callEc2SecGrp.setCtx(new TemplateContext(null));
				callEc2SecGrp.setName(internal);
				callEc2SecGrp.setStackId("rds." + ac.getId() + "." + sourceDBInstanceId);
				final Map<String, Object> props = new HashMap<String, Object>();
				props.put(Constants.AVAILABILITYZONE, ac.getDefZone());
				props.put(Constants.GROUPNAME, internal);
				props.put(Constants.CIDRIP, RdsServerCidrip);
				props.put(Constants.SOURCESECURITYGROUPNAME, null);
				// SourceSecurityGroupOwnerId is not required
				props.put(Constants.SOURCESECURITYGROUPOWNERID, null);

				// hardcoded values below
				props.put(Constants.FROMPORT, port0);
				props.put(Constants.TOPORT, port0);
				props.put(Constants.IPPROTOCOL, Constants.DEFAULT_RDS_PROTOCOL);

				callEc2SecGrp.setProperties(props);
				callEc2SecGrp.setType(SecurityGroupIngress.TYPE);
				final SecurityGroupIngress provider0 = new SecurityGroupIngress();
				try {
					provider0.create(callEc2SecGrp);
				} catch (final AmazonServiceException e) {
					logger.debug(e.getMessage());
				} catch (final AmazonClientException e) {
					logger.debug(e.getMessage());
				}

				final RdsIPRangeBean newAuth = new RdsIPRangeBean(
						masterSecGrp.getId(), RdsServerCidrip);
				ips.add(newAuth);
				sess.save(newAuth);
			}

			// modify the source DBInstance's status and commit the transaction
			source.setDbinstanceStatus(RDSUtilities.STATUS_MODIFYING);
			source.getReplicas().add(DBInstanceId);
			sess.save(source);
			sess.getTransaction().commit();

			final Connection master = getConnection("root",
					source.getMasterUserPassword(), source.getEngine()
							.toLowerCase(), source.getAddress(),
					source.getPort());
			logger.debug("Checking to see if the source DBInstance has RDS replication user already...");
			final String checkPermission = "SELECT User from mysql.user";
			final Statement check = master.createStatement();
			final ResultSet existingGrant = check.executeQuery(checkPermission);
			boolean exist = false;
			while (existingGrant.next()) {
				final String user = existingGrant.getString("User");
				logger.debug("User: " + user);
				if (user.equals(RDS_Constants.RDS_REPLICATION_USER)) {
					exist = true;
				}
			}

			// create a new user and grant replication privilege
			if (!exist) {
				logger.debug("Replicaion user for RDS does not exist; creating a replication user...");
				final String grantPermission = "GRANT REPLICATION SLAVE ON *.* TO \'"
						+ RDS_Constants.RDS_REPLICATION_USER
						+ "\'@\'%\' IDENTIFIED BY \'"
						+ RDS_Constants.RDS_REPLICATION_PASSWORD + "\'";
				final PreparedStatement grant = master
						.prepareStatement(grantPermission);
				grant.execute();
			}

			logger.debug("Flushing tables with read lock on the source DBInstance...");
			final String flushTables = "FLUSH TABLES WITH READ LOCK";
			final Statement flushTablesAndLock = master.createStatement();
			flushTablesAndLock.execute(flushTables);

			logger.debug("Getting the master status");
			final String getMasterStatus = "SHOW MASTER STATUS";
			final Statement queryMasterStatus = master.createStatement();
			final ResultSet masterStatus = queryMasterStatus
					.executeQuery(getMasterStatus);
			String masterFile = null;
			int position = -1;
			while (masterStatus.next()) {
				masterFile = masterStatus.getString("File");
				position = masterStatus.getInt("Position");
				// ignore Binlog_Do_DB and Binlog_Ignore_DB for now
			}
			logger.debug("Master file is " + masterFile
					+ " and the position is set at " + position);
			if (masterFile == null || position == -1) {
				RDSQueryFaults
						.InternalFailure("Master status could not be retrieved from the source DBInstance.");
			}

			logger.debug("Unlocking the tables...");
			final String unlockTables = "UNLOCK TABLES";
			final Statement unlock = master.createStatement();
			unlock.execute(unlockTables);
			logger.debug("Successfully unlocked the tables.");

			logger.debug("Close the connection to the source DBInstance.");
			master.close();

			logger.debug("Updating the databag to run the replication_server.rb recipe");
			final String task = "mysqldump";
			final String target = "?";
			final String databagName = "rds-" + ac.getId() + "-"
					+ source.getDbinstanceId();
			final String replication_item = "{\"Task\":\"" + task + "\", "
					+ "\"TargetHostname\":\"" + target + "\"}";
			ChefUtil.createDatabagItem(databagName, "Replication");
			ChefUtil.putDatabagItem(databagName, "Replication", replication_item);
			

			logger.debug("Starting a new thread to wait for read replica to spin up while returning the response message.");
			final String DBInstanceId0 = DBInstanceId;
			final String sourceDBInstanceId0 = sourceDBInstanceId;
			final String avZone0 = avZone;
			final String masterFile0 = masterFile;
			final int position0 = position;
			final int port1 = port;
			final Executable r = new ExecutorHelper.Executable() {
				@Override
				public void run() {
					HibernateUtil
							.withNewSession(new HibernateUtil.Operation<Object>() {
								@Override
								public Object ex(final Session s,
										final Object... as) throws Exception {
									replicationHelper(s, ac, DBInstanceId0,
											sourceDBInstanceId0, avZone0,
											port0, port1, masterFile0,
											position0);
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
			final String msg = "CreateDBInstanceReadReplica: Class: "
					+ e.getClass() + "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		CreateDBInstanceReadReplicaActionResultMessage.Builder resp = 
				CreateDBInstanceReadReplicaActionResultMessage.newBuilder();
		resp.setDbInstance(dbInst);
		return resp.buildPartial();
	}

	private void replicationHelper(Session sess, final AccountBean ac,
			final String dbInstanceId, final String sourceDBInstanceId,
			final String avZone, final int masterPort, final int replicaPort,
			final String masterFile, final int position) throws Exception {
		logger.debug("Locking before accessing the critical database table...");
		String address = null;
		boolean wait = true;
		int count = 0;
		while (wait) {
			logger.debug("Waiting for Resource(s) to be created... " + count
					+ "th trial!");
			Thread.sleep(30000);
			logger.debug("Renewing the session...");
			sess = HibernateUtil.newSession();
			sess.beginTransaction();
			final RdsDbinstance inst = InstanceEntity.selectDBInstance(sess,
					dbInstanceId, ac.getId());
			String stat = null;
			if (inst != null) {
				stat = inst.getDbinstanceStatus();
			}
			logger.debug("DBInstance existence: " + inst
					+ "; DBInstanceStatus: " + stat);
			if (inst != null && stat.equals("restoring")) {
				inst.setDbinstanceStatus(RDSUtilities.STATUS_MODIFYING);
				inst.setRead_only(true);
				inst.setSourceDbinstanceId(sourceDBInstanceId);
				address = inst.getAddress();
				sess.save(inst);
				sess.getTransaction().commit();
				sess.close();
				wait = false;
				break;
			}
			sess.close();
			++count;
		}
		logger.debug("Unlocking after accessing the critical database table...");

		sess = HibernateUtil.newSession();
		sess.beginTransaction();
		if (address == null) {
			throw RDSQueryFaults.InternalFailure();
		}

		// modify the ec2 security group of master DBInstance to grant access to
		// CIDRIP of slave DBInstance's IP/32
		logger.debug("Adding another authorization to the underlying ec2 security group");
		final String internal = "rds-" + ac.getId() + "-" + sourceDBInstanceId
				+ "-" + masterPort;
		List<RdsDbsecurityGroup> secGrps = SecurityGroupEntity
				.selectAllSecurityGroups(sess, internal, ac.getId(), null, 0);
		if (secGrps.size() != 1) {
			throw RDSQueryFaults.InternalFailure();
		}
		final RdsDbsecurityGroup masterSecGrp = secGrps.get(0);
		List<RdsIPRangeBean> ips = masterSecGrp.getIPRange(sess);
		final String slaveCidrip = address + "/32";
		final boolean authorized = false;
		if (!authorized) {
			logger.debug("Authorizing ingress for " + slaveCidrip
					+ " to access the source DBInstance.");
			final CallStruct callEc2SecGrp = new CallStruct();
			callEc2SecGrp.setAc(AccountUtil.toAccount(ac));
			callEc2SecGrp.setCtx(new TemplateContext(null));
			callEc2SecGrp.setName(internal);
			callEc2SecGrp.setStackId("rds." + ac.getId() + "." + sourceDBInstanceId);
			final Map<String, Object> props = new HashMap<String, Object>();
			props.put(Constants.AVAILABILITYZONE, ac.getDefZone());
			props.put(Constants.GROUPNAME, internal);
			props.put(Constants.CIDRIP, slaveCidrip);
			props.put(Constants.SOURCESECURITYGROUPNAME, null);
			// SourceSecurityGroupOwnerId is not required
			props.put(Constants.SOURCESECURITYGROUPOWNERID, null);
			// hardcoded values below
			props.put(Constants.FROMPORT, masterPort);
			props.put(Constants.TOPORT, masterPort);
			props.put(Constants.IPPROTOCOL, Constants.DEFAULT_RDS_PROTOCOL);

			callEc2SecGrp.setProperties(props);
			callEc2SecGrp.setType(SecurityGroupIngress.TYPE);
			final SecurityGroupIngress provider0 = new SecurityGroupIngress();
			try {
				provider0.create(callEc2SecGrp);
			} catch (final AmazonServiceException e) {
				logger.debug(e.getMessage());
			} catch (final AmazonClientException e) {
				logger.debug(e.getMessage());
			}

			final RdsIPRangeBean newAuth = new RdsIPRangeBean(
					masterSecGrp.getId(), slaveCidrip);
			ips.add(newAuth);
			sess.save(newAuth);
		}

		final String replicaInternal = "rds-" + ac.getId() + "-" + dbInstanceId
				+ "-" + replicaPort;
		secGrps = SecurityGroupEntity.selectAllSecurityGroups(sess,
				replicaInternal, ac.getId(), null, 0);
		if (secGrps.size() != 1) {
			throw RDSQueryFaults.InternalFailure();
		}
		final RdsDbsecurityGroup slaveSecGrp = secGrps.get(0);
		final String rds_host = Appctx.getBean("internalServiceIp");
		final String RdsServerCidrip = rds_host + "/32";
		logger.debug("Authorizing ingress for " + RdsServerCidrip
				+ " to access the read replica DBInstance.");
		final CallStruct callEc2SecGrp = new CallStruct();
		callEc2SecGrp.setAc(AccountUtil.toAccount(ac));
		callEc2SecGrp.setCtx(new TemplateContext(null));
		callEc2SecGrp.setName(replicaInternal);
		callEc2SecGrp.setStackId("rds." + ac.getId() + "." + dbInstanceId);
		final Map<String, Object> props = new HashMap<String, Object>();
		props.put(Constants.AVAILABILITYZONE, ac.getDefZone());
		props.put(Constants.GROUPNAME, replicaInternal);
		props.put(Constants.CIDRIP, RdsServerCidrip);
		props.put(Constants.SOURCESECURITYGROUPNAME, null);
		// SourceSecurityGroupOwnerId is not required
		props.put(Constants.SOURCESECURITYGROUPOWNERID, null);

		// hardcoded values below
		props.put(Constants.FROMPORT, replicaPort);
		props.put(Constants.TOPORT, replicaPort);
		props.put(Constants.IPPROTOCOL, Constants.DEFAULT_RDS_PROTOCOL);

		callEc2SecGrp.setProperties(props);
		callEc2SecGrp.setType(SecurityGroupIngress.TYPE);
		final SecurityGroupIngress provider0 = new SecurityGroupIngress();
		try {
			provider0.create(callEc2SecGrp);
		} catch (final AmazonServiceException e) {
			logger.debug(e.getMessage());
		} catch (final AmazonClientException e) {
			logger.debug(e.getMessage());
		}

		final RdsIPRangeBean newAuth = new RdsIPRangeBean(slaveSecGrp.getId(),
				RdsServerCidrip);
		ips = slaveSecGrp.getIPRange(sess);
		ips.add(newAuth);
		sess.save(newAuth);

		logger.debug("Copy the mysqldump file from the source DBInstance to the DBInstanceReadReplica");
		String key_dir = (String) ConfigurationUtil.getConfiguration(Arrays
				.asList(new String[] { "KEYS_DIR" }));
		final String key = ac.getDefKeyName() + ".pem";
		if (key_dir.charAt(key_dir.length() - 1) != '/') {
			key_dir += '/';
		}
		final String key_path = key_dir + key;
		logger.debug("Account's ec2 key is at: " + key_path);

		final RdsDbinstance source = InstanceEntity.selectDBInstance(sess,
				sourceDBInstanceId, ac.getId());
		final String masterHostname = source.getAddress();
		final String slaveHostname = address;

		logger.debug("check if mysqldump is completed or not before trying to scp the dump;"
				+ " databagitem Replication should be modified to have Task = mysqldump_complete");
		String databagName = "rds-" + ac.getId() + "-"
				+ source.getDbinstanceId();
		String databagItem = ChefUtil
				.getDatabagItem(databagName, "Replication");
		JsonNode replicationItem = JsonUtil.load(databagItem);
		JsonNode task = replicationItem.get("Task");
		String taskValue = task.getTextValue();

		boolean dumpReady = false;
		while (!dumpReady) {
			databagItem = ChefUtil.getDatabagItem(databagName, "Replication");
			replicationItem = JsonUtil.load(databagItem);
			task = replicationItem.get("Task");
			taskValue = task.getTextValue();
			if (taskValue.equals("mysqldump_complete")) {
				dumpReady = true;
			}
		}
		logger.debug("Replication databag item: " + replicationItem.toString()
				+ " and Task = " + taskValue);

		// scp the key file into source DBInstance
		final String scpCommand = "scp -o StrictHostKeyChecking=no -i "
				+ key_path + " " + key_path + " root@" + masterHostname
				+ ":/root";
		logger.debug("SCP command is: " + scpCommand);
		Runtime.getRuntime().exec(scpCommand);
		logger.debug("Scp'ed the client key to the source DBInstance.");

		// set the flag for scp and wait till this process is completed by chef
		String replication_item = "{\"Task\":\"" + "scp" + "\", "
				+ "\"TargetHostname\":\"" + slaveHostname + "\", "
				+ "\"Key\":\"" + key + "\"" + "}";
		ChefUtil.createDatabagItem(databagName, "Replication", replication_item);

		boolean scpDone = false;
		while (!scpDone) {
			databagItem = ChefUtil.getDatabagItem(databagName, "Replication");
			replicationItem = JsonUtil.load(databagItem);
			task = replicationItem.get("Task");
			taskValue = task.getTextValue();
			if (taskValue.equals("scp_complete")) {
				scpDone = true;
			}
		}
		logger.debug("Replication databag item: " + replicationItem.toString()
				+ " and Task = " + taskValue);
		ChefUtil.deleteDatabagItem(databagName, "Replication");

		// apply the mysqldump to the read replica DBInstance, then change the
		// master; restart mysql server on read replica
		databagName = "rds-" + ac.getId() + "-" + dbInstanceId;
		replication_item = "{\"Task\":\"slave\"}";
		ChefUtil.createDatabagItem(databagName, "Replication", replication_item);

		boolean restored = false;
		while (!restored) {
			databagItem = ChefUtil.getDatabagItem(databagName, "Replication");
			replicationItem = JsonUtil.load(databagItem);
			task = replicationItem.get("Task");
			taskValue = task.getTextValue();
			if (taskValue.equals("slave_complete")) {
				restored = true;
			}
		}
		logger.debug("Replication databag item: " + replicationItem.toString()
				+ " and Task = " + taskValue);

		final Connection slave = getConnection("root",
				source.getMasterUserPassword(), source.getEngine()
						.toLowerCase(), slaveHostname, replicaPort);
		final Statement changeMaster = slave.createStatement();
		final String change = "CHANGE MASTER TO MASTER_HOST=\'"
				+ masterHostname + "\', MASTER_USER=\'"
				+ RDS_Constants.RDS_REPLICATION_USER + "\', MASTER_PASSWORD=\'"
				+ RDS_Constants.RDS_REPLICATION_PASSWORD + "\', MASTER_LOG_FILE=\'"
				+ masterFile + "\', MASTER_LOG_POS=" + position;
		changeMaster.execute(change);
		logger.debug("Modified the master of the read replica to the source DBInstance.");

		final Statement startSlave = slave.createStatement();
		startSlave.execute("START SLAVE");
		final Statement flushTableRL = slave.createStatement();
		flushTableRL.execute("FLUSH TABLES WITH READ LOCK");
		final Statement setReadOnly = slave.createStatement();
		setReadOnly.execute("SET GLOBAL read_only = ON");
		logger.debug("Slave service started and configured for read_only.");

		logger.debug("Closing the connection to the replica DBInstance.");
		slave.close();

		ChefUtil.deleteDatabagItem(databagName, "Replication");

		logger.debug("Modifying the DBInstanceStatus for both DBInstances");
		final RdsDbinstance inst = InstanceEntity.selectDBInstance(sess,
				sourceDBInstanceId, ac.getId());
		inst.setDbinstanceStatus(RDSUtilities.STATUS_AVAILABLE);
		final RdsDbinstance inst2 = InstanceEntity.selectDBInstance(sess,
				dbInstanceId, ac.getId());
		inst2.setDbinstanceStatus(RDSUtilities.STATUS_AVAILABLE);
		sess.save(inst);
		sess.save(inst2);

		sess.getTransaction().commit();
		sess.close();
		logger.debug("CreateDBInstanceReadReplica completed successfully.");

	}
}
