package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionRequestMessage;
import com.msi.tough.core.Appctx;

public class ModifyDBInstanceActionRequestUnmarshaller implements
    Unmarshaller<ModifyDBInstanceActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(ModifyDBInstanceActionRequestUnmarshaller.class.getName());

    private static ModifyDBInstanceActionRequestUnmarshaller instance;

    public static ModifyDBInstanceActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new ModifyDBInstanceActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public ModifyDBInstanceActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final ModifyDBInstanceActionRequestMessage.Builder req =  ModifyDBInstanceActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) ModifyDBInstanceActionRequestMessage");

        // TODO: unmarshall the ActionRequestMessage

        return req.buildPartial();
    }
}
