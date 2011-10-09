/*
 * MusicalClockGlobalSettingItem2.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */
package com.karvitech.apps.musicalclock;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.ui.Color;



/**
 * 
 */
class MusicalClockGlobalSettingItem2 implements Persistable {

    static final int TIME_FORMAT_12 = 0;
    static final int TIME_FORMAT_24 = 1;
    

    boolean _militaryTime = false;
    boolean _showWhenCharging = false;
    boolean _keepBackLightOn = false;
    
    MusicalClockGlobalSettingItem2() {    }
    
    
    public void setMilitaryTime(boolean militaryTime) {
        _militaryTime = militaryTime;
    }    
    

    public boolean isMilitaryTime() {
        return _militaryTime;
    }  
    
    public boolean showWhenCharing() {
        return _showWhenCharging;
    }  
    
    public boolean keepBackLightOn() {
        return _keepBackLightOn;
    }   
    
    public void setKeepBackLightOn(boolean on) {
        _keepBackLightOn = on;
    }  
     
    public void setshowWhenCharing(boolean show) {
        _showWhenCharging = show;
    }          
}
