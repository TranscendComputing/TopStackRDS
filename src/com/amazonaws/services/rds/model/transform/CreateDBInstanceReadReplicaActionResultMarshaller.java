package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.CreateDBInstanceReadReplicaActionMessage.CreateDBInstanceReadReplicaActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionResultMessage;

public class CreateDBInstanceReadReplicaActionResultMarshaller implements
		Marshaller<String, CreateDBInstanceReadReplicaActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final CreateDBInstanceReadReplicaActionResultMessage input)
			 {
		
		final DBInstance dbInst = input.getDbInstance();
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_CREATEDBINSTANCEREADREPLICARESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);
		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				"CreateDBInstanceReadReplicaActionResultMessage");

		RDSQueryUtilV2.marshalDBInstance(nr, dbInst);

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}	
}
