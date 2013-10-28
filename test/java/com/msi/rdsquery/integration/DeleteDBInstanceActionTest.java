package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.msi.rdsquery.helper.RunningDBInstanceHelper;
import com.msi.tough.core.Appctx;

public class DeleteDBInstanceActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DeleteDBInstanceActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    @Autowired
    private RunningDBInstanceHelper dbInstHelper;
    
    String name1 = "rds-delDbInst-1-" + baseName;

    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBInstanceNoArgs() throws Exception {
        final DeleteDBInstanceRequest request = new DeleteDBInstanceRequest();
        getRdsClient().deleteDBInstance(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBInstanceInvalidParameters() throws Exception {
        final DeleteDBInstanceRequest request = new DeleteDBInstanceRequest();
        request.withDBInstanceIdentifier("DNE");
        request.withSkipFinalSnapshot(true);
        getRdsClient().deleteDBInstance(request);
    }

    @Test
    public void testGoodDeleteDBInstance() throws Exception {
    	
    	logger.info("Creating dbInst: " + name1);
    	name1 = dbInstHelper.getOrCreateDBInstance(name1);
    	logger.info("Waiting for DBInst");
    	boolean res = dbInstHelper.waitForDBInst(name1);
    	logger.info("Instance started successfully: " + res);
    	

        logger.info("Deleting DBInstance " + name1);
        final DeleteDBInstanceRequest delRequest = new DeleteDBInstanceRequest();
        delRequest.withDBInstanceIdentifier(name1);
        delRequest.withSkipFinalSnapshot(true);
        getRdsClient().deleteDBInstance(delRequest);
        
    }


}

