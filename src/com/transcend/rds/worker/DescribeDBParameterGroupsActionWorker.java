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
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBParameterGroupsActionMessage.DescribeDBParameterGroupsActionRequestMessage;
import com.transcend.rds.message.DescribeDBParameterGroupsActionMessage.DescribeDBParameterGroupsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBParameterGroup;

public class DescribeDBParameterGroupsActionWorker extends 
		AbstractWorker<DescribeDBParameterGroupsActionRequestMessage, DescribeDBParameterGroupsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeDBParameterGroupsActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBParameterGroupsActionResultMessage doWork(
           DescribeDBParameterGroupsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBParameterGroupsAction.");
       return super.doWork(req, getSession());
   }


	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeDBParameterGroupsActionResultMessage doWork0(DescribeDBParameterGroupsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeDBParameterGroupsActionResultMessage doWork0(DescribeDBParameterGroupsActionRequestMessage req,
				ServiceRequestContext context) throws Exception {

		DescribeDBParameterGroupsActionResultMessage.Builder resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userID = ac.getId();
			final String grpName = req.getDbParameterGroupName();
			final String marker = req.getMarker();
			final int maxRecords = ValidationManager.validateMaxRecords(
					req.getMaxRecords(), false);

			logger.info("DescribeDBParameterGroups: " + " account = "
					+ ac.getId() + " DBParameterGroupName = " + grpName
					+ " Marker = " + marker + " MaxRecords = " + maxRecords);

			// select the list of DBParameterGroups.
			final List<RdsDbparameterGroup> result = ParameterGroupEntity
					.selectDBParameterGroups(sess, grpName, ac.getId(), marker,
							maxRecords);

			if (grpName != null && (result == null || result.size() == 0)) {
				throw RDSQueryFaults.DBParameterGroupNotFound();
			}

			// build response document
			final List<DBParameterGroup> grl = new ArrayList<DBParameterGroup>();
			if (result != null) {
				logger.debug(result.size()
						+ " DBParameterGroups are selected from custom DBParameterGroups.");
				for (final RdsDbparameterGroup gb : result) {
					grl.add(toDBParameterGroup(gb));
				}
			}
			resp = DescribeDBParameterGroupsActionResultMessage.newBuilder();
			resp.addAllDbParameterGroups(grl);
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeDBParameterGroups: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
	
	public static DBParameterGroup toDBParameterGroup(
			final RdsDbparameterGroup p) {
		final DBParameterGroup.Builder g = DBParameterGroup.newBuilder();
		g.setDbParameterGroupFamily(p.getDbparameterGroupFamily());
		g.setDbParameterGroupName(p.getDbparameterGroupName());
		g.setDescription(p.getDescription());
		return g.buildPartial();
	}
}
