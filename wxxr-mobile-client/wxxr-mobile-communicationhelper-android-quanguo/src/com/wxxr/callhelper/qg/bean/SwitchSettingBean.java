package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;
/**
 * 开关界面实体类
 * 
 * @author cuizaixi
 * 
 */
public class SwitchSettingBean implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private String lable;
	private int descriptionId;
	private String SPKey;
	private int defaultSetting;
	public SwitchSettingBean() {

	}
	public SwitchSettingBean(String title, String lable, int descriptionId,
			String sPKey, int defaultSetting) {
		super();
		this.title = title;
		this.lable = lable;
		this.descriptionId = descriptionId;
		SPKey = sPKey;
		this.defaultSetting = defaultSetting;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLable() {
		return lable;
	}
	public void setLable(String lable) {
		this.lable = lable;
	}
	public int getDescriptionId() {
		return descriptionId;
	}
	public void setDescriptionId(int descriptionId) {
		this.descriptionId = descriptionId;
	}
	public String getSPKey() {
		return SPKey;
	}
	public void setSPKey(String sPKey) {
		SPKey = sPKey;
	}
	public int getDefaultSetting() {
		return defaultSetting;
	}
	public void setDefaultSetting(int defaultSetting) {
		this.defaultSetting = defaultSetting;
	}

}
