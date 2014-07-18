package com.wxxr.callhelper.qg.bean;
/**
 * 推荐应用的实体类
 * 
 * @author cuizaixi
 * 
 */
public class AppsRecommend {
	private int sourceID;
	private String name;
	private String description;
	private String downloadUrl;
	public int getSourceID() {
		return sourceID;
	}

	public AppsRecommend(String name, int sourceID, String description,
			String downloadUrl) {
		this.sourceID = sourceID;
		this.name = name;
		this.description = description;
		this.downloadUrl = downloadUrl;
	}

	public void setSourceID(int sourceID) {
		this.sourceID = sourceID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

}
