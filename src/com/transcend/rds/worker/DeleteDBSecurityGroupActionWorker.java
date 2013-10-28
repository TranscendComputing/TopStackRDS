/**
 *
 */
package com.transcend.rds.worker;

import java.util.Collection;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbinstance;
import com.msi.tough.model.rds.RdsDbsecurityGroup;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.SecurityGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DeleteDBSecurityGroupActionMessage.DeleteDBSecurityGroupActionRequestMessage;
import com.transcend.rds.message.DeleteDBSecurityGroupActionMessage.DeleteDBSecurityGroupActionResultMessage;

/**
 * @author tdhite
 */
public class DeleteDBSecurityGroupActionWorker extends 
		AbstractWorker<DeleteDBSecurityGroupActionRequestMessage, DeleteDBSecurityGroupActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DeleteDBSecurityGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DeleteDBSecurityGroupActionResultMessage doWork(
           DeleteDBSecurityGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DeleteDBSecurityGroupAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * deleteDBSecurityGroup ********************************************** This
	 * Operation deletes a database security group. Database security group must
	 * not be associated with any RDS Instances. Request: SecurityGroupName (R)
	 * Response: None Exceptions: DBSecurityGroupNotFound
	 * InvalidDBSecurityGroupState Processing 1. Check if SecurityGroup is
	 * associated with DBInstance 2. Delete security group 3. Return response
	 * 
	 * @param requestID
	 * @param userID
	 */
	@Override
	protected DeleteDBSecurityGroupActionResultMessage doWork0(DeleteDBSecurityGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

        final DeleteDBSecurityGroupActionResultMessage.Builder resp =
        		DeleteDBSecurityGroupActionResultMessage.newBuilder();
        final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			if (ac == null) {
				throw QueryFaults.AuthorizationNotFound();
			}
			final long userID = ac.getId();

			final String grpName = req.getDbSecurityGroupName();

			logger.info("DeleteDBSecurityGroup: UserID = " + userID
					+ " DBSecurityGroupName = " + grpName);

			if (grpName.equals("default")) {
				// user does not have authority to delete the default
				// DBSecurityGroup
				throw RDSQueryFaults
						.InvalidClientTokenId("The security token included in the request is invalid.");
			}

			final RdsDbsecurityGroup secGrp = SecurityGroupEntity
					.getSecurityGroup(sess, grpName, ac.getId());
			if (secGrp == null) {
				throw RDSQueryFaults.DBSecurityGroupNotFound();
			}

			// check status - cannot delete if it is associated with
			// DBInstance records - select DBSecurityGroupMemebershipRecords
			final Collection<RdsDbinstance> instances = InstanceEntity
					.selectBySecurityGroup(sess, grpName, ac);
			if (instances != null) {
				logger.debug(instances.size()
						+ " DBInstances are using this DBSecurityGroup.");
			} else {
				logger.debug("No DBInstance is using this DBSecurityGroup.");
			}
			if (instances != null && instances.size() > 0) {
				throw RDSQueryFaults.InvalidDBSecurityGroupState();
			}

			SecurityGroupEntity.deleteSecurityGroup(sess, secGrp);

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			final String msg = "DeleteDBSecurityGroup: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
	
	
	public void deleteInternalSecurityGroup(final Session sess,
			final AccountBean ac, final String grpName) {

		// delete the named record
		final List<RdsDbsecurityGroup> grps = SecurityGroupEntity
				.selectAllSecurityGroups(sess, grpName, ac.getId(), null, 1);
		if (grps == null || grps.size() != 1) {
			throw RDSQueryFaults.InternalFailure();
		}
		final RdsDbsecurityGroup grp = grps.get(0);
		if (grp == null) {
			throw RDSQueryFaults.InternalFailure();
		}
		// for (final RdsIPRangeBean ip : grp.getIPRange()) {
		// sess.delete(ip);
		// }
		// for (final RdsEC2SecurityGroupBean sec : grp.getEC2SecGroup()) {
		// sess.delete(sec);
		// }
		sess.delete(grp);
	}
}
