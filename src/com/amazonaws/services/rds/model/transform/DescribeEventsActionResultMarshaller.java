package com.amazonaws.services.rds.model.transform;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeEventsActionMessage.DescribeEventsActionResultMessage;
import com.transcend.rds.message.RDSMessage.Event;
import com.amazonaws.transform.Marshaller;
import com.generationjava.io.xml.XMLNode;
import com.msi.tough.core.Appctx;
import com.msi.tough.query.MarshallStruct;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.rdsquery.RDS_Constants;

public class DescribeEventsActionResultMarshaller implements
		Marshaller<String, DescribeEventsActionResultMessage> {
	private final static Logger logger = Appctx
			.getLogger(DescribeEventsActionResultMarshaller.class.getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.amazonaws.transform.Marshaller#marshall(java.lang.Object)
	 */
	@Override
	public String marshall(final DescribeEventsActionResultMessage input)
			 {
		logger.debug("There are (is) " + input.getEventsCount()
				+ " events with the marker, " + input.getMarker());

		final XMLNode nodeResponse =  new XMLNode(
				RDS_Constants.NODE_DESCRIBEEVENTSRESPONSE);
		nodeResponse.addAttr("xmlns", RDS_Constants.AWS_EMR_NAMESPACE);
		final XMLNode descEventsRes = QueryUtilV2.addNode(nodeResponse,
				RDS_Constants.NODE_DESCRIBEEVENTSRESULT);
		if (input.getEventsCount() > 0) {
			final XMLNode events = QueryUtilV2.addNode(descEventsRes,
					RDS_Constants.EVENTS);

			for (final Event e : input.getEventsList()) {
				final XMLNode event = QueryUtilV2.addNode(events, "Event");
				QueryUtilV2.addNode(event, RDS_Constants.MESSAGE, e.getMessage());
				QueryUtilV2.addNode(event, RDS_Constants.SOURCETYPE,
						e.getSourceType());
				QueryUtilV2.addNode(event, RDS_Constants.DATE, e.getDate());
				QueryUtilV2.addNode(event, RDS_Constants.SOURCEIDENTIFIER,
						e.getSourceIdentifier());
			}
		}

		// Setup the response metadata record
		QueryUtilV2.addResponseMetadata(nodeResponse, input.getRequestId());

		return nodeResponse.toString();
	}
}
