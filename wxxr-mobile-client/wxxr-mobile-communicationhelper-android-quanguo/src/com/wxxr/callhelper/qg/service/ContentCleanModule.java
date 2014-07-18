/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.Timer;
import java.util.TimerTask;

import com.wxxr.callhelper.qg.CleanContext;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.ContentCleanStategy;
import com.wxxr.callhelper.qg.IContentClearModule;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * @author wangxuyang
 *
 */
public class ContentCleanModule extends AbstractModule<ComHelperAppContext> implements IContentClearModule {
	private static final Trace log = Trace.register(ContentCleanModule.class);
	private Timer cleanTimer = new Timer("Content clean thread");
	private ContentCleanStategy cleanStategy = new StorageRestrictionCleanStrategy();
	
	protected void initServiceDependency() {	
		addRequiredService(IClientConfigManagerService.class);			
	}
	private CleanContext ctx = new CleanContext() {
		public ComHelperAppContext getAppContext() {			
			return context;
		}
	}; 

	@Override
	protected void startService() {
		context.registerService(IContentClearModule.class, this);
		cleanTimer.schedule(new TimerTask() {
			public void run() {				
				if (getCleanStategy().isCleanable(ctx)) {
					try {
						getCleanStategy().executeClean(ctx);
					} catch (Throwable e) {
						log.warn("Error when clean data", e);
					}
				}
			}
		}, 1000, 60*1000);		
	}

	@Override
	protected void stopService() {
		context.unregisterService(IContentClearModule.class, this);
		cleanTimer.cancel();		
	}
	

	@Override
	public String getModuleName() {
		return this.getClass().getSimpleName();
	}

	/**
	 * @return the cleanStategy
	 */
	public ContentCleanStategy getCleanStategy() {
		return cleanStategy;
	}

	/**
	 * @param cleanStategy the cleanStategy to set
	 */
	public void setCleanStategy(ContentCleanStategy cleanStategy) {
		this.cleanStategy = cleanStategy;
	}
}
