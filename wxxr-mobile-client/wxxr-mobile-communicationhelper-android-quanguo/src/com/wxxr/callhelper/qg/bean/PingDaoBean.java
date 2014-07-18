package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

public class PingDaoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int SourceID;
	private String name;
	private String channalCode;

	public PingDaoBean(String channalCode, String name) {
		this.name = name;
		this.channalCode = channalCode;
	}
	public PingDaoBean(int sourceID, String name, String channalCode) {
		SourceID = sourceID;
		this.name = name;
		this.channalCode = channalCode;
	}
	public int getSourceID() {
		return SourceID;
	}
	public void setSourceID(int sourceID) {
		SourceID = sourceID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getChannalCode() {
		return channalCode;
	}
	public void setChannalCode(String channalCode) {
		this.channalCode = channalCode;
	}
	@Override
	public String toString() {
		return "PingDaoBean [SourceID=" + SourceID + ", name=" + name
				+ ", channalCode=" + channalCode + "]";
	}

}
