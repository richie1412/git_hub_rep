/*
 * @(#)MyStockAdapter.java	 2012-5-4
 *
 * Copyright 2004-2012 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.ui.gd.GDPrivateInfoFragment;
import com.wxxr.callhelper.qg.ui.gd.GDSimiContactsActvity;
import com.wxxr.callhelper.qg.utils.ContactIconUtil;
import com.wxxr.callhelper.qg.utils.GDItemPortrait;
import com.wxxr.callhelper.qg.widget.DCMyViewGroupSiMiSmsNew;
import com.wxxr.mobile.android.app.AppUtils;

public class SiMiHomeAdapter extends BaseAdapter {
	List<PrivateSMSummary> data;

	LayoutInflater layoutInflater;

	SimpleDateFormat format_yue = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat format_day = new SimpleDateFormat("MMdd");
	SimpleDateFormat format_title = new SimpleDateFormat("yyyy年MM月");
	DCMyViewGroupSiMiSmsNew tools;
	GDSimiContactsActvity mActivity;
	Context mContext;
//	private int count;

	public SiMiHomeAdapter(Context pcontext, GDSimiContactsActvity activity, DisplayMetrics metric) {
		mActivity = activity;
		mContext = pcontext;
		layoutInflater = LayoutInflater.from(pcontext);

		tools = new DCMyViewGroupSiMiSmsNew(pcontext);

	}

	Hashtable<String, Integer> monthcountlist = new Hashtable<String, Integer>();
	Hashtable<String, Integer> monthofonecountlist = new Hashtable<String, Integer>();
	/**
	 * 0,不显示，隐藏， 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据sel来判断被勾选的内容，
	 */
	int sel_state = 0;

	Hashtable<String, String> selbobysid = new Hashtable<String, String>();
	Hashtable<String, String> canlebobysid = new Hashtable<String, String>();

	public void setShowSel() {
		sel_state = 3;
		notifyDataSetChanged();
	}

	public void setHideSel(boolean notify) {
		canlebobysid.clear();
		selbobysid.clear();
		sel_state = 0;
		if (notify) {
			notifyDataSetChanged();
		}
	}

	public void setSelAll() {
		canlebobysid.clear();
		selbobysid.clear();
		sel_state = 1;
		notifyDataSetChanged();
	}

	public void setCancelAll() {
		selbobysid.clear();
		canlebobysid.clear();
		sel_state = 2;
		notifyDataSetChanged();
	}

	public void setMonthCountHashtable(Hashtable<String, Integer> pcont) {
		Enumeration<String> keys = pcont.keys();
		synchronized (monthcountlist) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				monthcountlist.put(key, pcont.get(key));
			}
		}
	}

	public void setMonthCountOfOnePeopleHashtable(
			Hashtable<String, Integer> pcont) {
		Enumeration<String> keys = pcont.keys();
		synchronized (monthofonecountlist) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				monthofonecountlist.put(key, pcont.get(key));
			}
		}

	}

	int toltal = 0;

	@Override
	public int getCount() {
		return toltal;
	}

	@Override
	public Object getItem(int position) {

		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			View view = layoutInflater.inflate(R.layout.dcmain_item_not_arrive,
					null);

			// holder.iv_arrow = (ImageView)
			// view.findViewById(R.id.dc_group_arrow);
			holder.iv_checkbox = (CheckBox) view
					.findViewById(R.id.dc_group_check);
			holder.tv_name = (TextView) view.findViewById(R.id.tv_head_name);
			holder.tv_count = (TextView) view.findViewById(R.id.gd_tv_count);
			holder.tv_date = (TextView) view.findViewById(R.id.tv_head_date);
			holder.tv_content = (TextView) view
					.findViewById(R.id.tv_head_content);
//			holder.rl_check = (RelativeLayout) view
//					.findViewById(R.id.check_relative);
			holder.gd_iv_portrait = (ImageView) view
					.findViewById(R.id.gd_iv_private_info_portrait);
			holder.gd_rl_portrait = (RelativeLayout) view
					.findViewById(R.id.gd_rl_private_info_portrait);
			holder.gd_tv_portrait = (TextView) view
					.findViewById(R.id.gd_tv_private_info_portrait_text);
			holder.gd_rl_pi_count = (RelativeLayout) view.findViewById(R.id.gd_rl_private_info_count);

			// holder.simi_home_item_divline =
			// view.findViewById(R.id.simi_home_item_divline);
			// holder.simi_home_item_botline =
			// view.findViewById(R.id.simi_home_item_botline);

			holder.iv_checkbox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (buttonView.getTag() != null) {
								if (isChecked) {
									mActivity.pressAll();
									if (sel_state == 2) {
										sel_state = 3;
									}
									selbobysid.put(
											(String) buttonView.getTag(), "");
									canlebobysid.remove((String) buttonView
											.getTag());
								} else {
									selbobysid.remove((String) buttonView
											.getTag());
									canlebobysid.put(
											(String) buttonView.getTag(), "");
									if(selbobysid.isEmpty()){
										mActivity.pressCancelAll();
									}
								}
							}
						}
					});

			view.setTag(holder);
			convertView = view;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PrivateSMSummary body = data.get(position);
		
		if (sel_state == 0) {
			holder.iv_checkbox.setVisibility(View.INVISIBLE);
		} else {
			holder.iv_checkbox.setVisibility(View.VISIBLE);
		}
		
		//私密信息未读数
