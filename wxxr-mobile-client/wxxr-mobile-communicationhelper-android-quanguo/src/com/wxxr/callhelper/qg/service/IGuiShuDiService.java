package com.wxxr.callhelper.qg.service;

import android.content.ContentValues;

import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.mobile.core.api.IProgressMonitor;

public interface IGuiShuDiService {
	/**
	 * 根据手机号/区号 查找归属地 （异步） 1.手机号码 必须十一位 2.区号 必须是3位或者4位
	 * 
	 * @param phoneNumber
	 * @return
	 */
	public void getRegionByPhoneNumber(String phoneNumber, IProgressMonitor m);

	/**
	 * 查找短信内容中，手机号码的归属地
	 * 
	 * @param smsContent
	 * @return
	 */
	public void getRegionBySmsContent(String smsContent, IProgressMonitor m);
	/**
	 * 根据拨打的号码 可能带前缀
	 * 
	 * @param dialogNumber
	 * @param m
	 */
	public void getRegionByDialogNumber(final String dialogNumber,
			IProgressMonitor m);
	/**
	 * 同步方式 根据拨打的号码 可能带前缀
	 * 
	 * @param dialogNumber
	 * @param m
	 */
	public Region getRegionByDialogNumber(String dialogNumber);
	/**
	 * 同步方式
	 * 
	 * @param smsContent
	 * @return
	 */
	public Region getRegionBySmsContent(String smsContent);
	/**
	 * 同步方式
	 * 
	 * @param msisdn
	 *            手机号码必须是11位
	 * @return
	 */
	public Region getRegionByMsisdn(String msisdn);
	/**
	 * 是否是移动手机号码
	 * 
	 * @param msisdn
	 */

	public boolean isChinaMobileMsisdn(String msisdn);
}
