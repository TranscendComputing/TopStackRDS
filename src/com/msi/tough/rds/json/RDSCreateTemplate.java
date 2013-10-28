package com.msi.tough.rds.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.hibernate.Session;

import com.msi.tough.core.JsonUtil;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;

public class RDSCreateTemplate {

	public static String getTemplate(final Session sess,
			final RdsDbinstance instRec) {
		String jsonTemplate = null;

		// final List<RDSDBSecGroupResource> securityGroupResources = new
		// ArrayList<RDSDBSecGroupResource>();
		final ArrayList<LinkedHashMap<String, Object>> rdsSecurityGroupNames = new ArrayList<LinkedHashMap<String, Object>>();
		for (final RdsDbsecurityGroup secGrp : instRec.getSecurityGroups()) {
			final LinkedHashMap<String, Object> securityGroup = JsonUtil
					.toSingleHash("Ref", secGrp.getDbsecurityGroupName());
			rdsSecurityGroupNames.add(securityGroup);
		}

		final RDSDBInstResource rdsCFDBInstResource = new RDSDBInstResource();
		rdsCFDBInstResource.setDefaultProperties();

		// Wrapper object for entire JSON Template serialization
		final RDSCloudFormation ocf = new RDSCloudFormation(
				instRec.getDbinstanceId(), rdsCFDBInstResource);

		try {
			jsonTemplate = ocf.toJson();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return jsonTemplate;

	}

}
