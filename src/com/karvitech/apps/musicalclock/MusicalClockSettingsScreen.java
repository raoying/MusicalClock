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
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
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
    final static String STR_BACK_LIGHT_WARNING = "Keeping backlight on will prevent the phone from locking itself if you have a password. Do you still want to proceed?"; 
    
    KtListSwitchItem _militaryTime;
    KtListSwitchItem _keepBacklightOnWhenCharging;
    KtListSwitchItem _showWhenCharging;
    MusicalClockGlobalSettingItem2 _globalSettings;
    MusicalClockSettingsScreen() {
        _globalSettings = (MusicalClockGlobalSettingItem2)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_GLOBAL_SETTINGS_2);                    
        _militaryTime = new KtListSwitchItem(STR_MILITARY_TIME, _globalSettings.isMilitaryTime());
        _showWhenCharging = new KtListSwitchItem(STR_SHOW_WHEN_PLUGGED, _globalSettings.showWhenCharing());
        _keepBacklightOnWhenCharging = new KtListSwitchItem(STR_KEEP_BACK_LIGHT_ON, _globalSettings.keepBackLightOn()); 
        
        // set the title
        LabelField title = new LabelField(STR_SETTINGS, Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
        
        add(_militaryTime);
        add(new SeparatorField());

        add(_showWhenCharging);
        add(new SeparatorField());
        
        FieldChangeListener listener = new FieldChangeListener()
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
            }
        };
        
        _keepBacklightOnWhenCharging.setChangeListener(listener);
        add(_keepBacklightOnWhenCharging);
        add(new SeparatorField());        
    }
    
    protected boolean saveSettings() {
        _globalSettings.setKeepBackLightOn(_keepBacklightOnWhenCharging.getSwitchState());
        _globalSettings.setMilitaryTime(_militaryTime.getSwitchState());
        _globalSettings.setshowWhenCharing(_showWhenCharging.getSwitchState());
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
