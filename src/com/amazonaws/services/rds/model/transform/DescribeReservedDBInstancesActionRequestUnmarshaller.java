package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.DescribeReservedDBInstancesActionMessage.DescribeReservedDBInstancesActionRequestMessage;
import com.msi.tough.core.Appctx;

public class DescribeReservedDBInstancesActionRequestUnmarshaller implements
    Unmarshaller<DescribeReservedDBInstancesActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(DescribeReservedDBInstancesActionRequestUnmarshaller.class.getName());

    private static DescribeReservedDBInstancesActionRequestUnmarshaller instance;

    public static DescribeReservedDBInstancesActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DescribeReservedDBInstancesActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public DescribeReservedDBInstancesActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final DescribeReservedDBInstancesActionRequestMessage.Builder req =  DescribeReservedDBInstancesActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) DescribeReservedDBInstancesActionRequestMessage");

        // TODO: unmarshall the ActionRequestMessage

        return req.buildPartial();
    }
}
