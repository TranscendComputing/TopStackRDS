package com.msi.tough.rdsquery;

import java.util.List;

import com.amazonaws.services.rds.model.AvailabilityZone;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBParameterGroupStatus;
import com.amazonaws.services.rds.model.DBSecurityGroupMembership;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.EC2SecurityGroup;
import com.amazonaws.services.rds.model.IPRange;
import com.amazonaws.services.rds.model.Parameter;
import com.amazonaws.services.rds.model.PendingModifiedValues;
import com.amazonaws.services.rds.model.ReservedDBInstance;
import com.amazonaws.services.rds.model.ReservedDBInstancesOffering;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.utils.rds.RDSUtilities;


public class RDSQueryUtil {
	public static void marshalDBInstance(XMLNode parent, DBInstance dbInstance) {
		XMLNode n = new XMLNode(RDS_Constants.NODE_DBINSTANCE);
		parent.addNode(n);

		QueryUtil.addNode(n, RDS_Constants.NODE_ALLOCATEDSTORAGE,
				"" + dbInstance.getAllocatedStorage());
		QueryUtil.addNode(n, RDS_Constants.NODE_AUTOMINORVERSIONUPGRADE,
				dbInstance.getAutoMinorVersionUpgrade());
		QueryUtil.addNode(n, RDS_Constants.NODE_AVAILABILITYZONE,
				dbInstance.getAvailabilityZone());
		QueryUtil.addNode(n, RDS_Constants.NODE_BACKUPRETENTIONPERIOD,
				dbInstance.getBackupRetentionPeriod());
		QueryUtil.addNode(n, RDS_Constants.NODE_DBINSTANCECLASS,
				dbInstance.getDBInstanceClass());
		QueryUtil.addNode(n, RDS_Constants.NODE_DBINSTANCEIDENTIFIER,
				dbInstance.getDBInstanceIdentifier());
		QueryUtil.addNode(n, RDS_Constants.NODE_DBNAME, dbInstance.getDBName());
		QueryUtil.addNode(n, RDS_Constants.NODE_DBINSTANCESTATUS,
				dbInstance.getDBInstanceStatus());

		RDSQueryUtil.marshalDBParameterGroupStatus(n, dbInstance);
		RDSQueryUtil.marshalDBSecurityGroups(n, dbInstance);
		RDSQueryUtil.marshalEndpoint(n, dbInstance);

		QueryUtil.addNode(n, RDS_Constants.NODE_ENGINE, dbInstance.getEngine().toLowerCase());
		QueryUtil.addNode(n, RDS_Constants.NODE_ENGINEVERSION,
				dbInstance.getEngineVersion());
		QueryUtil.addNode(n, RDS_Constants.INSTANCE_CREATETIME,
				dbInstance.getInstanceCreateTime());
		QueryUtil.addNode(n, RDS_Constants.NODE_LICENSEMODEL,
				dbInstance.getLicenseModel());
		QueryUtil.addNode(n, RDS_Constants.NODE_LATESTRESTORABLETIME,
				dbInstance.getLatestRestorableTime());
		QueryUtil.addNode(n, RDS_Constants.NODE_MULTIAZ,
				dbInstance.getMultiAZ());

		RDSQueryUtil.marshalPendingModifiedValues(n,
				dbInstance.getPendingModifiedValues());

		QueryUtil.addNode(n, RDS_Constants.PREFERREDBACKUPWINDOW,
				dbInstance.getPreferredBackupWindow());
		QueryUtil.addNode(n, RDS_Constants.NODE_PREFERREDMAINTENANCEWINDOW,
				dbInstance.getPreferredMaintenanceWindow());

		RDSQueryUtil.marshalReadReplicaDBInstanceIdentifiers(n,
				dbInstance.getReadReplicaDBInstanceIdentifiers());

		QueryUtil.addNode(n, RDS_Constants.NODE_READREPLICASOURCEDBINSTANCEIDENTIFIER,
				dbInstance.getReadReplicaSourceDBInstanceIdentifier());
		
		QueryUtil.addNode(n, RDS_Constants.NODE_MASTERUSERNAME,
				dbInstance.getMasterUsername());
		
	}

