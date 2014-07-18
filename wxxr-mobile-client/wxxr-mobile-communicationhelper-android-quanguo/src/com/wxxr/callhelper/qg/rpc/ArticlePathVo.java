/*
 * @(#)MessagePathVo.java 2013-11-27
 *
 * Copyright 2013-2014 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlElement;
import com.wxxr.javax.xml.bind.annotation.XmlRootElement;

/**
 * 功能描述：
 * 
 * @author maruili
 * @createtime 2013-11-27 下午4:30:14
 */
@XmlRootElement(name="articlePaths")
public class ArticlePathVo implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String path;

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
