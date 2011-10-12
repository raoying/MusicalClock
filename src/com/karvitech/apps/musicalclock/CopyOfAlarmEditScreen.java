package com.karvitech.apps.musicalclock;

import javax.microedition.io.Connector;
import javax.microedition.io.file.*;

import java.util.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.util.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.i18n.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.applicationcontrol.*;
import com.karvitech.api.appTools.*;
import com.karvitech.apps.filelib.*;
import com.karvitech.apps.alarmlib.*;
import net.rim.device.api.ui.decor.*;
import net.rim.device.api.ui.picker.*;
import javax.microedition.io.file.*;
import java.io.*;
import javax.microedition.io.*;


public class CopyOfAlarmEditScreen extends MainScreen implements FieldChangeListener, 
                                                           AlarmItemListener, 
                                                           FileExplorerListener 
{
    public static final String STR_BUILDIN_ALARM = "/Alarm.mp3"; // for buzz
    public static final String STR_APP_TITLE = "Alarm";
    public static final String STR_VIBRATE = "Vibrate";
    public static final String STR_BUZZ = "Buzz";
    public static final String STR_MUSIC = "Alarm Sound";
    public static final String STR_VOLUME = "Alarm Volume";    
    public static final String STR_CHANGE_MUSIC = "Change Music File";
    public static final String STR_PLAY_LIST_FILE_NAME = "MusicClockplayList.lst";
    public static final char DELIMITOR = '|';
    

    private static final String[] VOLUME_CHOICES = {"  Loud  ", " Normal", "  Low  ", " Silent"};
    private static final int[]    VOLUME_CHOICE_INDEX = {AlarmItem.VOLUME_LOUD, AlarmItem.VOLUME_NORMAL, AlarmItem.VOLUME_LOW, AlarmItem.VOLUME_SILENT};
    
    Vector _playList = new Vector();
    AlarmMediaPlayer _player = null;
    private AlarmItem _alarmItemOrig; // the original AlarmItem passed into this creen
    private AlarmItem _alarmItem;  // the copy of the orignial that is used for editting, only when user presses save then it is copied back to orignal.
    
    // fields show in the list
    private boolean DEBUG_MODE = true;
    private VerticalFieldManager    _vfm;
    private KtListSwitchItem _alarmOn;    
    private KtListItem _alarmName;
    private KtListItem _alarmTime;
    private KtListItem _snoozeTimeField;
    private KtListItem _alarmSound;
    private KtListItem _alarmRepeat;
    private KtListItem _alarmVolume;
    private KtListSwitchItem _alarmVibrate;  
    private HorizontalFieldManager _buttonBar = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER);  

   // private String[] _mediaFileUri = new String[1];
    
    private Configuration _config =  Configuration.getInstance();
    private int _vibrateRunnableId = 0;
             
    private MenuItem _saveMenuItem = new MenuItem("Save", 100, 10)
    {
        public void run()
        {
            saveAction();
        }
    };
    
    private ButtonField _testButton = new ButtonField("Test", Field.FIELD_HCENTER) {
        
        private void initializeMedia() {
            
                Vector playList = new Vector();
                if(_alarmItem._alarmToneType == AlarmItem.TONE_MUSIC) {
                    FileUtil.createPlayList(_alarmItem._toneFile, playList);
                }
                if(DEBUG_MODE) {
                        System.out.println("createPlayList successful");
                }   
                // check if the alarmTone file is null
                if(_alarmItem._alarmToneType == AlarmItem.TONE_MUSIC 
                   &&_alarmItem._toneFile != null 
                   && _alarmItem._toneFile.length() > 0) 
                {
                    if(DEBUG_MODE) {
                        System.out.println("_alarmToneType == AlarmItem.TONE_MUSIC ");
                    }     
                    if( playList!= null && playList.size() > 0) {
                        _player.setPlayList(playList, 0);
                    } else {
                        _player.initializeMedia("file:///" + _alarmItem._toneFile);
                    }
                } 
                else if(_alarmItem._alarmToneType == AlarmItem.TONE_BUILD_IN) { 
                    if(DEBUG_MODE) {
                        System.out.println("_alarmToneType == AlarmItem.TONE_BUILD_IN ");
                    }                    
                    _player.initializeMedia("/" + _alarmItem._toneFile, true);             
                    _player.setPlayList(null, 0);
                }
                        
        }      
        protected boolean navigationClick(int status,int time) {
            //((UiApplication)(Application.getApplication())).pushScreen(new AlarmTonePickerScreen());
            if(_alarmItem._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
                // test radio software
                if(_testButton.getLabel().equalsIgnoreCase("Test")) {
                    startVibrate();
                    Util.runApp(_alarmItem._toneFile);
                    _testButton.setLabel("Stop");
                }
                else {
                    stopVibrate();
                    _testButton.setLabel("Test");                
                }
                return true;
                
            }
            
            // if not silent mode
            if(_alarmItem._volume != AlarmItem.VOLUME_SILENT) {
                if(_player == null) {
                    startVibrate();
                    _player = AlarmMediaPlayer.getInstance();  
                    if(_player == null) {
                        System.out.println("_player is null");
                    }              
                    initializeMedia();
                    if(DEBUG_MODE) {
                        System.out.println("initializeMedia passed ");
                    } 
                    //_playing = true;
                    _player.setPlayMode(AlarmMediaPlayer.PLAY_LOOP);
                    _player.setVolume(_alarmItem.getVolumeVal());
                    if(_player.start()) {
                        _testButton.setLabel("Stop");  
                    }
                    else {
                        UiApplication.getUiApplication().invokeLater(new Runnable()
                        {
                            public void run()
                            {
                                Dialog.inform("Failed to play alarm sound, please make sure the media files are valid. Preloaded ringtones or songs may not be accessible to third party software, please use your own media files.");
                            }
                        });
                    }                
                } 
                else {
                    stopVibrate();
                    _player.stop();
                    _player.close();
                    _player = null;
                    _testButton.setLabel("Test");
                }
            }
            else {
                if(_alarmVibrate.getSwitchState()) {
                    if(_vibrateRunnableId == 0) {
                        startVibrate();
                        _testButton.setLabel("Stop");
                    }
                    else {
                        stopVibrate();
                        _testButton.setLabel("Test");  
                    }                  
                }
            }
            return true;
        }    
    }; 

    private ButtonField      _btnSave = new ButtonField("Save",Field.FIELD_HCENTER) {
                                 protected boolean navigationClick(int status,int time) {
                                      saveAction();
                                      return true;
                                 }  
                             };

     
        
        private void startVibrate() {
            if(_alarmVibrate.getSwitchState()) {
                // vibrate now
                Alert.startVibrate(500);
                // vibrate every 2 seconds
                _vibrateRunnableId = UiApplication.getApplication().invokeLater(new AlarmItem.VibrateRunnable(), 2000, true);
            }
        }
        
        public void stopVibrate() {
            if(_vibrateRunnableId !=0) {
                UiApplication.getApplication().cancelInvokeLater(_vibrateRunnableId);
                _vibrateRunnableId = 0;
            }
        }

    
    public CopyOfAlarmEditScreen(AlarmItem item) {
        _vfm = new VerticalFieldManager();
        _buttonBar.add(_testButton);
        _buttonBar.add(_btnSave);
        
        
        Background background = BackgroundFactory.createLinearGradientBackground(Color.DARKGRAY, Color.DARKGRAY,
                                                Color.DARKGRAY, Color.DARKGRAY);
        //Background background = BackgroundFactory.createSolidTransparentBackground(Color.VIOLET, 100);
        //_vfm.setBackground(background);
        //_vfm.setBorder(BorderFactory.createRoundedBorder(new XYEdges(2, 2, 2, 2), Color.BURLYWOOD, Border.STYLE_SOLID)); 
        
        // save the reference to the original item and clone a new one for editing
        _alarmItemOrig = item;            
        _alarmItem = _alarmItemOrig.clone();
       
        LabelField title = new LabelField("Edit Alarm", Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
        
        // add the vfm
        add(_vfm);

        populateScreen();
    } // end of constructor
    
    private void saveAction() {
        boolean res = saveAlarm();
        // strangely the onClose method of AlarmEditScreen is not called when
        // it is closed here, so clean up the player here
        if(_player != null) {
            _player.stop();
            _player.close();
            _player = null;
        }        
        this.stopVibrate();                         
        if(res) {                              
            CopyOfAlarmEditScreen.this.close();
        }
    }
    private void populateScreen() {  
        
        // clear the VFM    
        _vfm.deleteAll();


        // Alarm name
        if(_alarmName == null) {         
            _alarmName = new KtListItem("Alarm Name", _alarmItem._alarmName,Field.USE_ALL_WIDTH);
            _alarmName.setChangeListener(this);
        }        
        _vfm.add(_alarmName);
        _vfm.add(new SeparatorField()); 
                
        // add the Alarm On/Off
        if(_alarmOn == null) {
            _alarmOn = new KtListSwitchItem("Alarm On", null, Field.USE_ALL_WIDTH);
            _alarmOn.setSwitchOn(_alarmItem._alarmOn);
        }
        _vfm.add(_alarmOn);
        _vfm.add(new SeparatorField());
        
        // Repeating
        if(_alarmRepeat == null) {         
            _alarmRepeat = new KtListItem("Repeat", _alarmItem.getRepeatStr(),Field.USE_ALL_WIDTH);
            _alarmRepeat.setChangeListener(this);
        }
        else {
            _alarmRepeat.setDetails(_alarmItem.getRepeatStr());
        }
        _vfm.add(_alarmRepeat);
        _vfm.add(new SeparatorField());
          
        // add the alarm time
        if(_alarmTime == null) {
            _alarmTime = new KtListItem("Alarm Time", _alarmItem.getTimeStr(), Field.USE_ALL_WIDTH );
            _alarmTime.setChangeListener(this);
            //_alarmItem._alarmTime;
        }        
        _vfm.add(_alarmTime);
        _vfm.add(new SeparatorField()); 
               
        // snooze time
        if(_snoozeTimeField== null) {
            _snoozeTimeField = new KtListItem("Snooze Time", _alarmItem.getSnoozeStr(), Field.USE_ALL_WIDTH );
            _snoozeTimeField.setChangeListener(this);
            //_alarmItem._alarmTime;
        }        
        _vfm.add(_snoozeTimeField);
        _vfm.add(new SeparatorField());
         
        
        // Alarm music/tone
        if(_alarmSound == null) {         
            _alarmSound = new KtListItem(STR_MUSIC, _alarmItem.getAlarmSoundStr(),Field.USE_ALL_WIDTH);
            _alarmSound.setChangeListener(this);
        }
         
        _vfm.add(_alarmSound);
        _vfm.add(new SeparatorField());   
             
        // alarm volum
        if(_alarmVolume == null) {         
            _alarmVolume = new KtListItem(STR_VOLUME, _alarmItem.getVolumeStr(),Field.USE_ALL_WIDTH);
            _alarmVolume.setChangeListener(this);
        }    
        _vfm.add(_alarmVolume);
        _vfm.add(new SeparatorField()); 
        
        // Vibrate
        if(_alarmVibrate == null) {
            _alarmVibrate = new KtListSwitchItem(STR_VIBRATE, null, Field.USE_ALL_WIDTH);
            _alarmVibrate.setSwitchOn(_alarmItem._alarmVibrateOn);
        }
        _vfm.add(_alarmVibrate);
        _vfm.add(new SeparatorField());
        
        // save and test button      
        _vfm.add(_buttonBar);      

    }
    
    private void refreshScreen() {
        _alarmOn.setSwitchOn(_alarmItem._alarmOn);
        _alarmName.setDetails(_alarmItem._alarmName);
        _alarmTime.setDetails(_alarmItem.getTimeStr());
        _alarmSound.setDetails(_alarmItem.getAlarmSoundStr());
        _alarmRepeat.setDetails(_alarmItem.getRepeatStr());
        _alarmVibrate.setSwitchOn(_alarmItem._alarmVibrateOn);
        _alarmVolume.setDetails(String.valueOf(_alarmItem.getVolumeStr()));
    }     

    // OnSvae override
    protected boolean onSave() {
        return saveAlarm();       
        //return true;
    }      
    
    private boolean saveAlarm() {
        if(this._alarmItem._alarmTime == Long.MIN_VALUE) {
            Dialog.inform("Please set a valid alarm time.");
            return false;
        }

        if(this._alarmItem._alarmName == null) {
            Dialog.inform("Please set a valid alarm name.");
            return false;
        }
                
        // save the 2 switch fields
        _alarmItem._alarmOn = _alarmOn.getSwitchState();
        _alarmItem._alarmVibrateOn = _alarmVibrate.getSwitchState();
        
        // copy the results to orignal item
        AlarmItem.copy(_alarmItem, _alarmItemOrig);
        
        // if the alarm item is new then add it
        Vector alarmList = (Vector)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST);               
        if(!alarmList.contains(_alarmItemOrig)) {
            alarmList.addElement(_alarmItemOrig);
        }
        // commit the settings
        _config.saveSettings();
        return true;
        
    }
    
    // implement the FieldChangeListener
    public void fieldChanged(Field field, int context) {
        // the Enable Schedule is checked, refresh screen
        if(field == this._alarmName) {
            KtQuerryDialog dlg = new KtQuerryDialog("Alarm Name", _alarmItem._alarmName);
           // dlg.setDialogClosedListener(this);
            dlg.doModal();
            _alarmItem._alarmName = dlg.getText();
            _alarmName.setDetails(_alarmItem._alarmName);
            _vfm.invalidate();
        }
        else if(field == this._alarmRepeat) {
            AlarmRepeatEditScreen repeatEditScreen = new AlarmRepeatEditScreen(_alarmItem);
            repeatEditScreen.setAlarmListenr(this);
            ((UiApplication)(Application.getApplication())).pushScreen(repeatEditScreen);
        }
        else if(field == this._alarmSound) {
            editMusic();
        }
        else if(field == this._alarmTime) {
            DateTimePicker datePicker;
            //Calendar cal;
            if(_alarmItem._alarmRepeating) {
                datePicker = DateTimePicker.createInstance( null, null, "hh:mm aa");            
            }
            else {
                datePicker = DateTimePicker.createInstance( null, SimpleDateFormat.DATE_LONG, SimpleDateFormat.TIME_DEFAULT);
            }
            
            
            if(_alarmItem._alarmTime != Long.MIN_VALUE) {
                Calendar cal = Calendar.getInstance();
               // TimeZone tz;
                Date dt;
                if(_alarmItem._alarmRepeating == true) {
                    cal = DateTimeUtilities.getDate((int)_alarmItem._alarmTime);
                   // tz = TimeZone.getTimeZone("GMT");
                }
                else {
                    cal = Calendar.getInstance();
                    dt = new Date(_alarmItem._alarmTime);
                    cal.setTime(dt);  
                   // tz = TimeZone.getDefault();
                }
               // cal.setTimeZone(tz);
               // cal.setTime(dt);   
                datePicker.setDateTime(cal);
            }
            
            if(datePicker.doModal()) {                
                _alarmItem._alarmTime = datePicker.getDateTime().getTime().getTime();
                // get the milli seconds past midnight
                // for repeating alarms
                if(_alarmItem._alarmRepeating) {
                    _alarmItem._alarmTime = TimeUtilities.getMillisecPastMidnight(_alarmItem._alarmTime);
                }
                _alarmTime.setDetails(_alarmItem.getTimeStr());
                _vfm.invalidate();
            }      
        }
        else if(field == this._snoozeTimeField) {
            // set the snooze time
            CustomSpinnerPopup snoozePicker = new CustomSpinnerPopup("Snooze Time", _alarmItem.getSnoozeChoices(), 3, _alarmItem.getSnoozeTimeIndex());
            snoozePicker.doModal();
            
              
            if(snoozePicker.isSet())
            {    
                int choice = snoozePicker.getSelectedIndex();
                _alarmItem.setSnooze(choice);
                _snoozeTimeField.setDetails(_alarmItem.getSnoozeStr());
                _snoozeTimeField.invalidate();
            }    
        }
        else if(field == this._alarmVolume) {
            // alarm volume
            //KtQuerryDialog dlg = new KtQuerryDialog(STR_VOLUME + "(0-10)", String.valueOf(_alarmItem._volume), EditField.EDITABLE|EditField.FILTER_INTEGER);
            
            // set the volume
            int volumeIndex = 0;
            for(int i=0;i<VOLUME_CHOICES.length;i++) {
                if(_alarmItem._volume == VOLUME_CHOICE_INDEX[i]) {
                    volumeIndex = i;
                    break;
                }
            }
            
            CustomSpinnerPopup volumePicker = new CustomSpinnerPopup("Volume", VOLUME_CHOICES, 3, volumeIndex);
            volumePicker.doModal();
            if(volumePicker.isSet())
            {    
                int index = volumePicker.getSelectedIndex();
                _alarmItem._volume = VOLUME_CHOICE_INDEX[index];
                _alarmVolume.setDetails(_alarmItem.getVolumeStr());
                _alarmVolume.invalidate();
            }  
            /*  
            Dialog dlg = new Dialog(STR_VOLUME, VOLUME_CHOICES, VOLUME_CHOICE_INDEX, _alarmItem._volume, null);
            dlg.doModal();
            int volume = dlg.getSelectedValue();
            _alarmItem._volume = volume;            
            _alarmVolume.setDetails(_alarmItem.getVolumeStr());
            _alarmVolume.invalidate();    */  
        }

    }
    
    // implement the AlarmItemListener
    public void AlarmItemChanged(AlarmItem item, Object context) {
        //populateScreen(); 
        if(context instanceof AlarmRepeatEditScreen) {
            this._alarmRepeat.setDetails(_alarmItem.getRepeatStr());
            this._alarmRepeat.invalidate();
            this._alarmTime.setDetails(_alarmItem.getTimeStr());
            this._alarmTime.invalidate();
        }       
       // refreshScreen();
        //_vfm.invalidate();
    }

    // implement KtFocusListener
    /*public void focusChanged(Field field, boolean focusGained) {
        if(field == _alarmSound && focusGained) {
            pla
        }
    }*/
    // implement the FileExplorerListener
    public boolean FileSelected(String uri) {
        FileConnection fc = null;
        
        try 
        {
            // check if the selection is valid
            if(_alarmItem._alarmToneType == AlarmItem.TONE_MUSIC) {
                fc = (FileConnection)Connector.open("file:///" + uri);
                if(fc.isDirectory() && Configuration.isFreeVersion()) {
                    UpgradeDialog.show();
                    //Dialog.alert("The free version can only play a single song for an alarm, please upgrade to full version to use this feature.");
                    return false;
                }
            }
            else if(_alarmItem._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
                // free version does not have radio
                if(Configuration.isFreeVersion()) {
                    UpgradeDialog.show();
                    return false;
                }
            }
            _alarmItem._toneFile = uri;
            _alarmSound.setDetails(_alarmItem.getAlarmSoundStr());
            _vfm.invalidate();
            return true;
        }
        catch (Exception e) {
            return false;
        }
        
    }
    
    protected void makeMenu(Menu menu, int instance)
    {
        menu.add(_saveMenuItem);
    }
    
    public boolean onClose() {
        if(_player != null) {
            _player.stop();
            _player.close();
            _player = null;
        }
        this.stopVibrate();
        return super.onClose();
    }
    
    
    /**
     * <description>
     * @param alarmTime <description>
     */
    static long scheduleNextAlarmTime(long alarmTime) {
        long nextAlarmTime = 0; //calculateNextScheduleTime(alarmTime);

        ApplicationDescriptor currentAppDesc = ApplicationDescriptor.currentApplicationDescriptor();
        ApplicationDescriptor appDesc = new ApplicationDescriptor(currentAppDesc, new String[] {"AUT_SCHEDULE"});
        ApplicationManager.getApplicationManager().scheduleApplication(appDesc, nextAlarmTime, true);
        return nextAlarmTime;
    } 
    
    // update teh music file if it has been changed
    /*
    protected void onExposed()  {
        if(_mediaFileUri[0] != null && !_alarmToneField.getText().equalsIgnoreCase(_mediaFileUri[0])) {
            _alarmToneField.setText(_mediaFileUri[0]);
            
            // create the play list and save it to the _playListVector, but do not save it to a file yet
            _playList.removeAllElements();
            createPlayList(_mediaFileUri[0]);
        }
        super.onExposed();
    }*/
    
    public void editMusic() {
        AlarmSoundExplorerScreen fileExplorer = new AlarmSoundExplorerScreen(_alarmItem._toneFile, true, "Alarm Sound", _alarmItem);
        fileExplorer.setListener(this);
        ((UiApplication)(Application.getApplication())).pushScreen(fileExplorer);
    }
    
    
    // inner class
    private class AlarmSoundSettingField extends VerticalFieldManager implements FieldChangeListener {
        private LabelField _title;
        private RadioButtonField _rbtNoSound;
        private RadioButtonField _rbtBuzz;
        private RadioButtonField _rbtMusic;
        private RadioButtonGroup _rbGroup;
        private ButtonField      _changeMusic;
        private EditField       _alarmMusicField;
        private ObjectChoiceField _volumeField;
        private String[]         _volume = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        
        public AlarmSoundSettingField(String title) {
            _title = new LabelField(title, Field.FIELD_HCENTER);
            _title.setFont(_title.getFont().derive(Font.BOLD));
            
            _rbGroup = new RadioButtonGroup();
            _rbtNoSound = new RadioButtonField(STR_VIBRATE, _rbGroup, _alarmItem._alarmToneType == AlarmItem.TONE_NONE);
            _rbtNoSound.setChangeListener(this);
            _rbtBuzz = new RadioButtonField(STR_BUZZ, _rbGroup, _alarmItem._alarmToneType == AlarmItem.TONE_BUILD_IN);
            _rbtBuzz.setChangeListener(this);
            _rbtMusic = new RadioButtonField(STR_MUSIC, _rbGroup, _alarmItem._alarmToneType == AlarmItem.TONE_MUSIC);            
            _rbtMusic.setChangeListener(this);            
            _changeMusic = new ButtonField(STR_CHANGE_MUSIC, ButtonField.CONSUME_CLICK|Field.FIELD_HCENTER);
            _changeMusic.setChangeListener(this);
            _alarmMusicField = new EditField("Alarm Music:", _alarmItem._toneFile, 200, Field.FIELD_HCENTER |EditField.NON_FOCUSABLE );
            
            _volumeField = new ObjectChoiceField("Volume:", _volume);
            _volumeField.setSelectedIndex(String.valueOf(_alarmItem._volume));
            
            add(_title);
            add(_rbtBuzz);
            add(_rbtMusic);
            add(_rbtNoSound);
            if(_alarmItem._alarmToneType == AlarmItem.TONE_MUSIC) {
                add(_alarmMusicField);
                add(_changeMusic);
            } 
            
            if(_alarmItem._alarmToneType != AlarmItem.TONE_NONE) {
                add(_volumeField);
            }
            
            //this.setChangeListener(this);
        }
        
        /**
         * Set the media file for the Alarm(it can be a file or a path)
         * @param fileName <description>
         */
        public void setText(String fileName) {
            _alarmMusicField.setText(fileName);
        }
        
        public int getVolume() {
            int index = _volumeField.getSelectedIndex();
            int volume = Integer.valueOf((String)_volumeField.getChoice(index)).intValue();
            return volume;
        }
        /**
         * get the text value of the Alarm Music field
         * @return <description>
         */
        public String getText() {
            return _alarmMusicField.getText();
        }
        
        public int getToneType() {
            int type = 0;
            if(_rbtMusic.isSelected()) {
                type = AlarmItem.TONE_MUSIC;
            }
            else if(_rbtBuzz.isSelected()) {
                type = AlarmItem.TONE_BUILD_IN;
            }
            else if(_rbtNoSound.isSelected()) {
                type = AlarmItem.TONE_NONE;
            }
            return type;
        }
        
        private void changedToneType() {
            if(_rbtMusic.isSelected()) {
                if(_alarmMusicField.getManager() != this) {
                    add(_alarmMusicField);                    
                }
                
                if(_volumeField.getManager() != this) {
                    add(_volumeField);
                }
                
                if(_changeMusic.getManager() != this) {
                    add(_changeMusic);           
                }
            }
            else if( _rbtNoSound.isSelected() || _rbtBuzz.isSelected()) {
                if(_alarmMusicField.getManager() == this) {
                    delete(_alarmMusicField);
                }
                if(_changeMusic.getManager() == this) {
                    delete(_changeMusic); 
                }
                
                // if vibrate is selected then remove the volume field
                if( _rbtNoSound.isSelected() && _volumeField.getManager() == this) {
                    delete(_volumeField);
                } 
                
                // if buzz is selected and volume is not shown, then show it
                if(_rbtBuzz.isSelected() && _volumeField.getManager() != this) {
                    add(_volumeField);
                }          
            }
            this.invalidate();
        }
        public void fieldChanged(Field field, int context) {
            // the Enable Schedule is checked, refresh screen
            if(field == _rbtMusic || field == _changeMusic) {
                if(_rbtMusic.isSelected()) {
                    editMusic();
                    changedToneType();
                }
            }
            else if(field == _rbtBuzz) {
                if(_rbtBuzz.isSelected()) {
                    changedToneType();
                }
            }
            else if(field == _rbtNoSound) {
                if(_rbtNoSound.isSelected()) {
                    changedToneType();
                }
            }
        }             
    }
}
