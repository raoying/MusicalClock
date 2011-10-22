/*
 * MusicalClockSettingsScreen.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.apps.musicalclock;
import java.util.*;
import net.rim.device.api.ui.UiApplication;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.MenuItem;
import java.util.TimeZone;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.decor.*;
import com.karvitech.api.appTools.*;
import com.karvitech.apps.alarmlib.*;

/**
 * 
 */
class MusicalClockSettingsScreen extends MainScreen {
    final static String STR_SETTINGS = "Settings";
    final static String STR_MILITARY_TIME = "Use 24-hour format";
    final static String STR_KEEP_BACK_LIGHT_ON = "Keep screen on when charging";
    final static String STR_SHOW_WHEN_PLUGGED = "Show when charging";
    final static String STR_USE_Fahrenheit = "Use Fahrenheit unit";
    final static String STR_USE_Celsius  = "Use Celsius unit ";
    final static String STR_BACK_LIGHT_WARNING = "Keeping backlight on will prevent the phone from locking itself if you have a password. Do you still want to proceed?";
	private static final String STR_LATITUDE = "Latitude";
	private static final String STR_LONGITUDE = "Longitude"; 
    
    KtListSwitchItem _militaryTime;
    KtListSwitchItem _keepBacklightOnWhenCharging;
    KtListSwitchItem _showWhenCharging;
    KtListItem _tempUnit;
    KtListItem _lati;
    KtListItem _longi;
    
    boolean _usingCelsius;
    String latStr;
    String lonStr;
    
    MusicalClockGlobalSettingItem2 _globalSettings;
    FieldChangeListener _listener = new FieldChangeListener()
    {
        public void fieldChanged(Field field, int context)
        {
            if(_keepBacklightOnWhenCharging.getSwitchState() && !_globalSettings.keepBackLightOn()) {
                    int selection = Dialog.ask(Dialog.D_YES_NO, STR_BACK_LIGHT_WARNING, Dialog.NO);                        
                    
                    // user selected no, switch back                    
                    if(selection == Dialog.NO) {
                        _keepBacklightOnWhenCharging.setSwitchOn(false);
                    }
            }
            else if(_tempUnit == field) {
            	_usingCelsius = !_usingCelsius;
            	if(_usingCelsius) {
            		_tempUnit.setTitle(STR_USE_Celsius);
            	}
            	else {
            		_tempUnit.setTitle(STR_USE_Fahrenheit);
            	}
            	_tempUnit.invalidate();
            }
            else if(field == _lati) {
            	setLocationValue(STR_LATITUDE, latStr, _lati);;
            }
            else if(field == _longi) {
            	setLocationValue(STR_LONGITUDE, lonStr, _longi);;
            }
        }
    };
    
    private void setLocationValue(String displayStr, String locationStr, KtListItem item) {
        KtQuerryDialog dlg = new KtQuerryDialog(displayStr, locationStr);
        // dlg.setDialogClosedListener(this);
         dlg.doModal();
         String str = dlg.getText();
         try {
        	 float fVal = Float.parseFloat(str);
        	 locationStr = str;
             item.setDetails(locationStr);
             item.invalidate();
         }
         catch(Exception e) {
        	 Dialog.inform("Please type in a valid number.");
         }    	
    }
    MusicalClockSettingsScreen() {
    	Configuration config = Configuration.getInstance();
        _globalSettings = (MusicalClockGlobalSettingItem2)config.getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2);                    
        _militaryTime = new KtListSwitchItem(STR_MILITARY_TIME, _globalSettings.isMilitaryTime());
        _showWhenCharging = new KtListSwitchItem(STR_SHOW_WHEN_PLUGGED, _globalSettings.showWhenCharing());
        _keepBacklightOnWhenCharging = new KtListSwitchItem(STR_KEEP_BACK_LIGHT_ON, _globalSettings.keepBackLightOn()); 
        

        _usingCelsius = ((Boolean)config.getKeyValue(MusicalClockContext.KEY_WEATHER_USE_CELSIUS)).booleanValue();
        String unitStr = _usingCelsius?STR_USE_Celsius:STR_USE_Fahrenheit;
        _tempUnit = new KtListItem(unitStr, null, 0);
        // set the title
        LabelField title = new LabelField(STR_SETTINGS, Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
        
        add(_militaryTime);
        add(new SeparatorField());

        add(_showWhenCharging);
        add(new SeparatorField());
        
        
        _keepBacklightOnWhenCharging.setChangeListener(_listener);
        add(_keepBacklightOnWhenCharging);
        add(new SeparatorField()); 
        
        _tempUnit.setChangeListener(_listener);
        add(_tempUnit);
        add(new SeparatorField());
        Object lat = config.getKeyValue(MusicalClockContext.KEY_WEATHER_LOCATION_LAT);
		if(lat != null) {
			latStr = lat.toString();
		}
		else {
			latStr = "N/A";
		}
        Object lon = config.getKeyValue(MusicalClockContext.KEY_WEATHER_LOCATION_LONG);
		if(lat != null) {
			lonStr = lon.toString();
		}
		else {
			lonStr = "N/A";
		}		
        _lati = new KtListItem(STR_LATITUDE, latStr, 0);
		_longi =  new KtListItem(STR_LONGITUDE, lonStr, 0);
		_lati.setChangeListener(_listener);
		_longi.setChangeListener(_listener);
		
        add(_lati);
        add(new SeparatorField());
        add(_longi);
        add(new SeparatorField());
    }
    
    protected boolean saveSettings() {
        _globalSettings.setKeepBackLightOn(_keepBacklightOnWhenCharging.getSwitchState());
        _globalSettings.setMilitaryTime(_militaryTime.getSwitchState());
        _globalSettings.setshowWhenCharing(_showWhenCharging.getSwitchState());
        Configuration.getInstance().setKeyValue(MusicalClockContext.KEY_WEATHER_USE_CELSIUS, new Boolean(_usingCelsius));
        Configuration.getInstance().setKeyValue(MusicalClockContext.KEY_WEATHER_LOCATION_LAT, new Float(Float.parseFloat(latStr)));
        Configuration.getInstance().setKeyValue(MusicalClockContext.KEY_WEATHER_LOCATION_LONG, new Float(Float.parseFloat(lonStr)));
        Configuration.getInstance().saveSettings();
        return true;
    }
    
   /* public boolean onClose() {
        saveSettings();
        return super.onClose();
    }*/    
    
    // OnSvae override
    protected boolean onSave() {
        return saveSettings();
    }
} 
