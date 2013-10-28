package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeOrderableDBInstanceOptionsRequest;
import com.msi.tough.core.Appctx;

public class DescribeOrderableDBInstanceOptionsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeOrderableDBInstanceOptionsActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testDescribeOrderableDBInstanceOptionsInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action DescribeOrderableDBInstance");
        final DescribeOrderableDBInstanceOptionsRequest request = new DescribeOrderableDBInstanceOptionsRequest();
        getRdsClient().describeOrderableDBInstanceOptions(request);
    }


}

