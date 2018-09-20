package com.imt.weather;

import java.net.URLEncoder;
import java.text.BreakIterator;
import java.util.ArrayList;

import javax.security.auth.PrivateCredentialPermission;


import com.imt.weather.data.City;
import com.imt.weather.data.DataSet;
import com.imt.weather.data.WeatherDatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class WeatherSearch extends Activity {
    /** Called when the activity is first created. */
	private Button searchButton;
	private EditText searchEditText;
	private ListView searchResultListView;
	private DataSet dataSet;
	private ProgressDialog progressDialog;
	private String searchAction;
	
	private SearchAdapter cityAdapter;
	private ArrayList<String> cityName = new ArrayList<String>();
	private ArrayList<String> cityCode = new ArrayList<String>();
	
	private InputMethodManager imm;
	
	private ImageView buttom_line;
	
	private final static int sameCity = 0x878;
	private final static int ServiceConnectionFailed = 0x986;
	
	private boolean firstAddCity;
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case ApplicationConstants.main:
				for(int i=0;i<dataSet.getCitys().size();i++)
				{
					Util.print("citys_size", dataSet.getCitys().get(i).getWeatherLocationName()+"------" +dataSet.getCitys().get(i).getWeatherLocationCode());
				}
				cityName.clear();
				cityCode.clear();
				ArrayList<City> cities = dataSet.getCitys();
				for(int i=0;i<cities.size();i++)
				{
					cityName.add(cities.get(i).getWeatherLocationName());
					cityCode.add(cities.get(i).getWeatherLocationCode());
				}
				cityAdapter = new SearchAdapter(WeatherSearch.this, cities);
				searchResultListView.setAdapter(cityAdapter);
				handler.sendEmptyMessage(ApplicationConstants.cancell);
				imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
				
				if(cityName.size()>0)
					buttom_line.setVisibility(View.VISIBLE);
				break;
			case ApplicationConstants.cancell:
				progressDialog.dismiss();
				break;
			case ApplicationConstants.network_timeout:
				new AlertDialog.Builder(WeatherSearch.this)
				.setTitle(getResources().getString(R.string.dialog_title))
				.setMessage(getResources().getString(R.string.networ_timeout))
				.setPositiveButton(getResources().getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						startSearch();
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
			case ApplicationConstants.add_sucess:
				Toast.makeText(WeatherSearch.this, getResources().getString(R.string.add_city_success), 1500).show();
				ApplicationConstants.updateWidget(WeatherSearch.this);
				handler.sendEmptyMessage(ApplicationConstants.cancell);
				break;
			case ApplicationConstants.add_fail:
				Toast.makeText(WeatherSearch.this, getResources().getString(R.string.add_city_fail), 1500).show();
				break;
			case sameCity:
				Toast.makeText(WeatherSearch.this, getResources().getString(R.string.city_exist), 1500).show();
				handler.sendEmptyMessage(ApplicationConstants.cancell);
				break;
			case ServiceConnectionFailed:
				Toast.makeText(WeatherSearch.this, getResources().getString(R.string.server_request_failed), 1500).show();
				handler.sendEmptyMessage(ApplicationConstants.cancell);
				break;
			} 
			
		}
		
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_search);
        init();
        Intent intent = getIntent();
        if(null != intent){
        	firstAddCity = intent.getBooleanExtra(
        			ApplicationConstants.KEY_IS_FIRST_ADD_CITY, false);
        }
