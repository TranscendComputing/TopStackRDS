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
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbengine;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.EngineEntity;
import com.msi.tough.utils.rds.RDSUtilities;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBEngineVersionsActionMessage.DescribeDBEngineVersionsActionRequestMessage;
import com.transcend.rds.message.DescribeDBEngineVersionsActionMessage.DescribeDBEngineVersionsActionResultMessage;
import com.transcend.rds.message.RDSMessage.DBEngineVersion;

public class DescribeDBEngineVersionsActionWorker extends 
		AbstractWorker<DescribeDBEngineVersionsActionRequestMessage, DescribeDBEngineVersionsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeDBEngineVersionsActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBEngineVersionsActionResultMessage doWork(
           DescribeDBEngineVersionsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBEngineVersionsAction.");
       return super.doWork(req, getSession());
   }
   

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeDBEngineVersionsActionResultMessage doWork0(DescribeDBEngineVersionsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeDBEngineVersionsActionResultMessage doWork0(DescribeDBEngineVersionsActionRequestMessage req,
ServiceRequestContext context) throws Exception {
		DescribeDBEngineVersionsActionResultMessage.Builder result = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final String userID = "" + ac.getId();
			// Validate Request
			final String marker = req.getMarker();
			final int maxRecords = ValidationManager.validateMaxRecords(
					req.getMaxRecords(), false);

			String grpFamily = "";
			if (!"".equals(req.getDbParameterGroupFamily())
					&& req.getDbParameterGroupFamily() != null) {
				grpFamily = ValidationManager.validateParamGrpFamily(
						req.getDbParameterGroupFamily(), true);
			}

			String engine = "";
			if (!"".equals(req.getEngine()) && req.getEngine() != null) {
				engine = ValidationManager.validateEngine(req.getEngine(),
						true);
			}

			String engineVersion = "";
			if (!"".equals(req.getEngineVersion())
					&& req.getEngineVersion() != null) {
				engineVersion = ValidationManager.validateEngineVersion(
						req.getEngineVersion(), true);
			}

			final boolean defaultOnly = RDSUtilities.defaultFalse(req
					.getDefaultOnly());

			logger.info("DescribeDBEngineVersions: " + " UserID = " + userID
					+ " DBParameterGroupFamily = " + grpFamily + " Engine = "
					+ engine + " EngineVersion = " + engineVersion
					+ " DefaultOnly = " + defaultOnly + " Marker = " + marker
					+ " MaxRecords = " + maxRecords);

			// Select the DBEngine records
			final List<RdsDbengine> entities = EngineEntity
					.selectDBEngineVersions(sess, userID, grpFamily, engine,
							engineVersion, defaultOnly, marker, maxRecords);

			if (entities == null) {
				msg = "DescribeDBEngineVersions: No EngineVersion records found";
				logger.error(msg);
				throw new BaseException(msg);
			}

			// build response document
			result = DescribeDBEngineVersionsActionResultMessage.newBuilder();

			final List<DBEngineVersion> va = new ArrayList<DBEngineVersion>();
			for (final RdsDbengine en : entities) {
				// logger.debug("Processing DBEngineVersion: " + en.getEngine()
				// + "; " + en.getEngineVersion() + "; " +
				// en.getDbparameterGroupFamily());
				va.add(toDBEngineVersion(en));
			}
			result.addAllDbEngineVersions(va);
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeDBEngineVersions: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return result.buildPartial();

	}
	
	public static DBEngineVersion toDBEngineVersion(RdsDbengine b) {
		DBEngineVersion.Builder engineRec = DBEngineVersion.newBuilder();
		engineRec.setDbParameterGroupFamily(b.getDbparameterGroupFamily());
		engineRec.setEngine(b.getEngine());
		engineRec.setEngineVersion(b.getEngineVersion());
		return engineRec.buildPartial();
	}
}
