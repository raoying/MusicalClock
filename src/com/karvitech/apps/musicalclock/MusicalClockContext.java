//#preprocess
/*
 * MusicalClockContext.java
 *
 * © Karvi Technologies, 2009-2014
 * Confidential and proprietary.
 */


package com.karvitech.apps.musicalclock;


import java.util.*;
import net.rim.device.api.ui.UiApplication;
import com.karvitech.api.appTools.*;
import net.rim.device.api.system.*;
import net.rim.device.api.util.*;
import com.karvitech.apps.alarmlib.*;
/**
 * 
 */
class MusicalClockContext implements RealtimeClockListener {
    // configuration keys    
    public static int KEY_TERM_ACCEPTED = 1;
    public static int KEY_GLOBAL_SETTINGS = 2;
    public static int KEY_ALARM_LIST = 3;
    public static int KEY_APP_VERSION = 4;
    public static final int KEY_FIRST_RUN_TIME = 5;
    public static final int KEY_GLOBAL_SETTINGS_2 = 6;
            
    private static MusicalClockContext _instance;
    private Vector _alarmItemList;
    
    public static MusicalClockContext getInstance() {
        if(_instance == null) {
            _instance = new MusicalClockContext();
        }
        return _instance;
    }
    
    private MusicalClockContext() {
        _alarmItemList = (Vector)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST);
    }
    
    Vector getAlarmItemList() {
        return _alarmItemList;
    }
    
   /**
    * implement the RealtimeClockListener interface
    */
    public void clockUpdated() 
    {      
        checkAlarms();
    } 
    
    private void checkAlarms() {
        for(int i=0; i < _alarmItemList.size();i++) {
            AlarmItem item = (AlarmItem)_alarmItemList.elementAt(i);
            if(item._alarmOn && alarmReady(item)) {
//#ifdef FREE_VERSION                
                if(UiApplication.getUiApplication() instanceof MusicalClockApp) {
                    UiApplication.getUiApplication().requestForeground();
                    // show the full screen ad
                    UiApplication uiApp = UiApplication.getUiApplication();
                    if(!(uiApp.getActiveScreen() instanceof AdScreen)) {
                        // only shows the ad screen if it is not already shown
                        uiApp.pushScreen(new AdScreen());
                    }
                }
//#endif  
                item.startAlarm();                
              
            }
        }
    }    
    
    // compare if the current time matches the alarm time in the alarm.
    private boolean alarmReady(AlarmItem item) {
        long currentTime = System.currentTimeMillis();        
        int[] calFields = null;
        long timeDiff;
        Calendar cal;
        
        calFields = DateTimeUtilities.getCalendarFields(Calendar.getInstance(), null);  //year, month, day, hour, minute, second, tick
        
        if(item._alarmRepeating) {
            // repeating alarm, check if the alarm is set for the weekday
            cal = Calendar.getInstance();
            if(!item.isAlarmSetForTheDay(cal.get(Calendar.DAY_OF_WEEK))) {
                return false;
            }
            
            cal = DateTimeUtilities.getDate((int)item._alarmTime);
            timeDiff = Math.abs(cal.getTime().getTime() - currentTime);
            if(timeDiff <= DateTimeUtilities.ONESECOND*30) {
                return true;
            }            
        } 
        else {
            // one time alarm, if it is less than 20 seconds from current time then fire it
            timeDiff = Math.abs(currentTime - item.getAlarmTime());
            if(timeDiff <= DateTimeUtilities.ONESECOND*30) {
                return true;
            }
        }
        
        return false;
    }
       
}

