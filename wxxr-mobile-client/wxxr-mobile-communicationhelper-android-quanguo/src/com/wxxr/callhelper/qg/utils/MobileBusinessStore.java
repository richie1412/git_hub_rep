package com.wxxr.callhelper.qg.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MobileBusinessStore {
	private static MobileBusinessStore instance = null;
	private final String PREFERENCE_NAME = "firstsatarclient";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	// ••1.2.1 业务查询
	// • 1.2.2 业务办理
	// • 1.2.3 活动广场
	// •1.2.4 应用推荐
	// •1.2.5 助手播报
	// •1.2.6 猜你喜欢
	// •1.2.7 城市优惠
	// •1.2.8 优惠列表

	
	private final static String KEY_LDTT = "laidiantixing";
	private final static String KEY_DXHZ = "duanxinhuizhi";
	
	
	private final static String KEY_BUSINESS_CHECK = "business_check";
	private final static String KEY_BUSINESS_PROCESSE = "business_process";
	private final static String KEY_APP_RECOMMEND = "app_recommend";

	// private final static String KEY_FIRST_IN_HOME="firstinhome";

	private MobileBusinessStore(Context c) {
		sp = c.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
		editor = sp.edit();
	}

	public synchronized static MobileBusinessStore getInstance(Context context) {
		if (instance == null) {
			instance = new MobileBusinessStore(context);
		}
		return instance;
	}

	public void setBusinessCheckText(String text) {
		editor.putString(KEY_BUSINESS_CHECK, text);
		editor.commit();
	}

	public void setBusinessProcessText(String text) {
		editor.putString(KEY_BUSINESS_PROCESSE, text);
		editor.commit();
	}

	public void setApp_recommend(String text) {
		editor.putString(KEY_APP_RECOMMEND, text);
		editor.commit();
	}
	
	public void setLaidianxitingCode(String privocecode,String text) {
		editor.putString(KEY_LDTT+privocecode, text);
		editor.commit();
	}
	
	public void setDuanxinhuizhiCode(String privocecode,String text) {
		editor.putString(KEY_DXHZ+privocecode, text);
		editor.commit();
	}

	public String getBusinessCheckText() {
		return sp.getString(KEY_BUSINESS_CHECK, "");
	}

	public String getBusinessProcessText() {
		return sp.getString(KEY_BUSINESS_PROCESSE, "");
	}

	public String getApp_recommend() {
		return sp.getString(KEY_APP_RECOMMEND, "");

	}
	
	
	public String getLaidianxitingCode(String privocecode) {
		return sp.getString(KEY_LDTT+privocecode, "");	
	}
	
	public String getDuanxinhuizhiCode(String privocecode) {
		return sp.getString(KEY_DXHZ+privocecode, "");
	}

}
