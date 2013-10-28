package com.msi.rdsquery.longrunning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import junit.framework.Assert;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.elasticache.model.PendingModifiedValues;
import com.amazonaws.services.rds.model.AuthorizeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DBParameterGroup;
import com.amazonaws.services.rds.model.DBParameterGroupNotFoundException;
import com.amazonaws.services.rds.model.DBSecurityGroup;
import com.amazonaws.services.rds.model.DBSecurityGroupNotFoundException;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.DescribeDBParameterGroupsRequest;
import com.amazonaws.services.rds.model.DescribeDBParameterGroupsResult;
import com.amazonaws.services.rds.model.DescribeDBParametersRequest;
import com.amazonaws.services.rds.model.DescribeDBParametersResult;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsRequest;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsResult;
import com.amazonaws.services.rds.model.ModifyDBParameterGroupRequest;
import com.amazonaws.services.rds.model.ModifyDBParameterGroupResult;
import com.amazonaws.services.rds.model.Parameter;
import com.amazonaws.services.rds.model.ResetDBParameterGroupRequest;
import com.amazonaws.services.rds.model.RevokeDBSecurityGroupIngressRequest;
import com.msi.rdsquery.integration.AbstractBaseRdsTest;
import com.msi.tough.core.Appctx;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.rds.RDSUtilities;

public class CreateAndDeleteInstanceTest  extends AbstractBaseRdsTest {

	private static Logger logger = Appctx.getLogger(CreateAndDeleteInstanceTest.class.getName());

	private static String suffix = UUID.randomUUID().toString().substring(0, 10);
	private String dbSecGrpName = "rds-sg-longrunning" + suffix;
	private String dbParamGrpName = "rds-pg-longrunning" + suffix;
	private String dbInstanceName = "rds-db-longrunning" + suffix;
	private String cidrip = "0.0.0.0/0";

	public void createDBSecurityGroup(){
		logger.info("Creating a new DBSecurityGroup:" + dbSecGrpName);
		CreateDBSecurityGroupRequest req = new CreateDBSecurityGroupRequest();
		req.setDBSecurityGroupDescription("This DBSecurityGroup is created for testing purpose at " + (new Date()).toString());
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DBSecurityGroup result = this.getRdsClient().createDBSecurityGroup(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getIPRanges().size(), 0);
		assertEquals(result.getEC2SecurityGroups().size(), 0);
		assertNotSame(result.getDBSecurityGroupDescription(), null);
		logger.info("CreateDBSecurityGroupResult: " + result.toString());
		authorizeDBSecurityGroupIngress();
	}

	public void authorizeDBSecurityGroupIngress(){
		logger.info("Authorizing a DBSecurityGroupIngress.");
		AuthorizeDBSecurityGroupIngressRequest req = new AuthorizeDBSecurityGroupIngressRequest();
		req.setCIDRIP(this.cidrip);
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DBSecurityGroup result = this.getRdsClient().authorizeDBSecurityGroupIngress(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getEC2SecurityGroups().size(), 0);
		assertEquals(result.getIPRanges().get(0).getCIDRIP(), this.cidrip);
		assertNotSame(result.getDBSecurityGroupDescription(), null);
		logger.info("AuthorizeDBSecurityGroupIngressResult: " + result.toString());
	}

	public void createDBParameterGroup(){
		logger.info("Creating a DBParameterGroup:" + dbParamGrpName);
		CreateDBParameterGroupRequest req = new CreateDBParameterGroupRequest();
		req.setDBParameterGroupName(dbParamGrpName);
		req.setDBParameterGroupFamily("mysql5.5");
		req.setDescription("This DBParameterGroup is created for testing purpose at " + (new Date()).toString());
		DBParameterGroup result = this.getRdsClient().createDBParameterGroup(req);
		assertEquals(result.getDBParameterGroupFamily().toLowerCase(), "mysql5.5");
		assertEquals(result.getDBParameterGroupName(), this.dbParamGrpName);
		assertNotSame(result.getDescription(), null);
		logger.info("CreateDBParameterGroupResult: " + result.toString());
	}

