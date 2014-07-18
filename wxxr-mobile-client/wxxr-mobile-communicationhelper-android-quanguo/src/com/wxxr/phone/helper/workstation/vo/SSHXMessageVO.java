/**
 * 
 */
package com.wxxr.phone.helper.workstation.vo;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlElement;
import com.wxxr.javax.xml.bind.annotation.XmlRootElement;



/**
 * @class desc A SSHXMessageVO
 * 
 * @author zhangjq
 * @version v1.0
 * @created 2013-8-23 11:42:24
 */
@XmlRootElement(name = "SSHXMessageVO")
public class SSHXMessageVO implements Serializable {
	@XmlElement(name="adsId")
	private long adsId;

	/** persistent field */
	@XmlElement(name="priority")
	private byte priority;

	/** nullable persistent field */
	@XmlElement(name="startDate")
	private long startDate;

	/** nullable persistent field */
	@XmlElement(name="endDate")
	private long endDate;

	/** persistent field */
	@XmlElement(name="channel")
	private String channel;
	@XmlElement(name="status")
	protected String status;
	@XmlElement(name="message")
	protected String message;
	@XmlElement(name="mimeType")
	private String mimeType;
	@XmlElement(name="md5")
	private String md5;
	@XmlElement(name="conditionExpr")
	private String conditionExpr;
	@XmlElement(name="schedulingExpr")
	private String schedulingExpr;
	public SSHXMessageVO() {
	}

	/**
	 * @return the adsId
	 */
	
	public long getAdsId() {
		return adsId;
	}

	/**
	 * @param adsId
	 *            the adsId to set
	 */
	public void setAdsId(long adsId) {
		this.adsId = adsId;
	}

	/**
	 * @return the priority
	 */
	public byte getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(byte priority) {
		this.priority = priority;
	}

	/**
	 * @return the startDate
	 */
	public long getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public long getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}

	/**
	 * @param channel
	 *            the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	
	public String getConditionExpr() {
		return conditionExpr;
	}

	public void setConditionExpr(String conditionExpr) {
		this.conditionExpr = conditionExpr;
	}

	public String getSchedulingExpr() {
		return schedulingExpr;
	}

	public void setSchedulingExpr(String schedulingExpr) {
		this.schedulingExpr = schedulingExpr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[adsId=" + adsId + ", channel=" + channel + ", conditionExpr=" + conditionExpr
                + ", endDate=" + endDate + ", md5=" + md5 + ", message=" + message + ", mimeType=" + mimeType
                + ", priority=" + priority + ", schedulingExpr=" + schedulingExpr + ", startDate=" + startDate
                + ", status=" + status + "]";
	}

}