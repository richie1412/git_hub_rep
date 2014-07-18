package com.wxxr.callhelper.qg.bean;

import java.util.Date;

import android.media.MediaPlayer;
import android.view.View;

public class CallRecordBean {

	private String user;
	private String phoneNum;
	private String date;
	private String duration;
	private String callRecordState;
	private String fileName;
	private Date org_date;
	private boolean status=false; //播放标记 ture 为播放中，false为停止
	private boolean durationStatus = false;
	private View referView;
	
	public boolean isDurationStatus() {
		return durationStatus;
	}
	public void setDurationStatus(boolean durationStatus) {
		this.durationStatus = durationStatus;
	}
	public View getReferView() {
		return referView;
	}
	public void setReferView(View referView) {
		this.referView = referView;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public Date getOrg_date() {
		return org_date;
	}
	public void setOrg_date(Date org_date) {
		this.org_date = org_date;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCallRecordState() {
		return callRecordState;
	}
	public void setCallRecordState(String callRecordState) {
		this.callRecordState = callRecordState;
	}
//	public MediaPlayer getPlayer() {
//		return player;
//	}
//	public void setPlayer(MediaPlayer player) {
//		this.player = player;
//	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	
	
}