//		getPrivateInfoUnReader(body.getPhoneNumber());
		int count = AppUtils.getService(IPrivateSMService.class).getUnreadSizeOfPhoneNumber(body.getPhoneNumber());
		
		tools.resetContent(holder.tv_name, holder.tv_count, holder.tv_date,
				holder.tv_content, holder.iv_checkbox, body,
				getSelstate(body.getPhoneNumber()), holder.gd_iv_portrait,
				holder.gd_rl_portrait, holder.gd_tv_portrait, holder.gd_rl_pi_count, count);
		
		// if(position==(getCount()-1)){
		// holder.simi_home_item_divline.setVisibility(View.GONE);
		// holder.simi_home_item_botline.setVisibility(View.VISIBLE);
		// }else{
		// holder.simi_home_item_divline.setVisibility(View.VISIBLE);
		// holder.simi_home_item_botline.setVisibility(View.GONE);
		// }

		// dcmyviewgroupitemtool.bingData(holder.iv_checkbox, holder.rl_check,
		// holder.tv_name, holder.tv_count, holder.tv_date, holder.tv_content,
		// holder.iv_weidu, body, body.getMessageCount(),
		// getSelstate(body.getPhoneNumber()));
		// dcmyviewgroupitemtool.bingData(holder.iv_checkbox, holder.rl_check,
		// holder.tv_name, holder.tv_count, holder.tv_date,
		// holder.tv_content, holder.iv_weidu, body, personsmscount,
		// getSelstate(body.bodyID));
		return convertView;
	}

	private boolean getSelstate(String phonenum) {
		// 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根
		if (sel_state == 0) {
			return false;
		} else if (sel_state == 1) {
			if (canlebobysid.containsKey(phonenum)) {
				return false;
			}
			return true;
		} else if (sel_state == 2) {
			return false;
		} else if (sel_state == 3) {
			if (selbobysid.containsKey(phonenum)) {
				return true;
			}
		}
		return false;
	}

	public void setData(List<PrivateSMSummary> pdata) {
		toltal = 0;
		data = pdata;
		if (data != null) {
			toltal = data.size();
		}
	}

	public void adapterClearPortrait() {
		tools.clearPortraitMaps();
	}

	public void updateData() {
		notifyDataSetChanged();
	}

	class ViewHolder {
		// public View simi_home_item_botline;
		// public View simi_home_item_divline;
		// public CheckBox dc_group_check;
		// public ImageView dc_group_arrow;
		// public ImageView iv_arrow;
		CheckBox iv_checkbox;
//		RelativeLayout rl_check;
		TextView tv_name;
		TextView tv_count;
		TextView tv_date;
		TextView tv_content;
		ImageView gd_iv_portrait;
		RelativeLayout gd_rl_portrait;
		TextView gd_tv_portrait;
		RelativeLayout gd_rl_pi_count;
	}

	public List<PrivateSMSummary> getSelteItems() {
		ArrayList<PrivateSMSummary> sleitem = new ArrayList<PrivateSMSummary>();
		// 0 不显示，隐藏 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据选中的来判断
		if (sel_state == 0) {

		} else if (sel_state == 1) {
			int size = data.size();
			for (int i = 0; i < size; i++) {
				PrivateSMSummary item = data.get(i);
				if (!canlebobysid.containsKey(item.getPhoneNumber())) {
					sleitem.add(item);
				}
			}
		} else if (sel_state == 2) {

		} else if (sel_state == 3) {

			int size = data.size();
			for (int i = 0; i < size; i++) {
				PrivateSMSummary item = data.get(i);
				if (selbobysid.containsKey(item.getPhoneNumber())) {
					sleitem.add(item);
				}
			}

		}

		return sleitem;
	}
	
	/**
	 * 得到未读数
	 */
//	private void getPrivateInfoUnReader(final String telphone){
//		CMProgressMonitor monitor = new CMProgressMonitor(mContext) {
//			
//			@Override
//			protected void handleFailed(Throwable cause) {
//				
//			}
//			
//			@Override
//			protected void handleDone(Object result) {
//				count = (Integer) result;
//			}
//		};
//		
//		monitor.executeOnMonitor(new Callable<Object>() {
//			
//			@Override
//			public Object call() throws Exception {
//				return AppUtils.getService(IPrivateSMService.class).getUnreadSizeOfPhoneNumber(telphone);
//			}
//		});
//	}

}
