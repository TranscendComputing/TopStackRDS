package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.CreateDBSecurityGroupActionMessage.CreateDBSecurityGroupActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class CreateDBSecurityGroupActionRequestUnmarshaller implements
		Unmarshaller<CreateDBSecurityGroupActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(CreateDBSecurityGroupActionRequestUnmarshaller.class.getName());

	private static CreateDBSecurityGroupActionRequestUnmarshaller instance;

	public static CreateDBSecurityGroupActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateDBSecurityGroupActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public CreateDBSecurityGroupActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final CreateDBSecurityGroupActionRequestMessage.Builder req =  CreateDBSecurityGroupActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling CreateDBSecurityGroup");
		req.setDbSecurityGroupDescription(QueryUtilV2.getString(in, "DBSecurityGroupDescription"));
		req.setDbSecurityGroupName(QueryUtilV2.getString(in, "DBSecurityGroupName"));
		return req.buildPartial();
	}
}
