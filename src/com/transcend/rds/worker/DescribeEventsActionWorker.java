/**
 * 
 */
package com.transcend.rds.worker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.core.HibernateUtil;
import com.msi.tough.model.AccountBean;
import com.msi.tough.model.EventLogBean;
import com.msi.tough.model.EventLogTagBean;
import com.msi.tough.query.ErrorResponse;
import com.msi.tough.query.QueryFaults;
import com.msi.tough.query.ServiceRequestContext;
import com.msi.tough.utils.EventUtil;
import com.msi.tough.utils.RDSQueryFaults;
import com.msi.tough.workflow.core.AbstractWorker;
import com.transcend.rds.message.DescribeEventsActionMessage.DescribeEventsActionRequestMessage;
import com.transcend.rds.message.DescribeEventsActionMessage.DescribeEventsActionResultMessage;
import com.transcend.rds.message.RDSMessage.Event;
//import com.transcend.rds.message.RDSMessage.Event.EventType;

public class DescribeEventsActionWorker extends 
		AbstractWorker<DescribeEventsActionRequestMessage, DescribeEventsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeEventsActionWorker.class.getName());

    
	   
    /**
    * We need a local copy of this doWork to provide the transactional
    * annotation.  Transaction management is handled by the annotation, which
    * can only be on a concrete class.
    * @param req
    * @return
    * @throws Exception
    */
   @Transactional
   public DescribeEventsActionResultMessage doWork(
           DescribeEventsActionRequestMessage req) throws Exception {
       logger.debug("Performing work for DescribeEventsAction.");
       return super.doWork(req, getSession());
   }
   
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.msi.tough.query.AbstractProxyAction#
	protected DescribeEventsActionResultMessage doWork0(DescribeEventsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

	 * javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	@Override
	protected DescribeEventsActionResultMessage doWork0(DescribeEventsActionRequestMessage req,
ServiceRequestContext context) throws Exception {

		final Logger logger = LoggerFactory
				.getLogger(this.getClass().getName());
		DescribeEventsActionResultMessage.Builder result = null;
		String msg = "";
		Session sess = null;
		try {
			sess = HibernateUtil.newSession();
			sess.beginTransaction();
			final AccountBean ac = context.getAccountBean();
			logger.debug("DescribeEvents: " + req.toString());

			Integer duration = req.getDuration();
			final SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss");
			Date startTime = null, endTime = null;
			String sourceId = null, sourceType = null;
			if(req.getStartTime() != null && !("".equals(req.getStartTime())))
				startTime = sdf.parse(req.getStartTime());
			if(req.getEndTime() != null && !("".equals(req.getEndTime())))
				endTime = sdf.parse(req.getEndTime());
			final String marker = req.getMarker();
			final Integer maxRecords = req.getMaxRecords();
			if(!"".equals(req.getSourceIdentifier()))
				sourceId = req.getSourceIdentifier();
			if(!"".equals(req.getSourceType()) && req.getSourceType() != null )
				sourceType = req.getSourceType();

			if (duration != -1 && (startTime != null || endTime != null)) {
				logger.debug("User passed Duration parameter with StartTime and/or EndTime parameters.");
				throw QueryFaults
						.InvalidParameterCombination("If Duration is specified, both StartTime and EndTime must be omitted.");
			}
			if (maxRecords < 20 || maxRecords > 100) {
				logger.debug("The passed value for MaxRecord parameter is out of range. The value must be in [20-100] range.");
				throw RDSQueryFaults
						.InvalidParameterValue("MaxRecord has to be equal to or greater than 20 while being less or equal to 100.");
			}
			if (sourceId != null && sourceType == null) {
				logger.debug("User passed SourceIdentifier parameter, but failed to supply SourceType parameter.");
				throw QueryFaults
						.MissingParameter("If SourceIdentifier is supplied, SourceType has to be provided.");
			}
			if (sourceType != null && !sourceType.equals("db-instance")
					&& !sourceType.equals("db-parameter-group")
					&& !sourceType.equals("db-security-group")
					&& !sourceType.equals("db-snapshot")) {
				logger.debug("SourceType value is not valid.");
				throw RDSQueryFaults
						.InvalidParameterValue("SourceType is specified with invalid parameter value.");
			}
			if (startTime != null && endTime != null
					&& startTime.getTime() > endTime.getTime()) {
				logger.debug("User StartTime > EndTime parameters.");
				throw QueryFaults
						.InvalidParameterCombination("StartTime cannot be > EndTime.");
			}

			// convert the Duration into StartTime if Duration is being used
			// instead of StartTime/EndTime
			if (startTime == null && endTime == null) {
				if (duration == -1) {
					duration = 60;
				}
				final Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				final int amt = -duration;
				cal.add(Calendar.MINUTE, amt);
				startTime = cal.getTime();
				logger.debug("Duration is applied to startTime: "
						+ startTime.toString());
			}

			Collection<Event> events = null;
			final List<EventLogTagBean> eventTags = EventUtil
					.queryEventLogTags(sess, ac.getId(), sourceId, startTime,
							endTime, marker, maxRecords);
			logger.debug(eventTags.size() + " event log tags are returned.");
			events = new LinkedList<Event>();
			for (final EventLogTagBean temp : eventTags) {
				// logger.debug("EventLogTag: " + temp.getEventId() + "; " +
				// temp.getTag());
				final EventLogBean event = (EventLogBean) sess.load(
						EventLogBean.class.getName(), temp.getEventId());
				if (event == null) {
					logger.debug("Failed to load EventLogBean with id="
							+ temp.getEventId());
				} else if (sourceType == null || event.getType().equals(sourceType)) {
						Event.Builder newEvent = Event.newBuilder();
						newEvent.setDate(DateHelper.getISO8601Date(event.getCreatedTime()));
						newEvent.setMessage(event.getMessage());
						newEvent.setSourceIdentifier(temp.getTag());
						newEvent.setSourceType(event.getType());
						events.add(newEvent.buildPartial());
				}
			}
			result = DescribeEventsActionResultMessage.newBuilder();
			result.addAllEvents(events);

		} catch (final ErrorResponse e) {
			sess.getTransaction().rollback();
			throw e;
		} catch (final Exception e) {
			sess.getTransaction().rollback();
			msg = "DescribeEvents: Class: " + e.getClass() + "Msg:"
					+ e.getMessage();
			logger.error(msg);
			throw RDSQueryFaults.InternalFailure();
		} finally {
			if (sess != null) {
				sess.close();
			}
		}
		return result.buildPartial();
	}
}
