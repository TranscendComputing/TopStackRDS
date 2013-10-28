package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeDBParameterGroupsActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeDBParameterGroupsActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBParameterGroupsActionMessage.DescribeDBParameterGroupsActionRequestMessage;
import com.transcend.rds.message.DescribeDBParameterGroupsActionMessage.DescribeDBParameterGroupsActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBParameterGroupsAction
        extends
        AbstractQueuedAction<DescribeDBParameterGroupsActionRequestMessage, DescribeDBParameterGroupsActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBParameterGroupsAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBParameterGroupsActionResultMessage message)
            throws ErrorResponse {
        return new DescribeDBParameterGroupsActionResultMarshaller()
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
    public DescribeDBParameterGroupsActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeDBParameterGroupsActionRequestMessage requestMessage =
                DescribeDBParameterGroupsActionRequestUnmarshaller
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
    		DescribeDBParameterGroupsActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

