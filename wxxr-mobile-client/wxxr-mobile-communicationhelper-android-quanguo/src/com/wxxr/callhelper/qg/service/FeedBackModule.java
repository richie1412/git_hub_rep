package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IFeedBackService;
import com.wxxr.callhelper.qg.rpc.AbstractMonitorRunnable;
import com.wxxr.callhelper.qg.rpc.FeedBackMessage;
import com.wxxr.callhelper.qg.rpc.IFeedBackRestService;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;

public class FeedBackModule extends AbstractModule<ComHelperAppContext> implements IFeedBackService {
	private static final Trace log = Trace.register(FeedBackModule.class);

	@Override
	public void addFeedBack(final String content,final String deviceId,final Integer type,IProgressMonitor monitor) {
	context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log)  {
			
			@Override
			protected Object executeTask() throws Throwable {
				 FeedBackMessage f=new FeedBackMessage();
				 f.setContent(content);
				 f.setDeviceId(deviceId);
				 f.setType(type);
				 context.getService(IRestProxyService.class).getRestService(IFeedBackRestService.class).addFeedBackMessage(f);
				return null;
			}
		});
	}
	@Override
	protected void initServiceDependency() {
		
	}

	@Override
	protected void startService() {
		context.registerService(IFeedBackService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IFeedBackService.class, this);
	}

	

}
