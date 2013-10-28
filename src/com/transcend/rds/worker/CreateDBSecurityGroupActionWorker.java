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
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.RDSUtil;
import com.msi.tough.utils.rds.QuotaEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBSecurityGroupActionMessage.CreateDBSecurityGroupActionRequestMessage;
import com.transcend.rds.message.CreateDBSecurityGroupActionMessage.CreateDBSecurityGroupActionResultMessage;
import com.transcend.rds.message.RDSMessage.EC2SecurityGroup;
import com.transcend.rds.message.RDSMessage.IPRange;

/**
 * @author tdhite
 */
public class CreateDBSecurityGroupActionWorker extends
		AbstractWorker<CreateDBSecurityGroupActionRequestMessage, CreateDBSecurityGroupActionResultMessage> {
		
	private final static Logger logger = Appctx
			.getLogger(CreateDBSecurityGroupActionWorker.class.getName());
	
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public CreateDBSecurityGroupActionResultMessage doWork(
           CreateDBSecurityGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for CreateDBSecurityGroupAction.");
       return super.doWork(req, getSession());
   }
   

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected CreateDBSecurityGroupActionResultMessage doWork0(CreateDBSecurityGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected CreateDBSecurityGroupActionResultMessage doWork0(CreateDBSecurityGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {
		CreateDBSecurityGroupActionResultMessage resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
	
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
	
			final long userID = ac.getId();
			RDSUtil.ensureDefaultSecurityGroup(sess, userID);
	
			final String grpName = req.getDbSecurityGroupName();
	
			final String desc = req.getDbSecurityGroupDescription();
			if (desc == null || desc.equals("") || grpName == null
					|| grpName.equals("")) {
				throw QueryFaults.MissingParameter();
			}
	
			logger.info("CreateDBSecurityGroup: " + " UserID = " + userID
					+ " DBSecurityGroupName = " + grpName + " Description = "
					+ req.getDbSecurityGroupDescription());
	
			// check quotas
			if (!QuotaEntity.withinQuota(sess, RDSUtilities.Quota.QUOTA_SECGRP,
					ac, 1)) {
				msg = "CreateDBSecurityGroup: DBSecurityGroupQuotaExceeded. this "
						+ "request will exceed your allocation of DBSecurityGroups";
				logger.error(msg);
				throw RDSQueryFaults.DBSecurityGroupQuotaExceeded();
			}
	
			// Does this securityGroup already exist?
			if (grpName.equals("default")) {
				throw RDSQueryFaults.DBSecurityGroupAlreadyExist();
			}
			final RdsDbsecurityGroup secGrp = SecurityGroupEntity
					.getSecurityGroup(sess, grpName, ac.getId());
			if (secGrp != null) {
				throw RDSQueryFaults.DBSecurityGroupAlreadyExist();
			}
	
			// insert new record
			final RdsDbsecurityGroup newSecGrp = SecurityGroupEntity
					.insertSecurityGroup(sess, ac,
							req.getDbSecurityGroupName(),
							req.getDbSecurityGroupDescription());
	
			// commit the new record to the services DB.
			sess.getTransaction().commit();
	
			// build response document
			resp = buildResponse(sess, newSecGrp);
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "CreateDBSecurityGroup: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp;
		}
	
	
	
	public static CreateDBSecurityGroupActionResultMessage buildResponse(final Session s,
			final RdsDbsecurityGroup r) {

		final CreateDBSecurityGroupActionResultMessage.Builder g = 
				CreateDBSecurityGroupActionResultMessage.newBuilder();
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
