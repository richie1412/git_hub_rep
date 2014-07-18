package com.wxxr.test.business;

import android.text.TextUtils;

public class BusinessManagerBean {

	private String name;
	private String businessName;
	private String businessIcon;
	private String businessCode;
	
	public String[] getBusinessNames(){
		if(!TextUtils.isEmpty(businessName)){
			return businessName.split("、");
		}
		return null;
	}
	
	public String[] getBusinessIcons(){
		if(!TextUtils.isEmpty(businessIcon)){
			return businessIcon.split("、");
		}
		return null;
	}
	
	public String[] getBusinessCode() {
		if(!TextUtils.isEmpty(businessCode)){
			return businessCode.split("、");
		}
		return null;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public void setBusinessIcon(String businessIcon){
		this.businessIcon = businessIcon;
	}
	
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	
}
