package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.CreateDBInstanceReadReplicaActionMessage.CreateDBInstanceReadReplicaActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.Constants;

public class CreateDBInstanceReadReplicaActionRequestUnmarshaller implements
		Unmarshaller<CreateDBInstanceReadReplicaActionRequestMessage, Map<String, String[]>> {
	private static Logger logger = Appctx
			.getLogger(CreateDBInstanceReadReplicaActionRequestUnmarshaller.class
					.getName());

	private static CreateDBInstanceReadReplicaActionRequestUnmarshaller instance;

	public static CreateDBInstanceReadReplicaActionRequestUnmarshaller getInstance() {
		if (instance == null) {
			instance = new CreateDBInstanceReadReplicaActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public CreateDBInstanceReadReplicaActionRequestMessage unmarshall(Map<String, String[]> in)
			 {
		final CreateDBInstanceReadReplicaActionRequestMessage.Builder req =  CreateDBInstanceReadReplicaActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) CreateDBInstanceReadReplicaActionRequestMessage");
		// TODO handle AutoMinorVersionUpgrade validation + default value setting here
		req.setAutoMinorVersionUpgrade(QueryUtilV2.getBoolean(in, Constants.AUTOMINORVERSIONUPGRADE));
		req.setAvailabilityZone(QueryUtilV2.getString(in, Constants.AVAILABILITYZONE));
		req.setDbInstanceClass(QueryUtilV2.getString(in, Constants.DBINSTANCECLASS));
		req.setDbInstanceIdentifier(QueryUtilV2.getString(in, Constants.DBINSTANCEIDENTIFIER));
		req.setPort(QueryUtilV2.getInt(in, Constants.PORT, -1));
		req.setSourceDBInstanceIdentifier(QueryUtilV2.getString(in, Constants.SOURCEDBINSTANCEIDENTIFIER));
		return req.buildPartial();
	}
}
