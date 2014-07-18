package com.wxxr.callhelper.qg.service;

public interface IClientCustomService {
	public static final String KEY_KT_COMMAND = "key_kt_command";

	public void setProviceCode(String provicecode);
	public void setCityName(String cityname);
	public String getCityName();
	public String getProviceCode();
	// 获取开通业务的短信命令
	public String getKTCommand();
	// 是否显示注册协议
	public boolean isShowRegisterProtocol();
	// 是否显示关于我们
	public boolean isShowAboutus();
	// 是否显示注册弹出框
	public boolean isShowRegisterSuccessDialog();

	public String get(String key);

	// 首页微资讯地址
	public String getHomeInfoUrl();
	// 微资讯每日悦读地址
	public String getEveryDayInfoUrl();

}
