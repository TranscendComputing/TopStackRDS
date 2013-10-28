package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeDBEngineVersionsActionMessage.DescribeDBEngineVersionsActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DescribeDBEngineVersionsActionRequestUnmarshaller implements
Unmarshaller<DescribeDBEngineVersionsActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
	.getLogger(DescribeDBEngineVersionsActionRequestUnmarshaller.class
			.getName());

	private static DescribeDBEngineVersionsActionRequestUnmarshaller instance;

	public static DescribeDBEngineVersionsActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeDBEngineVersionsActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeDBEngineVersionsActionRequestMessage unmarshall(Map<String, String[]> in)
	 {
		final DescribeDBEngineVersionsActionRequestMessage.Builder req =  DescribeDBEngineVersionsActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DescribeDBEngineVersionsActionRequestMessage");
		req.setDbParameterGroupFamily(QueryUtilV2.getString(in, "DBParameterGroupFamily"));
		req.setDefaultOnly(QueryUtilV2.getBoolean(in, "DefaultOnly", false));
		req.setEngine(QueryUtilV2.getString(in, "Engine"));
		req.setEngineVersion(QueryUtilV2.getString(in, "EngineVersion"));
		req.setMarker(QueryUtilV2.getString(in, "Marker"));
		req.setMaxRecords(QueryUtilV2.getInt(in, "MaxRecords", 20));

		return req.buildPartial();
	}
}
