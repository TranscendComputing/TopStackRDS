package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DeleteDBSnapshotActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DeleteDBSnapshotActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DeleteDBSnapshotActionMessage.DeleteDBSnapshotActionRequestMessage;
import com.transcend.rds.message.DeleteDBSnapshotActionMessage.DeleteDBSnapshotActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DeleteDBSnapshotAction
        extends
        AbstractQueuedAction<DeleteDBSnapshotActionRequestMessage, DeleteDBSnapshotActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DeleteDBSnapshotAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DeleteDBSnapshotActionResultMessage message)
            throws ErrorResponse {
        return new DeleteDBSnapshotActionResultMarshaller()
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
    public DeleteDBSnapshotActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DeleteDBSnapshotActionRequestMessage requestMessage =
                DeleteDBSnapshotActionRequestUnmarshaller
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
    		DeleteDBSnapshotActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

