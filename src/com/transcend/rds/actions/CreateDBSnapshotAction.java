package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.CreateDBSnapshotActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.CreateDBSnapshotActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.CreateDBSnapshotActionMessage.CreateDBSnapshotActionRequestMessage;
import com.transcend.rds.message.CreateDBSnapshotActionMessage.CreateDBSnapshotActionResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateDBSnapshotAction
        extends
        AbstractQueuedAction<CreateDBSnapshotActionRequestMessage, CreateDBSnapshotActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "CreateDBSnapshotAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		CreateDBSnapshotActionResultMessage message)
            throws ErrorResponse {
        return new CreateDBSnapshotActionResultMarshaller()
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
    public CreateDBSnapshotActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final CreateDBSnapshotActionRequestMessage requestMessage =
                CreateDBSnapshotActionRequestUnmarshaller
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
    		CreateDBSnapshotActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

