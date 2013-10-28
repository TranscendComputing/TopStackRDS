package com.msi.rdsquery.integration;


import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.msi.rdsquery.helper.DBSnapshotHelper;
import com.msi.rdsquery.helper.RunningDBInstanceHelper;
import com.msi.tough.core.Appctx;

public class ZTerminationTest extends AbstractBaseRdsTest {

    private static Logger logger = Appctx.getLogger
    		(ZTerminationTest.class.getName());

    @Autowired
    private RunningDBInstanceHelper dbInstHelper;
    @Autowired
    private DBSnapshotHelper dbSnapshotHelper;
    
    //Terminating running DBInstances
    @Test
    public void testGoodTerminateDBInstance() throws Exception {
    	logger.debug("Terminating DBInstances");
    	dbInstHelper.finalDestroy();
    }
    
    //Terminating running DBSnapshots
    @Test
    public void testGoodTerminateSnaphots() throws Exception {
    	logger.debug("Terminating DBSnapshots");
    	dbSnapshotHelper.finalDestroy();
    }
}

