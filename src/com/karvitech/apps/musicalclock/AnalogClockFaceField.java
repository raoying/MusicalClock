/*
 * ClockFaceField.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.apps.musicalclock;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
//import com.ws.api.utilities.math.*;
import java.util.TimeZone;

import net.rim.device.api.ui.Graphics;

/**
 * 
 */
class AnalogClockFaceField extends ClockFaceField {
    Bitmap _image = Bitmap.getBitmapResource("clock_background.png");
    Bitmap _image1 = Bitmap.getBitmapResource("hour_200_18.png");
    AnalogClockFaceField(MusicalClockSettingItem settingItem) {
        super(settingItem, false);
        clockUpdated();
    }
    
    public int getPreferredWidth() {
        return _image.getWidth();
    }
    public int getPreferredHeight() {
        return _image.getHeight();
    } 
    protected void layout(int width, int height) {
        setExtent(getPreferredWidth(),getPreferredHeight()); 
    }
    
    protected void paint(Graphics graphics) {
        //super.paint(graphics);
        paintClockFace(graphics);
        paintClockHands(graphics);        
    }    
    
    private int getXOffset() {
        return getPreferredWidth()/2;
    }
    
    private int getYOffset() {
        return getPreferredHeight()/2;
    }
    void paintClockFace(Graphics graphics) {
        graphics.setBackgroundColor(Color.VIOLET);
        graphics.clear();
        graphics.drawBitmap(0,0,_image.getWidth(),_image.getHeight(), _image, 0, 0);
        graphics.drawBitmap(40,0,_image1.getWidth(),_image1.getHeight(), _image1, 0, 0);
    }
    
    void paintClockHands(Graphics graphics) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        int xOffset = getXOffset();
        int yOffset = getYOffset();
        
        // draw the hour hand
        xPoints[0] = 0 + xOffset;
        xPoints[1] = -3 + xOffset;
        xPoints[2] = 3 + xOffset;
        yPoints[0] = 0 + yOffset;
        yPoints[1] = yPoints[2] = 60 + yOffset;
        
        int angle = (360*_hour/12);
        
    //    TransformationMatrix.rotate(xPoints, yPoints, angle);
        graphics.drawFilledPath(xPoints, yPoints, null, null);        
    }
} 
