package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeDBEngineVersionsActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeDBEngineVersionsActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBEngineVersionsActionMessage.DescribeDBEngineVersionsActionRequestMessage;
import com.transcend.rds.message.DescribeDBEngineVersionsActionMessage.DescribeDBEngineVersionsActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBEngineVersionsAction
        extends
        AbstractQueuedAction<DescribeDBEngineVersionsActionRequestMessage, DescribeDBEngineVersionsActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBEngineVersionsAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBEngineVersionsActionResultMessage message)
            throws ErrorResponse {
        return new DescribeDBEngineVersionsActionResultMarshaller()
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
    public DescribeDBEngineVersionsActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeDBEngineVersionsActionRequestMessage requestMessage =
                DescribeDBEngineVersionsActionRequestUnmarshaller
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
    		DescribeDBEngineVersionsActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

