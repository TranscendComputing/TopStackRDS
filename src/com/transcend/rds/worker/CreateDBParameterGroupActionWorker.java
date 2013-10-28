/**
 * 
 */
package com.transcend.rds.worker;

import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.BaseException;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.rds.RdsDbparameterGroup;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.rds.ValidationManager;
import com.msi.tough.utils.AccountUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.utils.rds.ParameterGroupEntity;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.CreateDBParameterGroupActionMessage.CreateDBParameterGroupActionRequestMessage;
import com.transcend.rds.message.CreateDBParameterGroupActionMessage.CreateDBParameterGroupActionResultMessage;

/**
 * @author tdhite
 */
public class CreateDBParameterGroupActionWorker 
		extends AbstractWorker<CreateDBParameterGroupActionRequestMessage, CreateDBParameterGroupActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(CreateDBParameterGroupActionWorker.class.getName());
	
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public CreateDBParameterGroupActionResultMessage doWork(
           CreateDBParameterGroupActionRequestMessage req) throws Exception {
       logger.debug("Performing work for CreateDBParameterGroupAction.");
       return super.doWork(req, getSession());
   }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected CreateDBParameterGroupActionResultMessage doWork0(CreateDBParameterGroupActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected CreateDBParameterGroupActionResultMessage doWork0(CreateDBParameterGroupActionRequestMessage req,
			ServiceRequestContext context) throws Exception {

		CreateDBParameterGroupActionResultMessage resp = null;
		String msg = "";
		final Session sess = HibernateUtil.newSession();
		try {
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			final long userId = ac.getId();

			logger.debug("Request object content: " + req.toString());
			if (req.getDbParameterGroupFamily() == null
					|| req.getDbParameterGroupName() == null
					|| req.getDescription() == null) {
				throw QueryFaults.MissingParameter();
			}

			// Validate request attributes
			final String grpName = ValidationManager.validateIdentifier(
					req.getDbParameterGroupName(), 255, true);

			final String grpFamily = ValidationManager.validateParamGrpFamily(
					req.getDbParameterGroupFamily(), true);

			final String desc = ValidationManager.validateIdentifier(
					req.getDescription(), 255, true);

			// Confirm that this parameterGroup does not already exist
			final RdsDbparameterGroup grpRec = ParameterGroupEntity
					.getParameterGroup(sess, grpName, ac.getId());

			if (grpRec != null) {
				throw RDSQueryFaults.DBParameterGroupAlreadyExists();
			}

			// insert record
			final RdsDbparameterGroup pGrpRec = insertParameterGroup(sess, req, ac);

			// build response document - contains newly created DBParameterGroup
			resp = toDBParameterGroup(pGrpRec);
			sess.getTransaction().commit();
		} catch (final ErrorResponse rde) {
			sess.getTransaction().rollback();
			throw rde;
		} catch (final Exception e) {
			e.printStackTrace();
			sess.getTransaction().rollback();
			msg = "CreateDBParameterGroup: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			sess.close();
		}
		return resp;
	}
	
	
	public static RdsDbparameterGroup insertParameterGroup(final Session sess,
			final CreateDBParameterGroupActionRequestMessage createDBParameterGroup,
			final AccountBean ac) {
		final String paramGrpFamily = createDBParameterGroup
				.getDbParameterGroupFamily();
		final String paramGrpName = createDBParameterGroup
				.getDbParameterGroupName();
		final String description = createDBParameterGroup.getDescription();

//		logger.info("insertParameterGroup: for " + " account = " + ac.getId()
//				+ " ParameterGroupName" + paramGrpName + " ParamterGroupFamily"
//				+ paramGrpFamily + " Description " + description);

		// build DBParameterGroup record
		final RdsDbparameterGroup paramGroup = new RdsDbparameterGroup(ac,
				paramGrpName, paramGrpFamily, description);

		sess.save(paramGroup);

		// copy the default parameters from the DBParameterGroupFamily
		final AccountBean sac = AccountUtil.readAccount(sess, 1L);
		if (paramGrpFamily.toUpperCase().equals("MYSQL5.1")) {
			logger.debug("Inserting Parameters into " + paramGrpName
					+ " with MySQL5.1 family Parameters");
			final RdsDbparameterGroup parent = getParameterGroup(sess,
					"default.mysql5.1", sac.getId());
			logger.debug("There are " + parent.getParameters().size()
					+ " parameters to copy.");
			ParameterGroupEntity.copyAndSetParamGroup(sess, paramGroup, parent.getParameters());
		} else if (paramGrpFamily.toUpperCase().equals("MYSQL5.5")) {
			logger.debug("Inserting Parameters into " + paramGrpName
					+ " with MySQL5.5 family Parameters");
			final RdsDbparameterGroup parent = getParameterGroup(sess,
					"default.mysql5.5", sac.getId());
			logger.debug("There are " + parent.getParameters().size()
					+ " parameters to copy.");
			ParameterGroupEntity.copyAndSetParamGroup(sess, paramGroup, parent.getParameters());
		}

		logger.info("insertParameterGroup: Successfully inserted "
				+ "DBParameter Group");
		return paramGroup;
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
		final List<RdsDbparameterGroup> result = ParameterGroupEntity.
				selectDBParameterGroups(sess, paramGrpName, acid, "", 1);
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
	
	public static CreateDBParameterGroupActionResultMessage toDBParameterGroup(
			final RdsDbparameterGroup p) {
		final CreateDBParameterGroupActionResultMessage.Builder g = CreateDBParameterGroupActionResultMessage.newBuilder();
		g.setDbParameterGroupFamily(p.getDbparameterGroupFamily());
		g.setDbParameterGroupName(p.getDbparameterGroupName());
		g.setDescription(p.getDescription());
		return g.buildPartial();
	}
}
