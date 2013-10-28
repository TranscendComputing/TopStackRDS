package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeDBSecurityGroupsActionMessage.DescribeDBSecurityGroupsActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DescribeDBSecurityGroupsActionRequestUnmarshaller implements
		Unmarshaller<DescribeDBSecurityGroupsActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DescribeDBSecurityGroupsActionRequestUnmarshaller.class
					.getName());

	private static DescribeDBSecurityGroupsActionRequestUnmarshaller instance;

	public static DescribeDBSecurityGroupsActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeDBSecurityGroupsActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeDBSecurityGroupsActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final DescribeDBSecurityGroupsActionRequestMessage.Builder req =  DescribeDBSecurityGroupsActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling DescribeDBSecurityGroupsActionRequestMessage");
		req.setDbSecurityGroupName(QueryUtilV2.getString(in, "DBSecurityGroupName"));
		req.setMarker(QueryUtilV2.getString(in, "Marker"));
		req.setMaxRecords(QueryUtilV2.getInt(in, "MaxRecords", 100));
		
		return req.buildPartial();
	}
}
