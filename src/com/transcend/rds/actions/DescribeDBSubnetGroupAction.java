package com.transcend.rds.actions;
import java.util.Map;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBSubnetGroupActionMessage.DescribeDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.DescribeDBSubnetGroupActionMessage.DescribeDBSubnetGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBSubnetGroupAction
        extends
        AbstractQueuedAction<DescribeDBSubnetGroupActionRequestMessage, DescribeDBSubnetGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBSubnetGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBSubnetGroupActionResultMessage message)
            throws ErrorResponse {
        return null;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DescribeDBSubnetGroupActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        return null;
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
    		DescribeDBSubnetGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

