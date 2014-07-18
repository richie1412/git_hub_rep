/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IMessageProviderModule;
import com.wxxr.callhelper.qg.ISSHXMsgManagementModule;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.bean.MsgType;
import com.wxxr.callhelper.qg.zjhz.bean.BizUser;
import com.wxxr.callhelper.qg.zjhz.bean.CellarPhoneUser;
import com.wxxr.callhelper.qg.zjhz.bean.CellarPhoneUserImpl;
import com.wxxr.callhelper.qg.zjhz.bean.Message;
import com.wxxr.callhelper.qg.zjhz.bean.StatisticsInfo;
import com.wxxr.callhelper.qg.zjhz.bean.UserAttributes;
import com.wxxr.callhelper.qg.zjhz.bean.UserAttributesImpl;
import com.wxxr.callhelper.qg.zjhz.bean.UserState;
import com.wxxr.callhelper.qg.zjhz.bean.ZJHZMessage;
import com.wxxr.callhelper.qg.zjhz.bean.ZJHZSMProcessingContext;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.mobile.rule.api.RuleException;
import com.wxxr.mobile.rule.api.RuleRuntime;
import com.wxxr.mobile.rule.api.StatelessRuleSession;
import com.wxxr.mobile.rule.impl.ConfigurationException;
import com.wxxr.mobile.rule.impl.RuleEngine;
import com.wxxr.mobile.rule.impl.RuleHelper;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;
/**
 * 消息提供模块
 * 主要功能：
 * 1.给ui提供消息
 * 2.处理用户已读取的消息，添加统计数据
 * 
 * @author wangxuyang
 * 
 */
public class MessageProviderModule extends AbstractModule<ComHelperAppContext> implements IMessageProviderModule {
	private static final Trace log = Trace.register(MessageProviderModule.class);



	// =============================life cycle ==========================
	@Override
	protected void initServiceDependency() {
		addRequiredService(IContentManager.class);
		addRequiredService(IHtmlMessageManager.class);
		addRequiredService(ISSHXMsgManagementModule.class);
	}

	@Override
	protected void startService() {
		context.registerService(IMessageProviderModule.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IMessageProviderModule.class, this);
	}

	public HtmlMessageBean getNextMessage(MsgType msgType) {
		if (msgType != null) {
			switch (msgType) {
			case SSHX:
				return getMessage();
			case MICRO_INFO:
				//TODO
				break;
			default:
				return null;
			}
		}
		return null;
	}
	private HtmlMessageBean getMessage(){
	try {
		List objects=new ArrayList();
		long time=System.currentTimeMillis();
		SSHXMessageBO[] msgs = context.getService(ISSHXMsgManagementModule.class).getBroadcastableSSHXMsgs();
		if(log.isDebugEnabled()){
			log.debug("get sshx msg spend ="+(System.currentTimeMillis()-time));
		}
		if(msgs==null || msgs.length==0){
			if(log.isDebugEnabled()){
				log.debug("msgs is null");
			}
			return null;
		}
		CellarPhoneUser user=new CellarPhoneUserImpl();
		UserAttributes attrs=new UserAttributesImpl();
		StatisticsInfo StatisticsInfo=new StatisticsInfo();
		//FIXME: get user name and attributes
		MyContext ctx=new MyContext(user, attrs);
		objects.addAll(Arrays.asList(msgs));
		objects.add(user);
		objects.add(attrs);
		objects.add(ctx);
		StatelessRuleSession ruleSession=getPublishPolicySession();
		ruleSession.executeRules(objects);
		ZJHZMessage zjhzMessage=(ZJHZMessage) RuleHelper.getObject(objects, ZJHZMessage.class);
		if(zjhzMessage!=null){
			SSHXMessageBO sshxMsg=zjhzMessage.getOriginal();
			String htmlId = context.getService(ISSHXMsgManagementModule.class).getHtmlIdByMsgId(sshxMsg.getAdsId());
			HtmlMessage htmlMsg = null;
			if (StringUtils.isNotBlank(htmlId)) {
				htmlMsg = context.getService(IHtmlMessageManager.class).getHtmlMessage(htmlId.toString());
			}
			if (htmlMsg!=null) {
				HtmlMessageBean msg = new HtmlMessageBean();
				msg.setMsgId(sshxMsg.getAdsId());
				msg.setHtmlid(htmlId);
				msg.setHtmlMessage(htmlMsg);
				msg.setRemoteURL(htmlMsg.getOrigUrl());
				if (log.isDebugEnabled()) {
					log.info("Display mesage Id:"+msg.getMsgId());
				}
				return msg;
			}
			
		}
		
		
		
//		if(msgs!=null && msgs.length>0){
//			SSHXMessageBO sshxMsg=msgs[0];
//			String htmlId = context.getService(ISSHXMsgManagementModule.class).getHtmlIdByMsgId(1L);
//			HtmlMessage htmlMsg = null;
//			if (StringUtils.isNotBlank(htmlId)) {
//				htmlMsg = context.getService(IHtmlMessageManager.class).getHtmlMessage("1");
//			}
//			if (htmlMsg!=null) {
//				HtmlMessageBean msg = new HtmlMessageBean();
//				msg.setMsgId(sshxMsg.getAdsId());
//				msg.setHtmlMessage(htmlMsg);
//				return msg;
//			}
//		}
			
			
			
			
			
	} catch (RuleException e) {
		log.error("",e);
	} catch (ConfigurationException e) {
		log.error("",e);
	}
	return null;
}

protected StatelessRuleSession getPublishPolicySession()
		throws RuleException, ConfigurationException {
	RuleEngine engine = context.getService(RuleEngine.class);

	return (StatelessRuleSession) engine.getRuleRuntime()
			.createRuleSession("CMHELP_DXHZ_RULESET", null,
					RuleRuntime.STATELESS_SESSION_TYPE);
}


