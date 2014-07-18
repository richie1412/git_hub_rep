package com.wxxr.callhelper.qg.service;

/**
 * 广东版——猜你喜欢数据缓存
 * 
 * @author cuizaixi
 * 
 */
public interface IGuessLikeDetailCache<T> extends ISPCache<T> {
	/**
	 * 设置当前缓存信息所属频道
	 * 
	 * @return
	 */
	void setChannelCode(String channelCode);
	/**
	 * 获取当前缓存信息所属频道
	 * 
	 * @return
	 */

	String getChannelCode();
}
