package com.wxxr.callhelper.qg.service;

import com.wxxr.mobile.core.rpc.http.apache.AbstractHttpRpcService;
import com.wxxr.mobile.core.rpc.http.api.HttpRpcService;
import com.wxxr.mobile.core.security.api.ISiteSecurityService;
import com.wxxr.mobile.web.grabber.common.AbstractGrabberModule;

public class GrabberHttpService extends AbstractGrabberModule{

	private AbstractHttpRpcService httpService = new AbstractHttpRpcService();
	
	@Override
	protected void initServiceDependency() {
		addRequiredService(ISiteSecurityService.class);
	}

	@Override
	protected void startService() {
		httpService.setConnectionPoolSize(10);
		httpService.startup(context);
		context.registerService(HttpRpcService.class, httpService);
	}

	@Override
	protected void stopService() {
		context.unregisterService(HttpRpcService.class, httpService);
		httpService.shutdown();
	}

}
