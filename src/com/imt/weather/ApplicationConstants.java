package com.imt.weather;



import android.content.Context;
import android.content.Intent;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;

public class ApplicationConstants {

  public final static int main = 0x111;
  public final static int update = 0x222;
  public final static int cancell = 0x333;
  public final static int network_timeout = 0x444;
  public final static int add_sucess = 0x555;
  public final static int add_fail = 0x666;
  public final static LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(
		  LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
  
  public final static LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(
		  LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
  

  public final static long[] updateSpinnerValue=new long[]{30*60*1000,1*60*60*1000,3*60*60*60*100,
	  6*60*60*60*100,12*60*60*60*100,24*60*60*60*1000};
  
  
  //eg searchstr = shenzhen
  public final static String searchCityUrl = "http://weather.service.msn.com/" +
  		"find.aspx?outputview=search&src=vista&weasearchstr=";
  //eg wealocations= wc:CHXX0120                 
  public final static String searchWeatherData = "http://weather.service.msn.com/" +
  		"data.aspx?src=vista&wealocations=";
  
  // is first add city
  public final static String KEY_IS_FIRST_ADD_CITY = "is_first_city";
  
  // update widget of default city
  public static void updateWidget(Context context){
		Util.print("search_sendbrocast", "brocast");
		Intent localIntent1 = new Intent("com.xuzhi.weather.UPDATE_WIDGET");
		context.sendBroadcast(localIntent1);
  }
  
}
