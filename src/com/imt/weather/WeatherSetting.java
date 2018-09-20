package com.imt.weather;

import java.net.URLEncoder;
import java.util.ArrayList;


import com.imt.weather.data.CityAdapterData;
import com.imt.weather.data.DataSet;
import com.imt.weather.data.WeatherDatabase;
import com.imt.weather.view.WeatherForecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.StaticLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class WeatherSetting extends Activity {
	private static final String TAG = "WeatherSetting";
	
    /** Called when the activity is first created. */
	private SharedPreferences preferences;
	private GridView cityGridView;
	private LinearLayout cityLinearLayout;
	
	private final static int usedHeight = 130;

	protected static final boolean DEBUG = false;
	private WeatherDatabase weatherDatabase=null;
	private Cursor weatherCursor=null;
	private CityAdapter cityAdapter;
	
	private Button back;
	//private Button update;
	private CheckBox cb_auto_city;
	private Context mContext;
	private int mCurPosition, mPrePosition;
	private CityAdapterData mCurCityAdapterData;
	private boolean firstAddCity = false;
	private DataSet dataSet;
	
	private ArrayList<CityAdapterData> cityDatas = new ArrayList<CityAdapterData>();
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			handler.post(new Runnable() {				
				@Override
				public void run() { 
					updateWeatherInfo();
				}
			});
		}    	
    };
    
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
		}
	};
	
	private IRemoteWeatherService mIRemoteWeatherService;
	private IShowWeatherInfo mCallBack;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_setting);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        Log.v("height", dm.heightPixels+"");
        Log.v("weight", dm.widthPixels+"");
        
        mContext = this;
        initSetting();

//		Intent intent = new Intent(WeatherSetting.this, RemoteWeatherService.class);
		bindWeatherService();
    }

	
    public void initSetting()
    {
    	preferences = getSharedPreferences("weather_setting", Context.MODE_PRIVATE);

    	cityLinearLayout = (LinearLayout)findViewById(R.id.layout_city_gridview);
    	cityGridView =  (GridView)findViewById(R.id.gridview);
    	
    	cityGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mCurPosition = arg2;
				mCurCityAdapterData = ((CityAdapterData)arg0.getAdapter().getItem(arg2));
				if(mCurCityAdapterData.isAdd()){
					Intent intent = new Intent(WeatherSetting.this,WeatherSearch.class);
					if(firstAddCity){
						intent.putExtra(ApplicationConstants.KEY_IS_FIRST_ADD_CITY, true);
					}else{
						intent.putExtra(ApplicationConstants.KEY_IS_FIRST_ADD_CITY, false);
					}
					startActivity(intent);
				}else{
					updateDefaultCity();					
					//updateRemoteWeatherInfo(mCurCityAdapterData);
				}
			}    		
		});
    	
    	cityGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mCurPosition = arg2;
				mCurCityAdapterData = (CityAdapterData)arg0.getAdapter().getItem(arg2);				
				if(mCurCityAdapterData.isAdd() || 0 != mCurCityAdapterData.getDefaultCity()){
					return true;
				}
				new AlertDialog.Builder(mContext)
				.setTitle(R.string.confim_delete)
				.setPositiveButton(mContext.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
							weatherDatabase.deleteData(mCurCityAdapterData.getCityName());
							cityAdapter.removeItem(mCurPosition);							
							cityAdapter.notifyDataSetChanged();
						}
				})
				.setNegativeButton(mContext.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.show();
				
				return true;
			}
		});
    	
    	weatherDatabase = Util.getWeatherDatabase(this);
    	
    	
    	back = (Button)findViewById(R.id.back);
    	back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				go();
			}
		});
    	/*
    	cb_auto_city = (CheckBox)findViewById(R.id.cb_auto_city);
    	boolean auto_city = Util.getAutoCity(mContext);
    	if(auto_city){
    		cb_auto_city.setChecked(true);
    		cb_auto_city.setEnabled(false);
    		cityGridView.setEnabled(false);    		
    	}
    	else{
    		cb_auto_city.setChecked(false);
    		cityGridView.setEnabled(true);
    	}
    	cb_auto_city.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "isChecked." + isChecked);
				if(isChecked){
					cb_auto_city.setEnabled(false);
					cityGridView.setEnabled(false);
					Util.setAutoCity(true, mContext);
					startSearch();
				}else{
					cb_auto_city.setEnabled(true);
					cityGridView.setEnabled(true);
					Util.setAutoCity(false, mContext);
				}
			}
		});
    	*/
    }
    
   
