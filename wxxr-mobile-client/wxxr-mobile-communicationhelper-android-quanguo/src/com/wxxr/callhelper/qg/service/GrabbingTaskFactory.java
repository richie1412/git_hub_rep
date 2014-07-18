package com.wxxr.callhelper.qg.service;

import com.wxxr.mobile.web.grabber.api.IGrabbingTaskFactory;
import com.wxxr.mobile.web.grabber.api.IWebPageGrabbingTask;
import com.wxxr.mobile.web.grabber.api.IWebSiteGrabbingTask;
import com.wxxr.mobile.web.grabber.common.AbstractGrabberModule;

public class GrabbingTaskFactory extends AbstractGrabberModule implements IGrabbingTaskFactory {

	@Override
	public IWebPageGrabbingTask createPageTask() {

		return new PageGrabbingTask();
	}

	@Override
	public IWebSiteGrabbingTask createSiteTask() {
		
		return null;
	}

	@Override
	protected void initServiceDependency() {

		
	}

	@Override
	protected void startService() {
		context.registerService(IGrabbingTaskFactory.class, this);
	}

	@Override
	protected void stopService() {
		context.registerService(IGrabbingTaskFactory.class, this);
	}

}
