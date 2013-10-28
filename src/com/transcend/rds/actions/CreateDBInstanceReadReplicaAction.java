package com.transcend.rds.actions;
import java.util.Map;
import com.amazonaws.services.rds.model.transform.CreateDBInstanceReadReplicaActionRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.CreateDBInstanceReadReplicaActionResultMarshaller;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.transcend.rds.message.CreateDBInstanceReadReplicaActionMessage.CreateDBInstanceReadReplicaActionRequestMessage;
import com.transcend.rds.message.CreateDBInstanceReadReplicaActionMessage.CreateDBInstanceReadReplicaActionResultMessage;
import com.yammer.metrics.core.Meter;

public class CreateDBInstanceReadReplicaAction
        extends
        AbstractQueuedAction<CreateDBInstanceReadReplicaActionRequestMessage, CreateDBInstanceReadReplicaActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "CreateDBInstanceReadReplicaAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		CreateDBInstanceReadReplicaActionResultMessage message)
            throws ErrorResponse {
        return new CreateDBInstanceReadReplicaActionResultMarshaller()
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
    public CreateDBInstanceReadReplicaActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final CreateDBInstanceReadReplicaActionRequestMessage requestMessage =
                CreateDBInstanceReadReplicaActionRequestUnmarshaller
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
    		CreateDBInstanceReadReplicaActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

