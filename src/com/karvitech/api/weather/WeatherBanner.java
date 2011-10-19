package com.karvitech.api.weather;

import java.util.Vector;

import net.rim.device.api.ui.container.HorizontalFieldManager;

public class WeatherBanner extends HorizontalFieldManager {
	static final int NUMBER_OF_DAYS = 5;
	Vector _weatherTiles = new Vector(NUMBER_OF_DAYS);
	public boolean _showInCelsiusUnit;
	
	public WeatherBanner( boolean showInCelsiusUnit) {
		_showInCelsiusUnit = showInCelsiusUnit;
		for(int i=0; i<NUMBER_OF_DAYS; i++) {
			WeatherTileField tile = new WeatherTileField();
			_weatherTiles.addElement(tile);
			add(tile);
		}
	}
	public void updateWeather(Vector infoList, boolean showInCelsiusUnit) {
		_showInCelsiusUnit = showInCelsiusUnit;
		if(infoList == null) {
			return;
		}
		
		for(int i=0; i<NUMBER_OF_DAYS;i++) {
			WeatherTileField tile = (WeatherTileField)_weatherTiles.elementAt(i);
			DayWeatherInfo dayInfo = (DayWeatherInfo)infoList.elementAt(i);
			tile.updateWeather(dayInfo);
			tile._showInCelsiusUnit = _showInCelsiusUnit;
		}
	}
}
