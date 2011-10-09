/*
 * MusicalClockSettingItem.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.apps.musicalclock;

import java.util.*;
import net.rim.device.api.util.Persistable;

// The item of each clock
public final class MusicalClockSettingItem //implements Persistable 
{
    private String _strDisplayName; // The City or Country name to be displayed on Clock face.
    private String _tzId;
    
    MusicalClockSettingItem(TimeZone tz) {
    //String timeZoneName) {
        _tzId = tz.getID();
    }
    
    public void setTimeZone(TimeZone tz) {
        _tzId = tz.getID();
    }
    
    public String getTimeZoneName() {
        return  _tzId;
    }

    public void setDisplayName(String cityName) {
        _strDisplayName = cityName;
    }
        
    public String getDisplayName() {
        return _strDisplayName;
    }
    
   /* public String getTimeZoneDisplayName() {
        return MusicalClockSettingItemField.getTimeZoneDisplayString(TimeZone.getTimeZone(_tzId));
    }*/
}
