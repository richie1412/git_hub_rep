package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IPrivateSimiNetService;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IEmailBindingResource;
import com.wxxr.callhelper.qg.rpc.IUserCheckCodeResource;
import com.wxxr.common.sync.vo.ResultBaseVO;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;

/**
 * 广东版 私密锁 的相关操作，邮件绑定，验证码获取，验证
 * 
 * @author yangrunfei
 * 
 */
public class PrivateSMServiceNetModule extends
		AbstractModule<ComHelperAppContext> implements IPrivateSimiNetService {

	private static final Trace log = Trace
			.register(PrivateSMServiceNetModule.class);

	@Override
	protected void initServiceDependency() {
		addRequiredService(IClientConfigManagerService.class);
		addRequiredService(IRestProxyService.class);
	}

	@Override
	protected void startService() {
		context.registerService(IPrivateSimiNetService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IPrivateSimiNetService.class, this);
	}

	@Override
	public boolean addEmailBinding(String deviceId, String email) {
		try {

			ResultBaseVO aa = context
					.getService(IRestProxyService.class)
					.getRestService(
							IEmailBindingResource.class,
							getService(IClientConfigManagerService.class)
									.getSSHXSyncRestServiceUrl())
					.addEmailBinding(deviceId, email);

			if (aa != null && aa.getResulttype() == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ee) {
			return false;
		}

	}

	@Override
	public boolean updateEmailBinding(String deviceId, String email) {
		try {

			ResultBaseVO aa = context
					.getService(IRestProxyService.class)
					.getRestService(
							IEmailBindingResource.class,
							getService(IClientConfigManagerService.class)
									.getSSHXSyncRestServiceUrl())
					.updateEmailBinding(deviceId, email);

			if (aa != null && aa.getResulttype() == 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception ee) {
			return false;
		}
	}

	@Override
	public String findEmail(String deviceId) {
		try {

			ResultBaseVO aa = context
					.getService(IRestProxyService.class)
					.getRestService(
							IEmailBindingResource.class,
							getService(IClientConfigManagerService.class)
									.getSSHXSyncRestServiceUrl())
					.findEmail(deviceId);

			aa.getResultInfo();
			return aa.getResultInfo();
		} catch (Exception ee) {
			return "";
		}
	}

	@Override
	public boolean generateCheckCode(String deviceId, String email)
			throws Exception {
		try {

			ResultBaseVO aa = context
					.getService(IRestProxyService.class)
					.getRestService(
							IUserCheckCodeResource.class,
							getService(IClientConfigManagerService.class)
									.getSSHXSyncRestServiceUrl())
					.generateCheckCode(deviceId, email);

			if (aa != null && aa.getResulttype() == 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception ee) {
			return false;
		}
	}

	@Override
	public boolean verifyCheckCode(String deviceId, String email,
			String checkcode) {
		try {

			ResultBaseVO aa = context
					.getService(IRestProxyService.class)
					.getRestService(
							IUserCheckCodeResource.class,
							getService(IClientConfigManagerService.class)
									.getSSHXSyncRestServiceUrl())
					.verifyCheckCode(deviceId, email, checkcode);
			if (aa != null && aa.getResulttype() == 0) {
				return true;
			} else {
				return false;
			}

		} catch (Exception ee) {
			return false;
		}
	}

}