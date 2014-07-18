/*
 * Copyright 2010-2013 WXXR Network Technology
 * Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.constant;

import java.util.HashMap;
import java.util.Map;

import com.wxxr.callhelper.qg.R;
import com.wxxr.mobile.core.util.StringUtils;

/**
 * @class desc ChannelStaticData.
 * @author wangxuyang
 * @version $Revision: 1.2 $
 * @created time 2013-9-23 上午10:15:16
 */
public class ChannelStaticData {
	private static ChannelStaticData instance;

	private ChannelStaticData() {
		init();
	}

	public static String[] channels = new String[] { "KJSM", "RWLS", "XHPD",
			"JKPD", "MSTD", "CJPD", "JYPD", "TYPD", "FYPD", "GATY", "SMPD",
			"MRPD", "FCPD", "GZSM", "JRRD" };
	public static String[] channel_descs = new String[] { "科技数码", "人文历史",
			"休闲娱乐", "生活健康", "旅游美食", "财经快报", "教育职场", "体育前沿", "每日育儿", "关爱糖友",
			"军事汽车", "伊人风尚", "房产家居", "关注睡眠", "WAP资讯" };
	public static HashMap<String, String> channel_key_name = new HashMap<String, String>();
	static {

		int size = channels.length;

		for (int i = 0; i < size; i++) {
			channel_key_name.put(channel_descs[i], channels[i]);
		}

	}

	private static int[] unselectedResourceIds = new int[] {
			R.drawable.channel_kjsm_unselected,
			R.drawable.channel_rwls_unselected,
			R.drawable.channel_xhpd_unselected,
			R.drawable.channel_jkpd_unselected,
			R.drawable.channel_mstd_unselected,
			R.drawable.channel_cjpd_unselected,
			R.drawable.channel_jypd_unselected,
			R.drawable.channel_typd_unselected,
			R.drawable.channel_fypd_unselected,
			R.drawable.channel_gaty_unselected,
			R.drawable.channel_smpd_unselected,
			R.drawable.channel_mrpd_unselected,
			R.drawable.channel_fcpd_unselected,
			R.drawable.channel_gzsm_unselected,
			R.drawable.channel_jrrd_unselected };

	private static int[] selectedResourceIds = new int[] {
			R.drawable.channel_kjsm_selected, R.drawable.channel_rwls_selected,
			R.drawable.channel_xhpd_selected, R.drawable.channel_jkpd_selected,
			R.drawable.channel_mstd_selected, R.drawable.channel_cjpd_selected,
			R.drawable.channel_jypd_selected, R.drawable.channel_typd_selected,
			R.drawable.channel_fypd_selected, R.drawable.channel_gaty_selected,
			R.drawable.channel_smpd_selected, R.drawable.channel_mrpd_selected,
			R.drawable.channel_fcpd_selected, R.drawable.channel_gzsm_selected,
			R.drawable.channel_jrrd_selected };

	public static class ChannelInfo {
		private String code;
		private String desc;
		private int selected_img_resId;
		private int unselected_img_resId;

		public String getCode() {
			return code;
		}

		public String getDesc() {
			return desc;
		}

		public int getSelected_img_resId() {
			return selected_img_resId;
		}

		public int getUnselected_img_resId() {
			return unselected_img_resId;
		}
	}

	private static Map<String/***/
	, ChannelInfo> channel_data = new HashMap<String, ChannelStaticData.ChannelInfo>();

	public static ChannelStaticData getInstance() {
		if (instance == null) {
			instance = new ChannelStaticData();
		}
		return instance;
	}

	public ChannelInfo getStaticChannelInfo(String code) {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		return channel_data.get(code);
	}

	public String[] getAllStaticChannelKeys() {
//		return channel_data.keySet().toArray(new String[channel_data.size()]);
		return channels;
	}

	private static void init() {
		for (int i = 0; i < channels.length; i++) {
			String code = channels[i];
			ChannelInfo ch = new ChannelInfo();
			ch.code = code;
			ch.desc = channel_descs[i];
			ch.selected_img_resId = selectedResourceIds[i];
			ch.unselected_img_resId = unselectedResourceIds[i];
			channel_data.put(code, ch);
		}

	}
}
