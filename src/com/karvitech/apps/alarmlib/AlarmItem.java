/*
 * AlarmItem.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;

import com.karvitech.apps.filelib.*;
import net.rim.device.api.util.Persistable;
import java.util.*;
import javax.microedition.media.PlayerListener;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.*;
//import com.karvitech.apps.utilities.*;
import com.karvitech.api.appTools.*;
/**
 * 
 */
public class AlarmItem implements AlarmPlayerListener, Persistable  {
    public static final int TONE_BUILD_IN = 1;
    public static final int TONE_MUSIC = 0; 
    public static final int TONE_NONE = 2;
    public static final int TONE_PLAY_LIST = 3; 
    public static final int TONE_RADIO_SOFTWARE = 4;
    public static final int TONE_RADIO_STREAMING = 5;
     // volume choices
    public static final int VOLUME_LOUD = 0;
    public static final int VOLUME_NORMAL = 1;
    public static final int VOLUME_LOW = 2;
    public static final int VOLUME_SILENT = 3;
        
    public String  _alarmName;
    public boolean _alarmOn = true; // default to on
    public boolean _alarmVibrateOn = false; 
    
    // Snooze 
    private final static String[] SNOOZE_CHOICES = {
        "5 Min.",
        "10 Min.",
        "15 Min.",
        "20 Min."
    };
    // repeating
    public boolean _alarmRepeating = true; // repeating alarm?
    public boolean _sunday = true;
    public boolean _monday = true;
    public boolean _tuesday = true;
    public boolean _wednesday = true;
    public boolean _thursday = true;
    public boolean _friday = true;
    public boolean _saturday = true;
    
    private int    _lastPlayFileIndex; // the music file played last time
    public long    _alarmTime;    // The alarm time is the time past midnight if repeating, otherwise it is the absolute time
    public int     _snoozeTime = 5; // default is 5 minutes
    public String  _toneFile; // used to display, and currently if only one file is selected
    public int     _alarmToneType;
    //private Vector  _playList; // for a play list
   // private String _playListSerialized;
    public int     _volume;
   // public int     _lastPlayIndex; // the index of the last played song in list
      
    private char DELIMITOR = '|';
    private int _alarmRunnableId; // the event id for the alarm runnable
            
    public AlarmItem() {
        _alarmTime = Long.MIN_VALUE;
        //_playList = Util.covertStringToTokens(_playListSerialized, DELIMITOR); 
        _volume = VOLUME_LOUD; // default to loudest
        _alarmToneType = AlarmItem.TONE_BUILD_IN;
        _toneFile = AlarmTuneManager.getInstance().getDefaultTone();
    }
    
    public AlarmItem clone() {
        AlarmItem item = new AlarmItem();
        
        // time and repeat 
        item._alarmRepeating = this._alarmRepeating;
        item._alarmTime = this._alarmTime;
        item._monday = this._monday;
        item._tuesday = this._tuesday;
        item._wednesday = this._wednesday;
        item._thursday = this._thursday;
        item._friday = this._friday;
        item._saturday = this._saturday;
        item._sunday = this._sunday;        
        
        // sound and volume
        item._volume = this._volume;
        item._lastPlayFileIndex = this._lastPlayFileIndex; // the music file played last time
        item._snoozeTime = this._snoozeTime; 
        if(this._toneFile != null) {
            item._toneFile = new String(this._toneFile); // only one file or folder is selected
        }
        else {
            item._toneFile = null;
        }
        
        item._alarmToneType = this._alarmToneType;
        
        if(this._alarmName != null) {
            item._alarmName = new String(this._alarmName);
        }
        else {
            item._alarmName = null;
        }
        item._alarmOn = this._alarmOn; // default to on
        item._alarmVibrateOn = this._alarmVibrateOn;        
        return item;
   }

    static public void copy(AlarmItem from, AlarmItem to) {      
        // time and repeat 
        to._alarmRepeating = from._alarmRepeating;
        to._alarmTime = from._alarmTime;
        to._monday = from._monday;
        to._tuesday = from._tuesday;
        to._wednesday = from._wednesday;
        to._thursday = from._thursday;
        to._friday = from._friday;
        to._saturday = from._saturday;
        to._sunday = from._sunday;        
        
        // sound and volume
        to._volume = from._volume;
        to._lastPlayFileIndex = from._lastPlayFileIndex; // the music file played last time
        to._snoozeTime = from._snoozeTime; 
        
        if(from._toneFile != null) {
            to._toneFile = new String(from._toneFile); // only one file or folder is selected
        }
        else {
            to._toneFile = null;
        }
        
        to._alarmToneType = from._alarmToneType;
        
        if(from._alarmName != null) {        
            to._alarmName = new String(from._alarmName);
        }
        else {
            to._alarmName = null;
        }
        
        to._alarmOn = from._alarmOn; // default to on
        to._alarmVibrateOn = from._alarmVibrateOn;        
   }
       
    public String getSnoozeStr() {
        String str = _snoozeTime + " Min.";
        return str;
    }
    