	@Autowired
	BasicDataSource mysql_check = null;

	@Test
	public void testCreateDBInstance() throws Exception {

        createDBSecurityGroup();
	    createDBParameterGroup();
		logger.info("Creating a new DBInstance.");
		CreateDBInstanceRequest req = new CreateDBInstanceRequest();
		req.setAllocatedStorage(7);
		req.setAvailabilityZone(super.getDefaultAvailabilityZone());
		req.setDBInstanceClass("db.m1.large");
		req.setDBInstanceIdentifier(this.dbInstanceName);
		req.setDBName("msi");
		req.setEngine("MySQL");
		req.setMasterUsername("msi");
		req.setMasterUserPassword("msi");
		//req.setPort(3306);
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<String> dBSecurityGroups = new ArrayList<String>();
		dBSecurityGroups.add(this.dbSecGrpName);
		req.setDBSecurityGroups(dBSecurityGroups);
		DBInstance result = this.getRdsClient().createDBInstance(req);
		assertEquals(result.getAllocatedStorage(), new Integer(7));
		assertEquals(result.getAutoMinorVersionUpgrade(), true);
		assertEquals(super.getDefaultAvailabilityZone(), result.getAvailabilityZone());
		assertNotSame(result.getBackupRetentionPeriod(), null);
		assertEquals(result.getDBInstanceClass(), req.getDBInstanceClass());
		assertEquals(result.getDBInstanceIdentifier(), this.dbInstanceName);
		assertEquals(result.getDBInstanceStatus(), "creating");
		assertEquals(result.getDBName(), "msi");
		assertEquals(result.getDBParameterGroups().get(0).getDBParameterGroupName(), this.dbParamGrpName);
		assertEquals(result.getDBParameterGroups().get(0).getParameterApplyStatus(), "in-sync");
		assertEquals(result.getDBSecurityGroups().get(0).getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getDBSecurityGroups().get(0).getStatus(), "active");
		assertEquals(result.getEndpoint(), null);
		assertEquals(result.getEngine().toLowerCase(), "mysql");
		assertEquals(result.getEngineVersion(), "5.5.20");
		assertNotSame(result.getInstanceCreateTime(), null);
		assertNotSame(result.getLatestRestorableTime(), null);
		assertEquals(result.getLicenseModel(), "general-public-license");
		assertEquals(result.getMasterUsername(), "msi");
		assertEquals(result.getMultiAZ(), true);
		assertEquals(result.getPendingModifiedValues().getAllocatedStorage(), null);
		assertEquals(result.getPendingModifiedValues().getBackupRetentionPeriod(), null);
		assertEquals(result.getPendingModifiedValues().getDBInstanceClass(), null);
		assertEquals(result.getPendingModifiedValues().getEngineVersion(), null);
		assertEquals(result.getPendingModifiedValues().getMasterUserPassword(), null);
		assertEquals(result.getPendingModifiedValues().getMultiAZ(), null);
		assertEquals(result.getPendingModifiedValues().getPort(), null);
		assertNotSame(result.getPreferredBackupWindow(), null);
		assertNotSame(result.getPreferredMaintenanceWindow(), null);
		assertEquals(result.getReadReplicaDBInstanceIdentifiers().size(), 0);
		assertEquals(result.getReadReplicaSourceDBInstanceIdentifier(), null);
		logger.info("CreateDBInstanceResult: " + result.toString());
		waitForDBInstance();
		testDBConnectivity();

	}

