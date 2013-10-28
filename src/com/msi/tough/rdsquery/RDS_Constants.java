/**
 * 
 */
package com.msi.tough.rdsquery;

public final class RDS_Constants {
	public static final String DEFAULT_AVAILABILITY_ZONE = "grizzly-nova";
	
	public static final String EMPTYSTRING = "";
	public static final String AWS_EMR_NAMESPACE = "http://rds.amazonaws.com/admin/2011-04-01/";

	public static final String NODE_DESCRIPTION = "Description";
	public static final String NODE_STATUS = "Status";
	public static final String NODE_MARKER = "Marker";
	public static final String NODE_NAME = "Name";
	public static final String NODE_OWNERID = "OwnerId";
	public static final String NODE_VPCID = "VpcId";

	public static final String REQUESTID = "RequestId";
	public static final String RESPONSE_METADATA = "ResponseMetadata";

	public static final String NODE_AUTHORIZEDBSECURITYGROUPINGRESS = "AuthorizeDBSecurityGroupIngress";
	public static final String NODE_AUTHORIZEDBSECURITYGROUPINGRESSRESPONSE = "AuthorizeDBSecurityGroupIngressResponse";
	public static final String NODE_AUTHORIZEDBSECURITYGROUPINGRESSRESULT = "AuthorizeDBSecurityGroupIngressResult";

	public static final String NODE_CREATEDBINSTANCE = "CreateDBInstance";
	public static final String NODE_CREATEDBINSTANCERESPONSE = "CreateDBInstanceResponse";
	public static final String NODE_CREATEDBINSTANCERESULT = "CreateDBInstanceResult";
	public static final String NODE_INSTANCECREATETIME = "InstanceCreateTime";
	public static final String NODE_MASTERUSERNAME = "MasterUsername";
	public static final String NODE_MASTERUSERPASSWORD = "MasterUserPassword";
	public static final String NODE_PARAMETERAPPLYSTATUS = "ParameterApplyStatus";

	public static final String NODE_CREATEDBINSTANCEREADREPLICA = "CreateDBInstanceReadReplica";
	public static final String NODE_CREATEDBINSTANCEREADREPLICARESPONSE = "CreateDBInstanceReadReplicaResponse";
	public static final String NODE_READREPLICADBINSTANCEIDENTIFIERS = "ReadReplicaDBInstanceIdentifiers";
	public static final String NODE_READREPLICADBINSTANCEIDENTIFIER = "ReadReplicaDBInstanceIdentifier";

	public static final String NODE_DBPARAMETERGROUP = "DBParameterGroup";
	public static final String NODE_DBPARAMETERGROUPS = "DBParameterGroups";
	public static final String NODE_DBPARAMETERGROUPFAMILY = "DBParameterGroupFamily";
	public static final String NODE_DBPARAMETERGROUPNAME = "DBParameterGroupName";

	public static final String NODE_CREATEDBPARAMETERGROUP = "CreateDBParameterGroup";
	public static final String NODE_CREATEDBPARAMETERGROUPRESPONSE = "CreateDBParameterGroupResponse";
	public static final String NODE_CREATEDBPARAMETERGROUPRESULT = "CreateDBParameterGroupResult";

	public static final String NODE_CREATEDBSECURITYGROUP = "CreateDBSecurityGroup";
	public static final String NODE_CREATEDBSECURITYGROUPRESPONSE = "CreateDBSecurityGroupResponse";
	public static final String NODE_CREATEDBSECURITYGROUPRESULT = "CreateDBSecurityGroupResult";

	public static final String NODE_DBSECURITYGROUP = "DBSecurityGroup";
	public static final String NODE_DBSECURITYGROUPS = "DBSecurityGroups";
	public static final String NODE_DBSECURITYGROUPNAME = "DBSecurityGroupName";
	public static final String NODE_DBSECURITYGROUPDESCRIPTION = "DBSecurityGroupDescription";

