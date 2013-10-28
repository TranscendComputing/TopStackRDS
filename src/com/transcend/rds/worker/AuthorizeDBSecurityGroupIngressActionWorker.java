/**
 *
 */
package com.transcend.rds.worker;

import java.util.ArrayList;
import java.util.List;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.network.Firewall;
import org.dasein.cloud.network.FirewallSupport;
import org.dasein.cloud.network.NetworkServices;
import org.hibernate.Session;
import org.slf4j.Logger;
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
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.SecurityGroupUtils;
import com.msi.tough.utils.rds.QuotaEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.AuthorizeDBSecurityGroupIngressActionMessage.AuthorizeDBSecurityGroupIngressActionRequestMessage;
import com.transcend.rds.message.AuthorizeDBSecurityGroupIngressActionMessage.AuthorizeDBSecurityGroupIngressActionResultMessage;
import com.transcend.rds.message.RDSMessage.EC2SecurityGroup;
import com.transcend.rds.message.RDSMessage.IPRange;

/**
 * @author tdhite
 */
public class AuthorizeDBSecurityGroupIngressActionWorker extends
		AbstractWorker<AuthorizeDBSecurityGroupIngressActionRequestMessage, AuthorizeDBSecurityGroupIngressActionResultMessage> {

	private final static Logger logger = Appctx
			.getLogger(AuthorizeDBSecurityGroupIngressActionWorker.class.getName());

	   /**
     * We need a local copy of this doWork to provide the transactional
     * annotation.  Transaction management is handled by the annotation, which
     * can only be on a concrete class.
     * @param req
     * @return
     * @throws Exception
     */
    @Transactional
    public AuthorizeDBSecurityGroupIngressActionResultMessage doWork(
            AuthorizeDBSecurityGroupIngressActionRequestMessage req) throws Exception {
        logger.debug("Performing work for AuthorizeDBSecurityGroupIngressAction.");
        return super.doWork(req, getSession());
    }

	public void addAuthorization(final Session sess, final AccountBean ac,
			final String ec2SecGrpName, final String ec2SecGrpId,
			final String cidrip, final String ec2Owner,
			final RdsDbsecurityGroup dbSecGrpRec) throws Exception {
		if (cidrip != null && !cidrip.equals("")) {
			final RdsIPRangeBean iprb = new RdsIPRangeBean(dbSecGrpRec.getId(),
					cidrip);
			sess.save(iprb);
		}

		if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
			final RdsEC2SecurityGroupBean egb = new RdsEC2SecurityGroupBean(
					dbSecGrpRec.getId(), ec2SecGrpName, ec2SecGrpId, ec2Owner);
			dbSecGrpRec.getEC2SecGroupBean(sess).add(egb);
			sess.save(egb);
		}
		sess.save(dbSecGrpRec);
	}


	@Override
	protected AuthorizeDBSecurityGroupIngressActionResultMessage doWork0(AuthorizeDBSecurityGroupIngressActionRequestMessage req,
ServiceRequestContext context) throws Exception {
        AuthorizeDBSecurityGroupIngressActionResultMessage resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();

		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userID = ac.getId();

			// input parameters
			final String dbSecGrpName = req.getDbSecurityGroupName();
			final String ec2SecGrpName = req.getEc2SecurityGroupName();
			final String cidrip = req.getCidrip();
			final String ec2Owner = req.getEc2SecurityGroupOwnerId();

			logger.debug("AuthoizeCacheSecurityGroupIngress:");
			logger.debug("UserId = " + userID);
			logger.debug("DBSecurityGroupName = " + dbSecGrpName);
			logger.debug("EC2SecurityGroupName = " + ec2SecGrpName);
			logger.debug("EC2OwerID = " + ec2Owner);
			logger.debug("CIDRIP = ", cidrip);

			// Validate combination of Authorize Request parameters
			if (cidrip == null || cidrip.equals("")) {
				if ((ec2SecGrpName == null || ec2SecGrpName.equals(""))
						&& (ec2Owner == null || ec2Owner.equals(""))) {
					throw QueryFaults
							.MissingParameter("You must supply either CIDRIP or ECSecurityGroup");
				} else if (ec2SecGrpName == null || ec2SecGrpName.equals("")
						|| ec2Owner == null || ec2Owner.equals("")) {
					throw QueryFaults
							.MissingParameter("You must supply both both EC2SecurityGroupName and EC2SecurityGroupOwnerID for an EC2SecurityGroup");
				}
			}

			// Confirm that the DBSecurityGroup exists
			final RdsDbsecurityGroup dbSecGrpRec = SecurityGroupEntity
					.getSecurityGroup(sess, dbSecGrpName, ac.getId());

			if (dbSecGrpRec == null) {
				throw RDSQueryFaults.DBSecurityGroupNotFound();
			}

			// Check the Authorization Quota
			if (!QuotaEntity.withinQuota(sess,
					RDSUtilities.Quota.QUOTA_AUTHORIZATION, userID, 1)) {
				msg = "AuthorizeDBSecurityGroup: AuthorizationQuotaExceeded Exceed "
						+ "Authorization allocation for user" + userID;
				logger.error(msg);
				throw RDSQueryFaults.DBSecurityGroupQuotaExceeded();
			}

			// checks if the ec2 security group exists or not
			// Note that AWS doesn't check the validity of source ec2 security
			// group; they simply ignore invalid ingresses when CreateDBInstance
			// is called with this kind of non-existing ec2 security groups
			final AccountType at = AccountUtil.toAccount(ac);

			String ec2groupId = null;
			if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
				final CloudProvider cloudProvider = DaseinHelper.getProvider(
						ac.getDefZone(), ac.getTenant(), ac.getAccessKey(),
						ac.getSecretKey());
				final NetworkServices network = cloudProvider
						.getNetworkServices();
				final FirewallSupport fs = network.getFirewallSupport();
				for (final Firewall f : fs.list()) {
					if (f.getName().equals(ec2SecGrpName)) {
						ec2groupId = f.getProviderFirewallId();
						logger.debug("Found the target ec2 security group. Its id is "
								+ ec2groupId);
						break;
					}
				}
				if (ec2groupId == null) {
					logger.error("EC2 security group with name, "
							+ ec2SecGrpName + ", could not be found.");
					throw RDSQueryFaults
							.InvalidParameterValue("EC2 Security Group "
									+ ec2SecGrpName + " cannot be found.");
				}
			}

			// check if the authorization exists already
			if (cidrip != null && !cidrip.equals("")) {
				for (final RdsIPRangeBean temp : dbSecGrpRec.getIPRange(sess)) {
					if (temp.getCidrip().equals(cidrip)) {
						throw RDSQueryFaults.AuthorizationAlreadyExists();
					}
				}
			}
			if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
				for (final RdsEC2SecurityGroupBean temp : dbSecGrpRec
						.getEC2SecGroupBean(sess)) {
					if (temp.getName().equals(ec2SecGrpName)) {
						throw RDSQueryFaults.AuthorizationAlreadyExists();
					}
				}
			}

			// Finally add the authorization
			addAuthorization(sess, ac, ec2SecGrpName, ec2groupId, cidrip,
					ec2Owner, dbSecGrpRec);

			// now check if there's any cloned DBSecurityGroups to update
			for (final String internalSecGrpName : dbSecGrpRec.getscaledInternals()) {
				final List<RdsDbsecurityGroup> secGrps = SecurityGroupEntity
						.selectAllSecurityGroups(sess, internalSecGrpName,
								ac.getId(), null, 0);
				if (secGrps == null || secGrps.size() != 1) {
					throw RDSQueryFaults.InternalFailure();
				}
				final RdsDbsecurityGroup secGrp = secGrps.get(0);
				addAuthorization(sess, ac, ec2SecGrpName, ec2groupId, cidrip,
						ec2Owner, secGrp);

				final String groupId = secGrp.getDbsecurityGroupId();
				logger.debug("Physical firewall groupId = " + groupId);

				if (ec2SecGrpName != null && !ec2SecGrpName.equals("")) {
					SecurityGroupUtils.authorizeSecurityGroupIngress(at,
							groupId, ec2groupId, secGrp.getStackId(), ec2Owner,
							secGrp.getPort(), null);
				} else {
					SecurityGroupUtils.authorizeSecurityGroupIngress(at,
							groupId, secGrp.getPort(), secGrp.getStackId(),
							cidrip);
				}
			}

			EventUtil.addEvent(sess, ac.getId(),
					"Applied change to security group", "db-security-group",
					new String[] { dbSecGrpName });

			sess.getTransaction().commit();

			// Build response Message
			resp = buildResponse(sess, dbSecGrpRec);

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

		return resp;
	}


	public static AuthorizeDBSecurityGroupIngressActionResultMessage buildResponse(final Session s,
			final RdsDbsecurityGroup r) {

		final AuthorizeDBSecurityGroupIngressActionResultMessage.Builder g =
				AuthorizeDBSecurityGroupIngressActionResultMessage.newBuilder();
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
