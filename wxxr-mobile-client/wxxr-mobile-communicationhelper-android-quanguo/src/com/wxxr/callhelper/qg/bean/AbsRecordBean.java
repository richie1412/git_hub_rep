package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

/**
 * 
 * @author cuizaixi
 * @create 2014-3-18 上午11:45:00
 */
public class AbsRecordBean implements Serializable {
	private static final long serialVersionUID = 1L;
	protected int id;// 记录唯一的id.
	protected int state;// 记录 当前的状态
	protected long date;// 记录发生的时间

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}

}