	public static final String NODE_EC2SECURITYGROUPS = "EC2SecurityGroups";
	public static final String NODE_EC2SECURITYGROUP = "EC2SecurityGroup";
	public static final String NODE_EC2SECURITYGROUPID = "EC2SecurityGroupId";
	public static final String NODE_EC2SECURITYGROUPNAME = "EC2SecurityGroupName";
	public static final String NODE_EC2SECURITYGROUPOWNERID = "EC2SecurityGroupOwnerId";

	public static final String NODE_CREATEDBSNAPSHOT = "CreateDBSnapshot";
	public static final String NODE_CREATEDBSNAPSHOTRESPONSE = "CreateDBSnapshotResponse";
	public static final String NODE_CREATEDBSNAPSHOTRESULT = "CreateDBSnapshotResult";

	public static final String NODE_DBSNAPSHOT = "DBSnapshot";
	public static final String NODE_DBSNAPSHOTS = "DBSnapshots";
	public static final String NODE_DBSNAPSHOTIDENTIFIER = "DBSnapshotIdentifier";
	public static final String NODE_SNAPSHOTCREATETIME = "SnapshotCreateTime";
	public static final String NODE_LICENSEMODEL = "LicenseModel";
	public static final String NODE_SNAPSHOTTYPE = "SnapshotType";

	public static final String NODE_DELETEDBINSTANCE = "DeleteDBInstance";
	public static final String NODE_DELETEDBINSTANCERESPONSE = "DeleteDBInstanceResponse";
	public static final String NODE_DELETEDBINSTANCERESULT = "DeleteDBInstanceResult";

	public static final String NODE_DELETEDBPARAMETERGROUP = "DeleteDBParameterGroup";
	public static final String NODE_DELETEDBPARAMETERGROUPRESPONSE = "DeleteDBParameterGroupResponse";

	public static final String NODE_DELETEDBSECURITYGROUP = "DeleteDBSecurityGroup";
	public static final String NODE_DELETEDBSECURITYGROUPRESPONSE = "DeleteDBSecurityGroupResponse";

	public static final String NODE_DELETEDBSNAPSHOT = "DeleteDBSnapshot";
	public static final String NODE_DELETEDBSNAPSHOTRESPONSE = "DeleteDBSnapshotResponse";
	public static final String NODE_DELETEDBSNAPSHOTRESULT = "DeleteDBSnapshotResult";

	public static final String NODE_DESCRIBEDBENGINEVERSIONS = "DescribeDBEngineVersions";
	public static final String NODE_DESCRIBEDBENGINEVERSIONSRESPONSE = "DescribeDBEngineVersionsResponse";
	public static final String NODE_DBENGINEVERSIONSRESULT = "DescribeDBEngineVersionsResult";
	public static final String NODE_DBENGINEVERSION = "DBEngineVersion";
	public static final String NODE_DBENGINEVERSIONS = "DBEngineVersions";

	public static final String NODE_DBINSTANCE = "DBInstance";
	public static final String NODE_DBINSTANCES = "DBInstances";
	public static final String NODE_DESCRIBEDBINSTANCES = "DescribeDBInstances";
	public static final String NODE_DESCRIBEDBINSTANCESRESPONSE = "DescribeDBInstancesResponse";
	public static final String NODE_DESCRIBEDBINSTANCESRESULT = "DescribeDBInstancesResult";

	public static final String NODE_ALLOCATEDSTORAGE = "AllocatedStorage";
	public static final String NODE_AUTOMINORVERSIONUPGRADE = "AutoMinorVersionUpgrade";
	public static final String NODE_AVAILABILITYZONE = "AvailabilityZone";
	public static final String NODE_AVAILABILITYZONES = "AvailabilityZones";
	public static final String NODE_BACKUPRETENTIONPERIOD = "BackupRetentionPeriod";
	public static final String NODE_PREFERREDBACKUPWINDOW = "PreferredBackupWindow";
	public static final String NODE_DBINSTANCECLASS = "DBInstanceClass";
	public static final String NODE_DBINSTANCEIDENTIFIER = "DBInstanceIdentifier";
	public static final String NODE_DBNAME = "DBName";
	public static final String NODE_DBINSTANCESTATUS = "DBInstanceStatus";
	public static final String NODE_ENDPOINT = "Endpoint";
	public static final String NODE_ADDRESS = "Address";
	public static final String NODE_PORT = "Port";
	public static final String NODE_PENDINGMODIFIEDVALUES = "PendingModifiedValues";

