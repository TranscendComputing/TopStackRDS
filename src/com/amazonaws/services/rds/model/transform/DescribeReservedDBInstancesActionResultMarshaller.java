package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeReservedDBInstancesActionMessage.DescribeReservedDBInstancesActionResultMessage;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeReservedDBInstancesActionResultMarshaller implements
		Marshaller<String, DescribeReservedDBInstancesActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeReservedDBInstancesActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBERESERVEDDBINSTANCESRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nodeActionResultMessage = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEDBRESERVEDDBINSTANCESRESULT);

		QueryUtilV2.addNode(nodeActionResultMessage, RDS_Constants.NODE_MARKER,
				input.getMarker());
		RDSQueryUtilV2.marshalReservedDBInstances(nodeResponse,
				input.getReservedDBInstanceList());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
