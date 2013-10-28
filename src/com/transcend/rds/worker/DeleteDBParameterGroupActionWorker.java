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
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.InstanceEntity;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DeleteDBParameterGroupActionMessage.DeleteDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.DeleteDBParameterGroupActionMessage.DeleteDBParameterGroupActionResultMessage;

/**
 * @author rarora
 */
public class DeleteDBParameterGroupActionWorker extends 
		AbstractWorker<DeleteDBParameterGroupActionRequestMessage, DeleteDBParameterGroupActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DeleteDBParameterGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DeleteDBParameterGroupActionResultMessage doWork(
           DeleteDBParameterGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DeleteDBParameterGroupAction.");
       return super.doWork(req, getSession());
   }

	/**
	 * deleteDBParameterGroup
	 * ************************************************************** This
	 * Operation deletes a particular DBParameterGroup. The DBParameterGroup
	 * cannot be associated with any RDS instances to be deleted. Request:
	 * DBParameterGroupName(R) Response: None Exceptions:
	 * DBParameterGroupNotFound InvalidDBParameterGroupState Processing Delete
	 * DB record only 1. Determine that Parameter group is in correct state 2.
	 * Delete named DBParameterGroup and associated Parameter Records 3. Return
	 * Response (true if worked, false if not).
	 */
	@Override
	protected DeleteDBParameterGroupActionResultMessage doWork0(DeleteDBParameterGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {
		final Session sess = HibernateUtil.newSession();

		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();

			final String paramGrpName = req.getDbParameterGroupName();

			logger.info("DeleteDBParameterGroup: account = " + ac.getId()
					+ " DBParameterGroupName = " + paramGrpName);

			// make sure DBParameterGroupName is passed
			if (paramGrpName == null || "".equals(paramGrpName)) {
				throw QueryFaults
						.MissingParameter("DBParameterGroupName must be supplied for DeleteDBParameterGroup request.");
			}
			if (paramGrpName.equals("default.mysql5.5")) {
				throw RDSQueryFaults
						.InvalidClientTokenId("You do not have privilege to delete default DBParameterGroup.");
			}

			// confirm that this DBParameterGroup exists
			final RdsDbparameterGroup pRec = ParameterGroupEntity
					.getParameterGroup(sess, paramGrpName, ac.getId());
			if (pRec == null) {
				throw RDSQueryFaults.DBParameterGroupNotFound();
			}

			// cannot delete if associated with a DBInstance
			final Collection<RdsDbinstance> dbInstances = InstanceEntity
					.selectDBInstancesByParameterGroup(sess,
							req.getDbParameterGroupName(),
							1, ac);

			if (dbInstances.size() == 0) {
				// delete the DBParameterGroup
				final List<RdsParameter> params = pRec.getParameters();
				for (final RdsParameter temp : params) {
					sess.delete(temp);
				}
				sess.delete(pRec);
			} else {
				final String msg = "One or more database instances are still members of "
						+ "this parameter group "
						+ paramGrpName
						+ ", so the group cannot be deleted";
				logger.error(msg);
				throw RDSQueryFaults.InvalidDBParameterGroupState(msg);
			}

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			final String msg = "DeleteDBParameterGroup: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		DeleteDBParameterGroupActionResultMessage.Builder resp = DeleteDBParameterGroupActionResultMessage.newBuilder();
		return resp.buildPartial();
	}
}
