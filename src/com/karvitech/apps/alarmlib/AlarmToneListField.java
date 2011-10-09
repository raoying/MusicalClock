/*
 * AlarmToneListField.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.apps.alarmlib;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;

/**
 * 
 */
class AlarmToneListField extends ListField implements ListFieldCallback {
    private String[] _elements;
    AlarmToneListField(String[] elements) {
        _elements = elements;
        setCallback(this);
        setSize(getSize());
    }
    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField , Graphics , int , int , int)
     */
    public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) 
    {
        if (index < _elements.length) 
        {            
            graphics.drawText(_elements[index], 0, y);
        }
    }

    
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField , int)
     */
    public Object get(ListField listField, int index) 
    {
        if (index >= 0 && index < _elements.length)
        {
            return _elements[index];
        }
        
        return null;
    }

    
   /**
    * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
    */
    public int getPreferredWidth(ListField listField) 
    {
        return Display.getWidth();
    }

    /**
    * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField , String , int)
    */
    public int indexOfList(ListField listField, String prefix, int start) 
    {
        return listField.indexOfList(prefix,start);
    }

    
    /**
     * Allows space bar to page down.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char , int , int)
     */
    public boolean keyChar(char key, int status, int time)
    {
        if (getSize() > 0 && key == Characters.SPACE) 
        {
            getScreen().scroll(Manager.DOWNWARD);
            return true;
        }
        
        return super.keyChar(key, status, time);
    }

    
    /**
     * Retrieves the number of elements in list field.
     * 
     * @return The number of elements in this list field.
     */
    public int getSize() 
    {
        return (_elements != null) ? _elements.length : 0;
    }
    
      
} 
