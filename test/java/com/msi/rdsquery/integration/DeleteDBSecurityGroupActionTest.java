package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.msi.tough.core.Appctx;

public class DeleteDBSecurityGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DeleteDBSecurityGroupActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-delSecGp-3-" + baseName;

    //DBSecurityGroupName is required
    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBSecurityGroupNoArgs() throws Exception {
        final DeleteDBSecurityGroupRequest request = new DeleteDBSecurityGroupRequest();
        getRdsClient().deleteDBSecurityGroup(request);
    }



    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBSecurityGroupInvalidParameters() throws Exception {
        final DeleteDBSecurityGroupRequest request = new DeleteDBSecurityGroupRequest();
        request.withDBSecurityGroupName("No Group");
        getRdsClient().deleteDBSecurityGroup(request);
    }


    @Test
    public void testGoodDeleteDBSecurityGroup() throws Exception {
        logger.info("Creating good DBSecGroup");
        final CreateDBSecurityGroupRequest request = new CreateDBSecurityGroupRequest();
        request.withDBSecurityGroupDescription("TestSecGroup");
        request.withDBSecurityGroupName(name1);
        getRdsClient().createDBSecurityGroup(request);
        
        logger.info("Deleting DBSecGroup with name = " + name1);
        final DeleteDBSecurityGroupRequest delRequest = new DeleteDBSecurityGroupRequest();
        delRequest.withDBSecurityGroupName(name1);
        getRdsClient().deleteDBSecurityGroup(delRequest);
    }
}

