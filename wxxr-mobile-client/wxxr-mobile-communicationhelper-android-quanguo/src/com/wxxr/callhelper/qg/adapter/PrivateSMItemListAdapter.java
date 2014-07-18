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
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.ui.SiMiPersonContentActivity;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * 私密信息详情adapter
 * 
 * @author yinzhen
 * 
 */
public class PrivateSMItemListAdapter extends BaseAdapter {

	private static final long SHOW_ITME = 5*60*1000;
	private IListDataProvider<TextMessageBean> mProvider;
	private List<TextMessageBean> selectedItems = new ArrayList<TextMessageBean>();
	// private final LayoutInflater inflater;
	// private final SimpleDateFormat fmt = new
	// SimpleDateFormat("MM月dd日  HH:mm");
	// private int padding;
	// LinearLayout.LayoutParams leftParams;
	// LinearLayout.LayoutParams rightParams;
	private boolean isChecked;
	private Context mContext;
	private SimpleDateFormat format_time;
	private SiMiPersonContentActivity mActivity;

	public PrivateSMItemListAdapter(Context context,
			SiMiPersonContentActivity activity, SimpleDateFormat format_time) {
		this.mContext = context;
		this.format_time = new SimpleDateFormat("yyyy-M-d-HH-mm");
		this.mActivity = activity;
		// this.provider = prov;
		// inflater = LayoutInflater.from(context);
		// this.padding = padding;
		//

	}

	@Override
	public int getCount() {
		if (mProvider == null) {
			return 0;
		} else
			return mProvider.getItemCounts();
	}

	@Override
	public TextMessageBean getItem(int position) {
		return mProvider.getItem(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 	getItem(position).getMo() != null ? 0 : 1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LeakDetailViewControl control = null;
		int type = getItemViewType(position);
			
			if (convertView == null) {
				if(type==0){				
					convertView = View.inflate(mContext,
							R.layout.sms_detail_item_left, null);
					control = new LeakDetailViewControl();
					control.root = convertView;
					control.tv_time = (TextView) convertView.findViewById(R.id.tv_detail_time_left);
					control.tv_content = (TextView) convertView
							.findViewById(R.id.tv_detail_content_left);
					control.cb_leak_detail = (CheckBox) convertView
							.findViewById(R.id.cb_leak_detail_left);			
					control.empty_line = convertView.findViewById(R.id.empty_line_left);
					
					convertView.setTag(control);
				}else if(type==1){
					convertView = View.inflate(mContext,
							R.layout.sms_detail_item_right, null);
					control = new LeakDetailViewControl();
					control.root = convertView;
					control.tv_time = (TextView) convertView.findViewById(R.id.tv_detail_time_right);
					control.tv_content = (TextView) convertView
							.findViewById(R.id.tv_detail_content_right);
					control.cb_leak_detail = (CheckBox) convertView
							.findViewById(R.id.cb_leak_detail_right);			
					control.empty_line = convertView.findViewById(R.id.empty_line_right);
					
					convertView.setTag(control);
				}				
			
			} else {
				control = (LeakDetailViewControl) convertView.getTag();
			}

		control.root.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
		if (isChecked) {
			control.cb_leak_detail.setVisibility(View.VISIBLE);
		} else {
			control.cb_leak_detail.setVisibility(View.GONE);
		}
		final TextMessageBean bodyBean = getItem(position);

		control.cb_leak_detail
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mActivity.pressAll();
							if (!selectedItems.contains(bodyBean)) {
								selectedItems.add(bodyBean);
							}
						} else {
							selectedItems.remove(bodyBean);
							if (selectedItems.isEmpty()) {
								mActivity.pressCancelAll();
							}
						}
					}
				});
		
		

		if (selectedItems.contains(bodyBean)) {
			control.cb_leak_detail.setChecked(true);
		} else {
			control.cb_leak_detail.setChecked(false);
		}

		control.tv_content.setText(bodyBean.getContent());
		
		if (position > 0) {
			if (getItem(position).getTimestamp()
					- getItem(position-1).getTimestamp() > SHOW_ITME) {
				control.empty_line.setVisibility(View.GONE);
				control.tv_time.setVisibility(View.VISIBLE);
				String realTime = Tools.getChatTime(format_time
						.format(new Date()), format_time.format(new Date(
								bodyBean.getTimestamp())));
				control.tv_time.setText(realTime);
			} else {
				control.empty_line.setVisibility(View.VISIBLE);
				control.tv_time.setVisibility(View.GONE);
			}
		} else {
			control.empty_line.setVisibility(View.GONE);
			control.tv_time.setVisibility(View.VISIBLE);
			Date now = new Date();
			Date chat = new Date(getItem(position).getTimestamp());
			String realTime = Tools.getChatTime(format_time.format(now),
					format_time.format(chat));
			control.tv_time.setText(realTime);
		}
		
		
		return convertView;

	}

	public void update() {
		notifyDataSetChanged();
	}

	public void setData(IListDataProvider<TextMessageBean> provider) {
		this.mProvider = provider;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public List<TextMessageBean> getSelected() {
		return selectedItems;
	}

	public void selectNone() {
		this.selectedItems.clear();
	}

	public void selectAll() {
		this.selectedItems.clear();
		for (int i = 0; i < mProvider.getItemCounts(); i++) {
			this.selectedItems.add(mProvider.getItem(i));
		}
	}
}
