package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.AuthorizeDBSecurityGroupIngressActionMessage.AuthorizeDBSecurityGroupIngressActionResultMessage;

public class AuthorizeDBSecurityGroupIngressActionResultMarshaller implements
		Marshaller<String, AuthorizeDBSecurityGroupIngressActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final AuthorizeDBSecurityGroupIngressActionResultMessage input)
			 {

		final XMLNode nodeResponse = new XMLNode(
				RDS_Constants.NODE_AUTHORIZEDBSECURITYGROUPINGRESSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);
		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_AUTHORIZEDBSECURITYGROUPINGRESSRESULT);
		final XMLNode n = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_DBSECURITYGROUP);
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBSECURITYGROUPDESCRIPTION,
				input.getDbSecurityGroupDescription());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBSECURITYGROUPNAME,
				input.getDbSecurityGroupName());
		RDSQueryUtilV2.marshalEC2SecurityGroups(n, input.getEc2SecurityGroupsList());
		RDSQueryUtilV2.marshalIPRanges(n, input.getIpRangesList());

		QueryUtilV2.addNode(n, "OwnerId", input.getOwnerId());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
