package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeDBParametersActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeDBParametersActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBParametersActionMessage.DescribeDBParametersActionRequestMessage;
import com.transcend.rds.message.DescribeDBParametersActionMessage.DescribeDBParametersActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBParametersAction
        extends
        AbstractQueuedAction<DescribeDBParametersActionRequestMessage, DescribeDBParametersActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBParametersAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBParametersActionResultMessage message)
            throws ErrorResponse {
        return new DescribeDBParametersActionResultMarshaller()
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
    public DescribeDBParametersActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeDBParametersActionRequestMessage requestMessage =
                DescribeDBParametersActionRequestUnmarshaller
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
    		DescribeDBParametersActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

