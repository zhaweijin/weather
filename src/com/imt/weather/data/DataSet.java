package com.imt.weather.data;

import java.util.ArrayList;

public class DataSet {

	//city list name
	private ArrayList<City> cities = new ArrayList<City>();     
	//current weather 
	private CurrentWeatherData currentWeatherData=null;

    //furture weather
	private ArrayList<ForecastWeatherData> forecastWeatherDatas = new ArrayList<ForecastWeatherData>();
	public DataSet()
	{
		
	}
	
	//furture
	public ArrayList<ForecastWeatherData> getForecastWeatherDatas()
	{
		return forecastWeatherDatas;
	}
	public ForecastWeatherData getForecastWeatherData()
	{
		return forecastWeatherDatas.get(forecastWeatherDatas.size()-1);
	}
	
	
	//city
	public ArrayList<City> getCitys()
	{
		return cities;
	}
	public City getCity()
	{
		return cities.get(cities.size()-1);
	}
	
 
	//current weather
	public void setCurrentWeatherData(CurrentWeatherData currentWeatherData)
	{
		this.currentWeatherData = currentWeatherData;
	}
	public CurrentWeatherData getCurrentWeatherData()
	{
		return currentWeatherData;
	}
	   

	 
	   
}
