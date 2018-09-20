package com.imt.weather.data;

public class ForecastWeatherData {

	/*
	 * <forecast 
	 * low="23" 
	 * high="29" 
	 * skycodeday="4" 
	 * skytextday="T-storms" 
	 * date="2011-05-16" 
	 * day="Monday" 
	 * shortday="Mon" 
	 * precip="95"/>
	 */
	
	
	private String highTemperature;
    private String lowTemperature;
    private String skytextday;
    private String date;
    private String day;
    private String precip;
    
    public void setHighTemperature(String highTemperature)
    {
    	this.highTemperature = highTemperature;
    }
    public String getHighTemperature()
    {
    	return highTemperature;
    }
    
    public void setLowTemperature(String lowTemperature)
    {
    	this.lowTemperature = lowTemperature;
    }
    public String getLowTemperature()
    {
    	return lowTemperature;
    }
    
    public void setSkeytextday(String skytextday)
    {
    	this.skytextday = skytextday;
    }
    public String getSkytextday()
    {
    	return skytextday;
    }
    
    public void setDate(String date)
    {
    	this.date = date;
    }
    public String getDate()
    {
    	return date;
    }
    
    
    public void setDay(String day)
    {
    	this.day = day;
    }
    public String getDay()
    {
    	return day;
    }
    
    public void setPrecip(String precip)
    {
    	this.precip = precip;
    }
    public String getPrecip()
    {
    	return precip;
    }
}
