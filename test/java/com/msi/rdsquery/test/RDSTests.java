package com.msi.rdsquery.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.AuthorizeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceReadReplicaRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.CreateDBParameterGroupRequest;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.CreateDBSnapshotRequest;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.amazonaws.services.rds.model.DeleteDBParameterGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DeleteDBSnapshotRequest;
import com.amazonaws.services.rds.model.DescribeDBParametersRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsRequest;
import com.amazonaws.services.rds.model.DescribeEngineDefaultParametersRequest;
import com.amazonaws.services.rds.model.ModifyDBParameterGroupRequest;
import com.amazonaws.services.rds.model.Parameter;
import com.amazonaws.services.rds.model.RebootDBInstanceRequest;
import com.amazonaws.services.rds.model.ResetDBParameterGroupRequest;
import com.amazonaws.services.rds.model.RestoreDBInstanceFromDBSnapshotRequest;
import com.amazonaws.services.rds.model.RevokeDBSecurityGroupIngressRequest;

public class RDSTests {

    @Test
    public void testNoOp() {

    }

	public static void main(String arg[]){

		//Avaiable Endpoints for AWS RDS:
		/*Region											Endpoint						Protocol
		 *
		US East (Northern Virginia) Region		rds.us-east-1.amazonaws.com					HTTPS
		US West (Oregon) Region					rds.us-west-2.amazonaws.com					HTTPS
		US West (Northern California) Region	rds.us-west-1.amazonaws.com					HTTPS
		EU (Ireland) Region						rds.eu-west-1.amazonaws.com					HTTPS
		Asia Pacific (Singapore) Region			rds.ap-southeast-1.amazonaws.com			HTTPS
		Asia Pacific (Tokyo) Region				rds.ap-northeast-1.amazonaws.com			HTTPS
		South America (Sao Paulo) Region		rds.sa-east-1.amazonaws.com					HTTPS	*/



		AWSCredentials creds = new BasicAWSCredentials("66e2e48653d34bbba71591cd88af5241", "0a5ec60c06b94e0b8fa17636a5e6da35");
		AmazonRDSClient rds = new AmazonRDSClient(creds);
		rds.setEndpoint("http://dk-sandbox:8080/RDSQuery/");

		// ============================================= RDS Test =======================================================

		// ===== DBSecurityGroup tests ======
		CreateDBSecurityGroupRequest DBSG_req = new CreateDBSecurityGroupRequest();
		DBSG_req.setDBSecurityGroupDescription("Blah blah");
		DBSG_req.setDBSecurityGroupName("dkim-test-sg");
		//rds.createDBSecurityGroup(DBSG_req);

		AuthorizeDBSecurityGroupIngressRequest DBSG_req2 = new AuthorizeDBSecurityGroupIngressRequest();
		DBSG_req2.setCIDRIP("0.0.0.0/0");
		DBSG_req2.setDBSecurityGroupName("dkim-test-sg");
		//rds.authorizeDBSecurityGroupIngress(DBSG_req2);

		AuthorizeDBSecurityGroupIngressRequest DBSG_req3 = new AuthorizeDBSecurityGroupIngressRequest();
		DBSG_req3.setEC2SecurityGroupName("dkim-test");
		DBSG_req3.setEC2SecurityGroupOwnerId("4");
		DBSG_req3.setDBSecurityGroupName("dkim-test-sg");
		//rds.authorizeDBSecurityGroupIngress(DBSG_req3);

		//rds.describeDBSecurityGroups();

		RevokeDBSecurityGroupIngressRequest DBSG_req4 = new RevokeDBSecurityGroupIngressRequest();
		DBSG_req4.setCIDRIP("0.0.0.0/0");
		DBSG_req4.setDBSecurityGroupName("dkim-test-sg");
		//DBSecurityGroup res = rds.revokeDBSecurityGroupIngress(DBSG_req4);

		RevokeDBSecurityGroupIngressRequest DBSG_req5 = new RevokeDBSecurityGroupIngressRequest();
		DBSG_req5.setDBSecurityGroupName("dkim-test-sg");
		DBSG_req5.setEC2SecurityGroupName("dkim-test");
		DBSG_req5.setEC2SecurityGroupOwnerId("4");
		//rds.revokeDBSecurityGroupIngress(DBSG_req5);

		DeleteDBSecurityGroupRequest DBSG_req6 = new DeleteDBSecurityGroupRequest();
		DBSG_req6.setDBSecurityGroupName("dkim-test-sg");
		//rds.deleteDBSecurityGroup(DBSG_req6);

		AuthorizeDBSecurityGroupIngressRequest DBSG_req7 = new AuthorizeDBSecurityGroupIngressRequest();
		DBSG_req7.setEC2SecurityGroupName("dkim-test");
		DBSG_req7.setEC2SecurityGroupOwnerId("4");
		DBSG_req7.setDBSecurityGroupName("default");
		//rds.authorizeDBSecurityGroupIngress(DBSG_req7);


		// ===== DBParameterGroup tests =====
		CreateDBParameterGroupRequest DBP_req = new CreateDBParameterGroupRequest();
		DBP_req.setDBParameterGroupFamily("MYSQL5.5");
		DBP_req.setDBParameterGroupName("dkim-dbparameter-grp");
		DBP_req.setDescription("Daniel Kim's DBParameterGroup");
		//rds.createDBParameterGroup(DBP_req);

		DeleteDBParameterGroupRequest DBP_req2 = new DeleteDBParameterGroupRequest();
		DBP_req2.setDBParameterGroupName("dkim-dbparameter-grp");
		//rds.deleteDBParameterGroup(DBP_req2);

		ResetDBParameterGroupRequest DBP_req3 = new ResetDBParameterGroupRequest();
		DBP_req3.setDBParameterGroupName("dkim-dbparameter-grp");
		/*Collection<Parameter> parameters = new LinkedList<Parameter>();
		//parameters.add(new Parameter().withParameterName("binlog_cache_size").withApplyMethod("pending-reboot"));
		//parameters.add(new Parameter().withParameterName("innodb_additional_mem_pool_size").withApplyMethod("pending-reboot"));
		DBP_req3.setParameters(parameters);
		DBP_req3.setResetAllParameters(false);*/
		//rds.resetDBParameterGroup(DBP_req3);

		ModifyDBParameterGroupRequest DBP_req4 = new ModifyDBParameterGroupRequest();
		DBP_req4.setDBParameterGroupName("dkim-dbparameter-grp");
		Collection<Parameter> parameters2 = new LinkedList<Parameter>();
		//parameters2.add(new Parameter().withParameterName("binlog_cache_size").withApplyMethod("immediate").withParameterValue("32760"));
		//parameters2.add(new Parameter().withParameterName("innodb_additional_mem_pool_size").withApplyMethod("pending-reboot").withParameterValue("2097152"));
		DBP_req4.setParameters(parameters2);
		//rds.modifyDBParameterGroup(DBP_req4);


		// ===== DBInstance tests =====
		CreateDBInstanceRequest DBI_req = new CreateDBInstanceRequest();
		DBI_req.setAllocatedStorage(5);
		DBI_req.setAvailabilityZone("essex-nova");
		DBI_req.setDBInstanceClass("db.m1.medium");
		DBI_req.setDBInstanceIdentifier("dkim-dbinstance");
		DBI_req.setEngine("MySQL");
		DBI_req.setMasterUsername("dkim");
		DBI_req.setMasterUserPassword("passThis");
		DBI_req.setPort(3306);
		DBI_req.setDBParameterGroupName("dkim-dbparameter-grp");
		Collection<String> dBSecurityGroups = new ArrayList<String>();
		dBSecurityGroups.add("dkim-test-sg");
		DBI_req.setDBSecurityGroups(dBSecurityGroups);
		//rds.createDBInstance(DBI_req);

		DeleteDBInstanceRequest DBI_req2 = new DeleteDBInstanceRequest();
		DBI_req2.setDBInstanceIdentifier("dkim-dbinstance");
		//DBI_req2.setFinalDBSnapshotIdentifier("final-snapshot");
		DBI_req2.setSkipFinalSnapshot(true);
		//rds.deleteDBInstance(DBI_req2);

		//DescribeDBInstancesResult DBI_res = rds.describeDBInstances();
		//System.out.println(DBI_res.toString());

		RestoreDBInstanceFromDBSnapshotRequest DBI_req3 = new RestoreDBInstanceFromDBSnapshotRequest();
		DBI_req3.setDBInstanceIdentifier("restored-dbinstance");
		DBI_req3.setDBSnapshotIdentifier("final-snapshot");
		//rds.restoreDBInstanceFromDBSnapshot(DBI_req3);

		RebootDBInstanceRequest DBI_req4 = new RebootDBInstanceRequest();
		DBI_req4.setDBInstanceIdentifier("dkim-dbinstance");
		//rds.rebootDBInstan ce(DBI_req4);

		CreateDBInstanceReadReplicaRequest DBI_req5 = new CreateDBInstanceReadReplicaRequest();
		DBI_req5.setDBInstanceIdentifier("dkim-read-dbinstance");
		DBI_req5.setSourceDBInstanceIdentifier("dkim-dbinstance");
		//rds.createDBInstanceReadReplica(DBI_req5);

		// ===== DBSnapshot tests =====
		CreateDBSnapshotRequest DBS_req = new CreateDBSnapshotRequest();
		DBS_req.setDBInstanceIdentifier("dkim-dbinstance");
		DBS_req.setDBSnapshotIdentifier("dkim-snapshot");
		//rds.createDBSnapshot(DBS_req);

		DeleteDBSnapshotRequest DBS_req2 = new DeleteDBSnapshotRequest();
		DBS_req2.setDBSnapshotIdentifier("dkim-snapshot");
		//rds.deleteDBSnapshot(DBS_req2);

		DescribeDBSnapshotsRequest DBS_req3 = new DescribeDBSnapshotsRequest();
		//rds.describeDBSnapshots(DBS_req3);


		// ===== Misc. tests =====
		//rds.describeEvents();

		DescribeEngineDefaultParametersRequest m_req2 = new DescribeEngineDefaultParametersRequest();
		m_req2.setDBParameterGroupFamily("mysql5.5");
		//m_req2.setMarker("101");
		//rds.describeEngineDefaultParameters(m_req2);

		DescribeDBParametersRequest m_req3 = new DescribeDBParametersRequest();
		m_req3.setDBParameterGroupName("sampleparametergroup");
		//m_req3.setDBParameterGroupName("dkim-dbparameter-grp");
		m_req3.setSource("user");
		//rds.describeDBParameters(m_req3);



		// ================================================================================================================





		String instanceId = "i-FA6E4434";
		String volumeId = "vol-C86A3F83";
		/*Volume vol = new Volume();
		CallStruct call = new CallStruct();
		AccountType ac = new AccountType();
		call.setAc(ac);
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("InstanceId", instanceId);
		properties.put("VolumeId", volumeId);
		properties.put("AvailabilityZone", "msicluster2");
		call.setProperties(properties);*/
		//vol.attach(call);


		/*AWSCredentials cred = new BasicAWSCredentials("access", "secret");
		AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		ec2.setEndpoint("http://euca3fe.momentumsoftware.com:8773/services/Eucalyptus");
		AttachVolumeRequest req = new AttachVolumeRequest();
		req.setDevice("/dev/xvdb");
		req.setInstanceId(instanceId);
		req.setVolumeId(volumeId);
		AttachVolumeResult result = ec2.attachVolume(req);
		System.out.println(result.getAttachment().getState());
		DescribeVolumesRequest req2 = new DescribeVolumesRequest();
		Collection<String> w = new LinkedList<String>();
		w.add(volumeId);
		req2.setVolumeIds(w);
		DescribeVolumesResult result2 = ec2.describeVolumes(req2);
		System.out.println(result2.getVolumes().get(0).getState());

		System.out.println("Check the status of the volume...");
		int failCount = 0;
		boolean available = false;
		while (failCount < 10 && !available) {
			DescribeVolumesRequest dvReq = new DescribeVolumesRequest();
			Collection<String> volumeIds = new LinkedList<String>();
			volumeIds.add(volumeId);
			dvReq.setVolumeIds(volumeIds);
			DescribeVolumesResult res = ec2.describeVolumes(dvReq);
			if(res.getVolumes().get(0).getState().equals("in-use")){
				available = true;
			}
		}
		if (failCount == 10 && !available) {
			System.out.println("Volume was never set to available state...");
		}*/

		AWSCredentials cred = new BasicAWSCredentials("access", "secret");
		try{
		AmazonEC2Client ec2 = new AmazonEC2Client(cred);
		//ec2.setEndpoint("http://essexfe.momentumsoftware.com:8773/services/Cloud");
		ec2.setEndpoint("http://euca3fe.momentumsoftware.com:8773/services/Eucalyptus");
		DescribeInstancesRequest req = new DescribeInstancesRequest();
		Collection<String> instanceIds = new LinkedList<String>();
		//instanceIds.add("nope");
		//req.setInstanceIds(instanceIds);
		DescribeInstancesResult result = ec2.describeInstances(req);
		System.out.println(result.toString());
		}catch (AmazonServiceException e) {
			e.printStackTrace();
			System.out.println("We are There!!!!!!!");
		}catch (AmazonClientException e) {
			System.out.println("We are HERE!!!!!!!");
		}
	}
}