	public void testDBConnectivity(){
		try{
			logger.debug("Attempting MySQL connection on instance.");
			mysql_check.getConnection();
		}
		catch(SQLException e){
			logger.error("MySQL connection could not be established. Test failed!");
			Assert.fail();
		}
		logger.debug("MySQL connection established successfully.");
	}
	public void waitForDBInstance(){
		try {
			logger.info("Waiting for the DBInstance to spin up. Sleeping for 3 minutes...");
			Thread.sleep(180000);

			int counter = 1;
			int lim = 30;
			long waitMilis = 30000;
			boolean creating = true;
			boolean timeout = false;
			logger.info("Describing the DBInstance to check its status.");
			DescribeDBInstancesRequest req = new DescribeDBInstancesRequest();
			req.setDBInstanceIdentifier(this.dbInstanceName);
			logger.info("DescribeDBInstancesRequest: " + req.toString());
			while(creating){
				DescribeDBInstancesResult result = this.getRdsClient().describeDBInstances(req);
				DBInstance dbinstance = result.getDBInstances().get(0);
				logger.info(counter + "th call to DescribeDBInstances: " + dbinstance.toString());
				if(dbinstance.getDBInstanceStatus().equals("available")){
					creating = false;
					mysql_check.setUrl("jdbc:mysql://" + dbinstance.getEndpoint().getAddress() + "/msi?autoReconnect=true");
					break;
				}
				Thread.sleep(waitMilis);
				if(counter < lim){
					++counter;
				}
				else{
					creating = false;
					timeout = true;
				}
			}
			if(timeout){
				logger.debug("CreateDBInstance: sent; DBInstance was not provisioned within limited time. Test failed!");
				fail("CreateDBInstance sent, but DBInstance was not provisioned within limited time. Test failed!");
			}
		} catch (InterruptedException e) {
			logger.debug("InterruptedException was caught unexpectedly. Test failed!");
		}
	}

	public void deleteDBInstance(){
		logger.info("Deleting the 1st DBInstance (source DBInstance).");
		DeleteDBInstanceRequest req = new DeleteDBInstanceRequest();
		req.setDBInstanceIdentifier(this.dbInstanceName);
		req.setSkipFinalSnapshot(true);
		DBInstance result = this.getRdsClient().deleteDBInstance(req);
		logger.info("DeleteDBInstanceResult: " + result.toString());
	}


	public void waitTillDBInstanceIsDeleted() throws InterruptedException {
			logger.info("Waiting for the DBInstance to be deleted.");
			int counter = 1;
			int lim = 30;
			long waitMilis = 15000;
			boolean deleting = true;
			boolean timeout = false;
			logger.info("Describing the DBInstance to check its status.");
			DescribeDBInstancesRequest req = new DescribeDBInstancesRequest();
			req.setDBInstanceIdentifier(this.dbInstanceName);
			logger.info("DescribeDBInstancesRequest: " + req.toString());
			while(deleting){
				DescribeDBInstancesResult result = this.getRdsClient().describeDBInstances(req);
				logger.info(counter + "th call to DescribeDBInstances: " + result.toString());
				if(result.getDBInstances().size() == 0){
					deleting = false;
				}
				Thread.sleep(waitMilis);
				if(counter < lim){
					++counter;
				}
				else{
					deleting = false;
					timeout = true;
				}
			}
			if(timeout){
				logger.debug("DeleteDBInstance: sent; DBInstance was not cleaned up within limited time. Test failed!");
				fail();
			}
	}

	public void deleteDBSecurityGroup(){
		logger.info("Deleting the DBSecurityGroup.");
		DeleteDBSecurityGroupRequest req = new DeleteDBSecurityGroupRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		this.getRdsClient().deleteDBSecurityGroup(req);
	}

	public void deleteDBParameterGroup(){
		logger.info("Deleting the DBParameterGroup.");
		DeleteDBParameterGroupRequest req = new DeleteDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		this.getRdsClient().deleteDBParameterGroup(req);
	}

	@After
	public void tearDown()
	{
		try{
			deleteDBInstance();
			waitTillDBInstanceIsDeleted();
		} catch (Exception e){
			// ignored
		}
	    try
	    {
	        deleteDBSecurityGroup();
	    } catch (Exception e) {
	        // ignored
	    }
        try
        {
            deleteDBParameterGroup();
        } catch (Exception e) {
            // ignored
        }
	}
}
