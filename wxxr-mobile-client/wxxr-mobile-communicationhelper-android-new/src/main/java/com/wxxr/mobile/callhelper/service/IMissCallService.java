package com.wxxr.mobile.callhelper.service;

import java.util.List;

import com.wxxr.mobile.callhelper.app.model.SMSSessionGroup;

/**
 * @author fudapeng
 */
public interface IMissCallService extends ICommmonService{
	
	/**
	 * 获得指定时间段的回执信息
	 * @param start
	 * @param end
	 * @return
	 */
	List<SMSSessionGroup> getgetShowListByTime(long start, long end);
	
	/**
	 * 是否开通来电提醒
	 * @return
	 */
	Switch isOpen();
	
	/**
	 * 设置是否开通来电提醒
	 */
	void switchSet();
	
}	
