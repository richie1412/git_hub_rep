/**
 * 
 */
package com.wxxr.callhelper.qg;

import com.wxxr.mobile.android.app.IAndroidFramework;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * @author neillin
 *
 */
public interface IComHelperFramework extends IAndroidFramework<ComHelperAppContext, AbstractModule<ComHelperAppContext>> {
	String getUserAgentString();
	/**
	 * register task handler in framework, it will be removed after 30 seconds
	 * @param handler
	 * @return
	 */
	Long registerTaskHandler(ITaskHandler handler);
	
	/**
	 * get and remove the task handler identified by hid from framework
	 * @param hid
	 * @return
	 */
	ITaskHandler getTaskHandler(Long hid);
}
