package com.imt.weather.view;


import java.util.Calendar;
import java.util.TimeZone;


import com.imt.weather.R;
import com.imt.weather.Util;
import com.imt.weather.data.WeatherDatabase;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherForecast extends RelativeLayout{

	private int row_id=0;
	private WeatherDatabase weatherDatabase=null;
	private Cursor weatherCursor=null;
	private Context context;
	
	
	//current
	
	public WeatherForecast(Context paramContext,int row_id)
	{
		super(paramContext);
		this.context = paramContext;
		RelativeLayout relativeLayout = (RelativeLayout)LayoutInflater.from(
				     paramContext).inflate(R.layout.weather_forecast, null);
		addView(relativeLayout);
		this.row_id = row_id;    
		Util.print("row_id", ""+row_id);
		weatherDatabase = Util.getWeatherDatabase(paramContext);
		weatherCursor = weatherDatabase.queryAllData();
		weatherCursor.move(row_id+1);
		init();
		
		if(!weatherCursor.isClosed())
			weatherCursor.close();
	}
	
	public void init()
	{
		//current
        ImageView imgCurrentSky = (ImageView)findViewById(R.id.imgCurrentSky);
        TextView txtCurrentSky = (TextView)findViewById(R.id.txtCurrentSky);
        TextView txtCurrentTemp = (TextView)findViewById(R.id.txtCurrentTemp);
        TextView txtTemp = (TextView)findViewById(R.id.txtTemp);
//        TextView txtOther = (TextView)findViewById(R.id.txtOther);
        TextView txtCity = (TextView)findViewById(R.id.txtCity);
        //---------------------
        String currentSkyType = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_SKEYTEXT));
        
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
        	txtCurrentSky.setText(Util.translateWeatherDetail(context, currentSkyType));
        else
        	txtCurrentSky.setText(currentSkyType);
       
        imgCurrentSky.setBackgroundResource(Util.getIconID(context, currentSkyType));
        //Util.print("currentSkyType", currentSkyType);
        String currentTemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_TEMPERATURE));
        txtCurrentTemp.setText(currentTemperature+"℃");
        String temprature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1))+"℃" +
                            " ~ " +
                            weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1))+"℃";
        txtTemp.setText(temprature);
        String cityname = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME));
       
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
        	txtCity.setText(Util.translateCityAndCuntry(context, cityname));
        else
        	txtCity.setText(cityname);
        //furture weather 1
        TextView txtDay1 = (TextView)findViewById(R.id.txtDay1);
        ImageView imgDay1 = (ImageView)findViewById(R.id.imgDay1);
        TextView txtTempHigh1 = (TextView)findViewById(R.id.txtTempHigh1);
        TextView txtTempLow1 = (TextView)findViewById(R.id.txtTempLow1);
        //---------------------
        String day1 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA1));
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            txtDay1.setText(Util.translateWeek(context, day1));
        else
        	txtDay1.setText(day1);
        String txtSkeytext1 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA1));
        imgDay1.setBackgroundResource(Util.getIconID(context, txtSkeytext1));
        //Util.print("txtSkeytext1", txtSkeytext1);
        txtTempHigh1.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1))+"℃");
        txtTempLow1.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1))+"℃");
        
        //furture weather 2
        TextView txtDay2 = (TextView)findViewById(R.id.txtDay2);
        ImageView imgDay2 = (ImageView)findViewById(R.id.imgDay2);
        TextView txtTempHigh2 = (TextView)findViewById(R.id.txtTempHigh2);
        TextView txtTempLow2 = (TextView)findViewById(R.id.txtTempLow2);
        //----------
        String day2 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA2));
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            txtDay2.setText(Util.translateWeek(context, day2));
        else
        	txtDay2.setText(day2);
        String txtSkeytext2 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA2));
        imgDay2.setBackgroundResource(Util.getIconID(context, txtSkeytext2));
        //Util.print("txtSkeytext2", txtSkeytext2);
        txtTempHigh2.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA2))+"℃");
        txtTempLow2.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA2))+"℃");
        
        //furture weather 3
        TextView txtDay3 = (TextView)findViewById(R.id.txtDay3);
        ImageView imgDay3 = (ImageView)findViewById(R.id.imgDay3);
        TextView txtTempHigh3 = (TextView)findViewById(R.id.txtTempHigh3);
        TextView txtTempLow3 = (TextView)findViewById(R.id.txtTempLow3);
        //----------------
        String day3 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA3));
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            txtDay3.setText(Util.translateWeek(context, day3));
        else
        	txtDay3.setText(day3);
        String txtSkeytext3 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA3));
        imgDay3.setBackgroundResource(Util.getIconID(context, txtSkeytext3));
        //Util.print("txtSkeytext3", txtSkeytext3);
        txtTempHigh3.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA3))+"℃");
        txtTempLow3.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA3))+"℃");
        //furture weather 4
        TextView txtDay4 = (TextView)findViewById(R.id.txtDay4);
        ImageView imgDay4 = (ImageView)findViewById(R.id.imgDay4);
        TextView txtTempHigh4 = (TextView)findViewById(R.id.txtTempHigh4);
        TextView txtTempLow4 = (TextView)findViewById(R.id.txtTempLow4);
        //------------
        String day4 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA4));
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            txtDay4.setText(Util.translateWeek(context, day4));
        else
        	txtDay4.setText(day4);
        String txtSkeytext4 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA4));
        imgDay4.setBackgroundResource(Util.getIconID(context, txtSkeytext4));
        //Util.print("txtSkeytext4", txtSkeytext4);
        txtTempHigh4.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA4))+"℃");
        txtTempLow4.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA4))+"℃");
        //furture weather 5
        TextView txtDay5 = (TextView)findViewById(R.id.txtDay5);
        ImageView imgDay5 = (ImageView)findViewById(R.id.imgDay5);
        TextView txtTempHigh5 = (TextView)findViewById(R.id.txtTempHigh5);
        TextView txtTempLow5 = (TextView)findViewById(R.id.txtTempLow5);
        //-----------
        String day5 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA5));
        
        if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            txtDay5.setText(Util.translateWeek(context, day5));
        else
        	txtDay5.setText(day5);
        String txtSkeytext5 = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA5));
        imgDay5.setBackgroundResource(Util.getIconID(context, txtSkeytext5));
        //Util.print("txtSkeytext5", txtSkeytext5);
        txtTempHigh5.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA5))+"℃");
        txtTempLow5.setText(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA5))+"℃");
        
        
        
        //last update time
        TextView txtLastupdateTextView = (TextView)findViewById(R.id.txtLastupdate);
        
        String timeZoneString = "GMT+08:00";
        java.text.DateFormat df;
        if(Util.is24HourFormat(context))
        {
        	df = new java.text.SimpleDateFormat("HH:mm:ss"); 
        }
        else {
        	df = new java.text.SimpleDateFormat("hh:mm:ss:a"); 
		}
    	Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timeZoneString));   
    	cal.setTimeInMillis(System.currentTimeMillis());
    	String time = df.format(cal.getTime());
        
        String newworkTimeString = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_DATE));
        String updateTime =    Util.translateNetworkTimeToLocalTime(context, newworkTimeString)
                               + " " +time;
        txtLastupdateTextView.setText(getResources().getString(R.string.networ_lasttime_update) + " " +updateTime);
        
        
        
        //onclick event
        ImageView layOut0 = (ImageView)findViewById(R.id.imgCurrentSky);
        
        layOut0.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sameEvent();
			}
		});
        
        
        ImageView layOut1 = (ImageView)findViewById(R.id.imgDay1);
        
        layOut1.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sameEvent();
			}
		});
        
        ImageView layOut2 = (ImageView)findViewById(R.id.imgDay2);
        
        layOut2.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TwoOnclickEvent();
			}
		});
        
        
        ImageView layOut3 = (ImageView)findViewById(R.id.imgDay3);
        
        layOut3.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ThreeOnclickEvent();
			}
		});
        
        
        ImageView layOut4 = (ImageView)findViewById(R.id.imgDay4);
        
        layOut4.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FourOnclickEvent();
			}
		});
        
        ImageView layOut5 = (ImageView)findViewById(R.id.imgDay5);
        
        layOut5.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FiveOnclickEvent();
			}
		});
        
	}
	
	public void sameEvent()
	{
		if(weatherCursor.isClosed())
		{
			weatherCursor = weatherDatabase.queryAllData();
			weatherCursor.move(row_id+1);
		}
		
		String date = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_DATE));
		String day = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_DAY));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            day=Util.translateWeek(context, day);
		String title =  Util.translateNetworkTimeToLocalTime(context,date) + " " + day;
		//firstfloor
		String txtCurrentSky = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_SKEYTEXT));
		String temperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_TEMPERATURE));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
			txtCurrentSky=Util.translateWeatherDetail(context, txtCurrentSky);
		String firstfloor= txtCurrentSky +  " " + temperature +"℃";                
		
		//secondfloor
		String hightemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1));
		String lowtemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1));
		String secondfloor = getResources().getString(R.string.Low) + " " +hightemperature +"℃" + 
		                       " " + getResources().getString(R.string.High)+ " " +lowtemperature +"℃";
		
		//thridfloor
		String humidity = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_HUMIDITY));
		String percip = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_PRECIP_DATA1));
		String thridfloor = getResources().getString(R.string.Humidity) + " "+ humidity + "%" +
		                    " " + getResources().getString(R.string.PrecipitationChance) +" " +percip +"%";
		
		//fourthfloor
		String WindSpeed = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_WINDSPEED));
		String fourthfloor = getResources().getString(R.string.WindSpeed) + " "+ WindSpeed + " " +"Km/hr S";
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(firstfloor +"\n" +secondfloor +"\n" + thridfloor +"\n" +fourthfloor +"\n")
		.show();
		
		weatherCursor.close();
	}
	
	public void TwoOnclickEvent()
	{
		
		if(weatherCursor.isClosed())
		{
			weatherCursor = weatherDatabase.queryAllData();
			weatherCursor.move(row_id+1);
		}
		
		
		String date = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DATE_DATA2));
		String day = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA2));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            day=Util.translateWeek(context, day);
		String title =  Util.translateNetworkTimeToLocalTime(context,date) + " " + day;
		//firstfloor
		String txtCurrentSky = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA2));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
			txtCurrentSky=Util.translateWeatherDetail(context, txtCurrentSky);
		String firstfloor= txtCurrentSky;
		
		//secondfloor
		String hightemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA2));
		String lowtemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA2));
		String secondfloor = getResources().getString(R.string.Low) + " " +hightemperature  + "℃" +
		                       " " + getResources().getString(R.string.High) + " "+ lowtemperature + "℃";
		
		//thridfloor 
		String percip = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_PRECIP_DATA2));
		String thridfloor = getResources().getString(R.string.PrecipitationChance) + " "+percip +"%";
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(firstfloor +"\n" +secondfloor +"\n" + thridfloor +"\n" )
		.show();
		
		weatherCursor.close();
	}
	
	public void ThreeOnclickEvent()
	{
		if(weatherCursor.isClosed())
		{
			weatherCursor = weatherDatabase.queryAllData();
			weatherCursor.move(row_id+1);
		}
		
		String date = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DATE_DATA3));
		String day = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA3));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            day=Util.translateWeek(context, day);
		String title =  Util.translateNetworkTimeToLocalTime(context,date) + " " + day;
		//firstfloor
		String txtCurrentSky = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA3));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
			txtCurrentSky=Util.translateWeatherDetail(context, txtCurrentSky);
		String firstfloor= txtCurrentSky;
		
		//secondfloor
		String hightemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA3));
		String lowtemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA3));
		String secondfloor = getResources().getString(R.string.Low) + " " +hightemperature + "℃" +
		                       " " + getResources().getString(R.string.High) +" " +lowtemperature + "℃";
		
		//thridfloor 
		String percip = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_PRECIP_DATA3));
		String thridfloor = getResources().getString(R.string.PrecipitationChance) + " "+ percip +"%";
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(firstfloor +"\n" +secondfloor +"\n" + thridfloor +"\n" )
		.show();
		
		weatherCursor.close();
	}
	
	public void FourOnclickEvent()
	{
		
		if(weatherCursor.isClosed())
		{
			weatherCursor = weatherDatabase.queryAllData();
			weatherCursor.move(row_id+1);
		}
		
		
		String date = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DATE_DATA4));
		String day = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA4));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            day=Util.translateWeek(context, day);
		String title =  Util.translateNetworkTimeToLocalTime(context,date) + " " + day;
		//firstfloor
		String txtCurrentSky = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA4));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
			txtCurrentSky=Util.translateWeatherDetail(context, txtCurrentSky);
		String firstfloor= txtCurrentSky;
		
		//secondfloor
		String hightemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA4));
		String lowtemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA4));
		String secondfloor = getResources().getString(R.string.Low) + " " +hightemperature + "℃" +
		                       " " + getResources().getString(R.string.High) + " " +lowtemperature + "℃";
		
		//thridfloor 
		String percip = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_PRECIP_DATA4));
		String thridfloor = getResources().getString(R.string.PrecipitationChance) +" "+ percip +"%";
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(firstfloor +"\n" +secondfloor +"\n" + thridfloor +"\n" )
		.show();
		
		weatherCursor.close();
	}
	
	
	public void FiveOnclickEvent()
	{
		if(weatherCursor.isClosed())
		{
			weatherCursor = weatherDatabase.queryAllData();
			weatherCursor.move(row_id+1);
		}
		
		String date = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DATE_DATA5));
		String day = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA5));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
            day=Util.translateWeek(context, day);
		String title =  Util.translateNetworkTimeToLocalTime(context,date) + " " + day;
		//firstfloor
		String txtCurrentSky = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA5));
		if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
			txtCurrentSky=Util.translateWeatherDetail(context, txtCurrentSky);
		String firstfloor= txtCurrentSky;
		
		//secondfloor
		String hightemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA5));
		String lowtemperature = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA5));
		String secondfloor = getResources().getString(R.string.Low) +" " +hightemperature + "℃" +
		                       " " + getResources().getString(R.string.High) + " "+ lowtemperature + "℃";
		
		//thridfloor 
		String percip = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_PRECIP_DATA5));
		String thridfloor = getResources().getString(R.string.PrecipitationChance) +" " + percip +"%";
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(firstfloor +"\n" +secondfloor +"\n" + thridfloor +"\n" )
		.show();
		
		weatherCursor.close();
	}
}
