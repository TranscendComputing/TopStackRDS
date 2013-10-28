/**
 *
 */
package com.transcend.rds.worker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.cf.AccountType;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.dasein.DaseinHelper;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.model.rds.RdsEC2SecurityGroupBean;
import com.msi.tough.model.rds.RdsIPRangeBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.SecurityGroupUtils;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.RDSMessage.EC2SecurityGroup;
import com.transcend.rds.message.RDSMessage.IPRange;
import com.transcend.rds.message.RevokeDBSecurityGroupIngressActionMessage.RevokeDBSecurityGroupIngressActionRequestMessage;
import com.transcend.rds.message.RevokeDBSecurityGroupIngressActionMessage.RevokeDBSecurityGroupIngressActionResultMessage;

/**
 * @author tdhite
 */
public class RevokeDBSecurityGroupIngressActionWorker extends
		AbstractWorker<RevokeDBSecurityGroupIngressActionRequestMessage, RevokeDBSecurityGroupIngressActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(RevokeDBSecurityGroupIngressActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public RevokeDBSecurityGroupIngressActionResultMessage doWork(
           RevokeDBSecurityGroupIngressActionRequestMessage req) throws Exception {
       logger.debug("Performing work for RevokeDBSecurityGroupIngressAction.");
       return super.doWork(req, getSession());
   }


	/**
	 * revokeDBSecurityGroupIngress
	 * ******************************************************** This Operation
	 * revokes Ingress from a DBSecurityGroup for previously authorized IP
	 * ranges or EC2 Security Groups. Required parameters for this Operation are
	 * one of CIDRIP or (EC2SecurityGroupName AND EC2SecurityGroupOwnerId).
	 *
	 * 1. Confirm that DBSecurityGroup record exists and is in appropriate state
	 * 2. Confirm quotas 3. If CIDRIP is set, delete record from IPRange table
	 * 4. If ECSecurityGroup and EC2SecurityGroupOwnerId is set, delete record
	 * from CSecurityGroups 5. Return response message.
	 */
	@Override
	protected RevokeDBSecurityGroupIngressActionResultMessage doWork0(RevokeDBSecurityGroupIngressActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		RevokeDBSecurityGroupIngressActionResultMessage.Builder resp = null;
		final Logger logger = LoggerFactory
				.getLogger(this.getClass().getName());
		String msg = "";

		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userId = ac.getId();

			final String dbSecGrpName = req.getDbSecurityGroupName();
			final String ec2SecGrpName = req.getEc2SecurityGroupName();
			final String cidrip = req.getCidrip();
			final String ec2Owner = req.getEc2SecurityGroupOwnerId();

			logger.info("RevokeDBSecurirtGoupIngress: UserId = " + userId
					+ " DBSecurityGroupName = " + dbSecGrpName
					+ " EC2SecurityGroupName = " + ec2SecGrpName
					+ " EC2OwerID = " + ec2Owner + " CIDRIP = " + cidrip);

			/*
			 * Validate combinations Either CIDRIP or (EC2SecuityGroupName and
			 * EC2SecurityGroupOwnerId) If EC2SecuityGroupName then and
			 * EC2SecurityGroupOwnerId must be supplied and vice versa
			 */
			if (cidrip == null || cidrip.equals("")) {
				if ((ec2SecGrpName == null || ec2SecGrpName.equals(""))) {
					throw ErrorResponse
							.invlidData("You must supply either CIDRIP or ECSecurityGroup");
				} else if (ec2SecGrpName == null || ec2SecGrpName.equals("")
						|| ec2Owner == null || ec2Owner.equals("")) {
					throw ErrorResponse
							.invlidData("You must supply both both EC2SecurityGroupName and EC2SecurityGroupOwnerID for an EC2SecurityGroup");
				}
			}

			// lookup DBsecurityGroup
			final RdsDbsecurityGroup dbSecGrpRec = SecurityGroupEntity
					.getSecurityGroup(sess, dbSecGrpName, ac.getId());

			if (dbSecGrpRec == null) {
				throw RDSQueryFaults.DBSecurityGroupNotFound();
			}

			// check if the target ingress exists
			boolean found = false;
			int index = 0;
			if (cidrip != null && !"".equals(cidrip)) {
				final List<RdsIPRangeBean> ips = dbSecGrpRec.getIPRange(sess);
				while (!found && index < ips.size()) {
					if (ips.get(index).getCidrip().equals(cidrip)) {
						found = true;
					}
					++index;
				}
			} else if (ec2SecGrpName != null && !"".equals(ec2SecGrpName)) {
				final List<RdsEC2SecurityGroupBean> ec2secs = dbSecGrpRec
						.getEC2SecGroupBean(sess);
				while (!found && index < ec2secs.size()) {
					if (ec2secs.get(index).getName().equals(ec2SecGrpName)) {
						found = true;
					}
					++index;
				}
			}
			if (!found) {
				throw QueryFaults.AuthorizationNotFound();
			}

			final Collection<RdsEC2SecurityGroupBean> rdsEc2SecGrps = dbSecGrpRec
					.getRdsEC2SecurityGroups(sess);
			final Collection<IPRange.Builder> awsIps = getIPRanges(sess, dbSecGrpRec);
			Collection<IPRange> awsIpsBuilt = new ArrayList<IPRange>();
			if (cidrip != null && !cidrip.equals("")) {
				for (final IPRange.Builder awsIp : awsIps) {
					if (awsIp.getCidrip().equals(cidrip)) {
						awsIp.setStatus(RDSUtilities.STATUS_REVOKING);
					}
					awsIpsBuilt.add(awsIp.buildPartial());
				}
			}
			if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
				for (final RdsEC2SecurityGroupBean rdsEc2Sec : rdsEc2SecGrps) {
					if (rdsEc2Sec.getSecGrpName().equals(ec2SecGrpName)) {
						rdsEc2Sec.setStatus(RDSUtilities.STATUS_REVOKING);
					}
				}
			}

			// finally revoke the ingress
			removeAuthorization(sess, ac, dbSecGrpName, ec2SecGrpName, cidrip,
					ec2Owner, dbSecGrpRec);

			// now check if there's any cloned DBSecurityGroups to update
			for (final String internalSecGrpName : dbSecGrpRec.getscaledInternals()) {
				final List<RdsDbsecurityGroup> secGrps = SecurityGroupEntity
						.selectAllSecurityGroups(sess, internalSecGrpName,
								ac.getId(), null, 1);
				if (secGrps == null) {
					throw RDSQueryFaults.InternalFailure();
				}
				final RdsDbsecurityGroup secGrp = secGrps.get(0);
				int counter = 0;
				// check for duplicate ingress
				if (cidrip != null && !cidrip.equals("")) {
					for (final RdsIPRangeBean ip : secGrp.getIPRange(sess)) {
						if (ip.getCidrip().equals(cidrip)) {
							++counter;
						}
					}
				}
				if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
					for (final RdsEC2SecurityGroupBean ec2secGrp : secGrp
							.getEC2SecGroupBean(sess)) {
						if (ec2SecGrpName.equals(ec2secGrp.getName())) {
							++counter;
						}
					}
				}
				logger.debug("There are " + counter + " same ingresses in "
						+ dbSecGrpName);

				removeAuthorization(sess, ac, internalSecGrpName,
						ec2SecGrpName, cidrip, ec2Owner, secGrp);

				// revoke the ingress from ec2 security group only if there is
				// no duplicate
				AccountType at = new AccountType();
				at.setAccessKey(ac.getAccessKey());
				at.setSecretKey(ac.getSecretKey());
				at.setTenant(ac.getTenant());
				at.setDefZone(ac.getDefZone());
				if (counter == 1) {
					if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
						String ec2groupId = null;
						final CloudProvider cloudProvider = DaseinHelper.getProvider(
								ac.getDefZone(), ac.getTenant(),
								ac.getAccessKey(), ac.getSecretKey());
						final NetworkServices network = cloudProvider.getNetworkServices();
						final FirewallSupport fs = network.getFirewallSupport();
						for(Firewall f : fs.list()){
							if(f.getName().equals(ec2SecGrpName)){
								ec2groupId = f.getProviderFirewallId();
								logger.debug("Found the target ec2 security group. Its id is " + ec2groupId);
								break;
							}
						}
						if(ec2groupId == null) {
							logger.error("EC2 security group with name, " + ec2SecGrpName + ", could not be found.");
							throw RDSQueryFaults
									.InvalidParameterValue("EC2 Security Group "
											+ ec2SecGrpName + " cannot be found.");
						}

						SecurityGroupUtils.revokeSecurityGroupIngress(
								at, secGrp.getDbsecurityGroupId(),
								ec2groupId, secGrp.getStackId(), ec2Owner,
								secGrp.getPort());
					} else {
						SecurityGroupUtils.revokeSecurityGroupIngress(
								at, secGrp.getDbsecurityGroupId(),
								secGrp.getPort(), secGrp.getStackId(), cidrip);
					}
				}
			}

			// build response document
			resp = RevokeDBSecurityGroupIngressActionResultMessage.newBuilder();
			resp.setDbSecurityGroupDescription(dbSecGrpRec
					.getDbsecurityGroupDescription());
			resp.setDbSecurityGroupName(dbSecGrpRec.getDbsecurityGroupName());
			final Collection<EC2SecurityGroup> ec2SecGrps = new ArrayList<EC2SecurityGroup>();
			for (final RdsEC2SecurityGroupBean i : rdsEc2SecGrps) {
				final EC2SecurityGroup.Builder eg = EC2SecurityGroup.newBuilder();
				eg.setEc2SecurityGroupName(i.getSecGrpName());
				eg.setEc2SecurityGroupOwnerId(i.getSecGroupOwnId());
				eg.setStatus(i.getStatus());
				ec2SecGrps.add(eg.buildPartial());
			}
			resp.addAllEc2SecurityGroups(ec2SecGrps);
			resp.addAllIpRanges(awsIpsBuilt);
			resp.setOwnerId(ac.getTenant());

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "AuthorizeDBSecurirtGoupIngress: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();

		} finally {
			sess.close();
		}

		return resp.buildPartial();
	}

	public void removeAuthorization(final Session sess, final AccountBean ac,
			final String dbSecGrpName, final String ec2SecGrpName,
			final String cidrip, final String ec2Owner,
			final RdsDbsecurityGroup dbSecGrpRec) throws Exception {
		if (cidrip != null && !cidrip.equals("")) {
			// IPRange is identified by dbGrpName, userID, cidrip
			SecurityGroupEntity.deleteIPRange(sess, dbSecGrpRec, dbSecGrpName,
					cidrip);
		}

		if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
			// EC2SecurityGroup is identified by dbGrpName, ec2GrpName, userID
			SecurityGroupEntity.deleteEc2Grp(sess, dbSecGrpRec, dbSecGrpName,
					ec2SecGrpName, ec2Owner);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<IPRange.Builder> getIPRanges(final Session s, RdsDbsecurityGroup inst) {
		final ArrayList<IPRange.Builder> ranges = new ArrayList<IPRange.Builder>();
		final Query q = s
				.createQuery("from RdsIPRangeBean where rdsSecGroupId=" + inst.getId());
		for (final RdsIPRangeBean bean : (List<RdsIPRangeBean>) q.list()) {
			final IPRange.Builder range = IPRange.newBuilder();
			range.setCidrip(bean.getCidrip());
			range.setStatus(bean.getStatus());
			ranges.add(range);
		}
		return ranges;
	}
}
