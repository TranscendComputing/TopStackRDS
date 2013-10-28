package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.SignalCreateDBSnapshotMessage.SignalCreateDBSnapshotRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;


public class SignalCreateDBSnapshotRequestUnmarshaller implements
Unmarshaller<SignalCreateDBSnapshotRequestMessage, Map<String, String[]>>
{
	private static Logger logger = Appctx
	.getLogger(SignalCreateDBSnapshotRequestUnmarshaller.class.getName());

	private static SignalCreateDBSnapshotRequestUnmarshaller instance;

	public static SignalCreateDBSnapshotRequestUnmarshaller getInstance()
	{
		if (instance == null)
		{
			instance = new SignalCreateDBSnapshotRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public SignalCreateDBSnapshotRequestMessage unmarshall(Map<String, String[]> in)
	
	{
		final SignalCreateDBSnapshotRequestMessage.Builder req =  SignalCreateDBSnapshotRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) SignalCreateDBSnapshotRequestMessage");
		req.setDbInstanceIdentifier(QueryUtil.requiredString(in, "DBInstanceIdentifier"));
		req.setOriginalDBInstanceIdentifier(QueryUtil.getString(in, "OriginalDBInstanceIdentifier"));
		req.setDbSnapshotIdentifier(QueryUtil.requiredString(in, "DBSnapshotIdentifier"));
		req.setAccountIdentifier(QueryUtil.getInt(in, "AccountIdentifier"));
	    
		return req.buildPartial();
	}
}
