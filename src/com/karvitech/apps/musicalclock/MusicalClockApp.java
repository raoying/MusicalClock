//#preprocess
/*
 * MusicalClockApp.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

/**
 */

package com.karvitech.apps.musicalclock;
//import com.karvitech.apps.musicalclock.*;
import java.util.*;
//import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.MenuItem;
import net.rim.blackberry.api.homescreen.*;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.system.*;
import com.karvitech.api.appTools.*;

//ifdef VER_4_7_0_AND_ABOVE
import net.rim.device.api.lcdui.control.*;
//endif


/**
 *
 */
public final class MusicalClockApp extends UiApplication implements SystemListener2
{
/*    public static int KEY_TERM_ACCEPTED = 1;
    public static int KEY_GLOBAL_SETTINGS = 2;
    public static int KEY_ALARM_LIST = 3;
    public static int KEY_APP_VERSION = 4;
  */  
  
    public static String MOBI_HAND_UPGRADE_URL = "http://www.mobihand.com/product.asp?id=386563&n=Musical-Clock---Multiple-Alarms";
    public static String MOBI_HAND_MORE_APP_URL = "http://www.mobihand.com/platformMain.asp?platform=5&sString=karvi&Submit=Search";
    public static String APP_WORLD_VENDOR_ID = "5010"; // my vendor id
    public static String APP_WORLD_UPGRADE_ID = "25151"; // the paid version id
    
    private boolean _charging = false;
    private Runnable _keepBacklightOnRunnable = null;
    private int _keepBacklightOnRunnableId;
    
    public static void main( String[] args )
    {
        if ( args != null && args.length > 0 && args[0].equals("init") ){         
        } else {
            (new MusicalClockApp()).enterEventDispatcher();
        }        
    }
    
    private MusicalClockApp() {
        try {    
//ifdef VER_4_7_0_AND_ABOVE        
            int directions = DirectionControl.DIRECTION_EAST | DirectionControl.DIRECTION_WEST 
                                |DirectionControl.DIRECTION_NORTH ;
            net.rim.device.api.ui.Ui.getUiEngineInstance().setAcceptableDirections(directions);
//endif
            // register event log
            EventLogger.register(0xbd2b228daa4b3197L, "MusicalClockApp", EventLogger.VIEWER_STRING); // com.karvitech.apps.MusicalClockApp

            initConfig();
            
            // add the clock listener
            this.addRealtimeClockListener(MusicalClockContext.getInstance());
            this.addSystemListener(this);
            
            Boolean termAccepted = (Boolean)(Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_TERM_ACCEPTED));
            if(termAccepted != null && termAccepted.booleanValue() == false) {
                MusicalClockApp.this.invokeLater(new Runnable() 
                {
                    public void run()
                    {
                        if(TermOfUseDialog.showDialog() == TermOfUseDialog.DECLINED) {
                            System.exit(0);                    
                        }
                        else {
                            Configuration.getInstance().setKeyValue(MusicalClockContext.KEY_TERM_ACCEPTED, new Boolean(true));
                            Configuration.getInstance().saveSettings();
                        }
                    }
                });
            }
            
         //   MusicalClockGlobalSettingItem globalSettings = (MusicalClockGlobalSettingItem)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS);
          //  MusicalClockMainScreen.getInstance().setStyle(globalSettings.getClockColor());
          //  MusicalClockMainScreen.getInstance().setBac (globalSettings.getClockColor());
            try {
                    pushScreen( MusicalClockMainScreen.getInstance() );
            }
            catch(Exception e) {
                EventLogger.logEvent( 0xbd2b228daa4b3197L, ("Failed to add main screen:" + e.toString() + " " + e.getMessage()).getBytes());
            }
        }
        catch (Exception e) {
            if( e instanceof ControlledAccessException ) {
                Application.getApplication().invokeLater(new Runnable() 
                    {
                        public void run()
                        {
                            Status.show("Due to the security settings of this device, the device needs to be rebooted first, please pull and reinsert the battery to run " + ApplicationDescriptor.currentApplicationDescriptor().getLocalizedName());
                            //CodeModuleManager.promptForResetIfRequired();             
                        }
                    });
            }
        } // catch
    }
    
    private void initConfig() {
        Configuration.init( 0xae4758ef9330b81L ); //com.karvitech.apps.musicalclock.MusicalClockSettings   
        Configuration config = Configuration.getInstance();
        
        // Global settings
        if(config.getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS) == null) {
            MusicalClockGlobalSettingItem globalSettings = new MusicalClockGlobalSettingItem();        
            config.setKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS, globalSettings);
        }

        // Global settings2
        if(config.getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2) == null) {
            MusicalClockGlobalSettingItem2 globalSettings2 = new MusicalClockGlobalSettingItem2();        
            config.setKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2, globalSettings2);
        }
                
        // Term Accepted
        if(config.getKeyValue(MusicalClockContext.KEY_TERM_ACCEPTED) == null) {
            Boolean termAccepted = new Boolean(false);
            config.setKeyValue(MusicalClockContext.KEY_TERM_ACCEPTED, termAccepted);
        }
        
        // Alarm list
        if(config.getKeyValue(MusicalClockContext.KEY_ALARM_LIST) == null) {
            Vector clocks = new Vector();
            //clocks.addElement(new MusicalClockSettingItem(Calendar.getInstance().getTimeZone()));
           // clocks.addElement(new MusicalClockSettingItem(TimeZone.getTimeZone("GMT")));
            config.setKeyValue(MusicalClockContext.KEY_ALARM_LIST, clocks);
        }
        
        // first run time
        if(config.getKeyValue(MusicalClockContext.KEY_FIRST_RUN_TIME) == null) {
            Long firstRunTime = new Long(System.currentTimeMillis());
            config.setKeyValue(MusicalClockContext.KEY_FIRST_RUN_TIME, firstRunTime); 
        }  
        
        // temperature unit
        if(config.getKeyValue(MusicalClockContext.KEY_WEATHER_USE_CELSIUS) == null) {
        	// yr.no uses Celsius
            Boolean useCelsius = new Boolean(true);
            config.setKeyValue(MusicalClockContext.KEY_WEATHER_USE_CELSIUS, useCelsius); 
        }  
        // save the initial settings
        config.saveSettings();
    } 
    
    /**
     * override the super class to refresh ad
     */
