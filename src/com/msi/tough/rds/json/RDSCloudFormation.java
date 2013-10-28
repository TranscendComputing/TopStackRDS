package com.msi.tough.rds.json;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.msi.tough.cf.json.CFBooleanParameter;
import com.msi.tough.cf.json.CFCommaDelimitedListParameter;
import com.msi.tough.cf.json.CFConstrainedNumberParameter;
import com.msi.tough.cf.json.CFDefaultStringParameter;
import com.msi.tough.cf.json.CFStringParameter;
import com.msi.tough.core.JsonUtil;

public class RDSCloudFormation {

	private String awsTemplateFormatVersion;
	private String description;
	private LinkedHashMap<String, Object> resources = null;
	private LinkedHashMap<String, Object> parameters = null;

	public RDSCloudFormation() {
		resources = new LinkedHashMap<String, Object>();
		parameters = new LinkedHashMap<String, Object>();
	}

	public RDSCloudFormation(final String DBInstanceID,
			final RDSDBInstResource rdsDBInstResource) {
		this();
		awsTemplateFormatVersion = "2010-09-09";
		description = "AWS Cloud Formation Template for RDS";

		// Parameters
		parameters.put("DBSnapshotIdentifier", new CFStringParameter(
				"The identifier for the DB Snapshot to restore from."));
		parameters.put("AllocatedStorage", new CFConstrainedNumberParameter(
				"The allocated storage size specified in gigabytes.", 1, 10,
				1024, "Must be between 10 and 1024GB"));
		parameters
				.put("AvailabilityZone",
						new CFStringParameter(
								"The name of the Availability Zone where the DB Instance is. Cannot be set when MultiAZ is true."));
		parameters
				.put("BackupRetentionPeriod",
						new CFStringParameter(
								"The number of days for which automatic DB Snapshots are retained."));
		parameters
				.put("DBInstanceClass",
						new CFStringParameter(
								"The name of the compute and memory capacity class of the DB Instance."));
		parameters
				.put("DBName",
						new CFDefaultStringParameter(
								"The name of the initial database of this instance that was provided at create time, if one was specified when the DB Instance was created.",
								"orcl"));
		parameters.put("DBParameterGroupName", new CFStringParameter(
				"The name of the DB Parameter Group."));
		parameters
				.put("DBSecurityGroups",
						new CFCommaDelimitedListParameter(
								"A list containing the DB Security Groups to assign to the Amazon RDS instance."));
		parameters
				.put("Engine",
						new CFStringParameter(
								"The name of the database engine to be used for this DB Instance."));
		parameters.put("EngineVersion", new CFStringParameter(
				"The version number of the database engine to use."));
		parameters.put("LicenseModel", new CFStringParameter(
				"License model information for this DB Instance."));
		parameters.put("MasterUsername", new CFStringParameter(
				"The master username for the DB Instance."));
		parameters.put("MasterUserPassword", new CFStringParameter(
				"The master password for the DB Instance."));
		parameters.put("Port", new CFDefaultStringParameter(
				"The port for the instance.", "3306"));
		parameters
				.put("PreferredBackupWindow",
						new CFStringParameter(
								"The daily time range during which automated backups are created if automated backups are enabled, as determined by the BackupRetentionPeriod."));
		parameters
				.put("PreferredMaintenanceWindow",
						new CFStringParameter(
								"The weekly time range (in UTC) during which system maintenance can occur."));
		parameters.put("MultiAZ", new CFBooleanParameter(
				"Specifies if the DB Instance is a Multi-AZ deployment.",
				"false"));

		// Resources
		resources.put(DBInstanceID, rdsDBInstResource);

	}

	@JsonProperty("AWSTemplateFormatVersion")
	public String getAWSTemplateFormatVersion() {
		return awsTemplateFormatVersion;
	}

	@JsonProperty("Description")
	public String getDescription() {
		return description;
	}

	@JsonProperty("Parameters")
	public LinkedHashMap<String, Object> getParameters() {
		return parameters;
	}

	@JsonProperty("Resources")
	public LinkedHashMap<String, Object> getResources() {
		return resources;
	}

	public void setAwsTemplateFormatVersion(
			final String awsTemplateFormatVersion) {
		this.awsTemplateFormatVersion = awsTemplateFormatVersion;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setParameters(final LinkedHashMap<String, Object> parameters) {
		this.parameters = parameters;
	}

	public void setResources(final LinkedHashMap<String, Object> resources) {
		this.resources = resources;
	}

	public String toJson() throws JsonGenerationException,
			JsonMappingException, IOException {
		return JsonUtil.toJsonString(this);
	}

}
