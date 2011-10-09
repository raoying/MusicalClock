/*
 * KtListItem.java
 *
 * © Karvi Technologies, Inc, 2010
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
public class KtListItem extends Field implements DrawStyle {
    public static int INDICATOR_NONE = 0;
    public static int INDICATOR_CHECK = 1;
    public static int INDICATOR_ACTION = 2;
    
    private static int _titleFontSize;
    private static int _detailsFontSize;
    private static int _detailsYOffset;
    
    private int _preferedHeight;
    private int _preferedWidth;
    
    private int _indicatorStyle;
    private String _title;
    private String _details; 
    private boolean _isFocusable = true;
    private FontFamily fontFamily[] = FontFamily.getFontFamilies();
    
    private int _backgroundFoucsColour = Color.BLUE;
    private int _backgroundUnFoucsColour = Color.WHITE;
   // private int _fontFocusColor = Color.WHITE;

    private int _backgroundColor = Color.WHITE; // background color 
        
    static {
        if(Display.getHeight()*Display.getWidth() >= 320*480) {
            _titleFontSize = 22;
            _detailsFontSize = 18;
            _detailsYOffset = 28;
        }
        else {
            _titleFontSize = 18;
            _detailsFontSize = 15;
            _detailsYOffset = 21;        
        }
    }
    
    public KtListItem(String title, String details, long style) {
        super(style);
        _title = title;
        _details = details;
        if(Display.getHeight()*Display.getWidth() >= 320*480) {
            _preferedHeight = 46;
        }
        else {
            _preferedHeight = 36;
        }
    }
   
    public void setPreferedWidth(int width) {
    	_preferedWidth = width;
    }
    public void setTitle(String title) {
        _title = title;
    }
    
    public void setDetails(String details) {
        _details = details;
    }
    
    public void setIndicatorStyle(int indicatorStyle) {
        _indicatorStyle = indicatorStyle;
    }
        
    public int getPreferredWidth(){
    	if(_preferedWidth <=0) {
    		Screen sr = UiApplication.getUiApplication().getActiveScreen();
    		return sr.getWidth();
    	}
    	else {
    		return _preferedWidth;
    	}
    }
    
    public int getPreferredHeight(){
        return _preferedHeight;
    }
    protected void layout(int width, int height) {
        setExtent(getPreferredWidth(),getPreferredHeight()); 
    }
        
    protected void drawFocus(Graphics graphics, boolean on){
        //graphics.invert(1, 1, getWidth(),getHeight());
        //graphics.invert(0, 0, getPreferredWidth(),getPreferredHeight());
    }
    
    protected void onFocus(int direction)
    {
        // set teh bkground and font color
        super.onFocus(direction);
        _backgroundColor = _backgroundFoucsColour;
        invalidate();
    }

    protected void onUnfocus()
    {
        // set teh bkground and font color
        super.onUnfocus();
        _backgroundColor = _backgroundUnFoucsColour;
        invalidate();
    }
    
    // make it public so can be easily used
    public void invalidate() {
        super.invalidate();
    }
    protected void paint(Graphics g){
       
        // drawRect(0,0,this.getWidth(),this.getHeight());
        g.setBackgroundColor(_backgroundColor);
        g.clear();
        
        Font oldFont = this.getFont();
        Font font = oldFont.derive(Font.BOLD,_titleFontSize);        
        
        //this.setFont(font);
        g.setFont(font);
        if(_title != null) {
            // for title, color is black if unfocused
            if(this.isFocus()) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.BLACK);
            }        
            int yOffset = (_details != null)?2:12;
            g.drawText(_title, 0,yOffset,0,this.getWidth());
        }
       // font = Font.getDefault().derive(Font.PLAIN, 9, Ui.UNITS_pt);
        font = fontFamily[0].getFont(FontFamily.SCALABLE_FONT,_detailsFontSize);   
       // font = font.derive(Font.PLAIN,_detailsFontSize); 
       // this.setFont(font);
        g.setFont(font);
        int color = g.getColor();
        

        if(_details != null) {
            // for details, color is gray if un focused
            if(this.isFocus()) {
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.GRAY);
            }
            g.drawText(_details, 0,_detailsYOffset,0,this.getWidth());
        }
        this.setFont(oldFont);
        g.setFont(oldFont);
    }
    
    public void setFocusable(boolean isFocusable)
    {
        this._isFocusable = isFocusable;
    } 
    
    public boolean isFocusable()
    {
        return _isFocusable;
    }
    
    public boolean keyChar(char key, int status, int time)
    {
        if (key == Characters.ENTER)
        {
            fieldChangeNotify(0);
            return true;
        }
        
        return false;
    }
    
    protected  boolean navigationClick(int status, int time)  {
        fieldChangeNotify(0);
        return true;
    }   
} 