//#ifdef FREE_VERSION
    public void activate() {
        MusicalClockMainScreen.getInstance().refresh();
    }
//#endif
    
    // implementing SystemListener   
    // bring the clock to forground when charging.
    public void batteryStatusChange(int status) {
        if((status & DeviceInfo.BSTAT_CHARGING) > 0 || (status & DeviceInfo.BSTAT_AC_CONTACTS) > 0 || (status & DeviceInfo.BSTAT_IS_USING_EXTERNAL_POWER) >0) 
        {
            if(_charging != true) {
                Configuration config = Configuration.getInstance(); 
                MusicalClockGlobalSettingItem2 globalSettings2 = (MusicalClockGlobalSettingItem2)config.getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2);  
                              
                _charging = true;
    
                // request foreground even if it already has it
                // because the build in clock may get the foreground if this app does not request foreground(when it is already foreground)
                //System.out.println("Going to foreground when batteryStatusChange");
                if(globalSettings2.showWhenCharing()) {
                    this.requestForeground();
                }
                
                if(globalSettings2.keepBackLightOn()) {

                    if(_keepBacklightOnRunnable == null) {
                        // commented out the keep screen on logic as it may pose a securty issue. 
                        // need to add a popup to make sure this is user confirmed because it prevents the phone going into lock, so has security problems 
                        _keepBacklightOnRunnable = new Runnable() {
                                                            public void run() {
                                                                try { 
                                                                    // only keep back light on if this app is foreground
                                                                    if(MusicalClockApp.this.isForeground()) {
                                                                        Backlight.enable(true);
                                                                    }
                                                                }
                                                                catch(Exception e) {
                                                                    if(e instanceof ControlledAccessException) {
                                                                        MusicalClockApp.this.invokeLater(new Runnable() 
                                                                                {
                                                                                    public void run()
                                                                                    {
                                                                                        cancelInvokeLater(_keepBacklightOnRunnableId);
                                                                                        _keepBacklightOnRunnable = null;
                                                                                        //Dialog.inform("Music Clock does not have the permission to keep back light on");
                                                                                        
                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            }
                                                    };
                        
                        int backlightOffTime = Backlight.getTimeoutDefault();
                        
                        // factor in the fade time, otherwise screen goes to fade and then go back alive
                        if(backlightOffTime > 5) {
                            backlightOffTime -= 5;
                        }
                        _keepBacklightOnRunnableId = this.invokeLater(_keepBacklightOnRunnable, backlightOffTime*1000, true);
                    }                
                }
            }          
            
        }
        else {
            
            _charging = false;
            
            // stop the keep back light on runnable
            if(_keepBacklightOnRunnable != null) {
                this.cancelInvokeLater(_keepBacklightOnRunnableId);
                _keepBacklightOnRunnable = null;
            }
        }
        
        /*
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("Status is:" + status).getBytes());
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("DeviceInfo.BSTAT_CHARGING is:" + DeviceInfo.BSTAT_CHARGING).getBytes()); // 1
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("DeviceInfo.BSTAT_AC_CONTACTS is:" + DeviceInfo.BSTAT_AC_CONTACTS).getBytes()); // 16
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("DeviceInfo.BSTAT_IS_USING_EXTERNAL_POWER is:" + DeviceInfo.BSTAT_IS_USING_EXTERNAL_POWER).getBytes()); // 4
        */
    }
    
    public void batteryGood() {}
    
    public void batteryLow()  {}
    public void powerOff()  {
        Configuration.getInstance().forceSaveSettings();
    }
    public void powerUp() {}
    public void backlightStateChange(boolean on) {}
    public void   cradleMismatch(boolean mismatch) {}
    public void   fastReset() {
        Configuration.getInstance().forceSaveSettings();
    }
    public void    powerOffRequested(int reason) {}
    
    public  void   usbConnectionStateChange(int state) {
        /*if(SystemListener2.USB_STATE_CABLE_CONNECTED == state) {
            if(!this.isForeground()) {
                System.out.println("Going to foreground when usbConnectionStateChange");
                this.requestForeground();
            }
        }*/
        /*
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("State is:" + state).getBytes());
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("SystemListener2.USB_STATE_CABLE_CONNECTED is:" + SystemListener2.USB_STATE_CABLE_CONNECTED).getBytes());
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("DeviceInfo.BSTAT_AC_CONTACTS is:" + DeviceInfo.BSTAT_AC_CONTACTS).getBytes());
        EventLogger.logEvent( 0xbd2b228daa4b3197L, ("DeviceInfo.BSTAT_IS_USING_EXTERNAL_POWER is:" + DeviceInfo.BSTAT_IS_USING_EXTERNAL_POWER).getBytes());  
        */
    }
    
}
