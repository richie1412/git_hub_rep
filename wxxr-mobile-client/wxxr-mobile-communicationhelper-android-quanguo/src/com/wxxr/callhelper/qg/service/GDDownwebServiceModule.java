package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.utils.GDWebCachePath;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
/**
 * 广东版，需要从服务器获取web 的数据
 * @author yangrunfei
 *
 */
public  class GDDownwebServiceModule extends AbstractModule<ComHelperAppContext> implements IGDDownWebService{

	
	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IOfficeLineHtmlProvideService.class);
		addRequiredService(IContentManager.class);
	}

	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IGDDownWebService.class, this);
	}

	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		context.unregisterService(IGDDownWebService.class, this);
	}
	
	
	int	count=0;
	

	@Override
	public ArrayList<HtmlMessageBean> getZhushouBobaoDefault( ) {
		ArrayList<HtmlMessageBean> array=new ArrayList<HtmlMessageBean>();
		GDWebCachePath aa=GDWebCachePath.getInstance(context.getApplication().getAndroidApplication().getApplicationContext());
		String[] downkeys=aa.getAllPathOfKeys("zsbb");
		for (String key : downkeys) {
			HtmlMessage  mess=getService(IContentManager.class).getHtmlMessage(key);			
			if(mess!=null&&mess.getUrl()!=null&&mess.getTitle()!=null){
				HtmlMessageBean bean=new HtmlMessageBean();
				bean.setHtmlMessage(mess);
				array.add(bean);
			}
		}
		           
		return array;
	}

	@Override
	public ArrayList<HtmlMessageBean> getCheapsOfOneCity(String pkey) {
		ArrayList<HtmlMessageBean> array=new ArrayList<HtmlMessageBean>();
		GDWebCachePath aa=GDWebCachePath.getInstance(context.getApplication().getAndroidApplication().getApplicationContext());
		String[] downkeys=aa.getAllPathOfKeys(pkey);
		for (String key : downkeys) {
			HtmlMessage  mess=getService(IContentManager.class).getHtmlMessage(key);			
			if(mess!=null&&mess.getUrl()!=null){
				HtmlMessageBean bean=new HtmlMessageBean();
				bean.setHtmlMessage(mess);
				array.add(bean);
			}
		}
		
		return array;
	}

	

}