	public static final String NODE_DESCRIBEDBPARAMETERGROUPS = "DescribeDBParameterGroups";
	public static final String NODE_DESCRIBEDBPARAMETERGROUPSRESPONSE = "DescribeDBParameterGroupsResponse";
	public static final String NODE_DESCRIBEDBPARAMETERGROUPSRESULT = "DescribeDBParameterGroupsResult";

	public static final String NODE_DESCRIBEDBPARAMETERS = "DescribeDBParameters";
	public static final String NODE_DESCRIBEDBPARAMETERSRESPONSE = "DescribeDBParametersResponse";
	public static final String NODE_DESCRIBEDBPARAMETERSRESULT = "DescribeDBParametersResult";
	public static final String NODE_PARAMETERS = "Parameters";
	public static final String NODE_PARAMETER = "Parameter";
	public static final String NODE_ALLOWEDVALUES = "AllowedValues";
	public static final String NODE_APPLYMETHOD = "ApplyMethod";
	public static final String NODE_APPLYTYPE = "ApplyType";
	public static final String NODE_DATATYPE = "DataType";
	public static final String NODE_ISMODIFIABLE = "IsModifiable";
	public static final String NODE_MINIMUMENGINEVERSION = "MinimumEngineVersion";
	public static final String NODE_PARAMETERNAME = "ParameterName";
	public static final String NODE_PARAMETERVALUE = "ParameterValue";
	public static final String NODE_SOURCE = "Source";

	public static final String NODE_DESCRIBEDBSECURITYGROUPS = "DescribeDBSecurityGroups";
	public static final String NODE_DESCRIBEDBSECURITYGROUPSRESPONSE = "DescribeDBSecurityGroupsResponse";
	public static final String NODE_DESCRIBEDBSECURITYGROUPSRESULT = "DescribeDBSecurityGroupsResult";

	public static final String NODE_DESCRIBEDBSNAPSHOTS = "DescribeDBSnapshots";
	public static final String NODE_DESCRIBEDBSNAPSHOTSRESPONSE = "DescribeDBSnapshotsResponse";
	public static final String NODE_DESCRIBEDBSNAPSHOTSRESULT = "DescribeDBSnapshotsResult";

	public static final String NODE_DESCRIBEENGINEDEFAULTPARAMETERS = "DescribeEngineDefaultParameters";
	public static final String NODE_DESCRIBEENGINEDEFAULTPARAMETERSRESPONSE = "DescribeEngineDefaultParametersResponse";
	public static final String NODE_DESCRIBEENGINEDEFAULTPARAMETERSRESULT = "DescribeEngineDefaultParametersResult";

	public static final String NODE_DESCRIBEEVENTS = "DescribeEvents";
	public static final String NODE_DESCRIBEEVENTSRESPONSE = "DescribeEventsResponse";
	public static final String NODE_DESCRIBEEVENTSRESULT = "DescribeEventsResult";
	public static final String EVENTS = "Events";
	public static final String EVENT = "Event";
	public static final String MESSAGE = "Message";
	public static final String SOURCETYPE = "SourceType";
	public static final String SOURCEIDENTIFIER = "SourceIdentifier";
	public static final String DATE = "Date";	

