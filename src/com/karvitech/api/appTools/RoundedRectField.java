/*
 * RoundRectField.java
 *
 * © <your company here>, 2003-2008
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;

import  net.rim.device.api.ui.*;

// Extending field to display a rounded rectangle with a gradient.
public class RoundedRectField extends Field {

  // Layout values
  private static final int CURVE_X = 12; // X-axis inset of curve
  private static final int CURVE_Y = 12; // Y-axis inset of curve
  private static final int MARGIN = 2;   // Space within component boundary
  
  // Static colors
  private static final int TEXT_COLOR = 0xFFFFFF;   // White
  private static final int BORDER_COLOR = 0x4A4A4A; // dark gray
  private static final int BACKGROUND_COLOR = 0xFFFFFF; // White
     
  // Point types array for rounded rectangle. Each point type
  // corresponds to one of the colors in the colors array. The
  // space marks the division between points on the top half of
  // the rectangle and those on the bottom.
  private static final byte[] PATH_POINT_TYPES = {
    Graphics.CURVEDPATH_END_POINT, 
    Graphics.CURVEDPATH_QUADRATIC_BEZIER_CONTROL_POINT,
    Graphics.CURVEDPATH_END_POINT, 
    Graphics.CURVEDPATH_END_POINT, 
    Graphics.CURVEDPATH_QUADRATIC_BEZIER_CONTROL_POINT,
    Graphics.CURVEDPATH_END_POINT, 
           
    Graphics.CURVEDPATH_END_POINT, 
    Graphics.CURVEDPATH_QUADRATIC_BEZIER_CONTROL_POINT,
    Graphics.CURVEDPATH_END_POINT, 
    Graphics.CURVEDPATH_END_POINT, 
    Graphics.CURVEDPATH_QUADRATIC_BEZIER_CONTROL_POINT,
    Graphics.CURVEDPATH_END_POINT, 
  };
  
  // Colors array for rounded rectangle gradient. Each color corresponds
  // to one of the points in the point types array. Top light, bottom black.
  private static final int[] PATH_GRADIENT = {
    0xAAAAAA, 0xAAAAAA, 0xAAAAAA, 0xAAAAAA, 0xAAAAAA, 0xAAAAAA,
          
    0x000000, 0x000000, 0x000000, 0x000000, 0x000000, 0x000000
  };
   
  // Center our readonly field in the space we're given.
  public RoundedRectField() {
    super(FIELD_HCENTER | FIELD_VCENTER | READONLY);
  }
       
  // This field in this demo has a fixed height.
  public int getPreferredHeight() { return 70; }
     
  // This field in this demo has a fixed width.
  public int getPreferredWidth() { return 240; }
      
  // When layout is requested, return our height and width.
  protected void layout (int width, int height) {
    setExtent(getPreferredWidth(), getPreferredHeight());
  }
   
  // When painting is requested, do it ourselves.
  protected void paint(Graphics g) {
    // Clear this area to white background, fully opaque.
    g.clear();
    g.setGlobalAlpha(255);
    g.setBackgroundColor(BACKGROUND_COLOR);
                
    // Drawing within our margin.
    int width = getPreferredWidth() - (MARGIN * 2);
    int height = getPreferredHeight() - (MARGIN * 2);
           
    // Compute paths for the rounded rectangle. The 1st point (0) is on the left
    // side, right where the curve in the top left corner starts. So the top left
    // corner is point 1. These points correspond to our static arrays.
    int[] xPts = {
      0, 0, CURVE_X, width - CURVE_X, width, width,
      width, width, width - CURVE_X, CURVE_X, 0, 0
    };
    int[] yPts = {
      CURVE_Y, 0, 0, 0, 0, CURVE_Y,
      height - CURVE_Y, height, height, height, height, height - CURVE_Y
    };
            
    // Draw the gradient fill.
    g.drawShadedFilledPath(xPts, yPts, PATH_POINT_TYPES, PATH_GRADIENT, null);
         
    // Draw a rounded rectangle for the outline.
    // I think that drawRoundRect looks better than drawPathOutline.
    g.setColor(BORDER_COLOR);
    g.drawRoundRect(0, 0, width, height, CURVE_X * 2, CURVE_Y * 2);
               
    // Place some text in the center.
    String someText = "Some Text";
    Font font = Font.getDefault().derive(Font.PLAIN, 9, Ui.UNITS_pt);
    int textWidth = font.getAdvance(someText);
    int textHeight = font.getHeight();
    g.setColor(TEXT_COLOR);
    g.setFont(font);
    g.drawText(someText, 
      (width / 2) - (textWidth / 2) - MARGIN,
      (height / 2) - (textHeight / 2) - MARGIN);
  }
}