    public int getSnoozeTimeIndex() {
        switch(_snoozeTime) {
            case 5:
                return 0;
            case 10:
                return 1;
            case 15:
                return 2;
            case 20:
                return 3;
            default:
                return 0;
        }
    }
    
    public void setSnooze(int choiceIndex) {
        switch(choiceIndex) {
            case 0:
                _snoozeTime = 5;
                break;
            case 1:
                _snoozeTime = 10;
                break;   
            case 2:
                _snoozeTime = 15;
                break;
            case 3:
                _snoozeTime = 20;
                break;
            default:
                _snoozeTime = 5; // default 5 minutes
        }
    }
    
    public String[] getSnoozeChoices() {
        return SNOOZE_CHOICES;
    }
    
    public String getTitle() {
        String[] strList = getDisplayStr();
        return strList[0];
    }
    
    public String getDeatils() {
        if(this._alarmRepeating) {
           return this.getTimeStr() + " " + this.getRepeatStr();
        }
        else {
            return this.getTimeStr(); 
        }
       // String[] strList = getDisplayStr();
       // return strList[1];
    }

    public String getVolumeStr() {
        switch(_volume) {
            case VOLUME_LOUD:
                return "Loud";
            case VOLUME_NORMAL:
                return "Normal";
            case VOLUME_LOW:
                return "Low";
            case VOLUME_SILENT:
                return "Silent";
            default:
                return "Normal";
        }
    }
    
