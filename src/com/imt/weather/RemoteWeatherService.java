package com.imt.weather;


import java.net.URLEncoder;

import com.imt.weather.data.City;
import com.imt.weather.data.DataSet;
import com.imt.weather.data.WeatherDatabase;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

public class RemoteWeatherService extends Service
{
	private static final String TAG = "RemoteWeatherService";
	private WeatherDatabase weatherDatabase=null;
	private Cursor weatherCursor=null;
	
	private long sleeptime;
	private SharedPreferences preferences;
	private DataSet dataSet=null;
	private RemoteCallbackList<IShowWeatherInfo> mCallbacks = new RemoteCallbackList<IShowWeatherInfo>();
	private WeatherInfo mWeatherInfo;
	private Context mContext;
	private String mDefaultCity;
	private boolean mFirstRun;
	private BroadcastReceiver mNetworkReceiver;
	
	// update weather info after update database weather info
	public static final int MSG_UPDATE_WEATHER_INFO = 0xff00;
	// update database weather info from network
	public static final int MSG_UPDATE_DB_WEATHER_INFO = 0xff01;
	public static final int MSG_NETWORK_CONNECTIVITY_CHANGE = 0xff02;
	public static final int MSG_NETWORK_ACTIVE = 0xff03;
	public static final int MSG_NETWORK_UN_ACTIVE = 0xff04;
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
//			Util.print(TAG, "" + this);
			updateWeatherData();			
		}
	};
    private static final HandlerThread sWorkerThread = new HandlerThread("imt-weather");
    static {
        sWorkerThread.start();
    }
    private static final Handler sWorker = new Handler(sWorkerThread.getLooper());
	private static final boolean DEBUG = true;
	
    private class MyBinder extends IRemoteWeatherService.Stub {
    	
		@Override
		public void updateWeatherInfo(WeatherInfo weatherInfo)
				throws RemoteException {
			mWeatherInfo = weatherInfo;
			mHandler.removeMessages(MSG_UPDATE_WEATHER_INFO);
			mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER_INFO);
		}

		@Override
		public void setDefaultCity(String cityCode, boolean firstRun)
				throws RemoteException {
			Util.print(TAG, "in cityCode." + cityCode + " firstRun." + firstRun);
			mDefaultCity = cityCode;
			mFirstRun = firstRun;
			sWorker.post(mRunnable);
		}

		@Override
		public void registerCallback(IShowWeatherInfo callback)
				throws RemoteException {
			if(null != callback) mCallbacks.register(callback);			
		}

		@Override
		public void unregisterCallback(IShowWeatherInfo callback)
				throws RemoteException {
			if(null != callback) mCallbacks.unregister(callback);			
		}
		
    }
    
    private Handler mHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {
//        	Util.print(TAG, " msg.what." + msg.what);
        	switch (msg.what) {        	
			case MSG_UPDATE_WEATHER_INFO:{
				callBack(mWeatherInfo);
				ApplicationConstants.updateWidget(mContext);
				mHandler.removeMessages(MSG_UPDATE_DB_WEATHER_INFO);
	            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_DB_WEATHER_INFO, sleeptime);
				break;
			}
			case MSG_UPDATE_DB_WEATHER_INFO:
			case MSG_NETWORK_ACTIVE:{
				sWorker.removeCallbacks(mRunnable);
	            sWorker.post(mRunnable);
				break;
			}
			case MSG_NETWORK_CONNECTIVITY_CHANGE:{
        	   	getNetworkConnState();
        	   	break;
			}			
			case MSG_NETWORK_UN_ACTIVE:{	        	   
				break;
			}
			default:
				break;
			}
            
            super.handleMessage(msg);  
        }  
    };
	
	@Override
	public IBinder onBind(Intent intent) {
		Util.print(TAG, "onBind." + intent);
		return new MyBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		Util.print(TAG, "onCreate()");
		weatherDatabase = Util.getWeatherDatabase(this);
		preferences = getSharedPreferences("weather_setting", Context.MODE_PRIVATE);
		sleeptime = ApplicationConstants.updateSpinnerValue[preferences.getInt("update_frequency", 0)];
		sleeptime = 10*60*1000; //default 10分钟刷新数据一次
		registerMyReceiver();
		mHandler.removeMessages(MSG_UPDATE_DB_WEATHER_INFO);
        mHandler.sendEmptyMessage(MSG_UPDATE_DB_WEATHER_INFO);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {		
		super.onStart(intent, startId);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {			
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void callBack(WeatherInfo weatherInfo){
		int n = mCallbacks.beginBroadcast();  
        Util.print(TAG, "mCallbacks.beginBroadcast() is " + n + weatherInfo.toString());  
        try {  
            for (int i = 0; i < n; i++) {
                mCallbacks.getBroadcastItem(i).show(weatherInfo);  
            }  
        } catch (RemoteException e) {  
            e.printStackTrace();
        }  
        mCallbacks.finishBroadcast();
	}

	
	public void updateWeatherData()
	{
		try {
			if(Util.getNetworkState(this))
			{
				String cityName="";
				String cityCode="";
				int is_default_city = 0;
				DataSet defaultDataSet = null;
				weatherCursor = weatherDatabase.queryAllData();
				City city = getCityByIp();
				if(weatherCursor.getCount()!=0 && null != city)
				{	
				    //if(mFirstRun && !Util.hasDefaultCity(RemoteWeatherService.this))
					if(mFirstRun){
						weatherCursor.close();
						// query default city
						weatherDatabase.deleteAllData();					
						mDefaultCity = city.getWeatherLocationCode(); 
						queryDefaultCity();
					}else{
						while(weatherCursor.moveToNext())
						{
							cityName = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME));
							cityCode = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_CODE));
							is_default_city = weatherCursor.getInt(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_IS_DEFAULT_CITY));
							Util.print(TAG, "service_search_path " + ApplicationConstants.searchWeatherData+cityCode);
							dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+cityCode);
							if(dataSet!=null)
							{
								Util.print(TAG, "weather_data_size "+ dataSet.getForecastWeatherDatas().size()+"");
//								for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
//								{
//									Util.print(TAG, "weather_getHighTemperature " + dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
//								}
								Util.updateData(this, dataSet, cityName, cityCode);
								if(DEBUG) Log.d(TAG, "getAutoCity." + Util.getAutoCity(mContext));
								if(Util.getAutoCity(mContext)){
									if(cityCode.equals(city.getWeatherLocationCode())){
										defaultDataSet = dataSet;
//										if(1 != is_default_city){
//											weatherDatabase.updateDataOfDefaultCity(cityName, 
//								    				null, 1);
//										}
										updateWeatherInfo(dataSet);
										mHandler.removeMessages(MSG_UPDATE_WEATHER_INFO);
										mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER_INFO);
									}else if(0 != is_default_city){
										;
//										weatherDatabase.updateDataOfDefaultCity(cityName, 
//							    				null, 0);
									}
								}else{
									if(0 != is_default_city){
										defaultDataSet = dataSet;										
										updateWeatherInfo(dataSet);
										mHandler.removeMessages(MSG_UPDATE_WEATHER_INFO);
										mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER_INFO);
									}
								}
							}
						}
						weatherCursor.close();
						
						if(null == defaultDataSet){
							dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+city.getWeatherLocationCode());
							if(dataSet!=null)
							{
								Util.print(TAG, "add weather_data_size "+ dataSet.getForecastWeatherDatas().size()+"");
//								for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
//								{
//									Util.print(TAG, "weather_getHighTemperature " + dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
//								}
								if(!checkCityIsSame(city.getWeatherLocationName())){
									Util.storeData(mContext, dataSet, city.getWeatherLocationName(), 
											city.getWeatherLocationCode(), 0);
								}
								updateWeatherInfo(dataSet);
								mHandler.removeMessages(MSG_UPDATE_WEATHER_INFO);
								mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER_INFO);								
							}							
						}						
					}
				}else if(weatherCursor.getCount()!=0){
					while(weatherCursor.moveToNext())
					{
						cityName = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME));
						cityCode = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_CODE));
						is_default_city = weatherCursor.getInt(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_IS_DEFAULT_CITY));
						Util.print(TAG, "service_search_path " + ApplicationConstants.searchWeatherData+cityCode);
						dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+cityCode);
						if(dataSet!=null)
						{
							Util.print(TAG, "weather_data_size "+ dataSet.getForecastWeatherDatas().size()+"");
//							for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
//							{
//								Util.print(TAG, "weather_getHighTemperature " + dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
//							}
							Util.updateData(this, dataSet, cityName, cityCode);
							if(DEBUG) Log.d(TAG, "getAutoCity." + Util.getAutoCity(mContext));
							if(0 != is_default_city){
								defaultDataSet = dataSet;										
								updateWeatherInfo(dataSet);
								mHandler.removeMessages(MSG_UPDATE_WEATHER_INFO);
								mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER_INFO);
							}
						}
					}
					weatherCursor.close();
					
					if(null == defaultDataSet){
						weatherCursor.close();
						if(null != city) mDefaultCity = city.getWeatherLocationCode();
						// query default city
						queryDefaultCity();					
					}				
				}else{
					weatherCursor.close();
					if(null != city) mDefaultCity = city.getWeatherLocationCode();
					// query default city
					queryDefaultCity();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			weatherCursor.close();
		}
		
	}
    
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(null != mRunnable) sWorker.removeCallbacks(mRunnable);
		unregisterMyReceiver();
	}
	
	private void updateWeatherInfo(DataSet dataSet){
		WeatherInfo weatherInfo = new WeatherInfo();
		weatherInfo.setCityCode(dataSet.getCity().getWeatherLocationCode());
		weatherInfo.setCityName(dataSet.getCity().getWeatherLocationName());
		weatherInfo.setCurrentTemperature(dataSet.getCurrentWeatherData().getCurrentTemperature());
		weatherInfo.setLowTemperature(dataSet.getForecastWeatherDatas().get(0).getLowTemperature());
		weatherInfo.setHighTemperature(dataSet.getForecastWeatherDatas().get(0).getHighTemperature());
		weatherInfo.setState(dataSet.getForecastWeatherDatas().get(0).getSkytextday());
		mWeatherInfo = weatherInfo;
	}	
	
	private void queryDefaultCity(){
		String cityCode = URLEncoder.encode(mDefaultCity);
		Util.print("bbcc", ">>>>>>>>>"+cityCode);
		if(cityCode.equals("")){
//			cityCode = Util.getLocationCityBySina();
//			if(cityCode.equals(""))
				return;
		}
		Util.print("bbcc", ">>>>>>>>>"+cityCode);
		dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+cityCode);
		if(dataSet!=null)
		{
			Util.print("bbcc", "default weather_data_size "+ dataSet.getForecastWeatherDatas().size()+"");
//			for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
//			{
//				Util.print(TAG, "weather_getHighTemperature " + dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
//			}
			if(!checkCityIsSame(dataSet.getCity().getWeatherLocationName())){
				Util.print("bbcc", dataSet.getCity().getWeatherLocationName());
				Util.storeData(mContext, dataSet, dataSet.getCity().getWeatherLocationName(), 
						dataSet.getCity().getWeatherLocationCode(), 0);
			}
			updateWeatherInfo(dataSet);
			mFirstRun = false;
			mHandler.removeMessages(MSG_UPDATE_WEATHER_INFO);
			mHandler.sendEmptyMessage(MSG_UPDATE_WEATHER_INFO);
		}
	}
	
    
    private City getCityByIp(){
    	City city = null;
    	String location = Util.getLocation();
    	Util.print("bbcc", "getCityByIP");
    	if(location==null){
    		location = Util.getLocationCityBySina();
    	}
    	if(null != location){				
    		// 如果不存在或第一次运行,则查找网络返回的城市,因有的城市名或查找出多个结果		
			try {
				location = URLEncoder.encode(location);
				Util.print("bbcc", location);						
				String searchString = ApplicationConstants.searchCityUrl + location;
				Util.print("bbcc", searchString);
				dataSet = Util.getDataSet(searchString);
				if(dataSet != null && dataSet.getCitys().size() > 0){
				  // 查找真正的天气数据 多城市时,只取第一个
					if(DEBUG){
					  for (int j = dataSet.getCitys().size() -1; j >= 0; j--){
						  Log.d(TAG, "" + j + ".Code." + dataSet.getCitys().get(j).getWeatherLocationCode() +
								  " Name." + dataSet.getCitys().get(j).getWeatherLocationName());
					  }
					}
					city = new City();
					city.setWeatherLocationCode(dataSet.getCitys().get(0).getWeatherLocationCode());
					city.setWeatherLocationName(dataSet.getCitys().get(0).getWeatherLocationName());
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();						
			}
    	}
    	return city;
    }

	private void registerMyReceiver(){				
		// network ConnectivityManager
		//{+++
		mNetworkReceiver = new BroadcastReceiver() {			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Util.print(TAG, "networkReceiver()");
				Message msg = mHandler.obtainMessage(MSG_NETWORK_CONNECTIVITY_CHANGE);
                mHandler.sendMessage(msg);					
			}
		};
		IntentFilter networkFilter = new IntentFilter();
		networkFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		registerReceiver(mNetworkReceiver, networkFilter);
		//}---	
	}
	
	private void unregisterMyReceiver(){		
		// network ConnectivityManager
		//{+++ 	
		if(null != mNetworkReceiver){
			unregisterReceiver(mNetworkReceiver);
			mNetworkReceiver = null;
		}
		//}---		
	}
	private void getNetworkConnState(){
		ConnectivityManager connec =  (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);  
		NetworkInfo info = connec.getActiveNetworkInfo();
		if(null != info){
			Message msg = mHandler.obtainMessage(MSG_NETWORK_ACTIVE);
			mHandler.sendMessage(msg);
			Util.print(TAG, "getActiveNetworkInfo() getTypeName: " + info.getTypeName() +
					" getState: " + info.getState() +
					" isAvailable: " + info.isAvailable() +
					" isConnected: " + info.isConnected() +
					" isConnectedOrConnecting: " + info.isConnectedOrConnecting() +
					" isFailover: " + info.isFailover() +
					" isRoaming: " + info.isRoaming());
		}else{
			Message msg = mHandler.obtainMessage(MSG_NETWORK_UN_ACTIVE);
			mHandler.sendMessage(msg);
			//if(DEBUG) Log.d(TAG, "getActiveNetworkInfo() info is null");
		}
		/*
		if(false){
			NetworkInfo[] allInfo=  connec.getAllNetworkInfo();
			int count = allInfo.length;
			for(int i = 0; i < count; i++){
				if(null != allInfo[i]){
					Log.d(TAG, "getAllNetworkInfo() getTypeName: " + allInfo[i].getTypeName() +
							" getState: " + allInfo[i].getState() +
							" isAvailable: " + allInfo[i].isAvailable() +
							" isConnected: " + allInfo[i].isConnected() +
							" isConnectedOrConnecting: " + allInfo[i].isConnectedOrConnecting() +
							" isFailover: " + allInfo[i].isFailover() +
							" isRoaming: " + allInfo[i].isRoaming());
				}
			}
		}
		*/
	}
	
	private boolean checkCityIsSame(String cityname)
	{
		WeatherDatabase weatherDatabase = Util.getWeatherDatabase(this);
		Cursor tempCursor = weatherDatabase.querySingleData(cityname);
		int size = tempCursor.getCount();
		tempCursor.close();
		if(size > 0)
		{
			return true;
		}
		else {
			return false;
		}		
	}
}