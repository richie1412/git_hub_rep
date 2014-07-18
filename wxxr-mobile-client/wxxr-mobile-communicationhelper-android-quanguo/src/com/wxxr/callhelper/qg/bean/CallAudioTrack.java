/**
 * 
 */
package com.wxxr.callhelper.qg.bean;

import java.io.File;
import java.util.Date;

/**
 * @author neillin
 *
 */
public class CallAudioTrack {
	private Date timestamp;
	private String phoneNumber,contactName;
	private int callType;
	private File dataFile;
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	/**
	 * @return the callType
	 */
	public int getCallType() {
		return callType;
	}
	/**
	 * @return the dataFile
	 */
	public File getDataFile() {
		return dataFile;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	/**
	 * @param callType the callType to set
	 */
	public void setCallType(int callType) {
		this.callType = callType;
	}
	/**
	 * @param dataFile the dataFile to set
	 */
	public void setDataFile(File dataFile) {
		this.dataFile = dataFile;
	}
	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}
	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	
	public String getRecordId() {
		return this.dataFile.getName();
	}
	
	
}
