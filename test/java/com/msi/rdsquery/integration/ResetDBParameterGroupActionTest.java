package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.amazonaws.services.rds.model.ResetDBParameterGroupRequest;
import com.msi.tough.core.Appctx;

public class ResetDBParameterGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(ResetDBParameterGroupActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-resetDBParGp-1-" + baseName;
    String parFamily = "MySQL5.5";

    @Test (expected = AmazonServiceException.class)
    public void testResetDBParameterGroupNoArgs() throws Exception {
        final ResetDBParameterGroupRequest request = new ResetDBParameterGroupRequest();
        getRdsClient().resetDBParameterGroup(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testResetDBParameterGroupInvalidParameters() throws Exception {
        final ResetDBParameterGroupRequest request = new ResetDBParameterGroupRequest();
        request.withDBParameterGroupName("");
        getRdsClient().resetDBParameterGroup(request);
    }


    @Test
    public void testGoodResetDBParameterGroup() throws Exception {
        logger.info("Creating DBParamGroup ");
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        request.setDBParameterGroupFamily(parFamily);
        request.setDBParameterGroupName(name1);
        request.setDescription("This param group was created as a test");
        getRdsClient().createDBParameterGroup(request);
        
        logger.info("Reseting DBParamGroup: " + name1);
        final ResetDBParameterGroupRequest resRequest = new ResetDBParameterGroupRequest();
        resRequest.withDBParameterGroupName(name1);
        resRequest.withResetAllParameters(true);
        getRdsClient().resetDBParameterGroup(resRequest);
        
        logger.info("Deleting DBParam Group " + name1);
        final DeleteDBParameterGroupRequest delRequest = new DeleteDBParameterGroupRequest();
        delRequest.withDBParameterGroupName(name1);
        getRdsClient().deleteDBParameterGroup(delRequest);
    }
}

