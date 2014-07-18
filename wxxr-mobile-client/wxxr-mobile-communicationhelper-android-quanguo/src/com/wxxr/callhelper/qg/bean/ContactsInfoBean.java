package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

/**
 * ͨѶ¼��ϵ�ˣ���������͵绰���룩
 * 
 * @author cuizaixi
 * 
 */
public class ContactsInfoBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private String telNum;
	public ContactsInfoBean(String name, String telNum) {
		this.name = name;
		this.telNum = telNum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelNum() {
		return telNum;
	}
	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}
	@Override
	public String toString() {
		return "ContactsInfoBean [name=" + name + ", telNum=" + telNum + "]";
	}

}
