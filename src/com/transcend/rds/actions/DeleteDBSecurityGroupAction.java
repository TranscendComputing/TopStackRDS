package com.transcend.rds.actions;
import java.util.Map;

import com.amazonaws.services.rds.model.transform.DeleteDBSecurityGroupActionRequestUnmarshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.rdsquery.RDS_Constants;
import com.transcend.rds.message.DeleteDBSecurityGroupActionMessage.DeleteDBSecurityGroupActionRequestMessage;
import com.transcend.rds.message.DeleteDBSecurityGroupActionMessage.DeleteDBSecurityGroupActionResultMessage;
import com.yammer.metrics.core.Meter;

public class DeleteDBSecurityGroupAction
        extends
        AbstractQueuedAction<DeleteDBSecurityGroupActionRequestMessage, DeleteDBSecurityGroupActionResultMessage> {

    private static Map<String, Meter> meters = initMeter("rds",
            "DeleteDBSecurityGroupAction");

    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.msi.tough.query.AbstractProxyAction#marshall(com.msi.tough.query.
	 * AbstractProxyAction.MarshallStruct,
	 * javax.servlet.http.HttpServletResponse)
	 */
	public String marshall(ServiceResponse resp,
    		DeleteDBSecurityGroupActionResultMessage message) throws ErrorResponse {

		final XMLNode nodeResponse = new XMLNode(
				RDS_Constants.NODE_DELETEDBSECURITYGROUPRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);

		// Setup the response metadata record
		QueryUtil.addResponseMetadata(nodeResponse, message.getRequestId());

		return nodeResponse.toString();
	}
    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.AbstractQueuedAction#handleRequest(com.msi.tough.
     * query.ServiceRequest, com.msi.tough.query.ServiceRequestContext)
     */
    @Override
    public DeleteDBSecurityGroupActionRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final DeleteDBSecurityGroupActionRequestMessage requestMessage =
                DeleteDBSecurityGroupActionRequestUnmarshaller
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
    		DeleteDBSecurityGroupActionResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

