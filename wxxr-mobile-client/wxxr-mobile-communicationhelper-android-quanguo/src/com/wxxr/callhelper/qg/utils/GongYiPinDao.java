package com.wxxr.callhelper.qg.utils;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class GongYiPinDao {
	private static GongYiPinDao instance = null;
	private final String PREFERENCE_NAME = "firstsatarclient";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	Map<String, String> keys=new HashMap<String, String>();

	// private final static String KEY_FIRST_IN_HOME="firstinhome";

	private GongYiPinDao(Context c) {
		sp = c.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
		editor = sp.edit();
		keys=(Map<String, String>)sp.getAll();
	}

	public synchronized static GongYiPinDao getInstance(Context context) {
		if (instance == null) {
			instance = new GongYiPinDao(context);
			
		}
		return instance;
	}

public void setGYPDKeys(String key){
	
if(!keys.containsKey(key)){
	editor.putString(key, key);
	editor.commit();
	keys.put(key, key);
}
	
}


public boolean isGYBBKeys(String key){
	
return keys.containsKey(key);
	
}
	

}
