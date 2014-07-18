package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.exception.NetworkNotAvailableException;
import com.wxxr.callhelper.qg.rpc.ArticlePathCollectionVo;
import com.wxxr.callhelper.qg.rpc.ChannelMsgPageVo;
import com.wxxr.callhelper.qg.rpc.IArticlePathResource;
import com.wxxr.callhelper.qg.rpc.IChannelMsgService;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
/**
 * 获取 21个频道 详细内容的 页面
 * 
 * @author yangrunfei
 * 
 */
public class MicoNewsModleService extends AbstractModule<ComHelperAppContext>
		implements
			IMicoNewsService {

	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IRestProxyService.class);
		addRequiredService(IClientConfigManagerService.class);
	}

	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IMicoNewsService.class, this);
	}

	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		context.unregisterService(IMicoNewsService.class, this);
	}

	@Override
	public ChannelMsgPageVo getChanleDetailMessage(String chanleid,
			String countperpage, String curpage)
			throws NetworkNotAvailableException {
		IDataExchangeCoordinator s = context
				.getService(IDataExchangeCoordinator.class);
		if (s.checkAvailableNetwork() < 0) {
			throw new NetworkNotAvailableException(
					"没有网络连接，无法从服务器下载你的设置，请配置网络连接");
		}
		ChannelMsgPageVo bb = getService(IRestProxyService.class)
				.getRestService(
						IChannelMsgService.class,
						getService(IClientConfigManagerService.class)
								.getSSHXSyncRestServiceUrl()).getChannelMsg(										
					AppUtils.getService(IClientCustomService.class).getProviceCode(), "CM", chanleid, countperpage, curpage);
		return bb;
	}

}
