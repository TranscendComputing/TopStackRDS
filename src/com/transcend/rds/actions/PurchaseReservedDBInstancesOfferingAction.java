package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.PurchaseReservedDBInstancesOfferingActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.PurchaseReservedDBInstancesOfferingActionMessage.PurchaseReservedDBInstancesOfferingActionRequestMessage;
import com.transcend.rds.message.PurchaseReservedDBInstancesOfferingActionMessage.PurchaseReservedDBInstancesOfferingActionResultMessage;
import com.yammer.metrics.core.Meter;

public class PurchaseReservedDBInstancesOfferingAction
        extends
        AbstractQueuedAction<PurchaseReservedDBInstancesOfferingActionRequestMessage, PurchaseReservedDBInstancesOfferingActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "PurchaseReservedDBInstancesOfferingAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		PurchaseReservedDBInstancesOfferingActionResultMessage message)
            throws ErrorResponse {
        return new PurchaseReservedDBInstancesOfferingActionResultMarshaller()
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
    public PurchaseReservedDBInstancesOfferingActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final PurchaseReservedDBInstancesOfferingActionRequestMessage requestMessage =
                PurchaseReservedDBInstancesOfferingActionRequestUnmarshaller
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
    		PurchaseReservedDBInstancesOfferingActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

