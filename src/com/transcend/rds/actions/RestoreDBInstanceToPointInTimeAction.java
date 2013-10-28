package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.RestoreDBInstanceToPointInTimeActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.RestoreDBInstanceToPointInTimeActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.RestoreDBInstanceToPointInTimeActionMessage.RestoreDBInstanceToPointInTimeActionRequestMessage;
import com.transcend.rds.message.RestoreDBInstanceToPointInTimeActionMessage.RestoreDBInstanceToPointInTimeActionResultMessage;
import com.yammer.metrics.core.Meter;

public class RestoreDBInstanceToPointInTimeAction
        extends
        AbstractQueuedAction<RestoreDBInstanceToPointInTimeActionRequestMessage, RestoreDBInstanceToPointInTimeActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "RestoreDBInstanceToPointInTimeAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		RestoreDBInstanceToPointInTimeActionResultMessage message)
            throws ErrorResponse {
        return new RestoreDBInstanceToPointInTimeActionResultMarshaller()
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
    public RestoreDBInstanceToPointInTimeActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final RestoreDBInstanceToPointInTimeActionRequestMessage requestMessage =
                RestoreDBInstanceToPointInTimeActionRequestUnmarshaller
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
    		RestoreDBInstanceToPointInTimeActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

