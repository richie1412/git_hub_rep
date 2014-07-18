package com.wxxr.callhelper.qg.service;

import com.wxxr.mobile.core.api.IProgressMonitor;

public interface ISmsContentParseService {
	/*
	 * 解析短信内容归属地及手机号码 的通讯录名称
	 */
	public void parseSmsContent(String content, IProgressMonitor m);
	/**
	 * 解析短信内容归属地及手机号码 的通讯录名称
	 * @param content
	 * @param m
	 */
	public String parseSmsContent(String content);


}
