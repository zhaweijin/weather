package com.imt.weather;

import java.util.ArrayList;


import com.imt.weather.data.DataSet;
import com.imt.weather.data.WeatherDatabase;
import com.imt.weather.view.WeatherForecast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class WeatherMain extends Activity implements OnClickListener{
    private static final String TAG = "Weather";
    /** Called when the activity is first created. */
	private ViewFlipper mainViewFlipper;
	private ArrayList<RelativeLayout> displayLayouts = new ArrayList<RelativeLayout>();
    private WeatherForecast forecast=null;
    private DataSet dataSet=null;
    
    private WeatherDatabase weatherDatabase=null;
    private int weatherDatabaseSizeCount=0;
    private Cursor weatherCursor=null;
    
    private SharedPreferences preferences;
    private ProgressDialog progressDialog;
    
    private boolean secondEnter = false;
    private boolean firstEnter = true;
    
    private TextView displayTextView;
    private int currentPos = 0;
    private int currentWeatherCount=0;
    
    //add
    //top
    private Button addCityButton;
    private Button updateButton;
    private Button arrayLeftButton;
    private Button arrayRightButton;
    private TextView cityNameTextView;
    
    //center
    private TextView currentDateTextView;
    private TextView releaseDateTextView;
    private ImageView currentWeatherIcon;
    private TextView currentWeatherDetail;
    private TextView currentWeatherTemperatureHigh;
    private TextView currentWeatherTemperatureLow;
    
    //one
    private TextView forcastWeekOne;
    private ImageView forcastWeekOneIcon;
    private TextView forcastWeekOneHighTemperature;
    private TextView forcastWeekOneLowTemperature;
    private TextView forcastWeekOneDetail;
    
    //two
    private TextView forcastWeekTwo;
    private ImageView forcastWeekTwoIcon;
    private TextView forcastWeekTwoHighTemperature;
    private TextView forcastWeekTwoLowTemperature;
    private TextView forcastWeekTwoDetail;
    
    //three
    private TextView forcastWeekThree;
    private ImageView forcastWeekThreeIcon;
    private TextView forcastWeekThreeHighTemperature;
    private TextView forcastWeekThreeLowTemperature;
    private TextView forcastWeekThreeDetail;
    
    //four
    private TextView forcastWeekFour;
    private ImageView forcastWeekFourIcon;
    private TextView forcastWeekFourHighTemperature;
    private TextView forcastWeekFourLowTemperature;
    private TextView forcastWeekFourDetail;
    
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
			switch (msg.what) {
			case ApplicationConstants.main:
				sendWidgetBroadcast();
				handler.sendEmptyMessage(ApplicationConstants.cancell);
				//display main weather date
				break;
			case ApplicationConstants.cancell:
				if(progressDialog!=null && progressDialog.isShowing())
				   progressDialog.dismiss();
				break;
			case ApplicationConstants.network_timeout:
				new AlertDialog.Builder(WeatherMain.this)
				.setTitle(getResources().getString(R.string.dialog_title))
				.setMessage(getResources().getString(R.string.networ_timeout))
				.setPositiveButton(getResources().getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						UpdateDatabaseData(true);
					}
				})
				.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(ApplicationConstants.cancell);
				}
		    	})
				.show();
			    break;
			} 
			
		}
		
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_main);
        
        //check is go Setting scrren
        weatherDatabase= Util.getWeatherDatabase(this);        
        try {
        	 Cursor cityCursor = weatherDatabase.queryAllData();
             int citysize = cityCursor.getCount();
            Log.v("size", "size="+citysize);
             if(citysize==0){
             	Intent intent = new Intent(WeatherMain.this, WeatherSetting.class);
             	startActivity(intent);
             	finish();
             }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        init();
        Intent intent = new Intent(WeatherMain.this, RemoteWeatherService.class);
        startService(intent);
        
        
     
    }

    @Override
    protected void onPause() {    
    	super.onPause();
    	if(null != mBroadcastReceiver){
			unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
    }
    
    public void init()
    {
    	
        preferences = getSharedPreferences("weather_setting", Context.MODE_PRIVATE);
        

        
        try {
        	 Util.copyDatabaseFile(getResources().getAssets().open("translate"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Util.print("copy failture", "copy failture");
		}
        
		//top
		 addCityButton = (Button)findViewById(R.id.add_city_weather);
		 addCityButton.setOnClickListener(this);
	     updateButton = (Button)findViewById(R.id.update);
	     updateButton.setOnClickListener(this);
	     arrayLeftButton = (Button)findViewById(R.id.up_city);
	     arrayLeftButton.setOnClickListener(this);
	     arrayRightButton = (Button)findViewById(R.id.next_city);
	     arrayRightButton.setOnClickListener(this);
	     cityNameTextView =(TextView)findViewById(R.id.city_name);
	     
	     
	     //current
	     currentDateTextView = (TextView)findViewById(R.id.current_date);
	     releaseDateTextView = (TextView)findViewById(R.id.current_release_date);
	     currentWeatherIcon = (ImageView)findViewById(R.id.current_weather_icon);
	     currentWeatherDetail = (TextView)findViewById(R.id.current_weather_detail);
	     currentWeatherTemperatureHigh =(TextView)findViewById(R.id.current_weather_temperature_high);
	     currentWeatherTemperatureLow =(TextView)findViewById(R.id.current_weather_temperature_low);
 		
	     //one
	     forcastWeekOne = (TextView)findViewById(R.id.forcast_one_week);
	     forcastWeekOneIcon = (ImageView)findViewById(R.id.forcast_one_weather_icon);
	     forcastWeekOneHighTemperature =(TextView)findViewById(R.id.forcast_one_weather_high_temprature);
	     forcastWeekOneLowTemperature =(TextView)findViewById(R.id.forcast_one_weather_low_temprature);
	     forcastWeekOneDetail = (TextView)findViewById(R.id.forcast_one_weather_detail);
		
	     //two
	     forcastWeekTwo = (TextView)findViewById(R.id.forcast_two_week);
	     forcastWeekTwoIcon = (ImageView)findViewById(R.id.forcast_two_weather_icon);
	     forcastWeekTwoHighTemperature =(TextView)findViewById(R.id.forcast_two_weather_high_temprature);
	     forcastWeekTwoLowTemperature =(TextView)findViewById(R.id.forcast_two_weather_low_temprature);
	     forcastWeekTwoDetail = (TextView)findViewById(R.id.forcast_two_weather_detail);
	     
	     //three
	     forcastWeekThree = (TextView)findViewById(R.id.forcast_three_week);
	     forcastWeekThreeIcon = (ImageView)findViewById(R.id.forcast_three_weather_icon);
	     forcastWeekThreeHighTemperature =(TextView)findViewById(R.id.forcast_three_weather_high_temprature);
	     forcastWeekThreeLowTemperature =(TextView)findViewById(R.id.forcast_three_weather_low_temprature);
	     forcastWeekThreeDetail = (TextView)findViewById(R.id.forcast_three_weather_detail);
	     
	     //four
	     forcastWeekFour = (TextView)findViewById(R.id.forcast_four_week);
	     forcastWeekFourIcon = (ImageView)findViewById(R.id.forcast_four_weather_icon);
	     forcastWeekFourHighTemperature =(TextView)findViewById(R.id.forcast_four_weather_high_temprature);
	     forcastWeekFourLowTemperature =(TextView)findViewById(R.id.forcast_four_weather_low_temprature);
	     forcastWeekFourDetail = (TextView)findViewById(R.id.forcast_four_weather_detail);
	     

	     
//      Util.getCurrentCity(this);
        
//        checkIsNeedUpdate();
       
	     if(currentWeatherCount<=0){
	    	 arrayRightButton.setVisibility(View.INVISIBLE);
	    	 arrayLeftButton.setVisibility(View.INVISIBLE);
	     }
	        
	     if(currentWeatherCount<=1)
	    	 arrayRightButton.setVisibility(View.INVISIBLE);
	     
	     
//	     if(currentPos<=0){
//	    	 arrayLeftButton.setVisibility(View.INVISIBLE);
//	     }else {
//			arrayLeftButton.setVisibility(View.VISIBLE);
//		}
//	     
//	     if(currentPos>=currentWeatherCount-1){
//	    	 arrayRightButton.setVisibility(View.INVISIBLE);
//	     }else {
//	    	 arrayRightButton.setVisibility(View.VISIBLE);
//		}
    }

    private void setCurrentPosToDefCity() {
        if (weatherDatabase == null) {
            return;
        }
        int defCityPos = weatherDatabase.getDefCityPosition();
        if (defCityPos != WeatherDatabase.POSITION_INVALID) {
            currentPos = defCityPos;
        }
    }
    
	private void initdisplayWeatherData() {
		weatherCursor = weatherDatabase.queryAllData();
		int count = weatherCursor.getCount();
		currentWeatherCount = count;
		weatherCursor.moveToFirst();
        if(count<=0)
        	return;
       
        currentPos = 0;
		// top
		cityNameTextView.setText(weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME)));
		// current
		currentDateTextView.setText(weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_DATE)));
		releaseDateTextView
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_OBSERVATIONTIME)));
		String currentWeatherDetailString = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_SKEYTEXT));

		currentWeatherIcon.setImageResource(Util.getIconID(this,
				currentWeatherDetailString));
		currentWeatherDetail.setText(currentWeatherDetailString);
		
		currentWeatherTemperatureHigh
				.setText(Util.FashiToSheshi(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1)))+"℃");
   
		currentWeatherTemperatureLow
				.setText("  ~ "+Util.FashiToSheshi(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1)))+"℃");

		// forcast one
		forcastWeekOne
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA2)));
		String forcastOneWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA2));
		forcastWeekOneDetail.setText(forcastOneWeatherDetail);
		forcastWeekOneIcon.setImageResource(Util.getIconID(this,
				forcastOneWeatherDetail));
		String forcastOneTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA2));
		String forcastOneTemperatureLow = weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA2));
		forcastWeekOneHighTemperature.setText(Util.FashiToSheshi(forcastOneTemperatureHigh)+"℃");
		forcastWeekOneLowTemperature.setText(Util.FashiToSheshi(forcastOneTemperatureLow)+"℃");

		// forcast two
		forcastWeekTwo
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA3)));
		String forcastTwoWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA3));
		forcastWeekTwoDetail.setText(forcastTwoWeatherDetail);
		forcastWeekTwoIcon.setImageResource(Util.getIconID(this,
				forcastTwoWeatherDetail));
		String forcastTwoTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA3));
		String forcastTwoTemperatureLow = weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA3));
		forcastWeekTwoHighTemperature.setText(Util.FashiToSheshi(forcastTwoTemperatureHigh)+"℃");
		forcastWeekTwoLowTemperature.setText(Util.FashiToSheshi(forcastTwoTemperatureLow)+"℃");

		// forcast three
		forcastWeekThree
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA4)));
		String forcastThreeWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA4));
		forcastWeekThreeDetail.setText(forcastThreeWeatherDetail);
		forcastWeekThreeIcon.setImageResource(Util.getIconID(this,
				forcastTwoWeatherDetail));
		String forcastThreeTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA4));
		String forcastThreeTemperatureLow = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA4));
		forcastWeekThreeHighTemperature.setText(Util.FashiToSheshi(forcastThreeTemperatureHigh)+"℃");
		forcastWeekThreeLowTemperature.setText(Util.FashiToSheshi(forcastThreeTemperatureLow)+"℃");

		// forcast four
		forcastWeekFour
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA5)));
		String forcastFourWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA5));
		forcastWeekFourDetail.setText(forcastFourWeatherDetail);
		forcastWeekFourIcon.setImageResource(Util.getIconID(this,
				forcastTwoWeatherDetail));
		String forcastFourTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA5));
		String forcastFourTemperatureLow = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA5));
		forcastWeekFourHighTemperature.setText(Util.FashiToSheshi(forcastFourTemperatureHigh)+"℃");
		forcastWeekFourLowTemperature.setText(Util.FashiToSheshi(forcastFourTemperatureLow)+"℃");
		weatherCursor.close();

	}
    
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        setCurrentPosToDefCity();;
		updateWeatherInfo();
		IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.xuzhi.weather.UPDATE_WIDGET");
        registerReceiver(mBroadcastReceiver, intentFilter);
        
        UpdateDatabaseData(false);
	}




	public void UpdateDatabaseData(final boolean isShowProgress)
	{
		Util.print("addcity", "addcity");
		if(isShowProgress){
			progressDialog = Util.getProgressDialog(this,getResources().getString(R.string.updating));
			progressDialog.show();
		}
		
		if(Util.getNetworkState(this))
		{
			addCityDataMianThread();
		}
		else {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.dialog_title))
			.setMessage(getResources().getString(R.string.no_network))
			.setPositiveButton(getResources().getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					progressDialog.dismiss();
					UpdateDatabaseData(isShowProgress);
				}
			})
			.setNegativeButton(getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(ApplicationConstants.cancell);
				}
			})
			.show();
		}
	}
	
	
	public void addCityDataMianThread()
	{
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					String cityName="";
					String cityCode="";
					boolean fail = false;
					
					weatherCursor = weatherDatabase.queryAllData();
					while(weatherCursor.moveToNext())
					{
						cityName = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME));
						cityCode = weatherCursor.getString(weatherCursor.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_CODE));

						Util.print("search_path", ApplicationConstants.searchWeatherData+cityCode);
						dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+ cityCode);
						if(dataSet!=null)
						{
							Util.print(TAG, "weather sky=="+dataSet.getCurrentWeatherData().getSkytext());
							Util.print(TAG, dataSet.getForecastWeatherDatas().size()+"");
							for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
							{
								Util.print("weather_getHighTemperature", dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
							}
							Util.updateData(WeatherMain.this, dataSet, cityName, cityCode);
						}
						else
						{
							fail=true;
						}
					}
					weatherCursor.close();
					handler.sendEmptyMessage(ApplicationConstants.main);
					if(fail)
					{
						handler.sendEmptyMessage(ApplicationConstants.network_timeout);
					}
					
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					weatherCursor.close();
					handler.sendEmptyMessage(ApplicationConstants.cancell);
				}
			}
		}).start();
	}
	
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// TODO Auto-generated method stub
//		getMenuInflater().inflate(R.menu.menu, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// TODO Auto-generated method stub
//		switch (item.getItemId()) {
//		case R.id.itemsetting:
//			Intent intent = new Intent(this,WeatherSetting.class);
//			startActivity(intent);
//			break;
//		case R.id.itemRefresh:
//			UpdateDatabaseData();
//			break;
//		case R.id.itemAbout:
//			Util.showDialogAbout(this);
//			break;
//		}
//		return true;
//	}
    
	
	public void sendWidgetBroadcast()
	{
		Intent localIntent1 = new Intent("com.xuzhi.weather.UPDATE_WIDGET");
		sendBroadcast(localIntent1);
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.add_city_weather:
			Intent intent = new Intent(WeatherMain.this,WeatherSetting.class);
			startActivity(intent);
			finish();
			break;
		case R.id.up_city:
			if(currentPos<0)
				currentPos =1;
			currentPos--;	
			updateWeatherData();
			break;
		case R.id.update:
			UpdateDatabaseData(true);
			break;
		case R.id.next_city:
			if(currentPos>currentWeatherCount-1)
				currentPos = currentWeatherCount-1;
			currentPos++;
			updateWeatherData();
			break;
		}
	}

	private void updateWeatherData() {
		
		if(currentPos<=0){
	    	 arrayLeftButton.setVisibility(View.INVISIBLE);
	     }else {
			arrayLeftButton.setVisibility(View.VISIBLE);
		}
	     
	     if(currentPos>=currentWeatherCount-1){
	    	 arrayRightButton.setVisibility(View.INVISIBLE);
	     }else {
	    	 arrayRightButton.setVisibility(View.VISIBLE);
		}
		
		weatherCursor = weatherDatabase.queryAllData();
		 
		weatherCursor.moveToPosition(currentPos);
       
		// top
		cityNameTextView.setText(weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_CITY_NAME)));
		// current
		currentDateTextView.setText(weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_DATE)));
		releaseDateTextView
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_OBSERVATIONTIME)));
		String currentWeatherDetailString = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_CURRENT_SKEYTEXT));

		currentWeatherIcon.setImageResource(Util.getIconID(this,
				currentWeatherDetailString));
		currentWeatherDetail.setText(currentWeatherDetailString);
		
		currentWeatherTemperatureHigh
				.setText(Util.FashiToSheshi(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA1)))+"℃");
   
		currentWeatherTemperatureLow
				.setText("  ~ "+Util.FashiToSheshi(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA1)))+"℃");

		// forcast one
		forcastWeekOne
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA2)));
		String forcastOneWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA2));
		forcastWeekOneDetail.setText(forcastOneWeatherDetail);
		forcastWeekOneIcon.setImageResource(Util.getIconID(this,
				forcastOneWeatherDetail));
		String forcastOneTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA2));
		String forcastOneTemperatureLow = weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA2));
		forcastWeekOneHighTemperature.setText(Util.FashiToSheshi(forcastOneTemperatureHigh)+"℃");
		forcastWeekOneLowTemperature.setText(Util.FashiToSheshi(forcastOneTemperatureLow)+"℃");

		// forcast two
		forcastWeekTwo
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA3)));
		String forcastTwoWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA3));
		forcastWeekTwoDetail.setText(forcastTwoWeatherDetail);
		forcastWeekTwoIcon.setImageResource(Util.getIconID(this,
				forcastTwoWeatherDetail));
		String forcastTwoTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA3));
		String forcastTwoTemperatureLow = weatherCursor.getString(weatherCursor
				.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA3));
		forcastWeekTwoHighTemperature.setText(Util.FashiToSheshi(forcastTwoTemperatureHigh)+"℃");
		forcastWeekTwoLowTemperature.setText(Util.FashiToSheshi(forcastTwoTemperatureLow)+"℃");

		// forcast three
		forcastWeekThree
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA4)));
		String forcastThreeWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA4));
		forcastWeekThreeDetail.setText(forcastThreeWeatherDetail);
		forcastWeekThreeIcon.setImageResource(Util.getIconID(this,
				forcastTwoWeatherDetail));
		String forcastThreeTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA4));
		String forcastThreeTemperatureLow = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA4));
		forcastWeekThreeHighTemperature.setText(Util.FashiToSheshi(forcastThreeTemperatureHigh)+"℃");
		forcastWeekThreeLowTemperature.setText(Util.FashiToSheshi(forcastThreeTemperatureLow)+"℃");

		// forcast four
		forcastWeekFour
				.setText(weatherCursor.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_DAY_DATA5)));
		String forcastFourWeatherDetail = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_SKYTEXTDAY_DATA5));
		forcastWeekFourDetail.setText(forcastFourWeatherDetail);
		forcastWeekFourIcon.setImageResource(Util.getIconID(this,
				forcastTwoWeatherDetail));
		String forcastFourTemperatureHigh = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_HIGH_DATA5));
		String forcastFourTemperatureLow = weatherCursor
				.getString(weatherCursor
						.getColumnIndexOrThrow(WeatherDatabase.KEY_FORECAST_LOW_DATA5));
		forcastWeekFourHighTemperature.setText(Util.FashiToSheshi(forcastFourTemperatureHigh)+"℃");
		forcastWeekFourLowTemperature.setText(Util.FashiToSheshi(forcastFourTemperatureLow)+"℃");

	}



//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            event.startTracking();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
    
//    public boolean onKeyUp(int keycode, KeyEvent event) {
//    	if(KeyEvent.KEYCODE_BACK==keycode)
//		{	
//			sendWidgetBroadcast();
//			finish();
//            return true;
//		}
//		else {
//			return super.onKeyDown(keycode, event);
//		}
//    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Intent intent = new Intent(WeatherMain.this, RemoteWeatherService.class);
        stopService(intent);
	}
	
	private void updateWeatherInfo(){
		weatherCursor = weatherDatabase.queryAllData();
		currentWeatherCount = weatherCursor.getCount();
//		initdisplayWeatherData();
		updateWeatherData();
	}
}