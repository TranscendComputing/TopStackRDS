package com.msi.rdsquery.integration;

import java.util.UUID;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DescribeDBParameterGroupsRequest;
import com.msi.tough.core.Appctx;

public class DescribeDBParameterGroupsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeDBParameterGroupsActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-descDbParsGp-1-" + baseName;
    String parFamily = "MySQL5.5";

    //Describes all param groups
    @Test
    public void testDescribeDBParameterGroupsNoArgs() throws Exception {
        final DescribeDBParameterGroupsRequest request = new DescribeDBParameterGroupsRequest();
        getRdsClient().describeDBParameterGroups(request);
    }


    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBParameterGroupsInvalidParameters() throws Exception {
        final DescribeDBParameterGroupsRequest request = new DescribeDBParameterGroupsRequest();
        request.withDBParameterGroupName("DoesNotExist");
        getRdsClient().describeDBParameterGroups(request);
    }


    @Test
    public void testGoodDescribeDBParameterGroups() throws Exception {
        logger.info("Creating DBParamGroup ");
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        request.setDBParameterGroupFamily(parFamily);
        request.setDBParameterGroupName(name1);
        request.setDescription("This param group was created as a test");
        getRdsClient().createDBParameterGroup(request);
        
        logger.info("Describing parameter group " + name1);
        final DescribeDBParameterGroupsRequest descRequest = new DescribeDBParameterGroupsRequest();
        descRequest.withDBParameterGroupName(name1);
        getRdsClient().describeDBParameterGroups(descRequest);
        
        logger.info("Deleting DBParam Group " + name1);
        final DeleteDBParameterGroupRequest delRequest = new DeleteDBParameterGroupRequest();
        delRequest.withDBParameterGroupName(name1);
        getRdsClient().deleteDBParameterGroup(delRequest);
    }


}

