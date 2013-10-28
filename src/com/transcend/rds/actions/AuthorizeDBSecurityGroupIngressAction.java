package com.transcend.rds.actions;
import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.rds.model.transform.AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.AuthorizeDBSecurityGroupIngressActionResultMarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.AuthorizeDBSecurityGroupIngressActionMessage.AuthorizeDBSecurityGroupIngressActionRequestMessage;
import com.transcend.rds.message.AuthorizeDBSecurityGroupIngressActionMessage.AuthorizeDBSecurityGroupIngressActionResultMessage;
import com.yammer.metrics.core.Meter;

public class AuthorizeDBSecurityGroupIngressAction
        extends
        AbstractQueuedAction<AuthorizeDBSecurityGroupIngressActionRequestMessage, AuthorizeDBSecurityGroupIngressActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(AuthorizeDBSecurityGroupIngressAction.class.getName());

    private static Map<String, Meter> meters = initMeter("rds",
            "AuthorizeDBSecurityGroupIngressAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		AuthorizeDBSecurityGroupIngressActionResultMessage message)
            throws ErrorResponse {
        return new AuthorizeDBSecurityGroupIngressActionResultMarshaller()
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
    public AuthorizeDBSecurityGroupIngressActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final AuthorizeDBSecurityGroupIngressActionRequestMessage requestMessage =
                AuthorizeDBSecurityGroupIngressActionRequestUnmarshaller
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
    		AuthorizeDBSecurityGroupIngressActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

