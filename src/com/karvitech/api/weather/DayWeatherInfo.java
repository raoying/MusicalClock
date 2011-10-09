package com.karvitech.api.weather;


import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
	
	public static Vector parseXML(String url) {
		try {
			Vector forecasts = new Vector();
			DayWeatherInfo dayweatherInfo = new DayWeatherInfo();
			/*URL url = new URL(
					"http://www.androidpeople.com/wp-content/uploads/2010/06/example.xml");
			url = new URL("http://api.yr.no/weatherapi/locationforecast/1.8/?lat=60.10;lon=9.58");*/
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
//			InputStream is = readXmlFromUrl("http://api.yr.no/weatherapi/locationforecast/1.8/?lat=60.10;lon=9.58");
			InputStream is = readXmlFromUrl(url);

			Document doc = db.parse(is);  //db.parse(new InputSource(url.openStream()));
			doc.getDocumentElement().normalize();

			// prepare the day weather info object
			
			// get the list of the time elements
			NodeList nodeList = doc.getElementsByTagName("time");
			String startTimeOfDay = null;
			Vector dayInfoList = new Vector();
			
			int index = 0;
			int totalNodes = nodeList.getLength();
			
			DayWeatherInfo dayWeatherInfo = null;
			while(index < totalNodes) {
				// processing time element

				
				DayWeatherInfo.PointWeatherInfo pointWeatherInfo = null;
				DayWeatherInfo.PeriodWeatherInfo  periodWeatherInfo = null;

				// get the time element
				Node node = nodeList.item(index);
				Element timeElmnt = (Element) node;
				
				String sectionStartTime = timeElmnt.getAttribute("from");
				String endTime = timeElmnt.getAttribute("to");
				
				// create the day weather info object if needed,
				// either when the parsing just started, or encouters a time section 
				// that is not in the current date.
				if(startTimeOfDay == null) {
					dayWeatherInfo =  new DayWeatherInfo();
					forecasts.addElement(dayWeatherInfo);
					startTimeOfDay = sectionStartTime;
				}
				else if(isANewDay(sectionStartTime,startTimeOfDay )) {
						// starting a new day, add the current data into the vector and create a new 
						// object for the new day
						dayInfoList.addElement(dayWeatherInfo);
						dayWeatherInfo =  new DayWeatherInfo();
						startTimeOfDay = sectionStartTime;
											
				}
				// the node sequence is one time element that is the info for a time point
				// followed by 2 time elements for span of 3 and 6 hours, then repeate 
				if(sectionStartTime.equalsIgnoreCase(endTime)) {
					// if the start time and end time are the same, then it is time point data
					pointWeatherInfo = new DayWeatherInfo.PointWeatherInfo();
					constructPointInfo( pointWeatherInfo,timeElmnt);
					dayWeatherInfo.addPointInfo(pointWeatherInfo);
				}
				else {
					// data for a period of 3 or 6 hours
					periodWeatherInfo = new DayWeatherInfo.PeriodWeatherInfo();
					constructPeriodInfo(periodWeatherInfo,timeElmnt);
					dayWeatherInfo.addPeriodInfo(periodWeatherInfo);
				}
				index++;
			}
			return forecasts;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void constructPointInfo(DayWeatherInfo.PointWeatherInfo pointWeatherInfo, Element timeElmnt) {
		NodeList tempList = timeElmnt.getElementsByTagName("temperature");
		Element tempElement = (Element) tempList.item(0);
		
		if(tempElement != null) {
			String tempratureStr = tempElement.getAttribute("value");
			pointWeatherInfo.temprature = Float.parseFloat(tempratureStr);
			pointWeatherInfo.tempratureUnit = tempElement.getAttribute("unit");
		}		
	}
	
	public static void constructPeriodInfo(DayWeatherInfo.PeriodWeatherInfo periodWeatherInfo, Element timeElmnt) {
		NodeList tempList = timeElmnt.getElementsByTagName("symbol");
		Element symbolElement = (Element) tempList.item(0);
		
		if(symbolElement != null) {
			periodWeatherInfo.symbol = symbolElement.getAttribute("id");
			periodWeatherInfo.symbolId = Integer.parseInt(symbolElement.getAttribute("number"));
		}
	}
	
	public static boolean isANewDay(String sectionStartTime, String startTimeOfDay ) {
		Date sectionTime = TimeUtilities.stringToDate(sectionStartTime);
		Date startDayTime = TimeUtilities.stringToDate(startTimeOfDay);
		
		return DateTimeUtilities.isSameDate(sectionTime.getTime(),startDayTime.getTime());
		//return false;
	}

	public static InputStream readXmlFromUrl(String uri) {
        try {

        	HttpConnection	conn = (HttpConnection)Connector.open(uri);
        	int rc = conn.getResponseCode();
        	if (rc != HttpConnection.HTTP_OK)
        	{
        		throw new IOException("HTTP response code: " + rc);
        	}
        	
            InputStream is = conn.openInputStream();
            return is;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
	}
	
	public static String readXmlAssetToString(String fileName) {
		String text = null;
        try {
            InputStream is = null;// = ctx.getAssets().open(fileName);
            
            // We guarantee that the available method returns the total
            // size of the asset...  of course, this does mean that a single
            // asset can't be more than 2 gigs.
            int size = is.available();
            
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            
            // Convert the buffer into a string.
             text = new String(buffer);
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        return text;
	}

}

