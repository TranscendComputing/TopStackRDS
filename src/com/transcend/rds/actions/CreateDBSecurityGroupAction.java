package com.transcend.rds.actions;
import java.util.Map;

import org.slf4j.Logger;

import com.amazonaws.services.rds.model.transform.CreateDBSecurityGroupActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.CreateDBSecurityGroupActionResultMarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.CreateDBSecurityGroupActionMessage.CreateDBSecurityGroupActionRequestMessage;
import com.transcend.rds.message.CreateDBSecurityGroupActionMessage.CreateDBSecurityGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateDBSecurityGroupAction
        extends
        AbstractQueuedAction<CreateDBSecurityGroupActionRequestMessage, CreateDBSecurityGroupActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(CreateDBSecurityGroupAction.class.getName());

    private static Map<String, Meter> meters = initMeter("rds",
            "CreateDBSecurityGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		CreateDBSecurityGroupActionResultMessage message)
            throws ErrorResponse {
        return new CreateDBSecurityGroupActionResultMarshaller()
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
    public CreateDBSecurityGroupActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final CreateDBSecurityGroupActionRequestMessage requestMessage =
                CreateDBSecurityGroupActionRequestUnmarshaller
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
    		CreateDBSecurityGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

