/**
 * 
 */
package com.wxxr.callhelper.qg;

import com.wxxr.mobile.core.event.api.IStreamEvent;

/**
 * @author neillin
 *
 */
public interface INotificationService {
	
	void registerNotificationEvent(Class<? extends IStreamEvent> clazz, int style);
	
	void unregisterNotificationEvent(Class<? extends IStreamEvent> clazz);

}
