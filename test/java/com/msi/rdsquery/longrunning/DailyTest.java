package com.msi.rdsquery.longrunning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.junit.Test;
import org.slf4j.Logger;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.AmazonRDSClient;
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
import com.amazonaws.services.rds.model.RebootDBInstanceRequest;
import com.amazonaws.services.rds.model.ResetDBParameterGroupRequest;
import com.amazonaws.services.rds.model.ResetDBParameterGroupResult;
import com.amazonaws.services.rds.model.RevokeDBSecurityGroupIngressRequest;
import com.msi.rdsquery.integration.AbstractBaseRdsTest;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SecurityGroupEntity;

public class DailyTest  extends AbstractBaseRdsTest {

	private static Logger logger = Appctx.getLogger(DailyTest.class.getName());

	private String dbSecGrpName = "rds-test-security-group";
	private String dbParamGrpName = "rds-test-parameter-group";
	private String dbInstanceName = "rds-test-dbinstance";
	private String dbReplicaName = "rds-test-readreplica";
	private String dbRestoredName = "rds-test-restored-dbinstance";
	private String dbSnapshotName = "rds-test-snapshot";
	private String cidrip = "0.0.0.0/0";
	private String cidrip2 = "172.16.6.28/32";
	private String avZone = "nova";

	private AmazonRDSClient getFailTestClient(){
		AWSCredentials cred = new BasicAWSCredentials("nulluser", "nullpasswd");
		AmazonRDSClient fclient = new AmazonRDSClient(cred);
		fclient.setEndpoint("http://" + getTargetServer() + ":8080/RDSQuery/");
		return fclient;
	}

	@Test
	public void setupFailUser(){
		Session s = HibernateUtil.getSession();
		s.beginTransaction();
		AccountBean ab = new AccountBean();
		ab.setName("nulluser");
		ab.setAccessKey("nulluser");
		ab.setSecretKey("nullpasswd");
		ab.setDefSecurityGroups("default");
		ab.setDefZone("nova");
		s.save(ab);
		s.getTransaction().commit();
	}

	@Test
	public void failTestCreateDBSecurityGroup(){
		logger.info("Creating a new DBSecurityGroup.");
		CreateDBSecurityGroupRequest req = new CreateDBSecurityGroupRequest();
		req.setDBSecurityGroupDescription("This DBSecurityGroup is created for testing purpose at " + (new Date()).toString());
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DBSecurityGroup result = this.getFailTestClient().createDBSecurityGroup(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getIPRanges().size(), 0);
		assertEquals(result.getEC2SecurityGroups().size(), 0);
		assertNotSame(result.getDBSecurityGroupDescription(), null);
		logger.info("CreateDBSecurityGroupResult: " + result.toString());
	}

	@Test
	public void failTestCreateDBParameterGroup(){
		logger.info("Creating a DBParameterGroup.");
		CreateDBParameterGroupRequest req = new CreateDBParameterGroupRequest();
		req.setDBParameterGroupName(dbParamGrpName);
		req.setDBParameterGroupFamily("mysql5.5");
		req.setDescription("This DBParameterGroup is created for testing purpose at " + (new Date()).toString());
		DBParameterGroup result = this.getFailTestClient().createDBParameterGroup(req);
		assertEquals(result.getDBParameterGroupFamily().toLowerCase(), "mysql5.5");
		assertEquals(result.getDBParameterGroupName(), this.dbParamGrpName);
		assertNotSame(result.getDescription(), null);
		logger.info("CreateDBParameterGroupResult: " + result.toString());
	}

