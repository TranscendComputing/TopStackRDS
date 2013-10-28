package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.RebootDBInstanceActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.RebootDBInstanceActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.RebootDBInstanceActionMessage.RebootDBInstanceActionRequestMessage;
import com.transcend.rds.message.RebootDBInstanceActionMessage.RebootDBInstanceActionResultMessage;
import com.yammer.metrics.core.Meter;

public class RebootDBInstanceAction
        extends
        AbstractQueuedAction<RebootDBInstanceActionRequestMessage, RebootDBInstanceActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "RebootDBInstanceAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		RebootDBInstanceActionResultMessage message)
            throws ErrorResponse {
        return new RebootDBInstanceActionResultMarshaller()
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
    public RebootDBInstanceActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final RebootDBInstanceActionRequestMessage requestMessage =
                RebootDBInstanceActionRequestUnmarshaller
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
    		RebootDBInstanceActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

