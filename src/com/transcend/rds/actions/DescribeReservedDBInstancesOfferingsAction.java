package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeReservedDBInstancesOfferingsActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeReservedDBInstancesOfferingsActionMessage.DescribeReservedDBInstancesOfferingsActionRequestMessage;
import com.transcend.rds.message.DescribeReservedDBInstancesOfferingsActionMessage.DescribeReservedDBInstancesOfferingsActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeReservedDBInstancesOfferingsAction
        extends
        AbstractQueuedAction<DescribeReservedDBInstancesOfferingsActionRequestMessage, DescribeReservedDBInstancesOfferingsActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeReservedDBInstancesOfferingsAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeReservedDBInstancesOfferingsActionResultMessage message)
            throws ErrorResponse {
        return new DescribeReservedDBInstancesOfferingsActionResultMarshaller()
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
    public DescribeReservedDBInstancesOfferingsActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeReservedDBInstancesOfferingsActionRequestMessage requestMessage =
                DescribeReservedDBInstancesOfferingsActionRequestUnmarshaller
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
    		DescribeReservedDBInstancesOfferingsActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

