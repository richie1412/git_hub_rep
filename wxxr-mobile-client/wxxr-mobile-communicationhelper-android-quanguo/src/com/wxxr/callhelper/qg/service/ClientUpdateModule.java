package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IClientUpdateService;
import com.wxxr.callhelper.qg.rpc.ClientInfo;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IClientUpdateRestService;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;

public class ClientUpdateModule extends AbstractModule<ComHelperAppContext>
		implements IClientUpdateService {
	private static final Trace log = Trace.register(ClientUpdateModule.class);

	@Override
	public ClientInfo getClientInfo() {
		String url = context.getService(IClientConfigManagerService.class).getRestServiceUrl();
		ClientInfo clientInfo = context.getService(IRestProxyService.class).getRestService(IClientUpdateRestService.class,url).getClientInfo();
		return clientInfo;
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IClientConfigManagerService.class);
		addRequiredService(IRestProxyService.class);
	}

	@Override
	protected void startService() {
		context.registerService(IClientUpdateService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IClientUpdateService.class, this);
	}

}
