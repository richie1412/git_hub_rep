package com.wxxr.callhelper.qg.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class GDWebCachePath {
	private static GDWebCachePath instance = null;
	private final String PREFERENCE_NAME = "gdwebcache";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private GDWebCachePath(Context c) {
		sp = c.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
		editor = sp.edit();
	}

	public synchronized static GDWebCachePath getInstance(Context context) {
		if (instance == null) {
			instance = new GDWebCachePath(context);
		}
		return instance;
	}
   /**
    * 
    * @param key  网页 的url
    * @param value   本地的文件夹路径
    */
	public void setKeyOfListFilePath(String key,String value){
		editor.putString(key, value);
		editor.commit();
	}
	 /**
	    * 
	    * @param key  网页 的url
	    * @param value   本地的文件夹路径
	    */
	public String getPathOfKey(String key){
		return sp.getString(key, "");
	}
	/**
	 * 
	 * @param pkey  
	 * @return
	 */
public String[] getAllPathOfKeys(String pkey	){
	ArrayList<String> aa=new ArrayList<String>(sp.getAll().keySet());
	ArrayList<String> result=new ArrayList<String>();
	for (String key : aa) {
		if(key.startsWith(pkey)){
			result.add(sp.getString(key, ""));
		}
	}
	
	return  result.toArray(new String[result.size()]);
}
	


}

