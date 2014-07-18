/*
 * @(#)ManagerSharedPreferences.java	 2011-12-06
 *
 * Copyright 2004-2011 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;


public class NSharedPreferences {

	private static NSharedPreferences instance = null;
	private final String PREFERENCE_NAME = "config";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private NSharedPreferences(Context c) {
		sp = c.getSharedPreferences(PREFERENCE_NAME, Activity.MODE_PRIVATE);
		editor = sp.edit();
	}

	public synchronized static NSharedPreferences getInstance(Context context) {
		if (instance == null) {
			instance = new NSharedPreferences(context);
		}
		return instance;
	}

	/**
	 * FIXME Comment this method desc：检查是否已存在该文件，其中key是xml的文件名�?
	 * 
	 * @param key
	 * @return
	 */
	public boolean contains(String key) {
		return sp.contains(key);
	}

	/**
	 * FIXME Comment this method desc：清除内容�?
	 * 
	 * @return
	 */
	public void clear() {
		sp.edit().clear().commit();
	}

	/**
	 * FIXME Comment this method desc：删除preference
	 * 
	 * @param key
	 */
	public void remove(String key) {
		sp.edit().remove(key).commit();
	}

	public boolean get(String key, boolean value) {
		return sp.getBoolean(key, value);
	}

	public float get(String key, float value) {
		return sp.getFloat(key, value);
	}

	public int get(String key, int value) {
		return sp.getInt(key, value);
	}

	public long get(String key, long value) {
		return sp.getLong(key, value);
	}

	public String get(String key, String value) {
		return sp.getString(key, value);
	}

	public boolean update(String key, boolean value) {
		editor.putBoolean(key, value);
		return editor.commit();
	}

	public boolean update(String key, float value) {
		editor.putFloat(key, value);
		return editor.commit();
	}

	public boolean update(String key, int value) {
		editor.putInt(key, value);
		return editor.commit();
	}

	public boolean update(String key, long value) {
		editor.putLong(key, value);
		return editor.commit();
	}

	public boolean update(String key, String value) {
		editor.putString(key, value);
		return editor.commit();
	}

	public void OnChangeListener(final OnChangeCallback callback) {
		sp.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {

			@Override
			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				callback.onChanged(sharedPreferences, key);
			}
		});
	}

	public interface OnChangeCallback {
		public void onChanged(SharedPreferences sharedPreferences, String key);
	}

}
