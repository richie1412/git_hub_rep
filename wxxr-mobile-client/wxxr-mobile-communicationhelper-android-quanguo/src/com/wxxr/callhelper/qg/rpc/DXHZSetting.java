/*
 * @(#)DXHZSetting.java	 2011-6-28
 *
 * Copyright 2004-2011 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.rpc;

import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;
import com.wxxr.mobile.core.util.StringUtils;

@XmlRootElement(name = "Customer")
public class DXHZSetting {
	public static String KEY_FYCHANNEL_DATE="fychanneldate";
	public static String KEY_USER_DATE="birthday";
	public static String KEY_USER_GENDER="sex";
	private String mobileNumber;// 定制用户手机号
	private Integer receiveStyle;// 接收模式(指定1(手机上没指定模式),提醒2,全开3)
	private String subscriptions;// 频道信息
	private Integer run;// 暂停激活
	private Integer grpStyle;// 单条模式0 群发模式1
	private String fychanneldate;// 定制妇幼频道:预产期或生日日期
	private Boolean enableService;// 暂停激活
	private Integer brandId;// 手机品牌
	private Boolean noannoy;// 免打扰状态（已定制值为：1；无定制值为0）
	private Boolean ltms;// "聊天模式状态（开通：true；关闭:false）
	private Integer sex; // 用户性别：0: 女,1:男
	private String birthday; 	// 用户生日
	private Boolean updated;
	private String receiptSendStyle;//回执短信发送方式:1普通短信,2闪电短信
	
	private String   		memo1;   				//预留字段
	
	private String   		memo2;   				//预留字段
	private String   		memo3;   				//预留字段
	
	public String getMemo1() {
		return memo1;
	}

	public void setMemo1(String memo1) {
		this.memo1 = memo1;
	}

	public String getMemo2() {
		return memo2;
	}

	public void setMemo2(String memo2) {
		this.memo2 = memo2;
	}

	public String getMemo3() {
		return memo3;
	}

	public void setMemo3(String memo3) {
		this.memo3 = memo3;
	}

	

	public String getReceiptSendStyle() {
		return receiptSendStyle;
	}

	public void setReceiptSendStyle(String receiptSendStyle) {
		this.receiptSendStyle = receiptSendStyle;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getReceiveStyle() {
		return receiveStyle;
	}

	public void setReceiveStyle(Integer receiveStyle) {
		this.receiveStyle = receiveStyle;
	}

	public String getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(String subscriptions) {
		this.subscriptions = subscriptions;
	}

	public Integer getRun() {
		return run;
	}

	public void setRun(Integer run) {
		this.run = run;
	}

	public Integer getGrpStyle() {
		return grpStyle;
	}

	public void setGrpStyle(Integer grpStyle) {
		this.grpStyle = grpStyle;
	}

	public String getFychanneldate() {
		return fychanneldate;
	}

	public void setFychanneldate(String fychanneldate) {
		this.fychanneldate = fychanneldate;
	}

	public Boolean getEnableService() {
		return enableService;
	}

	public void setEnableService(Boolean enableService) {
		this.enableService = enableService;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public Boolean getNoannoy() {
		return noannoy;
	}

	public void setNoannoy(Boolean noannoy) {
		this.noannoy = noannoy;
	}

	public Boolean getLtms() {
		return ltms;
	}

	public void setLtms(Boolean ltms) {
		this.ltms = ltms;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}
	
	public List<String> getChannels() {
		if(StringUtils.isBlank(subscriptions)){
			return null;
		}else{
			String[] tokens = StringUtils.split(subscriptions, ',');
			LinkedList<String> result = new LinkedList<String>();
			for (String token : tokens) {
				token = StringUtils.trim(token);
				result.add(token);
			}
			return result;
		}
	}
	
//	public boolean hasChannel(String ch) {
//		if(StringUtils.isBlank(subscriptions)){
//			return false;
//		}else{
//			String[] tokens = StringUtils.split(subscriptions, ',');
//			for (String token : tokens) {
//				token = StringUtils.trim(token);
//				if(token.equals(ch)){
//					return true;
//				}
//			}
//			return false;
//		}
//	}
//	
//	public void addChannel(String ch) {
//		if(hasChannel(ch)){
//			return;
//		}
//		this.subscriptions = new StringBuffer(this.subscriptions).append(',').append(ch).toString();
//	}
//	
//	public boolean removeChannel(String ch){
//		if(!hasChannel(ch)){
//			return false;
//		}
//		boolean removed = false;
//		String[] tokens = StringUtils.split(subscriptions, ',');
//		LinkedList<String> result = new LinkedList<String>();
//		for (String token : tokens) {
//			token = StringUtils.trim(token);
//			if(!token.equals(ch)){
//				result.add(token);
//			}else{
//				removed = true;
//			}
//		}
//		if(result.isEmpty()){
//			this.subscriptions = null;
//		}else{
//			this.subscriptions = StringUtils.join(result.iterator(), ',');
//		}
//		return removed;
//	}
//

	public void updateTo(Dictionary<String, String> map) {
		if(this.enableService != null){
			map.put("enableService", this.enableService.toString());
		}else{
			map.remove("enableService");
		}
		if(this.ltms != null){
			map.put("ltms", this.ltms.toString());
		}else{
			map.remove("ltms");
		}
		if(this.noannoy != null){
			map.put("noannoy", this.noannoy.toString());
		}else{
			map.remove("noannoy");
		}
		if(this.birthday != null){
			map.put("birthday", this.birthday);
		}else{
			map.remove("birthday");
		}
		if(this.brandId != null){
			map.put("brandId", this.brandId.toString());
		}else{
			map.remove("brandId");
		}
		if(this.fychanneldate != null){
			map.put("fychanneldate", this.fychanneldate.toString());
		}else{
			map.remove("fychanneldate");
		}
		if(this.grpStyle != null){
			map.put("grpStyle", this.grpStyle.toString());
		}else{
			map.remove("grpStyle");
		}
		if(this.mobileNumber != null){
			map.put("mobileNumber", this.mobileNumber.toString());
		}else{
			map.remove("mobileNumber");
		}
		if(this.receiveStyle != null){
			map.put("receiveStyle", this.receiveStyle.toString());
		}else{
			map.remove("receiveStyle");
		}
		if(this.run != null){
			map.put("run", this.run.toString());
		}else{
			map.remove("run");
		}
		if(this.sex != null){
			map.put("sex", this.sex.toString());
		}else{
			map.remove("sex");
		}
		if(this.subscriptions != null){
			map.put("subscriptions", this.subscriptions.toString());
		}else{
			map.remove("subscriptions");
		}
		if(this.updated != null){
			map.put("updated", this.updated.toString());
		}else{
			map.remove("updated");
		}
		if(this.receiptSendStyle != null){
			map.put("receiptSendStyle", this.receiptSendStyle.toString());
		}else{
			map.remove("receiptSendStyle");
		}
		
	}
	
	public void updateFrom(Dictionary<String, String> map ) {
		String s = map.get("enableService");
		if(s != null){
			this.enableService = Boolean.valueOf(s);
		}
		s = map.get("ltms");
		if(s != null){
			this.ltms = Boolean.valueOf(s);
		}
		s = map.get("noannoy");
		if(s != null){
			this.noannoy = Boolean.valueOf(s);
		}
		this.birthday = map.get("birthday");
		
		s = map.get("brandId");
		if(s != null){
			this.brandId = Integer.valueOf(s);
		}
		this.fychanneldate = map.get("fychanneldate");
		s = map.get("grpStyle");
		if(s != null){
			this.grpStyle = Integer.valueOf(s);
		}
		this.mobileNumber = map.get("mobileNumber");
		s = map.get("receiveStyle");
		if(s != null){
			this.receiveStyle = Integer.valueOf(s);
		}
		s = map.get("run");
		if(s != null){
			this.run = Integer.valueOf(s);
		}
		s = map.get("sex");
		if(s != null){
			this.sex = Integer.valueOf(s);
		}
		this.subscriptions = map.get("subscriptions");	
		s = map.get("updated");
		if(s != null){
			this.updated = Boolean.valueOf(s);
		}
		s = map.get("receiptSendStyle");
		if(s != null){
			this.receiptSendStyle = s;
		}
	}

	/**
	 * @return the updated
	 */
	public Boolean getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(Boolean updated) {
		this.updated = updated;
	}
}
