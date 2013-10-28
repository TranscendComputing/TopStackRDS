package com.msi.rdsquery.integration;

import java.util.UUID;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.msi.rdsquery.helper.DBSnapshotHelper;
import com.msi.tough.core.Appctx;

public class DescribeDBSnapshotsActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DescribeDBSnapshotsActionTest.class.getName());

    private final String baseName = UUID.randomUUID().toString()
            .substring(0, 8);

    @Autowired
    private DBSnapshotHelper dbSnapshotHelper;
    
    String name1 = "rds-autho-1-" + baseName;
    String name2 = "rds-autho-2-" + baseName;


    @Test
    public void testDescribeDBSnapshotsNoArgs() throws Exception {
        logger.info("Describing all DBSnapshots");
        final DescribeDBSnapshotsRequest request = new DescribeDBSnapshotsRequest();
        DescribeDBSnapshotsResult result = getRdsClient().describeDBSnapshots(request);
        logger.debug("Resulting DB snapshots" + result);
    }


    @Test (expected = AmazonServiceException.class)
    public void testDescribeDBSnapshotsInvalidParameters() throws Exception {
        final DescribeDBSnapshotsRequest request = new DescribeDBSnapshotsRequest();
        request.withDBSnapshotIdentifier(name2);
        getRdsClient().describeDBSnapshots(request);
    }

    @Test
    public void testGoodDescribeDBSnapshots() throws Exception {
    	logger.info("Creating DBSnapshot: " + name1);
    	name1 = dbSnapshotHelper.getOrCreateDBSnapshot(name1);
    	logger.info("Waiting for DBSnapshot");
    	boolean snapRes = dbSnapshotHelper.waitForDBSnapshot(name1);
    	logger.info("Snapshot successfully created: " + snapRes);
        
        logger.info("Describing DBSnapshot " + name1);
        final DescribeDBSnapshotsRequest descRequest = new DescribeDBSnapshotsRequest();
        descRequest.withDBSnapshotIdentifier(name1);
        getRdsClient().describeDBSnapshots(descRequest);  
    }
}

