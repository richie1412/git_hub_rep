/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.mobile.core.api.IProgressMonitor;

/**
 * @author wangyan
 *
 */
public interface IOfficeLineHtmlProvideService {
	HtmlMessage getHome(String url,IProgressMonitor monitor);
	HtmlMessage getMicrNews(String url,IProgressMonitor monitor);
	HtmlMessage get21CheapNews(String url,IProgressMonitor monitor,String citykey);
	
	HtmlMessage getSquareHome(String url,IProgressMonitor monitor);
	HtmlMessage get21CheapHome(String url,IProgressMonitor monitor);
	HtmlMessage getGuessYouLikeHome(String url,IProgressMonitor monitor);
	HtmlMessage getDefaultZhushouBaobao(String url,IProgressMonitor monitor);
	
	void downCheapsOfOneCity(String[] url,String key); 
	
}
