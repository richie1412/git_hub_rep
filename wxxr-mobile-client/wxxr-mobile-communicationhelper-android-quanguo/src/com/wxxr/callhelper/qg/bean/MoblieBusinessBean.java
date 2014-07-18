package com.wxxr.callhelper.qg.bean;

public class MoblieBusinessBean {
	String icon;
	String tile;
	/**
	 * 发送的内容
	 */
	String smsContent;
	String description;
	/**
	 * 端口号
	 */
	private String SpNumber;

	public String getSpNumber() {
		return SpNumber;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTile() {
		return tile;
	}

	public void setTile(String tile) {
		this.tile = tile;
	}

	public String getSmscode() {
		return smsContent;
	}

	public void setSmscode(String smscode) {
		this.smsContent = smscode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String pdescription) {
		// TODO Auto-generated method stub
		description = pdescription;
	}

	public void setSpNumber(String pSpNumber) {
		// TODO Auto-generated method stub
		SpNumber=pSpNumber;
	}
}
