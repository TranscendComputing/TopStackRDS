package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RebootDBInstanceActionMessage.RebootDBInstanceActionResultMessage;

public class RebootDBInstanceActionResultMarshaller implements
		Marshaller<String, RebootDBInstanceActionResultMessage> {

	@Override
	public String marshall(final RebootDBInstanceActionResultMessage input)
			 {

		final DBInstance dbInst = getDbInstance(input);
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_REBOOTDBINSTANCERESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nodeActionResultMessage = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_REBOOTDBINSTANCERESULT);
		RDSQueryUtilV2.marshalDBInstance(nodeActionResultMessage, dbInst);

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
	
	public DBInstance getDbInstance(RebootDBInstanceActionResultMessage message) {
		DBInstance.Builder result = DBInstance.newBuilder();
		result.setAllocatedStorage(message.getAllocatedStorage());
		result.setAutoMinorVersionUpgrade(message.getAutoMinorVersionUpgrade());
		result.setAvailabilityZone(message.getAvailabilityZone());
		result.setBackupRetentionPeriod(message.getBackupRetentionPeriod());
		result.setCharacterSetName(message.getCharacterSetName());
		result.setDbInstanceClass(message.getDbInstanceClass());
		result.setDbInstanceIdentifier(message.getDbInstanceIdentifier());
		result.setDbInstanceStatus(message.getDbInstanceStatus());
		result.setDbName(message.getDbName());
		result.addAllDbParameterGroups(message.getDbParameterGroupsList());
		result.addAllDbSecurityGroups(message.getDbSecurityGroupsList());
		result.setDbSubnetGroup(message.getDbSubnetGroup());
		result.setEndpoint(message.getEndpoint());
		result.setEngine(message.getEngine());
		result.setEngineVersion(message.getEngineVersion());
		result.setInstanceCreateTime(message.getInstanceCreateTime());
		result.setIops(message.getIops());
		result.setLatestRestorableTime(message.getLatestRestorableTime());
		result.setLicenseModel(message.getLicenseModel());
		result.setMasterUsername(message.getMasterUsername());
		result.setMultiAZ(message.getMultiAZ());
		result.addAllOptionGroupMemberships(message.getOptionGroupMembershipsList());
		result.setPendingModifiedValues(message.getPendingModifiedValues());
		result.setPreferredBackupWindow(message.getPreferredBackupWindow());
		result.setPreferredMaintenanceWindow(message.getPreferredMaintenanceWindow());
		result.setPubliclyAccessible(message.getPubliclyAccessible());
		result.addAllReadReplicaDBInstanceIdentifiers(message.getReadReplicaDBInstanceIdentifiersList());
		result.setReadReplicaSourceDBInstanceIdentifier(message.getReadReplicaSourceDBInstanceIdentifier());
		result.setSecondaryAvailabilityZone(message.getSecondaryAvailabilityZone());
		result.addAllStatusInfos(message.getStatusInfosList());
		result.addAllVpcSecurityGroups(message.getVpcSecurityGroupsList());
		return result.build();
}

}
