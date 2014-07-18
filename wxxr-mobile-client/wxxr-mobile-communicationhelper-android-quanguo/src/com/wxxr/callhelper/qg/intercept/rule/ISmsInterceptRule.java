package com.wxxr.callhelper.qg.intercept.rule;
/**
 * 拦截短信拦截规则
 * @author zhengjincheng
 *
 */
public interface ISmsInterceptRule {
	public boolean isMatch(String smsContent, String targetnumber);
}
