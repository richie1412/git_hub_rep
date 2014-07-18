package com.wxxr.callhelper.qg.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FirstStartPreferences {
	private static FirstStartPreferences instance = null;
	private final String PREFERENCE_NAME = "firstsatarclient";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	private final static String KEY_FIRST_IN_HOME="firstinhome";

	private FirstStartPreferences(Context c) {
		sp = c.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
		editor = sp.edit();
	}

	public synchronized static FirstStartPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new FirstStartPreferences(context);
		}
		return instance;
	}

	public void setFirstInHome(String value){
		editor.putString(KEY_FIRST_IN_HOME, value);
		editor.commit();
	}
	
	public String getFirstInHome(){
		return sp.getString(KEY_FIRST_IN_HOME, "");
	}

}