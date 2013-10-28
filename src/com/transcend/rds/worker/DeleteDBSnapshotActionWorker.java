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
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.engine.aws.ec2.Volume;
import com.msi.tough.engine.core.CallStruct;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.ResourcesBean;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.CFUtil;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DeleteDBSnapshotActionMessage.DeleteDBSnapshotActionRequestMessage;
import com.transcend.rds.message.DeleteDBSnapshotActionMessage.DeleteDBSnapshotActionResultMessage;

/**
 * @author tdhite
 */
public class DeleteDBSnapshotActionWorker extends 
		AbstractWorker<DeleteDBSnapshotActionRequestMessage, DeleteDBSnapshotActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DeleteDBSnapshotActionWorker.class.getName());
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DeleteDBSnapshotActionResultMessage doWork(
           DeleteDBSnapshotActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DeleteDBSnapshotAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * deleteDBSnapshot
	 * ******************************************************************** This
	 * Operation is used to delete a DBSnapshot. The DBSnapshot must be in the
	 * "available" state to be deleted. Request: DBSnapshotIdentifier Response:
	 * DBSnapshot record giving details of the deleted snapshot Exceptions:
	 * DBSnapshotNotFound InvalidDBSnapshotState - must be available. Processing
	 * 1. Confirm that DBSnapshot exists and is in appropriate state 2. Delete
	 * snapshot 3. Delete DBSnapshot database record 4. return response to user
	 */
	@Override
	protected DeleteDBSnapshotActionResultMessage doWork0(DeleteDBSnapshotActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		DeleteDBSnapshotActionResultMessage.Builder resp = null;
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userID = ac.getId();

			final String snapshotID = req
					.getDbSnapshotIdentifier();

			logger.info("deleteSnapshot: UserID = " + userID + " SnapshotID = "
					+ snapshotID);

			// check to make sure DBSnapshotIdentifier is passed along the
			// request
			if (snapshotID == null) {
				throw QueryFaults
						.MissingParameter("DBSnapshotIdentifier must be supplied for DeleteDBSnapshot request.");
			}

			// check to see if the snapshot exists
			final RdsSnapshot snapshot = SnapshotEntity.selectSnapshot(sess,
					null, snapshotID, userID);
			if (snapshot == null) {
				throw RDSQueryFaults.DBSnapshotNotFound();
			}

			// check if snapshot is in valid state
			if ((!snapshot.getStatus().equals(RDSUtilities.STATUS_AVAILABLE)) && (!snapshot.getStatus().equals("error"))) {
				throw RDSQueryFaults
						.InvalidDBSnapshotState("The state of the DBSnapshot does not allow deletion.");
			}

			// delete the volume
			if(!snapshot.getStatus().equals("error")){
				final String volId = snapshot.getVolumeId();
				logger.debug("Deleting the volume to store the snapshot...");
				final Volume vol = new Volume();
				final CallStruct volCall = new CallStruct();
				final AccountType at = new AccountType();
				volCall.setAvailabilityZone(snapshot.getAvailabilityZone());
				at.setAccessKey(ac.getAccessKey());
				at.setSecretKey(ac.getSecretKey());
				at.setTenant(ac.getTenant());
				volCall.setAc(at);
				final Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("VolumeId", volId);
				volCall.setPhysicalId(volId);
				volCall.setProperties(properties);
				vol.delete(volCall);
				logger.debug("Volume deleted...");	
			}

			// delete the snapshot record from the database
			sess.delete(snapshot);

			// delete the resources bean for the snapshot now
			List<ResourcesBean> res =  CFUtil.selectResourceRecordsOfType(sess, userID, snapshotID, null, com.msi.tough.engine.aws.rds.DBSnapshot.TYPE);
			if(res.size() != 1){
				throw RDSQueryFaults.InternalFailure();
			}
			sess.delete(res.get(0));
			
			res = CFUtil.selectResourceRecords(sess, userID, "rds." + userID + "." + snapshot.getDbinstanceId() + ".snapshot", 
					null, snapshot.getDbinstanceId() + "_snapshot", false);
			if(res != null && res.size() == 1){
				sess.delete(res.get(0));
			}
			
			// prepare the response element
			resp = DeleteDBSnapshotActionResultMessage.newBuilder();
			resp.setAllocatedStorage(snapshot.getAllocatedStorage());
			resp.setAvailabilityZone(snapshot.getAvailabilityZone());
			resp.setDbInstanceIdentifier(snapshot.getDbinstanceId());
			resp.setDbSnapshotIdentifier(snapshot.getDbsnapshotId());
			resp.setEngine(snapshot.getEngine());
			resp.setEngineVersion(snapshot.getEngineVersion());
			resp.setInstanceCreateTime(DateHelper.getISO8601Date(snapshot.getInstanceCreatedTime()));
			resp.setLicenseModel(snapshot.getLicenseModel());
			resp.setMasterUsername(snapshot.getMasterUsername());
			resp.setPort(snapshot.getPort());
			resp.setSnapshotCreateTime(DateHelper.getISO8601Date(snapshot.getSnapshotCreateTime()));
			resp.setStatus("deleted");

			EventUtil.addEvent(sess, ac.getId(), "Deleted user snapshot",
					"db-snapshot", new String[] { snapshotID });

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			final String msg = "DeleteSnapshot: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
}
