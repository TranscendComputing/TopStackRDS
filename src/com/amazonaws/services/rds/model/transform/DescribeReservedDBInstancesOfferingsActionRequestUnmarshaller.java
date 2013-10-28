package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.DescribeReservedDBInstancesOfferingsActionMessage.DescribeReservedDBInstancesOfferingsActionRequestMessage;
import com.msi.tough.core.Appctx;

public class DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller implements
    Unmarshaller<DescribeReservedDBInstancesOfferingsActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller.class.getName());

    private static DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller instance;

    public static DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public DescribeReservedDBInstancesOfferingsActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final DescribeReservedDBInstancesOfferingsActionRequestMessage.Builder req =  DescribeReservedDBInstancesOfferingsActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) DescribeReservedDBInstancesOfferingsActionRequestMessage");

        // TODO: unmarshall the ActionRequestMessage

        return req.buildPartial();
    }
}
