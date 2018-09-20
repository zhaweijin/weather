package com.imt.weather.data;


import com.imt.weather.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

import java.lang.Exception;

/**
 * @author zhaweijin
 * @function 
 */
public  class  WeatherDatabase {
    private static final String TAG = "Weather";

	//common
    public final static String KEY_ROWID = "_id";
	public final static String KEY_CITY_NAME = "cityname";
    public final static String KEY_CITY_CODE = "citycode";
    //current weather
    public final static String KEY_CURRENT_TEMPERATURE = "current_temperature";
    public final static String KEY_CURRENT_SKEYTEXT = "current_skytext";
    public final static String KEY_CURRENT_DATE = "current_date";
    public final static String KEY_CURRENT_DAY = "current_day";
    public final static String KEY_CURRENT_OBSERVATIONTIME = "current_obervationtime";
    public final static String KEY_CURRENT_HUMIDITY ="current_humidity";
    public final static String KEY_CURRENT_WINDSPEED = "current_windspeed";
    
    //furture weather 1
    public final static String KEY_FORECAST_LOW_DATA1 ="forecast_low_data1";
    public final static String KEY_FORECAST_HIGH_DATA1 = "forecast_high_data1";
    public final static String KEY_FORECAST_SKYTEXTDAY_DATA1 = "forecast_skytextday_data1";
    public final static String KEY_FORECAST_DATE_DATA1 = "forecast_date_data1";
    public final static String KEY_FORECAST_DAY_DATA1 = "forecast_day_data1";
    public final static String KEY_FORECAST_PRECIP_DATA1 = "forecast_precip_data1";
    
    //furture weather 2
    public final static String KEY_FORECAST_LOW_DATA2 ="forecast_low_data2";
    public final static String KEY_FORECAST_HIGH_DATA2 = "forecast_high_data2";
    public final static String KEY_FORECAST_SKYTEXTDAY_DATA2 = "forecast_skytextday_data2";
    public final static String KEY_FORECAST_DATE_DATA2 = "forecast_date_data2";
    public final static String KEY_FORECAST_DAY_DATA2 = "forecast_day_data2";
    public final static String KEY_FORECAST_PRECIP_DATA2 = "forecast_precip_data2";
    
    //furture weather 3
    public final static String KEY_FORECAST_LOW_DATA3 ="forecast_low_data3";
    public final static String KEY_FORECAST_HIGH_DATA3 = "forecast_high_data3";
    public final static String KEY_FORECAST_SKYTEXTDAY_DATA3 = "forecast_skytextday_data3";
    public final static String KEY_FORECAST_DATE_DATA3 = "forecast_date_data3";
    public final static String KEY_FORECAST_DAY_DATA3 = "forecast_day_data3";
    public final static String KEY_FORECAST_PRECIP_DATA3 = "forecast_precip_data3";
    
    
    //furture weather 4
    public final static String KEY_FORECAST_LOW_DATA4 ="forecast_low_data4";
    public final static String KEY_FORECAST_HIGH_DATA4 = "forecast_high_data4";
    public final static String KEY_FORECAST_SKYTEXTDAY_DATA4 = "forecast_skytextday_data4";
    public final static String KEY_FORECAST_DATE_DATA4 = "forecast_date_data4";
    public final static String KEY_FORECAST_DAY_DATA4 = "forecast_day_data4";
    public final static String KEY_FORECAST_PRECIP_DATA4 = "forecast_precip_data4";
    
    //furture weather 5
    public final static String KEY_FORECAST_LOW_DATA5 ="forecast_low_data5";
    public final static String KEY_FORECAST_HIGH_DATA5 = "forecast_high_data5";
    public final static String KEY_FORECAST_SKYTEXTDAY_DATA5 = "forecast_skytextday_data5";
    public final static String KEY_FORECAST_DATE_DATA5 = "forecast_date_data5";
    public final static String KEY_FORECAST_DAY_DATA5 = "forecast_day_data5";
    public final static String KEY_FORECAST_PRECIP_DATA5 = "forecast_precip_data5";
    // 2013.05.05 zsc add
    public final static String KEY_IS_DEFAULT_CITY = "is_default_city";

