package com.wxxr.mobile.callhelper.service;

import java.util.List;

import com.wxxr.mobile.callhelper.app.model.SMSInfo;
import com.wxxr.mobile.callhelper.app.model.SMSSessionGroup;

/**
 * @author fudapeng
 */
public interface ICommmonService {
	/**
	 * 删除短信
	 * @param msgId
	 */
	void deleteMessage(Integer msgId);
	
	/**
	 * 删除短信
	 * @param msg
	 */
	void deleteMessage(SMSInfo msg);
	
	
	/**
	 * 删除短信分组
	 * @param msg
	 */
	void deleteMessageGroup(SMSSessionGroup msg);
	
	/**
	 * 删除短信
	 * @param msg
	 */
	void deleteMessage(String number,boolean needfreshcache);
	
	
	
	
	
	/**
	 * 得到没有读的短信个数
	 * @return
	 */
	int getAllUnreadSize();
	
	/**
	 * 得到某个号码没有读的短信 个数
	 * @param num
	 * @return
	 */
	int getUnreadSizeOfPhoneNumber(String num);
	
	/**
	 * 设置信息已读
	 * @param num
	 */
	void setReadMsgOfPhoneNumber(String num);
	
	/**
	 * 设置信息未读
	 * @param num
	 */
	void setUnReadMsgOfPhoneNumber(String num);
	
	/**
	 * 得到页面显示列表
	 * @return
	 */
	List<SMSSessionGroup> getShowList();
	
	
}
