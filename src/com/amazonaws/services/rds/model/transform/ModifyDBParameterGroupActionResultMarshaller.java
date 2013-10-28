package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.ModifyDBParameterGroupActionMessage.ModifyDBParameterGroupActionResultMessage;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class ModifyDBParameterGroupActionResultMarshaller implements
		Marshaller<String, ModifyDBParameterGroupActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final ModifyDBParameterGroupActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_MODIFYDBPARAMETERGROUPRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_MODIFYDBPARAMETERGROUPRESULT);

		QueryUtilV2.addNode(nr, RDS_Constants.NODE_DBPARAMETERGROUPNAME,
				input.getDbParameterGroupName());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
