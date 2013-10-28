package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeDBInstancesActionMessage.DescribeDBInstancesActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DescribeDBInstancesActionRequestUnmarshaller implements
Unmarshaller<DescribeDBInstancesActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
	.getLogger(DescribeDBInstancesActionRequestUnmarshaller.class.getName());

	private static DescribeDBInstancesActionRequestUnmarshaller instance;

	public static DescribeDBInstancesActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeDBInstancesActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeDBInstancesActionRequestMessage unmarshall(Map<String, String[]> in)
	 {
		final DescribeDBInstancesActionRequestMessage.Builder req =  DescribeDBInstancesActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DescribeDBInstancesActionRequestMessage");
		req.setDbInstanceIdentifier(QueryUtilV2.getString(in, "DBInstanceIdentifier"));
		req.setMarker(QueryUtilV2.getString(in, "Marker"));
		req.setMaxRecords(QueryUtilV2.getInt(in, "MaxRecords", 100));
		return req.buildPartial();
	}
}
