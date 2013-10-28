package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DeleteDBInstanceActionMessage.DeleteDBInstanceActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DeleteDBInstanceActionRequestUnmarshaller implements
		Unmarshaller<DeleteDBInstanceActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DeleteDBInstanceActionRequestUnmarshaller.class.getName());

	private static DeleteDBInstanceActionRequestUnmarshaller instance;

	public static DeleteDBInstanceActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteDBInstanceActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DeleteDBInstanceActionRequestMessage unmarshall(final Map<String, String[]> in)
			 {
		final DeleteDBInstanceActionRequestMessage.Builder req =  DeleteDBInstanceActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DeleteDBInstanceActionRequestMessage");
		req.setDbInstanceIdentifier(QueryUtilV2.getString(in,
				"DBInstanceIdentifier"));
		req.setFinalDBSnapshotIdentifier(QueryUtilV2.getString(in,
				"FinalDBSnapshotIdentifier"));
		req.setSkipFinalSnapshot(QueryUtilV2.getBoolean(in, "SkipFinalSnapshot"));
		return req.buildPartial();
	}
}
