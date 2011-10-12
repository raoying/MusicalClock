package com.karvitech.api.weather;

import java.util.Vector;

public class UpdateWeatherRunnable implements Runnable{
	private String mUrl;
	private WeatherInfoListener mListener;
	
	public UpdateWeatherRunnable(float lat, float longi, WeatherInfoListener listener) {
		mUrl = WeatherUtils.YR_NO_FORECAST_URL + "lat=" + lat + ";lon=" + longi;
		mListener = listener;
	}
	public void run() {
		 Vector infoList = WeatherUtils.parseXML(mUrl);	
		 if(mListener != null) {
			 mListener.WeatherInfoChanged(infoList);
		 }
	}

}
