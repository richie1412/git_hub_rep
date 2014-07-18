package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IUserActivationRestService;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
import com.wxxr.mobile.preference.api.IPreferenceManager;
/**
 * 头像的上传和下载
 * @author yangrunfei
 *
 */
public class UserHeadModule  extends AbstractModule<ComHelperAppContext> implements IUserHead {

	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IPreferenceManager.class);
		addRequiredService(IRestProxyService.class);
		addRequiredService(IClientConfigManagerService.class);
		
	}

	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IUserHead.class, this);
	}

	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		context.unregisterService(IUserHead.class, this);
	}

	@Override
	public boolean uploadIcon(String deviceId, byte[] icon) {
		try{
		 context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class,
				 getService(IClientConfigManagerService.class).getSSHXSyncRestServiceUrl()).uploadIcon(deviceId, icon);
		 return true;
		 }catch(Exception ee){
			 return false;
		 }
	}
	@Override
	public byte[] findIcon(String deviceId) {
		try{
			 
			 return context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class,
					 getService(IClientConfigManagerService.class).getSSHXSyncRestServiceUrl()).findIcon(deviceId);
			 }catch(Exception ee){
				 return null;
			 }
	}
	
	

}
