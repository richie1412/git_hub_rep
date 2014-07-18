package com.wxxr.callhelper.qg.service;

import android.content.SharedPreferences;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

public class ClientCustomModule extends AbstractModule<ComHelperAppContext>
		implements
			IClientCustomService {
	public static final String KEY_PROVICE_CODE = "key_provice_code";
	private final String PREFERENCE_NAME = "ClientCustomModule";
	private static final String KEY_CITY_NAME = "key_city_code";
	private SharedPreferences sp;
	private ManagerSP msp;

	@Override
	public void setProviceCode(String provicecode) {
		msp.update(KEY_PROVICE_CODE, provicecode);
	}

	@Override
	public String getProviceCode() {
		return msp.get(KEY_PROVICE_CODE, "");
	}

	@Override
	public String get(String key) {
		return sp.getString(key, null);
	}
	@Override
	public String getKTCommand() {
		if (Constant.PROVICE_GD.equals(getProviceCode())) {
			return "BLTXZS";
		} else if (Constant.PROVICE_SC.equals(getProviceCode())) {
			return "kttxzsa";
		}
		return "BLTXZS";
	}

	@Override
	public boolean isShowRegisterProtocol() {
		if (Constant.PROVICE_GD.equals(getProviceCode())) {
			return true;
		}
		return false;
	}
	public boolean isShowRegisterSuccessDialog() {
		if (Constant.PROVICE_GD.equals(getProviceCode())
				|| Constant.PROVICE_SC.equals(getProviceCode())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isShowAboutus() {
		if (Constant.PROVICE_GD.equals(getProviceCode())) {
			return true;
		}
		return false;
	}
	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {

		// sp =
		// context.getApplication().getAndroidApplication().getApplicationContext()
		// .getApplicationContext().getSharedPreferences(PREFERENCE_NAME,Activity.MODE_PRIVATE);
		msp = ManagerSP.getInstance(context.getApplication()
				.getAndroidApplication().getApplicationContext());
		context.registerService(IClientCustomService.class, this);
		init();

	}

	private void init() {

	}

	@Override
	protected void stopService() {
		context.unregisterService(IClientCustomService.class, this);

	}

	@Override
	public String getHomeInfoUrl() {
		if (Constant.PROVICE_GD.equals(getProviceCode())) {
			return "/txzs/home";
		} else if (Constant.PROVICE_SC.equals(getProviceCode())) {
			return "/sctxzs/home";
		} else if (Constant.PROVICE_SH.equals(getProviceCode())) {
			return "/shtxzs/home";
		}
		return "/commontxzs/home";
	}

	@Override
	public String getEveryDayInfoUrl() {
		if (Constant.PROVICE_GD.equals(getProviceCode())) {
			return "/txzs/webs.html";
		} else if (Constant.PROVICE_SC.equals(getProviceCode())) {
			return "/sctxzs/webs.html";
		} else if (Constant.PROVICE_SH.equals(getProviceCode())) {
			return "/shtxzs/webs.html";
		}
		return "/commontxzs/webs.html";
	}

	@Override
	public void setCityName(String cityname) {
		msp.update(KEY_CITY_NAME, cityname);
	}
	@Override
	public String getCityName() {
		return msp.get(KEY_CITY_NAME, null);
	}

}
