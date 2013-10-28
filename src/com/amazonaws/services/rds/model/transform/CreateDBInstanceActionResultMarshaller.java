package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionResultMessage;
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;

public class CreateDBInstanceActionResultMarshaller implements
		Marshaller<String, CreateDBInstanceActionResultMessage> {
	@Override
	public String marshall(final CreateDBInstanceActionResultMessage input)
			 {
		
		final DBInstance dbInst = input.getDbInstance();
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_CREATEDBINSTANCERESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);
		final XMLNode result = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_CREATEDBINSTANCERESULT);
		RDSQueryUtilV2.marshalDBInstance(result, dbInst);

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}	
}
