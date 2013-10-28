package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DeleteDBSnapshotActionMessage.DeleteDBSnapshotActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DeleteDBSnapshotActionRequestUnmarshaller implements
		Unmarshaller<DeleteDBSnapshotActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DeleteDBSnapshotActionRequestUnmarshaller.class.getName());

	private static DeleteDBSnapshotActionRequestUnmarshaller instance;

	public static DeleteDBSnapshotActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteDBSnapshotActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DeleteDBSnapshotActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final DeleteDBSnapshotActionRequestMessage.Builder req =  DeleteDBSnapshotActionRequestMessage.newBuilder();

		logger.debug("Unmarshalling (inbound) DeleteDBSnapshotActionRequestMessage");
		req.setDbSnapshotIdentifier(QueryUtilV2.getString(in, "DBSnapshotIdentifier"));
		return req.buildPartial();
	}
}
