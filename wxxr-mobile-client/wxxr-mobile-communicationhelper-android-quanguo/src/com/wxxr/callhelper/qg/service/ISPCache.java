package com.wxxr.callhelper.qg.service;

import java.util.List;

/**
 * 用sharePreference实现缓存
 * 
 * @author cuizaixi
 * 
 */
public interface ISPCache<T> {
	/**
	 * 设置缓存信息
	 * 
	 * @param messages
	 */
	void setCachedMessage(List<List<String>> messages, T tag);
	/**
	 * 获取缓存信息
	 * 
	 * @return
	 */
	List<String> getCachedMessage(T tag);
	/**
	 * 清空缓存信息
	 * 
	 */
	void clearCachedMessage(T tag);
}
