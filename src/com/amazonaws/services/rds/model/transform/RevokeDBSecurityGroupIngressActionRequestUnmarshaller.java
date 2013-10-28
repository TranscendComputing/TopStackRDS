package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.RevokeDBSecurityGroupIngressActionMessage.RevokeDBSecurityGroupIngressActionRequestMessage;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.Constants;

public class RevokeDBSecurityGroupIngressActionRequestUnmarshaller implements
    Unmarshaller<RevokeDBSecurityGroupIngressActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(RevokeDBSecurityGroupIngressActionRequestUnmarshaller.class.getName());

    private static RevokeDBSecurityGroupIngressActionRequestUnmarshaller instance;

    public static RevokeDBSecurityGroupIngressActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new RevokeDBSecurityGroupIngressActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public RevokeDBSecurityGroupIngressActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final RevokeDBSecurityGroupIngressActionRequestMessage.Builder req =  RevokeDBSecurityGroupIngressActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) RevokeDBSecurityGroupIngressActionRequestMessage");
        req.setDbSecurityGroupName(QueryUtilV2.requiredString(in, Constants.DBSECURITYGROUPNAME));
        req.setCidrip(QueryUtilV2.getString(in, Constants.RDS_CIDRIP));
        req.setEc2SecurityGroupName(QueryUtilV2.getString(in, Constants.EC2SECURITYGROUPNAME));
        req.setEc2SecurityGroupOwnerId(QueryUtilV2.getString(in, Constants.EC2SECURITYGROUPOWNERID));
        
        return req.buildPartial();
    }
}
