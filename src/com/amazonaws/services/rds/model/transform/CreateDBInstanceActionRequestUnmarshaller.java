package com.amazonaws.services.rds.model.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.msi.tough.utils.RDSQueryFaults;

public class CreateDBInstanceActionRequestUnmarshaller implements
		Unmarshaller<CreateDBInstanceActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(CreateDBInstanceActionRequestUnmarshaller.class.getName());

	private static CreateDBInstanceActionRequestUnmarshaller instance;

	public static CreateDBInstanceActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateDBInstanceActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public CreateDBInstanceActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		CreateDBInstanceActionRequestMessage.Builder req =  CreateDBInstanceActionRequestMessage.newBuilder();
		//final CreateDBInstance req =  CreateDBInstance();
		
		logger.debug("Unmarshalling (inbound) CreateDBInstanceActionRequestMessage");
		req.setAutoMinorVersionUpgrade(QueryUtilV2.getBoolean(in, "AutoMinorVersionUpgrade", true));
		if(in.get("AllocatedStorage") != null){
			req.setAllocatedStorage(QueryUtilV2.getInt(in, "AllocatedStorage"));
		}
		else{
			throw RDSQueryFaults.MissingParameter();
		}
		if (in.get("AvailabilityZone") != null) {
			req.setAvailabilityZone(QueryUtilV2.getString(in, "AvailabilityZone"));
		}
		else{
			logger.debug("Default value for AvailabilityZone parameter is set.");
			req.setAvailabilityZone(RDS_Constants.DEFAULT_AVAILABILITY_ZONE);
		}
		req.setBackupRetentionPeriod(QueryUtilV2.getInt(in, "BackupRetentionPeriod", 1));
		if (in.get("DBInstanceClass") != null) {
			req.setDbInstanceClass(QueryUtilV2.getString(in, "DBInstanceClass"));
		}
		else{
			throw RDSQueryFaults.MissingParameter();
		}
		if (in.get("DBInstanceIdentifier") != null) {
			req.setDbInstanceIdentifier(QueryUtilV2.getString(in, "DBInstanceIdentifier"));
		}
		else{
			throw RDSQueryFaults.MissingParameter();
		}
		req.setDbName(QueryUtilV2.getString(in, "DBName"));
		if (in.get("DBParameterGroupName") != null) {
			req.setDbParameterGroupName(QueryUtilV2.getString(in, "DBParameterGroupName"));
		}
		else{
			req.setDbParameterGroupName("default.mysql5.5");
		}
		//DBSecurityGroups.member.1
		List<String> dbsl =  new ArrayList<String>();
		if(in.get("DBSecurityGroups.DBSecurityGroupName.1") != null) {
			for (int i = 1;; i++) {
				if (QueryUtilV2.getString(in, "DBSecurityGroups.DBSecurityGroupName." + i) == null) {
					break;
				} else{
					dbsl.add(QueryUtilV2.getString(in, "DBSecurityGroups.DBSecurityGroupName." + i));
				}
			}
			req.addAllDbSecurityGroups(dbsl);
		}
		else if(in.get("DBSecurityGroups.member.1") != null) {
			for (int i = 1;; i++) {
				if (QueryUtilV2.getString(in, "DBSecurityGroups.member." + i) == null) {
					break;
				} else{
					dbsl.add(QueryUtilV2.getString(in, "DBSecurityGroups.member." + i));
				}
			}
			req.addAllDbSecurityGroups(dbsl);
		}
		else{
			dbsl.add("default");
			req.addAllDbSecurityGroups(dbsl);
		}
		if (in.get("Engine") != null) {
			req.setEngine(QueryUtilV2.getString(in, "Engine"));
		}
		else{
			throw RDSQueryFaults.MissingParameter();
		}
		if (in.get("EngineVersion") != null) {
			req.setEngineVersion(QueryUtilV2.getString(in, "EngineVersion"));
		}
		else{
			req.setEngineVersion("5.5.20");
		}
		if (in.get("MasterUsername") != null) {
			req.setMasterUsername(QueryUtilV2.getString(in, "MasterUsername"));
		}
		else{
			throw RDSQueryFaults.MissingParameter();
		}
		if (in.get("MasterUserPassword") != null) {
			req.setMasterUserPassword(QueryUtilV2.getString(in, "MasterUserPassword"));
		}
		else{
			throw RDSQueryFaults.MissingParameter();
		}
		req.setMultiAZ(QueryUtilV2.getBoolean(in, "MultiAZ", true));
		req.setPort(QueryUtilV2.getInt(in, "Port", 3306));
		if (in.get("PreferredBackupWindow") != null) {
			req.setPreferredBackupWindow(QueryUtilV2.getString(in, "PreferredBackupWindow"));
		}
		else{
			req.setPreferredBackupWindow("05:30-06:00");
		}
		if (in.get("PreferredMaintenanceWindow") != null) {
			req.setPreferredMaintenanceWindow(QueryUtilV2.getString(in, "PreferredMaintenanceWindow"));
		}
		else{
			req.setPreferredMaintenanceWindow("sat:05:00-sat:05:30");
		}
		if (in.get("LicenseModel") != null) {
			req.setLicenseModel(QueryUtilV2.getString(in, "LicenseModel"));
		}
		else{
			req.setLicenseModel("general-public-license");
		}
		return req.buildPartial();
	}
}
