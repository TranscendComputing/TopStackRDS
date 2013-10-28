package com.amazonaws.services.rds.model.transform;
import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.MountDBVolumeMessage.MountDBVolumeRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.QueryUtil;


public class MountDBVolumeRequestUnmarshaller implements
Unmarshaller<MountDBVolumeRequestMessage, Map<String, String[]>>
{
	private static Logger logger = Appctx
	.getLogger(MountDBVolumeRequestUnmarshaller.class.getName());

	private static MountDBVolumeRequestUnmarshaller instance;

	public static MountDBVolumeRequestUnmarshaller getInstance()
	{
		if (instance == null)
		{
			instance = new MountDBVolumeRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public MountDBVolumeRequestMessage unmarshall(Map<String, String[]> in)
	
	{
		final MountDBVolumeRequestMessage.Builder req =  MountDBVolumeRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) MountDBVolumeRequestMessage");
		req.setAcId(QueryUtil.getLong(in, "AcId"));
		req.setStackId(QueryUtil.requiredString(in, "StackId"));
		req.setDevice(QueryUtil.requiredString(in, "Device"));
		req.setSnapshotId(Strings.nullToEmpty(QueryUtil.getString(in, "Snapshot")));


		return req.buildPartial();
	}
}
