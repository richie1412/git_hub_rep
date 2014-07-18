package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.wxxr.callhelper.qg.R;

import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.widget.DCMyViewGroupLouHuaCiShu;

public class LouhuaCishuAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private DCMyViewGroupLouHuaCiShu dcmyviewgroupitemtool;
	private ArrayList<ArrayList<BodyBean>> data;
	// private Hashtable<String, Integer> monthcountlist;
	private Hashtable<String, Integer> monthofonecountlist = new Hashtable<String, Integer>();
	private SimpleDateFormat format_yue = new SimpleDateFormat("yyyyMM");
	private SimpleDateFormat format_day = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat format_title = new SimpleDateFormat("yyyy年MM月");
	private SimpleDateFormat format_title_day = new SimpleDateFormat(
			"yyyy年MM月dd日");
	private boolean isweek = false;
	private Hashtable<String, String> address;

	public void setMonthofonecountlist(
			Hashtable<String, Integer> pmonthofonecountlist) {
		Enumeration<String> keys = pmonthofonecountlist.keys();
		synchronized (monthofonecountlist) {
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				monthofonecountlist.put(key, pmonthofonecountlist.get(key));
			}
		}
	}

	public LouhuaCishuAdapter(Context pcontext, DisplayMetrics metric) {
		layoutInflater = LayoutInflater.from(pcontext);
		dcmyviewgroupitemtool = new DCMyViewGroupLouHuaCiShu(pcontext, metric);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (data == null) {
			return 0;
		}
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.louhuacishu_item,
					null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.louhuacishu_title);
			holder.listview = (com.wxxr.callhelper.qg.widget.MyListView) convertView
					.findViewById(R.id.louhuacishu_itemlist);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ArrayList<BodyBean> list = data.get(position);

		holder.title.setVisibility(View.VISIBLE);
		if (isweek) {
			holder.title.setText(format_title_day.format(new Date(
					list.get(0).cdate)));
		} else {
			holder.title
					.setText(format_title.format(new Date(list.get(0).cdate)));
		}

		if (holder.listview.getAdapter() != null) {
			((DataInTimesAdapter) holder.listview.getAdapter()).setData(list);
			holder.listview.setAdapter(holder.listview.getAdapter());
		} else {
			holder.listview.setAdapter(new DataInTimesAdapter(list));
		}
		return convertView;
	}

	public void setWeekStyle(boolean ptrue) {
		isweek = ptrue;
	}

	public void setData(ArrayList<ArrayList<BodyBean>> array_mainlist_main) {

		data = array_mainlist_main;
	}

	class ViewHolder {
		TextView title;
		com.wxxr.callhelper.qg.widget.MyListView listview;
	}

	class DataInTimesAdapter extends BaseAdapter {

		private ArrayList<BodyBean> indata;

		public void setData(ArrayList<BodyBean> pdata) {
			indata = pdata;
		}

		DataInTimesAdapter(ArrayList<BodyBean> pdata) {

			indata = pdata;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return indata.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			InnerViewHolder holder = null;
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.dc_louhuacishu_item, null);
				holder = new InnerViewHolder();
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_head_name);
				holder.tv_count = (TextView) convertView
						.findViewById(R.id.tv_head_count);
				holder.tv_place = (TextView) convertView
						.findViewById(R.id.tv_tel_place);
				holder.divline = convertView
						.findViewById(R.id.iv_call_phone_divline);

				convertView.setTag(holder);
			} else {
				holder = (InnerViewHolder) convertView.getTag();
			}
			BodyBean body = indata.get(position);

			if (position == getCount() - 1) {
				holder.divline.setVisibility(View.GONE);
			} else {
				holder.divline.setVisibility(View.VISIBLE);
			}

			int num = 0;
			synchronized (monthofonecountlist) {
				if (isweek) {
					num = monthofonecountlist.get(format_day.format(new Date(
							body.cdate)) + "-" + body.address);
				} else {
					num = monthofonecountlist.get(format_yue.format(new Date(
							body.cdate)) + "-" + body.address);
				}
			}
			dcmyviewgroupitemtool.bindData(holder.tv_name, holder.tv_count,
					holder.tv_place, body, num, address.get(body.address));
			return convertView;
		}

	}

	class InnerViewHolder {
		TextView tv_name;
		TextView tv_count;
		TextView tv_place;
		View divline;
	}

	public void setCacheAddres(Hashtable<String, String> paddress) {
		address = paddress;

	}

}
