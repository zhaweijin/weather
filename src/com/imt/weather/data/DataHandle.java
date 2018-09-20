package com.imt.weather.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.imt.weather.Util;

public class DataHandle extends DefaultHandler{

	private DataSet dataSet;
	public void startDocument()throws SAXException
	{
		dataSet = new DataSet();
	}
	
	public DataSet getDataSet()
	{
		return this.dataSet;
	}
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		try {
			
			/**
			  * @fucntion  city name list
			  */
			if(localName.equals("weather"))
			{			
				dataSet.getCitys().add(new City());
				dataSet.getCity().setWeatherLocationName(attributes.getValue("weatherlocationname"));
				dataSet.getCity().setWeatherLocationCode(attributes.getValue("weatherlocationcode"));
			}
			/**
			  * @fucntion  furture weather
			  */
			else if(localName.equals("forecast"))
			{
				dataSet.getForecastWeatherDatas().add(new ForecastWeatherData());
				dataSet.getForecastWeatherData().setHighTemperature(attributes.getValue("high"));
				dataSet.getForecastWeatherData().setLowTemperature(attributes.getValue("low"));
				dataSet.getForecastWeatherData().setSkeytextday(attributes.getValue("skytextday"));
				dataSet.getForecastWeatherData().setDate(attributes.getValue("date"));
				dataSet.getForecastWeatherData().setDay(attributes.getValue("day"));
				dataSet.getForecastWeatherData().setPrecip(attributes.getValue("precip"));
				
			}
			/**
			  * @fucntion  current weather
			  */
			else if(localName.equals("current"))
			{
				dataSet.setCurrentWeatherData(new CurrentWeatherData());
				dataSet.getCurrentWeatherData().setCurrentTemperature(attributes.getValue("temperature"));
				dataSet.getCurrentWeatherData().setSkytext(attributes.getValue("skytext"));
				dataSet.getCurrentWeatherData().setObservationtime(attributes.getValue("observationtime"));
				dataSet.getCurrentWeatherData().setHumidity(attributes.getValue("humidity"));
				dataSet.getCurrentWeatherData().setWindspeed(attributes.getValue("windspeed"));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Util.print("web data handle error", "web data handle error");
		}

	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}

	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
}
