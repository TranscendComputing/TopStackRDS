package com.amazonaws.services.rds.model.transform;

import java.math.BigInteger;
import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeDBSnapshotsActionMessage.DescribeDBSnapshotsActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeDBSnapshotsActionRequestUnmarshaller implements
		Unmarshaller<DescribeDBSnapshotsActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DescribeDBSnapshotsActionRequestUnmarshaller.class.getName());

	private static DescribeDBSnapshotsActionRequestUnmarshaller instance;

	public static DescribeDBSnapshotsActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DescribeDBSnapshotsActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeDBSnapshotsActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		DescribeDBSnapshotsActionRequestMessage.Builder req =  DescribeDBSnapshotsActionRequestMessage.newBuilder(); 
		logger.debug("Unmarshalling (inbound) DescribeDBSnapshotsActionRequestMessage");
		req.setDbInstanceIdentifier(QueryUtilV2.getString(in,
				RDS_Constants.NODE_DBINSTANCEIDENTIFIER));
		req.setDbSnapshotIdentifier(QueryUtilV2.getString(in,
				RDS_Constants.NODE_DBSNAPSHOTIDENTIFIER));
		req.setMarker(QueryUtilV2.getString(in, RDS_Constants.NODE_MARKER));
		req.setMaxRecords(QueryUtilV2.getInt(in, RDS_Constants.MAXRECORDS));
		
		return req.buildPartial();
	}
}
