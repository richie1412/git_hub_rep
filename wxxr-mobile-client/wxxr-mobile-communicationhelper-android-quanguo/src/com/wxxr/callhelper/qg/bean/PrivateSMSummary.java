/**
 * 
 */
package com.wxxr.callhelper.qg.bean;

/**
 * @author neillin
 *
 */
public interface PrivateSMSummary {
	String getPhoneNumber();
	String getName();
	int getMessageCount();
	TextMessageBean getLatestMessage();	
}