//        startSearch();
    }

    
    public void displayWidth(){
    	DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        
    }
    
    /*
    public void sendWidgetBroadcast()
	{
    	Util.print("search_sendbrocast", "brocast");
		Intent localIntent1 = new Intent("com.xuzhi.weather.UPDATE_WIDGET");
		sendBroadcast(localIntent1);
	}
	*/
    
    public void init()
    {
    	searchEditText = (EditText)findViewById(R.id.editSearchcity);
    	
    	imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    	
    	
    	searchButton = (Button)findViewById(R.id.btnSearch);
    	searchButton.setOnClickListener(onClickListener);
    	
    	buttom_line = (ImageView)findViewById(R.id.buttom_line);
    	searchResultListView = (ListView)findViewById(R.id.listSearchResult);
    	searchResultListView.setOnItemClickListener(listviewOnItemClickListener);
    	
    }
    
    
    View.OnClickListener onClickListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnSearch:
				if(!searchEditText.getText().toString().trim().equals(""))
				{
					startSearch();
				}
				break;
			}
		}
	};
	
	AdapterView.OnItemClickListener listviewOnItemClickListener = new AdapterView.OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			addCityDataToDatabase(cityCode.get(arg2),arg2);
		}
		
	};
	
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if(keyCode== KeyEvent.KEYCODE_BACK)
//		{
//			
//			
//		}
//		return false;
//		
//	}


	public void startSearch()
	{
		progressDialog = Util.getProgressDialog(this,getResources().getString(R.string.Searching));
		progressDialog.show();
		if(Util.getNetworkState(this))
		{
			searchMainThread();
		}
		else {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.dialog_title))
			.setMessage(getResources().getString(R.string.no_network))
			.setPositiveButton(getResources().getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					progressDialog.dismiss();
					startSearch();
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
	
	public void searchMainThread()
	{
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					searchAction = URLEncoder.encode(searchEditText.getText().toString().trim());
					Util.print(">>", searchAction);
//					searchAction  = "sigiriya";
//					searchAction = Util.GetGoogleTranslateJSONString(searchAction, "zh-CN", "en", "utf-8");
					if(searchAction==null)
					{
						 handler.sendEmptyMessage(WeatherSearch.ServiceConnectionFailed);
					}
					else {
//						String searchString = ApplicationConstants.searchCityUrl+ searchAction+"&weadegreetype=%s";
						String searchString = ApplicationConstants.searchCityUrl+ searchAction;
//						searchString = searchString.replaceAll(" ", "%20");
						Util.print("search_path", searchString);
						dataSet = Util.getDataSet(searchString);
						if(dataSet!=null)
						  handler.sendEmptyMessage(ApplicationConstants.main);
						else
						  handler.sendEmptyMessage(ApplicationConstants.network_timeout);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handler.sendEmptyMessage(WeatherSearch.ServiceConnectionFailed);
				}
			}
		}).start();
	}
	
	public void addCityDataToDatabase(final String citycode,final int id)
	{
		progressDialog = Util.getProgressDialog(this,getResources().getString(R.string.adding));
		progressDialog.show();
		if(Util.getNetworkState(this))
		{
			addCityDataMianThread(citycode, id);
		}
		else {
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.dialog_title))
			.setMessage(getResources().getString(R.string.no_network))
			.setPositiveButton(getResources().getString(R.string.dialog_retry), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					addCityDataMianThread(citycode, id);
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


	
	public void addCityDataMianThread(final String citycode,final int id )
	{
		new Thread(new Runnable() {
			
			public void run() {
				// TODO Auto-generated method stub
				try {
					//+"&weadegreetype=%s"
					Util.print("search_path", ApplicationConstants.searchWeatherData+citycode);
//					dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+ citycode+"&weadegreetype=%s");
					dataSet = Util.getDataSet(ApplicationConstants.searchWeatherData+ citycode);
					if(dataSet!=null){
//						Util.print("weather_data_size", dataSet.getForecastWeatherDatas().size()+"");
//						for(int i=0;i<dataSet.getForecastWeatherDatas().size();i++)
//						{
//							Util.print("getHighTemperature", dataSet.getForecastWeatherDatas().get(i).getHighTemperature()+"");
//						}
						if(!checkCityIsSame(cityName.get(id))){
                            int is_default_city = 0;
                            if (firstAddCity && !Util.hasDefaultCity(WeatherSearch.this)){
                                is_default_city = 1;
                            } else {
                                is_default_city = 0;
                            }
							//Util.print("weather_current_data", dataSet.getCurrentWeatherData().getCurrentTemperature());
							Util.storeData(WeatherSearch.this, dataSet, cityName.get(id), cityCode.get(id),
									//is_default_city);
									0);
							firstAddCity = false;
							handler.sendEmptyMessage(ApplicationConstants.add_sucess);
						}
						else {
							handler.sendEmptyMessage(WeatherSearch.sameCity);
						}
					}
					else{
						handler.sendEmptyMessage(ApplicationConstants.add_fail);
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
	//true same
	public boolean checkCityIsSame(String cityname)
	{
		WeatherDatabase weatherDatabase = Util.getWeatherDatabase(this);
		Cursor tempCursor = weatherDatabase.querySingleData(cityname);
		int size = tempCursor.getCount();
		tempCursor.close();
		if(size>0)
		{
			return true;
		}
		else {
			return false;
		}
		
	}
	
}