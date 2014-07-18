package com.wxxr.callhelper.qg.service;

import android.content.Context;
/**
 * 利用百度API获取用户所在位置
 * 
 * @author cuizaixi
 * 
 */
public interface IBDLocationService {
	/**
	 * 
	 * @param listener
	 * @param context
	 */
	void getCityName(CityNameListener listener, Context context);
}