	public static final String NODE_DESCRIBEORDERABLEDBINSTANCEOPTIONS = "DescribeOrderableDBInstanceOptions";
	public static final String NODE_DESCRIBEORDERABLEDBINSTANCEOPTIONSRESPONSE = "DescribeOrderableDBInstanceOptionsResponse";
	public static final String NODE_DESCRIBEORDERABLEDBINSTANCEOPTIONSRESULT = "DescribeOrderableDBInstanceOptionsResult";

	public static final String NODE_DESCRIBERESERVEDDBINSTANCES = "DescribeReservedDBInstances";
	public static final String NODE_DESCRIBERESERVEDDBINSTANCESRESPONSE = "DescribeReservedDBInstancesResponse";
	public static final String NODE_DESCRIBERESERVEDDBINSTANCESRESULT = "DescribeReservedDBInstancesResult";

	public static final String NODE_DESCRIBEDBRESERVEDDBINSTANCESRESULT = "DescribeReservedDBInstancesResult";
	public static final String NODE_DESCRIBEDBRESERVEDDBINSTANCESRESULTRESPONSE = "DescribeReservedDBInstancesResultResponse";
	public static final String NODE_DESCRIBEDBRESERVEDDBINSTANCESRESULTRESULT = "DescribeReservedDBInstancesResultResult";

	public static final String NODE_DESCRIBERESERVEDDBINSTANCESOFFERINGS = "DescribeReservedDBInstancesOfferings";
	public static final String NODE_DESCRIBERESERVEDDBINSTANCESOFFERINGSRESPONSE = "DescribeReservedDBInstancesOfferingsResponse";
	public static final String NODE_DESCRIBERESERVEDDBINSTANCESOFFERINGSRESULT = "DescribeReservedDBInstancesOfferingsResult";

	public static final String NODE_MODIFYDBINSTANCE = "ModifyDBInstance";
	public static final String NODE_MODIFYDBINSTANCERESPONSE = "ModifyDBInstanceResponse";
	public static final String NODE_MODIFYDBINSTANCERESULT = "ModifyDBInstanceResult";

	public static final String NODE_MODIFYDBPARAMETERGROUP = "ModifyDBParameterGroup";
	public static final String NODE_MODIFYDBPARAMETERGROUPRESPONSE = "ModifyDBParameterGroupResponse";
	public static final String NODE_MODIFYDBPARAMETERGROUPRESULT = "ModifyDBParameterGroupResult";

	public static final String NODE_PURCHASERESERVEDDBINSTANCESOFFERING = "PurchaseReservedDBInstancesOffering";
	public static final String NODE_PURCHASERESERVEDDBINSTANCESOFFERINGRESPONSE = "PurchaseReservedDBInstancesOfferingResponse";
	public static final String NODE_PURCHASERESERVEDDBINSTANCESOFFERINGRESULT = "PurchaseReservedDBInstancesOfferingResult";

	public static final String NODE_REBOOTDBINSTANCE = "RebootDBInstance";
	public static final String NODE_REBOOTDBINSTANCERESPONSE = "RebootDBInstanceResponse";
	public static final String NODE_REBOOTDBINSTANCERESULT = "RebootDBInstanceResult";

	public static final String NODE_RESETDBPARAMETERGROUP = "ResetDBParameterGroup";
	public static final String NODE_RESETDBPARAMETERGROUPRESPONSE = "ResetDBParameterGroupResponse";
	public static final String NODE_RESETDBPARAMETERGROUPRESULT = "ResetDBParameterGroupResult";

	public static final String NODE_RESTOREDBINSTANCEFROMDBSNAPSHOT = "RestoreDBInstanceFromDBSnapshot";
	public static final String NODE_RESTOREDBINSTANCEFROMDBSNAPSHOTRESPONSE = "RestoreDBInstanceFromDBSnapshotResponse";
	public static final String NODE_RESTOREDBINSTANCEFROMDBSNAPSHOTRESULT = "RestoreDBInstanceFromDBSnapshotResult";

