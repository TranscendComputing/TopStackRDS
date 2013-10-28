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
import com.amazonaws.services.rds.model.RevokeDBSecurityGroupIngressRequest;
import com.msi.rdsquery.helper.SecurityGroupsHelper;
import com.msi.tough.core.Appctx;

public class RevokeDBSecurityGroupIngressActionTest extends AbstractBaseRdsTest {
    @Autowired
    private SecurityGroupsHelper secGrpHelper;

    private static Logger logger = Appctx.getLogger
    		(RevokeDBSecurityGroupIngressActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-revokeDbSecGp-1-" + baseName;

    
    @Test (expected = AmazonServiceException.class)
    public void testRevokeDBSecurityGroupIngressNoArgs() throws Exception {
        final RevokeDBSecurityGroupIngressRequest request = new RevokeDBSecurityGroupIngressRequest();
        getRdsClient().revokeDBSecurityGroupIngress(request);
    }


    @Test (expected = AmazonServiceException.class)
    public void testRevokeDBSecurityGroupIngressInvalidParameters() throws Exception {
        final RevokeDBSecurityGroupIngressRequest request = new RevokeDBSecurityGroupIngressRequest();
        request.withDBSecurityGroupName("Invalid-name");
        getRdsClient().revokeDBSecurityGroupIngress(request);
    }


    @Test
    public void testGoodRevokeDBSecurityGroupIngress() throws Exception {
        SecurityGroup mygroup = secGrpHelper.getEC2SecGroup();
    	createDBGroup(name1, mygroup);

        logger.info("Revoking DBSecGroup " + name1);
        final RevokeDBSecurityGroupIngressRequest revRequest = new RevokeDBSecurityGroupIngressRequest();
        revRequest.withDBSecurityGroupName(name1);
        revRequest.withEC2SecurityGroupId(mygroup.getGroupId());
        revRequest.withEC2SecurityGroupName(mygroup.getGroupName());
        revRequest.withEC2SecurityGroupOwnerId(mygroup.getOwnerId());
        getRdsClient().revokeDBSecurityGroupIngress(revRequest);
        deleteDBGroup(name1);
    }
   
    private void createDBGroup(String name, SecurityGroup mygroup) {

        logger.info("Creating DBSec group name to authorize");
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("Group to authorize");
        request.withDBSecurityGroupName(name);
        getRdsClient().createDBSecurityGroup(request);
        
        logger.info("Authorizing DBSecGropu with name = " + name);
        final AuthorizeDBSecurityGroupIngressRequest authRequest = new AuthorizeDBSecurityGroupIngressRequest();
        authRequest.withDBSecurityGroupName(name);
        authRequest.withEC2SecurityGroupId(mygroup.getGroupId());
        authRequest.withEC2SecurityGroupName(mygroup.getGroupName());
        authRequest.withEC2SecurityGroupOwnerId(mygroup.getOwnerId());
        getRdsClient().authorizeDBSecurityGroupIngress(authRequest);
    	
    }
    
    private void deleteDBGroup(String name) {
        logger.info("Deleting DBSecGroup with name = " + name);
        final DeleteDBSecurityGroupRequest delRequest = new DeleteDBSecurityGroupRequest();
        delRequest.withDBSecurityGroupName(name);
        getRdsClient().deleteDBSecurityGroup(delRequest);
    }


}

