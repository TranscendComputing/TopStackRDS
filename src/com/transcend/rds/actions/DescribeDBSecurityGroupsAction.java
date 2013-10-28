package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeDBSecurityGroupsActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeDBSecurityGroupsActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBSecurityGroupsActionMessage.DescribeDBSecurityGroupsActionRequestMessage;
import com.transcend.rds.message.DescribeDBSecurityGroupsActionMessage.DescribeDBSecurityGroupsActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBSecurityGroupsAction
        extends
        AbstractQueuedAction<DescribeDBSecurityGroupsActionRequestMessage, DescribeDBSecurityGroupsActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBSecurityGroupsAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBSecurityGroupsActionResultMessage message)
            throws ErrorResponse {
        return new DescribeDBSecurityGroupsActionResultMarshaller()
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
    public DescribeDBSecurityGroupsActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeDBSecurityGroupsActionRequestMessage requestMessage =
                DescribeDBSecurityGroupsActionRequestUnmarshaller
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
    		DescribeDBSecurityGroupsActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

