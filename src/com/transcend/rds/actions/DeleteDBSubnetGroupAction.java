package com.transcend.rds.actions;
import java.util.Map;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DeleteDBSubnetGroupActionMessage.DeleteDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.DeleteDBSubnetGroupActionMessage.DeleteDBSubnetGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DeleteDBSubnetGroupAction
        extends
        AbstractQueuedAction<DeleteDBSubnetGroupActionRequestMessage, DeleteDBSubnetGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DeleteDBSubnetGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DeleteDBSubnetGroupActionResultMessage message)
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
    public DeleteDBSubnetGroupActionRequestMessage handleRequest(
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
    		DeleteDBSubnetGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

