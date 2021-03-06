package com.karvitech.api.weather;

import java.util.Calendar;

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
    public static boolean _showInCelsiusUnit;
    
    private boolean _firstTitle;
    
    
    public void refreshData() {
    	_symbolImage = this.getSymbolImage(_dayInfo.getCurrentSymbol());
    	
    }
    public void updateWeather(DayWeatherInfo dayInfo, boolean firstTitle) {
    	_firstTitle = firstTitle;
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
        String text = null;
        
        // if not the current temp, then show the high and low 
        if(!_firstTitle) {
	        if(!_showInCelsiusUnit) {
	        	text = (int)(_lowTemp*9/5+32) + "\u00b0/" + (int)(_highTemp*9/5 + 32) + '\u00b0';
	        }
	        else {
	        	text = (int)_lowTemp + "\u00b0/" + (int)_highTemp + '\u00b0';
	        }
        } 
        else {
        	if(!_showInCelsiusUnit) {
        		text =  (int)(_dayInfo.getCurrentTemprature()*9/5 + 32) +  "\u00b0";
        	}
        	else {
        		text = (int)_dayInfo.getCurrentTemprature() +  "\u00b0";
        	}
        }

        if(text != null) {
        	int textXOffset;
            graphics.setColor(Color.WHITE);
            textXOffset = (this.getPreferredWidth() - font.getAdvance(text)) >> 1;
            graphics.drawText(text, textXOffset, fontHeight + imgHeight,0,this.getWidth());
        }

        // draw day string or "Now"
        if(_firstTitle) {
        	text = "Now";
        }
        else {
        	text = _dayInfo.dayInWeekStr;
        }
        int textXOffset = (this.getPreferredWidth() - font.getAdvance(text)) >> 1;
        graphics.drawText(text, textXOffset,2,0,this.getWidth());

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
			Calendar cal = Calendar.getInstance();
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			if(hour >= 6 && hour < 18) {
				strBuf.append('d');
			}
			else {
				strBuf.append('n');
			}
		}
		strBuf.append("_low");
		strBuf.append(".png");
		String fileName = strBuf.toString();
		//fileName = "01n_low.png";
		return Bitmap.getBitmapResource(fileName);
	}

}
