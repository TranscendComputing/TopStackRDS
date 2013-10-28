package com.amazonaws.services.rds.model.transform;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;

import com.transcend.rds.message.DescribeEventsActionMessage.DescribeEventsActionRequestMessage;
import com.amazonaws.transform.Unmarshaller;
import com.google.common.base.Strings;
import com.msi.tough.core.Appctx;
import com.msi.tough.core.DateHelper;
import com.msi.tough.rdsquery.QueryUtilV2;
import com.msi.tough.utils.RDSQueryFaults;

public class DescribeEventsActionRequestUnmarshaller implements
Unmarshaller<DescribeEventsActionRequestMessage, Map<String, String[]>>
{
	private static Logger logger = Appctx
	.getLogger(DescribeEventsActionRequestUnmarshaller.class.getName());

	private static DescribeEventsActionRequestUnmarshaller instance;

	public static DescribeEventsActionRequestUnmarshaller getInstance()
	{
		if (instance == null)
		{
			instance = new DescribeEventsActionRequestUnmarshaller();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see com.amazonaws.transform.Unmarshaller#unmarshall(java.lang.Object)
	 */
	@Override
	public DescribeEventsActionRequestMessage unmarshall(Map<String, String[]> in)
	
	{
		final DescribeEventsActionRequestMessage.Builder req =  DescribeEventsActionRequestMessage.newBuilder();
		logger.debug("Unmarshalling (inbound) DescribeEventsActionRequestMessage");
		req.setDuration(QueryUtilV2.getInt(in, "Duration", -1));
		req.setMarker(QueryUtilV2.getString(in, "Marker"));
		req.setMaxRecords(QueryUtilV2.getInt(in, "MaxRecords", 100));
		req.setSourceIdentifier(QueryUtilV2.getString(in, "SourceIdentifier"));
		req.setSourceType(QueryUtilV2.getString(in, "SourceType"));

		// parse the Date
		String[] formats =  new String[]{"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "yyyy-MM-dd'T'HH:mm:ss'Z'", 
				"yyyy-MM-dd'T'HH:mm'Z'", "yyyy-MM-dd'T'HH'Z'"};
		String start = QueryUtilV2.getString(in, "StartTime");
		String end = QueryUtilV2.getString(in, "EndTime");
		boolean startFound = false;
		boolean endFound = false;
		
		logger.debug("StartTime: " + start);
		int i = 0;
		while(start != null && !startFound && i < formats.length){
			try{
				SimpleDateFormat iso8601 =  new SimpleDateFormat(formats[i]);
				++i;
				Date startDate = iso8601.parse(start);
				req.setStartTime(start);
				startFound = true;
			}catch(ParseException e){
				logger.debug("ParseException occured while parsing StartTime parameter with " + formats[i - 1] + ".");
			}
		}
		if(!startFound && start != null && !("".equals(start))) {
			throw RDSQueryFaults.InvalidParameterValue("StartTime parameter cannot be parsed. It must be in ISO8601 format.");
		}
		
		logger.debug("EndTime: " + end);
		i = 0;
		while(end != null && !("".equals(end)) && !endFound && i < formats.length){
			try{
				SimpleDateFormat iso8601 =  new SimpleDateFormat(formats[i]);
				++i;
				Date endDate = iso8601.parse(end);
				req.setEndTime(end);
				endFound = true;
			}catch(ParseException e){
				logger.debug("ParseException occured while parsing EndTime parameter with " + formats[i - 1] + ".");
			}
		}
		if(!endFound && end != null && !("".equals(end))){
			throw RDSQueryFaults.InvalidParameterValue("EndTime parameter cannot be parsed. It must be in ISO8601 format.");
		}
		
		return req.buildPartial();
	}
}
