package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.bean.Region;

/**
 * 
 * @author cuizaixi
 * 
 */
public interface ICommonNumberService {
	boolean isHandle(String num);
	void initData();
	String getRegion();
	String getRegionWithoutBrand();
}
