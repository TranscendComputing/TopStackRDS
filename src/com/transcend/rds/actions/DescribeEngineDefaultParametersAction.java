package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.DescribeEngineDefaultParametersActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.DescribeEngineDefaultParametersActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.DescribeEngineDefaultParametersActionMessage.DescribeEngineDefaultParametersActionRequestMessage;
import com.transcend.rds.message.DescribeEngineDefaultParametersActionMessage.DescribeEngineDefaultParametersActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DescribeEngineDefaultParametersAction
        extends
        AbstractQueuedAction<DescribeEngineDefaultParametersActionRequestMessage, DescribeEngineDefaultParametersActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DescribeEngineDefaultParametersAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		DescribeEngineDefaultParametersActionResultMessage message)
            throws ErrorResponse {
        return new DescribeEngineDefaultParametersActionResultMarshaller()
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
    public DescribeEngineDefaultParametersActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DescribeEngineDefaultParametersActionRequestMessage requestMessage =
                DescribeEngineDefaultParametersActionRequestUnmarshaller
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
    		DescribeEngineDefaultParametersActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

