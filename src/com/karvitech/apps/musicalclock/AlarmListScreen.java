//#preprocess
/*
 * AlarmListScreen.java
 *
 * © <your company here>, 2003-2008
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
//#ifdef VERION_4_6_ABOVE 
import net.rim.device.api.ui.decor.*;
//#endif
import com.karvitech.api.appTools.*;
import com.karvitech.apps.alarmlib.*;

/**
 * 
 */
class AlarmListScreen extends MainScreen implements FocusChangeListener, FieldChangeListener, ConfigurationListener {
    static final String STR_ALARM_LIST = "Alarm List";
    static final String STR_NEW_ALARM = "New Alarm";
     
    Vector _alarmSettings;
    MainScreen _screen;
    private BasicEditField _cityNameBasic;  
    private VerticalFieldManager _alarmVfm;
    
    private MenuItem _saveMenuItem = new MenuItem("Save", 100, 10)
    {
        public void run()
        {
            saveChanges();            
            _screen.close();
            MusicalClockMainScreen.getInstance().refresh();
        }
    };     
    
    private MenuItem _addMenuItem = new MenuItem(STR_NEW_ALARM, 100, 10)
    {
        public void run()
        {
            // test code
            //Util.getApplist(); 
            addAlarm();
        }
    }; 
    
    private MenuItem _deleteMenuItem = new MenuItem("Delete Alarm", 100, 10)
    {
        public void run()
        {
            deleteAlarm();
        }
    };    
        
    public static void showScreen() {
        ((UiApplication)(Application.getApplication())).pushScreen(new AlarmListScreen());
    }
    private AlarmListScreen() {
        Configuration.getInstance().addListener(this);
        LabelField title = new LabelField(STR_ALARM_LIST, Field.FIELD_HCENTER);
        title.setFont(title.getFont().derive(Font.BOLD));    
        setTitle(title);
                
        populateScreen();     
        
        // test code
       // Util.getApplist();   
    }
    
    void populateScreen() {
        deleteAll();
        add(new ButtonField(STR_NEW_ALARM, Field.FIELD_HCENTER) {
                /*public int getPreferredWidth()  {
                    return Display.getWidth();
                } */           
                protected boolean navigationClick(int status,int time) {
                    addAlarm();
                    return true;
          
                }         
            });        
        //setTitle(new ButtonField("Title Button"));
        _screen = this;
        _alarmVfm = new VerticalFieldManager(Field.FIELD_HCENTER|Field.USE_ALL_WIDTH);
//#ifdef VERION_4_6_ABOVE         
        XYEdges edges = new XYEdges(5, 5, 5, 5);
        Border border = BorderFactory.createRoundedBorder(edges);        
        _alarmVfm.setBorder(border);
//#else
        add(new SeparatorField());
//#endif             
        //_settings = MusicalClockSettings.getClockSettings();
        _alarmSettings = (Vector)Configuration.getInstance().getKeyValue(MusicalClockContext.KEY_ALARM_LIST);        

        for(int i=0;i<_alarmSettings.size();i++) {
            AlarmItem item = (AlarmItem)(_alarmSettings.elementAt(i));
            AlarmItemField itemField = new AlarmItemField(item);
            itemField.setChangeListener(this);
         //   itemField.setFocusListener(this);
            _alarmVfm.add(itemField);
            _alarmVfm.add(new SeparatorField());
        }
        
        add(_alarmVfm);         
    }
    
    void addAlarm() {
        if(Configuration.isFreeVersion())  {
            if(_alarmSettings.size() >= 2) {
                //Dialog.inform("The free version supports 2 alarms. The full version supports unlimited alarms. Please check out the full version.");
                UpgradeDialog.show();
                return;
            } 
        }             
        AlarmItem item = new AlarmItem();
        UiApplication.getUiApplication().pushScreen(new AlarmEditScreen(item));
    }
    
    void deleteAlarm() {
        Field field = _alarmVfm.getFieldWithFocus();
      //  int fieldIndex = getFieldWithFocusIndex();
        //Field sepratorField = this.getField(fieldIndex + 1);

        if(field instanceof AlarmItemField) {
            AlarmItemField itemField = (AlarmItemField)field;
            AlarmItem item = itemField.getItem();
            _alarmSettings.removeElement(item);
            //_alarmVfm.delete(_alarmVfm.getFieldWithFocus());
        }
 
        //delete(sepratorField);
        Configuration.getInstance().saveSettings();
    }
    
    void saveChanges() {
        boolean isDirty = false;
        int itemCount = 0;
        for(int i=0; i<this.getFieldCount(); i++) {
            Field field = this.getField(i);
            if(field instanceof AlarmItemField) {
                AlarmItemField itemField = (AlarmItemField)field;
                if(itemField.isDirty())
                {                    
                    isDirty = true;
                    AlarmItem item = itemField.getItem();
                    if(itemCount >= _alarmSettings.size())
                    {
                        _alarmSettings.addElement(item);
                    }               
                  /*  else {
                        AlarmItem item = (AlarmItem)_settings.elementAt(i);
                    }*/
                }
                itemCount++;
            }
        }
        if(isDirty) {
            Configuration.getInstance().saveSettings();
        }
    }

    protected void makeMenu(Menu menu, int instance) {
        menu.deleteAll();
        menu.add(_addMenuItem);
        if(_alarmVfm.getFieldWithFocus() instanceof AlarmItemField)
    //    if(this.getFieldCount() > 2)
        {
            menu.add(_deleteMenuItem);
        }
        menu.add(_saveMenuItem);
    }
    
    protected boolean onSavePrompt() {
        return true;
    }
    protected boolean onSave() {
        saveChanges();
        
        return true;
    }
       
    public boolean onClose() {
        // remove listener when the screen is closed
        Configuration.getInstance().removeListener(this);
        return super.onClose();
    }
   
    void refresh() { 
        this.deleteAll();
        this.invalidate();
        populateScreen();  
    }
    public void focusChanged(Field field, int eventType)
    {
      /*  if(field instanceof AlarmItemField) {
            AlarmItem item = ((AlarmItemField)field).getItem();
            UiApplication.getUiApplication().pushScreen(new AlarmEditScreen(item));            
        }*/
    }     
    
    /**
     * implementing FieldChangedListener
     * @param field <description>
     * @param context <description>
     */
    public void fieldChanged(Field field, int context) {
        if(field instanceof AlarmItemField) {
        	// context 0 means either a navigation click
        	// or touch on the non switch part
    		AlarmItem item = ((AlarmItemField)field).getItem();
        	if(context == 0) {
        		UiApplication.getUiApplication().pushScreen(new AlarmEditScreen(item));            
        	}
        	else if(context == 1) {
        		// context 1 means toggled the switch
        		// save the settings
        		item._alarmOn = !item._alarmOn;
        		Configuration.getInstance().saveSettings();    
        	}
        }
    }
        // implements the listener
    public void configurationChanged() {
        refresh();
    }
    
    
}
