package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.ModifyDBSubnetGroupRequest;
import com.msi.tough.core.Appctx;

public class ModifyDBSubnetGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(ModifyDBSubnetGroupActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testModifyDBSubnetGroupInvalidParameters() throws Exception {
    	logger.debug("Testing unsuported action ModifyDBSubnetGroup");
        final ModifyDBSubnetGroupRequest request = new ModifyDBSubnetGroupRequest();
        getRdsClient().modifyDBSubnetGroup(request);
    }

}

