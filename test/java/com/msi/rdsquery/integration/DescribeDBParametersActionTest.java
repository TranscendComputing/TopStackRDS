package com.msi.rdsquery.integration;

import java.util.UUID;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DescribeDBParametersRequest;
import com.msi.tough.core.Appctx;

public class DescribeDBParametersActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeDBParametersActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-descPars-1-" + baseName;
    String parFamily = "MySQL5.5";

    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBParametersNoArgs() throws Exception {
        final DescribeDBParametersRequest request = new DescribeDBParametersRequest();
        getRdsClient().describeDBParameters(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBParametersInvalidParameters() throws Exception {
        final DescribeDBParametersRequest request = new DescribeDBParametersRequest();
        request.withDBParameterGroupName(name1);
        getRdsClient().describeDBParameters(request);
    }


    @Test
    public void testGoodDescribeDBParameters() throws Exception {
        logger.info("Creating DBParamGroup ");
        final CreateDBParameterGroupRequest request = new CreateDBParameterGroupRequest();
        request.setDBParameterGroupFamily(parFamily);
        request.setDBParameterGroupName(name1);
        request.setDescription("This param group was created as a test");
        getRdsClient().createDBParameterGroup(request);
        
        logger.info("Describing db params for " + name1);
        final DescribeDBParametersRequest descRequest = new DescribeDBParametersRequest();
        descRequest.withDBParameterGroupName(name1);
        descRequest.withMaxRecords(20);
        descRequest.withSource("system");
        getRdsClient().describeDBParameters(descRequest);
        
        logger.info("Deleting DBParam Group " + name1);
        final DeleteDBParameterGroupRequest delRequest = new DeleteDBParameterGroupRequest();
        delRequest.withDBParameterGroupName(name1);
        getRdsClient().deleteDBParameterGroup(delRequest);
    }


}

