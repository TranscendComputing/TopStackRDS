package com.msi.rdsquery.integration;

import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.DeleteDBSubnetGroupRequest;
import com.msi.tough.core.Appctx;

public class DeleteDBSubnetGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(DeleteDBSubnetGroupActionTest.class.getName());

    @Test (expected = AmazonServiceException.class)
    public void testDeleteDBSubnetGroupNoArgs() throws Exception {
    	logger.debug("Testing unsuported action DeleteDBSubnetGroup");
        final DeleteDBSubnetGroupRequest request = new DeleteDBSubnetGroupRequest();
        getRdsClient().deleteDBSubnetGroup(request);
    }
}

