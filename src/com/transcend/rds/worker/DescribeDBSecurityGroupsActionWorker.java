/**
 * 
 */
package com.transcend.rds.worker;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBSecurityGroupsActionMessage.DescribeDBSecurityGroupsActionRequestMessage;
import com.transcend.rds.message.DescribeDBSecurityGroupsActionMessage.DescribeDBSecurityGroupsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBSecurityGroup;
import com.transcend.rds.message.RDSMessage.EC2SecurityGroup;
import com.transcend.rds.message.RDSMessage.IPRange;

public class DescribeDBSecurityGroupsActionWorker extends 
		AbstractWorker<DescribeDBSecurityGroupsActionRequestMessage, DescribeDBSecurityGroupsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeDBSecurityGroupsActionWorker.class.getName());
	

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBSecurityGroupsActionResultMessage doWork(
           DescribeDBSecurityGroupsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBSecurityGroupsAction.");
       return super.doWork(req, getSession());
   }
   

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeDBSecurityGroupsActionResultMessage doWork0(DescribeDBSecurityGroupsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeDBSecurityGroupsActionResultMessage doWork0(DescribeDBSecurityGroupsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		DescribeDBSecurityGroupsActionResultMessage.Builder resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userID = ac.getId();
			RDSUtil.ensureDefaultSecurityGroup(sess, userID);

			// input parameters
			final String dbSecGrpName = req.getDbSecurityGroupName();
			final String marker = req.getMarker();
			final int maxRecords = ValidationManager.validateMaxRecords(
					req.getMaxRecords(), false);

			logger.info("DescribeSecurityGroup: " + " UserID = " + userID
					+ " DBSecurityGroup = " + dbSecGrpName + " Marker = "
					+ marker + " maxRecords = " + maxRecords);

			List<RdsDbsecurityGroup> result = null;

			if (dbSecGrpName != null) {
				final RdsDbsecurityGroup res = SecurityGroupEntity
						.getSecurityGroup(sess, dbSecGrpName, ac.getId());
				if (res == null) {
					throw RDSQueryFaults.DBSecurityGroupNotFound();
				}
				result = new LinkedList<RdsDbsecurityGroup>();
				result.add(res);
			} else {
				// select the securityGroups
				result = SecurityGroupEntity.selectSecurityGroups(sess,
						dbSecGrpName, ac.getId(), marker, maxRecords);
			}
			// build response document
			resp = DescribeDBSecurityGroupsActionResultMessage.newBuilder();
			final List<DBSecurityGroup> sgs = new ArrayList<DBSecurityGroup>();
			if (result != null) {
				for (final RdsDbsecurityGroup g : result) {
					sgs.add(toDBSecurityGroup(sess, g));
				}
			}
			resp.addAllDbSecurityGroups(sgs);
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeSecurityGroup: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
	
	public static DBSecurityGroup toDBSecurityGroup(final Session s,
			final RdsDbsecurityGroup r) {

		final DBSecurityGroup.Builder g = DBSecurityGroup.newBuilder();
		g.setDbSecurityGroupDescription(r.getDbsecurityGroupDescription());
		g.setDbSecurityGroupName(r.getDbsecurityGroupName());
		final List<EC2SecurityGroup> l = new ArrayList<EC2SecurityGroup>();
		for (final RdsEC2SecurityGroupBean rdsEC2SecGrp : r
				.getEC2SecGroupBean(s)) {
			final EC2SecurityGroup.Builder ec2g = EC2SecurityGroup.newBuilder();
			ec2g.setEc2SecurityGroupName(rdsEC2SecGrp.getName());
			ec2g.setEc2SecurityGroupOwnerId(rdsEC2SecGrp.getOwnId());
			ec2g.setStatus(rdsEC2SecGrp.getStatus());
			l.add(ec2g.buildPartial());
		}
		g.addAllEc2SecurityGroups(l);

		final List<IPRange> ipl = new ArrayList<IPRange>();
		for (final RdsIPRangeBean ipRange : r.getIPRange(s)) {
			final IPRange.Builder ip = IPRange.newBuilder();
			ip.setCidrip(ipRange.getCidrip());
			ip.setStatus(ipRange.getStatus());
			ipl.add(ip.buildPartial());
		}
		g.addAllIpRanges(ipl);
		g.setOwnerId(r.getAccount().getTenant());
		return g.buildPartial();
	}
}
