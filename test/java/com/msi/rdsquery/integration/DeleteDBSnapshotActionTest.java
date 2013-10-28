package com.msi.rdsquery.integration;

import java.util.UUID;

import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DeleteDBSnapshotRequest;
import com.msi.rdsquery.helper.DBSnapshotHelper;
import com.msi.tough.core.Appctx;

public class DeleteDBSnapshotActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DeleteDBSnapshotActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);
    
    @Autowired
    private DBSnapshotHelper dbSnapshotHelper;

    String name1 = "rds-delSnap-1-Inst-" + baseName;
    String name2 = "rds-delSnap-2-Snap-" + baseName;



    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBSnapshotNoArgs() throws Exception {
        final DeleteDBSnapshotRequest request = new DeleteDBSnapshotRequest();
        getRdsClient().deleteDBSnapshot(request);
    }



    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBSnapshotInvalidParameters() throws Exception {
        final DeleteDBSnapshotRequest request = new DeleteDBSnapshotRequest();
        request.withDBSnapshotIdentifier(name2);
        getRdsClient().deleteDBSnapshot(request);
    }


    @Test
    public void testGoodDeleteDBSnapshot() throws Exception {

    	logger.info("Creating DBSnapshot: " + name1);
    	name1 = dbSnapshotHelper.getOrCreateDBSnapshot(name1);
    	logger.info("Waiting for DBSnapshot");
    	boolean snapRes = dbSnapshotHelper.waitForDBSnapshot(name1);
    	logger.info("Snapshot successfully created: " + snapRes);
        
        logger.info("Deleting DBSnapshot " + name1);
        final DeleteDBSnapshotRequest delSnapReq = new DeleteDBSnapshotRequest();
        delSnapReq.withDBSnapshotIdentifier(name1);
        getRdsClient().deleteDBSnapshot(delSnapReq);
    }

}

