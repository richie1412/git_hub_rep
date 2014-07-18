/**
 * 
 */
package com.wxxr.callhelper.qg.bean;

import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

import com.wxxr.mobile.core.util.StringUtils;

/**
 * @author neillin
 *
 */
public class PrivateSMSetting {
	private String password;
	private List<String> privateNumbers;
	private boolean ringBellWhenReceiving;
	private Boolean moveOutgingIn;
	private String bindemail;
	
	public synchronized String getBindemail() {
		return bindemail;
	}

	public synchronized void  setBindemail(String bindemail) {
		this.bindemail = bindemail;
	}

	/**
	 * @return the password
	 */
	public synchronized String getPassword() {
		return password;
	}
	
	public synchronized boolean addPrivateNumber(String number){
		if(number == null){
			return false;
		}
		if(privateNumbers == null){
			privateNumbers = new LinkedList<String>();
		}
		if(!privateNumbers.contains(number)){
			privateNumbers.add(number);
			return true;
		}
		return false;
	}
	
	public synchronized boolean removePrivateNumber(String number){
		return privateNumbers != null ? privateNumbers.remove(number) : false;
	}
	
	/**
	 * @return the privateNumbers
	 */
	public synchronized List<String> getPrivateNumbers() {
		return privateNumbers == null ? (List<String>)Collections.EMPTY_LIST : new LinkedList<String>(this.privateNumbers);
	}
	/**
	 * @param password the password to set
	 */
	public synchronized void setPassword(String password) {
		this.password = password;
	}
	
	public synchronized void updateTo(Dictionary<String, String> dict){
		if(this.password != null){
			dict.put("password", password);
		}else{
			dict.remove("password");
		}
		dict.put("ringBellWhenReceiving", String.valueOf(ringBellWhenReceiving));
		if((this.privateNumbers != null)&&(privateNumbers.size() > 0)){
			dict.put("privateNumbers", StringUtils.join(privateNumbers.iterator(), ','));
		}else{
			dict.remove("privateNumbers");
		}
		if(this.moveOutgingIn != null){
			dict.put("moveOutgingIn", this.moveOutgingIn.toString());
		}else{
			dict.remove("moveOutgingIn");
		}
		
		dict.put("bindemail", bindemail);
		
	}
	
	public synchronized void updateFrom(Dictionary<String, String> dict){
		this.password = dict.get("password");
		String val = dict.get("privateNumbers");
		if(val != null){
			this.privateNumbers = new LinkedList<String>();
			this.privateNumbers.addAll(Arrays.asList(StringUtils.split(val, ',')));
		}
		val = dict.get("ringBellWhenReceiving");
		this.ringBellWhenReceiving = "true".equalsIgnoreCase(val);
		val = dict.get("moveOutgingIn");
		if(val != null){
			this.moveOutgingIn = Boolean.valueOf(val);
		}
		bindemail=dict.get("bindemail");
	}

	/**
	 * @return the ringBellWhenReceiving
	 */
	public boolean isRingBellWhenReceiving() {
		return ringBellWhenReceiving;
	}

	/**
	 * @param ringBellWhenReceiving the ringBellWhenReceiving to set
	 */
	public void setRingBellWhenReceiving(boolean ringBellWhenReceiving) {
		this.ringBellWhenReceiving = ringBellWhenReceiving;
	}

	/**
	 * @return the moveOutgingIn
	 */
	public Boolean getMoveOutgingIn() {
		return moveOutgingIn;
	}

	/**
	 * @param moveOutgingIn the moveOutgingIn to set
	 */
	public void setMoveOutgingIn(Boolean moveOutgingIn) {
		this.moveOutgingIn = moveOutgingIn;
	}
	
}
