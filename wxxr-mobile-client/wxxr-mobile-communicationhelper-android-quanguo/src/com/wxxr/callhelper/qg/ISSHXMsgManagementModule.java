/**
 * 
 */
package com.wxxr.callhelper.qg;

import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * 1.定时通过增量同步模块更新SSHX消息，维护SSHX消息
 * 2.添加统计数据
 * @author wangxuyang
 *
 */
public interface ISSHXMsgManagementModule {
	String TYPE_CONTENT = "sshx";
	/**
	 * 添加统计数据
	 * @param msgIds
	 */
	void addReadSSHXForStatistics(Long msgId, long readTime);
	/**
	 * 获取可播发的闪闪回信消息
	 * @return
	 */
	SSHXMessageBO[] getBroadcastableSSHXMsgs();
	/**
	 * 获取可播发的htmlIds
	 * @return
	 */
	String[] getAllBroadcastableHtmlIds();
	/**
	 * 获取已读消息历史
	 * @return
	 */
	String[] getMessageHistory();
	
	
	SSHXMessageBO getSSHX(Long msgId) throws Exception;
	

	
	SSHXMessageBO removeSSHX(Long msgId) throws Exception;
	
	String getHtmlIdByMsgId(Long msgId);
	
	Long[] getPrepareRemovedMsgs();
	
	Long getHtmlRecordLastModified(String id);
	

}
