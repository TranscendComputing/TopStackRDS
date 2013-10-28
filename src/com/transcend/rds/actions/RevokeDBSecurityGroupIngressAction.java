package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.RevokeDBSecurityGroupIngressActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.RevokeDBSecurityGroupIngressActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.RevokeDBSecurityGroupIngressActionMessage.RevokeDBSecurityGroupIngressActionRequestMessage;
import com.transcend.rds.message.RevokeDBSecurityGroupIngressActionMessage.RevokeDBSecurityGroupIngressActionResultMessage;
import com.yammer.metrics.core.Meter;

public class RevokeDBSecurityGroupIngressAction
        extends
        AbstractQueuedAction<RevokeDBSecurityGroupIngressActionRequestMessage, RevokeDBSecurityGroupIngressActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "RevokeDBSecurityGroupIngressAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		RevokeDBSecurityGroupIngressActionResultMessage message)
            throws ErrorResponse {
        return new RevokeDBSecurityGroupIngressActionResultMarshaller()
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
    public RevokeDBSecurityGroupIngressActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final RevokeDBSecurityGroupIngressActionRequestMessage requestMessage =
                RevokeDBSecurityGroupIngressActionRequestUnmarshaller
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
    		RevokeDBSecurityGroupIngressActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

