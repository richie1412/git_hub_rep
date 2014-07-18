package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.bean.UrlBean;

public interface IFetchURLofContent {
	/**
	 * 获取助手播报默认的x篇文章
	 * 
	 * @param key
	 *            zsbb:ch
	 * @return
	 */
	UrlBean getContentOfZSBB();
	/**
	 * 获取广场主页的url
	 * 
	 * @param key
	 *            gc:cu
	 * @return
	 */
	UrlBean getContentOfSquareURL();
	
	/**
	 * 应用推荐的网页
	 * 
	 * @param key
	 *            dsyh:cu
	 * @return
	 */
	UrlBean getContentOfApp_Recommend();
	
	/**
	 * 获取猜你喜欢的home URL
	 * 
	 * @param key
	 *            guesslike:cu
	 * @return
	 */
	UrlBean getContentOfGuessLikeUrl();
	
	
	/**
	 * 地市优惠列表主页
	 * 
	 * @param key
	 *            dsyh:cu
	 * @return
	 */
	UrlBean getContentOf21CityCheap();
	/**
	 * 获取某个城市的优惠列表
	 * 
	 * @param key
	 * @return
	 */
	UrlBean getContentOfCheapListUrl(String city);	
}
