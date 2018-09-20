package com.imt.weather.data;

public class CurrentWeatherData {

	/*
	 * <current 
	 * temperature="32" 
	 * skycode="32" 
	 * skytext="Clear" 
	 * date="2011-05-12" 
	 * day="Thursday" 
	 * shortday="Thu" 
	 * observationtime="15:00:00" 
	 * observationpoint="Shenzhen" 
	 * feelslike="39" 
	 * humidity="66" 
	 * windspeed="8" 
	 * winddisplay="8 ## SSE"/>
	 */
	private String currentTemperature;
    private String humidity;
    private String windspeed;
    private String observationtime;
    private String skytext;
    
    public void setCurrentTemperature(String currentTemperature)
    {
    	this.currentTemperature = currentTemperature;
    }
    public String getCurrentTemperature()
    {
    	return currentTemperature;
    }
    
    public void setHumidity(String humidity)
    {
    	this.humidity = humidity;
    }
    public String getHumidity()
    {
    	return humidity;
    }
    
    public void setWindspeed(String windspeed)
    {
    	this.windspeed = windspeed;
    }
    public String getWindspeed()
    {
    	return windspeed;
    }
    
    public void setObservationtime(String observationtime)
    {
    	this.observationtime = observationtime;
    }
    public String getObservationtime()
    {
    	return observationtime;
    }
    
    
    public void setSkytext(String skytext)
    {
    	this.skytext = skytext;
    }
    public String getSkytext()
    {
    	return skytext;
    }

}
