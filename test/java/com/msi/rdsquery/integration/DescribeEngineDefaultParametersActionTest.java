package com.msi.rdsquery.integration;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeEngineDefaultParametersRequest;
import com.msi.tough.core.Appctx;

public class DescribeEngineDefaultParametersActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeEngineDefaultParametersActionTest.class.getName());


    String parFamily = "MySQL5.5";

    //ParameterGroupFamily is required
    @Test (expected = AmazonServiceException.class)
    public void testDescribeEngineDefaultParametersNoArgs() throws Exception {
        final DescribeEngineDefaultParametersRequest request = new DescribeEngineDefaultParametersRequest();
        getRdsClient().describeEngineDefaultParameters(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testDescribeEngineDefaultParametersInvalidParameters() throws Exception {
        final DescribeEngineDefaultParametersRequest request = new DescribeEngineDefaultParametersRequest();
        request.withDBParameterGroupFamily("DoesNotExist");
        getRdsClient().describeEngineDefaultParameters(request);
    }


    @Test
    public void testGoodDescribeEngineDefaultParameters() throws Exception {
        logger.info("Descriing Engine default parameters ");
        final DescribeEngineDefaultParametersRequest request = new DescribeEngineDefaultParametersRequest();
        request.withDBParameterGroupFamily(parFamily);
        request.withMaxRecords(50);
        getRdsClient().describeEngineDefaultParameters(request);
    }


}

