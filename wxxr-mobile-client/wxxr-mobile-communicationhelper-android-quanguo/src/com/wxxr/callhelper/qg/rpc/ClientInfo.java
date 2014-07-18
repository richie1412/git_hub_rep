/*
 * @(#)ClientInfoVO.java	 Mar 23, 2012
 *
 * Copyright 2004-2012 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "ClientInfo")
public class ClientInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String version;//版本号
    private Integer status;//状态（不需要升级：0,必须升级：1）
    private String description;//描述（说明）
    private String url;//下载地址
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	@Override
	public String toString() {
		return "ClientInfoVO [version=" + version + ", status=" + status
				+ ", description=" + description + ", url=" + url + "]";
	}
}
