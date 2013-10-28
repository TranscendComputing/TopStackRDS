package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.ResetDBParameterGroupActionMessage.ResetDBParameterGroupActionResultMessage;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class ResetDBParameterGroupActionResultMarshaller implements
		Marshaller<String, ResetDBParameterGroupActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final ResetDBParameterGroupActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_RESETDBPARAMETERGROUPRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nodeActionResultMessage = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_RESETDBPARAMETERGROUPRESULT);
		QueryUtilV2.addNode(nodeActionResultMessage, RDS_Constants.NODE_DBPARAMETERGROUPNAME,
				input.getDbParameterGroupName());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
