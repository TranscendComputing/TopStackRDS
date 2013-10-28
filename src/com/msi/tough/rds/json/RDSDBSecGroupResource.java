package com.msi.tough.rds.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.Session;

import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;

public class RDSDBSecGroupResource {

	private String type;
	private LinkedHashMap<String, Object> properties = null;
	private String name;

	public RDSDBSecGroupResource(final Session sess, final String name,
			final String description, final RdsDbsecurityGroup rdsDbSec) {
		properties = new LinkedHashMap<String, Object>();
		this.name = name;
		type = "AWS::RDS::DBSecurityGroup";
		properties.put("GroupDescription", description);
		final ArrayList<LinkedHashMap<String, Object>> ingress = new ArrayList<LinkedHashMap<String, Object>>();
		for (final RdsEC2SecurityGroupBean ec2SecGrp : rdsDbSec
				.getEC2SecGroupBean(sess)) {
			final LinkedHashMap<String, Object> ec2groups = new LinkedHashMap<String, Object>();
			ec2groups.put("EC2SecurityGroupName", ec2SecGrp.getName());
			ec2groups.put("EC2SecurityGroupOwnerId", ec2SecGrp.getOwnId());
			ingress.add(ec2groups);
		}
		for (final RdsIPRangeBean ipRange : rdsDbSec.getIPRange(sess)) {
			final LinkedHashMap<String, Object> ipRanges = new LinkedHashMap<String, Object>();
			ipRanges.put("CIDRIP", ipRange.getCidrip());
			ingress.add(ipRanges);
		}
		properties.put("DBSecurityGroupIngress", ingress);
	}

	@JsonIgnore
	public String getName() {
		return name;
	}

	@JsonProperty("Properties")
	public LinkedHashMap<String, Object> getProperties() {
		return properties;
	}

	@JsonProperty("Type")
	public String getType() {
		return type;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setProperties(final LinkedHashMap<String, Object> properties) {
		this.properties = properties;
	}

	public void setProperties(final String description) {
		properties.put("Description", description);
	}

	public void setType(final String type) {
		this.type = type;
	}
}
