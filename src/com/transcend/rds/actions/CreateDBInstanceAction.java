package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.CreateDBInstanceActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.CreateDBInstanceActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionRequestMessage;
import com.transcend.rds.message.CreateDBInstanceActionMessage.CreateDBInstanceActionResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateDBInstanceAction
        extends
        AbstractQueuedAction<CreateDBInstanceActionRequestMessage, CreateDBInstanceActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "CreateDBInstanceAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		CreateDBInstanceActionResultMessage message)
            throws ErrorResponse {
        return new CreateDBInstanceActionResultMarshaller()
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
    public CreateDBInstanceActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final CreateDBInstanceActionRequestMessage requestMessage =
                CreateDBInstanceActionRequestUnmarshaller
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
    		CreateDBInstanceActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

