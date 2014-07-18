/*
 * @(#)FeedBackMessageVO.java	 Apr 1, 2012
 *
 * Copyright 2004-2012 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "FeedBackMessage")
public class FeedBackMessage {
	private String deviceId;//设备ID
    private String content;//内容
    private Integer type;//问题类型
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FeedBackMessageVO [deviceId=" + deviceId + ", content="
				+ content + ", type=" + type + "]";
	}
}
