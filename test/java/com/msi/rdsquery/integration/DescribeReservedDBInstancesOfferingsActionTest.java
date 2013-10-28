package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeReservedDBInstancesOfferingsRequest;
import com.msi.tough.core.Appctx;

public class DescribeReservedDBInstancesOfferingsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeReservedDBInstancesOfferingsActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testDescribeReservedDBInstancesOfferingsInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action DescribeReservedDBInstancesOfferings");
    	final DescribeReservedDBInstancesOfferingsRequest request = new DescribeReservedDBInstancesOfferingsRequest();
        getRdsClient().describeReservedDBInstancesOfferings(request);
    }

}

