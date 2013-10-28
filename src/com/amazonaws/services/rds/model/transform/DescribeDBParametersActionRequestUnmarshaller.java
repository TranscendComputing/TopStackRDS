package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeDBParametersActionMessage.DescribeDBParametersActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.rds.RDSUtilities;

public class DescribeDBParametersActionRequestUnmarshaller implements
Unmarshaller<DescribeDBParametersActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
	.getLogger(DescribeDBParametersActionRequestUnmarshaller.class.getName());

	private static DescribeDBParametersActionRequestUnmarshaller instance;

	public static DescribeDBParametersActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeDBParametersActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeDBParametersActionRequestMessage unmarshall(Map<String, String[]> in)
	 {
		final DescribeDBParametersActionRequestMessage.Builder req =  DescribeDBParametersActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DescribeDBParametersActionRequestMessage");
		req.setDbParameterGroupName(QueryUtilV2.getString(in, RDS_Constants.NODE_DBPARAMETERGROUPNAME));
		req.setMarker(QueryUtilV2.getString(in, Constants.MARKER));
		req.setSource(QueryUtilV2.getString(in, Constants.SOURCE));
		req.setMaxRecords(QueryUtilV2.getInt(in, Constants.MAXRECORDS, 100));
		return req.buildPartial();
	}
}
