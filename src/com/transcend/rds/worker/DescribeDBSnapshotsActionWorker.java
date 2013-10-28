/**
 * 
 */
package com.transcend.rds.worker;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsSnapshot;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.SnapshotEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBSnapshotsActionMessage.DescribeDBSnapshotsActionRequestMessage;
import com.transcend.rds.message.DescribeDBSnapshotsActionMessage.DescribeDBSnapshotsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBSnapshot;

public class DescribeDBSnapshotsActionWorker extends 
		AbstractWorker<DescribeDBSnapshotsActionRequestMessage, DescribeDBSnapshotsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeDBSnapshotsActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBSnapshotsActionResultMessage doWork(
           DescribeDBSnapshotsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBSnapshotsAction.");
       return super.doWork(req, getSession());
   }


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeDBSnapshotsActionResultMessage doWork0(DescribeDBSnapshotsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeDBSnapshotsActionResultMessage doWork0(DescribeDBSnapshotsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		/*
		 * RDSImpl impl = new RDSImpl(); DescribeDBSnapshotsResult result =
		 * impl.describeDBSnapshots(request, getAwsAccessKeyId(),
		 * getRequestId());
		 */
		DescribeDBSnapshotsActionResultMessage.Builder resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			String instID = null;
			String snapshotID = null;
			final AccountBean ac = context.getAccountBean();
			final long userID = ac.getId();
			if(!"".equals(req.getDbInstanceIdentifier()))
					instID = req.getDbInstanceIdentifier();
			if(!"".equals(req.getDbSnapshotIdentifier()))
					snapshotID = req
					.getDbSnapshotIdentifier();
			final String marker = req.getMarker();
			int maxRec = req.getMaxRecords();
			if (maxRec == 0) {
				// set the default value for MaxRecords parameter
				maxRec = 20;
			}
			final int maxRecords = ValidationManager.validateMaxRecords(maxRec,
					false);

			logger.info("DescribeDBSnapshots: UserID = " + userID
					+ " SnapshotID = " + snapshotID + " Marker = " + marker
					+ " MaxRecords = " + maxRecords);

			final List<RdsSnapshot> snpList = SnapshotEntity.selectSnapshots(
					sess, instID, snapshotID, userID);
			// return DBSnapshotNotFound message only if user asked for a
			// specific DBSnapshot
			// IMPORTANT!!!! These three lines below are disabled because AWS
			// RDS does not return this exception even when no DBSnapshot is
			// found for the target DBSnapshotIdentifier
			/*
			 * if ((snpList == null || snpList.size() == 0) && snapshotID !=
			 * null) { throw RDSQueryFaults.DBSnapshotNotFound(); }
			 */
			if ((snpList == null || snpList.size() == 0) && snapshotID != null && !("".equals(snapshotID))) {
				throw RDSQueryFaults
						.DBSnapshotNotFound("There is no DBSnapshot taken from "
								+ snapshotID + ".");
			}

			// build response document
			resp = DescribeDBSnapshotsActionResultMessage.newBuilder();
			final List<DBSnapshot> dl = new ArrayList<DBSnapshot>();
			for (final RdsSnapshot snp : snpList) {
				dl.add(toDBSnapshot(snp));
			}
			resp.addAllDbSnapshots(dl);

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeSnapshots: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
	
    public static DBSnapshot toDBSnapshot(final RdsSnapshot b) {
        final DBSnapshot.Builder snpRec = DBSnapshot.newBuilder();
        snpRec.setDbSnapshotIdentifier(b.getDbsnapshotId());
        snpRec.setDbInstanceIdentifier(b.getDbinstanceId());
        snpRec.setAllocatedStorage(b.getAllocatedStorage());
        snpRec.setAvailabilityZone(b.getAvailabilityZone());
        snpRec.setEngine(b.getEngine());
        snpRec.setEngineVersion(b.getEngineVersion());
        snpRec.setMasterUsername(b.getMasterUsername());
        snpRec.setPort(b.getPort());
        snpRec.setInstanceCreateTime(DateHelper.getISO8601Date(b.getInstanceCreatedTime()));
        snpRec.setSnapshotCreateTime(DateHelper.getISO8601Date(b.getSnapshotCreateTime()));
        snpRec.setStatus(b.getStatus());
        snpRec.setLicenseModel(b.getLicenseModel());
        // TODO add the line below once AWS Java SDK is ready
        // snpRec.setSnapshotType(b.getSnapshotType());
        return snpRec.buildPartial();
    }
}
