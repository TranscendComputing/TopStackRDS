package com.amazonaws.services.rds.model.transform;

import com.amazonaws.services.rds.model.DBParameterGroup;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.CreateDBParameterGroupActionMessage.CreateDBParameterGroupActionResultMessage;

public class CreateDBParameterGroupActionResultMarshaller implements
		Marshaller<String, CreateDBParameterGroupActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final CreateDBParameterGroupActionResultMessage input )
			 {
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_CREATEDBPARAMETERGROUPRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_CREATEDBPARAMETERGROUPRESULT);
		final XMLNode pg = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_DBPARAMETERGROUP);

		QueryUtilV2.addNode(pg, RDS_Constants.NODE_DBPARAMETERGROUPFAMILY,
				input.getDbParameterGroupFamily());
		QueryUtilV2.addNode(pg, RDS_Constants.NODE_DBPARAMETERGROUPNAME,
				input.getDbParameterGroupName());
		QueryUtilV2.addNode(pg, RDS_Constants.NODE_DESCRIPTION,
				input.getDescription());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