    public int getVolumeVal() {
        switch(_volume) {
            case VOLUME_LOUD:
                return 100;
            case VOLUME_NORMAL:
                return 60;
            case VOLUME_LOW:
                return 30;
            case VOLUME_SILENT:
                return 0;
            default:
                return 60;
        }    
    }
    public String getTimeStr() {
        if(_alarmTime == Long.MIN_VALUE) {
            return "N/A";
        }
        
        if(!_alarmRepeating) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getDefault());
            cal.setTime(new Date(_alarmTime));            
            return TimeUtilities.getDateTimeString(cal, !_alarmRepeating);
        }
        else {
            return TimeUtilities.getDateTimeString(_alarmTime);
        }
    }
    
    public String getRepeatStr() {
        StringBuffer dispStrBuff = new StringBuffer();
        if(_alarmRepeating) {
            if(_sunday) {
                dispStrBuff.append("Sun,");
            }
            if(_monday) {
                dispStrBuff.append("Mon,");
            }        
            if(_tuesday) {
                dispStrBuff.append("Tue,");
            }
            if(_wednesday) {
                dispStrBuff.append("Wed,");
            }        
            if(_thursday) {
                dispStrBuff.append("Thu,");
            }
            if(_friday) {
                dispStrBuff.append("Fri,");
            }        
            if(_saturday) {
                dispStrBuff.append("Sat,");
            }
            
            if(dispStrBuff.length() > 0) {
                dispStrBuff.deleteCharAt(dispStrBuff.length() -1);
            }
            else {
                // should not happen, but uses as a safe guard
                 dispStrBuff.append("No repeating");
            }
        }
        else {
           // String dateStr = TimeUtilities.constructDate(cal);
            dispStrBuff.append("No repeating");            
        }
        return dispStrBuff.toString();
    }           
    
    public String[] getDisplayStr() {
        StringBuffer dispStrBuff = new StringBuffer();
        String[] strList = new String[2];
        strList[0] = _alarmName;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(_alarmTime));
               
        // alarm name
       // dispStrBuff.append(_alarmName);
        dispStrBuff.append("(");
        if(_alarmRepeating) {
            dispStrBuff.append(TimeUtilities.getTimeString(cal) + " ");
            if(_sunday) {
                dispStrBuff.append("Sun,");
            }
            if(_monday) {
                dispStrBuff.append("Mon,");
            }        
            if(_tuesday) {
                dispStrBuff.append("Tue,");
            }
            if(_wednesday) {
                dispStrBuff.append("Wed,");
            }        
            if(_thursday) {
                dispStrBuff.append("Thu,");
            }
            if(_friday) {
                dispStrBuff.append("Fri,");
            }        
            if(_saturday) {
                dispStrBuff.append("Sat,");
            }
            dispStrBuff.deleteCharAt(dispStrBuff.length() -1);
        }
        else {

           // String dateStr = TimeUtilities.constructDate(cal);
            dispStrBuff.append(TimeUtilities.getDateTimeString(cal));
            
        }
        dispStrBuff.append(")"); 
        strList[1] = dispStrBuff.toString();
        return strList;     
    }
    
    public long getAlarmTime() {
        return _alarmTime;
    }    
    
   /* public void setPlayList(Vector playList) {
        _playList = playList;
        _playListSerialized = Util.convertStringVectorToText(_playList, DELIMITOR);
    }*/
    
    /*public Vector getPlayList() {
        return _playList;
    }*/
    
    public String getAlarmSoundStr() {
        if(_toneFile == null) {
            return "N/A";
        }
        else if(this._alarmToneType == AlarmItem.TONE_BUILD_IN) {
            String dispStr = AlarmTuneManager.getInstance().getToneNameByFileName(_toneFile);
            return dispStr;
        }
        else if(this._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
            String dispStr = null;
            int handle = CodeModuleManager.getModuleHandle(_toneFile);
            if(handle > 0) {
                ApplicationDescriptor[] appDesc = CodeModuleManager.getApplicationDescriptors(handle);
                if(appDesc != null && appDesc.length > 0) {
                   dispStr = appDesc[0].getLocalizedName();
                }
            }
            return dispStr;            
        }
        else {
            return _toneFile;
        }
    }
    
    public boolean isAlarmSetForTheDay(int dayOfWeek) {
        // check if the alarm is set for the particular weekday 

        switch(dayOfWeek) {
            case Calendar.SUNDAY:
                if(!this._sunday) {
                    return false;
                }
                break;
            case Calendar.MONDAY:
                if(!this._monday) {
                    return false;
                }
                break;                
            case Calendar.TUESDAY:
                if(!this._tuesday) {
                    return false;
                }
                break;
            case Calendar.WEDNESDAY:
                if(!this._wednesday) {
                    return false;
                }
                break; 
            case Calendar.THURSDAY:
                if(!this._thursday) {
                    return false;
                }
                break;
            case Calendar.FRIDAY:
                if(!this._friday) {
                    return false;
                }
                break; 
            case Calendar.SATURDAY:
                if(!this._saturday) {
                    return false;
                }
                break;  
        }
        return true;
    }
    
    public void stopAlarm() {
        try {
            if(_alarmToneType != TONE_NONE && _alarmToneType != TONE_RADIO_SOFTWARE ) {
                AlarmMediaPlayer player = AlarmMediaPlayer.getInstance();        
                player.stop(); 
                player.close();
            } 
            
            if(this._alarmVibrateOn) {
                UiApplication.getApplication().cancelInvokeLater(_alarmRunnableId);
                _alarmRunnableId = 0;
            }
        }
        catch (Exception e) {
             System.out.println("stopAlarm exception:" + e.toString() + ":" + e.getMessage());
        }
    }
    public void snoozeAlarm() {
        stopAlarm();
        Application.getApplication().invokeLater(new SnoozeRunnable(), _snoozeTime*60*1000, false);        
    }
    
    public void startAlarm() {
        // if music or buzz, play
        if(_alarmToneType != TONE_NONE && _alarmToneType != TONE_RADIO_SOFTWARE) {
            Vector playList = new Vector();
            
            AlarmMediaPlayer player = AlarmMediaPlayer.getInstance(); 
            player.setPlayMode(AlarmMediaPlayer.PLAY_LOOP); 

           if(this._alarmToneType == TONE_MUSIC) {       
                FileUtil.createPlayList(_toneFile, playList);
                if(playList.size() > 0) {
                	// start from last index plus one(next song)
                	this._lastPlayFileIndex++;
                	this._lastPlayFileIndex %= playList.size();
                	player.setPlayList(playList,_lastPlayFileIndex);
                    player.setListener(this);                    
                }
                else {        
                    player.initializeMedia("file:///" + _toneFile);
                }
            }
            else if(this._alarmToneType == TONE_BUILD_IN) {
                player.initializeMedia("/" + _toneFile, true); // play build in alarm file, open it using IS
            }

            player.setVolume(getVolumeVal());
            if(!player.start()) {
                player.setPlayList(null, 0);
                 player.initializeMedia("/" + AlarmTuneManager.getInstance().getDefaultTone(), true);
                 player.start();
            }
        }
        else if(this._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
           if(!Util.runApp(_toneFile)) {
               // failed to launch or
               // no radio software, play default tone
                AlarmMediaPlayer player = AlarmMediaPlayer.getInstance(); 
                player.setPlayMode(AlarmMediaPlayer.PLAY_LOOP);                
                player.setVolume(getVolumeVal());
                if(!player.start()) {
                    player.setPlayList(null, 0);
                    player.initializeMedia("/" + AlarmTuneManager.getInstance().getDefaultTone(), true);
                    player.start();
                }
           }
        }
        
        if(this._alarmVibrateOn) {
            //  vibrate
            Alert.startVibrate(500);
            _alarmRunnableId = UiApplication.getApplication().invokeLater(new VibrateRunnable(), 2000, true);
        }
        AlarmConfirmationDialog.showDialog(this);
            
    } 
    
     
    class SnoozeRunnable implements Runnable {
            public void run()
            {
                startAlarm();
            }
    }          
    public static class VibrateRunnable implements Runnable {
            public void run()
            {
                Alert.startVibrate(500);
            }
    }      
    
    // implement AlarmPlayerListener
    public void PlayerUpdate(final String event, Object eventData)
    {
         if(event.equals(PlayerListener.END_OF_MEDIA)) {
             this._lastPlayFileIndex = ((Integer)eventData).intValue();
         }
    }
} 
