package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.CreateDBParameterGroupActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.CreateDBParameterGroupActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.CreateDBParameterGroupActionMessage.CreateDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.CreateDBParameterGroupActionMessage.CreateDBParameterGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateDBParameterGroupAction
        extends
        AbstractQueuedAction<CreateDBParameterGroupActionRequestMessage, CreateDBParameterGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "CreateDBParameterGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		CreateDBParameterGroupActionResultMessage message)
            throws ErrorResponse {
        return new CreateDBParameterGroupActionResultMarshaller()
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
    public CreateDBParameterGroupActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final CreateDBParameterGroupActionRequestMessage requestMessage =
                CreateDBParameterGroupActionRequestUnmarshaller
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
    		CreateDBParameterGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

