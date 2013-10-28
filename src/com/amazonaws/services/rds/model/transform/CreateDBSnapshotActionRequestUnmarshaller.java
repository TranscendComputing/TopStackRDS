package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.CreateDBSnapshotActionMessage.CreateDBSnapshotActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.utils.RDSQueryFaults;

public class CreateDBSnapshotActionRequestUnmarshaller implements
		Unmarshaller<CreateDBSnapshotActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(CreateDBSnapshotActionRequestUnmarshaller.class.getName());

	private static CreateDBSnapshotActionRequestUnmarshaller instance;

	public static CreateDBSnapshotActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateDBSnapshotActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public CreateDBSnapshotActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final CreateDBSnapshotActionRequestMessage.Builder req =  CreateDBSnapshotActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) CreateDBSnapshotActionRequestMessage");
		req.setDbInstanceIdentifier(in.get("DBInstanceIdentifier")[0]);
		req.setDbSnapshotIdentifier(in.get("DBSnapshotIdentifier")[0]);
		if(req.getDbInstanceIdentifier() == null || req.getDbSnapshotIdentifier() == null) {
			throw RDSQueryFaults.MissingParameter();
		}
		return req.buildPartial();
	}
}
