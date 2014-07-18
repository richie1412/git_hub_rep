package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;

import android.content.Context;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IFetchURL;
import com.wxxr.callhelper.qg.utils.MobileBusinessStore;
import com.wxxr.callhelper.qg.utils.XMLParstTool;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;

public class MoblieBusinessModule extends AbstractModule<ComHelperAppContext>
		implements IMoblieBusiness {

	final static long timeout = 60 * 60 * 1000 * 5;

	private static Trace log = Trace.register(ContentManager.class);

	@Override
	public ArrayList<MoblieBusinessBean> getBusinessCheckFromLocal(
			final Context contxt) {
		// TODO Auto-generated method stub
		String context = MobileBusinessStore.getInstance(contxt)
				.getBusinessCheckText();
		String realcontent = "";
		if (context.length() > 0) {
			String[] contents = context.split("#");
			if ((System.currentTimeMillis() - Long.parseLong(contents[0])) > timeout) {
				new Thread() {
					public void run() {
						getBusinessCheckFromServer(contxt);
					}
				}.start();
			} else {
				realcontent = contents[1];
			}
		} else {
			getBusinessCheckFromServer(contxt);

		}
		context = MobileBusinessStore.getInstance(contxt)
				.getBusinessCheckText();
		if (context != null && context.length() > 10) {
			realcontent = context.split("#")[1];
		}
		ArrayList<MoblieBusinessBean> fd = XMLParstTool
				.parseBusinessBeanXml(realcontent);

		return fd;
	}

	@Override
	public ArrayList<MoblieBusinessBean> getBusinessProcessFromLocal(
			final Context contxt) {
		// TODO Auto-generated method stub
		String context = MobileBusinessStore.getInstance(contxt)
				.getBusinessProcessText();
		String realcontent = "";
		if (context.length() > 0) {
			String[] contents = context.split("#");
			if ((System.currentTimeMillis() - Long.parseLong(contents[0])) > timeout) {
				new Thread() {
					public void run() {
						getBusinessProcessFromServer(contxt);
					}
				}.start();
			} else {
				realcontent = contents[1];
			}
		} else {
			getBusinessProcessFromServer(contxt);
		}
		context = MobileBusinessStore.getInstance(contxt)
				.getBusinessProcessText();
		if (context != null && context.length() > 10) {
			realcontent = context.split("#")[1];
		}
		ArrayList<MoblieBusinessBean> fd = XMLParstTool
				.parseBusinessBeanXml(realcontent);

		return fd;
	}

	public void getBusinessProcessFromServer(Context contxt) {
		// TODO Auto-generated method stub
		String code = AppUtils.getService(IClientCustomService.class)
				.getProviceCode();
		if (code != null) {
			code = code.toLowerCase();
		}

		String content = getService(IRestProxyService.class).getRestService(
				IFetchURL.class,
				getService(IClientConfigManagerService.class)
						.getSSHXSyncRestServiceUrl()).getMsg("1",
				"biz_conduction", code, "", "");
		if (content != null && content.length() > 0) {
			MobileBusinessStore.getInstance(contxt).setBusinessProcessText(
					System.currentTimeMillis() + "#" + content);
		}

	}

	public void getBusinessCheckFromServer(Context contxt) {
		// TODO Auto-generated method stub
		String code = AppUtils.getService(IClientCustomService.class)
				.getProviceCode();
		if (code != null) {
			code = code.toLowerCase();
		}
		String content = getService(IRestProxyService.class).getRestService(
				IFetchURL.class,
				getService(IClientConfigManagerService.class)
						.getSSHXSyncRestServiceUrl()).getMsg("1", "biz_query",
				code, "", "");
		if (content != null && content.length() > 0) {
			MobileBusinessStore.getInstance(contxt).setBusinessCheckText(
					System.currentTimeMillis() + "#" + content);
		}
	}

	@Override
	public MoblieBusinessBean getLaiDianTiXing() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}

			final Context context = this.context.getApplication()
					.getAndroidApplication().getBaseContext();
			String content = MobileBusinessStore.getInstance(context)
					.getLaidianxitingCode(code);
			MoblieBusinessBean resul = XMLParstTool
					.parseOneBusinessBeanXml(content);
			if (resul == null) {
				content = getService(IRestProxyService.class).getRestService(
						IFetchURL.class,
						getService(IClientConfigManagerService.class)
								.getUrlOfGetUrls()).getMsg("1", "ldtx", code,
						"", "");
				if (content != null && content.length() > 10) {
					MobileBusinessStore.getInstance(context)
							.setLaidianxitingCode(code, content);
					resul = XMLParstTool.parseOneBusinessBeanXml(content);
				}

			} else {
				try {
					new Thread() {
						public void run() {
							String code = AppUtils.getService(
									IClientCustomService.class)
									.getProviceCode();
							if (code != null) {
								code = code.toLowerCase();
							}
							String content = getService(IRestProxyService.class)
									.getRestService(
											IFetchURL.class,
											getService(
													IClientConfigManagerService.class)
													.getUrlOfGetUrls()).getMsg(
											"1", "ldtx", code, "", "");
							if (content != null && content.length() > 10) {
								MobileBusinessStore.getInstance(context)
										.setLaidianxitingCode(code, content);
							}
						}
					}.start();

				} catch (Exception ee) {

				}
			}

			return resul;
		} catch (Exception e) {
			// TODO: handle exception
			if (log.isDebugEnabled()) {
				log.debug("getLaiDianTiXing  err...." + e.toString());
			}
		}
		return null;
	}

	@Override
	public MoblieBusinessBean getDuanXinHuiZhi() {
		try {
			String code = AppUtils.getService(IClientCustomService.class)
					.getProviceCode();
			if (code != null) {
				code = code.toLowerCase();
			}

			final Context context = this.context.getApplication()
					.getAndroidApplication().getBaseContext();
			String content = MobileBusinessStore.getInstance(context)
					.getDuanxinhuizhiCode(code);
			MoblieBusinessBean resul = XMLParstTool
					.parseOneBusinessBeanXml(content);
			if (resul == null) {
				content = getService(IRestProxyService.class).getRestService(
						IFetchURL.class,
						getService(IClientConfigManagerService.class)
								.getUrlOfGetUrls()).getMsg("1", "dxhz", code,
						"", "");
				if (content != null && content.length() > 10) {
					MobileBusinessStore.getInstance(context)
							.setDuanxinhuizhiCode(code, content);
					resul = XMLParstTool.parseOneBusinessBeanXml(content);
				}

			} else {
				try {
					new Thread() {
						public void run() {
							String code = AppUtils.getService(
									IClientCustomService.class)
									.getProviceCode();
							if (code != null) {
								code = code.toLowerCase();
							}
							String content = getService(IRestProxyService.class)
									.getRestService(
											IFetchURL.class,
											getService(
													IClientConfigManagerService.class)
													.getUrlOfGetUrls()).getMsg(
											"1", "dxhz", code, "", "");
							if (content != null && content.length() > 10) {
								MobileBusinessStore.getInstance(context)
										.setDuanxinhuizhiCode(code, content);
							}
						}
					}.start();

				} catch (Exception ee) {

				}
			}

			return resul;
		} catch (Exception e) {
			// TODO: handle exception
			if (log.isDebugEnabled()) {
				log.debug("getDuanXinHuiZhi  err...." + e.toString());
			}
		}
		return null;
	}

	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stubz
		addRequiredService(IClientConfigManagerService.class);

	}

	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IMoblieBusiness.class, this);
	}

	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		removeRequiredService(ClientConfigManagerService.class);
		context.unregisterService(IMoblieBusiness.class, this);
	}

}
