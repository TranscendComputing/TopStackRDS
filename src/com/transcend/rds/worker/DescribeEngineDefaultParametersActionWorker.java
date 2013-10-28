/**
 * 
 */
package com.transcend.rds.worker;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeEngineDefaultParametersActionMessage.DescribeEngineDefaultParametersActionRequestMessage;
import com.transcend.rds.message.DescribeEngineDefaultParametersActionMessage.DescribeEngineDefaultParametersActionResultMessage;
import com.transcend.rds.message.RDSMessage.Parameter;

public class DescribeEngineDefaultParametersActionWorker extends 
		AbstractWorker<DescribeEngineDefaultParametersActionRequestMessage, DescribeEngineDefaultParametersActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeEngineDefaultParametersActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeEngineDefaultParametersActionResultMessage doWork(
           DescribeEngineDefaultParametersActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeEngineDefaultParametersAction.");
       return super.doWork(req, getSession());
   }
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeEngineDefaultParametersActionResultMessage doWork0(DescribeEngineDefaultParametersActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeEngineDefaultParametersActionResultMessage doWork0(DescribeEngineDefaultParametersActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		DescribeEngineDefaultParametersActionResultMessage.Builder resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			// final AccountBean ac = context.getAccountBean();
			final AccountBean sac = AccountUtil.readAccount(sess, 1L);
			final String marker = req.getMarker();
			final int maxRecord = req.getMaxRecords();

			if (req.getDbParameterGroupFamily() == null) {
				throw QueryFaults
						.MissingParameter("DBParameterGroupFamily has to be supplied for DescribeEngineDefaultParameters request.");
			}
			if (req.getMaxRecords() < 20 || req.getMaxRecords() > 100) {
				throw RDSQueryFaults
						.InvalidParameterValue("MaxRecord must be 20 at least or 100 at greatest.");
			}

			final String grpFamily = ValidationManager.validateIdentifier(
					req.getDbParameterGroupFamily(), 255, true);

			final String grpName = "default." + grpFamily.toLowerCase();

			logger.info("DescribeEngineDefaultParameters: ParameterGroupFamily = "
					+ grpFamily);

			// reuse the method used by the DescribeDBParameters and
			// pass default GrpName and user = engine
			resp = selectParameters(sess, sac, grpName, null, marker,
							maxRecord);

			// load reply into EngineDefaults structure for return
			resp.setDbParameterGroupFamily(grpFamily);

			// build the response document
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeEngineDefaultParameters: Class: " + e.getClass()
					+ "Msg:" + e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp.buildPartial();
	}
	
	/**************************************************************************
	 * Select all of the DBParameters associated with the given DBParameterGroup
	 * DBParameterGroupName is required If source is provided only return
	 * DBParameters for that source (user | engine | system)
	 * 
	 * @param userID
	 * @param grpName
	 * @param source
	 * @param marker
	 * @param maxRecords
	 * @return
	 * @throws BaseException
	 */

	@SuppressWarnings("unchecked")
	public static DescribeEngineDefaultParametersActionResultMessage.Builder selectParameters(
			final Session sess, final AccountBean ac, final String grpName,
			final String source, final String marker, final int maxRecords) {
		final DescribeEngineDefaultParametersActionResultMessage.Builder resp = DescribeEngineDefaultParametersActionResultMessage.newBuilder();

		logger.info("selectParameters:" + " account = " + ac.getId()
				+ " DBParameterGroupName = " + grpName + " Source = " + source
				+ " marker = " + marker + " maxRecord = " + maxRecords);

		// confirm that the DBParameterGroup exists
		final RdsDbparameterGroup paramGrp = getParameterGroup(sess, grpName,
				ac.getId());
		if (paramGrp == null) {
			logger.error("selectParameters: DBParameterGroupNotFound: ParameterGroup"
					+ " not found for" + grpName);
			throw RDSQueryFaults.DBParameterGroupNotFound();
		} else {
			String markerSql = "";
			String selSql = "";
			String grpSql = "";

			if (marker != null && !marker.equals("")) {
				markerSql = " AND param.id > '" + marker + "'";
			}

			if (grpName != null && !grpName.equals("")) {
				grpSql = " AND pgroup.dbparameterGroupName = '" + grpName + "'";
			}

			if (source != null && !source.equals("")) {
				selSql = " AND param.source = '" + source + "'";
			}

			final String sql = "SELECT param FROM RdsParameter param INNER JOIN param.rdsParamGroup pgroup WHERE pgroup.account.id = "
					+ ac.getId()
					+ grpSql
					+ selSql
					+ markerSql
					+ " ORDER BY param.id";

			logger.info("selectParameters: Query is " + sql);
			final Query query = sess.createQuery(sql);
			if (maxRecords != 0) {
				query.setFirstResult(0);
				query.setMaxResults(maxRecords + 1);
			}

			final List<RdsParameter> parameters = query.list();
			logger.debug(parameters.size()
					+ " parameters are returned. If you see an additional parameter, it is to verify whether marker should be returned or not.");

			final Collection<Parameter> paramsConverted = new LinkedList<Parameter>();
			int lim = parameters.size();
			if (lim > maxRecords) {
				--lim;
				final String token = "" + parameters.get(lim - 1).getId();
				resp.setMarker(token);
			}
			for (int i = 0; i < lim; ++i) {
				final RdsParameter parameter = parameters.get(i);
				final Parameter.Builder paramConverted = Parameter.newBuilder();
				paramConverted.setAllowedValues(Strings.nullToEmpty(parameter.getAllowedValues()));
				paramConverted.setApplyMethod(Strings.nullToEmpty(parameter.getApplyMethod()));
				paramConverted.setApplyType(Strings.nullToEmpty(parameter.getApplyType()));
				paramConverted.setDataType(Strings.nullToEmpty(parameter.getDataType()));
				paramConverted.setDescription(Strings.nullToEmpty(parameter.getDescription()));
				paramConverted.setIsModifiable(parameter.getIsModifiable());
				paramConverted.setMinimumEngineVersion(Strings.nullToEmpty(parameter
						.getMinimumEngineVersion()));
				paramConverted.setParameterName(Strings.nullToEmpty(parameter.getParameterName()));
				paramConverted.setParameterValue(Strings.nullToEmpty(parameter.getParameterValue()));
				paramConverted.setSource(Strings.nullToEmpty(parameter.getSource()));
				paramsConverted.add(paramConverted.buildPartial());
			}
			resp.addAllParameters(paramsConverted);
		}
		return resp;
	}
	
	/**************************************************************************
	 * This returns the single named ParameterGroup record but uses the same
	 * implementation as selectParameterGroups that returns an array of records.
	 * 
	 * @param sess
	 * 
	 * @param paramGrpName
	 * @param userID
	 * @return
	 * @throws BaseException
	 */
	public static RdsDbparameterGroup getParameterGroup(final Session sess,
			final String paramGrpName, final long acid) {
		logger.info("getParameterGroup: " + " account = " + acid
				+ " ParameterGroupName = " + paramGrpName);
		final List<RdsDbparameterGroup> result = selectDBParameterGroups(sess,
				paramGrpName, acid, "", 1);
		if (result == null) {
			// don't throw exception - return null.
			logger.debug("getParameterGroup: No ParamerGroup record "
					+ "found for user = " + acid + " ParameterGroupName = "
					+ paramGrpName);
			return null;
		} else {
			if (result.size() != 1) {
				logger.debug("getParameterGroup: Found " + result.size()
						+ " records");
				return null;
			} else {
				return result.get(0);
			}
		}
	}
	

	/**************************************************************************
	 * Select list of DBParameterGroups either for 1. Named DBParameterGroup 2.
	 * All DBParameterGroups for that user
	 * 
	 * @param paramGrpName
	 * @param userID
	 * @param marker
	 * @param maxRecords
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("unchecked")
	public static List<RdsDbparameterGroup> selectDBParameterGroups(
			final Session sess, final String paramGrpName, final long acid,
			final String marker, final Integer maxRecords) {

		String markerSql = "";
		String groupSql = "";
		if (marker != null && !"".equals(marker)) {
			markerSql = " and dbparameterGroupName > '" + marker + "'";
		}

		if (paramGrpName != null && !"".equals(paramGrpName)) {
			groupSql = " and dbparameterGroupName = '" + paramGrpName + "'";
		}

		final String sql = "from RdsDbparameterGroup where account= " + acid
				+ markerSql + groupSql + " order by dbparameterGroupName";

		logger.info("selectDBParameterGroups: SQL Query is " + sql);
		final Query query = sess.createQuery(sql);
		query.setFirstResult(0);
		if (maxRecords != null) {
			query.setMaxResults(maxRecords);
		}
		return query.list();
	}

}
