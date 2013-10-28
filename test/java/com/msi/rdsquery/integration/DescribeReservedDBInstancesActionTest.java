package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeReservedDBInstancesRequest;
import com.msi.tough.core.Appctx;

public class DescribeReservedDBInstancesActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeReservedDBInstancesActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testDescribeReservedDBInstancesInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action DescribeReservedDBInstances");
        final DescribeReservedDBInstancesRequest request = new DescribeReservedDBInstancesRequest();
        getRdsClient().describeReservedDBInstances(request);
    }

}

