package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.ModifyDBInstanceActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.ModifyDBInstanceActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionRequestMessage;
import com.transcend.rds.message.ModifyDBInstanceActionMessage.ModifyDBInstanceActionResultMessage;
import com.yammer.metrics.core.Meter;

public class ModifyDBInstanceAction
        extends
        AbstractQueuedAction<ModifyDBInstanceActionRequestMessage, ModifyDBInstanceActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "ModifyDBInstanceAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		ModifyDBInstanceActionResultMessage message)
            throws ErrorResponse {
        return new ModifyDBInstanceActionResultMarshaller()
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
    public ModifyDBInstanceActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final ModifyDBInstanceActionRequestMessage requestMessage =
                ModifyDBInstanceActionRequestUnmarshaller
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
    		ModifyDBInstanceActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