//    
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// TODO Auto-generated method stub
//		super.onActivityResult(requestCode, resultCode, data);
//		if (requestCode == 1) {
//
//			Util.print("update adapter", "update adapter");
//			weatherCursor = weatherDatabase.queryAllData();
//			cityListView.setAdapter(new CityAdapter(this,weatherCursor));
//		}
//	}
//    

   

	@Override
	protected void onResume() {		
		super.onResume();
		Util.print(TAG, "onResume()");		
		updateWeatherInfo();
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xuzhi.weather.UPDATE_WIDGET");
        registerReceiver(mBroadcastReceiver, intentFilter);
        //boolean auto_city = Util.getAutoCity(mContext);
        //if(auto_city) startSearch();
        if((null != cityDatas && cityDatas.size() <= 1) || (Util.getAutoCity(mContext))){        	
        	Util.setAutoCity(true, mContext);
        	if(null != mIRemoteWeatherService){
				try {
					mIRemoteWeatherService.setDefaultCity("", false);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
        }
	}
    
    
    @Override
	protected void onPause() {
		super.onPause();
		if(null != mBroadcastReceiver){
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
	}
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(!weatherCursor.isClosed())
			weatherCursor.close();
//		 go();
		unbindWeatherService();
    }
    
    private void go(){
    	
         try {
        	 Cursor cityCursor = weatherDatabase.queryAllData();
        	 if(cityCursor.getCount()>0){
        		 Intent intent = new Intent(WeatherSetting.this,WeatherMain.class);
        		 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        	                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        		 startActivity(intent);
        		 finish();
        	 }else {
        		 finish();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		go();
    		return true;
    	}
    	return super.onKeyUp(keyCode, event);
    	
    }
    
    private void updateDefaultCity(){
    	Util.print(TAG, "updateDefaultCity()");
    	CityAdapterData defaultCity = cityAdapter.getDefaultCity();
    	CityAdapterData currentCity = cityAdapter.getItem(mCurPosition);
    	if(null != defaultCity && null != currentCity){
    		if(defaultCity.equals(currentCity)){
	    		// update db
	    		weatherDatabase.updateDataOfDefaultCity(currentCity.getCityName(), 
	    				null, 0);
	    		// update adapter
	    		currentCity.setDefaultCity(0);
	    		cityAdapter.notifyDataSetChanged();
	    		Util.setAutoCity(true, mContext);
	    		if(null != mIRemoteWeatherService){
					try {
						mIRemoteWeatherService.setDefaultCity("", false);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
    		}else{
    			// update db
	    		weatherDatabase.updateDataOfDefaultCity(currentCity.getCityName(), 
	    				null, 1);
	    		weatherDatabase.updateDataOfDefaultCity(defaultCity.getCityName(), 
	    				null, 0);
	    		// update adapter
	    		currentCity.setDefaultCity(1);
	    		defaultCity.setDefaultCity(0);
	    		cityAdapter.notifyDataSetChanged();
	    		Util.setAutoCity(false, mContext);
	    		updateRemoteWeatherInfo(mCurCityAdapterData);
    		}
    	}else if(null == defaultCity){
    		// update db
    		weatherDatabase.updateDataOfDefaultCity(currentCity.getCityName(), 
    				null, 1);
    		// update adapter
    		currentCity.setDefaultCity(1);
    		cityAdapter.notifyDataSetChanged();
    		Util.setAutoCity(false, mContext);
    		updateRemoteWeatherInfo(mCurCityAdapterData);
    	}
    	ApplicationConstants.updateWidget(WeatherSetting.this);
    }
    
    private void updateCurrentDefaultCity(){
    	CityAdapterData defaultCity = cityAdapter.getDefaultCity();    	
    	if(null != defaultCity){
    		// update db
    		weatherDatabase.updateDataOfDefaultCity(defaultCity.getCityName(), 
    				null, 0);
    		// update adapter
    		defaultCity.setDefaultCity(0);
    		cityAdapter.notifyDataSetChanged();
    	}
    	ApplicationConstants.updateWidget(WeatherSetting.this);
    }
    
    private void updateDefaultCity(int position){
    	CityAdapterData defaultCity = cityAdapter.getDefaultCity();
    	CityAdapterData currentCity = cityAdapter.getItem(position);
    	if(null != defaultCity && null != currentCity){
    		// update db
    		weatherDatabase.updateDataOfDefaultCity(defaultCity.getCityName(), 
    				null, 0);
    		weatherDatabase.updateDataOfDefaultCity(currentCity.getCityName(), 
    				null, 1);
    		// update adapter
    		defaultCity.setDefaultCity(0);
    		currentCity.setDefaultCity(1);
    		cityAdapter.notifyDataSetChanged();
    	}
    	ApplicationConstants.updateWidget(WeatherSetting.this);
    }
    
    private void updateWeatherInfo(){
    	weatherCursor = weatherDatabase.queryAllData();
		cityDatas.clear();
		if(weatherCursor.getCount()!=0)
		{
			firstAddCity = false;
			while(weatherCursor.moveToNext())
			{
				CityAdapterData cityAdapterData = new CityAdapterData();
				cityAdapterData.setCityName(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME)));
				cityAdapterData.setCityCode(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_CODE)));
				cityAdapterData.setLowTemperature(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1)));
				cityAdapterData.setHighTemperature(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1)));
				cityAdapterData.setWeatherState(weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_SKEYTEXT)));				
				cityAdapterData.setDefaultCity(weatherCursor.getInt(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_IS_DEFAULT_CITY)));
				cityAdapterData.setAdd(false);				
				cityDatas.add(cityAdapterData);
			}
		}else{
			firstAddCity = true;
		}
		weatherCursor.close();
		
		CityAdapterData cityAdapterData = new CityAdapterData();
		cityAdapterData.setAdd(true);
		cityDatas.add(cityAdapterData);
		
		
		cityAdapter = new CityAdapter(this, WeatherSetting.this,cityDatas);
		cityGridView.setAdapter(cityAdapter);
    }
    
    private class MyServiceConnection implements ServiceConnection{
        @Override  
        public void onServiceConnected(ComponentName name, IBinder service) {  
            mIRemoteWeatherService = IRemoteWeatherService.Stub.asInterface(service);
            if((null != cityDatas && cityDatas.size() <= 1) || (Util.getAutoCity(mContext))){        	
            	Util.setAutoCity(true, mContext);
            	if(null != mIRemoteWeatherService){
    				try {
    					mIRemoteWeatherService.setDefaultCity("", false);
    				} catch (RemoteException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
        		}
            }
        }
        
        @Override  
        public void onServiceDisconnected(ComponentName name) {  
        	;
        }
    }
    
    MyServiceConnection mConnection = new MyServiceConnection();
    
    private void bindWeatherService(){
    	Intent intent = new Intent();
    	intent.setClassName("com.imt.weather", "com.imt.weather.RemoteWeatherService");
    	bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void unbindWeatherService(){
    	unbindService(mConnection);
    }
    
    private void updateRemoteWeatherInfo(DataSet dataSet, String cityName, String cityCode){
    	if(null != mIRemoteWeatherService && null != dataSet){
    		try {
    			WeatherInfo weatherInfo = new WeatherInfo();
    			weatherInfo.setCityCode(cityCode);
    			weatherInfo.setCityName(cityName);
    			weatherInfo.setLowTemperature(dataSet.getForecastWeatherDatas().get(0).getLowTemperature());
    			weatherInfo.setHighTemperature(dataSet.getForecastWeatherDatas().get(0).getHighTemperature());
    			weatherInfo.setState(dataSet.getForecastWeatherDatas().get(0).getSkytextday());
				mIRemoteWeatherService.updateWeatherInfo(weatherInfo);
			} catch (RemoteException e) {				
				e.printStackTrace();
			}
    	}
    }
    
    private void updateRemoteWeatherInfo(CityAdapterData cityAdapterData){
    	if(null != mIRemoteWeatherService && null != cityAdapterData){
    		try {
    			WeatherInfo weatherInfo = new WeatherInfo();
    			weatherInfo.setCityCode(cityAdapterData.getCityCode());
    			weatherInfo.setCityName(cityAdapterData.getCityName());
    			weatherInfo.setLowTemperature(cityAdapterData.getLowTemperature());
    			weatherInfo.setHighTemperature(cityAdapterData.getHighTemperature());
    			weatherInfo.setState(cityAdapterData.getWeatherState());
				mIRemoteWeatherService.updateWeatherInfo(weatherInfo);
			} catch (RemoteException e) {				
				e.printStackTrace();
			}
    	}
    }
    
    private void startSearch(){    	
		new Thread(new Runnable() {
    		String citycode = null;
    		String cityname = null;
    		int position = 0;
			public void run() {
				String location = Util.getLocation();
		    	if(null != location){					
		    		boolean found = false;
		    		boolean default_city = false;
//		    		if(!firstAddCity){		    			
//						for(int i = cityDatas.size() - 1; i >= 0; i--){
//							if(location.equals(cityDatas.get(i).getCityName())){
//								found = true;
//								if(1 == cityDatas.get(i).getDefaultCity()){
//									default_city = true;
//								}
//								break;
//							}
//						}
//		    		}
		    		// 如果不存在或第一次运行,则查找网络返回的城市,因有的城市名或查找出多个结果
		    		if(firstAddCity || !found){
			    		try {
			    			location = URLEncoder.encode(location);
							Util.print(">>", location);						
							String searchString = ApplicationConstants.searchCityUrl + location;
							Util.print("search_path", searchString);
							dataSet = Util.getDataSet(searchString);
							if(dataSet != null && dataSet.getCitys().size() > 0){
							  // 查找真正的天气数据 多城市时,只取第一个
								if(DEBUG){
								  for (int j = dataSet.getCitys().size() -1; j >= 0; j--){
									  Log.d(TAG, "" + j + ".Code." + dataSet.getCitys().get(j).getWeatherLocationCode() +
											  " Name." + dataSet.getCitys().get(j).getWeatherLocationName());
								  }
								}
								citycode = dataSet.getCitys().get(0).getWeatherLocationCode();
								cityname = dataSet.getCitys().get(0).getWeatherLocationName();
								Util.print("cityCode", ApplicationConstants.searchWeatherData+citycode);
								dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData + citycode);
								if(dataSet!=null){
									if(DEBUG){								
										Util.print("weather_data_size", dataSet.getForecastWeatherDatas().size()+"");
										for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
										{
											Util.print("getHighTemperature", dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
										}
									}
									for(int i = cityDatas.size() - 1; i >= 0; i--){
										if(citycode.equals(cityDatas.get(i).getCityCode())){
											found = true;
											if(1 == cityDatas.get(i).getDefaultCity()){
												default_city = true;
											}else{
												position = i;
											}
											break;
										}
									}
									if(!found){
										//Util.print("weather_current_data", dataSet.getCurrentWeatherData().getCurrentTemperature());
										Util.storeData(WeatherSetting.this, dataSet, cityname, citycode, 
												1);//(firstAddCity ? 1 : 0));
										firstAddCity = false;
										handler.post(new Runnable() {					
											@Override
											public void run() {
												updateCurrentDefaultCity();
												updateRemoteWeatherInfo(dataSet, cityname, citycode);
											}
										});
									}else{
										if(!default_city)
											handler.post(new Runnable() {					
											@Override
											public void run() {
												updateDefaultCity(position);
												updateRemoteWeatherInfo(dataSet, cityname, citycode);
											}
										});
									}
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();						
						}
		    		}
		    	}
		    	handler.post(new Runnable() {					
					@Override
					public void run() {
						//cb_auto_city.setEnabled(true);
						updateWeatherInfo();
					}
				});
			}
		}).start();		
    }
}