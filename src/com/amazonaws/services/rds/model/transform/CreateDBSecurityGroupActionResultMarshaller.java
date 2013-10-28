package com.amazonaws.services.rds.model.transform;

import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDSQueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.CreateDBSecurityGroupActionMessage.CreateDBSecurityGroupActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBSecurityGroup;

public class CreateDBSecurityGroupActionResultMarshaller implements
		Marshaller<String, CreateDBSecurityGroupActionResultMessage> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final CreateDBSecurityGroupActionResultMessage in)
			 {

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_CREATEDBSECURITYGROUPRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		final XMLNode nr = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_CREATEDBSECURITYGROUPRESULT);
		final XMLNode dbSecGrp = QueryUtilV2.addNode(nr,
				RDS_Constants.NODE_DBSECURITYGROUP);

		QueryUtilV2.addNode(dbSecGrp,
				RDS_Constants.NODE_DBSECURITYGROUPDESCRIPTION,
				in.getDbSecurityGroupDescription());
		QueryUtilV2.addNode(dbSecGrp, RDS_Constants.NODE_DBSECURITYGROUPNAME,
				in.getDbSecurityGroupName());

		RDSQueryUtilV2.marshalEC2SecurityGroups(dbSecGrp,
				in.getEc2SecurityGroupsList());
		RDSQueryUtilV2.marshalIPRanges(dbSecGrp, in.getIpRangesList());

		QueryUtilV2.addNode(dbSecGrp, RDS_Constants.NODE_OWNERID, in.getOwnerId());

		// TODO: update aws sdk so this becomes available
		// QueryUtilV2.addNode(nr, RDS_Constants.NODE_VPCID, o.getVpcId());

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, in.getRequestId());

		return nodeResponse.toString();
	}
}
