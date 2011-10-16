/*
 * TimeUtitilies.java
 *
 * © Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;
import java.util.*;

import net.rim.device.api.i18n.*;
import net.rim.device.api.util.DateTimeUtilities;


/**
 * 
 */
public class TimeUtilities {
    private TimeUtilities() {    }
    
    public static String  constructDate(Calendar cal) {
            int year = cal.get(Calendar.MONTH);
            int month = cal.get(Calendar.MONTH);
            int date = cal.get(Calendar.DATE);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); 
            return constructDate(month, date, dayOfWeek, year);

    }
            
    public static String  constructDate(int month, int date, int dayOfWeek, int year) {
        StringBuffer dateStr = new StringBuffer();
        switch (dayOfWeek) {
            case Calendar.MONDAY: 
                dateStr.append("Monday");
                break;
            case Calendar.TUESDAY: 
                dateStr.append("Tuesday");
                break;
            case Calendar.WEDNESDAY: 
                dateStr.append("Wednesday");
                break;
            case Calendar.THURSDAY: 
                dateStr.append("Thursday");
                break;
            case Calendar.FRIDAY: 
                dateStr.append("Friday");
                break;                   
            case Calendar.SATURDAY: 
                dateStr.append("Saturday");
                break;
            case Calendar.SUNDAY: 
                dateStr.append("Sunday");
                break;                                                                             
        } 
        dateStr.append(", ");
        
        switch (month) {
            case Calendar.JANUARY:
               dateStr.append("Jan");
               break; 
            case Calendar.FEBRUARY:
               dateStr.append("Feb");
               break;
            case Calendar.MARCH:
               dateStr.append("Mar");
               break;
            case Calendar.APRIL:
               dateStr.append("Apr");
               break;
            case Calendar.MAY:
               dateStr.append("May");
               break;                                                            
            case Calendar.JUNE:
               dateStr.append("Jun");
               break; 
            case Calendar.JULY:
               dateStr.append("Jul");
               break;
            case Calendar.AUGUST:
               dateStr.append("Aug");
               break;
            case Calendar.SEPTEMBER:
               dateStr.append("Sep");
               break;
            case Calendar.OCTOBER:
               dateStr.append("Oct");
               break;
            case Calendar.NOVEMBER:
               dateStr.append("Nov");
               break;
            case Calendar.DECEMBER:
               dateStr.append("Dec");
               break;               
        }
        dateStr.append(" ");
        dateStr.append(Integer.toString(date));
        
        if(year > 0) {
            dateStr.append(",");
            dateStr.append(Integer.toString(year));
        }
        return dateStr.toString();
    }
    
    public static String getTimeString(Calendar cal) {
        
        int hour = cal.get(Calendar.HOUR);  
        int minute = cal.get(Calendar.MINUTE);
        boolean isAm = (cal.get(Calendar.AM_PM) == Calendar.AM);
        
        String hourStr;
        String minuteStr;
        
        if(!isAm) {
            hour += 12;
        }
        
        if(hour < 10) {
            hourStr = "0" + Integer.toString(hour);
        }    
        else {
            hourStr = Integer.toString(hour);
        }
        if(minute < 10) {
            minuteStr = "0" + Integer.toString(minute);
        }    
        else {
            minuteStr = Integer.toString(minute);
        }
                
        String timeStr = hourStr + ":" + minuteStr; 
        return   timeStr;     
    }
    
    public static String getDateTimeString(long milliPastMidnight) {
        int hour =  (int)(milliPastMidnight/(60*60*1000));
        int minute = (int)((milliPastMidnight - hour*60*60*1000)/(60*1000));
        if(minute < 10) {
             return String.valueOf(hour) + ":0" + String.valueOf(minute);  
        }
        else {
            return String.valueOf(hour) + ":" + String.valueOf(minute);   
        } 
    }
    public static String getDateTimeString(Calendar cal) {
       return  getDateTimeString(cal, true);
    }
    public static String getDateTimeString(Calendar cal, boolean includeDate) {
        
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR);  
        int minute = cal.get(Calendar.MINUTE);      
        int year = cal.get(Calendar.YEAR);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        boolean isAm = (cal.get(Calendar.AM_PM) == Calendar.AM);
        
        String dateStr = null;
        if(includeDate)
        {
            dateStr  = TimeUtilities.constructDate(month, date, dayOfWeek, year);
        }
        String hourStr;
        String minuteStr;
        
        if(!isAm) {
            hour += 12;
        }
        
        if(hour < 10) {
            hourStr = "0" + Integer.toString(hour);
        }    
        else {
            hourStr = Integer.toString(hour);
        }
        if(minute < 10) {
            minuteStr = "0" + Integer.toString(minute);
        }    
        else {
            minuteStr = Integer.toString(minute);
        }
                
        String timeStr = hourStr + ":" + minuteStr;
        if(!includeDate) {
            return timeStr;
        }
        String dateTimeStr = timeStr + " " + dateStr; 
        return   dateTimeStr;     
    }
    
    public static String getFormatedDateString(String format) {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); //e.g. 2008-06-03T12:15:03Z

        DateFormat dateFormat = new SimpleDateFormat(format); //e.g. 2008-06-03T12:15:03Z
        //Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.setTime(new Date(System.currentTimeMillis())); //now
        String formattedDate = dateFormat.format(c, new StringBuffer(), null).toString(); //formatted in UTC/GMT time
        return formattedDate;
    }
            
   public  static long[] calculateHourMinute(long ms) {
        long hour = ms/(60*60*1000);
        long minute = (ms%(60*60*1000))/(60*1000);
        return new long[] {hour, minute};
    } 
    
    /**
     * get the milli-seconds past midnight
     * @param absMilli the ticks from the UTC start time
     * @return <description>
     */
    public static int getMillisecPastMidnight(long absMilli) {
        
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());
            cal.setTime(new Date(absMilli));
            
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int relativeMilli = hour*60*60*1000 + minute*60*1000; 
            return relativeMilli; 
    }  
    
    /**
     * Convert YR.NO date string(2011-09-25T21:00:00Z) to Date object
     * @param str
     * @return
     */
    public static Date stringToDate(String str) {
    	//DateFormat df = new SimpleDateFormat("EEE MMM dd yyyy z HH:mm");
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	int index = str.indexOf("T");
    	String dateStr = str.substring(0, index);
    	String timeStr = str.substring(index+1, str.length() - 1);
    	String [] tokens = Util.split(dateStr, "-");
    	String [] timeTokens = Util.split(timeStr, ":");
    	
    	TimeZone tz = TimeZone.getTimeZone(DateTimeUtilities.GMT);
    	Calendar cal = Calendar.getInstance(tz);
    	
    	int[] timeFields = new int[7];                                
    	timeFields[0] = Integer.parseInt(tokens[0]); // year
    	timeFields[1] = Integer.parseInt(tokens[1]) - 1; // month 
    	timeFields[2] = Integer.parseInt(tokens[2]); // day
    	
    	timeFields[3] = Integer.parseInt(timeTokens[0]); // hour
    	timeFields[4] = Integer.parseInt(timeTokens[1]); // minute 
    	timeFields[5] = Integer.parseInt(timeTokens[2]); // seconds 
    	timeFields[6] = 0; // tick
    	
    	DateTimeUtilities.setCalendarFields(cal, timeFields);
    	Date date = cal.getTime();
    	
    	System.out.print("converted GMT date:" + date.toString());
    	cal.setTimeZone(TimeZone.getDefault());
    	date = cal.getTime();
    	System.out.print("converted Default date:" + date.toString());
    	return date;
    	//String newStr = dateStr + " " + timeStr;
    	//Date today = df.parse(newStr);
    	
    }
    
    public static String getWeekDayStr(int dayInWeek) {
        StringBuffer dateStr = new StringBuffer();
        switch (dayInWeek) {
            case Calendar.MONDAY: 
                dateStr.append("Monday");
                break;
            case Calendar.TUESDAY: 
                dateStr.append("Tuesday");
                break;
            case Calendar.WEDNESDAY: 
                dateStr.append("Wednesday");
                break;
            case Calendar.THURSDAY: 
                dateStr.append("Thursday");
                break;
            case Calendar.FRIDAY: 
                dateStr.append("Friday");
                break;                   
            case Calendar.SATURDAY: 
                dateStr.append("Saturday");
                break;
            case Calendar.SUNDAY: 
                dateStr.append("Sunday");
                break;                                                                             
        }
        return dateStr.toString();
    }
} 
