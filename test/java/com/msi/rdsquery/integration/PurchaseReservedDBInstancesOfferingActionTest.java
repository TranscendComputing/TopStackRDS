package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.PurchaseReservedDBInstancesOfferingRequest;
import com.msi.tough.core.Appctx;

public class PurchaseReservedDBInstancesOfferingActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(PurchaseReservedDBInstancesOfferingActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testPurchaseReservedDBInstancesOfferingInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action PurchaseReservedDBInstances");
    	final PurchaseReservedDBInstancesOfferingRequest request = new PurchaseReservedDBInstancesOfferingRequest();
        getRdsClient().purchaseReservedDBInstancesOffering(request);
    }
}

