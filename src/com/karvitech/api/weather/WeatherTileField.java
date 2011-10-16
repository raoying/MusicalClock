package com.karvitech.api.weather;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;

public class WeatherTileField extends Field {
    private int _preferedHeight;
    private int _preferedWidth;
    private Bitmap _symbolImage; 
    private float _highTemp;
    private float _lowTemp;
    private int _symbolId;
    private char _unit;
    private DayWeatherInfo _dayInfo;
    
    private static int _titleFontSize;
    private static int _detailsFontSize;
    private static int _detailsYOffset;
    
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
    
    public void updateWeather(DayWeatherInfo dayInfo) {
    	_dayInfo = dayInfo;
    	_highTemp = dayInfo.getHighTemp();
    	_lowTemp = dayInfo.getLowTemp();
    	_symbolImage = this.getSymbolImage(dayInfo.getCurrentSymbol());
    }
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
		
        int color = Color.BLACK;
        graphics.setBackgroundColor(color);
        graphics.clear();
        
        
        // draw image
		if(_symbolImage!=null) {
			int imgWidth = _symbolImage.getWidth();
			int imgHeight = _symbolImage.getHeight();
			graphics.drawBitmap(xOffset, yOffset, imgWidth, imgHeight, _symbolImage, 0, 0);
		}
		//graphics.drawText
		// draw temp and unit
        Font oldFont = this.getFont();
        Font font = oldFont.derive(Font.BOLD,_titleFontSize);        
        
        //this.setFont(font);
        graphics.setFont(font);
        String text = (int)_lowTemp + "/" + (int)_highTemp;
        if(text != null) {
            // for title, color is black if unfocused
            if(this.isFocus()) {
            	graphics.setColor(Color.WHITE);
            } else {
            	graphics.setColor(Color.WHITE);
            }        
            //int yOffset = (_details != null)?2:12;
            graphics.drawText(text, 0, getHeight() - 12,0,this.getWidth());
        }
        this.setFont(oldFont);
        graphics.setFont(oldFont);
	}
	
	private Bitmap getSymbolImage(int symbolId) {
		String fileName = symbolId + ".png";
		fileName = "01n.png";
		return Bitmap.getBitmapResource(fileName);
	}

}
