package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeDBParameterGroupsActionMessage.DescribeDBParameterGroupsActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DescribeDBParameterGroupsActionRequestUnmarshaller implements
Unmarshaller<DescribeDBParameterGroupsActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
	.getLogger(DescribeDBParameterGroupsActionRequestUnmarshaller.class
			.getName());

	private static DescribeDBParameterGroupsActionRequestUnmarshaller instance;

	public static DescribeDBParameterGroupsActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeDBParameterGroupsActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeDBParameterGroupsActionRequestMessage unmarshall(Map<String, String[]> in)
	 {
		final DescribeDBParameterGroupsActionRequestMessage.Builder req =  DescribeDBParameterGroupsActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DescribeDBParameterGroupsActionRequestMessage");
		req.setDbParameterGroupName(QueryUtilV2.getString(in, "DBParameterGroupName"));
		req.setMarker(QueryUtilV2.getString(in, "Marker"));
		req.setMaxRecords(Integer.valueOf(QueryUtilV2.getInt(in, "MaxRecords", 100)));
		return req.buildPartial();
	}
}
