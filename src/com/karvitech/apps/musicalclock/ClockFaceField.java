/*
 * ClockFaceField.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.apps.musicalclock;
import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.ui.Field;
//import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.UiApplication;
/**
 * 
 */
abstract class ClockFaceField extends Field implements RealtimeClockListener {
    Calendar _cal;
    protected MusicalClockSettingItem _settingItem;
    
    protected int _month;
    protected int _date;
    protected int _hour;
    protected int _minute;
    protected boolean _isAm;
    protected int _dayOfWeek;
    
    protected String _dateStr;
    protected boolean _militaryTime;
    
    
    ClockFaceField(MusicalClockSettingItem settingItem, boolean militaryTime) {
         _militaryTime = militaryTime;
        _settingItem = settingItem;
        UiApplication.getApplication().addRealtimeClockListener(this);
        clockUpdated();
    }
    
    public void setMilitaryTime(boolean useMilitaryTime) {
        _militaryTime = useMilitaryTime;
    }
    
   /**
    * implement the RealtimeClockListener interface
    */
    public void clockUpdated() 
    {        

                //_cal.setTimeZone(TimeZone.getTimeZone(item._strDisplayName));
                _cal = Calendar.getInstance(TimeZone.getTimeZone(_settingItem.getTimeZoneName()));
                _month = _cal.get(Calendar.MONTH);
                _date = _cal.get(Calendar.DATE);
                _hour = _cal.get(Calendar.HOUR);
                _minute = _cal.get(Calendar.MINUTE);
                _isAm = _cal.get(Calendar.AM_PM) == Calendar.AM;

                if(_hour == 0 && !_militaryTime) { // not sure if this is U.S only, if so for other country this needs to be changed
                    _hour = 12;
                }
                else if(_militaryTime && !_isAm) {
                    _hour += 12;
                }
                
                _dayOfWeek = _cal.get(Calendar.DAY_OF_WEEK);  
                _dateStr = constructDate(_month, _date, _dayOfWeek);
                
        
        
        this.invalidate();
        
    }
    
    static String  constructDate(int month, int date, int dayOfWeek) {
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
        return dateStr.toString();
    }
    
    public boolean isFocusable() {
        return false;
    }
    protected boolean trackwheelClick(int status, int time) {
        AlarmListScreen.showScreen();
        return true;
    }

} 
