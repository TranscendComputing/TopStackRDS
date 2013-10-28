package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBInstanceReadReplicaRequest;
import com.msi.rdsquery.helper.RunningDBInstanceHelper;
import com.msi.tough.core.Appctx;

public class CreateDBInstanceReadReplicaActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(CreateDBInstanceReadReplicaActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    @Autowired
    private RunningDBInstanceHelper dbInstHelper;
    
    String name1 = "rds-crReadRepSource-1-" + baseName;
    String name2 = "rds-crReadRep-1-" + baseName;

    @Test (expected = AmazonServiceException.class)
    public void testCreateDBInstanceReadReplicaNoArgs() throws Exception {
        final CreateDBInstanceReadReplicaRequest request = new CreateDBInstanceReadReplicaRequest();
        getRdsClient().createDBInstanceReadReplica(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testCreateDBInstanceReadReplicaInvalidParameters() throws Exception {
        final CreateDBInstanceReadReplicaRequest request = new CreateDBInstanceReadReplicaRequest();
        request.withSourceDBInstanceIdentifier("InvalidInstance");
        getRdsClient().createDBInstanceReadReplica(request);
    }

    @Test
    public void testGoodCreateDBInstanceReadReplica() throws Exception {
    	
    	logger.info("Creating dbInst: " + name1);
    	name1 = dbInstHelper.getOrCreateDBInstance(name1);
    	logger.info("Waiting for DBInst " + name1);
    	boolean res = dbInstHelper.waitForDBInst(name1);
    	logger.info("Instance started successfully: " + res);

        logger.info("Creating DBInstanceReadReplica from name1");
        final CreateDBInstanceReadReplicaRequest crReadRepRequest = new CreateDBInstanceReadReplicaRequest();
        crReadRepRequest.withDBInstanceIdentifier(name2);
        crReadRepRequest.withSourceDBInstanceIdentifier(name1);
        getRdsClient().createDBInstanceReadReplica(crReadRepRequest);
      	boolean replicaResp = dbInstHelper.waitForDBInst(name2, 350);
    	logger.info("Instance started successfully: " + replicaResp);

    }
}

