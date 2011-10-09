/*
 * MusicalClockGlobalSettingItem.java
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
class MusicalClockGlobalSettingItem implements Persistable {
    static final int DIGITAL = 0;
    static final int ANALOG =1;
    static final int LCD = 2;
    static final int TIME_FORMAT_12 = 0;
    static final int TIME_FORMAT_24 = 1;
    
    int _clockType = DIGITAL;
    int _color = DigitalClockFaceField.SKY_COLOR;
    int _bakGroundType = DigitalClockFaceField.BACKGROUND_BLACK;
    boolean _militaryTime = false;
    
    MusicalClockGlobalSettingItem() {    }
    
    public void setClockType(int type) {
        _clockType = type;
    }
    
    public void setBackGroundType(int type) {
        _bakGroundType = type;
    }
        
    public void setClockColor(int color) {
        _color = color;
    }
    
    public void setMilitaryTime(boolean militaryTime) {
        _militaryTime = militaryTime;
    }    
    
    public int getClockColor() {
        return _color;
    }
    public int getClockType() {
        return _clockType;
    }
    public int getBackgroundType() {
        return _bakGroundType;
    }    
    public boolean isMilitaryTime() {
        return _militaryTime;
    }    
        
}
