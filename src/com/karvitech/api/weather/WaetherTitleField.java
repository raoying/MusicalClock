package com.karvitech.api.weather;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class WaetherTitleField extends Field {
    private int _preferedHeight;
    private int _preferedWidth;
    private Bitmap _symbolImage; 
    private float _highTemp;
    private float _lowTemp;
    private int _symbolId;
    private char _unit;
    
    public void setTemp(float high, float low) {
    	_highTemp = high;
    	_lowTemp = low;
    }
    
    public void setSymbol(int id) {
    	_symbolId = id;
    	_symbolImage = getSymbolImage(_symbolId);
    }
    
	protected void layout(int width, int height) {
		// TODO Auto-generated method stub
		setExtent(getPreferredWidth(),getPreferredHeight());
	}
    public int getPreferredWidth(){
    	_preferedWidth = Display.getWidth()/5;
    	return _preferedWidth;
    }
    
    public int getPreferredHeight(){
    	_preferedHeight = Display.getWidth()/5;
    	return _preferedHeight;
    }	

	protected void paint(Graphics graphics) {
		// TODO Auto-generated method stub
		int xOffset = 0;
		int yOffset = 0;
		int imgWidth = _symbolImage.getWidth();
		int imgHeight = _symbolImage.getHeight();
		graphics.drawBitmap(xOffset, yOffset, imgWidth, imgHeight, _symbolImage, 0, 0);
		
		// draw temp and unit

	}
	
	private Bitmap getSymbolImage(int symbolId) {
		String fileName = symbolId + ".png";
		return Bitmap.getBitmapResource(fileName);
	}

}
