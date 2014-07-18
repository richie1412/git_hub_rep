package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;

import android.content.Context;

import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.bean.UrlBean;

public interface IMoblieBusiness {
	/**
	 * 获取业务查询的数据
	 * @param contxt
	 * @return
	 */
	ArrayList<MoblieBusinessBean> getBusinessCheckFromLocal(Context contxt);
	
	/**
	 * 获取业务办理的数据
	 * @param contxt
	 * @return
	 */
	ArrayList<MoblieBusinessBean> getBusinessProcessFromLocal(Context contxt);
	
	/**
	 * 获取 来电提醒 的指令
	 * 
	 * @param key
	 *            dsyh:cu
	 * @return
	 */
	MoblieBusinessBean getLaiDianTiXing();
	/**
	 * 获取短信回执 的指令
	 * 
	 * @param key
	 * @return
	 */
	MoblieBusinessBean getDuanXinHuiZhi();	
}
