package com.imt.weather;
import com.imt.weather.WeatherInfo;
import com.imt.weather.IShowWeatherInfo;

interface IRemoteWeatherService {
	void updateWeatherInfo(in WeatherInfo weatherInfo);
	void setDefaultCity(String cityCode, boolean firstRun);
	void registerCallback(IShowWeatherInfo callback);
	void unregisterCallback(IShowWeatherInfo callback);
}
