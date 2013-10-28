package com.amazonaws.services.rds.model.transform;

import com.transcend.rds.message.DescribeDBSecurityGroupsActionMessage.DescribeDBSecurityGroupsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBSecurityGroup;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBSecurityGroupsActionResultMarshaller implements
		Marshaller<String, DescribeDBSecurityGroupsActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(
			final DescribeDBSecurityGroupsActionResultMessage input)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEDBSECURITYGROUPSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEDBSECURITYGROUPSRESULT);

		final XMLNode ns = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_DBSECURITYGROUPS);

		if (input.getDbSecurityGroupsCount() > 0) {
			for (final DBSecurityGroup g : input.getDbSecurityGroupsList()) {
				final XMLNode n = QueryUtilV2.addNode(ns,
						RDS_Constants.NODE_DBSECURITYGROUP);

				QueryUtilV2.addNode(n,
						RDS_Constants.NODE_DBSECURITYGROUPDESCRIPTION,
						g.getDbSecurityGroupDescription());

				QueryUtilV2.addNode(n, RDS_Constants.NODE_DBSECURITYGROUPNAME,
						g.getDbSecurityGroupName());

				RDSQueryUtilV2.marshalEC2SecurityGroups(n,
						g.getEc2SecurityGroupsList());
				RDSQueryUtilV2.marshalIPRanges(n, g.getIpRangesList());

				QueryUtilV2
						.addNode(n, RDS_Constants.NODE_OWNERID, g.getOwnerId());

				// TODO: update sdk so this is available
				// QueryUtilV2.addNode(n, RDS_Constants.NODE_VPCID, g.getVpcId());
			}
		}

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
