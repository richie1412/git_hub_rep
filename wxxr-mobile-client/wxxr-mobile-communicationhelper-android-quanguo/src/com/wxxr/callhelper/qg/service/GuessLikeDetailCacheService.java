package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.select.Evaluator.CssNthEvaluator;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.constant.ChannelStaticData;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * 广东版——猜你喜欢数据缓存
 * 
 * @author cuizaixi
 * 
 */
public class GuessLikeDetailCacheService
		extends
			AbstractModule<ComHelperAppContext>
		implements
			IGuessLikeDetailCache<String> {
	private ManagerSP sp;
	private String mChannleCode;
	private String[] channels;
	@Override
	protected void initServiceDependency() {

	}
	@Override
	protected void startService() {
		channels = ChannelStaticData.channels;
		sp = ManagerSP.getInstance(context.getApplication()
				.getAndroidApplication().getApplicationContext());
		context.registerService(IGuessLikeDetailCache.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IGuessLikeDetailCache.class, this);
	}
	@Override
	public void setCachedMessage(List<List<String>> messages, String channelCode) {
		this.mChannleCode = channelCode;
		ensureChannleCodeExit(channelCode);
		StringBuilder sb = new StringBuilder();
		for (List<String> list : messages) {
			for (String string : list) {
				sb.append(string + "%%");
			}
		}
		sp.update(this.mChannleCode, sb.toString());
	}
	@Override
	public List<String> getCachedMessage(String channelCode) {
		String message = sp.get(channelCode, null);
		if (!StringUtil.isEmpty(message)) {
			ArrayList<String> list = new ArrayList<String>();
			String[] messages = message.split("%%");
			for (String string : messages) {
				list.add(string);
			}
			return list;
		}
		return null;
	}
	@Override
	public void clearCachedMessage(String ChannelCode) {
		sp.update(ChannelCode, null);
	}
	@Override
	public String getChannelCode() {
		return this.mChannleCode;
	}
	@Override
	public void setChannelCode(String channleCode) {
		this.mChannleCode = channleCode;
	}
	private void ensureChannleCodeExit(String channelCode) {
		if (!Arrays.asList(channels).contains(channelCode)) {
			throw new NullPointerException(" There is no such ChannelCode"
					+ channelCode + "in current channle, checked it befor");
		}
		return;
	}
}
