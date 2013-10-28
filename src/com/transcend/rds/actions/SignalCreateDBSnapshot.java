package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.SignalCreateDBSnapshotRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.SignalCreateDBSnapshotResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.SignalCreateDBSnapshotMessage.SignalCreateDBSnapshotRequestMessage;
import com.transcend.rds.message.SignalCreateDBSnapshotMessage.SignalCreateDBSnapshotResultMessage;
import com.yammer.metrics.core.Meter;

public class SignalCreateDBSnapshot
        extends
        AbstractQueuedAction<SignalCreateDBSnapshotRequestMessage, SignalCreateDBSnapshotResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "SignalCreateDBSnapshot");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		SignalCreateDBSnapshotResultMessage message)
            throws ErrorResponse {
        return new SignalCreateDBSnapshotResultMarshaller()
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
    public SignalCreateDBSnapshotRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final SignalCreateDBSnapshotRequestMessage requestMessage =
                SignalCreateDBSnapshotRequestUnmarshaller
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
    		SignalCreateDBSnapshotResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

