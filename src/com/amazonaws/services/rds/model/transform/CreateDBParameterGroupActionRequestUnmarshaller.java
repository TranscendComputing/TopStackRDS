package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.CreateDBParameterGroupActionMessage.CreateDBParameterGroupActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class CreateDBParameterGroupActionRequestUnmarshaller implements
		Unmarshaller<CreateDBParameterGroupActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(CreateDBParameterGroupActionRequestUnmarshaller.class
					.getName());

	private static CreateDBParameterGroupActionRequestUnmarshaller instance;

	public static CreateDBParameterGroupActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateDBParameterGroupActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public CreateDBParameterGroupActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final CreateDBParameterGroupActionRequestMessage.Builder req =  CreateDBParameterGroupActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) CreateDBParameterGroup");
		req.setDbParameterGroupFamily(QueryUtilV2.getString(in, "DBParameterGroupFamily"));
		req.setDbParameterGroupName(QueryUtilV2.getString(in, "DBParameterGroupName"));
		req.setDescription(QueryUtilV2.getString(in, "Description"));
		return req.buildPartial();
	}
}
