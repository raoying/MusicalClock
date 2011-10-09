/*
 * VerticalFieldManagerWithBackground.java
 *
 * © Karvi Technology Inc, 2003-2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;

import net.rim.device.api.ui.component.*;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.container.*;


/**
 *  This class is only useful for pre 4.6 OS
 */

public class  VerticalFieldManagerWithBackground extends VerticalFieldManager {
    public static final int BORDER_NONE = 0;
    public static final int BORDER_ROUND_CORNER = 1;
    private int _borderStyle;
    private int _backGroundColor = -1;
        
    public VerticalFieldManagerWithBackground(int borderStyle, int backGroundColor) {
        super(Manager.NO_VERTICAL_SCROLL | Manager.NO_VERTICAL_SCROLLBAR | USE_ALL_WIDTH | USE_ALL_HEIGHT);
        _borderStyle = borderStyle;
        _backGroundColor = backGroundColor;
    } 
    
    public void paintBackground( Graphics g ) {

                    g.clear();
                    int color = g.getColor();
                    if(_backGroundColor == -1) {
                        g.setColor( Color.LIGHTGREY );
                    }
                    else {
                        g.setColor(_backGroundColor);
                    }
                    
                    g.fillRect( 0, 0, Display.getWidth(), Display.getHeight() );
                    g.setColor( color );
                
    }
          
    public void paint(Graphics graphics)
    {
                graphics.setBackgroundColor(Color.LIGHTGREY);                
                graphics.clear();
                super.paint(graphics);
                if(_borderStyle != BORDER_NONE) {
                    int x = this.getLeft();
                    int y = this.getTop();
                    int width = Math.min(this.getPreferredWidth(), Display.getWidth());
                    int height = this.getPreferredHeight();
                    graphics.drawRoundRect(x, y, width, height, 20, 20);
                }
    }
    
    public int getPreferredWidth() {
        if(_borderStyle != BORDER_NONE) {
            return Display.getWidth() - 10;
        }
        else {
            return super.getPreferredWidth();
        }
    }
    protected void sublayout(int width, int height) {
                 //super.sublayout(width, height)
                // setExtent(Display.getWidth(), Display.getHeight());
                 super.sublayout(Display.getWidth(), Display.getHeight());
    }
}
