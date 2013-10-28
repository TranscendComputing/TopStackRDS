package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DeleteDBInstanceActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DeleteDBInstanceActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DeleteDBInstanceActionMessage.DeleteDBInstanceActionRequestMessage;
import com.transcend.rds.message.DeleteDBInstanceActionMessage.DeleteDBInstanceActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DeleteDBInstanceAction
        extends
        AbstractQueuedAction<DeleteDBInstanceActionRequestMessage, DeleteDBInstanceActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DeleteDBInstanceAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DeleteDBInstanceActionResultMessage message)
            throws ErrorResponse {
        return new DeleteDBInstanceActionResultMarshaller()
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
    public DeleteDBInstanceActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DeleteDBInstanceActionRequestMessage requestMessage =
                DeleteDBInstanceActionRequestUnmarshaller
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
    		DeleteDBInstanceActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

