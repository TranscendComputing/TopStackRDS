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
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.model.rds.RdsParameter;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeDBParametersActionMessage.DescribeDBParametersActionRequestMessage;
import com.transcend.rds.message.DescribeDBParametersActionMessage.DescribeDBParametersActionResultMessage;
import com.transcend.rds.message.RDSMessage.Parameter;

public class DescribeDBParametersActionWorker extends 
		AbstractWorker<DescribeDBParametersActionRequestMessage, DescribeDBParametersActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeDBParametersActionWorker.class.getName());

    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeDBParametersActionResultMessage doWork(
           DescribeDBParametersActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeDBParametersAction.");
       return super.doWork(req, getSession());
   }
   
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeDBParametersActionResultMessage doWork0(DescribeDBParametersActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeDBParametersActionResultMessage doWork0(DescribeDBParametersActionRequestMessage req,
			ServiceRequestContext context) throws Exception {
		DescribeDBParametersActionResultMessage resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();

			if (req.getDbParameterGroupName() == null
					|| "".equals(req.getDbParameterGroupName())) {
				throw QueryFaults
						.MissingParameter("DBParameterGroupName must be supplied for DescribeDBParameters request.");
			}

			final String grpName = ValidationManager.validateIdentifier(
					req.getDbParameterGroupName(), 255, true);
			final String source = ValidationManager.validateParamSource(
					req.getSource(), false);
			final int maxRecord = req.getMaxRecords();
			if (maxRecord < 20 || maxRecord > 100) {
				throw RDSQueryFaults
						.InvalidParameterValue("MaxRecord value must be in between 20 and 100.");
			}
			final String marker = req.getMarker();

			logger.info("DescribeDBParameters: " + " account = " + ac.getId()
					+ " DBParameterGroupName = " + grpName + " Soruce = "
					+ source + " Marker = " + marker + " Max Records = "
					+ maxRecord);

			// select the parameters
			resp = buildResponse(sess, ac, grpName,
					source, marker, maxRecord);

			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "DescribeDBParameters: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp;
	}
	
	@SuppressWarnings("unchecked")
	public static DescribeDBParametersActionResultMessage buildResponse(
			final Session sess, final AccountBean ac, final String grpName,
			final String source, final String marker, final int maxRecords) {
		final DescribeDBParametersActionResultMessage.Builder resp = DescribeDBParametersActionResultMessage.newBuilder();

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
		return resp.buildPartial();
	}
	
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
