package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DeleteDBSecurityGroupActionMessage.DeleteDBSecurityGroupActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class DeleteDBSecurityGroupActionRequestUnmarshaller implements
		Unmarshaller<DeleteDBSecurityGroupActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(DeleteDBSecurityGroupActionRequestUnmarshaller.class.getName());

	private static DeleteDBSecurityGroupActionRequestUnmarshaller instance;

	public static DeleteDBSecurityGroupActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new DeleteDBSecurityGroupActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DeleteDBSecurityGroupActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final DeleteDBSecurityGroupActionRequestMessage.Builder req =  DeleteDBSecurityGroupActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DeleteDBSecurityGroupActionRequestMessage");
		req.setDbSecurityGroupName(QueryUtilV2.requiredString(in, "DBSecurityGroupName"));
		return req.buildPartial();
	}
}
