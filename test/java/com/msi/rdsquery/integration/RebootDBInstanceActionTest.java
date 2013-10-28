package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.RebootDBInstanceRequest;
import com.msi.rdsquery.helper.RunningDBInstanceHelper;
import com.msi.tough.core.Appctx;

public class RebootDBInstanceActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(RebootDBInstanceActionTest.class.getName());
    
    @Autowired
    private RunningDBInstanceHelper dbInstHelper;

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    String name1 = "rds-rebtInst-1-" + baseName;

    @Test (expected = AmazonServiceException.class)
    public void testRebootDBInstanceNoArgs() throws Exception {
        final RebootDBInstanceRequest request = new RebootDBInstanceRequest();
        getRdsClient().rebootDBInstance(request);
    }



    @Test (expected = AmazonServiceException.class)
    public void testRebootDBInstanceInvalidParameters() throws Exception {
        final RebootDBInstanceRequest request = new RebootDBInstanceRequest();
        request.withDBInstanceIdentifier("");
        getRdsClient().rebootDBInstance(request);
    }


    //@Test
    public void testGoodRebootDBInstance() throws Exception {
    	
    	logger.info("Creating dbInst: " + name1);
    	name1 = dbInstHelper.getOrCreateDBInstance(name1);
    	logger.info("Waiting for DBInst");
    	boolean res = dbInstHelper.waitForDBInst(name1);
    	logger.info("Instance started successfully: " + res);
        
        logger.info("Reboot DBInstance " + name1);
        final RebootDBInstanceRequest rebRequest = new RebootDBInstanceRequest();
        rebRequest.withDBInstanceIdentifier(name1);
        getRdsClient().rebootDBInstance(rebRequest);

    }
    	
}

