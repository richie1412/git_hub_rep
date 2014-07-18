package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;

import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
/**
 * 从 本地取数据的接口 ，不去联网下载呢
 * 
 * @author yangrunfei
 *
 */
public interface IGDDownWebService {
 
 ArrayList<HtmlMessageBean> getZhushouBobaoDefault( ); 
 
 ArrayList<HtmlMessageBean> getCheapsOfOneCity(String key);
}
