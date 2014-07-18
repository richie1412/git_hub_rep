/**
 * 
 */
package com.wxxr.callhelper.qg.event;

/**
 * @author neillin
 *
 */
public class NumberAddedEvent extends PrivateSMEvent {
	public String getNumber() {
		return (String)getSource();
	}
	
	public void setNumber(String num){
		setSource(num);
	}
}
