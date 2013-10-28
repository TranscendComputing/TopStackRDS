package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DeleteDBParameterGroupActionMessage.DeleteDBParameterGroupActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DeleteDBParameterGroupActionRequestUnmarshaller implements
		Unmarshaller<DeleteDBParameterGroupActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DeleteDBParameterGroupActionRequestUnmarshaller.class
					.getName());

	private static DeleteDBParameterGroupActionRequestUnmarshaller instance;

	public static DeleteDBParameterGroupActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteDBParameterGroupActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DeleteDBParameterGroupActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final DeleteDBParameterGroupActionRequestMessage.Builder req =  DeleteDBParameterGroupActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DeleteDBParameterGroupActionRequestMessage");
		req.setDbParameterGroupName(QueryUtilV2.getString(in, "DBParameterGroupName"));
		return req.buildPartial();
	}
}
