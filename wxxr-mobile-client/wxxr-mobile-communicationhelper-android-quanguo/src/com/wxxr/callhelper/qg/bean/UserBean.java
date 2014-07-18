package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
//  个人信息
public class UserBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nickName;
	private String telNum;
	private String provice;
	private byte[] icon;
	private String gendar;
	private int Birthday;
	public int getBirthday() {
		return Birthday;
	}
	public byte[] getIcon() {
		return icon;
	}
	public void setIcon(byte[] icon) {
		this.icon = icon;
	}
	public void setBirthday(int birthday) {
		Birthday = birthday;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getTelNum() {
		return telNum;
	}
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}
	public String getGendar() {
		return gendar;
	}
	public void setGendar(String gendar) {
		this.gendar = gendar;
	}
	public String getProvice() {
		return provice;
	}
	public void setProvice(String provice) {
		this.provice = provice;
	}
	@Override
	public String toString() {
		return "UserBean [nickName=" + nickName + ", telNum=" + telNum
				+ ", provice=" + provice + ", icon=" + icon + ", gendar="
				+ gendar + ", Birthday=" + Birthday + "]";
	}

}
