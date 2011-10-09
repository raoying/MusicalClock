/*
 * AlarmRepeatEditScreen.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */


package com.karvitech.apps.musicalclock;
import java.util.Vector;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.ui.*; 
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Dialog; 
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.NullField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.system.*;

import com.karvitech.api.appTools.*;
import com.karvitech.apps.alarmlib.*;



/**
 * 
 */
public class AlarmRepeatEditScreen extends MainScreen implements FieldChangeListener {
    private AlarmItem _alarmItem;
    private VerticalFieldManager    _vfm;
    private AlarmItemListener _listener;
        
    // type of repeating
    private KtListSwitchItem _chkNoRepeat;
    private KtListSwitchItem _chkMonday;
    private KtListSwitchItem _chkTuesday;
    private KtListSwitchItem _chkWednesday;
    private KtListSwitchItem _chkThursday;    
    private KtListSwitchItem _chkFriday;
    private KtListSwitchItem _chkSaturday;
    private KtListSwitchItem _chkSunday;

     private MenuItem _saveMenuItem = new MenuItem("Save", 100, 10)
    {
        public void run()
        {
             saveAlarmRepeating();                                 
             AlarmRepeatEditScreen.this.close();   
        }
    };
    private ButtonField _btnSave = new ButtonField("Save",Field.FIELD_HCENTER) {
                                 protected boolean navigationClick(int status,int time) {
                                      if(saveAlarmRepeating()) {
                                        // strangely the onClose method of AlarmEditScreen is not called when
                                        // it is closed here, so clean up the player here                                   
                                        AlarmRepeatEditScreen.this.close();  
                                      }                                    
                                      return true;
                                 }  
                             };    
    
    public AlarmRepeatEditScreen(AlarmItem item) {
        _alarmItem = item;
        
        // screen title
        LabelField title = new LabelField("Alarm Repeating", Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
        
        _vfm = new VerticalFieldManager();
        this.add(_vfm);
        
        // initialize the radio buttons
        _chkNoRepeat = new KtListSwitchItem("No repeating",  !_alarmItem._alarmRepeating);
        _chkNoRepeat.setChangeListener(this);
        
        _chkMonday = new KtListSwitchItem("Monday",  _alarmItem._monday);
        _chkMonday.setChangeListener(this);
        
        _chkTuesday = new KtListSwitchItem("Tuesday",  _alarmItem._tuesday);
        _chkTuesday.setChangeListener(this);
        
        _chkWednesday = new KtListSwitchItem("Wednesday",  _alarmItem._wednesday);
        _chkWednesday.setChangeListener(this);
        
        _chkThursday = new KtListSwitchItem("Thursday",  _alarmItem._thursday);    
        _chkThursday.setChangeListener(this);
        
        _chkFriday = new KtListSwitchItem("Friday",  _alarmItem._friday);
        _chkFriday.setChangeListener(this);
        
        _chkSaturday = new KtListSwitchItem("Saturday",  _alarmItem._saturday);
        _chkSaturday.setChangeListener(this);
        
        _chkSunday = new KtListSwitchItem("Sunday",  _alarmItem._sunday);
        _chkSunday.setChangeListener(this);
            
        populateScreen();
    }
    
    private void populateScreen() {
            _vfm.add(_chkNoRepeat);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkMonday);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkTuesday);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkWednesday);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkThursday);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkFriday);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkSaturday);
            _vfm.add(new SeparatorField());
            
            _vfm.add(_chkSunday);
            _vfm.add(new SeparatorField());
            _vfm.add(_btnSave); 
    }
    
    private boolean hasError() {
        if(!_chkNoRepeat.getSwitchState()) {
            boolean selectedOneDay =  _chkMonday.getSwitchState()
                                     | _chkTuesday.getSwitchState() 
                                     | _chkWednesday.getSwitchState()
                                     |_chkThursday.getSwitchState()
                                     |_chkFriday.getSwitchState()
                                     |_chkSaturday.getSwitchState()
                                     |_chkSunday.getSwitchState();
             if(!selectedOneDay) {
                 Dialog.inform("At least one field needs to be checked.");
                 return true;
             }                
        }
        return false;
    }
    private boolean saveAlarmRepeating() {
        if(hasError()) {
            return false;
        }
        
        // only modify the time when the time has a value
        if(_alarmItem._alarmTime != Long.MIN_VALUE) {
            if(_alarmItem._alarmRepeating == true && _chkNoRepeat.getSwitchState() == true) {
                // changed from repeating to no repeat
                // use current date
                long curTime = System.currentTimeMillis();
                long millPastMidnight =  TimeUtilities.getMillisecPastMidnight(curTime);
                _alarmItem._alarmTime = curTime - millPastMidnight + _alarmItem._alarmTime;
            } else if(_alarmItem._alarmRepeating == false && _chkNoRepeat.getSwitchState() == false) {
                // changed from no repeating to repeating 
                // get the milli seconds past midnight. i.e, remove the date
                _alarmItem._alarmTime = TimeUtilities.getMillisecPastMidnight(_alarmItem._alarmTime);
            }
        }       
        _alarmItem._alarmRepeating = !_chkNoRepeat.getSwitchState();
        _alarmItem._monday = this._chkMonday.getSwitchState();
        _alarmItem._tuesday = this._chkTuesday.getSwitchState();
        _alarmItem._wednesday = this._chkWednesday.getSwitchState();
        _alarmItem._thursday = this._chkThursday.getSwitchState();
        _alarmItem._friday = this._chkFriday.getSwitchState();
        _alarmItem._saturday = this._chkSaturday.getSwitchState();
        _alarmItem._sunday = this._chkSunday.getSwitchState();  
        
        // notify listener
        _listener.AlarmItemChanged(_alarmItem, this);  
        return true;     
    }
   
    // OnSave override
    protected boolean onSave() {
        return saveAlarmRepeating();        
        //return true;
    }   
    
    protected void makeMenu(Menu menu, int instance)
    {
        menu.add(_saveMenuItem);
    }
    
    public void fieldChanged(Field field, int context) {
        // the Enable Schedule is checked, refresh screen
        if(field == _chkNoRepeat) {
            //_chkNoRepeat.setSwitchOn(!_chkNoRepeat.getSwitchState());
            if(_chkNoRepeat.getSwitchState()) {
                _chkMonday.setSwitchOn(false);
                _chkTuesday.setSwitchOn(false);
                _chkWednesday.setSwitchOn(false);
                _chkThursday.setSwitchOn(false);    
                _chkFriday.setSwitchOn(false);
                _chkSaturday.setSwitchOn(false);
                _chkSunday.setSwitchOn(false);
            }
        }
        else 
        {
            KtListSwitchItem chkField = (KtListSwitchItem)field;
            //chkField.setSwitchOn(!chkField.getSwitchState());
            if(chkField.getSwitchState()) {
                _chkNoRepeat.setSwitchOn(false); 
            }
        }        
    }   // fieldChanged   
    
    public void setAlarmListenr(AlarmItemListener listener) {
        _listener = listener;
    } 
} 
