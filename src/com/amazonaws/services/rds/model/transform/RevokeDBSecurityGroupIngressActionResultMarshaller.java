package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.RDSMessage.DBSecurityGroup;
import com.transcend.rds.message.RevokeDBSecurityGroupIngressActionMessage.RevokeDBSecurityGroupIngressActionResultMessage;

public class RevokeDBSecurityGroupIngressActionResultMarshaller implements
		Marshaller<String, RevokeDBSecurityGroupIngressActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final RevokeDBSecurityGroupIngressActionResultMessage input)
			 {

		// TODO: fill out the result as an XML document
		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_REVOKEDBSECURITYGROUPINGRESSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nodeActionResultMessage = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_REVOKEDBSECURITYGROUPINGRESSRESULT);

		final XMLNode nodeDBSecGroup = QueryUtilV2.addNode(nodeActionResultMessage,
				RDS_Constants.NODE_DBSECURITYGROUP);

		QueryUtilV2.addNode(nodeDBSecGroup,
				RDS_Constants.NODE_DBSECURITYGROUPDESCRIPTION,
				input.getDbSecurityGroupDescription());
		QueryUtilV2.addNode(nodeDBSecGroup,
				RDS_Constants.NODE_DBSECURITYGROUPNAME,
				input.getDbSecurityGroupName());

		RDSQueryUtilV2.marshalEC2SecurityGroups(nodeDBSecGroup,
				input.getEc2SecurityGroupsList());
		RDSQueryUtilV2.marshalIPRanges(nodeDBSecGroup, input.getIpRangesList());
		QueryUtilV2.addNode(nodeDBSecGroup, RDS_Constants.NODE_OWNERID,
				input.getOwnerId());

		// TODO: update aws sdk for this
		// QueryUtilV2.addNode(nodeActionResultMessage, RDS_Constants.NODE_VPCID,
		// input.getVpcId());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
