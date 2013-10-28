package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.msi.tough.core.Appctx;

public class CreateDBSecurityGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(CreateDBSecurityGroupActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    String name1 = "rds-creDbSecGp-4-" + baseName;
    
    //DBSecurityName is required
    @Test (expected = AmazonServiceException.class)
    public void testCreateDBSecurityGroupNoArgs() throws Exception {
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        getRdsClient().createDBSecurityGroup(request);
    }

    //DBSecGroupName may not be set to "Default"
    @Test (expected = AmazonServiceException.class)
    public void testCreateDBSecurityGroupInvalidParameters() throws Exception {
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("This is a test security group");
        request.withDBSecurityGroupName("Default");
        getRdsClient().createDBSecurityGroup(request);
    }
    
    //DBSecGroupName may not contain spaces
    //@Test (expected = AmazonServiceException.class)
    @Test
    public void testCreateDBSecurityGroupInvalidParameters2() throws Exception {
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("This is a bad security group");
        request.withDBSecurityGroupName(name1 + "-invalid Group");
        getRdsClient().createDBSecurityGroup(request);
    }


    @Test
    public void testGoodCreateDBSecurityGroup() throws Exception {
        logger.info("Creating good DBSecGroup");
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("TestSecGroup");
        request.withDBSecurityGroupName(name1);
        getRdsClient().createDBSecurityGroup(request);
        
        //Deleting secGroup
        logger.info("Deleting DBSecGroup with name = " + name1);
        final DeleteDBSecurityGroupRequest delRequest = new DeleteDBSecurityGroupRequest();
        delRequest.withDBSecurityGroupName(name1);
        getRdsClient().deleteDBSecurityGroup(delRequest);
    }


}