	@Test
	public void failTestCreateDBInstance(){
		logger.info("Creating a new DBInstance. This should fail and result in error status.");
		CreateDBInstanceRequest req = new CreateDBInstanceRequest();
		req.setAllocatedStorage(6);
		req.setAvailabilityZone(avZone);
		req.setDBInstanceClass("db.t1.micro");
		//req.setDBInstanceClass("db.m1.medium");
		req.setDBInstanceIdentifier(this.dbInstanceName);
		req.setEngine("MySQL");
		req.setMasterUsername("msi");
		req.setMasterUserPassword("msi");
		//req.setPort(3306);
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<String> dBSecurityGroups = new ArrayList<String>();
		dBSecurityGroups.add(this.dbSecGrpName);
		req.setDBSecurityGroups(dBSecurityGroups);
		DBInstance result = this.getFailTestClient().createDBInstance(req);
		assertEquals(result.getAllocatedStorage(), new Integer(6));
		assertEquals(result.getAutoMinorVersionUpgrade(), true);
		assertEquals(result.getAvailabilityZone(), avZone);
		assertNotSame(result.getBackupRetentionPeriod(), null);
		assertEquals(result.getDBInstanceClass(), "db.t1.micro");
		assertEquals(result.getDBInstanceIdentifier(), this.dbInstanceName);
		assertEquals(result.getDBInstanceStatus(), "creating");
		assertEquals(result.getDBName(), null);
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
	}

	@Test
	public void failTestDescribeDBInstance(){
		try {
			// give some time for the system to detect failure
			Thread.sleep(15000);
		} catch (InterruptedException e) {}

		DescribeDBInstancesRequest req = new DescribeDBInstancesRequest();
		req.setDBInstanceIdentifier(this.dbInstanceName);
		DescribeDBInstancesResult result = this.getFailTestClient().describeDBInstances(req);
		assertEquals("error", result.getDBInstances().get(0).getDBInstanceStatus());
	}

	@Test
	public void failTestDeleteDBInstance(){
		logger.info("Deleting the 1st DBInstance (source DBInstance).");
		DeleteDBInstanceRequest req = new DeleteDBInstanceRequest();
		req.setDBInstanceIdentifier(this.dbInstanceName);
		req.setSkipFinalSnapshot(true);
		DBInstance result = this.getFailTestClient().deleteDBInstance(req);
		logger.info("DeleteDBInstanceResult: " + result.toString());

		try {
			// give some time for the DBInstance to get cleaned up; it will remain in 'deleting' state for a few seconds
			Thread.sleep(10000);
		} catch (InterruptedException e) {}
	}

	@Test
	public void failTestDeleteDBParameterGroup(){
		logger.info("Deleting the DBParameterGroup.");
		DeleteDBParameterGroupRequest req = new DeleteDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		this.getFailTestClient().deleteDBParameterGroup(req);
		logger.info("There is no result response for this action. Continue testing.");
	}

	@Test
	public void failTestDeleteDBSecurityGroup(){
		logger.info("Deleting the DBSecurityGroup.");
		DeleteDBSecurityGroupRequest req = new DeleteDBSecurityGroupRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		this.getFailTestClient().deleteDBSecurityGroup(req);
		logger.info("There is no result response for this action. Continue testing.");
	}

	@Test
	public void cleanupFailUser(){
		Session s = HibernateUtil.getSession();
		s.beginTransaction();
		AccountBean ab = AccountUtil.readAccount(s, "nulluser");
		// delete the auto-generated default DBSecurityGroup
		RdsDbsecurityGroup sg = SecurityGroupEntity.getSecurityGroup(s, "default", ab.getId());
		List<RdsIPRangeBean> ipList = sg.getIPRange(s);
		for(RdsIPRangeBean ip : ipList){
			s.delete(ip);
		}
		s.delete(sg);
		s.delete(ab);
		s.getTransaction().commit();
	}


	// Now start testing RDSQuery
	@Test
	public void testCreateDBSecurityGroup(){
		logger.info("Creating a new DBSecurityGroup.");
		CreateDBSecurityGroupRequest req = new CreateDBSecurityGroupRequest();
		req.setDBSecurityGroupDescription("This DBSecurityGroup is created for testing purpose at " + (new Date()).toString());
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DBSecurityGroup result = this.getRdsClient().createDBSecurityGroup(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getIPRanges().size(), 0);
		assertEquals(result.getEC2SecurityGroups().size(), 0);
		assertNotSame(result.getDBSecurityGroupDescription(), null);
		logger.info("CreateDBSecurityGroupResult: " + result.toString());
	}