	public static final String NODE_RESTOREDBINSTANCETOPOINTINTIME = "RestoreDBInstanceToPointInTime";
	public static final String NODE_RESTOREDBINSTANCETOPOINTINTIMERESPONSE = "RestoreDBInstanceToPointInTimeResponse";
	public static final String NODE_RESTOREDBINSTANCETOPOINTINTIMERESULT = "RestoreDBInstanceToPointInTimeResult";

	public static final String NODE_REVOKEDBSECURITYGROUPINGRESS = "RevokeDBSecurityGroupIngress";
	public static final String NODE_REVOKEDBSECURITYGROUPINGRESSRESPONSE = "RevokeDBSecurityGroupIngressResponse";
	public static final String NODE_REVOKEDBSECURITYGROUPINGRESSRESULT = "RevokeDBSecurityGroupIngressResult";

	public static final String NODE_ENGINE = "Engine";
	public static final String NODE_ENGINEVERSION = "EngineVersion";
	public static final String INSTANCE_CREATETIME = "InstanceCreateTime";
	public static final String NODE_LATESTRESTORABLETIME = "LatestRestorableTime";
	public static final String NODE_MULTIAZ = "MultiAZ";
	public static final String PREFERREDBACKUPWINDOW = "PreferredBackupWindow";
	public static final String NODE_PREFERREDMAINTENANCEWINDOW = "PreferredMaintenanceWindow";
	public static final String NODE_READREPLICASOURCEDBINSTANCEIDENTIFIER = "ReadReplicaSourceDBInstanceIdentifier";

	public static final String NODE_IPRANGES = "IPRanges";
	public static final String NODE_IPRANGE = "IPRange";
	public static final String NODE_CIDRIP = "CIDRIP";

	public static final String NODE_RESERVEDDBINSTANCES = "ReservedDBInstances";
	public static final String NODE_CURRENCYCODE = "CurrencyCode";
	public static final String NODE_DBINSTANCECOUNT = "DBInstanceCount";
	public static final String NODE_DURATION = "Duration";
	public static final String NODE_FIXEDPRICE = "FixedPrice";
	public static final String NODE_OFFERINGTYPE = "OfferingType";
	public static final String NODE_PRODUCTDESCRIPTION = "ProductDescription";
	public static final String NODE_RESERVEDDBINSTANCEID = "ReservedDBInstanceId";
	public static final String NODE_RESERVEDDBINSTANCESOFFERINGID = "ReservedDBInstancesOfferingId";
	public static final String NODE_STARTTIME = "StartTime";
	public static final String NODE_STATE = "State";
	public static final String NODE_USAGEPRICE = "UsagePrice";

	public static final String NODE_ENGINEDEFAULTS = "EngineDefaults";

	public static final String NODE_ORDERABLEDBINSTANCEOPTIONS = "OrderableDBInstanceOptions";
	public static final String NODE_ORDERABLEDBINSTANCEOPTION = "OrderableDBInstanceOption";
	public static final String NODE_MULTIAZCAPABLE = "MultiAZCapable";
	public static final String NODE_READREPLICACAPABLE = "ReadReplicaCapable";
	public static final String NODE_VPCCAPABLE = "VpcCapable";
	public static final String NODE_VPCMULTIAZCAPABLE = "VpcMultiAZCapable";
	public static final String NODE_VPCREADREPLICACAPABLE = "VpcReadReplicaCapable";

	public static final String NODE_RESERVEDDBINSTANCESOFFERINGS = "ReservedDBInstancesOfferings";
	public static final String NODE_RESERVEDDBINSTANCESOFFERING = "ReservedDBInstancesOffering";
	public static final String NODE_RECURRINGCHARGES = "RecurringCharges";

	public static final String MAXRECORDS = "MaxRecords";

	public static final String RDS_REPLICATION_PASSWORD = "fv3t20jas39130fjvf93";
	public static final String RDS_REPLICATION_USER = "rds_replica";

	public static final String FINALDBSNAPSHOTIDENTIFIER = "FinalDBSnapshotIdentifier";

	public static final String LICENSEMODEL = "LicenseModel";

	
}
