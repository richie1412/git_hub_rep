/**
 * 
 */
package com.wxxr.phone.helper.workstation;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

/**
 * @class desc A SSHXMessageBO
 * 
 * @author zhangjq
 * @version v1.0
 * @created 2013-8-26 下午07:09:42
 */
public class SSHXMessageBO implements Externalizable {

	private Long adsId;
	private byte priority;
	private Date startDate;
	private Date endDate;
	private String channel;
	protected String status;
	private String mimeType;
	protected String message;
	private String md5;
	private String conditionExpr;
	private String schedulingExpr;

	/**
	 * @return the adsId
	 */
	public Long getAdsId() {
		return adsId;
	}

	/**
	 * @param adsId
	 *            the adsId to set
	 */
	public void setAdsId(Long adsId) {
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
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate
	 *            the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate
	 *            the endDate to set
	 */
	public void setEndDate(Date endDate) {
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

	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * @param md5
	 *            the md5 to set
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType
	 *            the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * @return the conditionExpr
	 */
	public String getConditionExpr() {
		return conditionExpr;
	}

	/**
	 * @param conditionExpr
	 *            the conditionExpr to set
	 */
	public void setConditionExpr(String conditionExpr) {
		this.conditionExpr = conditionExpr;
	}

	/**
	 * @return the schedulingExpr
	 */
	public String getSchedulingExpr() {
		return schedulingExpr;
	}

	/**
	 * @param schedulingExpr
	 *            the schedulingExpr to set
	 */
	public void setSchedulingExpr(String schedulingExpr) {
		this.schedulingExpr = schedulingExpr;
	}

	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.adsId = in.readLong();
		this.priority = in.readByte();
		this.startDate = new Date(in.readLong());
		this.endDate = new Date(in.readLong());
		this.channel = in.readUTF();
		this.status = in.readUTF();
		this.mimeType = in.readUTF();
		this.message = in.readUTF();
		this.md5 = in.readUTF();
		// 可选参数处理
		if (in.read() != 0) {
			this.conditionExpr = in.readUTF();
		}
		if (in.read() != 0) {
			this.schedulingExpr = in.readUTF();
		}

	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeLong(this.adsId);
		out.writeByte(this.priority);
		out.writeLong(this.startDate.getTime());
		out.writeLong(this.endDate.getTime());
		out.writeUTF(this.channel);
		out.writeUTF(this.status);
		out.writeUTF(this.mimeType);
		out.writeUTF(this.message);
		out.writeUTF(this.md5);
		// 可选参数处理
		if (this.conditionExpr != null) {
			out.write(1);
			out.writeUTF(this.conditionExpr);
		} else {
			out.write(0);
		}
		if (this.schedulingExpr != null) {
			out.write(1);
			out.writeUTF(this.schedulingExpr);
		} else {
			out.write(0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[adsId=" + adsId + ", channel=" + channel + ", conditionExpr="
				+ conditionExpr + ", endDate=" + endDate + ", md5=" + md5
				+ ", message=" + message + ", mimeType=" + mimeType
				+ ", priority=" + priority + ", schedulingExpr="
				+ schedulingExpr + ", startDate=" + startDate + ", status="
				+ status + "]";
	}

}
