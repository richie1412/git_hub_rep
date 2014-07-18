/**
 * 
 */
package com.wxxr.mobile.callhelper.app.model;

import java.util.List;


/**
 * @author fudapeng
 * 短信信息分组
 */
public class SMSSessionGroup {
	/**
	 * 发手机的号码，这个号码关联联系人
	 */
	private String phoneNumber;
	
//	/**
//	 * 显示在会话列表的电话号码，漏接电话短信列表时，就要显示短信里面提示的电话号码而不是发短信的人的电话号码
//	 */
//	private ContactPerson person;
	
	/**
	 * 具体sessin的短信列表
	 */
	private List<SMSInfo> sms;
	
	private String showSessionContent;
	
	/**
	 * sms里面有没有未读短信，如果有就是true，反之false
	 */
	private boolean isHasUnRead;
	
	/**
	 * 短信的个数---- sms.size();
	 */
	private int size;
}
