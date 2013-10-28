package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.RestoreDBInstanceToPointInTimeRequest;
import com.msi.tough.core.Appctx;

public class RestoreDBInstanceToPointInTimeActionTest extends AbstractBaseRdsTest {
    private static Logger logger = Appctx.getLogger
    		(PurchaseReservedDBInstancesOfferingActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testRestoreDBInstanceToPointInTimeInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action RestoreDBInstanceToPointInTime");
    	final RestoreDBInstanceToPointInTimeRequest request = new RestoreDBInstanceToPointInTimeRequest();
        getRdsClient().restoreDBInstanceToPointInTime(request);
    }

}

