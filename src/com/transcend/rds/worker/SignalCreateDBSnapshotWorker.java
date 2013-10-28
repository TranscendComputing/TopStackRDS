/**
 * 
 */
package com.transcend.rds.worker;

import java.util.HashMap;
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
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.Constants;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.SignalCreateDBSnapshotMessage.SignalCreateDBSnapshotRequestMessage;
import com.transcend.rds.message.SignalCreateDBSnapshotMessage.SignalCreateDBSnapshotResultMessage;

/**
 * @author dkim@momenumsi.com
 */
public class SignalCreateDBSnapshotWorker extends
		AbstractWorker<SignalCreateDBSnapshotRequestMessage, SignalCreateDBSnapshotResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(SignalCreateDBSnapshotWorker.class.getName());
	
	   
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public SignalCreateDBSnapshotResultMessage doWork(
           SignalCreateDBSnapshotRequestMessage req) throws Exception {
       logger.debug("Performing work for SignalCreateDBSnapshot.");
       return super.doWork(req, getSession());
   }
   
	@Override
	protected SignalCreateDBSnapshotResultMessage doWork0(SignalCreateDBSnapshotRequestMessage req,
			ServiceRequestContext context) throws Exception {
		// get the parameters
		final String dbInstId   = req.getDbInstanceIdentifier();
		final String odbInstId  = req.getOriginalDBInstanceIdentifier();
		final String snapshotId = req.getDbSnapshotIdentifier();
		final int    accountId  = req.getAccountIdentifier();
		final Session sess      = HibernateUtil.newSession();

		
		logger.debug("List of Parameters = \n +" + "DBInstanceIdentifier: "
				+ dbInstId + "\n" + "DBSnapshotIdentifier: "
				+ snapshotId + "\n" + "AccountId: " + accountId);
		RdsSnapshot snapshot = null;
		if (odbInstId == null) {
			snapshot = SnapshotEntity.selectSnapshot(sess,
					dbInstId, snapshotId, accountId);
		} else {
			snapshot = SnapshotEntity.selectSnapshot(sess,
					odbInstId, snapshotId,
					accountId);
		}
		final RdsDbinstance instance = InstanceEntity.selectDBInstance(
				sess, dbInstId, accountId);
		final AccountBean ac = AccountUtil.readAccount(sess, new Long(
				accountId));
		logger.debug("Snapshot: " + snapshot + "; DBInstance: " + instance
				+ "; Account: " + ac);

		if (instance == null) {
			logger.debug("DBInstance couldn't be read...");
		} else {
			logger.debug("DBInstanceIdentifier: "
					+ instance.getDbinstanceId());
		}

		final String avZone = instance.getAvailabilityZone();
		logger.debug("AvailabilityZone: " + avZone);

		logger.debug("detatching the volume");
		final String volId = snapshot.getVolumeId();
		final Volume vol = new Volume();
		final CallStruct call = new CallStruct();
		final AccountType at = new AccountType();
		at.setAccessKey(ac.getAccessKey());
		at.setSecretKey(ac.getSecretKey());
		at.setTenant(ac.getTenant());
		at.setDefZone(ac.getDefZone());
		call.setAc(at);
		final Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("VolumeId", volId);
		prop.put(Constants.AVAILABILITYZONE, avZone);
		call.setProperties(prop);
		vol.detach(call);

		logger.debug("update the status to \"available\"");
		if (odbInstId != null) {
			EventUtil.addEvent(sess, ac.getId(), "Restored from snapshot "
					+ snapshotId, "db-instance",
					new String[] { dbInstId });
			EventUtil.addEvent(sess, ac.getId(), "Restored from snapshot "
					+ snapshotId, "db-snapshot",
					new String[] { dbInstId });
			// update the address if it wasn't available earlier

		} else {
			EventUtil.addEvent(sess, ac.getId(), "User snapshot created",
					"db-snapshot", new String[] { snapshotId });
		}
		instance.setDbinstanceStatus("available");
		sess.save(instance);
		snapshot.setStatus("available");
		sess.save(snapshot);
		
		//return "Done";
		SignalCreateDBSnapshotResultMessage.Builder resp = 
				SignalCreateDBSnapshotResultMessage.newBuilder();
		return resp.buildPartial();

	}   
}
