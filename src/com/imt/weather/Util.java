package com.imt.weather;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;


import com.imt.weather.data.DataHandle;
import com.imt.weather.data.DataSet;
import com.imt.weather.data.TranslateDatabase;
import com.imt.weather.data.WeatherDatabase;


import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Join;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ListView;

public class Util {

	private static final boolean DEBUG = false;
	private static final String TAG = "Util";
	private static final String KEY_AUTO_CITY = "auto_city";
	private static WeatherDatabase weatherDatabase;
	private static TranslateDatabase translate;
	private static Context mcontext;
	
	public static void print(String tag,String value)
	{
		if(DEBUG) Log.v(tag, value);
	}
	
	 /**
	  * @author zhaweijin
	  * @fucntion get network state
	  */
	   public static boolean getNetworkState(Context context)
	    {
	    	                        
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;

	}

	public static ProgressDialog getProgressDialog(Context context,String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage(message);
		progressDialog.setIcon(R.drawable.icon);
		progressDialog.setIndeterminate(false);
		return progressDialog;
	}
	
	public static int getDisplayMetricsHeight(Activity activity)
	{
		android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}
	
	 
	public static void showDialogAbout(Context context)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getResources().getString(R.string.about) );
		builder.setMessage(context.getResources().getString(R.string.xuzhi_weather)+ "  "  + context.getResources().getString(R.string.version) + ":  " + "2.0" +"\n"+
				"2.0-------------------------------------->" + context.getResources().getString(R.string.about_change1)+"\n"+
				"2.0-------------------------------------->" + context.getResources().getString(R.string.about_change2)+"\n"+
		        "2.0.1-------------------------------------->" + context.getResources().getString(R.string.about_change3)+"\n");
		builder.show();
	}
	
	public static void setListViewHeight(Activity activity,ListView listView,int hasUsedHeight)
	{
		ViewGroup.LayoutParams lParams;
		listView.setLayoutParams(ApplicationConstants.LP_FW);
		listView.setDividerHeight(1);
//		listView.setDivider(context.getResources().getDrawable(R.drawable.mid));
		listView.setCacheColorHint(Color.TRANSPARENT);
		lParams = listView.getLayoutParams();
		
		
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		lParams.height=metrics.heightPixels-hasUsedHeight;
		listView.setLayoutParams(lParams);
		Util.print("he", lParams.height+"");
	}
	
	

	public static DataSet getDataSet(String urlpath) {
		try {

			URL url = new URL(urlpath);
			
			HttpURLConnection mycConnection = (HttpURLConnection) url.openConnection();
			mycConnection.setConnectTimeout(5000);
			mycConnection.setReadTimeout(8000);

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			DataHandle gwh = new DataHandle();
			
			
//			Util.print("result", myConverString(mycConnection.getInputStream()));

			InputStreamReader isr = new InputStreamReader(mycConnection.getInputStream(), "utf-8");
			InputSource is = new InputSource(isr);
			xr.setContentHandler(gwh);

			xr.parse(is);

			return gwh.getDataSet();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v("getwebdataerror", "getwebdataerror");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static String myConverString(InputStream is){
		String line="";
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while((line = reader.readLine())!=null){
				Util.print("****", "****");
				sb.append(line).append("\n");
			}
		} catch (Exception e) {
			// TODO: handle exception
			Util.print("****", "----------");
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	
	
	public static WeatherDatabase getWeatherDatabase(Context context)
	{
		
		if(weatherDatabase==null)
		{
			weatherDatabase = new WeatherDatabase(context);
		}
		return weatherDatabase;
	}
	
	

	public static TranslateDatabase getTranslate(Context context)
	{
		
		if(translate==null)
		{
			translate = new TranslateDatabase(context);
		}
		return translate;
	}
	
	public static void storeData(Context context,DataSet dataSet,String cityname,String citycode, int is_default_city)
	{
		Util.print(TAG, ""+cityname);
		weatherDatabase = Util.getWeatherDatabase(context);
		weatherDatabase.insertData(cityname, citycode, 
				dataSet.getCurrentWeatherData().getCurrentTemperature(), 
				dataSet.getCurrentWeatherData().getSkytext(), 
				dataSet.getForecastWeatherDatas().get(0).getDate(), 
				dataSet.getForecastWeatherDatas().get(0).getDay(), 
				dataSet.getCurrentWeatherData().getObservationtime(),
				dataSet.getCurrentWeatherData().getHumidity(), 
				dataSet.getCurrentWeatherData().getWindspeed(), 
				dataSet.getForecastWeatherDatas().get(0).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(0).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(0).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(0).getDate(), 
				dataSet.getForecastWeatherDatas().get(0).getDay(), 
				dataSet.getForecastWeatherDatas().get(0).getPrecip(), 
				
				dataSet.getForecastWeatherDatas().get(1).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(1).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(1).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(1).getDate(), 
				dataSet.getForecastWeatherDatas().get(1).getDay(), 
				dataSet.getForecastWeatherDatas().get(1).getPrecip(),
				
				dataSet.getForecastWeatherDatas().get(2).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(2).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(2).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(2).getDate(), 
				dataSet.getForecastWeatherDatas().get(2).getDay(), 
				dataSet.getForecastWeatherDatas().get(2).getPrecip(),
				
				dataSet.getForecastWeatherDatas().get(3).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(3).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(3).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(3).getDate(), 
				dataSet.getForecastWeatherDatas().get(3).getDay(), 
				dataSet.getForecastWeatherDatas().get(3).getPrecip(),
		        
				dataSet.getForecastWeatherDatas().get(4).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(4).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(4).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(4).getDate(), 
				dataSet.getForecastWeatherDatas().get(4).getDay(), 
				dataSet.getForecastWeatherDatas().get(4).getPrecip(),
				is_default_city
		);
	}
	
	
	public static void updateData(Context context,DataSet dataSet,String cityname,String citycode)
	{
		weatherDatabase = Util.getWeatherDatabase(context);
		weatherDatabase.updateData(cityname, citycode, 
				dataSet.getCurrentWeatherData().getCurrentTemperature(), 
				dataSet.getCurrentWeatherData().getSkytext(), 
				dataSet.getForecastWeatherDatas().get(0).getDate(), 
				dataSet.getForecastWeatherDatas().get(0).getDay(), 
				dataSet.getCurrentWeatherData().getObservationtime(),
				dataSet.getCurrentWeatherData().getHumidity(), 
				dataSet.getCurrentWeatherData().getWindspeed(), 
				dataSet.getForecastWeatherDatas().get(0).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(0).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(0).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(0).getDate(), 
				dataSet.getForecastWeatherDatas().get(0).getDay(), 
				dataSet.getForecastWeatherDatas().get(0).getPrecip(), 
				
				dataSet.getForecastWeatherDatas().get(1).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(1).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(1).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(1).getDate(), 
				dataSet.getForecastWeatherDatas().get(1).getDay(), 
				dataSet.getForecastWeatherDatas().get(1).getPrecip(),
				
				dataSet.getForecastWeatherDatas().get(2).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(2).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(2).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(2).getDate(), 
				dataSet.getForecastWeatherDatas().get(2).getDay(), 
				dataSet.getForecastWeatherDatas().get(2).getPrecip(),
				
				dataSet.getForecastWeatherDatas().get(3).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(3).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(3).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(3).getDate(), 
				dataSet.getForecastWeatherDatas().get(3).getDay(), 
				dataSet.getForecastWeatherDatas().get(3).getPrecip(),
		        
				dataSet.getForecastWeatherDatas().get(4).getLowTemperature(),
				dataSet.getForecastWeatherDatas().get(4).getHighTemperature(), 
				dataSet.getForecastWeatherDatas().get(4).getSkytextday(), 
				dataSet.getForecastWeatherDatas().get(4).getDate(), 
				dataSet.getForecastWeatherDatas().get(4).getDay(), 
				dataSet.getForecastWeatherDatas().get(4).getPrecip()
		);
	}
	
	
	public static String translateWeek(Context context,String week)
	{
		if(week.compareToIgnoreCase("Monday")==0)
			return context.getResources().getString(R.string.Monday);
		else if(week.compareToIgnoreCase("Tuesday")==0)
			return context.getResources().getString(R.string.Tuesday);
		else if(week.compareToIgnoreCase("Wednesday")==0)
			return context.getResources().getString(R.string.Wednesday);
		else if(week.compareToIgnoreCase("Thursday")==0)
			return context.getResources().getString(R.string.Thursday);
		else if(week.compareToIgnoreCase("Friday")==0)
			return context.getResources().getString(R.string.Friday);
		else if(week.compareToIgnoreCase("Saturday")==0)
			return context.getResources().getString(R.string.Saturday);
		else if(week.compareToIgnoreCase("Sunday")==0)
			return context.getResources().getString(R.string.Sunday);
		else
			return context.getResources().getString(R.string.Monday);
			
	}
	
	public static String getLocaleLanguage() {
		Locale l = Locale.getDefault();
		return String.format("%s-%s", l.getLanguage(), l.getCountry());
		}
	
	public static String translateCityAndCuntry(Context context,String cityandcountry)
	{
		String city;
		String country;
		Cursor cursor;
		TranslateDatabase translate;
		if(cityandcountry.split(",").length==2)
		{
			city = cityandcountry.split(",")[0].trim();
			country = cityandcountry.split(",")[1].trim();
			
			if(country.compareToIgnoreCase("CHN")==0)
			{
				country = context.getResources().getString(R.string.CHN);
			}
			
            translate = getTranslate(context);
            cursor = translate.queryAllData();
            Util.print("translate_size", cursor.getCount()+"");
            int size = cursor.getCount();
            boolean finded = false;
            while(cursor.moveToNext())
            {
//            	Util.print("city_pingyin and name ", cursor.getString(cursor.getColumnIndexOrThrow(Translate.KEY_PINYIN))+
//        				cursor.getString(cursor.getColumnIndexOrThrow(Translate.KEY_NAME)));
            	if(city.compareToIgnoreCase(cursor.getString(cursor.getColumnIndexOrThrow(TranslateDatabase.KEY_PINYIN)))==0)
            	{
            		city = cursor.getString(cursor.getColumnIndexOrThrow(TranslateDatabase.KEY_NAME));
            		finded = true;
            		break;
            	}		
            }
            cursor.close();
            
            Util.print("finded", Boolean.toString(finded));
            if(!finded)
            {
            	try {
            		Util.print("name", Util.GetGoogleTranslateJSONString(city, "en", "zh-CN", "utf-8"));
            		String tempcity = city;
            		city = Util.GetGoogleTranslateJSONString(city, "en", "zh-CN", "utf-8");
            		if(city==null)
            		{
            			city = tempcity;
            		}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
            	
            }
            
            Util.print("city", city);
            return city+" , "+country;
        
		}
		else {
			return cityandcountry;
		}
	}
	
	
	
	public static String GetGoogleTranslateJSONString(
			String strTranslateString, String strRequestLanguage,
			String strResultLanguage, String encoding) throws Exception {
		URL url = new URL(
				"http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q="
						+ strTranslateString + "&langpair="
						+ strRequestLanguage + "%7C" + strResultLanguage + "");
		Util.print("url", "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&q="
				+ strTranslateString + "&langpair="
				+ strRequestLanguage + "%7C" + strResultLanguage + "");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		// request GET 200，
		// post 206
		if (conn.getResponseCode() == 200) {
			InputStream inStream = conn.getInputStream();
			byte[] data = readStream(inStream);
			String result = new String(data, encoding);
			Util.print("result", result);
			JSONObject jsonObject = new JSONObject(result).getJSONObject("responseData");
//			JSONObject jsonObject2 = new JSONObject(result).get("responseStatus");
            int translateResponse = new JSONObject(result).getInt("responseStatus");
            if(translateResponse==200)
            	return jsonObject.getString("translatedText");
            else
            	return null;
		}
		return null;
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}
    
	public static String translateWeatherDetail(Context context,String detail)
	{
		if(detail.toLowerCase().contains("cloud"))
			detail = context.getResources().getString(R.string.clouds);
		else if(detail.toLowerCase().contains("fair") || detail.toLowerCase().contains("fine") || 
				detail.toLowerCase().contains("clear") || detail.toLowerCase().contains("sun"))
			detail = context.getResources().getString(R.string.clear);
		else if(detail.toLowerCase().contains("fog"))
			detail = context.getResources().getString(R.string.fog);
		else if(detail.toLowerCase().contains("dust"))
			detail = context.getResources().getString(R.string.dust);
		else if(detail.toLowerCase().contains("haze"))
			detail = context.getResources().getString(R.string.haze);
		else if(detail.toLowerCase().contains("smoke"))
			detail = context.getResources().getString(R.string.smoke);
		else if(detail.toLowerCase().contains("shower"))
			detail = context.getResources().getString(R.string.shower);
		else if(detail.toLowerCase().contains("rain"))
			detail = context.getResources().getString(R.string.rain);
		else if(detail.toLowerCase().contains("sprinkles"))
			detail = context.getResources().getString(R.string.sprinkles);
		else if(detail.toLowerCase().contains("thunderstorm") || detail.toLowerCase().contains("storms"))
			detail = context.getResources().getString(R.string.thunderstorm);
		else if(detail.toLowerCase().contains("snowshowers"))
			detail = context.getResources().getString(R.string.snowshowers);
		else if(detail.toLowerCase().contains("sleet"))
			detail = context.getResources().getString(R.string.sleet);
		else if(detail.toLowerCase().contains("flurry"))
			detail = context.getResources().getString(R.string.flurry);
		else if(detail.toLowerCase().contains("snow"))
			detail = context.getResources().getString(R.string.snow);
		else if(detail.toLowerCase().contains("blizzard"))
			detail = context.getResources().getString(R.string.blizzard);
		else if(detail.toLowerCase().contains("wind"))
			detail = context.getResources().getString(R.string.wind);
		else if(detail.toLowerCase().contains("gust"))
			detail = context.getResources().getString(R.string.gust);
		else if(detail.toLowerCase().contains("thunder"))
			detail = context.getResources().getString(R.string.thunder);
		else if(detail.toLowerCase().contains("storm"))
			detail = context.getResources().getString(R.string.storm);
		else if(detail.toLowerCase().contains("partly"))
			detail = context.getResources().getString(R.string.partly);
		else if(detail.toLowerCase().contains("storm"))
			detail = context.getResources().getString(R.string.storm);
		else if(detail.toLowerCase().contains("mostly"))
			detail = context.getResources().getString(R.string.mostly);
		else if(detail.toLowerCase().contains("scattered"))
			detail = context.getResources().getString(R.string.scattered);
		else if(detail.toLowerCase().contains("strong"))
			detail = context.getResources().getString(R.string.strong);
		else if(detail.toLowerCase().contains("weak"))
			detail = context.getResources().getString(R.string.weak);
		else if(detail.toLowerCase().contains("hot"))
			detail = context.getResources().getString(R.string.hot);
		else if(detail.toLowerCase().contains("icy"))
			detail = context.getResources().getString(R.string.icy);
		else if(detail.toLowerCase().contains("frigid"))
			detail = context.getResources().getString(R.string.frigid);
		else if(detail.toLowerCase().contains("drizzle"))
			detail = context.getResources().getString(R.string.Drizzle);
		else if(detail.toLowerCase().contains("hail"))
			detail = context.getResources().getString(R.string.Hail);
		else if(detail.toLowerCase().contains("sprinkles"))
			detail = context.getResources().getString(R.string.Sprinkles);
		
		return detail;
	}
	
	
	public static boolean  copyDatabaseFile(InputStream inputStream)
	 {
		 try {
             String newparentpath = "data/data/com.imt.weather/databases";
             String path  = newparentpath +"/translate";
			 byte[] temp = new byte[1024];
			 File dirFile = new File(newparentpath);
			 if(!dirFile.exists())
			     dirFile.mkdirs();
			 File file = new File(path);
			 if(!file.exists())
				 file.createNewFile();

				 int length;
				 FileOutputStream fos = new FileOutputStream(path);
				 while ((length =inputStream.read(temp))!=-1) {
					fos.write(temp, 0, length);
				}
				 fos.flush();
				 fos.close();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
      return true;
	 }
	
	
	public  static int getIconID(Context context,String detail)
	{
		Util.print("detail", "weather sky===="+detail);
		if(detail.toLowerCase().contains("cloud") || detail.toLowerCase().contains("partly") 
				|| detail.toLowerCase().contains("mostly"))
		{
				return R.drawable.cloudy;
		}	
		else if(detail.toLowerCase().contains("fair") || detail.toLowerCase().contains("fine") || 
				detail.toLowerCase().contains("clear") || detail.toLowerCase().contains("sun"))
		{
				return R.drawable.clear;
		}
		else if(detail.toLowerCase().contains("dust") || detail.toLowerCase().contains("haze") ||
				detail.toLowerCase().contains("smoke") || detail.toLowerCase().contains("fog"))
		{
				return R.drawable.fog;
		}
		else if(detail.toLowerCase().contains("rain") || detail.toLowerCase().contains("sprinkles"))
		{
				return R.drawable.rain_mid;
		}
		else if(detail.toLowerCase().contains("shower")){
			return R.drawable.shower;
		}
		else if(detail.toLowerCase().contains("thunderstorm") || detail.toLowerCase().contains("storms"))
		{		
				return R.drawable.thunderstrom;
		}
		else if(detail.toLowerCase().contains("flurry") || detail.toLowerCase().contains("snow") ||
				detail.toLowerCase().contains("blizzard") || detail.toLowerCase().contains("snowshowers") 
				|| detail.toLowerCase().contains("sleet") ||
				detail.toLowerCase().contains("icy"))
		{
				return R.drawable.snow_big;
		}
		else if(detail.toLowerCase().contains("wind") || detail.toLowerCase().contains("gust"))
		{
				return R.drawable.wind;
		}
		else if(detail.toLowerCase().contains("thunder") || detail.toLowerCase().contains("storm"))
		{
				return R.drawable.thunderstrom;	
		}
		else if(detail.toLowerCase().contains("frigid") || detail.toLowerCase().contains("drizzle") ||
				detail.toLowerCase().contains("hail"))
		{
				return R.drawable.hail;
		}
		return R.drawable.icon;
	}
	
	
	
	
    
    public static String getCurrentCity(Context context)
    {
    	LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        mcontext = context;
        locationManager = (LocationManager)context.getSystemService(serviceName);

        String provider = locationManager.NETWORK_PROVIDER;
        
//      Criteria criteria = new Criteria();
//      criteria.setAccuracy(Criteria.ACCURACY_FINE);
//      criteria.setAltitudeRequired(false);
//      criteria.setBearingRequired(false);
//      criteria.setCostAllowed(true);
//      criteria.setPowerRequirement(Criteria.POWER_LOW);
     
      //String provider = locationManager.getBestProvider(criteria,true);
       
       Location location = locationManager.getLastKnownLocation(provider);

       String a = updateWithNewLocation(location);
       return a;
       // locationManager.requestLocationUpdates(provider, 2000, 10,locationListener);
    }

   private final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {updateWithNewLocation(location);
            }
            public void onProviderDisabled(String provider){updateWithNewLocation(null);
            }
            
            public void onProviderEnabled(String provider){ }
            public void onStatusChanged(String provider, int status,
            Bundle extras){ }
            
    };
    private static String updateWithNewLocation(Location location) {
            String latLongString = "";
            List<Address> addresses = new ArrayList<Address>();
            
            Log.v("cc", "cc");
            if (location != null) 
            
            {
               double lat = location.getLatitude();
               double lng = location.getLongitude();
            
               Geocoder code = new  Geocoder(mcontext);
               try {
            	   //addresses = code.getFromLocation(21.330000, 115.070000, 100);
            	   //addresses = code.getFromLocation(25.030010, 121.301000, 100);
            	   addresses = code.getFromLocation(39.550110, 116.241200, 100);
            	   //addresses = code.getFromLocation(lat, lng, 100);
			   } catch (Exception e) {
				// TODO: handle exception
			    }
              
          
             	String AdminArea = addresses.get(0).getAdminArea();
            	String countryName = addresses.get(0).getCountryName();
            	String countryCode = addresses.get(0).getCountryCode();
//              	String mFeathre = addresses.get(0).getFeatureName();
//            	String loc = addresses.get(0).getLocality();
            	
            	
//            	if(countryCode=="HK" || countryCode =="MO")
//            		return countryName;
//            	
//            	else
//            		return loc.substring(0, loc.length()-1);
			   return "a";	
            } else 
            {
                latLongString = "aa";
                return latLongString;
            }
           
    }
    
	public static String getSystemDataFormat(Context context) {
		ContentResolver cv = context.getContentResolver();
		String dateString = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.DATE_FORMAT);
		if(dateString!=null)
		{
			if (!dateString.equals("")) {
				return dateString;
			} else {
				return "yyyy-MM-dd";
			}
		}
		return "yyyy-MM-dd";

	}
	
	public static String translateNetworkTimeToLocalTime(Context context,String dateString)
	{
	    String[] dayStrings = dateString.split("-");
	   
	    Date date = new Date(Integer.parseInt(dayStrings[0])-1900,Integer.parseInt(dayStrings[1])-1,
	    		Integer.parseInt(dayStrings[2]));
	    java.text.DateFormat df = new java.text.SimpleDateFormat(getSystemDataFormat(context));
	    return df.format(date);
	}
  
	
	public static  boolean is24HourFormat(Context context) {
		ContentResolver cv = context.getContentResolver();
		String strTimeFormat = android.provider.Settings.System.getString(cv,
				android.provider.Settings.System.TIME_12_24);
		if(strTimeFormat!=null)
		{
			if (strTimeFormat.equals("24"))
			{
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	
	
	public static String FashiToSheshi(String fashi){
		if(fashi!=null && !fashi.equals("")){
			String sheshi = ""+(int)((Integer.parseInt(fashi)-32)*5/(float)9);
			return sheshi;
		}
		return "";
	}
	
	public static String getLocation(){
	    //for globe locating
		URL url;
		URLConnection conn = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String str = "";

		try {
			url = new URL("http://j.maxmind.com/app/geoip.js");
			conn = url.openConnection();
			is = conn.getInputStream();
			isr = new InputStreamReader(is,"gb2312");
			br = new BufferedReader(isr);
			String input = "";
			while ((input = br.readLine()) != null) {
				str += input;
			}

			String content = new String(str.getBytes("UTF-8"),"UTF-8");



			String [] list = content.split("'");
			
			if(DEBUG){
				for (int i = 0; i < list.length; i++) {
	
					Log.d(TAG, "   " + i + " = " + list[i]);
				}		
			}
			/*
			 	0 = function geoip_country_code() { return 
				1 = CN
				2 = ; }function geoip_country_name() { return 
				3 = China
				4 = ; }function geoip_city()         { return 
				5 = Guangzhou
				6 = ; }function geoip_region()       { return 
				7 = 30
				8 = ; }function geoip_region_name()  { return 
				9 = Guangdong
				10 = ; }function geoip_latitude()     { return 
				11 = 23.1167
				12 = ; }function geoip_longitude()    { return 
				13 = 113.2500
				14 = ; }function geoip_postal_code()  { return 
				15 = 
				16 = ; }function geoip_area_code()    { return 
				17 = 
				18 = ; }function geoip_metro_code()   { return 
				19 = 
				20 = ; }
			 */
			// 2013.08.14 zsc add for geoip_city() => geoip_region_name() => geoip_country_name()
			if(null == list[5] || list[5].isEmpty()){
				if(null == list[9] || list[9].isEmpty()){
					return list[3];
				}
				return list[9];
			}
			return list[5];

		} catch (Exception e) {
			e.printStackTrace();
		}	

		return null;		
	}
	
	public static boolean getAutoCity(Context context){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);        	
        return preferences.getBoolean(KEY_AUTO_CITY, true);
    }
    
    public static void setAutoCity(boolean is_checked, Context context){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);        	
       	SharedPreferences.Editor editor = preferences.edit();
       	editor.putBoolean(KEY_AUTO_CITY, is_checked);
		editor.apply();       	
    }

    public static boolean hasDefaultCity(Context context) {
        WeatherDatabase weatherDatabase = getWeatherDatabase(context);
        return weatherDatabase.hasDefaultCity();
    }
    
    
    
    public static String getLocationCityBySina(){
    	
//    	Util.print(TAG, "aa");
    	URL url;
		URLConnection conn = null;
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		String str = "";
        String re = "";
		try {
			url = new URL("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json");
			conn = url.openConnection();
			is = conn.getInputStream();
			isr = new InputStreamReader(is,"gb2312");
			br = new BufferedReader(isr);
			String input = "";
			while ((input = br.readLine()) != null) {
				str += input;
			}
			String content = new String(str.getBytes("UTF-8"),"UTF-8");
 
			JSONObject jsonobject=new JSONObject(content);
			 
			re = (String)jsonobject.get("city");
//			Log.v(TAG, ">>"+jsonobject.get("city"));

		}catch (Exception e) {
			// TODO: handle exception
        	e.printStackTrace();
		}
		
		return re;
 
    }
 
}
