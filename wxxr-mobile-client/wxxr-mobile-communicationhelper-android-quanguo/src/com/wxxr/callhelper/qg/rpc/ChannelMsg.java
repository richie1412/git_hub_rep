/*
 * @(#)ChannelMsg.java 2013-11-26
 *
 * Copyright 2013-2014 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;



/**
 * ����������
 * @author maruili
 * @createtime 2013-11-26 ����4:03:15
 */
@XmlRootElement(name = "msgs")
public class ChannelMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	private String msg;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
