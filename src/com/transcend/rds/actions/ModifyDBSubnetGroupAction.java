package com.transcend.rds.actions;
import java.util.Map;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.ModifyDBSubnetGroupActionMessage.ModifyDBSubnetGroupActionRequestMessage;
import com.transcend.rds.message.ModifyDBSubnetGroupActionMessage.ModifyDBSubnetGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class ModifyDBSubnetGroupAction
        extends
        AbstractQueuedAction<ModifyDBSubnetGroupActionRequestMessage, ModifyDBSubnetGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "ModifyDBSubnetGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		ModifyDBSubnetGroupActionResultMessage message)
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
    public ModifyDBSubnetGroupActionRequestMessage handleRequest(
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
    		ModifyDBSubnetGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

