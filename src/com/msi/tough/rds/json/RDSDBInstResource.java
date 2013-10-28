package com.msi.tough.rds.json;

import java.util.LinkedHashMap;
import org.codehaus.jackson.annotate.JsonProperty;
import com.msi.tough.core.JsonUtil;

public class RDSDBInstResource {

	private String type;
	private LinkedHashMap<String,Object> properties = null;

	public RDSDBInstResource(){
		properties = new LinkedHashMap<String, Object>();
		type = "AWS::RDS::DBInstance";
	}
	
	public void setType( String type ) { this.type = type; }
	public void setProperties( LinkedHashMap<String,Object> properties) { this.properties = properties; }

	@JsonProperty("Type")
	public String getType() { return type;} 

	@JsonProperty("Properties")
	public LinkedHashMap<String, Object> getProperties() { return properties; }

	public void setDefaultProperties(){

		//addRefParameter( properties, "DBSnapshotIdentifier");
		
		addRefParameter( properties, "AllocatedStorage");
		addRefParameter( properties, "AvailabilityZone");
		addRefParameter( properties, "BackupRetentionPeriod");
		addRefParameter( properties, "DBInstanceClass");
		addRefParameter( properties, "DBName");
		addRefParameter( properties, "DBParameterGroupName") ;
		addRefParameter( properties, "DBSecurityGroups");
		addRefParameter( properties, "Engine");
		addRefParameter( properties, "EngineVersion");
		addRefParameter( properties, "LicenseModel");
		addRefParameter( properties, "MasterUsername");
		addRefParameter( properties, "MasterUserPassword");
		addRefParameter( properties, "Port");
		addRefParameter( properties, "PreferredBackupWindow");
		addRefParameter( properties, "PreferredMaintenanceWindow");
		addRefParameter( properties, "MultiAZ");
	}
	
	private void addRefParameter( LinkedHashMap<String,Object> properties, String parameter ){
		properties.put(parameter, JsonUtil.toSingleHash("Ref", parameter));
	}
}
