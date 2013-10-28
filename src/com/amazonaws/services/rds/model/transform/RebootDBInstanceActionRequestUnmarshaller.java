package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.RebootDBInstanceActionMessage.RebootDBInstanceActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class RebootDBInstanceActionRequestUnmarshaller implements
		Unmarshaller<RebootDBInstanceActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(RebootDBInstanceActionRequestUnmarshaller.class.getName());

	private static RebootDBInstanceActionRequestUnmarshaller instance;

	public static RebootDBInstanceActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new RebootDBInstanceActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public RebootDBInstanceActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final RebootDBInstanceActionRequestMessage.Builder req =  RebootDBInstanceActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) RebootDBInstanceActionRequestMessage");
		req.setDbInstanceIdentifier(QueryUtilV2.getString(in, RDS_Constants.NODE_DBINSTANCEIDENTIFIER));
		return req.buildPartial();
	}
}
