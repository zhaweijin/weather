package com.imt.weather.data;


import com.imt.weather.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.util.Log;

public  class  TranslateDatabase {
	

	public final static String KEY_NAME = "name";
    public final static String KEY_ROWID = "_id";
    public final static String KEY_PINYIN = "pingyin";
    
    private SQLiteDatabase db;
    private final static String DATABASE_NAME = "translate";
    private final static String TABLE_NAME = "translate";
    private final static String CREATE_DATABASE = "create table if not exists translate(" +
      "_id Integer primary key autoincrement," +
      "pingyin text not null," +
      "name text not null)";
    
    Context mContext;
    public TranslateDatabase(Context c)
    {
    	this.mContext = c;
    	db = mContext.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        try {
			  db.execSQL(CREATE_DATABASE);
			  Log.v("create", "Create translate note ok");   
		} catch ( Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Util.print("translate table exist", "translate table exist");
		}
    }
    

	public long insertData(String name,String pingyin)
    {
    	ContentValues contentValues = new ContentValues();
    	contentValues.put(KEY_NAME, name);
    	contentValues.put(KEY_PINYIN, pingyin);   
    	
    	
        return db.insert(TABLE_NAME, null, contentValues);
    }
    
	
    public Cursor queryAllData()
    {
    	Cursor cursor=null;
    	try {
    		cursor = db.rawQuery("select * from translate", null);
    	}catch (Exception e) {
			// TODO: handle exception
    		cursor.close();
    		e.printStackTrace();
		}
    	return cursor;
    }
    
}
