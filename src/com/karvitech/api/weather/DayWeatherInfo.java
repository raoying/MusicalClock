package com.karvitech.api.weather;


import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.karvitech.api.appTools.TimeUtilities;

public class DayWeatherInfo {
	// Symbols
	public static final int SUN = 1;
	public static final int LIGHT_CLOUD = 2;
	public static final int PARTLY_CLOUD = 3;
	public static final int CLOUD = 4;
	public static final int LIGHT_RAIN_SUN = 5;
	public static final int LIGHT_RAIN_THUNDER_SUN = 6;
	public static final int SLEET_SUN = 7;
	public static final int SNOW_SUN = 8;
	public static final int LIGHT_RAIN = 9;
	public static final int RAIN = 10;
	public static final int RAIN_THUNDER = 11;
	public static final int SLEET = 12;
	public static final int SNOW = 13;
	public static final int SNOW_THUNDER = 14;
	public static final int FOG = 15;
	
	private static final int PERIOD_NUM = 8;
	private static final int POINT_NUM = 4;
	
	// the vector holds the info for every 3 hours
	Vector periodWeatherInfo = new Vector(PERIOD_NUM);
	
	// 4 points, every 6 hours
	Vector pointWeatherInfo = new Vector(POINT_NUM);
	
	String dateStr; // in utc
	String tempUnit; // C or F
	private float _highTemp = Float.MIN_VALUE;
	private float _lowTemp = Float.MAX_VALUE;

	public float getHighTemp() {
		
		for(int i=0;i<pointWeatherInfo.size();i++) {
			PointWeatherInfo pointData = (PointWeatherInfo)pointWeatherInfo.elementAt(i); 
			if(_highTemp < pointData.temprature) {
				_highTemp = pointData.temprature;
			}
		}
		return _highTemp;
	}
	
	public float getLowTemp() {
		for(int i=0;i<pointWeatherInfo.size();i++) {
			PointWeatherInfo pointData = (PointWeatherInfo)pointWeatherInfo.elementAt(i); 
			if(_lowTemp > pointData.temprature) {
				_lowTemp = pointData.temprature;
			}
		}
		return _lowTemp;	
	}

	public int getCurrentSymbol() {
		PeriodWeatherInfo periodInfo = (PeriodWeatherInfo)periodWeatherInfo.elementAt(0);
		return periodInfo.symbolId;
	}
	public void clear() {
		periodWeatherInfo.removeAllElements();
	}
	
	public void addPointInfo(PointWeatherInfo info) {
		//return pointWeatherInfo.add(info);
		pointWeatherInfo.addElement(info);
	}

	public void addPeriodInfo(PeriodWeatherInfo info) {
		//return periodWeatherInfo.add(info);
		periodWeatherInfo.addElement(info);
	}

	public static class PeriodWeatherInfo  {
		public String startTime;
		public int timeSpan; // 3 or 6 hours
		public String symbol;
		public int symbolId;
		public int preceipation;
		
	}
	
	public static class PointWeatherInfo { // every 6 hours
		public String startTime;
		public float temprature; // this is the only field currently we care
		public String tempratureUnit;
		public float  chanceOfRain;
		public WindInfo windInfo;
		public CloudInfo cloundInfo;
		public PrecipitationInfo rainInfo;
	}
	
	private class WindInfo {
		String windspeedId; // looks always ff
		float windmps;
		float beaufort; // strength ?
		String description; // only in Norweaigan
		
	}
	
	private class CloudInfo {
		String cloudiness; // looks like the value is always NN
		float  percent;
	}
	
	private class PrecipitationInfo {
		String unit; // mm
		float  value; // rain fall number in unit
	}
	
}