	@Test
	public void testDescribeDBSecurityGroups0(){
		logger.info("Describing the DBSecurityGroup.");
		DescribeDBSecurityGroupsRequest req = new DescribeDBSecurityGroupsRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DescribeDBSecurityGroupsResult result = this.getRdsClient().describeDBSecurityGroups(req);
		assertEquals(result.getDBSecurityGroups().get(0).getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getDBSecurityGroups().get(0).getIPRanges().size(), 0);
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().size(), 0);
		assertNotSame(result.getDBSecurityGroups().get(0).getDBSecurityGroupDescription(), null);
		logger.info("DescribeDBSecurityGroupsResult: " + result.toString());
	}

	@Test
	public void testAuthorizeDBSecurityGroupIngress(){
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

	@Test 
	public void testDescribeDBSecurityGroups1(){
		logger.info("Describing the DBSecurityGroup.");
		DescribeDBSecurityGroupsRequest req = new DescribeDBSecurityGroupsRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DescribeDBSecurityGroupsResult result = this.getRdsClient().describeDBSecurityGroups(req);
		assertEquals(result.getDBSecurityGroups().get(0).getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().size(), 0);
		assertEquals(result.getDBSecurityGroups().get(0).getIPRanges().get(0).getCIDRIP(), this.cidrip);
		assertNotSame(result.getDBSecurityGroups().get(0).getDBSecurityGroupDescription(), null);
		logger.info("DescribeDBSecurityGroupsResult: " + result.toString());
	}

	@Test
	public void testAuthorizeDBSecurityGroupIngress2(){
		logger.info("Authorizing a DBSecurityGroupIngress.");
		AuthorizeDBSecurityGroupIngressRequest req = new AuthorizeDBSecurityGroupIngressRequest();
		req.setEC2SecurityGroupOwnerId("1");
		req.setEC2SecurityGroupName("default");
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DBSecurityGroup result = this.getRdsClient().authorizeDBSecurityGroupIngress(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getEC2SecurityGroups().get(0).getEC2SecurityGroupName(), "default");
		assertEquals(result.getEC2SecurityGroups().get(0).getEC2SecurityGroupOwnerId(), "1");
		assertEquals(result.getIPRanges().get(0).getCIDRIP(), this.cidrip);
		assertNotSame(result.getDBSecurityGroupDescription(), null);
		logger.info("AuthorizeDBSecurityGroupIngressResult: " + result.toString());
	}

	@Test
	public void testDescribeDBSecurityGroups2(){
		logger.info("Describing the DBSecurityGroup.");
		DescribeDBSecurityGroupsRequest req = new DescribeDBSecurityGroupsRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DescribeDBSecurityGroupsResult result = this.getRdsClient().describeDBSecurityGroups(req);
		assertEquals(result.getDBSecurityGroups().get(0).getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().get(0).getEC2SecurityGroupName(), "default");
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().get(0).getEC2SecurityGroupOwnerId(), "1");
		assertEquals(result.getDBSecurityGroups().get(0).getIPRanges().get(0).getCIDRIP(), this.cidrip);
		assertNotSame(result.getDBSecurityGroups().get(0).getDBSecurityGroupDescription(), null);
		logger.info("DescribeDBSecurityGroupsResult: " + result.toString());
	}

	@Test
	public void testCreateDBParameterGroup(){
		logger.info("Creating a DBParameterGroup.");
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

	@Test
	public void testDescribeDBParameterGroups(){
		logger.info("Describing the DBParameterGroup.");
		DescribeDBParameterGroupsRequest req = new DescribeDBParameterGroupsRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		DescribeDBParameterGroupsResult result = this.getRdsClient().describeDBParameterGroups(req);
		assertEquals(result.getDBParameterGroups().get(0).getDBParameterGroupName(), this.dbParamGrpName);
		assertEquals(result.getDBParameterGroups().get(0).getDBParameterGroupFamily().toLowerCase(), "mysql5.5");
		assertEquals(result.getMarker(), null);
		logger.info("DescribeDBParameterGroupsResult: " + result.toString());
	}

	@Test
	public void testDescribeDBParameters0(){
		logger.info("Describing DB Parameters");
		DescribeDBParametersRequest req = new DescribeDBParametersRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		DescribeDBParametersResult result = this.getRdsClient().describeDBParameters(req);
		LinkedList<Parameter> fullList = new LinkedList<Parameter>();
		for(Parameter p : result.getParameters()){
			fullList.add(p);
		}
		logger.info(result.getParameters().size() + " items are returned.");
		logger.info("1st item: " + fullList.get(0).getParameterName());
		logger.info("Last item: " + fullList.get(fullList.size() - 1).getParameterName());
		if(result.getMarker() != null){
			logger.info("Marker: " + result.getMarker());
			req.setMarker(result.getMarker());
			result = this.getRdsClient().describeDBParameters(req);
			for(Parameter p : result.getParameters()){
				fullList.add(p);
			}
			logger.info(result.getParameters().size() + " items are returned.");
		}
		assertEquals(fullList.size(), 190);
		logger.info("DescribeDBParametersResult: " + result.toString());
	}

	@Test
	public void testModifyDBParameterGroup0(){
		logger.info("Modifying some DB Parameters");
		ModifyDBParameterGroupRequest req = new ModifyDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		Parameter autocommit = new Parameter();
		autocommit.setParameterName("autocommit");
		autocommit.setParameterValue("1");
		autocommit.setApplyMethod("immediate");
		parameters.add(autocommit);
		req.setParameters(parameters);
		ModifyDBParameterGroupResult result = this.getRdsClient().modifyDBParameterGroup(req);
		assertEquals(result.getDBParameterGroupName(), this.dbParamGrpName);
		logger.info("ModifyDBParameterGroupResult: " + result.toString());
	}

	@Test
	public void testDescribeDBParameters1(){
		logger.info("Describing DB Parameters");
		DescribeDBParametersRequest req = new DescribeDBParametersRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		req.setSource(Constants.USER);
		DescribeDBParametersResult result = this.getRdsClient().describeDBParameters(req);
		assertEquals(result.getParameters().size(), 1);
		assertEquals(result.getParameters().get(0).getParameterName(), "autocommit");
		assertEquals(result.getParameters().get(0).getParameterValue(), "1");
		logger.info("DescribeDBParametersResult: " + result.toString());
	}

	@Test
	public void testModifyDBParameterGroup1(){
		logger.info("Modifying some DB Parameters");
		ModifyDBParameterGroupRequest req = new ModifyDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		Parameter tmp_table_size = new Parameter();
		tmp_table_size.setParameterName("tmp_table_size");
		tmp_table_size.setParameterValue("12412253");
		tmp_table_size.setApplyMethod("pending-reboot");
		parameters.add(tmp_table_size);
		req.setParameters(parameters);
		ModifyDBParameterGroupResult result = this.getRdsClient().modifyDBParameterGroup(req);
		assertEquals(result.getDBParameterGroupName(), this.dbParamGrpName);
		logger.info("ModifyDBParameterGroupResult: " + result.toString());
	}

	@Test
	public void testDescribeDBParameters2(){
		logger.info("Describing DB Parameters");
		DescribeDBParametersRequest req = new DescribeDBParametersRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		req.setSource(Constants.USER);
		DescribeDBParametersResult result = this.getRdsClient().describeDBParameters(req);
		assertEquals(result.getParameters().size(), 2);
		assertEquals(result.getParameters().get(0).getParameterName(), "autocommit");
		assertEquals(result.getParameters().get(0).getParameterValue(), "1");
		assertEquals(result.getParameters().get(1).getParameterName(), "tmp_table_size");
		assertEquals(result.getParameters().get(1).getParameterValue(), "12412253");
		logger.info("DescribeDBParametersResult: " + result.toString());
	}

	@Test
	public void testResetDBParameterGroup0(){
		logger.info("Resetting a DBParameterGroup");
		ResetDBParameterGroupRequest req = new ResetDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		parameters.add(new Parameter().withParameterName("autocommit").withApplyMethod("immediate"));
		req.setParameters(parameters);
		req.setResetAllParameters(false);
		ResetDBParameterGroupResult result = this.getRdsClient().resetDBParameterGroup(req);
		logger.info("ResetDBParameterGroupResult: " + result.toString());
	}

	@Test
	public void testDescribeDBParameters3(){
		logger.info("Describing DB Parameters");
		DescribeDBParametersRequest req = new DescribeDBParametersRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		req.setSource(Constants.USER);
		DescribeDBParametersResult result = this.getRdsClient().describeDBParameters(req);
		assertEquals(result.getParameters().size(), 1);
		assertEquals(result.getParameters().get(0).getParameterName(), "tmp_table_size");
		assertEquals(result.getParameters().get(0).getParameterValue(), "12412253");
		logger.info("DescribeDBParametersResult: " + result.toString());
	}

	@Test
	public void testResetDBParameterGroup1(){
		logger.info("Resetting a DBParameterGroup");
		ResetDBParameterGroupRequest req = new ResetDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		req.setResetAllParameters(true);
		this.getRdsClient().resetDBParameterGroup(req);
		logger.info("ResetDBParameterGroupResult: ");
	}

	@Test
	public void testDescribeDBParameters4(){
		logger.info("Describing DB Parameters");
		DescribeDBParametersRequest req = new DescribeDBParametersRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		req.setSource(Constants.USER);
		DescribeDBParametersResult result = this.getRdsClient().describeDBParameters(req);
		assertEquals(result.getParameters().size(), 0);
		logger.info("DescribeDBParametersResult: " + result.toString());
	}

	@Test
	public void testModifyDBParameterGroup2(){
		logger.info("Modifying some DB Parameters");
		ModifyDBParameterGroupRequest req = new ModifyDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		parameters.add(new Parameter().withParameterName("binlog_cache_size").withApplyMethod("immediate").withParameterValue("32760"));
		parameters.add(new Parameter().withParameterName("innodb_additional_mem_pool_size").withApplyMethod("pending-reboot").withParameterValue("2097152"));
		req.setParameters(parameters);
		ModifyDBParameterGroupResult result = this.getRdsClient().modifyDBParameterGroup(req);
		assertEquals(result.getDBParameterGroupName(), this.dbParamGrpName);
		logger.info("ModifyDBParameterGroupResult: " + result.toString());
	}

	@Test
	public void testDescribeDBParameters5(){
		logger.info("Describing DB Parameters");
		DescribeDBParametersRequest req = new DescribeDBParametersRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		req.setSource(Constants.USER);
		DescribeDBParametersResult result = this.getRdsClient().describeDBParameters(req);
		assertEquals(result.getParameters().size(), 2);
		assertEquals(result.getParameters().get(0).getParameterName(), "binlog_cache_size");
		assertEquals(result.getParameters().get(0).getParameterValue(), "32760");
		assertEquals(result.getParameters().get(1).getParameterName(), "innodb_additional_mem_pool_size");
		assertEquals(result.getParameters().get(1).getParameterValue(), "2097152");
		logger.info("DescribeDBParametersResult: " + result.toString());
	}

	@Test
	public void testCreateDBInstance(){
		logger.info("Creating a new DBInstance.");
		CreateDBInstanceRequest req = new CreateDBInstanceRequest();
		req.setAllocatedStorage(6);
		req.setAvailabilityZone(avZone);
		req.setDBInstanceClass("db.t1.micro");
		//req.setDBInstanceClass("db.m1.medium");
		req.setDBInstanceIdentifier(this.dbInstanceName);
		req.setEngine("MySQL");
		req.setMasterUsername("msi");
		req.setMasterUserPassword("msi");
		//req.setPort(3306);
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<String> dBSecurityGroups = new ArrayList<String>();
		dBSecurityGroups.add(this.dbSecGrpName);
		req.setDBSecurityGroups(dBSecurityGroups);
		DBInstance result = this.getRdsClient().createDBInstance(req);
		assertEquals(result.getAllocatedStorage(), new Integer(6));
		assertEquals(result.getAutoMinorVersionUpgrade(), true);
		assertEquals(result.getAvailabilityZone(), avZone);
		assertNotSame(result.getBackupRetentionPeriod(), null);
		assertEquals(result.getDBInstanceClass(), "db.t1.micro");
		assertEquals(result.getDBInstanceIdentifier(), this.dbInstanceName);
		assertEquals(result.getDBInstanceStatus(), "creating");
		assertEquals(result.getDBName(), null);
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
	}

	@Test
	public void waitForDBInstance0(){
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
				fail();
			}
		} catch (InterruptedException e) {
			logger.debug("InterruptedException was caught unexpectedly. Test failed!");
		}
	}

	/*@Test
	public void testModifyDBParameterGroup3(){
		logger.info("Modifying some DB Parameters");
		ModifyDBParameterGroupRequest req = new ModifyDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		Collection<Parameter> parameters = new LinkedList<Parameter>();
		parameters.add(new Parameter().withParameterName("binlog_cache_size").withApplyMethod("immediate").withParameterValue("32761"));
		parameters.add(new Parameter().withParameterName("innodb_additional_mem_pool_size").withApplyMethod("pending-reboot").withParameterValue("2097150"));
		req.setParameters(parameters);
		ModifyDBParameterGroupResult result = this.getRdsClient().modifyDBParameterGroup(req);
		assertEquals(result.getDBParameterGroupName(), this.dbParamGrpName);
		logger.info("ModifyDBParameterGroupResult: " + result.toString());
	}*/

	// TODO describe the DBInstance and DBParameterGroup to check the status

	@Test 
	public void testReboot(){
		logger.info("Rebooting the DBInstance.");
		RebootDBInstanceRequest req = new RebootDBInstanceRequest();
		req.setDBInstanceIdentifier(this.dbInstanceName);
		DBInstance result = this.getRdsClient().rebootDBInstance(req);
		logger.info("CreateDBSnapshotResult: " + result.toString());
	}


	@Test
	public void waitForDBInstance1(){
		try {
			int counter = 1;
			int lim = 30;
			long waitMilis = 30000;
			boolean rebooting = true;
			boolean timeout = false;
			logger.info("Describing the DBInstance to check its status.");
			DescribeDBInstancesRequest req = new DescribeDBInstancesRequest();
			req.setDBInstanceIdentifier(this.dbInstanceName);
			logger.info("DescribeDBInstancesRequest: " + req.toString());
			while(rebooting){
				DescribeDBInstancesResult result = this.getRdsClient().describeDBInstances(req);
				DBInstance dbinstance = result.getDBInstances().get(0);
				logger.info(counter + "th call to DescribeDBInstances: " + dbinstance.toString());
				if(dbinstance.getDBInstanceStatus().equals("available")){
					rebooting = false;
					break;
				}
				Thread.sleep(waitMilis);
				if(counter < lim){
					++counter;	
				}
				else{
					rebooting = false;
					timeout = true;
				}
			}
			if(timeout){
				logger.debug("CreateDBInstance: sent; DBInstance was not provisioned within limited time. Test failed!");
				fail();
			}
		} catch (InterruptedException e) {
			logger.debug("InterruptedException was caught unexpectedly. Test failed!");
		}
	}

	// TODO reset the parameter group

	// TODO reboot the DBInstance	

	/*@Test
	public void testCreateDBSnapshot(){
		logger.info("Creating a new DBSnapshot.");
		CreateDBSnapshotRequest req = new CreateDBSnapshotRequest();
		req.setDBInstanceIdentifier(dbInstanceName);
		req.setDBSnapshotIdentifier(dbSnapshotName);
		DBSnapshot result = this.getRdsClient().createDBSnapshot(req);
		logger.info("CreateDBSnapshotResult: " + result.toString());
	}*/

	/*@Test
	public void testDeleteDBSnapshot(){
		logger.info("Deleting the DBSnapshot.");
		DeleteDBSnapshotRequest req = new DeleteDBSnapshotRequest();
		req.setDBSnapshotIdentifier(dbSnapshotName);
		DBSnapshot result = this.getRdsClient().deleteDBSnapshot(req);
		logger.info("DeleteDBSnapshotResult: " + result.toString());
	}*/

	/*@Test
	public void testCreateDBInstanceReadReplica(){
		logger.info("Creating a new DBInstance read replica.");
		CreateDBInstanceReadReplicaRequest req = new CreateDBInstanceReadReplicaRequest();
		req.setDBInstanceIdentifier(dbReplicaName);
		req.setSourceDBInstanceIdentifier(dbInstanceName);
		req.setAvailabilityZone(avZone);
		req.setDBInstanceClass("db.m1.medium");
		req.setPort(3306);
		req.setAutoMinorVersionUpgrade(false);
		DBInstance result = this.getRdsClient().createDBInstanceReadReplica(req);
		logger.info("CreateDBInstanceReadReplicaResult: " + result.toString());
	}*/

	// TODO delete the read replica

	// TODO create 2 new read replica

	@Test
	public void testDeleteDBInstance(){
		logger.info("Deleting the 1st DBInstance (source DBInstance).");
		DeleteDBInstanceRequest req = new DeleteDBInstanceRequest();
		req.setDBInstanceIdentifier(this.dbInstanceName);
		req.setSkipFinalSnapshot(true);
		DBInstance result = this.getRdsClient().deleteDBInstance(req);
		logger.info("DeleteDBInstanceResult: " + result.toString());
	}

	@Test
	public void waitForDBInstance2(){
		try {
			logger.info("Waiting for the DBInstance to be cleaned up..");
			Thread.sleep(20000);

			int counter = 1;
			int lim = 30;
			long waitMilis = 5000;
			boolean deleting = true;
			boolean timeout = false;
			logger.info("Describing the DBInstance to check its status.");
			DescribeDBInstancesRequest req = new DescribeDBInstancesRequest();
			req.setDBInstanceIdentifier(this.dbInstanceName);
			logger.info("DescribeDBInstancesRequest: " + req.toString());
			while(deleting){
				try{
					DescribeDBInstancesResult result = this.getRdsClient().describeDBInstances(req);
					DBInstance dbinstance = result.getDBInstances().get(0);
					logger.info(counter + "th call to DescribeDBInstances: " + dbinstance.toString());
				}catch(AmazonServiceException e){
					if(e.getErrorCode().equals("DBInstanceNotFound")){
						deleting = false;	
					}
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
				fail();
			}
		} catch (InterruptedException e) {
			logger.debug("InterruptedException was caught unexpectedly. Test failed!");
			fail();
		}
	}

	/*@Test
	public void testDeleteDBInstanceReadReplica(){
		logger.info("Deleting the 2nd DBInstance (DBInstanceReadReplica).");
		DeleteDBInstanceRequest req = new DeleteDBInstanceRequest();
		req.setDBInstanceIdentifier(this.dbReplicaName);
		req.setSkipFinalSnapshot(true);
		DBInstance result = this.getRdsClient().deleteDBInstance(req);
		logger.info("DeleteDBInstanceResult: " + result.toString());
	}*/

	/*@Test(expected=DBInstanceNotFoundException.class)
	public void waitTillDBInstanceIsDeleted(){
		try {
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
		} catch (InterruptedException e) {
			logger.debug("InterruptedException was caught unexpectedly. Test failed!");
		}
	}*/

	// TODO delete the two replicas

	// TODO restore the DBInstance from DBSnapshot

	// TODO delete the snapshot

	// TODO delete the restored DBInstance

	@Test
	public void testRevokeDBSecurityGroupIngress(){
		logger.info("Revoking a DBSecurityGroupIngress.");
		RevokeDBSecurityGroupIngressRequest req = new RevokeDBSecurityGroupIngressRequest();
		req.setCIDRIP(this.cidrip);
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DBSecurityGroup result = this.getRdsClient().revokeDBSecurityGroupIngress(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getIPRanges().get(0).getStatus(), RDSUtilities.STATUS_REVOKING);
		assertEquals(result.getEC2SecurityGroups().get(0).getEC2SecurityGroupName(), "default");
		assertEquals(result.getEC2SecurityGroups().get(0).getEC2SecurityGroupOwnerId(), "1");
		assertNotSame(result.getDBSecurityGroupDescription(), null);
	}

	@Test
	public void testDescribeDBSecurityGroups3(){
		logger.info("Describing the DBSecurityGroup.");
		DescribeDBSecurityGroupsRequest req = new DescribeDBSecurityGroupsRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DescribeDBSecurityGroupsResult result = this.getRdsClient().describeDBSecurityGroups(req);
		assertEquals(result.getDBSecurityGroups().get(0).getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getDBSecurityGroups().get(0).getIPRanges().size(), 0);
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().get(0).getEC2SecurityGroupName(), "default");
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().get(0).getEC2SecurityGroupOwnerId(), "1");
		assertNotSame(result.getDBSecurityGroups().get(0).getDBSecurityGroupDescription(), null);
		logger.info("DescribeDBSecurityGroupsResult: " + result.toString());
	}

	@Test
	public void testRevokeDBSecurityGroupIngress2(){
		logger.info("Revoking a DBSecurityGroupIngress.");
		RevokeDBSecurityGroupIngressRequest req = new RevokeDBSecurityGroupIngressRequest();
		req.setEC2SecurityGroupOwnerId("1");
		req.setEC2SecurityGroupName("default");
		req.setDBSecurityGroupName(dbSecGrpName);
		DBSecurityGroup result = this.getRdsClient().revokeDBSecurityGroupIngress(req);
		assertEquals(result.getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getEC2SecurityGroups().get(0).getStatus(), RDSUtilities.STATUS_REVOKING);
		assertEquals(result.getEC2SecurityGroups().get(0).getEC2SecurityGroupName(), "default");
		assertEquals(result.getEC2SecurityGroups().get(0).getEC2SecurityGroupOwnerId(), "1");
		assertNotSame(result.getDBSecurityGroupDescription(), null);
	}

	@Test
	public void testDescribeDBSecurityGroups4(){
		logger.info("Describing the DBSecurityGroup.");
		DescribeDBSecurityGroupsRequest req = new DescribeDBSecurityGroupsRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DescribeDBSecurityGroupsResult result = this.getRdsClient().describeDBSecurityGroups(req);
		assertEquals(result.getDBSecurityGroups().get(0).getDBSecurityGroupName(), this.dbSecGrpName);
		assertEquals(result.getDBSecurityGroups().get(0).getIPRanges().size(), 0);
		assertEquals(result.getDBSecurityGroups().get(0).getEC2SecurityGroups().size(), 0);
		assertNotSame(result.getDBSecurityGroups().get(0).getDBSecurityGroupDescription(), null);
		logger.info("DescribeDBSecurityGroupsResult: " + result.toString());
	}

	@Test
	public void testDeleteDBSecurityGroup(){
		logger.info("Deleting the DBSecurityGroup.");
		DeleteDBSecurityGroupRequest req = new DeleteDBSecurityGroupRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		this.getRdsClient().deleteDBSecurityGroup(req);
		logger.info("There is no result response for this action. Continue testing.");
	}

	@Test(expected=DBSecurityGroupNotFoundException.class)
	public void testDescribeDBSecurityGroups6(){
		logger.info("Describing the DBSecurityGroup, " + this.dbSecGrpName + ". Expect DBSecurityGroupNotFoundException to be thrown.");
		DescribeDBSecurityGroupsRequest req = new DescribeDBSecurityGroupsRequest();
		req.setDBSecurityGroupName(this.dbSecGrpName);
		DescribeDBSecurityGroupsResult result = this.getRdsClient().describeDBSecurityGroups(req);
		logger.info("DescribeDBSecurityGroupsResult: " + result.toString());
	}

	@Test
	public void testDeleteDBParameterGroup(){
		logger.info("Deleting the DBParameterGroup.");
		DeleteDBParameterGroupRequest req = new DeleteDBParameterGroupRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		this.getRdsClient().deleteDBParameterGroup(req);
		logger.info("There is no result response for this action. Continue testing.");
	}

	@Test(expected=DBParameterGroupNotFoundException.class)
	public void testDescribeDBParameterGroups0(){
		logger.info("Describing the DBParameterGroup, " + this.dbParamGrpName + ". Expect DBParameterGroupNotFoundException to be thrown.");
		DescribeDBParameterGroupsRequest req = new DescribeDBParameterGroupsRequest();
		req.setDBParameterGroupName(this.dbParamGrpName);
		DescribeDBParameterGroupsResult result = this.getRdsClient().describeDBParameterGroups(req);
		logger.info("DescribeDBParameterGroupsResult: " + result.toString());
	}

	@Test
	public void testDescribeDBInstancesGeneric(){
		logger.info("Calling DescribeDBInstances action.");
		DescribeDBInstancesResult result = this.getRdsClient().describeDBInstances();
		logger.info("DescribeDBInstancesResult: " + result.toString());
	}
}
