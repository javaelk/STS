/**
 * 
 */
package uw.star.rts.util;

import java.text.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;


/**
 * @author Shimin Li modified by Robert Shum to add more functions
 */
public class DateUtils {

	/**
	 * Returns current date and time with time zone.
	 * @return current date and time
	 */
	public static String now() {

		String dateFormat = "EEE MMM dd HH:mm:ss z yyyy";
		Calendar cal = Calendar.getInstance();
		DateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setTimeZone(cal.getTimeZone());
		return sdf.format(cal.getTime());
	}
	
	/**
	 * Returns current date and time with time zone.
	 * @param dateFormat the date and time format (Java SimpleDateFormat)
	 * @return current date and time
	 */
	public static String now(String dateFormat) {

		Calendar cal = Calendar.getInstance();
		DateFormat sdf = new SimpleDateFormat(dateFormat);
		sdf.setTimeZone(cal.getTimeZone());
		return sdf.format(cal.getTime());
	}

	public static Date stringToDate(String stringValue) {

		if (stringValue.isEmpty() | stringValue.equalsIgnoreCase("empty")) return null;
		
		DateFormat dateFormat = new SimpleDateFormat(
				"EEE MMM dd HH:mm:ss z yyyy");

		try {                  
			Date convertedDate = dateFormat.parse(stringValue);
			return convertedDate;

		} catch (ParseException e) {
			System.out.println("Cannot parse string to date");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Calculates the time difference between the two dates. If the result is
	 * negative, the first param is less (earlier date) than the 2nd param. If
	 * positive, the 1st param is greater.
	 * 
	 * @param both
	 *            parameters are String formats
	 * @return gets the time difference in hours between the two dates. Returns
	 *         a long object
	 */
	public static long getTimeDifference(String date1, String date2) {

		Date cDate1 = stringToDate(date1);
		Date cDate2 = stringToDate(date2);

		long diff = (cDate1.getTime() - cDate2.getTime()) / (1000 * 60 * 60);

		return diff;

	}
		
	/**
	 * Calculates the time difference between the two dates. If the result is
	 * negative, the first param is less (earlier date) than the 2nd param. If
	 * positive, the 1st param is greater.
	 * 
	 * @param both
	 *            parameters are String formats
	 * @return gets the time difference in hours between the two dates. Returns
	 *         a long object
	 */
	public static int getNumWorkingDays(String date1, String date2) {

		int workDays = 0;

		Date cDate1 = stringToDate(date1);
		Date cDate2 = stringToDate(date2);

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(cDate1);

		Calendar endCal = Calendar.getInstance();
		endCal.setTime(cDate2);

		if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
			startCal.setTime(cDate2);
			endCal.setTime(cDate1);
		}

		do {
			startCal.add(Calendar.DAY_OF_MONTH, 1);
			if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				workDays++;
			}
		} while (startCal.getTimeInMillis() < endCal.getTimeInMillis());

		return workDays;
	}
	
	/**
     * Calculates the time difference between the two dates. If the first date is after the 2nd date
     * it will return an empty string.  If the first input day is not 00:00:00, then we ignore this date
     * because it is not a full day.  If the 2nd input day is not 23:59:59, then we also ignore this date 
     * as it is not a full day.
     * @param date1 Start date
     * @param date2 End date
     * @return a map container containing Map objects with the name and end/start times of a day
     */
    public static Vector<Map<String, String>> getWorkingDays(String date1, String date2) {
          
          String DATE_FORMAT = "MM/dd/yy";
          SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
          DateFormat startDateFormat = new SimpleDateFormat("EEE MMM dd 00:00:00 z yyyy");
          DateFormat endDateFormat = new SimpleDateFormat("EEE MMM dd 23:59:59 z yyyy");
          
          Date cDate1 = stringToDate(date1);
          Date cDate2 = stringToDate(date2);
          
          Calendar startCal = Calendar.getInstance();
          startCal.setTime(cDate1);

          Calendar endCal = Calendar.getInstance();
          endCal.setTime(cDate2);
          
          Vector<Map<String, String>> workingDays = new Vector<Map<String, String>>();
          
          //if the dates are equal or the 2nd date is greater than the 1st, return emtpy container
          if(cDate1.after(cDate2) | cDate1.equals(cDate2)){
                return workingDays;
          }
          
          //we don't care about the time in ms
          startCal.clear(Calendar.MILLISECOND);
          endCal.clear(Calendar.MILLISECOND);
          
          if(startCal.get(Calendar.HOUR_OF_DAY)!=0 | startCal.get(Calendar.SECOND)!=0| 
                      startCal.get(Calendar.MINUTE)!=0){
                            startCal.set(Calendar.HOUR_OF_DAY, 0);
                            startCal.set(Calendar.MINUTE, 0);
                            startCal.set(Calendar.SECOND, 0);
                            startCal.add(Calendar.DATE, 1);
          }
          if(endCal.get(Calendar.HOUR_OF_DAY)!=23 | endCal.get(Calendar.SECOND)!=59| 
                      endCal.get(Calendar.MINUTE)!=59){
                            endCal.set(Calendar.HOUR_OF_DAY, 23);
                            endCal.set(Calendar.MINUTE, 59);
                            endCal.set(Calendar.SECOND, 59);
                            endCal.add(Calendar.DATE, -1);
          }
          
          //do while the days are not a weekend
          while(startCal.getTimeInMillis() < endCal.getTimeInMillis()){
                
                if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
                            && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
                      
                      //add the name, start and end date into the vector
                      Map<String, String> day = new LinkedHashMap<String, String>();
                      
                      day.put("Name", sdf.format(startCal.getTime()));
                      day.put("Start", startDateFormat.format(startCal.getTime()));
                      day.put("End", endDateFormat.format(startCal.getTime()));
                      
                      workingDays.add(day);
                }
                startCal.add(Calendar.DAY_OF_MONTH, 1);
          } 
          
          //if start > end date, return the container
          return workingDays;
    }


    /**
	 * Returns the different date of a given date 
	 * @param date The given date
	 * @param early The number of days different
	 * @return The different date
	 */
	public static String getDate(String date, int difference) {

		if (date == null || date.isEmpty()) {
			return "";
		}
		
		Calendar cal = Calendar.getInstance();
        cal.setTime(stringToDate(date));
		cal.add(Calendar.DATE, difference);
        
		DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
		return dateFormat.format(cal.getTime());
	}
    
	

    /**
	 * Returns the latest next date of a given date from a list of dates
	 * @param date The given date
	 * @param dates The list of dates
	 * @return The latest next date
	 */
	public static String getLatestNextDate(String date, Vector<String> dates) {

		if (date == null || date.isEmpty()) {
			return "";
		}
		
		
		//TODO
		Date nextDate = stringToDate(date);  System.out.println(date);
		
		for(int i = 0; i < dates.size(); i++) {
			if (stringToDate(dates.elementAt(i)).after(nextDate)) {
				if (stringToDate(dates.elementAt(i)).before(nextDate)) {
					nextDate = stringToDate(dates.elementAt(i));
				}
			}
		}
		
		
		return nextDate.toString();
	}
	
	
}
