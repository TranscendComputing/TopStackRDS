package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeReservedDBInstancesActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeReservedDBInstancesActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeReservedDBInstancesActionMessage.DescribeReservedDBInstancesActionRequestMessage;
import com.transcend.rds.message.DescribeReservedDBInstancesActionMessage.DescribeReservedDBInstancesActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeReservedDBInstancesAction
        extends
        AbstractQueuedAction<DescribeReservedDBInstancesActionRequestMessage, DescribeReservedDBInstancesActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeReservedDBInstancesAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeReservedDBInstancesActionResultMessage message)
            throws ErrorResponse {
        return new DescribeReservedDBInstancesActionResultMarshaller()
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
    public DescribeReservedDBInstancesActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeReservedDBInstancesActionRequestMessage requestMessage =
                DescribeReservedDBInstancesActionRequestUnmarshaller
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
    		DescribeReservedDBInstancesActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

