/**
 * 
 */
package com.transcend.rds.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBInstancesActionMessage.DescribeDBInstancesActionRequestMessage;
import com.transcend.rds.message.DescribeDBInstancesActionMessage.DescribeDBInstancesActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBInstance;
import com.transcend.rds.message.RDSMessage.DBParameterGroupStatus;
import com.transcend.rds.message.RDSMessage.DBSecurityGroupMembership;
import com.transcend.rds.message.RDSMessage.Endpoint;

public class DescribeDBInstancesActionWorker extends 
		AbstractWorker<DescribeDBInstancesActionRequestMessage, DescribeDBInstancesActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeDBInstancesActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBInstancesActionResultMessage doWork(
           DescribeDBInstancesActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBInstancesAction.");
       return super.doWork(req, getSession());
   }


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeDBInstancesActionResultMessage doWork0(DescribeDBInstancesActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeDBInstancesActionResultMessage doWork0(DescribeDBInstancesActionRequestMessage req,
ServiceRequestContext context) throws Exception {
		DescribeDBInstancesActionResultMessage.Builder result = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userID = ac.getId();
			final String instanceID = req.getDbInstanceIdentifier();
			final String marker = req.getMarker();
			final int maxRecords = ValidationManager.validateMaxRecords(
					req.getMaxRecords(), false);

			logger.info("DescribeInstance: " + " UserID = " + userID
					+ " InstanceID  = " + instanceID + " Marker = " + marker
					+ " MaxRecords = " + maxRecords);
			final List<RdsDbinstance> il = InstanceEntity.selectInstances(sess,
					instanceID, ac, marker, maxRecords);

			if (instanceID != null && il.size() == 0) {
				throw RDSQueryFaults.DBInstanceNotFound();
			}

			// build response document resp = new DescribeDBInstancesResponse();
			final List<DBInstance> dbs = new ArrayList<DBInstance>();
			if (il != null) {
				for (final RdsDbinstance inst : il) {
					final DBInstance dbInstance = toDBInstance(
							inst, ac);
					dbs.add(dbInstance);
				}
			}
			result = DescribeDBInstancesActionResultMessage.newBuilder();
			result.addAllDbInstances(dbs);
			if (il.size() > 0 && maxRecords <= il.size()) {
				result.setMarker(il.get(il.size() - 1).getDbinstanceId());
			}
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeInstance: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}

		return result.buildPartial();
	}
	
	public static DBInstance toDBInstance(final RdsDbinstance b,
			final AccountBean ac) {
		final DBInstance.Builder instRec = DBInstance.newBuilder();
		instRec.setDbInstanceIdentifier(b.getDbinstanceId());
		instRec.setReadReplicaSourceDBInstanceIdentifier(Strings.nullToEmpty(b
				.getSourceDbinstanceId()));
		if(b.getReplicas() != null && b.getReplicas().size() > 0){
			instRec.addAllReadReplicaDBInstanceIdentifiers(b.getReplicas());
		}
		instRec.setDbInstanceClass(Strings.nullToEmpty(b.getDbinstanceClass()));
		instRec.setAllocatedStorage(Integer.valueOf(b.getAllocatedStorage()));
		instRec.setInstanceCreateTime(DateHelper.getISO8601Date(b.getInstanceCreateTime()));
		instRec.setDbInstanceStatus(Strings.nullToEmpty(b.getDbinstanceStatus()));
		instRec.setEngine(Strings.nullToEmpty(b.getEngine()));
		instRec.setEngineVersion(Strings.nullToEmpty(b.getEngineVersion()));
		instRec.setAvailabilityZone(Strings.nullToEmpty(b.getAvailabilityZone()));
		instRec.setMultiAZ(b.getMultiAz());
		instRec.setMasterUsername(Strings.nullToEmpty(b.getMasterUsername()));
		instRec.setDbName(Strings.nullToEmpty(b.getDbName()));
		instRec.setAutoMinorVersionUpgrade(b.getAutoMinorVersionUpgrade());
		instRec.setBackupRetentionPeriod(Integer.valueOf(b
				.getBackupRetentionPeriod()));
		instRec.setLatestRestorableTime(DateHelper.getISO8601Date(b.getLatestRestorableTime()));
		instRec.setPreferredBackupWindow(b.getPreferredBackupWindow());
		instRec.setPreferredMaintenanceWindow(b.getPreferredMaintenanceWindow());
		instRec.setLicenseModel(b.getLicenseModel());
		final Endpoint.Builder endpoint = Endpoint.newBuilder();
		endpoint.setAddress(Strings.nullToEmpty(b.getAddress()));
		endpoint.setPort(Integer.valueOf(b.getPort()));
		instRec.setEndpoint(endpoint);
		String status = (Strings.nullToEmpty(b.getDbinstanceStatus()));
		if(status.equals("restoring")){
			status = "creating";
		}
		instRec.setDbInstanceStatus(status);
		final String dbparamName = b.getDbParameterGroup();
		final Collection<DBParameterGroupStatus> dBParameterGroups = new LinkedList<DBParameterGroupStatus>();
		final DBParameterGroupStatus.Builder dbparamStatus = DBParameterGroupStatus.newBuilder();
		dbparamStatus.setDbParameterGroupName(dbparamName);
		if (b.getPendingRebootParameters() == null
				|| b.getPendingRebootParameters().size() == 0) {
			dbparamStatus.setParameterApplyStatus(RDSUtilities.STATUS_IN_SYNC);
		} else {
			dbparamStatus
					.setParameterApplyStatus(RDSUtilities.STATUS_PENDING_REBOOT);
		}
		dBParameterGroups.add(dbparamStatus.buildPartial());
		instRec.addAllDbParameterGroups(dBParameterGroups);

		final Collection<DBSecurityGroupMembership> dBSecurityGroups = new LinkedList<DBSecurityGroupMembership>();
		for (final RdsDbsecurityGroup secGrp : b.getSecurityGroups()) {
			final DBSecurityGroupMembership.Builder membership = DBSecurityGroupMembership.newBuilder();
			membership.setDbSecurityGroupName(secGrp.getDbsecurityGroupName());
			membership.setStatus(secGrp.getStatus());
			dBSecurityGroups.add(membership.buildPartial());
		}
		instRec.addAllDbSecurityGroups(dBSecurityGroups);
		return instRec.buildPartial();
	}
}
