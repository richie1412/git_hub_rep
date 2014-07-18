package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

public class HtmlMessageBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long msgId;
	private HtmlMessage htmlMessage;
	private String remoteURL;
	private long readTime;
	private String htmlid;
	public String getHtmlid() {
		return htmlid;
	}
	public void setHtmlid(String htmlid) {
		this.htmlid = htmlid;
	}
	public Long getMsgId() {
		return msgId;
	}
	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}
	public HtmlMessage getHtmlMessage() {
		return htmlMessage;
	}
	public void setHtmlMessage(HtmlMessage htmlMessage) {
		this.htmlMessage = htmlMessage;
	}
	public String getRemoteURL() {
		return remoteURL;
	}
	public void setRemoteURL(String remoteURL) {
		this.remoteURL = remoteURL;
	}
	public long getReadTime() {
		return readTime;
	}
	public void setReadTime(long readTime) {
		this.readTime = readTime;
	}
	
}
