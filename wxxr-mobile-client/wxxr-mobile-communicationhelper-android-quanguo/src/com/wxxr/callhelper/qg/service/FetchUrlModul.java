package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.bean.UrlBean;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IFetchURL;
import com.wxxr.callhelper.qg.utils.XMLParstTool;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
import com.wxxr.mobile.preference.api.IPreferenceManager;

public class FetchUrlModul extends AbstractModule<ComHelperAppContext>
		implements IFetchURLofContent {
	private static Trace log = Trace.register(ContentManager.class);
	@Override
	public UrlBean getContentOfZSBB() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}
			String content = getService(IRestProxyService.class)
					.getRestService(IFetchURL.class,
							getService(IClientConfigManagerService.class).getUrlOfGetUrls())
					.getMsg("1", "assistant_news", code, "", "");

			return XMLParstTool.parseUrlsXml(content);
		} catch (Exception e) {
			// TODO: handle exception
			if(log.isDebugEnabled()){
				log.debug("getContentOfZSBB  err...."+e.toString());
			}
		}
		return null;
	}

	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IClientCustomService.class);
		addRequiredService(IRestProxyService.class);
		addRequiredService(IClientConfigManagerService.class);
		
	}

	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IFetchURLofContent.class, this);
	}

	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		context.unregisterService(IFetchURLofContent.class, this);
	}

	@Override
	public UrlBean getContentOfSquareURL() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}
			String content = getService(IRestProxyService.class)
					.getRestService(IFetchURL.class,
							getService(IClientConfigManagerService.class).getUrlOfGetUrls())
					.getMsg("1", "activity_plaza", code, "", "");			                     
			return XMLParstTool.parseUrlXml(content);
		} catch (Exception e) {
			if(log.isDebugEnabled()){
				log.debug("getContentOfSquareURL  err...."+e.toString());
			}
		}
		return null;
	}

	@Override
	public UrlBean getContentOfApp_Recommend() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}
			String content = getService(IRestProxyService.class)
					.getRestService(IFetchURL.class,
							getService(IClientConfigManagerService.class).getSSHXSyncRestServiceUrl())
					.getMsg("1", "app_recommend", code, "", "");

			return XMLParstTool.parseUrlXml(content);
		} catch (Exception e) {
			if(log.isDebugEnabled()){
				log.debug("getContentOfApp_Recommend  err...."+e.toString());
			}
		}
		return null;
	}

	@Override
	public UrlBean getContentOfGuessLikeUrl() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}
			String content = getService(IRestProxyService.class)
					.getRestService(IFetchURL.class,
							getService(IClientConfigManagerService.class).getUrlOfGetUrls())
					.getMsg("1", "favorite", code, "", "");

			return XMLParstTool.parseUrlXml(content);
		} catch (Exception e) {
			if(log.isDebugEnabled()){
				log.debug("getContentOfGuessLikeUrl  err...."+e.toString());
			}
		}
		return null;
	}

	@Override
	public UrlBean getContentOf21CityCheap() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}
			String content = getService(IRestProxyService.class)
					.getRestService(IFetchURL.class,
							getService(IClientConfigManagerService.class).getUrlOfGetUrls())
					.getMsg("1", "city_preferential", code, "", "");

			return XMLParstTool.parseUrlXml(content);
		} catch (Exception e) {
			if(log.isDebugEnabled()){
				log.debug("getContentOf21CityCheap  err...."+e.toString());
			}
		}
		return null;
	}

	// http://wiki.corp.wxxr.com.cn/wiki/pages/viewpage.action?pageId=57246680
	@Override
	public UrlBean getContentOfCheapListUrl(String city) {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}
			String content = getService(IRestProxyService.class)
					.getRestService(IFetchURL.class,
							getService(IClientConfigManagerService.class).getUrlOfGetUrls())
					.getMsg("1", "preferential", code, city, "");

			return XMLParstTool.parseUrlsXml(content);
		} catch (Exception e) {
			if(log.isDebugEnabled()){
				log.debug("getContentOfCheapListUrl  err...."+e.toString());
			}
		}
		return null;
	}

}
