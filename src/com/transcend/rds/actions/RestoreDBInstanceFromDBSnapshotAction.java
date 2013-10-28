package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.RestoreDBInstanceFromDBSnapshotActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionRequestMessage;
import com.transcend.rds.message.RestoreDBInstanceFromDBSnapshotActionMessage.RestoreDBInstanceFromDBSnapshotActionResultMessage;
import com.yammer.metrics.core.Meter;

public class RestoreDBInstanceFromDBSnapshotAction
        extends
        AbstractQueuedAction<RestoreDBInstanceFromDBSnapshotActionRequestMessage, RestoreDBInstanceFromDBSnapshotActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "RestoreDBInstanceFromDBSnapshotAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		RestoreDBInstanceFromDBSnapshotActionResultMessage message)
            throws ErrorResponse {
        return new RestoreDBInstanceFromDBSnapshotActionResultMarshaller()
                .marshall(message);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public RestoreDBInstanceFromDBSnapshotActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final RestoreDBInstanceFromDBSnapshotActionRequestMessage requestMessage =
                RestoreDBInstanceFromDBSnapshotActionRequestUnmarshaller
                .getInstance().unmarshall(req.getParameterMap());

        return requestMessage;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#buildResponse(com.msi.tough.
     * query.ServiceResponse, com.google.protobuf.Message)
     */
    @Override
    public ServiceResponse buildResponse(ServiceResponse resp,
    		RestoreDBInstanceFromDBSnapshotActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