    public final static int ID_INVALID = -1;
    public final static int POSITION_INVALID = -1;

    
    
    private SQLiteDatabase db;
    // 2013.05.06 zsc modify DATABASE_NAME = DATABASE_NAME + DATABASE_VERSION;
    private final static String DATABASE_NAME = "weather_01";
    private final static String TABLE_NAME = "weather";
    
    
    private final static String CREATE_DATABASE = "create table if not exists " +TABLE_NAME +"(" +
      "_id Integer primary key autoincrement," +
      "cityname text not null," +
      "citycode text," +
      //current weather
      "current_temperature text," +              
      "current_skytext text," +
      "current_date text," +
      "current_day text," +
      "current_obervationtime text," +
      "current_humidity text," +
      "current_windspeed text," +
      //furture weather 1
      "forecast_low_data1 text," +
      "forecast_high_data1 text," +
      "forecast_skytextday_data1 text," +
      "forecast_date_data1 text," +
      "forecast_day_data1 text," +
      "forecast_precip_data1 text," +
      //furture weather 2
      "forecast_low_data2 text," +
      "forecast_high_data2 text," +
      "forecast_skytextday_data2 text," +
      "forecast_date_data2 text," +
      "forecast_day_data2 text," +
      "forecast_precip_data2 text," +
      //furture weather 3
      "forecast_low_data3 text," +
      "forecast_high_data3 text," +
      "forecast_skytextday_data3 text," +
      "forecast_date_data3 text," +
      "forecast_day_data3 text," +
      "forecast_precip_data3 text," +
      //furture weather 4
      "forecast_low_data4 text," +
      "forecast_high_data4 text," +
      "forecast_skytextday_data4 text," +
      "forecast_date_data4 text," +
      "forecast_day_data4 text," +
      "forecast_precip_data4 text," +
      //furture weather 5
      "forecast_low_data5 text," +
      "forecast_high_data5 text," +
      "forecast_skytextday_data5 text," +
      "forecast_date_data5 text," +
      "forecast_day_data5 text," +
      "forecast_precip_data5 text," +
      "is_default_city integer" +
      " )";
    
    Context mContext;
    public WeatherDatabase(Context context)
    {
    	this.mContext = context;
    	db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        try {
			  db.execSQL(CREATE_DATABASE);
			  Util.print("weather", "Create weather table ok");   
		} catch ( Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Util.print("table is exist","table is exist");
		}
    }
    

