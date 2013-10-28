package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.msi.tough.core.Appctx;

public class CreateDBParameterGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(CreateDBParameterGroupActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-crDbParGp-1-" + baseName;
    String parFamily = "MySQL5.5";

    
    @Test (expected = AmazonServiceException.class)
    public void testCreateDBParameterGroupNoArgs() throws Exception {
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        getRdsClient().createDBParameterGroup(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testCreateDBParameterGroupInvalidParameters() throws Exception {
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        request.setDBParameterGroupFamily(parFamily);
        request.setDBParameterGroupName("");
        request.setDescription("This param group should not be created");
        getRdsClient().createDBParameterGroup(request);
    }

    @Test
    public void testGoodCreateDBParameterGroup() throws Exception {
        logger.info("Creating DBParamGroup ");
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        request.setDBParameterGroupFamily(parFamily);
        request.setDBParameterGroupName(name1);
        request.setDescription("This param group was created as a test");
        getRdsClient().createDBParameterGroup(request);
        
        logger.info("Deleting DBParam Group " + name1);
        final DeleteDBParameterGroupRequest delRequest = new DeleteDBParameterGroupRequest();
        delRequest.withDBParameterGroupName(name1);
        getRdsClient().deleteDBParameterGroup(delRequest);
    }
}
