/*
 * Copyright 2010-2013 WXXR Network Technology
 * Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.widget.Toast;

import com.tencent.mm.sdk.platformtools.Log;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.bean.UrlBean;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IClientConfigSettingRestService;
import com.wxxr.callhelper.qg.rpc.UrlLocator;
import com.wxxr.callhelper.qg.ui.gd.FragmentSquareNew;
import com.wxxr.callhelper.qg.utils.GDWebCachePath;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.mobile.preference.api.IPreferenceManager;

/**
 * @class desc ClientSettingSyncService.
 * @author wangxuyang
 * @version $Revision: 1.11 $
 * @created time 2013-9-9 下午12:06:58
 */
public class ClientConfigManagerService extends
		AbstractModule<ComHelperAppContext> implements
		IClientConfigManagerService {
	private static final Trace log = Trace
			.register(ClientConfigManagerService.class);
	private Timer timer = new Timer("Setting data sync timer thread");
	Dictionary<String, String> defaultSettings = new Hashtable<String, String>();
	private String dailyNewsUrl;
	private String homePageUrl;
	private String restServiceUrl;
	private String sshxSyncUrl;

	private String guessLikeUrl;
	private String cityCheapHomeUrl;
	private String squarlUrl, app_recommend,fetchUrl;

	private long lastCheckTime;
	private int checkIntervalInSeconds = 30 * 60; // 30 minutes

	public String getModuleName() {
		return this.getClass().getSimpleName();
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IUserActivationService.class);
		addRequiredService(IPreferenceManager.class);
	}

	@Override
	protected void startService() {
		loadLocalSettings();
		this.dailyNewsUrl = getURL("dailyNewsUrl");
		this.homePageUrl = getURL("homePageUrl");
		this.restServiceUrl = getURL("server");
		this.sshxSyncUrl = getURL("SSHX_SYNC_SERVER_URI");
		this.guessLikeUrl = getURL("guessLikeUrl");
		this.cityCheapHomeUrl = getURL("cityCheapHomeUrl");
		this.squarlUrl = getURL("squarlUrl");
		this.fetchUrl=getURL("fetchUrl");

		context.registerService(IClientConfigManagerService.class, this);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (((System.currentTimeMillis() - lastCheckTime) >= checkIntervalInSeconds * 1000L)
						|| context.getApplication().isInDebugMode()) {
					try {
						loadRemoteSettings();
					} catch (Throwable e) {
						log.warn("Error when get remote settings", e);
					}
					lastCheckTime = System.currentTimeMillis();
				}
			}
		}, 10000, 10000);

	}

	private boolean isNetworkConnected() {
		return context.getService(IDataExchangeCoordinator.class)
				.checkAvailableNetwork() > 0;
	}

	@Override
	protected void stopService() {
		context.unregisterService(IClientConfigManagerService.class, this);
	}

	private void loadRemoteSettings() throws IOException {
		if (!isNetworkConnected()) {
			if (log.isDebugEnabled()) {
				log.debug("Network is not connected.");
			}
			return;
		}
		boolean forTest = context.getApplication().isInDebugMode();
		String userId = context.getService(IUserActivationService.class)
				.getCurrentUserId();
		String provinceCode = context.getService(IClientCustomService.class)
				.getProviceCode();
		UrlLocator urlLocator = getRestService(
				IClientConfigSettingRestService.class).getServiceUrl(userId,
				provinceCode, forTest);
		IPreferenceManager prefManager = context
				.getService(IPreferenceManager.class);

		String prefName = getModuleName();
		if (!prefManager.hasPreference(prefName)) {
			Dictionary<String, String> d = new Hashtable<String, String>();
			prefManager.newPreference(prefName, d);
		}
		if (urlLocator != null) {
			if (StringUtils.isNotBlank(urlLocator.getDailyNewsUrl())) {
				this.dailyNewsUrl = urlLocator.getDailyNewsUrl();
				if (isChanged(getModuleName(), "dailyNewsUrl",
						urlLocator.getDailyNewsUrl())) {
					prefManager.updatePreference(prefName, "dailyNewsUrl",
							urlLocator.getDailyNewsUrl());
				}
			}
			if (StringUtils.isNotBlank(urlLocator.getHomePageUrl())) {
				this.homePageUrl = urlLocator.getHomePageUrl();
				if (isChanged(prefName, "dailyNewsUrl",
						urlLocator.getHomePageUrl())) {
					prefManager.updatePreference(prefName, "homePageUrl",
							urlLocator.getHomePageUrl());
				}
			}
			if (StringUtils.isNotBlank(urlLocator.getRestServiceUrl())) {
				this.restServiceUrl = urlLocator.getRestServiceUrl();
				if (isChanged(prefName, "server",
						urlLocator.getRestServiceUrl())) {
					prefManager.updatePreference(prefName, "server",
							urlLocator.getRestServiceUrl());
				}
			}
			if (StringUtils.isNotBlank(urlLocator.getSshxRestUrl())) {
				this.sshxSyncUrl = urlLocator.getSshxRestUrl();
				if (isChanged(prefName, "server", urlLocator.getSshxRestUrl())) {
					prefManager
							.updatePreference(prefName, "SSHX_SYNC_SERVER_URI",
									urlLocator.getSshxRestUrl());
				}
			}
		}

		
		loadCityData();
		
		

	}

	private void loadCityData() {
		IPreferenceManager prefManager = context
				.getService(IPreferenceManager.class);

		String prefName = getModuleName();
		if (!prefManager.hasPreference(prefName)) {
			Dictionary<String, String> d = new Hashtable<String, String>();
			prefManager.newPreference(prefName, d);
		}
		String cheap21 = "";
		String like = "";
		String square = "";
		String app_recommend = "";
		UrlBean bean = getService(IFetchURLofContent.class)
				.getContentOf21CityCheap();
		if (bean != null) {
			cheap21 = bean.getUrl().get(0);
		}
		bean = getService(IFetchURLofContent.class).getContentOfGuessLikeUrl();
		if (bean != null) {
			like = bean.getUrl().get(0);
		}

		bean = getService(IFetchURLofContent.class).getContentOfSquareURL();
		if (bean != null) {
			square = bean.getUrl().get(0);
		}
		bean = getService(IFetchURLofContent.class).getContentOfApp_Recommend();
		if (bean != null) {
			app_recommend = bean.getUrl().get(0);
		}
		if (StringUtils.isNotBlank(cheap21)) {
			this.cityCheapHomeUrl = cheap21;
			if (isChanged(getModuleName(), "cityCheapHomeUrl", cheap21)) {
				prefManager.updatePreference(prefName, "cityCheapHomeUrl",
						cheap21);
			}
		}

		if (StringUtils.isNotBlank(like)) {
			this.guessLikeUrl = like;
			if (isChanged(getModuleName(), "guessLikeUrl", like)) {
				prefManager.updatePreference(prefName, "guessLikeUrl", like);
			}
		}

	//	if (StringUtils.isNotBlank(square)) {
			this.squarlUrl = square;
			if (isChanged(getModuleName(), "squarlUrl", square)) {
				prefManager.updatePreference(prefName, "squarlUrl", square);
			}
	//	}

		if (StringUtils.isNotBlank(app_recommend)) {
			this.app_recommend = app_recommend;
			if (isChanged(getModuleName(), "app_recommend", app_recommend)) {
				prefManager.updatePreference(prefName, "app_recommend",
						app_recommend);
			}
		}
	}

	boolean isChanged(String pid, String name, String new_value) {
		String old_value = context.getService(IPreferenceManager.class)
				.getPreference(pid, name);
		if (old_value == null) {
			return new_value != null;
		}
		return !old_value.equals(new_value);
	}

	private void loadLocalSettings() {
		InputStream in = null;
		try {
			in = context.getApplication().getAndroidApplication().getAssets()
					.open("server.properties");
			Properties props = new Properties();
			props.load(in);
			if (props.size() > 0) {
				Set<Object> keys = props.keySet();
				for (Object obj : keys) {
					String key = (String) obj;
					defaultSettings.put(key, props.getProperty(key));
				}
			}
		} catch (IOException e) {
			log.warn("Error when open file:server.properties", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {

				}
			}
		}
	}

	private <T> T getRestService(Class<T> clazz) {
		String url = defaultSettings.get("server");
		if (url == null) {
			url = getURL("server");
		}
		if (url == null) {
			throw new IllegalArgumentException(
					"There is not sshx sync server url setup, you should specified server target url[SSHX_SYNC_SERVER_URI] !!!");
		}
		return context.getService(IRestProxyService.class).getRestService(
				clazz, url);
	}

	@Override
	public String getURL(String name) {
		if (StringUtils.isBlank(name)) {
			throw new IllegalArgumentException("The attribute name is null!!!");
		}
		String value = null;
		String pid = getModuleName();
		if (context.getService(IPreferenceManager.class).hasPreference(pid,
				name)) {
			value = context.getService(IPreferenceManager.class).getPreference(
					pid, name);
			if (value != null) {
				return value;
			}
		}
		value = defaultSettings.get(name);
		// if (context.getService(IPreferenceManager.class).hasPreference(pid))
		// {
		// context.getService(IPreferenceManager.class).putPreference(pid,
		// defaultSettings);
		// }else{
		// context.getService(IPreferenceManager.class).newPreference(pid,
		// defaultSettings);
		// }
		// value = defaultSettings.get(name);
		return value;
	}

	@Override
	public String getHomeInfoUrl() {
		return this.homePageUrl;
	}

	@Override
	public String getEveryDayInfoUrl() {
		return this.dailyNewsUrl;
	}

	@Override
	public String getRestServiceUrl() {
		return this.restServiceUrl;
	}

	@Override
	public String getSSHXSyncRestServiceUrl() {
		return sshxSyncUrl;
	}

	@Override
	public String getGuessLikeInfoUrl() {
		return guessLikeUrl;
	}

	@Override
	public String getCityCheapHomeUrl() {
		// TODO Auto-generated method stub
		return cityCheapHomeUrl;
	}

	@Override
	public String getSquarelUrl() {
		// TODO Auto-generated method stub
		return squarlUrl;
	}

	@Override
	public String getApp_RecommendUrl() {
		// TODO Auto-generated method stub
		return app_recommend;
	}

	@Override
	public void forceLoadURL() {
		// TODO Auto-generated method stub
//		new Thread(){			
//			public void run() {
				
				try {
					loadCityData();	
					FragmentSquareNew.lasttime=0;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			
					new Thread(){
						public void run(){
							ArrayList<HtmlMessageBean> defaultlist = getService(
									IGDDownWebService.class).getZhushouBobaoDefault();
							if(defaultlist!=null&&defaultlist.size()>0){
								
							}else{							
							  getService(IFetchDefaultZhushouBobao.class).getDefaultZhushouBaobao();
							}
						}
					}.start();				
				
			
				lastCheckTime=System.currentTimeMillis();				
		//	};
		//}.start();
	}


	@Override
	public String getUrlOfGetUrls() {
		return this.sshxSyncUrl;
	}

}
