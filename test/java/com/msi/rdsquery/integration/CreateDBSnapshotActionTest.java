package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBSnapshotRequest;
import com.msi.rdsquery.helper.DBSnapshotHelper;
import com.msi.tough.core.Appctx;

public class CreateDBSnapshotActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(CreateDBSnapshotActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
        
    @Autowired
    private DBSnapshotHelper dbSnapshotHelper;
    
    String name1 = "rds-crDBSnap-" + baseName;

    @Test (expected = AmazonServiceException.class)
    public void testCreateDBSnapshotNoArgs() throws Exception {
        final CreateDBSnapshotRequest request = new CreateDBSnapshotRequest();
        getRdsClient().createDBSnapshot(request);
    }

    @Test (expected = AmazonServiceException.class)
    public void testCreateDBSnapshotInvalidParameters() throws Exception {
        final CreateDBSnapshotRequest request = new CreateDBSnapshotRequest();
        request.withDBInstanceIdentifier("");
        getRdsClient().createDBSnapshot(request);
    }

    @Test
    public void testGoodCreateDBSnapshot() throws Exception {
    	logger.info("Creating DBSnapshot: " + name1);
    	name1 = dbSnapshotHelper.getOrCreateDBSnapshot(name1);
    	logger.info("Waiting for DBSnapshot");
    	boolean snapRes = dbSnapshotHelper.waitForDBSnapshot(name1);
    	logger.info("Snapshot successfully created: " + snapRes);
          
    }
}

