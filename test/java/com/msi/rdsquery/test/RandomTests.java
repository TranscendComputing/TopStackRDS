package com.msi.rdsquery.test;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CreateVolumeRequest;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.msi.tough.cf.AccountType;
import com.msi.tough.engine.aws.ec2.Volume;
import com.msi.tough.engine.core.CallStruct;

public class RandomTests {
	public static void main(String arg[]){
		/*AWSCredentials awsCredentials = new BasicAWSCredentials("66e2e48653d34bbba71591cd88af5241,", "0a5ec60c06b94e0b8fa17636a5e6da35.");
		AmazonRDSClient client = new AmazonRDSClient(awsCredentials);
		client.setEndpoint("http://sandbox:8080/RDSQuery");*/
		
		int i = 0;
		for(i = 0; i < 10; i++){
			System.out.println(0);
		}
		System.out.println(i);
		
	}
	
	
	
	public static void createDBInstance(AmazonRDSClient client){
		CreateDBInstanceRequest req = new CreateDBInstanceRequest();
		req.setAllocatedStorage(5);
		req.setAutoMinorVersionUpgrade(false);
		req.setAvailabilityZone("nova");
		req.setDBInstanceClass("db.m1.medium");
		req.setDBInstanceIdentifier("dkim-test-db");
		req.setDBName("myDatabase");
		//req.set
		
		client.createDBInstance(req);
	}
}
