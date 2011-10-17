package com.karvitech.api.weather;

import com.karvitech.api.appTools.Util;

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
    private Bitmap _scaledSymbolImage; 
    private float _highTemp;
    private float _lowTemp;
    private int _symbolId;
    private char _unit;
    private DayWeatherInfo _dayInfo;
    private static int _orientation;
    
    private static int _titleFontSize;
    private static int _detailsFontSize;
    private static int _detailsYOffset;
    
    
    public void updateWeather(DayWeatherInfo dayInfo) {
    	_orientation = Display.getOrientation();
    	_dayInfo = dayInfo;
    	_highTemp = dayInfo.getHighTemp();
    	_lowTemp = dayInfo.getLowTemp();
    	_symbolImage = this.getSymbolImage(dayInfo.getCurrentSymbol());
    	this.configSize();
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
    	Font font = getFont().derive(Font.PLAIN,_titleFontSize);  
    	int fontHeight = font.getHeight();
    	if(_symbolImage != null) {
        	_preferedHeight = fontHeight*2 + _symbolImage.getHeight();
        	return _preferedHeight;  		
    	}
    	else 
    		return Display.getHeight()/5;
    }	

    public void configSize() {
        if(Display.getHeight()*Display.getWidth() >= 320*480) {
            _titleFontSize = 18;
            _detailsFontSize = 18;
            _detailsYOffset = 28;
        }
        else {
            _titleFontSize = 15;
            _detailsFontSize = 15;
            _detailsYOffset = 21;        
        }
        
        if(_orientation != Display.getOrientation()) {
        	
        }
        
    }
	protected void paint(Graphics graphics) {
		if(_orientation != Display.getOrientation()) {
			configSize();
			_orientation = Display.getOrientation();
		}
		//graphics.drawText
		// draw temp and unit
        Font oldFont = this.getFont();
        Font font = oldFont.derive(Font.PLAIN,_titleFontSize);        
        int fontHeight = font.getHeight();
        
        
		// TODO Auto-generated method stub
        int imgWidth = _symbolImage.getWidth();
		int imgHeight = _symbolImage.getHeight();
		
        int xOffset = (this.getPreferredWidth() - imgWidth) >> 1;
		int yOffset = fontHeight; // no need for spacing because the image itself has it
		
        int color = Color.BLACK;
        graphics.setBackgroundColor(color);
        graphics.clear();
        
		
        // draw image
		if(_symbolImage!=null) {
			graphics.drawBitmap(xOffset, yOffset, imgWidth, imgHeight, _symbolImage, 0, 0);
		}

        //this.setFont(font);
        graphics.setFont(font);
        String text = (int)_lowTemp + "\u00b0/" + (int)_highTemp + '\u00b0';
        if(text != null) {
            // for title, color is black if unfocused
            if(this.isFocus()) {
            	graphics.setColor(Color.WHITE);
            } else {
            	graphics.setColor(Color.WHITE);
            }
            
           // String dayStr = "Fri";
            int textXOffset = (this.getPreferredWidth() - font.getAdvance(_dayInfo.dayInWeekStr)) >> 1;
            graphics.drawText(_dayInfo.dayInWeekStr, textXOffset,2,0,this.getWidth());
            //int yOffset = (_details != null)?2:12;
            textXOffset = (this.getPreferredWidth() - font.getAdvance(text)) >> 1;
            graphics.drawText(text, textXOffset, fontHeight + imgHeight,0,this.getWidth());
        }
        this.setFont(oldFont);
        graphics.setFont(oldFont);
	}
	
	private Bitmap getSymbolImage(int symbolId) {
		StringBuffer strBuf = new StringBuffer();
		if(symbolId < 10) {
			strBuf.append('0');
		}
		strBuf.append(symbolId);
		if((symbolId >= 1 && symbolId <= 3) ||(symbolId >= 5 && symbolId <= 8)) {
			strBuf.append('d');
		}
		strBuf.append("_low");
		strBuf.append(".png");
		String fileName = strBuf.toString();
		//fileName = "01n_low.png";
		return Bitmap.getBitmapResource(fileName);
	}

}
