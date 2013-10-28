package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.PurchaseReservedDBInstancesOfferingActionMessage.PurchaseReservedDBInstancesOfferingActionRequestMessage;
import com.msi.tough.core.Appctx;

public class PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller implements
    Unmarshaller<PurchaseReservedDBInstancesOfferingActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller.class.getName());

    private static PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller instance;

    public static PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public PurchaseReservedDBInstancesOfferingActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final PurchaseReservedDBInstancesOfferingActionRequestMessage.Builder req =  PurchaseReservedDBInstancesOfferingActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) PurchaseReservedDBInstancesOfferingActionRequestMessage");

        // TODO: unmarshall the ActionRequestMessage

        return req.buildPartial();
    }
}