	public HtmlMessageBean[] getMessages(MsgType msgType, int limit) {
		if (msgType != null) {
			switch (msgType) {

				
			case MICRO_INFO:
				//TODO
				break;
			default:
				return null;
			}
		}
		return null;
	}

	public HtmlMessageBean[] getHomePageMessage() {
		return null;
	}
	public void notifyMessageRead(Long messageId, long readTime) {		
		context.getService(ISSHXMsgManagementModule.class).addReadSSHXForStatistics(messageId, readTime);		
	}

	public HtmlMessageBean[] getMessageHistory(int start,int limit) {
		
		String[] htmlIds=context.getService(ISSHXMsgManagementModule.class).getMessageHistory();
		if(htmlIds==null){
			return null;
		}

			List<HtmlMessageBean> ret = new LinkedList<HtmlMessageBean>();
			for (String htmlId :htmlIds) {
				HtmlMessage htmlMsg = context.getService(IHtmlMessageManager.class).getHtmlMessage(htmlId);
				if (htmlMsg!=null) {
					HtmlMessageBean msg = new HtmlMessageBean();
					msg.setHtmlMessage(htmlMsg);
					msg.setHtmlid(htmlId);
					msg.setRemoteURL(htmlMsg.getOrigUrl());
					try {
						Long time = context.getService(ISSHXMsgManagementModule.class).getHtmlRecordLastModified(htmlId);
						if(time!=null){
							msg.setReadTime(time);

						}
						
					} catch (Exception e) {
						log.warn("Get sshx message error", e);
					}
					ret.add(msg);
				}
			}	
			HtmlMessageBean[] result = ret.size()>0 ? ret.toArray(new HtmlMessageBean[ret.size()]):null;
			if (result!=null) {
				Arrays.sort(result,new Comparator<HtmlMessageBean>() {
					public int compare(HtmlMessageBean lhs, HtmlMessageBean rhs) {
						long flag = lhs.getReadTime()-rhs.getReadTime();
						if (flag>0) {
							return -1;
						}else if (flag<0) {
							return 1;
						}else{
							return 0;
						}
					}
				});
				return  copyOfRange(result, start, start+limit<htmlIds.length?start+limit:htmlIds.length,result.getClass());
			}
			
		return null;
	}
	@Override
	public int getTotalMessageHistorySize() {
		int total = 0;
		String[] msgIds = context.getService(ISSHXMsgManagementModule.class).getMessageHistory();
		if (msgIds!=null) {
			total = msgIds.length;
		}
		return total;
	}
	 private <T> T[] copyOfRange(T[] original, int from, int to,Class<? extends T[]> newType) {
	        int newLength = to - from;
	        if (newLength < 0)
	            throw new IllegalArgumentException(from + " > " + to);
	        T[] copy = ((Object)newType == (Object)Object[].class)
	                ? (T[]) new Object[newLength]
	                : (T[]) Array.newInstance(newType.getComponentType(), newLength);
	        System.arraycopy(original, from, copy, 0,
	                         Math.min(original.length - from, newLength));
	        return copy;
	   }
	 

		class MyContext implements ZJHZSMProcessingContext{
	        private BizUser user;
	        private UserAttributes attrs;
	        
	        
			
			public MyContext(BizUser user, UserAttributes attrs) {
				super();
				this.user = user;
				this.attrs = attrs;
			}

			@Override
			public boolean isUserInGroup(Long userId, String groupId) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public UserState getUserState() {
				return null;
			}

			@Override
			public UserAttributes getUserAttributes() {
				return attrs;
			}

			@Override
			public int getUserPrivilege() {
				  return 0;
			}

			@Override
			public boolean unQueuePublishingMessage(Message message) {
				return true;
			}

			@Override
			public BizUser getUser() {
				return this.user;
			}

			@Override
			public StatisticsInfo getUserStatistics() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public StatelessRuleSession getChannelSubRulesession(String channel) {
				return null;
			}

			@Override
			public void sendSMMessage(String destAddr, String message,
					Map<String, String> params) throws IOException {
				
			}

			@Override
			public boolean isUserPaused() {

				return false;
			}

			@Override
			public Object getPublishMessages() {
		
				return null;
			}
			
		}


		
}
