/*
 * AlarmConfirmationDialog.java
 *
 * © <your company here>, 2009-2014
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;

/**
 * 
 */
class AlarmConfirmationDialog extends Dialog implements DialogClosedListener {
    public static final int CHOICE_DISMISS_ALARM = 1;
    public static final int CHOICE_SNOOZE = 2;
    
    private boolean _cancelReboot;
    private AlarmItem _item;
    
    private AlarmConfirmationDialog(AlarmItem item, Object[] choices, int[] values, int defaultChoice, Bitmap bitmap) {      
        super(item._alarmName , choices,values,defaultChoice, Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION)); 
        _item = item;
    }
    
    public static void showDialog(AlarmItem item)
    {   
        Object[] list;                    
        int[] indexList;
        int defaultChocie; 
        if(item._alarmToneType == AlarmItem.TONE_RADIO_SOFTWARE) {
            // if Radio, then there is no Snooze
            indexList = new int[1];            
            indexList[0] = CHOICE_DISMISS_ALARM; 
            list = new Object[1];
            list[0] = "Dismiss"; 
            defaultChocie = 0;         
        } else {
            indexList = new int[2];            
            indexList[0] = CHOICE_DISMISS_ALARM;
            indexList[1] = CHOICE_SNOOZE; 
            list = new Object[2];
            list[0] = "Stop Alarm";
            list[1] = "Snooze"; 
            defaultChocie = 1;           
                        
        }
        
        AlarmConfirmationDialog dlg = new AlarmConfirmationDialog(item, list,indexList,defaultChocie, Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION));
        dlg.setDialogClosedListener(dlg);
        
        synchronized (Application.getEventLock())
        {
            Ui.getUiEngine().pushGlobalScreen(dlg, 1, UiApplication.GLOBAL_QUEUE);
        }               
    }
    
    // somehow this method is not called when the user clicks a button to close the dialoag
    // the code is just left here for reference
    public boolean onClose() {
        int choice = getSelectedValue();
        if(choice == CHOICE_DISMISS_ALARM) {
            _item.stopAlarm();
        }
        else {
            _item.snoozeAlarm();
        }
        return super.onClose();
    }
    
 /*   protected  boolean navigationClick(int status, int time) {
        int choice = getSelectedValue();
        if(choice == CHOICE_DISMISS_ALARM) {
           // AlarmAppContext.getInstance().stopAlarm();
           _item.stopAlarm();
        }
        else {
            //AlarmAppContext.getInstance().snoozeAlarm();
            _item.snoozeAlarm();
        }
        return super.onClose();
    }*/
        
    public void dialogClosed(Dialog dialog, int choice) {
        if(dialog != this)
        {
            return;
        }
        if(choice == CHOICE_DISMISS_ALARM) {
           // AlarmAppContext.getInstance().stopAlarm();
           _item.stopAlarm();
        }
        else {
            //AlarmAppContext.getInstance().snoozeAlarm();
            _item.snoozeAlarm();
        }       
    }   
} 
