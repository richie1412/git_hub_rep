/**
 * 
 */
package com.wxxr.mobile.core.api;

import java.util.concurrent.ExecutorService;

import com.wxxr.mobile.core.microkernel.api.IKernelContext;

/**
 * @author neillin
 *
 */
public interface IApplication extends IKernelContext {
	public class ApplicationFactory {
		public static ApplicationFactory INSTANCE = new ApplicationFactory();
		private ApplicationFactory(){}
		
		private IApplication application;
		
		public IApplication getApplication() {
			return this.application;
		}
		
		public void setApplication(IApplication app){
			if(this.application != null){
				throw new IllegalStateException("Application had been initialized !!!");
			}
			if(app == null){
				throw new IllegalArgumentException("Application cannot be NULL !");
			}
			this.application = app;
		}
	}
	
	ExecutorService getExecutor();
}
