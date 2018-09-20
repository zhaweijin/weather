package com.imt.weather.data;

public class CityAdapterData {

	private String lowTemperature;
	private String highTemperature;
	private String weatherState;
	private boolean isAdd;
	private String cityName;
    // 2013.05.05 zsc add
	private String cityCode;
	private int is_default_city;
	
	public CityAdapterData() {
		;
	}
	
	public String getLowTemperature() {
		return lowTemperature;
	}
	public void setLowTemperature(String lowTemperature) {
		this.lowTemperature = lowTemperature;
	}
	public String getHighTemperature() {
		return highTemperature;
	}
	public void setHighTemperature(String highTemperature) {
		this.highTemperature = highTemperature;
	}
	public String getWeatherState() {
		return weatherState;
	}
	public void setWeatherState(String weatherState) {
		this.weatherState = weatherState;
	}
	public boolean isAdd() {
		return isAdd;
	}
	public void setAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
   // 2013.05.05 zsc add
	//{+++
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public int getDefaultCity() {
		return is_default_city;
	}
	public void setDefaultCity(int is_default_city) {
		this.is_default_city = is_default_city;
	}
	//}---
	
}
