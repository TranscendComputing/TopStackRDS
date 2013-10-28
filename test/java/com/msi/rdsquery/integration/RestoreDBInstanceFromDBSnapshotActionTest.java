package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.RestoreDBInstanceFromDBSnapshotRequest;
import com.msi.rdsquery.helper.DBSnapshotHelper;
import com.msi.tough.core.Appctx;

public class RestoreDBInstanceFromDBSnapshotActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(RestoreDBInstanceFromDBSnapshotActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    @Autowired
    private DBSnapshotHelper dbSnapshotHelper;
    
    String name1 = "rds-autho-1-" + baseName;
    String name2 = "rds-autho-1-" + baseName;


    @Test (expected = AmazonServiceException.class)
    public void testRestoreDBInstanceFromDBSnapshotNoArgs() throws Exception {
        final RestoreDBInstanceFromDBSnapshotRequest request = new RestoreDBInstanceFromDBSnapshotRequest();
        getRdsClient().restoreDBInstanceFromDBSnapshot(request);
    }



    @Test (expected = AmazonServiceException.class)
    public void testRestoreDBInstanceFromDBSnapshotInvalidParameters() throws Exception {
        final RestoreDBInstanceFromDBSnapshotRequest request = new RestoreDBInstanceFromDBSnapshotRequest();
        request.withDBName("");
        request.withDBSnapshotIdentifier("");
        getRdsClient().restoreDBInstanceFromDBSnapshot(request);
    }

    @Test
    public void testGoodRestoreDBInstanceFromDBSnapshot() throws Exception {
    	logger.info("Creating DBSnapshot: " + name1);
    	name1 = dbSnapshotHelper.getOrCreateDBSnapshot(name1);
    	logger.info("Waiting for DBSnapshot");
    	boolean snapRes = dbSnapshotHelper.waitForDBSnapshot(name1);
    	logger.info("Snapshot successfully created: " + snapRes);
    	
        logger.info("Restorting DBInstanceSnapshot " + name1);
        final RestoreDBInstanceFromDBSnapshotRequest request = new RestoreDBInstanceFromDBSnapshotRequest();
        request.withDBInstanceIdentifier(name2);
        request.withDBSnapshotIdentifier(name1);
        getRdsClient().restoreDBInstanceFromDBSnapshot(request);
    }

}

