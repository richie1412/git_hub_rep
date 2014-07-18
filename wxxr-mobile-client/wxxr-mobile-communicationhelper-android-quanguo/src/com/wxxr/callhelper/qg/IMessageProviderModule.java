/**
 * 
 */
package com.wxxr.callhelper.qg;

import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.bean.MsgType;

/**
 *
 * 消息提供模块
 * 主要功能：
 * 1.给ui提供消息
 * 2.处理用户已读取的消息，添加统计数据
 * 3.给ui提供消息历史信息
 * @author wangxuyang
 * 
 */
public interface IMessageProviderModule {
	/**
	 * 获取首页微资讯内容
	 * @return
	 */
	HtmlMessageBean[] getHomePageMessage();
	/**
	 * 获取下一条闪闪回信或者微资讯内容
	 * @param msgType-消息类型
	 * @return
	 */
	HtmlMessageBean getNextMessage(MsgType msgType);
	/**
	 * 获取多条闪闪回信或者微资讯信息
	 * @param msgType-消息类型
	 * @param limit-最多获取条数
	 * @return
	 */
	HtmlMessageBean[] getMessages(MsgType msgType,int limit);
	/**
	 * 标记被读消息
	 *  
	 * @param messageId
	 * @param readTime
	 */
	void notifyMessageRead(Long messageId,long readTime);
	/**
	 * 获取消息历史
	 * @param limit-最多获取条数
	 * @return
	 */
	HtmlMessageBean[] getMessageHistory(int start,int limit);
	/**
	 * 获取消息历史总数
	 * @return
	 */
	int getTotalMessageHistorySize();
}
