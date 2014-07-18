/**
 * 
 */
package com.wxxr.callhelper.qg.zjhz.bean;

import java.io.IOException;
import java.util.Map;

import com.wxxr.mobile.rule.api.StatelessRuleSession;


/**
 * @author neillin
 *
 */
public interface ZJHZSMProcessingContext  {
 
    
    /**
     * check if the userId is in the specific group identified by the groupId, 
     * the specific group should be one of the groups pre-defined by this user
     * @param userId
     * @param groupId
     * @return
     */
	public boolean isUserInGroup(Long userId, String groupId);
    

    
    /**
     * return state of current user
     * @return
     */
	public UserState getUserState();
    
	public UserAttributes getUserAttributes();
    
    public int getUserPrivilege();
    
    /**
     * return true if the message was found and unqueued
     * @param message
     * @return
     */
    public boolean unQueuePublishingMessage(Message message);
    
    public BizUser getUser();
    
    public StatisticsInfo getUserStatistics();
    
    public StatelessRuleSession getChannelSubRulesession(String channel);
    
    public void sendSMMessage(String destAddr,String message,Map<String, String> params) throws IOException;
    
    public boolean isUserPaused();
    
    /**
     * 获取所有可用的播放消息
     * @return 播放消息列表
     */
    public Object getPublishMessages();
}
 
