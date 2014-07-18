/**
 * 
 */
package com.wxxr.callhelper.qg.event;

import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.mobile.core.event.api.GenericEventObject;
import com.wxxr.mobile.core.event.api.IStreamEvent;

/**
 * @author neillin
 *
 */
public class NewPrivateSMReceivedEvent extends GenericEventObject implements IStreamEvent {
	public TextMessageBean getMessage() {
		return (TextMessageBean)getSource();
	}
	
	public void setMessage(TextMessageBean msg){
		setSource(msg);
	}
}
