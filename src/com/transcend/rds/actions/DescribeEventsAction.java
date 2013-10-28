package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeEventsActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeEventsActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeEventsActionMessage.DescribeEventsActionRequestMessage;
import com.transcend.rds.message.DescribeEventsActionMessage.DescribeEventsActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeEventsAction
        extends
        AbstractQueuedAction<DescribeEventsActionRequestMessage, DescribeEventsActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeEventsAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeEventsActionResultMessage message)
            throws ErrorResponse {
        return new DescribeEventsActionResultMarshaller()
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
    public DescribeEventsActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeEventsActionRequestMessage requestMessage =
                DescribeEventsActionRequestUnmarshaller
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
    		DescribeEventsActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

