package com.imt.weather;




import java.util.Calendar;
import java.util.TimeZone;


import com.imt.weather.data.WeatherDatabase;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

public class WeatherWidget extends AppWidgetProvider{

	private WeatherDatabase weatherDatabase=null;
	private Cursor weatherCursor=null;
	//current weather
	private String cityAndCountry="";
	private String current_high_temperature="";
	private String current_low_temperature="";
	private int currentWeatherIcon=0;
	private String current_skyText="";

	

	private String updateTime;
	RemoteViews widget_layout=null;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		//Log.v("receive", "receive");
		if (intent != null)
	    {
	      String str1 = intent.getAction();
			if (str1.equals("com.xuzhi.weather.UPDATE_WIDGET")) {
				Util.print("receive_big", "start");
				AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
				ComponentName componentName = new ComponentName(context, WeatherWidget.class);
				if(widget_layout==null)
				    widget_layout = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
				refreshView(context, widget_layout);
				appWidgetManger.updateAppWidget(componentName, widget_layout);
			}
//			else if(str1.equals("android.intent.action.BOOT_COMPLETED"))
//			{
//				Util.print("reboot", "reboot");
//				startService(context);
//			}
	    }
	}

	  

	
	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		//Log.v("enable", "enable");
		startService(context);
	}



	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		Util.print("update", "widget update");
		
		
		
		RemoteViews widget_layout = new RemoteViews(context.getPackageName(),R.layout.weather_widget);
		
		refreshView(context,widget_layout);
		appWidgetManager.updateAppWidget(appWidgetIds, widget_layout);
	}

	
	public void refreshView(Context context,RemoteViews widget_layout)
	{
		Intent intenttext = new Intent(context,WeatherMain.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intenttext,0);
	    widget_layout.setOnClickPendingIntent(R.id.weather_widget, pIntent);
		
		
		weatherDatabase = Util.getWeatherDatabase(context);
		weatherCursor = weatherDatabase.queryDefaultData();
		weatherCursor.moveToFirst();
		if(weatherCursor.getCount()!=0)
		{
			//current data
			 
			cityAndCountry  = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME));
//			if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
//				cityAndCountry = Util.translateCityAndCuntry(context, cityAndCountry);
			String txtSky = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_SKEYTEXT));
			currentWeatherIcon = Util.getIconID(context, txtSky);
			
			current_high_temperature = Util.FashiToSheshi(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1)))+"℃"; 
			                                 
			current_low_temperature = Util.FashiToSheshi(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1)))+"℃";
//			if(Util.getLocaleLanguage().compareToIgnoreCase("zh-cn")==0)
//			    current_skyText = Util.translateWeatherDetail(context, txtSky);
//			else 
				current_skyText = txtSky;
			
			
			
			//update time
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
	        String updateTime =    Util.translateNetworkTimeToLocalTime(context, newworkTimeString);
			
			//updateTime = updateTime + " " +time;
			Util.print("date", "date." + updateTime);
			String day = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_DAY));
			Util.print("day", "day." + day);
		    
			
			
			
			//set current data
			widget_layout.setImageViewResource(R.id.weather_icon, currentWeatherIcon);
			widget_layout.setTextViewText(R.id.week, day);
			widget_layout.setTextViewText(R.id.high_temperture, current_high_temperature );
			widget_layout.setTextViewText(R.id.low_temperture, "  ~ "+ current_low_temperature);
			widget_layout.setTextViewText(R.id.detail, current_skyText);
			widget_layout.setTextViewText(R.id.city, cityAndCountry);
			
			
			//update time
			//context.getResources().getString(R.string.networ_lasttime_update)
			widget_layout.setTextViewText(R.id.time,  updateTime);
			
		}
		else {
			widget_layout.setImageViewResource(R.id.weather_icon, currentWeatherIcon);
			widget_layout.setTextViewText(R.id.week, "");
			widget_layout.setTextViewText(R.id.high_temperture, "");
			widget_layout.setTextViewText(R.id.low_temperture, "");
			widget_layout.setTextViewText(R.id.detail, "");
			widget_layout.setTextViewText(R.id.city, "");
			
			
			//update time
			//context.getResources().getString(R.string.networ_lasttime_update)
			widget_layout.setTextViewText(R.id.time,  updateTime);
		}
		weatherCursor.close();
		
	    
	}

//	@Override
//	public void onDisabled(Context context) {
//		// TODO Auto-generated method stub
//		super.onDisabled(context);
//		if(!weatherCursor.isClosed())
//			weatherCursor.close();
//	}
	

	public void startService(Context context)
	{
		Intent intent = new Intent(context, RemoteWeatherService.class);
		context.startService(intent);
		
	}
	
	

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
		WeatherService.ThreadStop=true;
		
		Intent intent = new Intent(context, RemoteWeatherService.class);
		context.stopService(intent);
	}
	
}
