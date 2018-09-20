package com.imt.weather;


import com.imt.weather.data.DataSet;
import com.imt.weather.data.WeatherDatabase;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

public class WeatherService extends Service
{
	public static  boolean ThreadStop = false;
	private WeatherDatabase weatherDatabase=null;
	private Cursor weatherCursor=null;
	
	private long sleeptime;
	private SharedPreferences preferences;
	private DataSet dataSet=null;
	
	WorldClockTimeChangeReceiver worldtimeBroadcastReceiver;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub		
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		//Log.v("sss", "ss");
		worldtimeBroadcastReceiver = getInstance();
		registerTimerBroadcasts(WeatherService.this);
		
		
		weatherDatabase = Util.getWeatherDatabase(this);
		preferences = getSharedPreferences("weather_setting", Context.MODE_PRIVATE);
		sleeptime = ApplicationConstants.updateSpinnerValue[preferences.getInt("update_frequency", 0)];
		//Log.v("weather_time", ""+sleeptime);
		Util.print("sleeptime", ""+sleeptime);
		new Thread(new Runnable() {
			public void run() {
				
				while (!ThreadStop) {
					try {
						Thread.sleep(sleeptime);
						
						updateWeatherData();
						
						//Log.v("update weather data", "update weather data");
						Intent localIntent1 = new Intent("com.xuzhi.weather.UPDATE_WIDGET");
						sendBroadcast(localIntent1);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
				Util.print("time thread stop", "time thread stop");
			}
		}).start();
		super.onStart(intent, startId);
	}

	
	public void updateWeatherData()
	{
		try {
			if(Util.getNetworkState(this))
			{
				String cityName="";
				String cityCode="";
				weatherCursor = weatherDatabase.queryAllData();
				if(weatherCursor.getCount()!=0)
				{
					while(weatherCursor.moveToNext())
					{
						cityName = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME));
						cityCode = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_CODE));

						Util.print("service_search_path", ApplicationConstants.searchWeatherData+cityCode+"&weadegreetype=%s");
						dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+ cityCode+"&weadegreetype=%s");
						if(dataSet!=null)
						{
							Util.print("weather_data_size", dataSet.getForecastWeatherDatas().size()+"");
							for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
							{
								Util.print("weather_getHighTemperature", dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
							}
							Util.updateData(this, dataSet, cityName, cityCode);
						}
					}
				}
				weatherCursor.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			weatherCursor.close();
		}
		
	}
    
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		WeatherService.this.unregisterReceiver(worldtimeBroadcastReceiver);
	}
	
	public  WorldClockTimeChangeReceiver getInstance()
	  {
	    if (worldtimeBroadcastReceiver == null)
	    	worldtimeBroadcastReceiver = new WorldClockTimeChangeReceiver();
	    return worldtimeBroadcastReceiver;
	  }

	 public void registerTimerBroadcasts(Context paramContext)
	  {
	    IntentFilter localIntentFilter = new IntentFilter();
	    localIntentFilter.addAction("android.intent.action.TIME_SET");
	    localIntentFilter.addAction("Intent.ACTION_TIME_CHANGED");
	    paramContext.registerReceiver(worldtimeBroadcastReceiver, localIntentFilter);
	  }
	 
	 
		class WorldClockTimeChangeReceiver extends BroadcastReceiver
		{
		  public void onReceive(Context paramContext, Intent paramIntent)
		  {
		    String str = paramIntent.getAction();
		    if ( ("android.intent.action.TIME_SET".equals(str)) || 
		    		(str.equals(Intent.ACTION_TIME_CHANGED))
		    	)
		    {
		    	Intent localIntent1 = new Intent("com.xuzhi.weather.UPDATE_WIDGET");
				sendBroadcast(localIntent1);
		    }
		  }
		}
}