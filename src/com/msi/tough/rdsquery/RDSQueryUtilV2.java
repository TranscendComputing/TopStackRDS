package com.msi.tough.rdsquery;

import java.util.List;

import com.generationjava.io.xml.XMLNode;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.rds.RDSUtilities;
import com.transcend.rds.message.RDSMessage.AvailabilityZone;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RDSMessage.DBParameterGroupStatus;
import com.transcend.rds.message.RDSMessage.DBSecurityGroupMembership;
import com.transcend.rds.message.RDSMessage.DBSnapshot;
import com.transcend.rds.message.RDSMessage.EC2SecurityGroup;
import com.transcend.rds.message.RDSMessage.IPRange;
import com.transcend.rds.message.RDSMessage.Parameter;
import com.transcend.rds.message.RDSMessage.PendingModifiedValues;
import com.transcend.rds.message.RDSMessage.ReservedDBInstance;
import com.transcend.rds.message.RDSMessage.ReservedDBInstancesOffering;


public class RDSQueryUtilV2 {
	public static void marshalDBInstance(XMLNode parent, DBInstance dbInstance) {
		XMLNode n = new XMLNode(RDS_Constants.NODE_DBINSTANCE);
		parent.addNode(n);

		QueryUtilV2.addNode(n, RDS_Constants.NODE_ALLOCATEDSTORAGE,
				"" + dbInstance.getAllocatedStorage());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_AUTOMINORVERSIONUPGRADE,
				dbInstance.getAutoMinorVersionUpgrade());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_AVAILABILITYZONE,
				dbInstance.getAvailabilityZone());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_BACKUPRETENTIONPERIOD,
				dbInstance.getBackupRetentionPeriod());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBINSTANCECLASS,
				dbInstance.getDbInstanceClass());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBINSTANCEIDENTIFIER,
				dbInstance.getDbInstanceIdentifier());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBNAME, dbInstance.getDbName());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBINSTANCESTATUS,
				dbInstance.getDbInstanceStatus());

		RDSQueryUtilV2.marshalDBParameterGroupStatus(n, dbInstance);
		RDSQueryUtilV2.marshalDBSecurityGroups(n, dbInstance);
		RDSQueryUtilV2.marshalEndpoint(n, dbInstance);

		QueryUtilV2.addNode(n, RDS_Constants.NODE_ENGINE, dbInstance.getEngine().toLowerCase());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ENGINEVERSION,
				dbInstance.getEngineVersion());
		QueryUtilV2.addNode(n, RDS_Constants.INSTANCE_CREATETIME,
				dbInstance.getInstanceCreateTime());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_LICENSEMODEL,
				dbInstance.getLicenseModel());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_LATESTRESTORABLETIME,
				dbInstance.getLatestRestorableTime());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_MULTIAZ,
				dbInstance.getMultiAZ());

		RDSQueryUtilV2.marshalPendingModifiedValues(n,
				dbInstance.getPendingModifiedValues());

		QueryUtilV2.addNode(n, RDS_Constants.PREFERREDBACKUPWINDOW,
				dbInstance.getPreferredBackupWindow());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_PREFERREDMAINTENANCEWINDOW,
				dbInstance.getPreferredMaintenanceWindow());

		RDSQueryUtilV2.marshalReadReplicaDBInstanceIdentifiers(n,
				dbInstance.getReadReplicaDBInstanceIdentifiersList());

		QueryUtilV2.addNode(n, RDS_Constants.NODE_READREPLICASOURCEDBINSTANCEIDENTIFIER,
				dbInstance.getReadReplicaSourceDBInstanceIdentifier());
		
		QueryUtilV2.addNode(n, RDS_Constants.NODE_MASTERUSERNAME,
				dbInstance.getMasterUsername());
		
	}

	private static void marshalReadReplicaDBInstanceIdentifiers(XMLNode parent,
			List<String> readReplicaDBInstanceIdentifiers) {
		if (readReplicaDBInstanceIdentifiers != null
				&& readReplicaDBInstanceIdentifiers.size() > 0) {
			XMLNode nodeIDs = QueryUtilV2.addNode(parent, RDS_Constants.NODE_READREPLICADBINSTANCEIDENTIFIERS);
			for (String dbi : readReplicaDBInstanceIdentifiers) {
				QueryUtilV2.addNode(nodeIDs, RDS_Constants.NODE_READREPLICADBINSTANCEIDENTIFIER, dbi);
			}
		}
	}

	private static void marshalPendingModifiedValues(XMLNode parent,
			PendingModifiedValues pendingModifiedValues) {
		XMLNode nodePMV = QueryUtilV2.addNode(parent, RDS_Constants.NODE_PENDINGMODIFIEDVALUES);
		if (nodePMV != null && pendingModifiedValues != null) {
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_ALLOCATEDSTORAGE, pendingModifiedValues.getAllocatedStorage());
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_BACKUPRETENTIONPERIOD, pendingModifiedValues.getBackupRetentionPeriod());
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_DBINSTANCECLASS, pendingModifiedValues.getDbInstanceClass());
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_ENGINEVERSION, pendingModifiedValues.getEngineVersion());
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_MASTERUSERPASSWORD, pendingModifiedValues.getMasterUserPassword());
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_MULTIAZ, pendingModifiedValues.getMultiAZ());
			QueryUtilV2.addNode(nodePMV, RDS_Constants.NODE_PORT, pendingModifiedValues.getPort());
		}
	}

	public static void marshalEndpoint(XMLNode parent, DBInstance dbInstance) {
		if (dbInstance.getEndpoint() != null &&
				dbInstance.getEndpoint().getAddress() != null) {
			XMLNode ep = QueryUtilV2.addNode(parent, RDS_Constants.NODE_ENDPOINT);
			QueryUtilV2.addNode(ep, RDS_Constants.NODE_ADDRESS, dbInstance.getEndpoint().getAddress());
			QueryUtilV2.addNode(ep, RDS_Constants.NODE_PORT, dbInstance.getEndpoint().getPort());
		}
	}

	public static void marshalDBSecurityGroups(XMLNode parent, DBInstance dbInstance) {
		List<DBSecurityGroupMembership> memberships = dbInstance.getDbSecurityGroupsList();
		if (memberships != null) {
			XMLNode nodeGroups = QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBSECURITYGROUPS);
			for (DBSecurityGroupMembership m: memberships) {
				XMLNode nodeGroup = QueryUtilV2.addNode(nodeGroups, RDS_Constants.NODE_DBSECURITYGROUP);
				QueryUtilV2.addNode(nodeGroup, RDS_Constants.NODE_STATUS, m.getStatus());
				QueryUtilV2.addNode(nodeGroup, RDS_Constants.NODE_DBSECURITYGROUPNAME, m.getDbSecurityGroupName());
			}
		}
	}

	public static void marshalDBParameterGroupStatus(XMLNode parent, DBInstance dbInstance) {
		List<DBParameterGroupStatus> dbpGroups = dbInstance.getDbParameterGroupsList();
		if (dbpGroups != null && dbpGroups.size() > 0) {
			XMLNode nodeGroups = QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBPARAMETERGROUPS);
			for (DBParameterGroupStatus status : dbpGroups) {
				XMLNode nodeGroup = QueryUtilV2.addNode(nodeGroups, RDS_Constants.NODE_DBPARAMETERGROUP);
				QueryUtilV2.addNode(nodeGroup, RDS_Constants.NODE_DBPARAMETERGROUPNAME, status.getDbParameterGroupName());
				QueryUtilV2.addNode(nodeGroup, RDS_Constants.NODE_PARAMETERAPPLYSTATUS, status.getParameterApplyStatus());
			}
		}
	}

	public static void marshalDBSnapshot(XMLNode parent, DBSnapshot o) {
		XMLNode n = QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBSNAPSHOT);
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ALLOCATEDSTORAGE, o.getAllocatedStorage());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_AVAILABILITYZONE, o.getAvailabilityZone());
		QueryUtilV2.addNode(n, RDS_Constants.LICENSEMODEL, o.getLicenseModel());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBINSTANCEIDENTIFIER,
				o.getDbInstanceIdentifier());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_DBSNAPSHOTIDENTIFIER,
				o.getDbSnapshotIdentifier());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ENGINE, o.getEngine());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_ENGINEVERSION, o.getEngineVersion());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_INSTANCECREATETIME, o.getInstanceCreateTime());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_MASTERUSERNAME, o.getMasterUsername());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_PORT, o.getPort());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_SNAPSHOTCREATETIME, o.getSnapshotCreateTime());
		QueryUtilV2.addNode(n, RDS_Constants.NODE_STATUS, o.getStatus());
	}

	public static void marshalEC2SecurityGroups(XMLNode parent,
			List<EC2SecurityGroup> ec2SecurityGroups) {
		XMLNode nodeGroups = QueryUtilV2.addNode(parent, RDS_Constants.NODE_EC2SECURITYGROUPS);
		if (ec2SecurityGroups != null)
		{
			for (EC2SecurityGroup sg : ec2SecurityGroups) {
				XMLNode ec2secgrp = QueryUtilV2.addNode(nodeGroups, RDS_Constants.NODE_EC2SECURITYGROUP);
				QueryUtilV2.addNode(ec2secgrp, RDS_Constants.NODE_EC2SECURITYGROUPOWNERID, sg.getEc2SecurityGroupOwnerId());
				QueryUtilV2.addNode(ec2secgrp, RDS_Constants.NODE_EC2SECURITYGROUPNAME, sg.getEc2SecurityGroupName());
				QueryUtilV2.addNode(ec2secgrp, RDS_Constants.NODE_STATUS, sg.getStatus());
			}
		}
	}

	public static void marshalIPRanges(XMLNode parent, List<IPRange> ipRanges) {
		XMLNode nodeIPS = QueryUtilV2.addNode(parent, RDS_Constants.NODE_IPRANGES);
		for (IPRange r: ipRanges) {
			XMLNode nodeIP = QueryUtilV2.addNode(nodeIPS, RDS_Constants.NODE_IPRANGE);
			QueryUtilV2.addNode(nodeIP, RDS_Constants.NODE_CIDRIP, r.getCidrip());
			QueryUtilV2.addNode(nodeIP, RDS_Constants.NODE_STATUS, r.getStatus());
		}
	}

	public static void marshalReservedDBInstance(XMLNode parent,
			ReservedDBInstance rdbInstance) {
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_CURRENCYCODE, rdbInstance.getCurrencyCode());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBINSTANCECLASS, rdbInstance.getDbInstanceClass());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBINSTANCECOUNT, rdbInstance.getDbInstanceCount());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_DURATION, rdbInstance.getDuration());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_FIXEDPRICE, rdbInstance.getFixedPrice());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_MULTIAZ, rdbInstance.getMultiAZ());
		
		// TODO: these exist in the latest SDK
		//QueryUtilV2.addNode(rdbisNode, RDS_Constants.NODE_OFFERINGTYPE, rdbi.getOfferingType());
		//RDSQueryUtilV2.marshallNode(rdbisNode, rdbi.getRecurringCharges);

		QueryUtilV2.addNode(parent, RDS_Constants.NODE_PRODUCTDESCRIPTION, rdbInstance.getProductDescription());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCEID, rdbInstance.getReservedDBInstanceId());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERINGID, rdbInstance.getReservedDBInstancesOfferingId());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_STARTTIME, rdbInstance.getStartTime());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_STATE, rdbInstance.getState());
		QueryUtilV2.addNode(parent, RDS_Constants.NODE_USAGEPRICE, rdbInstance.getUsagePrice());
	}

	public static void marshalReservedDBInstances(XMLNode parent,
			List<ReservedDBInstance> reservedDBInstances) {
		XMLNode rdbisNode = QueryUtilV2.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCES);
		for (ReservedDBInstance rdbi : reservedDBInstances) {
			RDSQueryUtilV2.marshalReservedDBInstance(rdbisNode, rdbi);
		}
	}

	public static void marshalParameters(XMLNode parent, List<Parameter> parameters) {
		final XMLNode nl = QueryUtilV2.addNode(parent, RDS_Constants.NODE_PARAMETERS);
		for (Parameter p : parameters) {
			final XMLNode n = QueryUtilV2.addNode(nl, RDS_Constants.NODE_PARAMETER);
			QueryUtilV2.addNode(n, RDS_Constants.NODE_ALLOWEDVALUES, p.getAllowedValues());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_APPLYMETHOD, p.getApplyMethod());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_APPLYTYPE, p.getApplyType());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_DATATYPE, p.getDataType());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_DESCRIPTION, p.getDescription());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_ISMODIFIABLE, "" + p.getIsModifiable());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_MINIMUMENGINEVERSION,
					p.getMinimumEngineVersion());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_PARAMETERNAME, p.getParameterName());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_PARAMETERVALUE, p.getParameterValue());
			QueryUtilV2.addNode(n, RDS_Constants.NODE_SOURCE, p.getSource());
			
			
			DBInstance.Builder inst = DBInstance.newBuilder();
			inst.setAllocatedStorage(5);
		}
	}
	
	public static void marshalAvailabilityZones(XMLNode parent,
			List<AvailabilityZone> availabilityZones) {
		final XMLNode azl = QueryUtilV2.addNode(parent, RDS_Constants.NODE_AVAILABILITYZONES);
		for (AvailabilityZone az : availabilityZones) {
			final XMLNode azn = QueryUtilV2.addNode(azl, RDS_Constants.NODE_AVAILABILITYZONE);
			QueryUtilV2.addNode(azn, RDS_Constants.NODE_NAME, az.getName());
		}
	}

	public static void marshalReservedDBInstance(XMLNode parent,
			ReservedDBInstancesOffering o) {
    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_CURRENCYCODE, o.getCurrencyCode());
    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_DBINSTANCECLASS, o.getDbInstanceClass());

    	// TODO: update aws sdk for this
    	//QueryUtilV2.addNode(no, RDS_Constants.NODE_DBINSTANCECOUNT, o.getDBInstanceCount());

    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_DURATION, o.getDuration());
    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_FIXEDPRICE, o.getFixedPrice());
    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_MULTIAZ, o.getMultiAZ());

    	// TODO: update aws sdk for this
    	//QueryUtilV2.addNode(no, RDS_Constants.NODE_OFFERINGTYPE, o.getOfferingType());

    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_PRODUCTDESCRIPTION, o.getProductDescription());

    	// TODO: update aws sdk for these
    	//QueryUtilV2.addNode(no, RDS_Constants.NODE_RECURRINGCHARGES, o.getRecurringCharges());
    	//QueryUtilV2.addNode(no, RDS_Constants.NODE_RESERVEDDBINSTANCEID, o.getReservedDBInstanceId());

    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERINGID, o.getReservedDBInstancesOfferingId());

    	// TODO: update aws sdk for these
    	//QueryUtilV2.addNode(no, RDS_Constants.NODE_STARTTIME, o.getStartTime());
    	//QueryUtilV2.addNode(no, RDS_Constants.NODE_STATUS, o.getState();

    	QueryUtilV2.addNode(parent, RDS_Constants.NODE_USAGEPRICE, o.getUsagePrice());
	}
}
