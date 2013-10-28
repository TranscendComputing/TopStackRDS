package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeDBParameterGroupsActionMessage.DescribeDBParameterGroupsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBParameterGroup;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBParameterGroupsActionResultMarshaller implements
		Marshaller<String, DescribeDBParameterGroupsActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeDBParameterGroupsActionResultMessage input)
			 {
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEDBPARAMETERGROUPSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEDBPARAMETERGROUPSRESULT);

		final XMLNode n = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_DBPARAMETERGROUPS);

		if (input.getDbParameterGroupsCount() > 0) {
			for (final DBParameterGroup g : input.getDbParameterGroupsList()) {
				final XMLNode ng = QueryUtilV2.addNode(n,
						RDS_Constants.NODE_DBPARAMETERGROUP);

				QueryUtilV2.addNode(ng,
						RDS_Constants.NODE_DBPARAMETERGROUPFAMILY,
						g.getDbParameterGroupFamily());

				QueryUtilV2.addNode(ng, RDS_Constants.NODE_DBPARAMETERGROUPNAME,
						g.getDbParameterGroupName());

				QueryUtilV2.addNode(ng, RDS_Constants.NODE_DESCRIPTION,
						g.getDescription());
			}
		}
		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
