package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

public class ComSecretaryBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static long callback = 0;
	public static long sendback = 1;
	private int id;
	private long secreType;// 0 打电话 1.发短信
	private String telnum;
	private long alertTime;
	private int state;// 0未提醒，1，已提醒
	private int delayTime;
	public ComSecretaryBean(long secreType, String telnum, long alertTime,
			int state, int delayTime) {
		this.secreType = secreType;
		this.telnum = telnum;
		this.alertTime = alertTime;
		this.state = state;
		this.delayTime = delayTime;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ComSecretaryBean() {
	}
	public long getSecreType() {
		return secreType;
	}
	public void setSecreType(long secreType) {
		this.secreType = secreType;
	}
	public String getTelnum() {
		return telnum;
	}
	public void setTelnum(String telnum) {
		this.telnum = telnum;
	}
	public long getAlertTime() {
		return alertTime;
	}
	public void setAlertTime(long alertTime) {
		this.alertTime = alertTime;
	}
	public int getDelayTime() {
		return delayTime;
	}
	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}
	@Override
	public String toString() {
		return "ComSecretaryBean [id=" + id + ", secreType=" + secreType
				+ ", telnum=" + telnum + ", alertTime=" + alertTime
				+ ", state=" + state + ", delayTime=" + delayTime + "]";
	}

}
