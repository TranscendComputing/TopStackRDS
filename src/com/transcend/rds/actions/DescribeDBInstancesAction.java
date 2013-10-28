package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeDBInstancesActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeDBInstancesActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBInstancesActionMessage.DescribeDBInstancesActionRequestMessage;
import com.transcend.rds.message.DescribeDBInstancesActionMessage.DescribeDBInstancesActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBInstancesAction
        extends
        AbstractQueuedAction<DescribeDBInstancesActionRequestMessage, DescribeDBInstancesActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBInstancesAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBInstancesActionResultMessage message)
            throws ErrorResponse {
        return new DescribeDBInstancesActionResultMarshaller()
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
    public DescribeDBInstancesActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeDBInstancesActionRequestMessage requestMessage =
                DescribeDBInstancesActionRequestUnmarshaller
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
    		DescribeDBInstancesActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

