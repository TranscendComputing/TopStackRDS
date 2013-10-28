package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.ModifyDBInstanceRequest;
import com.msi.tough.core.Appctx;

public class ModifyDBInstanceActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(ModifyDBInstanceActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testModifyDBInstanceInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action ModifyDBInstance");
        final ModifyDBInstanceRequest request = new ModifyDBInstanceRequest();
        getRdsClient().modifyDBInstance(request);
    }

}

