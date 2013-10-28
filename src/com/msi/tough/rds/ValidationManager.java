package com.msi.tough.rds;

/*
 * Provides full validation for the various fields and records before
 * they are inserted into the private RDS database
 * The RDS has fairly comprehensive validation rules that are implemented
 * here
 * 
 * The earlier methods validate individual fields the last methods validate
 * entire records using the field validation operations
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.hibernate.Session;
import org.slf4j.Logger;

import com.amazonaws.services.rds.model.ApplyMethod;
import com.amazonaws.services.rds.model.CreateDBInstanceReadReplicaRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.ModifyDBInstanceRequest;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.rds.RDSUtilities;

public class ValidationManager {
	private static Logger logger = Appctx.getLogger(ValidationManager.class
			.getName());

	/**************************************************************************
	 * Allocated Store is BigInteger must be between 5 and 1024 giga bytes
	 * Defaults to 10
	 * 
	 * @param allocStore
	 * @param required
	 * @return int allocatedStorage
	 * @throws BaseException
	 */
	public static int validateAllocatedStorage(final Integer allocStore,
			final boolean required) {
		String msg = "";
		int retval = 0;

		if (allocStore == null) {
			if (required) {
				msg = "Validate: AllocateStorage must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = 10;
			}
		} else if (allocStore.intValue() < 5 || allocStore.intValue() > 1024) {
			msg = "Allocated Store must be between 5 and 1024 Gigabytes";
			logger.error("ValidateInstance: " + msg);
			throw new BaseException(msg);
		} else {
			retval = allocStore.intValue();
		}
		return retval;
	}

	/*****************************************************************
	 * General purpose method for validating any String attribute This will
	 * check length and generate exception if null and required
	 * 
	 * @param id
	 * @param length
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static String validateAttribute(String id, final int length,
			final Boolean required) {
		final int len = id.length();
		String msg = "";

		if (id == null || id == "") {
			// null value - if required throw exception
			// no default
			if (required) {
				msg = "ValidationFailure: " + id + " is null";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				id = "";
			}
		} else if (len < 1 || len > length) {
			msg = "ValidationFailure for :" + id + " must be between 1 "
					+ "and " + length + " and not " + id.length();
			logger.error(msg);
			throw new BaseException(msg);
		}
		return id;
	}

	/**************************************************************************
	 * Availability zone is normally optional - defaults to east TODO: look up
	 * from availabilityZone table
	 * 
	 * @param aZone
	 * @param required
	 * @return String - availabilityZone
	 * @throws BaseException
	 */
	public static String validateAvailabilityZone(final String aZone,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (aZone == null || aZone == null) {
			if (required) {
				msg = "Validate: AvailabilityZone must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = RDSUtilities.AVAIL_ZN_EAST;
			}
		} else {
			if (!(aZone.equalsIgnoreCase(RDSUtilities.AVAIL_ZN_EAST)
					|| aZone.equalsIgnoreCase(RDSUtilities.AVAIL_ZN_WEST)
					|| aZone.equalsIgnoreCase(RDSUtilities.AVAIL_ZN_SOUTH)
					|| aZone.equalsIgnoreCase(RDSUtilities.AVAIL_ZN_AP) || aZone
					.equalsIgnoreCase(RDSUtilities.AVAIL_ZN_EU))) {
				msg = "Validate: Availability Zone needs to be one of"
						+ RDSUtilities.AVAIL_ZN_EAST + " | "
						+ RDSUtilities.AVAIL_ZN_WEST + " | "
						+ RDSUtilities.AVAIL_ZN_SOUTH + " | "
						+ RDSUtilities.AVAIL_ZN_EU + " | "
						+ RDSUtilities.AVAIL_ZN_AP;
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = aZone;
			}
		}
		return retval;
	}

	/**************************************************************************
	 * BackupRetentionPeriod is optional. Defaults to 1 range 0-8 // cannot be
	 * set to 0 if has instance has readreplica's
	 * 
	 * @param backRetPeriod
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static int validateBackupRetPeriod(final Integer backRetPeriod,
			final boolean required) throws BaseException {
		String msg = "";
		int retval = 0;

		if (backRetPeriod == null) {
			if (required) {
				msg = "Validate: BackupRetentionPeriod must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = 1;
			}
		}

		else if (backRetPeriod.intValue() < 0 || backRetPeriod.intValue() > 8) {
			msg = "Validate: BackupRententionPeriod must be between 0 and 8";
			logger.error("ValidateInstance: " + msg);
			throw new BaseException(msg);
		} else {
			retval = backRetPeriod.intValue();
		}
		return retval;
	}

	/**************************************************************************
	 * Engine is required and only current valid value is "MySQL" TODO: Should
	 * look up from Engine Table
	 * 
	 * @param engine
	 * @param required
	 * @return String engine
	 * @throws BaseException
	 */
	public static String validateEngine(final String engine,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (engine == null || engine == "") {
			if (required) {
				msg = "Validate: Engine be Provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = RDSUtilities.ENGINE_MYSQL;
			}
		} else if (!engine.equalsIgnoreCase(RDSUtilities.ENGINE_MYSQL)
				&& !engine.equalsIgnoreCase(RDSUtilities.ENGINE_ORACLE_EE)
				&& !engine.equalsIgnoreCase(RDSUtilities.ENGINE_ORACLE_SE)
				&& !engine.equalsIgnoreCase(RDSUtilities.ENGINE_ORACLE_SE1)) {
			msg = "Validate: The only engine supported today is MySQL and Oracle not ."
					+ engine + ".";
			logger.error("ValidateInstance: " + msg);
			throw new BaseException(msg);
		} else {
			retval = engine;
		}
		return retval;
	}

	/**************************************************************************
	 * Engine version is optional and only value is 5.5 TODO: Should look up
	 * default engine version from Engine table
	 * 
	 * @param engineVersion
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static String validateEngineVersion(final String engineVersion,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (engineVersion == null || engineVersion == null) {
			if (required) {
				msg = "Validate: EngineVersion be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = RDSUtilities.ENGINE_VERSION_55;
			}
		} else if (!engineVersion.contentEquals(RDSUtilities.ENGINE_VERSION_55)
				&& !engineVersion
						.contentEquals(RDSUtilities.ENGINE_VERSION_11_2_0_2_V2)) {
			msg = "Validate: The only engine version supported today is 5.5 and 11.2";
			logger.error("ValidateInstance: " + msg);
			throw new BaseException(msg);
		} else {
			retval = engineVersion;
		}
		return retval;
	}

	/**************************************************************************
	 * Source Type is required and has no default but must be one of db-instance
	 * | db-security-group | db-parameter-group | db-snapshot
	 * 
	 * @param sourceType
	 * @throws RDSExceptione
	 */
	public static String validateEventSourceType(final String sourceType,
			final boolean required) throws BaseException {
		if (sourceType == null || sourceType == "") {
			// required and no default
			if (required) {
				final String msg = "ValidationFailure: SourceType is null";
				logger.error(msg);
				throw new BaseException(msg);
			}
		} else if (sourceType != RDSUtilities.EVENT_SRC_DBINSTANCE
				&& sourceType != RDSUtilities.EVENT_SRC_SECGRP
				&& sourceType != RDSUtilities.EVENT_SRC_PARAMGRP
				&& sourceType != RDSUtilities.EVENT_SRC_SNAPS) {
			final String msg = "SourceType must be one of db-instance | "
					+ "db-security-group | db-parameter-group | db-snapshot";
			logger.error(msg);
			throw new BaseException(msg);
		}
		return sourceType;
	}

	/*****************************************************************
	 * validate identifier First character must be a letter Cannot end with a
	 * hyphen or contain two consecutive hyphen This is multi-purpose method
	 * that can used for any identifier by passing in the max size of the
	 * identifier
	 * 
	 * @param id
	 * @param length
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static String validateIdentifier(final String id, final int length,
			final Boolean required) throws BaseException {
		final int len = id.length();
		final char first = id.charAt(0);
		final char last = id.charAt(len - 1);
		String msg = "";

		if (id == null || "".equals(id)) {
			// identifier is nearly always required field and
			// has no default
			if (required) {
				msg = "ValidationFailure: " + id + " is null";
				logger.error(msg);
				throw new BaseException(msg);
			}
		} else if (id.length() < 1 || id.length() > length) {
			msg = "ValidationFailure for :" + id + " must be between 1 "
					+ "and " + length + " and not " + id.length();
			logger.error(msg);
			throw new BaseException(msg);
		}

		if (!Character.isLetter(first)) {
			msg = "Validation Failure for : " + id + " the first character "
					+ "must be a letter";
			logger.error(msg);
			throw new BaseException(msg);
		}

		if (last == '-') {
			msg = "Validation Failure for : " + id + " the last character "
					+ "cannot be a hypen";
			logger.error(msg);
			throw new BaseException(msg);
		}

		if (id.contains("--")) {
			msg = "Validation Failure for : " + id + "Cannot contain "
					+ "two consequtive hypens";
			logger.error(msg);
			throw new BaseException(msg);
		}
		return id;
	}

	/**************************************************************************
	 * ValidateInstance Record This method validates the CreateInstanceRequest
	 * message and sets defaults. This seems very complex but does a lot of
	 * essential work if RDS is to be robust. It returns a record in the
	 * RdsDBinstance format generated by Hibernate and can be directly saved to
	 * the RDS database using Hibernate
	 * 
	 * @param crtRec
	 * @param instID
	 * @param userID
	 * @return RdsDbinstance
	 * @throws BaseException
	 */
	public static RdsDbinstance validateInstance(final Session sess,
			final CreateDBInstanceRequest crtRec, final AccountBean ac) {

		final RdsDbinstance resp = new RdsDbinstance();

		final String instID = crtRec.getDBInstanceIdentifier();
		validateIdentifier(instID, 63, true);

		// build and set the primary key record
		resp.setDbinstanceId(instID);
		resp.setAccount(ac);

		// AllocateStorage
		resp.setAllocatedStorage(validateAllocatedStorage(
				crtRec.getAllocatedStorage(), true));

		// Engine
		resp.setEngine(validateEngine(crtRec.getEngine(), true));

		// Engine Version
		resp.setEngineVersion(validateEngineVersion(crtRec.getEngineVersion(),
				false));

		// AvailabilityZone
		final String avz = crtRec.getAvailabilityZone();
		resp.setAvailabilityZone(avz);

		// DBInstanceClass
		final String inc = crtRec.getDBInstanceClass();
		resp.setDbinstanceClass(inc);

		// MulitAZ is Boolean and defaults to False
		resp.setMultiAz(RDSUtilities.defaultFalse(crtRec.getMultiAZ()));

		// Port
		resp.setPort(validatePort(crtRec.getPort(), false));

		// MasterUsername
		resp.setMasterUsername(validateMasterUsername(
				crtRec.getMasterUsername(), true));

		// MaterUsername is required - no default
		resp.setMasterUserPassword(validateMasterPassword(
				crtRec.getMasterUserPassword(), true));

		// DBname is optional and has no default so do nothing
		resp.setDbName(crtRec.getDBName());

		// AutoMinorVersionUpgrade is boolean default to false
		resp.setAutoMinorVersionUpgrade(RDSUtilities.defaultTrue(crtRec
				.getAutoMinorVersionUpgrade()));

		// BackupRetentionPeriod
		resp.setBackupRetentionPeriod(validateBackupRetPeriod(
				crtRec.getBackupRetentionPeriod(), false));

		// PreferredMaintenanceWindow
		resp.setPreferredMaintenanceWindow(crtRec
				.getPreferredMaintenanceWindow());

		// PreferredMaintenanceWindow
		resp.setPreferredBackupWindow(crtRec.getPreferredBackupWindow());

		// AutoMinorVersionUpgrade boolean defaults to false
		resp.setAutoMinorVersionUpgrade(RDSUtilities.defaultFalse(crtRec
				.getAutoMinorVersionUpgrade()));

		// Perform cross field validation

		// PreferredBackupWindow cannot be set if BackupRetentionPeriod is 0
		// since we default the PreferredBackupWindow to that of the
		// AvailabilityZone
		// all we need do is set PreferredBackupWindow to null
		if (resp.getBackupRetentionPeriod() == 0) {
			resp.setPreferredBackupWindow("");
		}

		// Availability Zone parameter cannot be specified if the
		// MultiAZ parameter is set to true
		if (resp.getMultiAz()
				&& (crtRec.getAvailabilityZone() != null || crtRec
						.getAvailabilityZone() != "")) {
			throw new BaseException(
					"ValidateInstance: Availability Zone cannot be specified if MultiAZ is set to true");
		}

		// the following are set manually
		resp.setInstanceCreateTime(RDSUtilities.getCurrentDateTime());
		resp.setLatestRestorableTime(RDSUtilities.getCurrentDateTime());
		resp.setDbinstanceStatus(RDSUtilities.STATUS_CREATING);
		resp.setReplicationUserName("replicate");
		resp.setReplicationPassword(UUID.randomUUID().toString());

		logger.info("Successfully Validated Messsage for Instance " + instID);
		return resp;
	}

	/**************************************************************************
	 * DBInstanceClass is required and must be one of db.m1.small | db.m1.large
	 * | db.m1.xlarge | db.m2.xlarge |db.m2.2xlarge | db.m2.4xlarge Default is
	 * db.m1.small
	 * 
	 * @return EBInstanceClass
	 * @throws BaseException
	 */
	public static String validateInstanceClass(final String instClass,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (instClass == null || instClass == "") {
			if (required) {
				msg = "Validate: DBInstanceClass must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = RDSUtilities.INST_CLASS_SMALL;
			}
		}

		else if (!(instClass.contentEquals(RDSUtilities.INST_CLASS_SMALL)
				|| instClass.contentEquals(RDSUtilities.INST_CLASS_LARGE)
				|| instClass.contentEquals(RDSUtilities.INST_CLASS_XLARGE)
				|| instClass.contentEquals(RDSUtilities.INST_CLASS_2LARGE)
				|| instClass.contentEquals(RDSUtilities.INST_CLASS_2XLARGE) || instClass
				.contentEquals(RDSUtilities.INST_CLASS_4XLARGE))) {
			msg = "DBInstanceClass must be one of "
					+ RDSUtilities.INST_CLASS_SMALL + " | "
					+ RDSUtilities.INST_CLASS_LARGE + " | "
					+ RDSUtilities.INST_CLASS_XLARGE + " | "
					+ RDSUtilities.INST_CLASS_2LARGE + " | "
					+ RDSUtilities.INST_CLASS_2XLARGE + " | "
					+ RDSUtilities.INST_CLASS_4XLARGE + " | " + " and not "
					+ instClass;

			logger.error("ValidateInstance: " + msg);
			throw new BaseException(msg);
		} else {
			retval = instClass;
		}
		return retval;
	}

	/*************************************************************************
	 * MasterUserPassword is required - no default should be between 4 and 41
	 * characters long
	 * 
	 * @param password
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static String validateMasterPassword(final String password,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (password == null || password == "") {
			if (required) {
				msg = "Validate: MasterUserPassword must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = RDSUtilities.MYSQL_ROOT_PASSWORD;
			}
		} else {
			final int len = password.length();
			if (len < 4 && len > 41) {
				msg = "Validate: MasteRUserPassword should be between 4 and 41 characters";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = password;
			}
		}
		return retval;
	}

	/**************************************************************************
	 * MaterUsername is required - no default should be 1 -16 characters first
	 * character needs to be a letter cannot contain a reserved word
	 * 
	 * @param userName
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static String validateMasterUsername(final String userName,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (userName == null || userName == "") {
			if (required) {
				msg = "Validate: MasterUserName must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = RDSUtilities.MYSQL_ROOT_USER;
			}
		} else {
			final int len = userName.length();
			if (len < 1 && len > 16) {
				msg = "Validate: MasterUsername should be between 1 and 16 characters";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = userName;
			}
		}
		return retval;
	}

	/***************************************************************************
	 * Max Records controls pagination it can be either 1 (if selecting a single
	 * record) or between 20 and 100 when returning lists
	 * 
	 * @param maxRecords
	 * @return int maxRecords
	 * @throws BaseException
	 */
	public static int validateMaxRecords(final int inVal, final boolean required)
			throws BaseException {
		int maxRecords = 0;
		if (inVal == 0) {
			if (required) {
				final String msg = "Validation Error: MaxRecords is required";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				maxRecords = 100;
			}
		} else if (inVal != 1 && (inVal < 20 || inVal > 100)) {
			final String msg = "Validation Error: MaxRecords must be between 20 "
					+ "and 100 and not " + maxRecords;
			logger.error(msg);
			throw new BaseException(msg);
		} else {
			maxRecords = inVal;
		}
		return maxRecords;
	}

	/*************************************************************************
	 * The BigInteger version of maxRecords validation operation
	 * 
	 * @param maxRecords
	 * @param required
	 * @return int MaxRecords
	 * @throws BaseException
	 */
	public static int validateMaxRecords(final Integer inVal,
			final boolean required) throws BaseException {
		int maxRecords = 0;

		if (inVal == null) {
			if (required) {
				final String msg = "Validation Error: MaxRecords is required";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				maxRecords = 100;
			}
		} else {
			maxRecords = inVal.intValue();

			if (maxRecords != 1 && (maxRecords < 20 || maxRecords > 100)) {
				final String msg = "Validation Error: MaxRecords must be between 20 "
						+ "and 100 and not " + maxRecords;
				logger.error(msg);
				throw new BaseException(msg);
			}
		}
		return maxRecords;
	}

	/**************************************************************************
	 * MinimumEngine version defaults to the current minimum engine version of
	 * 5.5 If future engine versions are added we will need to confirm that the
	 * minimum engine version requested is not less than or greater than the
	 * version supported in the DBEngine table
	 * 
	 * @param minEngineVersion
	 * @param required
	 * @return value of minimum engine version
	 * @throws BaseException
	 *             TODO: Depends on the engine (eg MySQL) and should be
	 *             calculated from the DBEngine table (select Minimum version
	 *             from dbengine where engine = "mysql")
	 */
	public static String validateMinEngineVersion(
			final String minEngineVersion, final boolean required)
			throws BaseException {
		String retval = "";

		if (!(minEngineVersion == null || minEngineVersion == "")) {
			if (required) {
				final String msg = "Validation Error: Minimum Engine Version is Required";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				// set default to the minimum engine version supported
				retval = RDSUtilities.ENGINE_VERSION_55;
			}
		}

		else if (!minEngineVersion
				.equalsIgnoreCase(RDSUtilities.ENGINE_VERSION_55)) {
			final String msg = "Validation Error: Minumum Engine Version can "
					+ "only be set to " + RDSUtilities.ENGINE_VERSION_55;
			logger.error("addParameter:n " + msg);
			throw new BaseException(msg);
		}
		return retval;
	}

	/****************************************************************************
	 * This merges the new values provided in the modifyRequest with the
	 * existing values on the database record (dbRec) The modified values will
	 * take precedence assuming they pass validation We can only modify a
	 * limited number (10) of dbInstance attributes as listed below The
	 * following (8) attributes are not modifiable AvailabilityZone,
	 * address(host), Port, Engine, MasterUsername, DBName,
	 * ReadReplicaSourceDbinstanceIdentifier, InstanceCreateTime, We provide the
	 * RdsInstance record for the existing instance record and For each
	 * attribute that is provided in the modify request (not null) we validate
	 * and copy to the RDsInstance record that is returned to the caller
	 * 
	 * @param modifyRec
	 * @param dbRec
	 * @param userID
	 * @return updated RdsDbinstance
	 * @throws BaseException
	 */
	public static RdsDbinstance validateModifyRequest(final Session sess,
			final ModifyDBInstanceRequest modifyRec, final RdsDbinstance dbRec,
			final long userID) throws BaseException {
		String msg = "";

		try {

			// 1. DBInstanceClass
			String instClass = modifyRec.getDBInstanceClass();
			if (instClass != null && instClass == "") {
				instClass = validateInstanceClass(
						modifyRec.getDBInstanceClass(), false);
				dbRec.setDbinstanceClass(instClass);
			}

			// 2. AllocatedStorage - BigInteger
			if (modifyRec.getAllocatedStorage() != null) {
				int allocStorage = validateAllocatedStorage(
						modifyRec.getAllocatedStorage(), false);
				// Value must be 10% greater than current. Values that are not
				// are rounded up to 10% more
				final int target = dbRec.getAllocatedStorage()
						+ dbRec.getAllocatedStorage() / 10;
				if (allocStorage < target) {
					allocStorage = target;
				}
				dbRec.setAllocatedStorage(allocStorage);
			}

			// 3. Engine Version
			String engineVersion = modifyRec.getEngineVersion();
			if (engineVersion != null && engineVersion != "") {
				engineVersion = validateEngine(engineVersion, false);
				dbRec.setEngineVersion(engineVersion);
			}

			// 4. MulitAZ - Boolean
			if (modifyRec.getMultiAZ() != (Boolean) null) {
				dbRec.setMultiAz(modifyRec.getMultiAZ());
			}

			// 5. MasterUserPassword
			String mPassword = modifyRec.getMasterUserPassword();
			if (mPassword != null && mPassword != "") {
				mPassword = validateMasterPassword(
						modifyRec.getMasterUserPassword(), false);
				dbRec.setMasterUserPassword(mPassword);
			}

			// 6. AutoMinorVersionUpgrade is boolean
			if (modifyRec.getAutoMinorVersionUpgrade() == (Boolean) null) {
				dbRec.setAutoMinorVersionUpgrade(RDSUtilities
						.defaultFalse(modifyRec.getAutoMinorVersionUpgrade()));
			}

			// 7. AllowMajorVersionUpgrade
			// This is not part of the original Create Request or
			// the pending modification records
			if (modifyRec.getAllowMajorVersionUpgrade() == (Boolean) null) {
				dbRec.setAllowMajorVersionUpgrade(modifyRec
						.getAllowMajorVersionUpgrade());
			}

			// 8. BackupRetentionPeriod - BigInteger
			if (modifyRec.getBackupRetentionPeriod() != null) {
				final int backRetPeriod = validateBackupRetPeriod(
						modifyRec.getBackupRetentionPeriod(), false);
				dbRec.setBackupRetentionPeriod(backRetPeriod);
			}

			// 9. PreferredMaintenanceWindow
			String prefMainWindow = modifyRec.getPreferredMaintenanceWindow();
			if (prefMainWindow != null && prefMainWindow != "") {
				prefMainWindow = validatePreferredMainWindow(prefMainWindow,
						false);
				dbRec.setPreferredMaintenanceWindow(prefMainWindow);
			}

			// 10. PreferredMaintenanceWindow
			String prefBackWindow = modifyRec.getPreferredBackupWindow();
			if (prefBackWindow != null && prefBackWindow != "") {
				prefBackWindow = validatePreferredBackWindow(prefBackWindow,
						false);
				dbRec.setPreferredBackupWindow(prefBackWindow);
			}

			// Perform cross field validation

			// PreferredBackupWindow cannot be set if BackupRetentionPeriod is 0
			// since we default the PreferredBackupWindow to that of the
			// AvailabilityZone
			// all we need do is set PreferredBackupWindow to null
			if (dbRec.getBackupRetentionPeriod() == 0) {
				dbRec.setPreferredBackupWindow("");
			}

			// Availability Zone parameter cannot be specified if the
			// MultiAZ parameter is set to true
			if (dbRec.getMultiAz()
					&& (dbRec.getAvailabilityZone() != null || dbRec
							.getAvailabilityZone() != "")) {
				msg = "ValidateInstance: Availability Zone cannot be specified if "
						+ "MultiAZ is set to true";
				logger.error(msg);
				throw new BaseException(msg);
			}

			dbRec.setDbinstanceStatus(RDSUtilities.STATUS_CREATING);

			msg = " validateModifyRequest:Successfully Validated Messsage "
					+ "for Instance " + modifyRec.getDBInstanceIdentifier();
			logger.info(msg);

		} catch (final BaseException e) {
			throw new BaseException(e.getMessage());
		} catch (final Exception e) {
			msg = " validateModifyRequest: Class: " + e.getClass() + "Msg: "
					+ e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);
		}
		return dbRec;
	}

	/****************************************************************************
	 * Apply Method is an enumeration and can only be one of immediate and
	 * pending-reboot - defaults to pending-reboot
	 * 
	 * @param appMethEnum
	 * @param required
	 * @return String containing the ApplyMethod value
	 * @throws BaseException
	 */
	public static String validateParamApplyMethod(
			final ApplyMethod appMethEnum, final Boolean required)
			throws BaseException {
		String appMeth = appMethEnum.toString();

		if (!(appMeth == null || appMeth == "")) {
			if (required) {
				final String msg = "Validation Error: Apply Method is Required";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				// Null and optional so return the default
				appMeth = RDSUtilities.PARM_APPMETHOD_PENDING;
			}
		}

		else if (appMeth != RDSUtilities.PARM_APPMETHOD_IMMEDIATE
				&& appMeth != RDSUtilities.PARM_APPMETHOD_PENDING) {
			final String msg = "Validation Error: Apply Method must be one of "
					+ "immediate or pending-reboot and not " + appMeth;
			logger.error(msg);
			throw new BaseException(msg);
		}
		return appMeth;
	}

	/***************************************************************************
	 * Apply type can only be one of static and dynamic and defaults to static
	 * 
	 * @param appType
	 * @param required
	 * @return String containing the apply type
	 * @throws BaseException
	 */
	public static String validateParamApplyType(final String appType,
			final boolean required) throws BaseException {
		String retval = "";

		if (!(appType == null || appType == "")) {
			if (required) {
				final String msg = "Validation Error: Apply Type is required";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				// set default
				retval = RDSUtilities.PARM_APPTYPE_STATIC;
			}
		}

		else if (!(appType.equalsIgnoreCase(RDSUtilities.PARM_APPTYPE_DYNAMIC) || appType
				.equalsIgnoreCase(RDSUtilities.PARM_APPTYPE_STATIC))) {
			final String msg = "Validation Error: Apply Type must be one of "
					+ "static or dynamic and not " + appType;
			logger.error(msg);
			throw new BaseException(msg);
		} else {
			// valid Apply Type provided
			retval = appType;
		}
		return retval;
	}

	/**************************************************************************
	 * ParameterGroupFamily is essentially the concatenation of Engine and
	 * Version and the only current valid value is "MySQL5.5" TODO: Should look
	 * up from Engine Table
	 * 
	 * @param paramGrpFamily
	 * @param required
	 * @return String engine
	 * @throws BaseException
	 */
	public static String validateParamGrpFamily(final String parmGrpFamily,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		if (parmGrpFamily == null || parmGrpFamily == "") {
			if (required) {
				msg = "Validate: ParamterGroupFamily must be Provided";
				logger.error(msg);
				throw RDSQueryFaults.MissingParameter();
			} else {
				retval = RDSUtilities.ENGINE_FAMILY;
			}
		} else if (!parmGrpFamily.toUpperCase().contentEquals(
				RDSUtilities.ENGINE_FAMILY.toUpperCase())
				&& !parmGrpFamily.toUpperCase().contentEquals(
						RDSUtilities.ENGINE_FAMILY_ORACLE.toUpperCase())) {
			msg = "Validate: The only ParameterGroupFamily supported is "
					+ RDSUtilities.ENGINE_FAMILY + "and "
					+ RDSUtilities.ENGINE_FAMILY_ORACLE + " and not "
					+ parmGrpFamily;
			logger.error("ValidateInstance: " + msg);
			throw RDSQueryFaults
					.InvalidParameterValue("DBParameterGroupFamily parameter has a value that is not compatible.");
		} else if (parmGrpFamily.toUpperCase().contentEquals(
				RDSUtilities.ENGINE_FAMILY.toUpperCase())) {
			retval = RDSUtilities.ENGINE_FAMILY;
		} else {
			retval = RDSUtilities.ENGINE_FAMILY_ORACLE;
		}
		return retval;
	}

	/**************************************************************************
	 * Parameter Source can only be user since the engine and system parameters
	 * will be loaded by SQL script as reference data not by the AWS users using
	 * the RDS service
	 * 
	 * @param source
	 * @param required
	 * @return source value
	 * @throws BaseException
	 */
	public static String validateParamSource(final String source,
			final boolean required) throws ErrorResponse {
		final String retval = "";

		if (source == null || "".equals(source)) {
			return null;
		}
		if (!source.equals("user") && !source.equals("system")
				&& !source.equals("engine-default")) {
			throw RDSQueryFaults
					.InvalidParameterValue("Source value must be \"user\", \"system\", or \"engine-default\".");
		}
		return source;
	}

	/**************************************************************************
	 * Port is optional and defaults to 3306. If set must be between 1150 and
	 * 65635
	 * 
	 * @param port
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static int validatePort(final Integer port, final boolean required)
			throws BaseException {
		String msg = "";
		int retval = 0;

		if (port == null) {
			if (required) {
				msg = "Validate: Port must be provided";
				logger.error(msg);
				throw new BaseException(msg);
			} else {
				retval = 3306;
			}
		}

		else if (port.intValue() < 1150 || port.intValue() > 65635) {
			msg = "Validate: Port must be between 1150 and 65635";
			logger.error("ValidateInstance: " + msg);
			throw new BaseException(msg);
		} else {
			retval = port.intValue();
		}
		return retval;
	}

	/**************************************************************************
	 * PreferredBackupWindow is optional and occurs daily It defaults to the
	 * PreferredBackupWindow for the AvialabilityZone Must be in the format
	 * hh24:mi-hh24:mi must be at least two hours in duration
	 * 
	 * @param pBackWin
	 * @param required
	 * @return
	 * @throws BaseException
	 */
	public static String validatePreferredBackWindow(final String pBackWin,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		try {

			if (pBackWin == null || pBackWin == "") {
				if (required) {
					msg = "Validate: PreferredBackupWindow must be provided";
					logger.error(msg);
					throw new BaseException(msg);
				} else {
					// TODO look up from the availability zone table
					retval = "04:05-06:05";
				}
			} else {
				// Must be in the format "hh24:mi-hh24:mi" and at
				// least two hours in duration
				if (pBackWin.length() != 11) {
					msg = "Validate: PreferredBackupWindow format is "
							+ "incorrect: It should be hh24:mi-hh24:mi"
							+ "(eg 00:01-02:01) and not " + pBackWin;
					logger.error(msg);
					throw new BaseException(msg);
				}
				final String startString = pBackWin.substring(0, 5);
				final String endString = pBackWin.substring(6, 11);

				final DateFormat formatter = new SimpleDateFormat("HH:mm");
				final Date startDate = (Date) formatter.parse(startString);
				final Date endDate = (Date) formatter.parse(endString);

				final long diff = endDate.getTime() / 1000
						- startDate.getTime() / 1000;
				if (diff < 2 * 60 * 60) {
					msg = "Validate: PreferredMaintenanceWindow difference is less "
							+ "than 2 hours (120 minutes) = "
							+ diff
							/ 60
							+ " minutes";
					logger.error(msg);
					throw new BaseException(msg);
				}
			}

		} catch (final ParseException e) {
			msg = "updateInstance: ExceptionClass: " + e.getClass() + " Msg: "
					+ e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);

		} catch (final Exception e) {
			msg = "updateInstance: ExceptionClass: " + e.getClass() + " Msg: "
					+ e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);
		}
		return retval;
	}

	/***************************************************************************
	 * PreferredBackupWindow is optional and occurs weekly It defaults to the
	 * PreferredBackupWindow for the AvialabilityZone Must be in the format
	 * ddd:hh24:mi-ddd:hh24:mi Valid days are Mon,Tue, Wed, Thr, Fri, Sat, Sun
	 * must be at least four hours in duration
	 * 
	 * @param pMainWin
	 * @param required
	 * @return String preferredBackupWindow
	 * @throws BaseException
	 */
	public static String validatePreferredMainWindow(final String pMainWin,
			final boolean required) throws BaseException {
		String msg = "";
		String retval = "";

		try {

			if (pMainWin == null || pMainWin == "") {
				if (required) {
					msg = "Validate: PreferredMaintenanceWindow must be provided";
					logger.error(msg);
					throw new BaseException(msg);
				} else {
					// TODO default to the PreferredMaintenance for the
					// availability zone
					retval = "Sun:00:01-Sun:04:01";
				}
			} else {
				// Must be in the format "ddd:hh24:mi-ddd:hh24:mi and at least
				// four hours in duration
				if (pMainWin.length() != 19) {
					msg = "Validate: PreferredMaintenanceWindow format is "
							+ "incorrect: It should be ddd:hh24:mi-ddd:hh24:mi"
							+ "(eg Sun:00:01-Sun:04:01) and not " + pMainWin;
					logger.error(msg);
					throw new BaseException(msg);
				}

				final String startString = pMainWin.substring(0, 9);
				final String endString = pMainWin.substring(10, 19);

				final DateFormat formatter = new SimpleDateFormat("EEE:HH:mm");
				final Date startDate = (Date) formatter.parse(startString);
				final Date endDate = (Date) formatter.parse(endString);

				final long diff = endDate.getTime() / 1000
						- startDate.getTime() / 1000;
				if (diff < 4 * 60 * 60) {
					msg = "Validate: PreferredMaintenanceWindow difference is less "
							+ "than 4 hours (240 minutes) = "
							+ diff
							/ 60
							+ " minutes";
					logger.error(msg);
					throw new BaseException(msg);
				}
			}

		} catch (final ParseException e) {
			msg = "Validate: ParseExcption: Msg: " + e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);

		} catch (final Exception e) {
			msg = "updateInstance: ExceptionClass: " + e.getClass() + " Msg: "
					+ e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);
		}
		return retval;
	}

	/**************************************************************************
	 * The read replica is based mostly on the existing master record with the
	 * exception of DBInstanceClass, AvailabilityZone, Port,
	 * AutoMinorVersionUpgrade which can be supplied by the user There is no
	 * need to validate the attributes of the master record since they would
	 * have been validated when created / modified. This method therefore
	 * compiles the RdDbinstance record by validating the 5 values provided in
	 * CreateReadReplica request and and copies the others from the master
	 * instance record.
	 * 
	 * @param crtRec
	 * @param userID
	 * @return fully validated RdsDbinstance record
	 * @throws BaseException
	 */
	public static RdsDbinstance validateReadReplica(final Session sess,
			final CreateDBInstanceReadReplicaRequest crtRec,
			final AccountBean ac) throws BaseException {
		String msg = "";
		RdsDbinstance resp = null;

		try {

			resp = new RdsDbinstance();

			// validate the Read Replica InstanceID supplied by the user
			final String rrInstID = validateIdentifier(
					crtRec.getDBInstanceIdentifier(), 63, true);
			final String srcInstID = crtRec.getSourceDBInstanceIdentifier();

			// Get the master instance record
			final RdsDbinstance masterRec = RDSUtil.getInstance(sess,
					srcInstID, ac.getId());
			if (masterRec == null) {
				msg = "validateReadReplica Master DBInstance record "
						+ srcInstID + " does not exists";
				logger.error(msg);
				throw new BaseException(msg);
			}

			// Master Status must be Available
			/*
			 * if (! masterRec.getDBInstanceStatus().equalsIgnoreCase(
			 * RDSUtilities.STATUS_AVAILABLE)) { msg =
			 * "validateReadReplica Master DBInstance record status " +
			 * "must be Available and not " + masterRec.getDBInstanceStatus();
			 * logger.error(msg); throw new RDSException(msg); }
			 */

			// build and set the primary key record
			resp.setDbinstanceId(rrInstID);
			resp.setAccount(ac);

			// 1. ReadReplicaSourceDbinstanceIdentifier
			resp.setSourceDbinstanceId(srcInstID);

			// 2. dbInstanceClass
			resp.setDbinstanceClass(validateInstanceClass(
					crtRec.getDBInstanceClass(), false));

			// 3. AvailabilityZone
			resp.setAvailabilityZone(validateAvailabilityZone(
					crtRec.getAvailabilityZone(), false));

			// 4. Port
			resp.setPort(validatePort(crtRec.getPort(), false));

			// 5. AutoMinorVersionUpgrade is boolean default to false
			resp.setAutoMinorVersionUpgrade(RDSUtilities.defaultFalse(crtRec
					.getAutoMinorVersionUpgrade()));

			// the following are copied from the master record unaltered
			// AllocateStorage
			resp.setAllocatedStorage(masterRec.getAllocatedStorage());

			// Engine
			resp.setEngine(masterRec.getEngine());

			// Engine Version
			resp.setEngineVersion(masterRec.getEngineVersion());

			// MulitAZ is Boolean and defaults to False
			resp.setMultiAz(masterRec.getMultiAz());

			// MasterUsername
			resp.setMasterUsername(masterRec.getMasterUsername());

			// MaterUsername is required - no default
			resp.setMasterUserPassword(masterRec.getMasterUserPassword());

			// DBname is optional and has no default so do nothing
			resp.setDbName(masterRec.getDbName());

			// AutoMinorVersionUpgrade is boolean default to false
			resp.setAutoMinorVersionUpgrade(masterRec
					.getAutoMinorVersionUpgrade());

			// BackupRetentionPeriod
			resp.setBackupRetentionPeriod(masterRec.getBackupRetentionPeriod()
					.intValue());

			// PreferredMaintenanceWindow
			resp.setPreferredMaintenanceWindow(masterRec
					.getPreferredMaintenanceWindow());

			// PreferredMaintenanceWindow
			resp.setPreferredBackupWindow(masterRec.getPreferredBackupWindow());

			// AutoMinorVersionUpgrade boolean defaults to false
			resp.setAutoMinorVersionUpgrade(RDSUtilities.defaultFalse(crtRec
					.getAutoMinorVersionUpgrade()));

			// the following are set manually
			resp.setInstanceCreateTime(RDSUtilities.getCurrentDateTime());
			resp.setLatestRestorableTime(RDSUtilities.getCurrentDateTime());
			resp.setDbinstanceStatus(RDSUtilities.STATUS_CREATING);

			msg = "validateReadReplica:Successfully Validated Messsage "
					+ "for Instance " + rrInstID;
			logger.info(msg);

		} catch (final BaseException e) {
			throw new BaseException(e.getMessage());
		} catch (final Exception e) {
			msg = "validateReadReplica: Class: " + e.getClass() + "Msg: "
					+ e.getMessage();
			logger.error(msg);
			throw new BaseException(msg);
		}
		return resp;
	}
}
