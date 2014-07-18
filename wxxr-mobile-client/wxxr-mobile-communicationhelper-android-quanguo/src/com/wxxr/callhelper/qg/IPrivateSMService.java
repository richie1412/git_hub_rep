/**
 * 
 */
package com.wxxr.callhelper.qg;

import java.util.List;
import java.util.Set;

import com.wxxr.callhelper.qg.adapter.IListDataProvider;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.bean.TextMessageBean;

/**
 * @author neillin
 *
 */
public interface IPrivateSMService {
	
	
	
	String getBindedEmail();
	void setBindedEmail(String email);

	boolean isPasswordSetup();
	void setupPassword(String passwd);
	boolean verifyPassword(String password);
	
	boolean addPrivateNumber(String number);//添加单个联系人
	void addPrivateNumberMore(List<String> numbers);//添加多个联系人
	
	boolean isAPrivateNumber(String number);
	
	Boolean getMoveOutgoing2PrivateOutbox();
	void setMoveOutgoing2PrivateOutbox(boolean bool);
	
	void addOutgoingMessage(TextMessageBean msg);
	
	boolean removePrivateNumber(String number);
	List<String> getAllPrivateNumber();
	
	void setRingBellWhenReceiving(boolean bool);
	boolean isRingBellWhenReceiving();
	
	String getContactName(String number);
	
	List<PrivateSMSummary> getSummarys();
	
	List<TextMessageBean> getAllMessageOf(String phoneNumber);
	
	void onSMReceived(TextMessageBean msg);
	/**
	 * 
	 * @param msg
	 * @param notifyother  是否通知其他监听者
	 * @param refershdata   是否更新缓存
	 */
	void onSMReceived(TextMessageBean msg,boolean notifyother,boolean refershdata);
	
	void deleteMessage(Integer msgId);
	
	void deleteMessage(TextMessageBean msg);
	void deleteMessage(String number,boolean needfreshcache);
	
	void setViewOnShow(boolean bool);
	
	IListDataProvider<PrivateSMSummary> getDataProvider();
	
	IListDataProvider<TextMessageBean> getMessageDataProvider(String number);
	
	int getAllUnreadSize();
	int getUnreadSizeOfPhoneNumber(String num);
	void setReadMsgOfPhoneNumber(String num);
	
}
