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
	public void refreshCurrentData() {
		// the first tile is the current condition
		// refresh every 3 hours
		for(int i=0;i<_weatherTiles.size();i++) {
			WeatherTileField tile = (WeatherTileField)_weatherTiles.elementAt(i);
			tile.refreshData();
		}
	}
	public void updateWeather(Vector infoList) {
		updateWeather(infoList, _showInCelsiusUnit);
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
