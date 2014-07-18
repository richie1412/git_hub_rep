/*
 * @(#)MessagePathListVo.java 2013-11-27
 *
 * Copyright 2013-2014 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wxxr.javax.xml.bind.annotation.XmlRootElement;

/**
 * 功能描述：文章路径列表
 * @author maruili
 * @createtime 2013-11-27 下午4:46:43
 */
@XmlRootElement(name = "ArticlePathCollect")
public class ArticlePathCollectionVo implements Serializable{
	private static final long serialVersionUID = 1L;
	@XStreamImplicit(itemFieldName="articlePaths")
	private ArticlePathVo[] articlePaths;
	public ArticlePathVo[] getArticlePaths() {
		return articlePaths;
	}
	public void setArticlePaths(ArticlePathVo[] articlePaths) {
		this.articlePaths = articlePaths;
	}
	

}
