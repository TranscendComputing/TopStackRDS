package com.msi.rdsquery.integration;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.rds.model.CreateDBSubnetGroupRequest;
import com.msi.tough.core.Appctx;

public class CreateDBSubnetGroupActionTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(CreateDBSubnetGroupActionTest.class.getName());


    @Test (expected = AmazonServiceException.class)
    public void testCreateDBSubnetGroupNoArgs() throws Exception {
    	logger.debug("Testing unsuported action CreateDBSubnetGroup");
        final CreateDBSubnetGroupRequest request = new CreateDBSubnetGroupRequest();
        getRdsClient().createDBSubnetGroup(request);
    }
}

