/**
 * 
 */
package com.transcend.rds.worker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.aws.ec2.Volume;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ProtobufUtil;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.MountDBVolumeMessage.MountDBVolumeRequestMessage;
import com.transcend.rds.message.MountDBVolumeMessage.MountDBVolumeResultMessage;

/**
 * @author dkim@momenumsi.com
 */
public class MountDBVolumeWorker extends
		AbstractWorker<MountDBVolumeRequestMessage, MountDBVolumeResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(MountDBVolumeWorker.class.getName());
	
	   
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public MountDBVolumeResultMessage doWork(
           MountDBVolumeRequestMessage req) throws Exception {
       logger.debug("Performing work for MountDBVolume.");      
       String accessKey;
       String requestId;
       AccountBean account;
       Session session = getSession();
       accessKey = (String) ProtobufUtil.getRequiredField(req, "callerAccessKey");
       assert(accessKey != null);
       requestId = (String) ProtobufUtil.getRequiredField(req, "requestId");
       assert(requestId != null);
       try {
           if ("SYSTEM".equals(accessKey)) {
               account = AccountUtil.readAccount(session, 1L);
           } else {
               account = AccountUtil.readAccount(session, accessKey);
           }
       }
       catch (Exception e) {
           mark(null, e);
           logger.error("Failed to load account.", e);
           ErrorResponse er = ErrorResponse.InternalFailure();
           er.setRequestId(requestId);
           throw er;
       }
       ServiceRequestContext context = new ServiceRequestContext();
       context.setAccountBean(account);
       try
       {
    	   MountDBVolumeResultMessage result = doWork0(req, context);
           result = ProtobufUtil.setRequiredField(result, "typeId", true);
           mark(result, null);
           return result;
       }
       catch (ErrorResponse e) {
           mark(null, e);
           e.setRequestId(requestId);
           throw e;
       }
       catch (Exception e) {
           mark(null, e);
           logger.error("Failed to perform work.", e);
           ErrorResponse er = ErrorResponse.InternalFailure();
           er.setRequestId(requestId);
           throw er;
       }
   }
   
	@Override
	protected MountDBVolumeResultMessage doWork0(MountDBVolumeRequestMessage req,
			ServiceRequestContext context) throws Exception {
		final long acId = req.getAcId();
		final String stackId    = req.getStackId();
		final String device     = req.getDevice();
		final String snapshotId = req.getSnapshotId();
		final Session session   = HibernateUtil.newSession();
	
		logger.debug("MOUNT DB VOLUME: " + "AccountId = " + acId + "; StackId = " + stackId);
		
		AccountBean ac = AccountUtil.readAccount(session, acId);
		AccountType at = AccountUtil.toAccount(ac);
		
		String targetVolId = null;
		String instanceId = null;
		
		List<ResourcesBean> rList = CFUtil.selectResourceRecordsOfType(session, acId, stackId, null, com.msi.tough.engine.aws.rds.DBInstance.TYPE);
		if(rList == null || rList.size() != 1){
			logger.debug("There should be only one resource of this type with the given stack id.");
			// TODO signal fail hook for the stackID
		}
		ResourcesBean r = rList.get(0);
		String dbInstId = r.getPhysicalId();
		
		RdsDbinstance instance = InstanceEntity.selectDBInstance(session, dbInstId, acId);
		if(instance == null || "".equals(instance)){
			logger.debug("DBInstance record could not be found!");
			// TODO signal fail hook for the stackID
		}
		instanceId = instance.getInstanceId();
		
		if(snapshotId == null || "".equals(snapshotId)){
			targetVolId = instance.getVolumeId();
		}else{
			RdsSnapshot snapshot = SnapshotEntity.selectSnapshot(session, dbInstId, snapshotId, acId);
			if(snapshot == null){
				// TODO signal fail hook for the DBSnapshot
			}
			targetVolId = snapshot.getVolumeId();
		}
		
		logger.debug("MOUNT DB VOLUME: Target volume = " + targetVolId + "; Instance = " + instanceId + "; Device = " + device);
		CallStruct call = new CallStruct();
		call.setAc(at);
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("InstanceId", instanceId);
		props.put("VolumeId", targetVolId);
		props.put("Device", device);
		call.setProperties(props);
		Volume.attach(call);
		//return "ACK 200";
		MountDBVolumeResultMessage.Builder resp = 
				MountDBVolumeResultMessage.newBuilder();
		return resp.buildPartial();
	}   
}
