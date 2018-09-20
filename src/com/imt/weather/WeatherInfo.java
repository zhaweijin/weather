package com.imt.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherInfo implements Parcelable {
	// city name
	private String cityName;
	// city code
	private String cityCode;
	// low temperature
	private String lowTemperature;
	// high Temperature
	private String highTemperature;
	// weather state
	private String state;
	
	
	public String getCurrentTemperature() {
		return currentTemperature;
	}

	public void setCurrentTemperature(String currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	private String currentTemperature;
	
	public String getLowTemperature() {
		return lowTemperature;
	}
	
	public void setLowTemperature(String lowTemperature) {
		this.lowTemperature = lowTemperature;
	}
	
	public String getHighTemperature() {
		return highTemperature;
	}
	
	public void setHighTemperature(String highTemperature) {
		this.highTemperature = highTemperature;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getCityName() {
		return cityName;
	}
	
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public String getCityCode() {
		return cityCode;
	}
	
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
			// must order by createFromParcel
			dest.writeString(cityCode);
			dest.writeString(cityName);
			dest.writeString(lowTemperature);
			dest.writeString(highTemperature);
			dest.writeString(state);
	}

	public static final Parcelable.Creator<WeatherInfo> CREATOR = 
			new Creator<WeatherInfo>() {
		@Override
		public WeatherInfo[] newArray(int size) {
			return new WeatherInfo[size];
		}

		@Override
		public WeatherInfo createFromParcel(Parcel source) {
			String cityCode = source.readString();
			String cityName = source.readString();
			String lowTemperature = source.readString();
			String highTemperature = source.readString();
			String state = source.readString();
			WeatherInfo weatherInfo = new WeatherInfo();
			weatherInfo.setCityCode(cityCode);
			weatherInfo.setCityName(cityName);
			weatherInfo.setLowTemperature(lowTemperature);
			weatherInfo.setHighTemperature(highTemperature);
			weatherInfo.setState(state);			
			return weatherInfo;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String toString() { 
		return " cityCode." + cityCode +
				" cityName." + cityName +
				" lowTemperature." + lowTemperature +
				" highTemperature." + highTemperature +
				" state." + state;
	}
}
