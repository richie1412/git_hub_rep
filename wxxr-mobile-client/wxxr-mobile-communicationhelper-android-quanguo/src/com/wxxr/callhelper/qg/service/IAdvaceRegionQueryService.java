package com.wxxr.callhelper.qg.service;

import android.content.Context;

import com.wxxr.callhelper.qg.bean.Region;
/**
 * 
 * @author cuizaixi
 * 
 */
public interface IAdvaceRegionQueryService {
	String getRegionByNum(String number, Context context);
	// 带前缀的号码
	String getRegionByDialogNumber(String number);
	// 显示归属地，但是不包括品牌名称
	String getRegionWithoutBrand(String number);
}
