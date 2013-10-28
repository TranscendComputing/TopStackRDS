package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeEngineDefaultParametersActionMessage.DescribeEngineDefaultParametersActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DescribeEngineDefaultParametersActionRequestUnmarshaller implements
Unmarshaller<DescribeEngineDefaultParametersActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
	.getLogger(DescribeEngineDefaultParametersActionRequestUnmarshaller.class
			.getName());

	private static DescribeEngineDefaultParametersActionRequestUnmarshaller instance;

	public static DescribeEngineDefaultParametersActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeEngineDefaultParametersActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeEngineDefaultParametersActionRequestMessage unmarshall(Map<String, String[]> in)
	 {
		final DescribeEngineDefaultParametersActionRequestMessage.Builder req =  DescribeEngineDefaultParametersActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DescribeEngineDefaultParametersActionRequestMessage");
		req.setDbParameterGroupFamily(QueryUtilV2.requiredString(in, "DBParameterGroupFamily"));
		req.setMarker(QueryUtilV2.getString(in, "Marker"));
		req.setMaxRecords(QueryUtilV2.getInt(in, "MaxRecords", 100));
		return req.buildPartial();
	}
}
