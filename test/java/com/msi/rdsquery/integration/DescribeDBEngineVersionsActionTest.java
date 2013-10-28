package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeDBEngineVersionsRequest;
import com.msi.tough.core.Appctx;

public class DescribeDBEngineVersionsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeDBEngineVersionsActionTest.class.getName());

    String parFamily = "MySQL5.5";

    @Test
    public void testDescribeDBEngineVersionsNoArgs() throws Exception {
        final DescribeDBEngineVersionsRequest request = new DescribeDBEngineVersionsRequest();
        getRdsClient().describeDBEngineVersions(request);
    }
    

    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBEngineVersionsInvalidParameters() throws Exception {
        final DescribeDBEngineVersionsRequest request = new DescribeDBEngineVersionsRequest();
        request.withDBParameterGroupFamily("DoesNotExist");
        getRdsClient().describeDBEngineVersions(request);
    }


    @Test
    public void testGoodDescribeDBEngineVersions() throws Exception {
        logger.info("Describing DBEngine Versions");
        final DescribeDBEngineVersionsRequest request = new DescribeDBEngineVersionsRequest();
        request.withDBParameterGroupFamily(parFamily);
        request.withDefaultOnly(true);
        request.withMaxRecords(50);
        getRdsClient().describeDBEngineVersions(request);
    }
}
