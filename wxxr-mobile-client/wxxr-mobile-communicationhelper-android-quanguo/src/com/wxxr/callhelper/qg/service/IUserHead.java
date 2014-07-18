package com.wxxr.callhelper.qg.service;

public interface IUserHead {
	/**
	 * 上传图片
	 * 
	 * @return
	 */

	boolean uploadIcon(String deviceId,byte[] icon);
	
	
	/**
	 * 下载图片
	 * 
	 * @return
	 */

    public byte[] findIcon(String deviceId);
}
