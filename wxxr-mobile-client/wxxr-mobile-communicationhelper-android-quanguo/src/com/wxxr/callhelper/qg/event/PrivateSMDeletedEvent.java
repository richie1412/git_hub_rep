/**
 * 
 */
package com.wxxr.callhelper.qg.event;

import com.wxxr.callhelper.qg.bean.TextMessageBean;

/**
 * @author neillin
 *
 */
public class PrivateSMDeletedEvent extends PrivateSMEvent {
	public TextMessageBean getMessage() {
		return (TextMessageBean)getSource();
	}
	
	public void setMessage(TextMessageBean msg){
		setSource(msg);
	}
}
