package com.msi.rdsquery.integration;

import java.util.UUID;

import com.amazonaws.AmazonServiceException;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.msi.rdsquery.helper.RunningDBInstanceHelper;
import com.msi.tough.core.Appctx;

public class CreateDBInstanceActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(CreateDBInstanceActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    @Autowired
    private RunningDBInstanceHelper dbInstHelper;

    String name1 = "rds-createDBInst-1-" + baseName;

    @Test (expected = AmazonServiceException.class)
    public void testCreateDBInstanceNoArgs() throws Exception {
        final CreateDBInstanceRequest request = new CreateDBInstanceRequest();
        getRdsClient().createDBInstance(request);
    }


    //AllocatedStorage must be > 5 for MySQL (not checked)
    //DBInstanceId must not equal ""
    @Test (expected = AmazonServiceException.class)
    public void testCreateDBInstanceInvalidParameters() throws Exception {
        final CreateDBInstanceRequest request = new CreateDBInstanceRequest();
        request.setAllocatedStorage(4);
        request.setDBInstanceClass("db.m1.medium");
        request.setDBInstanceIdentifier("");
        request.setEngine("MySQL");
        request.setMasterUsername("userName");
        request.setMasterUserPassword("myPassword");
        getRdsClient().createDBInstance(request);
    }

    //DBInstance reused through testing
    @Test
    public void testGoodCreateDBInstance() throws Exception {
    	logger.info("Creating dbInst: " + name1);
    	dbInstHelper.createDBInstance(name1);
    	logger.info("Waiting for DBInst");
    	boolean res = dbInstHelper.waitForDBInst(name1);
    	logger.info("Instance started successfully: " + res);
    }
}

