package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.ResetDBParameterGroupActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.ResetDBParameterGroupActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.ResetDBParameterGroupActionMessage.ResetDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.ResetDBParameterGroupActionMessage.ResetDBParameterGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class ResetDBParameterGroupAction
        extends
        AbstractQueuedAction<ResetDBParameterGroupActionRequestMessage, ResetDBParameterGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "ResetDBParameterGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		ResetDBParameterGroupActionResultMessage message)
            throws ErrorResponse {
        return new ResetDBParameterGroupActionResultMarshaller()
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
    public ResetDBParameterGroupActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final ResetDBParameterGroupActionRequestMessage requestMessage =
                ResetDBParameterGroupActionRequestUnmarshaller
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
    		ResetDBParameterGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