	public void insertData(String cityname,String citycode,
			//current
			String current_temperature,String current_skytext,String current_date,String current_day,
			String current_obervationtime,String current_humidity,String current_windspeed,
			//forecast1
			String forecast_low_data1,String forecast_high_data1,String forecast_skytextday_data1,
			String forecast_date_data1,String forecast_day_data1,String forecast_precip_data1,
			//forecast2
			String forecast_low_data2,String forecast_high_data2,String forecast_skytextday_data2,
			String forecast_date_data2,String forecast_day_data2,String forecast_precip_data2,
			//forecast3
			String forecast_low_data3,String forecast_high_data3,String forecast_skytextday_data3,
			String forecast_date_data3,String forecast_day_data3,String forecast_precip_data3,
			//forecast4
			String forecast_low_data4,String forecast_high_data4,String forecast_skytextday_data4,
			String forecast_date_data4,String forecast_day_data4,String forecast_precip_data4,
			//forecast5
			String forecast_low_data5,String forecast_high_data5,String forecast_skytextday_data5,
			String forecast_date_data5,String forecast_day_data5,String forecast_precip_data5,
			int is_default_city
	        )
    {
    	ContentValues contentValues = new ContentValues();
    	contentValues.put(KEY_CITY_NAME, cityname);
    	contentValues.put(KEY_CITY_CODE, citycode);   
    	//current
    	contentValues.put(KEY_CURRENT_DATE,current_date);
    	contentValues.put(KEY_CURRENT_DAY,current_day);
    	contentValues.put(KEY_CURRENT_HUMIDITY,current_humidity);
    	contentValues.put(KEY_CURRENT_OBSERVATIONTIME,current_obervationtime);
    	contentValues.put(KEY_CURRENT_SKEYTEXT,current_skytext);
    	contentValues.put(KEY_CURRENT_TEMPERATURE,current_temperature);
    	contentValues.put(KEY_CURRENT_WINDSPEED,current_windspeed);
    	//forecast1
    	contentValues.put(KEY_FORECAST_DATE_DATA1,forecast_date_data1);
    	contentValues.put(KEY_FORECAST_DAY_DATA1,forecast_day_data1);
    	contentValues.put(KEY_FORECAST_HIGH_DATA1,forecast_high_data1);
    	contentValues.put(KEY_FORECAST_LOW_DATA1,forecast_low_data1);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA1,forecast_precip_data1);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA1,forecast_skytextday_data1);
    	//forecast2
    	contentValues.put(KEY_FORECAST_DATE_DATA2,forecast_date_data2);
    	contentValues.put(KEY_FORECAST_DAY_DATA2,forecast_day_data2);
    	contentValues.put(KEY_FORECAST_HIGH_DATA2,forecast_high_data2);
    	contentValues.put(KEY_FORECAST_LOW_DATA2,forecast_low_data2);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA2,forecast_precip_data2);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA2,forecast_skytextday_data2);
    	//forecast3
    	contentValues.put(KEY_FORECAST_DATE_DATA3,forecast_date_data3);
    	contentValues.put(KEY_FORECAST_DAY_DATA3,forecast_day_data3);
    	contentValues.put(KEY_FORECAST_HIGH_DATA3,forecast_high_data3);
    	contentValues.put(KEY_FORECAST_LOW_DATA3,forecast_low_data3);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA3,forecast_precip_data3);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA3,forecast_skytextday_data3);
    	//forecast4
    	contentValues.put(KEY_FORECAST_DATE_DATA4,forecast_date_data4);
    	contentValues.put(KEY_FORECAST_DAY_DATA4,forecast_day_data4);
    	contentValues.put(KEY_FORECAST_HIGH_DATA4,forecast_high_data4);
    	contentValues.put(KEY_FORECAST_LOW_DATA4,forecast_low_data4);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA4,forecast_precip_data4);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA4,forecast_skytextday_data4);
    	//forecast5
    	contentValues.put(KEY_FORECAST_DATE_DATA5,forecast_date_data5);
    	contentValues.put(KEY_FORECAST_DAY_DATA5,forecast_day_data5);
    	contentValues.put(KEY_FORECAST_HIGH_DATA5,forecast_high_data5);
    	contentValues.put(KEY_FORECAST_LOW_DATA5,forecast_low_data5);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA5,forecast_precip_data5);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA5,forecast_skytextday_data5);
    	
    	// is_default_city
    	contentValues.put(KEY_IS_DEFAULT_CITY, is_default_city);
        try {
        	 db.insert(TABLE_NAME, null, contentValues);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    

    public void deleteData(String cityname)
    {
    	try {
    		String sqlString = "delete from " + TABLE_NAME + " where " +KEY_CITY_NAME +" =?";
    		Util.print("sqlstring", sqlString);
    		
    		Cursor cursor = querySingleData(cityname);
    		int size = cursor.getCount();
    		if(size>1)
    		{
    			cursor.moveToLast();
    			int rowid = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ROWID));
    			deleteDataByID(rowid);
    		}
    		else {
    			db.execSQL(sqlString,new String[]{cityname});
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
    }
 
    
    
    public void deleteDataByID(int rowid)
    {
    	try {
    		String sqlString = "delete from " + TABLE_NAME + " where " +KEY_ROWID +" =?";
    		Util.print("sqlstring", sqlString);
    		db.execSQL(sqlString,new Object[]{rowid});
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    	
    }
    
    public void deleteAllData()
    {
    	try {
    		String sqlString = "delete from " + TABLE_NAME;
    		Util.print("sqlstring", sqlString);
   			db.execSQL(sqlString);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}	
    }
   
    public Cursor queryAllData()
    {
    	Cursor cursor=null;
    	String sqlString = "select * from " + TABLE_NAME; 
    	Util.print("sqlstring", sqlString);
    	try {
    		cursor = db.rawQuery(sqlString, null);
    	}catch (Exception e) {
			// TODO: handle exception
    		cursor.close();
    		e.printStackTrace();
		}
    	return cursor;
    }
    
    
    public Cursor queryAllIDData()
    {
    	Cursor cursor=null;
    	String sqlString = "select "+KEY_ROWID + " from " + TABLE_NAME; 
    	Util.print("sqlstring", sqlString);
    	try {
    		cursor = db.rawQuery(sqlString,null);
    	}catch (Exception e) {
			// TODO: handle exception
    		cursor.close();
    		e.printStackTrace();
		}
    	return cursor;
    }

    public int getDefCityPosition() {
        int defCityPosition = POSITION_INVALID;
        if (db == null) {
            return POSITION_INVALID;
        }
        Cursor allCitiesCursor = this.queryAllData();
        if (!allCitiesCursor.moveToFirst()) {
            allCitiesCursor.close();
            return POSITION_INVALID;
        }
        do {
            int isDefCity = allCitiesCursor.getInt(allCitiesCursor.getColumnIndexOrThrow(KEY_IS_DEFAULT_CITY));
            if (isDefCity == 1) {
                defCityPosition = allCitiesCursor.getPosition();
                allCitiesCursor.close();
                return defCityPosition;
            }
        } while (allCitiesCursor.moveToNext());
        allCitiesCursor.close();
        return defCityPosition;
    }
	

    public Cursor querySingleData(String cityname)
    {
    	Cursor cursor=null;
    	String sqlString = "select * from " + TABLE_NAME +" where "+ KEY_CITY_NAME + " =?"; 
    	Util.print("sqlstring", sqlString);
    	try {
			cursor = db.rawQuery(sqlString, new String[]{cityname});
		} catch (Exception e) {
			// TODO: handle exception
			cursor.close();
			e.printStackTrace();
		}
    	return cursor;	
    }

    public boolean hasDefaultCity() {
        if (queryDefaultData() != null) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public Cursor queryDefaultData()
    {
    	Cursor cursor=null;
    	String sqlString = "select * from " + TABLE_NAME +" where "+ KEY_IS_DEFAULT_CITY + " = 1"; 
    	Util.print("sqlstring", sqlString);
    	try {
			cursor = db.rawQuery(sqlString, null);
		} catch (Exception e) {
			// TODO: handle exception
			cursor.close();
			e.printStackTrace();
		}
    	return cursor;	
    }
    
    public void close()
    {
    	db.close();
    }

   
    public void updateData(String cityname,String citycode,
			//current
			String current_temperature,String current_skytext,String current_date,String current_day,
			String current_obervationtime,String current_humidity,String current_windspeed,
			//forecast1
			String forecast_low_data1,String forecast_high_data1,String forecast_skytextday_data1,
			String forecast_date_data1,String forecast_day_data1,String forecast_precip_data1,
			//forecast2
			String forecast_low_data2,String forecast_high_data2,String forecast_skytextday_data2,
			String forecast_date_data2,String forecast_day_data2,String forecast_precip_data2,
			//forecast3
			String forecast_low_data3,String forecast_high_data3,String forecast_skytextday_data3,
			String forecast_date_data3,String forecast_day_data3,String forecast_precip_data3,
			//forecast4
			String forecast_low_data4,String forecast_high_data4,String forecast_skytextday_data4,
			String forecast_date_data4,String forecast_day_data4,String forecast_precip_data4,
			//forecast5
			String forecast_low_data5,String forecast_high_data5,String forecast_skytextday_data5,
			String forecast_date_data5,String forecast_day_data5,String forecast_precip_data5
	        )
    {
    	ContentValues contentValues = new ContentValues();
    	contentValues.put(KEY_CITY_NAME, cityname);
    	contentValues.put(KEY_CITY_CODE, citycode);   
    	//current
    	contentValues.put(KEY_CURRENT_DATE,current_date);
    	contentValues.put(KEY_CURRENT_DAY,current_day);
    	contentValues.put(KEY_CURRENT_HUMIDITY,current_humidity);
    	contentValues.put(KEY_CURRENT_OBSERVATIONTIME,current_obervationtime);
    	contentValues.put(KEY_CURRENT_SKEYTEXT,current_skytext);
    	contentValues.put(KEY_CURRENT_TEMPERATURE,current_temperature);
    	contentValues.put(KEY_CURRENT_WINDSPEED,current_windspeed);
    	//forecast1
    	contentValues.put(KEY_FORECAST_DATE_DATA1,forecast_date_data1);
    	contentValues.put(KEY_FORECAST_DAY_DATA1,forecast_day_data1);
    	contentValues.put(KEY_FORECAST_HIGH_DATA1,forecast_high_data1);
    	contentValues.put(KEY_FORECAST_LOW_DATA1,forecast_low_data1);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA1,forecast_precip_data1);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA1,forecast_skytextday_data1);
    	//forecast2
    	contentValues.put(KEY_FORECAST_DATE_DATA2,forecast_date_data2);
    	contentValues.put(KEY_FORECAST_DAY_DATA2,forecast_day_data2);
    	contentValues.put(KEY_FORECAST_HIGH_DATA2,forecast_high_data2);
    	contentValues.put(KEY_FORECAST_LOW_DATA2,forecast_low_data2);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA2,forecast_precip_data2);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA2,forecast_skytextday_data2);
    	//forecast3
    	contentValues.put(KEY_FORECAST_DATE_DATA3,forecast_date_data3);
    	contentValues.put(KEY_FORECAST_DAY_DATA3,forecast_day_data3);
    	contentValues.put(KEY_FORECAST_HIGH_DATA3,forecast_high_data3);
    	contentValues.put(KEY_FORECAST_LOW_DATA3,forecast_low_data3);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA3,forecast_precip_data3);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA3,forecast_skytextday_data3);
    	//forecast4
    	contentValues.put(KEY_FORECAST_DATE_DATA4,forecast_date_data4);
    	contentValues.put(KEY_FORECAST_DAY_DATA4,forecast_day_data4);
    	contentValues.put(KEY_FORECAST_HIGH_DATA4,forecast_high_data4);
    	contentValues.put(KEY_FORECAST_LOW_DATA4,forecast_low_data4);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA4,forecast_precip_data4);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA4,forecast_skytextday_data4);
    	//forecast5
    	contentValues.put(KEY_FORECAST_DATE_DATA5,forecast_date_data5);
    	contentValues.put(KEY_FORECAST_DAY_DATA5,forecast_day_data5);
    	contentValues.put(KEY_FORECAST_HIGH_DATA5,forecast_high_data5);
    	contentValues.put(KEY_FORECAST_LOW_DATA5,forecast_low_data5);
    	contentValues.put(KEY_FORECAST_PRECIP_DATA5,forecast_precip_data5);
    	contentValues.put(KEY_FORECAST_SKYTEXTDAY_DATA5,forecast_skytextday_data5);    	
    	
    	String sqlString = KEY_CITY_NAME + "='" + cityname + "'";
        try {
        	db.update(TABLE_NAME, contentValues,sqlString,null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
    
    public void updateDataOfDefaultCity(String cityname,String citycode,int is_defalut_city)
    {
    	ContentValues contentValues = new ContentValues();
    	contentValues.put(KEY_IS_DEFAULT_CITY, is_defalut_city);
    	
    	String sqlString = KEY_CITY_NAME + "='" + cityname + "'";
        try {
        	db.update(TABLE_NAME, contentValues,sqlString,null);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
}
