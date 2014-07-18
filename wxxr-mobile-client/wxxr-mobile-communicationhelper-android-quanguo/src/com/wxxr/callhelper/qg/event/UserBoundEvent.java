/**
 * 
 */
package com.wxxr.callhelper.qg.event;

import com.wxxr.mobile.core.event.api.GenericEventObject;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;

/**
 * @author neillin
 *
 */
public class UserBoundEvent extends GenericEventObject implements IBroadcastEvent {
	public String getUserId(){
		return (String)getSource();
	}
	
	public void setUserId(String userId){
		setSource(userId);
	}
}
