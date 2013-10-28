package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.DeleteDBInstanceActionMessage.DeleteDBInstanceActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionResultMessage;

public class RestoreDBInstanceFromDBSnapshotActionResultMarshaller implements
		Marshaller<String, RestoreDBInstanceFromDBSnapshotActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final RestoreDBInstanceFromDBSnapshotActionResultMessage input)
			 {
		
		final DBInstance dbInst = input.getDbInstance();
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_RESTOREDBINSTANCEFROMDBSNAPSHOTRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nodeActionResultMessage = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_RESTOREDBINSTANCEFROMDBSNAPSHOTRESULT);

		RDSQueryUtilV2.marshalDBInstance(nodeActionResultMessage, dbInst);

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
