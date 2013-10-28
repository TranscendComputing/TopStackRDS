package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeEventsRequest;
import com.msi.tough.core.Appctx;

public class DescribeEventsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeEventsActionTest.class.getName());

    @Test
    public void testDescribeEventsNoArgs() throws Exception {
        final DescribeEventsRequest request = new DescribeEventsRequest();
        getRdsClient().describeEvents(request);
    }

    //If source id is provided, source type needs to be provided as well
    @Test (expected = AmazonServiceException.class)
    public void testDescribeEventsInvalidParameters() throws Exception {
        final DescribeEventsRequest request = new DescribeEventsRequest();
        request.withSourceIdentifier("DNE");
        getRdsClient().describeEvents(request);
    }

    @Test
    public void testGoodDescribeEvents() throws Exception {
        logger.info("Describing Events ");
        final DescribeEventsRequest request = new DescribeEventsRequest();
        request.withDuration(50);
        request.withMaxRecords(50);
        request.withSourceType("db-parameter-group");
        getRdsClient().describeEvents(request);
    }
}
