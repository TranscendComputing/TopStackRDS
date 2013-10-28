package com.msi.tough.rds;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestHarness {
	
	private static String thisClass = "com.msi.tough.rds.TestHarness";

		public static void main(String[] args) {
		
		Logger logger = LoggerFactory.getLogger(thisClass);
		String msg = "";
		try{	
			 /*
			 * Must be in the format hh24:mi-hh24:mi
			 * must be at least two hours in duration
			 */
			String pMainWin = "00:01-02:00";

			if (pMainWin.length() != 11)
			{
				msg = "Validate: PreferredBackupWindow format is " +
				"incorrect: It should be hh24:mi-hh24:mi" +
				"(eg 00:01-02:01) and not " + pMainWin;
				logger.error(msg);
			}
			String startString = pMainWin.substring(0, 5);
			String endString = pMainWin.substring(6, 11);
		
			DateFormat formatter = new SimpleDateFormat("HH:mm");
			Date startDate = (Date)formatter.parse(startString); 
			Date endDate = (Date)formatter.parse(endString);
		
			long diff = (endDate.getTime()/1000) - (startDate.getTime()/1000); 
			if (diff < (2*60*60))
			{
				msg = "Validate: PreferredMaintenanceWindow difference is less "
					+ "than 2 hours (120 minutes) = " + diff/60 + " minutes";
				logger.error(msg);
			}
		
		} catch (ParseException e) {				
			msg = "updateInstance: ExceptionClass: " + e.getClass()
				+ " Msg: " + e.getMessage();
			logger.error(msg);	
		
		} catch (Exception e) {				
			msg = "updateInstance: ExceptionClass: " + e.getClass()
				+ " Msg: " + e.getMessage();
			logger.error(msg);	
		}
	}
}
