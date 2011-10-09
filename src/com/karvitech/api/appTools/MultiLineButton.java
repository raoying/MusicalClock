/*
 * MultiLineButton.java
 *
 * © Karvi Technology Inc., 2009-2014
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;

import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*; 
import net.rim.device.api.system.*;
/**
 * 
 */
public class MultiLineButton extends ButtonField implements DrawStyle{
    
    private String[] _strList;
    
    public MultiLineButton(String[] strList, long style) {
        super(style);
        _strList = strList;
    }
   
    
    public int getPreferredWidth(){
        Screen sr = UiApplication.getUiApplication().getActiveScreen();
        return sr.getWidth();
    }
    
    public int getPreferredHeight(){
        return 44;
    }
    
   /* protected void drawFocus(Graphics graphics, boolean on){
        //graphics.invert(1, 1, getWidth(),getHeight());
        graphics.invert(0, 0, getPreferredWidth(),getPreferredHeight());
    }*/
    
    protected void paint(Graphics g){
        // drawRect(0,0,this.getWidth(),this.getHeight());
        g.drawText(_strList[0], 0,0,0,this.getWidth());
        g.drawText(_strList[1], 0,20,0,this.getWidth());
    }
} 
