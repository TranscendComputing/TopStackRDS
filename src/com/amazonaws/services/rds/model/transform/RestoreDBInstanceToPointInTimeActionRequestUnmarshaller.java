package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.RestoreDBInstanceToPointInTimeActionMessage.RestoreDBInstanceToPointInTimeActionRequestMessage;
import com.msi.tough.core.Appctx;

public class RestoreDBInstanceToPointInTimeActionRequestUnmarshaller implements
    Unmarshaller<RestoreDBInstanceToPointInTimeActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(RestoreDBInstanceToPointInTimeActionRequestUnmarshaller.class.getName());

    private static RestoreDBInstanceToPointInTimeActionRequestUnmarshaller instance;

    public static RestoreDBInstanceToPointInTimeActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new RestoreDBInstanceToPointInTimeActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public RestoreDBInstanceToPointInTimeActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final RestoreDBInstanceToPointInTimeActionRequestMessage.Builder req =  RestoreDBInstanceToPointInTimeActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) RestoreDBInstanceToPointInTimeActionRequestMessage");

        // TODO: unmarshall the ActionRequestMessage

        return req.buildPartial();
    }
}
