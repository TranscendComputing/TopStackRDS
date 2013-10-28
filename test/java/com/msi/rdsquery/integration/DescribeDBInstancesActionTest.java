package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.msi.rdsquery.helper.RunningDBInstanceHelper;
import com.msi.tough.core.Appctx;

public class DescribeDBInstancesActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeDBInstancesActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    
    @Autowired
    private RunningDBInstanceHelper dbInstHelper;
    
    String name1 = "rds-descDbInst-1-" + baseName;


    @Test
    public void testDescribeDBInstancesNoArgs() throws Exception {
    	logger.info("describing all instances");
        final DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
        getRdsClient().describeDBInstances(request);
    }


    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBInstancesInvalidParameters() throws Exception {
        final DescribeDBInstancesRequest request = new DescribeDBInstancesRequest();
        request.withDBInstanceIdentifier("DNE");
        getRdsClient().describeDBInstances(request);
    }

    @Test
    public void testGoodDescribeDBInstances() throws Exception {
    	logger.info("Creating dbInst: " + name1);
    	name1 = dbInstHelper.getOrCreateDBInstance(name1);
    	logger.info("Waiting for DBInst");
    	boolean res = dbInstHelper.waitForDBInst(name1);
    	logger.info("Instance started successfully: " + res);
        
        logger.info("Describing DBInstance " + name1);
        final DescribeDBInstancesRequest descRequest = new DescribeDBInstancesRequest();
        descRequest.withDBInstanceIdentifier(name1);
        getRdsClient().describeDBInstances(descRequest);

    }
}

