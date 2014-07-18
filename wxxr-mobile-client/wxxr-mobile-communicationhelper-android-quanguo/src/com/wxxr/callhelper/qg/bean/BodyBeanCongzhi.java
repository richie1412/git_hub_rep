package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

public class BodyBeanCongzhi implements Serializable{
	/**
	 * 
	 */
	public static final int YUEJIE = 1;
	public static final int YUE = 2;
	public static final int TINGJIE = 3;
	private static final long serialVersionUID = 1L;
	public Integer type;// 短信提示类型：月结、余额、停机、温馨提示
	public String address;// 短信号码
	public String content;// 短信内容
	public long cdate;// 短信时间
	public Integer bodyID;
	public String month;// 短信时间 月份
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public long getCdate() {
		return cdate;
	}
	public void setCdate(long cdate) {
		this.cdate = cdate;
	}
	public Integer getBodyID() {
		return bodyID;
	}
	public void setBodyID(Integer bodyID) {
		this.bodyID = bodyID;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	@Override
	public String toString() {
		return "BodyBeanCongzhi [type=" + type + ", address=" + address
				+ ", content=" + content + ", cdate=" + cdate + ", bodyID="
				+ bodyID + ", month=" + month + "]";
	}

}
