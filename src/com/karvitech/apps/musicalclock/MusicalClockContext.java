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
import net.rim.device.api.ui.component.Dialog;

import com.karvitech.api.appTools.*;
import com.karvitech.api.weather.UpdateWeatherRunnable;

import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

import com.karvitech.apps.alarmlib.*;
/**
 * 
 */
class MusicalClockContext implements RealtimeClockListener {
//#ifdef FREE_VERSION  
	public static final boolean free_version = true;
//#else
	public static final boolean free_version = false;
//#endif	
    // configuration keys    
	public static int MINUTES_INTERVAL = 60*24;
    public static int KEY_TERM_ACCEPTED = 1;
    public static int KEY_GLOBAL_SETTINGS = 2;
    public static int KEY_ALARM_LIST = 3;
    public static int KEY_APP_VERSION = 4;
    public static final int KEY_FIRST_RUN_TIME = 5;
    public static final int KEY_GLOBAL_SETTINGS_2 = 6;
    public static final int KEY_WEATHER_LOCATION_LAT = 7;
    public static final int KEY_WEATHER_LOCATION_LONG = 8;
    public static final int KEY_WEATHER_DISABLED = 9;
    public static final int KEY_WEATHER_USE_CELSIUS = 10;
    public static final int KEY_FIRST_TRIAL_RUN_TIME = 11; // this is used for weather trial
    public static final int KEY_TRIAL_EXPIRE_SHOWN = 12; // user has been shown the trial expired dialog
    
    private static int TRIAL_DAYS = 7;
    private static MusicalClockContext _instance;
    private Vector _alarmItemList;
    private int _minutesPassed;
    
    public static MusicalClockContext getInstance() {
        if(_instance == null) {
            _instance = new MusicalClockContext();
        }
        return _instance;
    }
    
    private MusicalClockContext() {
        _alarmItemList = (Vector)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST);
    }
    
    public static boolean isTrialExpired() {
    	if(!free_version) {
    		// paid version, never expire
    		return false;
    	}
        Long firstRunTime = (Long)Configuration.getInstance().getKeyValue(KEY_FIRST_TRIAL_RUN_TIME);
        // first run time
        if(firstRunTime == null) {
        	Dialog.inform("The weather feature is a seven day trial in the free version, it is fully supported in the paid version.");
        	firstRunTime = new Long(System.currentTimeMillis());
            Configuration.getInstance().setKeyValue(MusicalClockContext.KEY_FIRST_TRIAL_RUN_TIME, firstRunTime); 
        }  
        long runTime = System.currentTimeMillis() - firstRunTime.longValue();
        if(runTime >= DateTimeUtilities.ONEDAY*TRIAL_DAYS)  {
            return true;
        }
        else {
            return false;
        }        
    }
    public static boolean showWeather() {
    	Boolean weatherDisabled = (Boolean)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_WEATHER_DISABLED);
    	if((weatherDisabled!= null && weatherDisabled.booleanValue()) || isTrialExpired()) {
    		return false;
    	}
    	return true;
    }
    
    public boolean showInCelsiusUnit() {
    	Boolean showInCelsius = (Boolean)Configuration.getInstance().getKeyValue(KEY_WEATHER_USE_CELSIUS);
    	return showInCelsius.booleanValue();
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
        checkWeather();
    } 
    private void checkWeather() {
    	if(free_version) {
    		boolean trialExpired = isTrialExpired();
    		if(trialExpired) {
	            Boolean expireDialogShown = (Boolean)(Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_TRIAL_EXPIRE_SHOWN));
	            if(expireDialogShown != null && expireDialogShown.booleanValue() == false) {
	            	
	                UiApplication.getUiApplication().invokeLater(new Runnable() 
	                {
	                	public void run() {
	                		UpgradeDialog.show(true);
	                	}
	                });
                    Configuration.getInstance().setKeyValue(MusicalClockContext.KEY_TRIAL_EXPIRE_SHOWN, new Boolean(true));
                    Configuration.getInstance().saveSettings();
	            }
	            return;
    		} // trial expired
    	}
    	
    	if(!showWeather()) {
    		return;
    	}
    	_minutesPassed += 1;
    	if(_minutesPassed > MINUTES_INTERVAL) {
    		// needs to get the new weather data
    		_minutesPassed = 0;
    		Configuration config = Configuration.getInstance();
    		
            double longitude = ((Float)config.getKeyValue(KEY_WEATHER_LOCATION_LONG)).doubleValue();
            double latitude = ((Float)config.getKeyValue(KEY_WEATHER_LOCATION_LAT)).doubleValue();;
            new Thread(new UpdateWeatherRunnable(latitude,longitude,MusicalClockMainScreen.getInstance())).start();
    	}
    	MusicalClockMainScreen.getInstance().updateWeatherIfNeeded();
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

