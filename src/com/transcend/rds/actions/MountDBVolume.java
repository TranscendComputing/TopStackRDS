package com.transcend.rds.actions;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.rds.model.transform.MountDBVolumeRequestUnmarshaller;
import com.amazonaws.services.rds.model.transform.MountDBVolumeResultMarshaller;
import com.msi.tough.core.Appctx;
import com.msi.tough.model.AccountBean;
import com.msi.tough.query.AbstractQueuedAction;
import com.msi.tough.query.ActionHelper;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ProtobufUtil;
import com.msi.tough.query.ServiceRequest;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.query.ServiceResponse;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.workflow.core.Workflow;
import com.transcend.rds.message.MountDBVolumeMessage.MountDBVolumeRequestMessage;
import com.transcend.rds.message.MountDBVolumeMessage.MountDBVolumeResultMessage;
import com.yammer.metrics.core.Meter;

public class MountDBVolume
        extends
        AbstractQueuedAction<MountDBVolumeRequestMessage, MountDBVolumeResultMessage> {
    @Autowired
    private ActionHelper actionHelper;
  
    @Resource
    private Workflow workflow;
    
    private static Map<String, Meter> meters = initMeter("rds",
            "MountDBVolume");
    
	private final static Logger logger = Appctx
			.getLogger(MountDBVolume.class.getName());
	
	
	/*
    awsAccessKeyId = req.getParameter("AWSAccessKeyId");
    if (awsAccessKeyId != null) {
        accountBean = AccountUtil.readAccount(s, awsAccessKeyId);
        if (accountBean != null) {
            accountId = accountBean.getId();
        }
    }
    */

    /**
     * Validate that the request has minimal required information to be sane.
     *
     * @param req
     */
    @Override
    protected void validate(final ServiceRequest req,
            ServiceRequestContext context) {
        String awsAccessKeyId = req.getParameter("AWSAccessKeyId");
        setUseContextSession(false);
        if (awsAccessKeyId != null) {
	        context.setAwsAccessKeyId(awsAccessKeyId);
	        AccountBean accountBean = AccountUtil.readAccount(getSession(), awsAccessKeyId);
            if (accountBean != null) {
		        context.setAccountBean(accountBean);
		        context.setAccountId(accountBean.getId());
            }
        }
        
        actionHelper.validate(req, context, this, false);
    }
  
    
    /*
     * (non-Javadoc)
     *
     * @see
     * com.msi.tough.query.QueuedAction#process(com.google.protobuf.Message,
     * com.google.protobuf.Message)
     */   
    @Override
    public void process(ServiceRequest req, ServiceResponse resp)
            throws ErrorResponse {
       
        ServiceRequestContext context = new ServiceRequestContext();
        context.setRequestId(req.getRequestId());
        validate(req, context);
        MountDBVolumeRequestMessage message = this.handleRequest(req, context);
        logger.debug("Built message: " + message.getClass().getName());
        message = ProtobufUtil.setRequiredField(message, "typeId", true);
        message = ProtobufUtil.setRequiredField(message, "requestId",
                "requestId");
        message = ProtobufUtil.setRequiredField(message, "callerAccessKey",
                "awsKey");
        workflow.doWork(message, context);
    }


    
    @Override
    protected void mark(Object ret, Exception e) {
        markStandard(meters, e);
    }

    public String marshall(ServiceResponse resp,
    		MountDBVolumeResultMessage message)
            throws ErrorResponse {
        return new MountDBVolumeResultMarshaller()
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
    public MountDBVolumeRequestMessage handleRequest(
            ServiceRequest req, ServiceRequestContext context)
            throws ErrorResponse {
        final MountDBVolumeRequestMessage requestMessage =
                MountDBVolumeRequestUnmarshaller
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
    		MountDBVolumeResultMessage message) {
        resp.setPayload(marshall(resp, message));
        return resp;
    }
}

