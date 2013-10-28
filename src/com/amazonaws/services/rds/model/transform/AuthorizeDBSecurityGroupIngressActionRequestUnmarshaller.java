package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.AuthorizeDBSecurityGroupIngressActionMessage.AuthorizeDBSecurityGroupIngressActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.Constants;

public class AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller implements
Unmarshaller<AuthorizeDBSecurityGroupIngressActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
	.getLogger(AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller.class
			.getName());

	private static AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller instance;

	public static AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public AuthorizeDBSecurityGroupIngressActionRequestMessage unmarshall(Map<String, String[]> in)
	 {
		final AuthorizeDBSecurityGroupIngressActionRequestMessage.Builder req =  AuthorizeDBSecurityGroupIngressActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) AuthorizeDBSecurityGroupIngressActionRequestMessage");
		req.setCidrip(QueryUtilV2.getString(in, Constants.RDS_CIDRIP));
		req.setDbSecurityGroupName(QueryUtilV2.requiredString(in, Constants.DBSECURITYGROUPNAME));
		req.setEc2SecurityGroupName(QueryUtilV2.getString(in, Constants.EC2SECURITYGROUPNAME));
		req.setEc2SecurityGroupOwnerId(QueryUtilV2.getString(in, Constants.EC2SECURITYGROUPOWNERID));

		return req.buildPartial();
	}
}
