package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.rds.model.AuthorizeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.msi.tough.core.Appctx;
import com.msi.rdsquery.helper.SecurityGroupsHelper;

public class AuthorizeDBSecurityGroupIngressActionTest extends AbstractBaseRdsTest {

    @Autowired
    private SecurityGroupsHelper secGrpHelper;
    
    private static Logger logger = Appctx.getLogger
    		(AuthorizeDBSecurityGroupIngressActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-authoDBSecGp-3-" + baseName;

    //DBSecurityGroupName is required
    @Test (expected = AmazonServiceException.class)
    public void testAuthorizeDBSecurityGroupIngressNoArgs() throws Exception {
        final AuthorizeDBSecurityGroupIngressRequest request = new AuthorizeDBSecurityGroupIngressRequest();
        getRdsClient().authorizeDBSecurityGroupIngress(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testAuthorizeDBSecurityGroupIngressInvalidParameters() throws Exception {
        final AuthorizeDBSecurityGroupIngressRequest request = new AuthorizeDBSecurityGroupIngressRequest();
        request.withDBSecurityGroupName("Invalid-name");
        getRdsClient().authorizeDBSecurityGroupIngress(request);
    }

    @Test
    public void testGoodAuthorizeDBSecurityGroupIngress() throws Exception {
        logger.info("Creating DBSec group name to authorize");
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("Group to authorize");
        request.withDBSecurityGroupName(name1);
        getRdsClient().createDBSecurityGroup(request);
        
        SecurityGroup mygroup = secGrpHelper.getEC2SecGroup();

        logger.info("Authorizing DBSecGropu with name = " + name1);
        final AuthorizeDBSecurityGroupIngressRequest authRequest = new AuthorizeDBSecurityGroupIngressRequest();
        authRequest.withDBSecurityGroupName(name1);
        authRequest.withEC2SecurityGroupId(mygroup.getGroupId());
        authRequest.withEC2SecurityGroupName(mygroup.getGroupName());
        authRequest.withEC2SecurityGroupOwnerId(mygroup.getOwnerId());
        getRdsClient().authorizeDBSecurityGroupIngress(authRequest);
        
        logger.info("Deleting DBSecGroup with name = " + name1);
        final DeleteDBSecurityGroupRequest delRequest = new DeleteDBSecurityGroupRequest();
        delRequest.withDBSecurityGroupName(name1);
        getRdsClient().deleteDBSecurityGroup(delRequest);
    }
   
}
