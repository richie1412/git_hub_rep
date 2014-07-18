package com.wxxr.callhelper.qg.service;

import java.util.HashMap;
import java.util.Map;

import com.wxxr.callhelper.qg.bean.Region;
/**
 * 
 * @author cuizaixi
 * 
 */
public class CommonNumberQuery implements ICommonNumberService {

	private Map<String, String> map;
	private String region;
	public CommonNumberQuery() {
		initData();
	}

	@Override
	public boolean isHandle(String num) {
		//号码长度大于5则不用此方法查询。
		if (num.length() > 5) {
			return false;
		}
		String region = map.get(num);
		if (region != null) {
			this.region = region;
			return true;
		}
		return false;
	}
	// 未完待续。。。
	@Override
	public void initData() {
		map = new HashMap<String, String>();
		map.put("10086", "中国移动客服");
		map.put("10010", "中国联通客服");
		map.put("10000", "中国电信客服");
		map.put("17951", "中国移动IP号码");
		map.put("17900", "中国联通IP号码");
		map.put("95555", "招商银行");
		map.put("95566", "中国银行");
		map.put("95533", "建设银行");
		map.put("95588", "工商银行");
		map.put("95558", "中信银行");
		map.put("95599", "农业银行");
		map.put("95568", "民生银行");
		map.put("95595", "光大银行");
		map.put("95559", "交通银行");
		map.put("95508", "广发银行");
		map.put("95528", "浦发银行");
		map.put("95501", "深发银行");
		map.put("95577", "华夏银行");
		map.put("95533", "建设银行");
		map.put("95533", "兴业银行");
		map.put("110", "匪警");
		map.put("119", "火警");
		map.put("120", "急救中心");
		map.put("122", "交通事故");
		map.put("12395", "水上求救专用");
		map.put("12121", "天气预报");
		map.put("12117", "报时服务");
		map.put("12119", "森林火警");
		map.put("95598", "供电局");
		map.put("12318", "文化市场综合执法");
		map.put("12315", "消费者申诉举报");
		map.put("12358", "价格监督举报");
		map.put("12365", "质量监督电话");
		map.put("12310", "机构编制违规举报热线");
		map.put("12369", "环保局监督电话");
		map.put("12333", "民工维权热线电话");
		map.put("12366", "税务局通用电话");
		map.put("95518", "中国人保");
		map.put("95500", "太平洋保险");
		map.put("95522", "泰康人寿");
		map.put("95567", "新华人寿");
	}

	@Override
	public String getRegion() {
		return this.region;
	}

	@Override
	public String getRegionWithoutBrand() {
		return this.region;
	}
}
