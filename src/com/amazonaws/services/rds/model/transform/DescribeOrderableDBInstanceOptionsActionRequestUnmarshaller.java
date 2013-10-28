package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeOrderableDBInstanceOptionsActionMessage.DescribeOrderableDBInstanceOptionsActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.msi.tough.core.Appctx;

public class DescribeOrderableDBInstanceOptionsActionRequestUnmarshaller
    implements
    Unmarshaller<DescribeOrderableDBInstanceOptionsActionRequestMessage, Map<String, String[]>>
{

    private static Logger logger = Appctx
        .getLogger(DescribeOrderableDBInstanceOptionsActionRequestUnmarshaller.class
            .getName());

    private static DescribeOrderableDBInstanceOptionsActionRequestUnmarshaller instance;

    public static DescribeOrderableDBInstanceOptionsActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new DescribeOrderableDBInstanceOptionsActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public DescribeOrderableDBInstanceOptionsActionRequestMessage unmarshall(
        Map<String, String[]> in) 
    {
        final DescribeOrderableDBInstanceOptionsActionRequestMessage.Builder req =
             DescribeOrderableDBInstanceOptionsActionRequestMessage.newBuilder();

        logger
            .debug("Unmarshalling (inbound) DescribeOrderableDBInstanceOptionsActionRequestMessage");

        // TODO: unmarshall the ActionRequestMessage

        return req.buildPartial();
    }
}
