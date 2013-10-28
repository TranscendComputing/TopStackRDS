package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeDBParametersActionMessage.DescribeDBParametersActionResultMessage;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBParametersActionResultMarshaller implements
		Marshaller<String, DescribeDBParametersActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeDBParametersActionResultMessage input)
			 {

		// TODO: fill out the result as an XML document
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEDBPARAMETERSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEDBPARAMETERSRESULT);

		QueryUtilV2.addNode(nr, RDS_Constants.NODE_MARKER, input.getMarker());

		RDSQueryUtilV2.marshalParameters(nr, input.getParametersList());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
