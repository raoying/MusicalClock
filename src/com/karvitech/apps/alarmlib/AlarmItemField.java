/*
 * AlarmitemField.java
 *
 * © <your company here>, 2009-2014
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;

import com.karvitech.api.appTools.KtCompositeListItem;
import com.karvitech.api.appTools.KtListItem;
import com.karvitech.api.appTools.KtListSwitchItem;
/**
 * 
 */
public class AlarmItemField extends KtCompositeListItem {// KtListSwitchItem { // MultiLineButton {
    private AlarmItem _item;
    
    public AlarmItemField(AlarmItem item) {
        super(item.getTitle(),item.getDeatils(),Field.USE_ALL_WIDTH);
        //super(item.getDisplayStr(), Field.USE_ALL_WIDTH |ButtonField.CONSUME_CLICK );
        _item = item;
        this.setSwitchOn(_item._alarmOn);
       // this.setToggleable(false);
    }
    
    public AlarmItem getItem() {
        return _item;
    }
    
    /**
     * When an item is clicked, the navigationClick method does nothing
     * @param status <description>
     * @param time <description>
     * @return <description>
     */
  /*  protected boolean navigationClick(int status, int time) {
     //   UiApplication.getUiApplication().pushScreen(new AlarmEditScreen(_item));
        return super.navigationClick( status, time) ;
    }
    */
 /*   public int getPreferredWidth()  {
        return Display.getWidth();
    }*/
} 
