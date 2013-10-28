package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeDBSnapshotsActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeDBSnapshotsActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeDBSnapshotsActionMessage.DescribeDBSnapshotsActionRequestMessage;
import com.transcend.rds.message.DescribeDBSnapshotsActionMessage.DescribeDBSnapshotsActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeDBSnapshotsAction
        extends
        AbstractQueuedAction<DescribeDBSnapshotsActionRequestMessage, DescribeDBSnapshotsActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeDBSnapshotsAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeDBSnapshotsActionResultMessage message)
            throws ErrorResponse {
        return new DescribeDBSnapshotsActionResultMarshaller()
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
    public DescribeDBSnapshotsActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeDBSnapshotsActionRequestMessage requestMessage =
                DescribeDBSnapshotsActionRequestUnmarshaller
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
    		DescribeDBSnapshotsActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

