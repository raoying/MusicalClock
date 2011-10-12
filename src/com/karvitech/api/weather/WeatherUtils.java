package com.karvitech.api.weather;

import java.io.InputStream;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import net.rim.device.api.util.DateTimeUtilities;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.karvitech.api.appTools.TimeUtilities;

public class WeatherUtils {
	public static final String YR_NO_FORECAST_URL = "http://api.yr.no/weatherapi/locationforecast/1.8/?";
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
			//Vector dayInfoList = new Vector();
			
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
					dayWeatherInfo.dateStr = startTimeOfDay;
				}
				else if(sectionStartTime.equalsIgnoreCase(endTime) && isANewDay(sectionStartTime,startTimeOfDay )) {
					// only check if it is a new day for point data, period data in the beginning of
					// new day has 6 hours of data from last day
					// starting a new day, add the current data into the vector and create a new 
					// object for the new day
					forecasts.addElement(dayWeatherInfo);
					dayWeatherInfo =  new DayWeatherInfo();
					startTimeOfDay = sectionStartTime;
					dayWeatherInfo.dateStr = startTimeOfDay;
											
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
		
		if(DateTimeUtilities.isSameDate(sectionTime.getTime(),startDayTime.getTime(), TimeZone.getDefault(), TimeZone.getDefault())) {
			/*Calendar cal = Calendar.getInstance();
			cal.setTime(sectionTime);
			int[] fields = DateTimeUtilities.getCalendarFields(cal, null);
			int firstDate = fields[2];
			cal.setTime(startDayTime);
			fields = DateTimeUtilities.getCalendarFields(cal, null);
			int secondDate = fields[2];*/
			return false;
		}
		return true;
		//return !DateTimeUtilities.isSameDate(sectionTime.getTime(),startDayTime.getTime());
		//return false;
	}

	public static InputStream readXmlFromUrl(String uri) {
        try {
        	InputStream is;
            Class classs = Class.forName(uri);
            
            
            //to actually retrieve the resource prefix the name of the file with a "/"
            is = classs.getResourceAsStream("/weather_data.xml");
/*
        	HttpConnection	conn = (HttpConnection)Connector.open(uri);
        	int rc = conn.getResponseCode();
        	if (rc != HttpConnection.HTTP_OK)
        	{
        		throw new IOException("HTTP response code: " + rc);
        	}
        	
            is = conn.openInputStream();*/
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