	private static void marshalReadReplicaDBInstanceIdentifiers(XMLNode parent,
			List<String> readReplicaDBInstanceIdentifiers) {
		if (readReplicaDBInstanceIdentifiers != null
				&& readReplicaDBInstanceIdentifiers.size() > 0) {
			XMLNode nodeIDs = QueryUtil.addNode(parent, RDS_Constants.NODE_READREPLICADBINSTANCEIDENTIFIERS);
			for (String dbi : readReplicaDBInstanceIdentifiers) {
				QueryUtil.addNode(nodeIDs, RDS_Constants.NODE_READREPLICADBINSTANCEIDENTIFIER, dbi);
			}
		}
	}

	private static void marshalPendingModifiedValues(XMLNode parent,
			PendingModifiedValues pendingModifiedValues) {
		XMLNode nodePMV = QueryUtil.addNode(parent, RDS_Constants.NODE_PENDINGMODIFIEDVALUES);
		if (nodePMV != null && pendingModifiedValues != null) {
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_ALLOCATEDSTORAGE, pendingModifiedValues.getAllocatedStorage());
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_BACKUPRETENTIONPERIOD, pendingModifiedValues.getBackupRetentionPeriod());
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_DBINSTANCECLASS, pendingModifiedValues.getDBInstanceClass());
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_ENGINEVERSION, pendingModifiedValues.getEngineVersion());
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_MASTERUSERPASSWORD, pendingModifiedValues.getMasterUserPassword());
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_MULTIAZ, pendingModifiedValues.getMultiAZ());
			QueryUtil.addNode(nodePMV, RDS_Constants.NODE_PORT, pendingModifiedValues.getPort());
		}
	}

	public static void marshalEndpoint(XMLNode parent, DBInstance dbInstance) {
		if (dbInstance.getEndpoint() != null &&
				dbInstance.getEndpoint().getAddress() != null) {
			XMLNode ep = QueryUtil.addNode(parent, RDS_Constants.NODE_ENDPOINT);
			QueryUtil.addNode(ep, RDS_Constants.NODE_ADDRESS, dbInstance.getEndpoint().getAddress());
			QueryUtil.addNode(ep, RDS_Constants.NODE_PORT, dbInstance.getEndpoint().getPort());
		}
	}

	public static void marshalDBSecurityGroups(XMLNode parent, DBInstance dbInstance) {
		List<DBSecurityGroupMembership> memberships = dbInstance.getDBSecurityGroups();
		if (memberships != null) {
			XMLNode nodeGroups = QueryUtil.addNode(parent, RDS_Constants.NODE_DBSECURITYGROUPS);
			for (DBSecurityGroupMembership m: memberships) {
				XMLNode nodeGroup = QueryUtil.addNode(nodeGroups, RDS_Constants.NODE_DBSECURITYGROUP);
				QueryUtil.addNode(nodeGroup, RDS_Constants.NODE_STATUS, m.getStatus());
				QueryUtil.addNode(nodeGroup, RDS_Constants.NODE_DBSECURITYGROUPNAME, m.getDBSecurityGroupName());
			}
		}
	}

	public static void marshalDBParameterGroupStatus(XMLNode parent, DBInstance dbInstance) {
		List<DBParameterGroupStatus> dbpGroups = dbInstance.getDBParameterGroups();
		if (dbpGroups != null && dbpGroups.size() > 0) {
			XMLNode nodeGroups = QueryUtil.addNode(parent, RDS_Constants.NODE_DBPARAMETERGROUPS);
			for (DBParameterGroupStatus status : dbpGroups) {
				XMLNode nodeGroup = QueryUtil.addNode(nodeGroups, RDS_Constants.NODE_DBPARAMETERGROUP);
				QueryUtil.addNode(nodeGroup, RDS_Constants.NODE_DBPARAMETERGROUPNAME, status.getDBParameterGroupName());
				QueryUtil.addNode(nodeGroup, RDS_Constants.NODE_PARAMETERAPPLYSTATUS, status.getParameterApplyStatus());
			}
		}
	}

	public static void marshalDBSnapshot(XMLNode parent, DBSnapshot o) {
		XMLNode n = QueryUtil.addNode(parent, RDS_Constants.NODE_DBSNAPSHOT);
		QueryUtil.addNode(n, RDS_Constants.NODE_ALLOCATEDSTORAGE, o.getAllocatedStorage());
		QueryUtil.addNode(n, RDS_Constants.NODE_AVAILABILITYZONE, o.getAvailabilityZone());
		QueryUtil.addNode(n, RDS_Constants.LICENSEMODEL, o.getLicenseModel());
		QueryUtil.addNode(n, RDS_Constants.NODE_DBINSTANCEIDENTIFIER,
				o.getDBInstanceIdentifier());
		QueryUtil.addNode(n, RDS_Constants.NODE_DBSNAPSHOTIDENTIFIER,
				o.getDBSnapshotIdentifier());
		QueryUtil.addNode(n, RDS_Constants.NODE_ENGINE, o.getEngine());
		QueryUtil.addNode(n, RDS_Constants.NODE_ENGINEVERSION, o.getEngineVersion());
		QueryUtil.addNode(n, RDS_Constants.NODE_INSTANCECREATETIME, o.getInstanceCreateTime());
		QueryUtil.addNode(n, RDS_Constants.NODE_MASTERUSERNAME, o.getMasterUsername());
		QueryUtil.addNode(n, RDS_Constants.NODE_PORT, o.getPort());
		QueryUtil.addNode(n, RDS_Constants.NODE_SNAPSHOTCREATETIME, o.getSnapshotCreateTime());
		QueryUtil.addNode(n, RDS_Constants.NODE_STATUS, o.getStatus());
	}

	public static void marshalEC2SecurityGroups(XMLNode parent,
			List<EC2SecurityGroup> ec2SecurityGroups) {
		XMLNode nodeGroups = QueryUtil.addNode(parent, RDS_Constants.NODE_EC2SECURITYGROUPS);
		if (ec2SecurityGroups != null)
		{
			for (EC2SecurityGroup sg : ec2SecurityGroups) {
				XMLNode ec2secgrp = QueryUtil.addNode(nodeGroups, RDS_Constants.NODE_EC2SECURITYGROUP);
				QueryUtil.addNode(ec2secgrp, RDS_Constants.NODE_EC2SECURITYGROUPOWNERID, sg.getEC2SecurityGroupOwnerId());
				QueryUtil.addNode(ec2secgrp, RDS_Constants.NODE_EC2SECURITYGROUPNAME, sg.getEC2SecurityGroupName());
				QueryUtil.addNode(ec2secgrp, RDS_Constants.NODE_STATUS, sg.getStatus());
			}
		}
	}

	public static void marshalIPRanges(XMLNode parent, List<IPRange> ipRanges) {
		XMLNode nodeIPS = QueryUtil.addNode(parent, RDS_Constants.NODE_IPRANGES);
		for (IPRange r: ipRanges) {
			XMLNode nodeIP = QueryUtil.addNode(nodeIPS, RDS_Constants.NODE_IPRANGE);
			QueryUtil.addNode(nodeIP, RDS_Constants.NODE_CIDRIP, r.getCIDRIP());
			QueryUtil.addNode(nodeIP, RDS_Constants.NODE_STATUS, r.getStatus());
		}
	}

	public static void marshalReservedDBInstance(XMLNode parent,
			ReservedDBInstance rdbInstance) {
		QueryUtil.addNode(parent, RDS_Constants.NODE_CURRENCYCODE, rdbInstance.getCurrencyCode());
		QueryUtil.addNode(parent, RDS_Constants.NODE_DBINSTANCECLASS, rdbInstance.getDBInstanceClass());
		QueryUtil.addNode(parent, RDS_Constants.NODE_DBINSTANCECOUNT, rdbInstance.getDBInstanceCount());
		QueryUtil.addNode(parent, RDS_Constants.NODE_DURATION, rdbInstance.getDuration());
		QueryUtil.addNode(parent, RDS_Constants.NODE_FIXEDPRICE, rdbInstance.getFixedPrice());
		QueryUtil.addNode(parent, RDS_Constants.NODE_MULTIAZ, rdbInstance.getMultiAZ());
		
		// TODO: these exist in the latest SDK
		//QueryUtil.addNode(rdbisNode, RDS_Constants.NODE_OFFERINGTYPE, rdbi.getOfferingType());
		//RDSQueryUtil.marshallNode(rdbisNode, rdbi.getRecurringCharges);

		QueryUtil.addNode(parent, RDS_Constants.NODE_PRODUCTDESCRIPTION, rdbInstance.getProductDescription());
		QueryUtil.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCEID, rdbInstance.getReservedDBInstanceId());
		QueryUtil.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERINGID, rdbInstance.getReservedDBInstancesOfferingId());
		QueryUtil.addNode(parent, RDS_Constants.NODE_STARTTIME, rdbInstance.getStartTime());
		QueryUtil.addNode(parent, RDS_Constants.NODE_STATE, rdbInstance.getState());
		QueryUtil.addNode(parent, RDS_Constants.NODE_USAGEPRICE, rdbInstance.getUsagePrice());
	}

	public static void marshalReservedDBInstances(XMLNode parent,
			List<ReservedDBInstance> reservedDBInstances) {
		XMLNode rdbisNode = QueryUtil.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCES);
		for (ReservedDBInstance rdbi : reservedDBInstances) {
			RDSQueryUtil.marshalReservedDBInstance(rdbisNode, rdbi);
		}
	}

	public static void marshalParameters(XMLNode parent, List<Parameter> parameters) {
		final XMLNode nl = QueryUtil.addNode(parent, RDS_Constants.NODE_PARAMETERS);
		for (Parameter p : parameters) {
			final XMLNode n = QueryUtil.addNode(nl, RDS_Constants.NODE_PARAMETER);
			QueryUtil.addNode(n, RDS_Constants.NODE_ALLOWEDVALUES, p.getAllowedValues());
			QueryUtil.addNode(n, RDS_Constants.NODE_APPLYMETHOD, p.getApplyMethod());
			QueryUtil.addNode(n, RDS_Constants.NODE_APPLYTYPE, p.getApplyType());
			QueryUtil.addNode(n, RDS_Constants.NODE_DATATYPE, p.getDataType());
			QueryUtil.addNode(n, RDS_Constants.NODE_DESCRIPTION, p.getDescription());
			QueryUtil.addNode(n, RDS_Constants.NODE_ISMODIFIABLE, "" + p.getIsModifiable());
			QueryUtil.addNode(n, RDS_Constants.NODE_MINIMUMENGINEVERSION,
					p.getMinimumEngineVersion());
			QueryUtil.addNode(n, RDS_Constants.NODE_PARAMETERNAME, p.getParameterName());
			QueryUtil.addNode(n, RDS_Constants.NODE_PARAMETERVALUE, p.getParameterValue());
			QueryUtil.addNode(n, RDS_Constants.NODE_SOURCE, p.getSource());
		}
	}

	public static void marshalAvailabilityZones(XMLNode parent,
			List<AvailabilityZone> availabilityZones) {
		final XMLNode azl = QueryUtil.addNode(parent, RDS_Constants.NODE_AVAILABILITYZONES);
		for (AvailabilityZone az : availabilityZones) {
			final XMLNode azn = QueryUtil.addNode(azl, RDS_Constants.NODE_AVAILABILITYZONE);
			QueryUtil.addNode(azn, RDS_Constants.NODE_NAME, az.getName());
		}
	}

	public static void marshalReservedDBInstance(XMLNode parent,
			ReservedDBInstancesOffering o) {
    	QueryUtil.addNode(parent, RDS_Constants.NODE_CURRENCYCODE, o.getCurrencyCode());
    	QueryUtil.addNode(parent, RDS_Constants.NODE_DBINSTANCECLASS, o.getDBInstanceClass());

    	// TODO: update aws sdk for this
    	//QueryUtil.addNode(no, RDS_Constants.NODE_DBINSTANCECOUNT, o.getDBInstanceCount());

    	QueryUtil.addNode(parent, RDS_Constants.NODE_DURATION, o.getDuration());
    	QueryUtil.addNode(parent, RDS_Constants.NODE_FIXEDPRICE, o.getFixedPrice());
    	QueryUtil.addNode(parent, RDS_Constants.NODE_MULTIAZ, o.getMultiAZ());

    	// TODO: update aws sdk for this
    	//QueryUtil.addNode(no, RDS_Constants.NODE_OFFERINGTYPE, o.getOfferingType());

    	QueryUtil.addNode(parent, RDS_Constants.NODE_PRODUCTDESCRIPTION, o.getProductDescription());

    	// TODO: update aws sdk for these
    	//QueryUtil.addNode(no, RDS_Constants.NODE_RECURRINGCHARGES, o.getRecurringCharges());
    	//QueryUtil.addNode(no, RDS_Constants.NODE_RESERVEDDBINSTANCEID, o.getReservedDBInstanceId());

    	QueryUtil.addNode(parent, RDS_Constants.NODE_RESERVEDDBINSTANCESOFFERINGID, o.getReservedDBInstancesOfferingId());

    	// TODO: update aws sdk for these
    	//QueryUtil.addNode(no, RDS_Constants.NODE_STARTTIME, o.getStartTime());
    	//QueryUtil.addNode(no, RDS_Constants.NODE_STATUS, o.getState();

    	QueryUtil.addNode(parent, RDS_Constants.NODE_USAGEPRICE, o.getUsagePrice());
	}
}
