package com.imt.weather;


import java.util.ArrayList;


import com.imt.weather.data.CityAdapterData;
import com.imt.weather.data.WeatherDatabase;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class CityAdapter extends BaseAdapter {

	private static final String TAG = "CityAdapter";

	private ArrayList<CityAdapterData> cityAdapterDatas;

	private Activity activity;
	private Context context;
	private WeatherDatabase weatherDatabase;
	public CityAdapter(Context context,Activity activity,ArrayList<CityAdapterData> cityAdapterDatas) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.activity = activity;
		this.cityAdapterDatas = cityAdapterDatas;
		weatherDatabase = Util.getWeatherDatabase(context);
		
	}
	public int getCount()
	{
		return cityAdapterDatas.size();
	}
	public void removeItem(int position){
		cityAdapterDatas.remove(position);
	}
	public CityAdapterData getDefaultCity(){
		CityAdapterData cityAdapterData = null;
		for(CityAdapterData cityAdapterData2 : cityAdapterDatas){
			if(0 != cityAdapterData2.getDefaultCity()){
				cityAdapterData = cityAdapterData2;
				break;
			}
		}
		return cityAdapterData;
	}
	public CityAdapterData getItem(int position)
	{
		return cityAdapterDatas.get(position);
	}
	
	public long getItemId(int position)
	{
		return position;
	}
	
	public View getView(int position,View view,ViewGroup parent)
	{
		final ViewHolder viewHolder;
			if(view ==null)
			{
				viewHolder = new ViewHolder();
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				view=layoutInflater.inflate(R.layout.gridview_row, null);
				viewHolder.defaultCity = (CheckBox)view.findViewById(R.id.cb_default_city);
				viewHolder.cityname = (TextView)view.findViewById(R.id.cityname);
				viewHolder.high_temperature = (TextView)view.findViewById(R.id.weather_temperature_high);
				viewHolder.low_temperature = (TextView)view.findViewById(R.id.weather_temperature_low);
				viewHolder.weather_icon = (ImageView)view.findViewById(R.id.weather_icon);
				viewHolder.addButton = (Button)view.findViewById(R.id.addbutton);
				view.setTag(viewHolder);
			}
			else {
				viewHolder = (ViewHolder)view.getTag();
			}

		//check display add or weather
	    if(cityAdapterDatas.get(position).isAdd()){
	    	viewHolder.addButton.setVisibility(View.VISIBLE);
	    	viewHolder.defaultCity.setVisibility(View.INVISIBLE);
	    	viewHolder.cityname.setVisibility(View.INVISIBLE);
	    	viewHolder.high_temperature.setVisibility(View.INVISIBLE);
	    	viewHolder.low_temperature.setVisibility(View.INVISIBLE);
	    	viewHolder.weather_icon.setVisibility(View.INVISIBLE);
	    	
	    }else {
	    	viewHolder.addButton.setVisibility(View.INVISIBLE);
	    	viewHolder.defaultCity.setVisibility(View.VISIBLE);
	    	viewHolder.cityname.setVisibility(View.VISIBLE);
	    	viewHolder.high_temperature.setVisibility(View.VISIBLE);
	    	viewHolder.low_temperature.setVisibility(View.VISIBLE);
	    	viewHolder.weather_icon.setVisibility(View.VISIBLE);
	    	
	    	viewHolder.defaultCity.setChecked((0 == cityAdapterDatas.get(position).getDefaultCity() ? false : true));
	    	viewHolder.cityname.setText(cityAdapterDatas.get(position).getCityName());
	    	viewHolder.low_temperature.setText(" ~ "+Util.FashiToSheshi(cityAdapterDatas.get(position).getLowTemperature()) +"℃");
	    	viewHolder.high_temperature.setText(Util.FashiToSheshi(cityAdapterDatas.get(position).getHighTemperature()) +"℃");
	    	viewHolder.weather_icon.setImageResource(Util.getIconID(context, cityAdapterDatas.get(position).getWeatherState()));
		}
			
			
			
		//final int id = position;
//		view.setOnLongClickListener(new View.OnLongClickListener() {
//			
//			public boolean onLongClick(View v) {
//				// TODO Auto-generated method stub
//				
//				Util.print("delete", citynameList.get(id));
//				new AlertDialog.Builder(context)
//				.setTitle(context.getResources().getString(R.string.dialog_title))
//				.setMessage(context.getResources().getString(R.string.dialog_delete_city_message))
//				.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
//					
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						weatherDatabase.deleteData(citynameList.get(id));
//						citynameList.remove(id);
//						notifyDataSetChanged();
//					}
//				})
//				.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
//					
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						
//					}
//				})
//				.show();
//				return true;
//			}
//		});
		/*
		view.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.print("aaaaaaa", "aaaaa");
				if(cityAdapterDatas.get(id).isAdd()){
					Intent intent = new Intent(context,WeatherSearch.class);
					context.startActivity(intent);
				}
			}
		});
		
		viewHolder.addButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.print("bbbbb", "bbbbbbb");
				if(cityAdapterDatas.get(id).isAdd()){
					Intent intent = new Intent(context,WeatherSearch.class);
					context.startActivity(intent);
				}
			}
		});
		
		
		view.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				if(cityAdapterDatas.get(id).isAdd()){
					return false;
				}
				new AlertDialog.Builder(context)
				.setTitle(R.string.confim_delete)
				.setPositiveButton(context.getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(!cityAdapterDatas.get(id).isAdd()){
							weatherDatabase.deleteData(cityAdapterDatas.get(id).getCityName());
							cityAdapterDatas.remove(id);
							notifyDataSetChanged();
						}
					}
				})
				.setNegativeButton(context.getResources().getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				})
				.show();
				
				return false;
			}
		});
		*/
		
//		view.setLayoutParams(new GridView.LayoutParams(272, 205));
		
		
		return view;
		
		
		
		
	}
	
	
	
	
	public class ViewHolder
	{
		TextView high_temperature;
		TextView low_temperature;
		
		ImageView weather_icon;
		Button addButton;
		TextView cityname;
		// 2013.05.05 zsc add
		CheckBox defaultCity;
	}
	
	
	
}