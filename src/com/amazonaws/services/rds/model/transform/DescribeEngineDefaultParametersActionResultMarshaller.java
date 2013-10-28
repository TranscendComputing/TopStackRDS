package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.DescribeEngineDefaultParametersActionMessage.DescribeEngineDefaultParametersActionResultMessage;
import com.transcend.rds.message.RDSMessage.EngineDefaults;

public class DescribeEngineDefaultParametersActionResultMarshaller implements
		Marshaller<String, DescribeEngineDefaultParametersActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final DescribeEngineDefaultParametersActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEENGINEDEFAULTPARAMETERSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);
		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEENGINEDEFAULTPARAMETERSRESULT);

		final XMLNode edNode = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_ENGINEDEFAULTS);
		QueryUtilV2.addNode(edNode, RDS_Constants.NODE_DBPARAMETERGROUPFAMILY,
				input.getDbParameterGroupFamily());
		QueryUtilV2.addNode(edNode, RDS_Constants.NODE_MARKER, input.getMarker());
		RDSQueryUtilV2.marshalParameters(edNode, input.getParametersList());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
