/*
 * KtCheckbox.java
 *
 *  © Karvi Technologies, Inc, 2010
 * Confidential and proprietary.
 */

package com.karvitech.api.appTools;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;

public class KtCheckbox extends Field {

    private static int RIGHT_MARGIN = 10; // the right margin for the switch
     
    private  String _strOn = "On";
    private  String _strOff = "Off";   
    // images
    private static Bitmap _onImage; 
    private static Bitmap _offImage;

    //statics
    private static int _imgHeight;
    private static int _imgWidth; 
    
    private int _preferedHeight;
    private int _preferedWidth;
    private boolean _toggleable = true;
    private boolean _switchOn;
    private boolean _accurateToggle; // for touch screen devices, use accurate toggle 
    private int _xOffset;
    private int _yOffset;
  
    static {
        _onImage = Bitmap.getBitmapResource("btn_check_on_25x36.png");
        _offImage = Bitmap.getBitmapResource("btn_check_off_25x36.png");
        _imgWidth = _onImage.getWidth();
        _imgHeight = _onImage.getHeight();
        
    }

   public  KtCheckbox(String title, boolean initialState) {
       super();
       this.setSwitchOn(initialState);
       init();
   }    
   
   public  KtCheckbox(long style) {
        super(style);
        
        init();
    }
    
   private void init() {
       // initialize the image x, y offset
	   if(Display.getHeight()*Display.getWidth() >= 320*480) {
		   _preferedHeight = 46;
		   _preferedWidth = 46;
	   }
	   else {
		   _preferedHeight = 36; 
		   _preferedWidth = 36;
	   }
	   _xOffset = this.getPreferredWidth() - _imgWidth - RIGHT_MARGIN;
       _yOffset = this.getPreferredHeight() -_imgHeight;
       _yOffset >>= 1;
 
   }
    public void setSwitchOn(boolean on) {
        _switchOn = on;
        invalidate();
    }
    
    public boolean getSwitchState() {
        return _switchOn;
    }
    
    public boolean isToggleEnabled() {
    	return _toggleable;
    }
    
    public void setToggleable(boolean toggleable) {
    	_toggleable = toggleable;
    }
    
    public boolean isFocusable()
    {
        return true;
    }
    protected  boolean navigationClick(int status, int time)  {
        // force repaint
    	if(this.isToggleEnabled()) {
	        _switchOn = !_switchOn;
	        invalidate();        

    	}
    	this.fieldChangeNotify(0);
    	return true;
    }    
    
    /**
     * use touch event to accurately toggle the switch
     */
    /*
    protected  boolean  touchEvent(TouchEvent message) {
    	if(message.getEvent() == TouchEvent.UP) {
	    	int x = message.getX(1);
	    	int y = message.getY(1);
	        invalidate();
	    	if( x > _xOffset) {
		        _switchOn = !_switchOn;
	            fieldChangeNotify(1); // touched the switch
	    	}
	    	else {
	    		// touched non-switch part
	    		// do not change the switch value
	    		fieldChangeNotify(0); 
	    	}
	    	return true;
    	}
    	else {
    		return false; // do not consume if we do not do anything    
    	}
    }*/
     
    protected void paint(Graphics g){
       // super.paint(g);
        drawSwtich( g);
    }
    private void drawSwtich(Graphics g) {
        Bitmap darwImg = _switchOn?_onImage:_offImage;
        g.drawBitmap(_xOffset, _yOffset, _imgWidth, _imgHeight, darwImg, 0, 0);
    }

	protected void layout(int width, int height) {
		// TODO Auto-generated method stub
		setExtent(getPreferredWidth(),getPreferredHeight());
	}
    public int getPreferredWidth(){
    	return _preferedWidth;
    }
    
    public int getPreferredHeight(){
        return _preferedHeight;
    }	
} 
