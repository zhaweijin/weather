package com.imt.weather;

import java.util.ArrayList;

import com.imt.weather.data.City;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
	// 2013.05.07 zsc modify cityName => city
	//private ArrayList<String> searchDatas;
	private ArrayList<City> searchDatas;
	private Context context;

	public SearchAdapter(Context context,ArrayList<City> searchDatas) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.searchDatas = searchDatas;

	}

	public int getCount() {
		return searchDatas.size();
	}

	public Object getItem(int position) {
		return searchDatas.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (view == null) {
			viewHolder = new ViewHolder();
			LayoutInflater layoutInflater = LayoutInflater.from(context);
			view = layoutInflater.inflate(R.layout.search_row, null);
			viewHolder.name = (TextView) view.findViewById(R.id.name);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		City city = searchDatas.get(position);
		viewHolder.name.setText(city.getWeatherLocationName() + "   " + 
						city.getWeatherLocationCode());	
		return view;

	}

	public class ViewHolder {
		TextView name;
	}

}