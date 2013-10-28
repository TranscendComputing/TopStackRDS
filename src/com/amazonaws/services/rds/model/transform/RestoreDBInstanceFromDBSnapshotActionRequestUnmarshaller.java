package com.amazonaws.services.rds.model.transform;

import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.transform.Unmarshaller;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionRequestMessage;
import com.msi.tough.core.Appctx;
import com.msi.tough.rdsquery.QueryUtilV2;

public class RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller implements
    Unmarshaller<RestoreDBInstanceFromDBSnapshotActionRequestMessage, Map<String, String[]>>
{
    private static Logger logger = Appctx
        .getLogger(RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller.class.getName());

    private static RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller instance;

    public static RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller getInstance()
    {
        if (instance == null)
        {
            instance = new RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller();
        }
        return instance;
    }

    /*
     * (non-Javadoc)
     * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
     */
    @Override
    public RestoreDBInstanceFromDBSnapshotActionRequestMessage unmarshall(Map<String, String[]> in)
        
    {
        final RestoreDBInstanceFromDBSnapshotActionRequestMessage.Builder req =  RestoreDBInstanceFromDBSnapshotActionRequestMessage.newBuilder();

        logger.debug("Unmarshalling (inbound) RestoreDBInstanceFromDBSnapshotActionRequestMessage");
        req.setAutoMinorVersionUpgrade(QueryUtilV2.getBoolean(in, "AutoMinorVersionUpgrade"));
        String avZone = QueryUtilV2.getString(in, "AvailabilityZone");
        req.setAvailabilityZone(avZone);
        req.setDbInstanceClass(QueryUtilV2.getString(in, "DBInstanceClass"));
        req.setDbInstanceIdentifier(QueryUtilV2.requiredString(in, "DBInstanceIdentifier"));
        req.setDbName(QueryUtilV2.getString(in, "DBName"));
        req.setDbSnapshotIdentifier(QueryUtilV2.requiredString(in, "DBSnapshotIdentifier"));
        req.setEngine(QueryUtilV2.getString(in, "Engine"));
        req.setLicenseModel(QueryUtilV2.getString(in, "LicenseModel"));
        req.setMultiAZ(QueryUtilV2.getBoolean(in, "MultiAZ"));
        req.setPort(QueryUtilV2.getInt(in, "Port", -1));

        return req.buildPartial();
    }
}
