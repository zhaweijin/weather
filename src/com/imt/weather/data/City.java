package com.imt.weather.data;

public class City {

	/*
	 * <weather 
weatherlocationcode="wc:CHXX0120" 
weatherlocationname="Shenzhen, CHN" 
zipcode="" 
weatherfullname="Shenzhen, Guangdong, China" 
searchlocation="Shenzhen, Guangdong, China" 
searchdistance="0" 
searchscore="0.95" 
url="http://local.msn.com/worldweather.aspx?eid=8074732&q=Shenzhen-CHN" 
imagerelativeurl="http://blst.msn.com/as/wea3/i/en-us/" 
degreetype="#%S#" 
provider="Foreca" 
isregion="False" 
region="" 
alert="" 
searchresult="Shenzhen, Guangdong, China" 
lat="22.5439887519217" 
lon="114.101445141892" 
entityid="8074732">
<current temperature="32" skycode="30" skytext="Partly Cloudy"/>
</weather>
	 */
	
	
	private String weatherlocationname;
    private String weatherlocationcode;	
    
    public void setWeatherLocationName(String name)
    {
    	this.weatherlocationname = name;
    }
    public String getWeatherLocationName()
    {
    	return weatherlocationname;
    }
    
    public void setWeatherLocationCode(String code)
    {
    	this.weatherlocationcode = code;
    }
    public String getWeatherLocationCode()
    {
    	return weatherlocationcode;
    }
}
