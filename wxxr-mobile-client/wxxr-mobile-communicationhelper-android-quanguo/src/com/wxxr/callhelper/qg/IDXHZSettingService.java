/**
 * 
 */
package com.wxxr.callhelper.qg;

import java.util.List;
import java.util.Map;

import com.wxxr.callhelper.qg.exception.AppException;
import com.wxxr.mobile.core.api.IProgressMonitor;

/**
 * @author neillin
 *
 */
public interface IDXHZSettingService {
	void subscribeChannel(String channel, Map<String, String> params,IProgressMonitor monitor);
	
	Map<String, String> getSubscribedChannelParams(String channel) throws AppException;
	
	void unsubscriberChannel(String channel, IProgressMonitor monitor);
	
	List<String> getSubscribedChannels() throws AppException;
	
	void setReceivingMode(Integer mode, IProgressMonitor monitor);
	
	void setNoannoyMode(Boolean noannoyMode, IProgressMonitor monitor);
	
	void setItms(Boolean ltmsMode, IProgressMonitor monitor);
	
	void setReceiptSendStyle(String receiptStyle, IProgressMonitor monitor);
	
	Integer getReceivingMode() throws AppException;//接收模式(指定1(手机上没指定模式),提醒2,全开3)
	
	Boolean getNoannoyMode() throws AppException;//夜间免打扰状态（已定制值为：1；无定制值为0）
	
	Boolean getltms() throws AppException;// 聊天模式状态（开通：true；关闭:false）
	
	String getReceiptSendStyle() throws AppException;//回执短信发送方式:1普通短信,2闪电短信
	
	Map<String,String> getAllChannels() throws AppException;
	
}
