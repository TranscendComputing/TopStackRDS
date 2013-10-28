package com.msi.rdsquery.integration;

import java.util.UUID;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.msi.tough.core.Appctx;

public class DeleteDBParameterGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DeleteDBParameterGroupActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-delParGp-1-" + baseName;
    String parFamily = "MySQL5.5";


    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBParameterGroupNoArgs() throws Exception {
        final DeleteDBParameterGroupRequest request = new DeleteDBParameterGroupRequest();
        getRdsClient().deleteDBParameterGroup(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBParameterGroupInvalidParameters() throws Exception {
        final DeleteDBParameterGroupRequest request = new DeleteDBParameterGroupRequest();
        request.withDBParameterGroupName("invalidName");
        getRdsClient().deleteDBParameterGroup(request);
    }


    @Test
    public void testGoodDeleteDBParameterGroup() throws Exception {
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

