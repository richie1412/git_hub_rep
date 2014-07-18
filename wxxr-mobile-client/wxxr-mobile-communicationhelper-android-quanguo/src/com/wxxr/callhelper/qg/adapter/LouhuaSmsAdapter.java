package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
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
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.ui.LouHuaMainActivity;
import com.wxxr.callhelper.qg.utils.ContactIconUtil;
import com.wxxr.callhelper.qg.utils.GDItemPortrait;

public class LouhuaSmsAdapter extends BaseAdapter {
	ArrayList<ArrayList<BodyBean>> data;

	LayoutInflater layoutInflater;

	SimpleDateFormat format_yue = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat format_day = new SimpleDateFormat("MMdd");
	SimpleDateFormat format_title = new SimpleDateFormat("yyyy年MM月");

	private LouHuaMainActivity mActivity;

	public LouhuaSmsAdapter(Context pcontext, DisplayMetrics metric,
			LouHuaMainActivity activity) {
		layoutInflater = LayoutInflater.from(pcontext);
		dcmyviewgroupitemtool = new DCMyViewGroupItem(pcontext, metric);
		this.mActivity = activity;
	}

	Hashtable<String, Integer> monthcountlist = new Hashtable<String, Integer>();
	Hashtable<String, Integer> monthofonecountlist = new Hashtable<String, Integer>();
	/**
	 * 0,不显示，隐藏， 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据sel来判断被勾选的内容，
	 */
	int sel_state;

	Hashtable<Integer, String> selbobysid = new Hashtable<Integer, String>();
	Hashtable<Integer, String> canlebobysid = new Hashtable<Integer, String>();

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

	DCMyViewGroupItem dcmyviewgroupitemtool;
	int toltal = 0;

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return toltal;
	}

	@Override
	public Object getItem(int position) {
		int count = data.size();
		int realsize = 0;
		BodyBean body = null;
		for (int i = 0; i < count && body == null; i++) {
			ArrayList<BodyBean> bean = data.get(i);
			if (realsize + bean.size() <= position) {
				realsize += bean.size();
			} else {
				int one_pos = position - realsize;
				body = bean.get(one_pos);
			}
		}
		return body;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			View view = layoutInflater.inflate(R.layout.louhuasms_listitem,
					null);
			// TODO Auto-generated method stub

			holder.cb = (CheckBox) view.findViewById(R.id.dc_group_check);
			// holder.rl_check = (RelativeLayout) view
			// .findViewById(R.id.check_relative);
			holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (buttonView.getTag() != null) {
						if (isChecked) {
							if (sel_state == 2) {
								sel_state = 3;
							}
							selbobysid.put((Integer) buttonView.getTag(), "");
							canlebobysid.remove((Integer) buttonView.getTag());
							mActivity.pressAll();
						} else {
							selbobysid.remove((Integer) buttonView.getTag());
							canlebobysid.put((Integer) buttonView.getTag(), "");
							if (selbobysid.isEmpty()) {
								mActivity.pressCancelAll();
							}
						}

					}
				}
			});

			holder.tv_name = (TextView) view.findViewById(R.id.tv_head_name);
			holder.tv_count = (TextView) view.findViewById(R.id.tv_head_count);
			holder.tv_date = (TextView) view.findViewById(R.id.tv_head_date);
			holder.tv_content = (TextView) view
					.findViewById(R.id.tv_head_content);
			// holder.titlediv = view.findViewById(R.id.louhua_sms_title);
			// holder.titletexview = (TextView) view
			// .findViewById(R.id.sms_timetext);
			holder.iv_weidu = (ImageView) view.findViewById(R.id.iv_head_weidu);
			// 头像
			holder.gd_iv_portrait = (ImageView) view
					.findViewById(R.id.gd_iv_leak_session_portrait);
			holder.gd_rl_portrait = (RelativeLayout) view
					.findViewById(R.id.gd_rl_leak_session_portrait);
			holder.gd_tv_portrait = (TextView) view
					.findViewById(R.id.gd_tv_leak_session_portrait_text);
			// holder.louhua_sms_item_divline =
			// view.findViewById(R.id.louhua_sms_item_divline);
			// holder.louhua_sms_item_botline=view.findViewById(R.id.louhua_list_item_botline);

			// holder.titlele_divline=view.findViewById(R.id.dc_sms_time_divline);

			view.setTag(holder);
			convertView = view;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int realsize = 0;
		int count = data.size();

		BodyBean body = null;
		boolean showtitle = false;
		boolean showdivline = true;
		for (int i = 0; i < count && body == null; i++) {
			ArrayList<BodyBean> bean = data.get(i);
			if (realsize + bean.size() <= position) {
				realsize += bean.size();
			} else {

				int one_pos = position - realsize;
				body = bean.get(one_pos);
				if (one_pos == 0) {
					showtitle = true;
				}
				if (one_pos == bean.size() - 1) {
					showdivline = false;
				}

				realsize += one_pos;
			}
		}

		if (showtitle) {
			int num = 0;
			synchronized (monthcountlist) {
				num = monthcountlist.get(format_yue
						.format(new Date(body.cdate)));
			}

			// holder.titletexview.setText(format_title
			// .format(new Date(body.cdate)) + " " + num + "条");
			// holder.titlediv.setVisibility(View.VISIBLE);
			// holder.titlele_divline.setVisibility(View.VISIBLE);
		} else {
			// holder.titlele_divline.setVisibility(View.GONE);
			// holder.titlediv.setVisibility(View.GONE);
		}
		int personsmscount = 0;
		synchronized (monthofonecountlist) {
			// String key=format_yue.format(new Date(body.cdate)) + "-"+
			// body.address;
			String key = body.address;
			if (monthofonecountlist.containsKey(key)) {
				personsmscount = monthofonecountlist.get(key);
			}
		}

		// if(showdivline){
		// holder.louhua_sms_item_divline.setVisibility(View.VISIBLE);
		// }else{
		// holder.louhua_sms_item_divline.setVisibility(View.INVISIBLE);
		// }

		dcmyviewgroupitemtool.bingData(holder.cb,/* holder.rl_check */
				holder.tv_name, holder.tv_count, holder.tv_date,
				holder.tv_content, holder.iv_weidu, body, personsmscount,
				getSelstate(body.bodyID), holder.gd_iv_portrait,
				holder.gd_rl_portrait, holder.gd_tv_portrait);
		if (position == getCount() - 1) {
			// holder.louhua_sms_item_botline.setVisibility(View.VISIBLE);
			// holder.louhua_sms_item_divline.setVisibility(View.GONE);
		} else {
			// holder.louhua_sms_item_botline.setVisibility(View.GONE);
			// holder.louhua_sms_item_divline.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	private boolean getSelstate(Integer bodyID) {
		// 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根
		if (sel_state == 0) {
			return false;
		} else if (sel_state == 1) {
			if (canlebobysid.containsKey(bodyID)) {
				return false;
			}
			return true;
		} else if (sel_state == 2) {
			return false;
		} else if (sel_state == 3) {
			if (selbobysid.containsKey(bodyID)) {
				return true;
			}
		}
		return false;
	}

	public void setData(ArrayList<ArrayList<BodyBean>> pdata) {
		toltal = 0;
		data = pdata;
		if (data != null) {
			int tempcount = data.size();
			for (int i = 0; i < tempcount; i++) {
				toltal += data.get(i).size();
			}

		}
	}

	public void clearData() {

	}

	class ViewHolder {
		// public View titlele_divline;
		// public View louhua_sms_item_botline;
		// public View louhua_sms_item_divline;
		CheckBox cb;
		// RelativeLayout rl_check;
		TextView tv_name;
		TextView tv_count;
		TextView tv_date;
		TextView tv_content;
		ImageView iv_weidu;
		ImageView gd_iv_portrait;
		RelativeLayout gd_rl_portrait;
		TextView gd_tv_portrait;
		// View titlediv;
		// TextView titletexview;

	}

	class DCMyViewGroupItem {
		private Context mContext;
		private int curScreen = 0;
		private Scroller mScroller = null;
		private DisplayMetrics metric;
		LayoutInflater inflater;
		int item_length;
		int item_length2;
		// ImageView twoLL;
		int total_length;
		int dis;
		int mOffsetX;
		int mOffsetY;
		int mOffsetX_move;
		int mdistance;
		long touchDown;
		long touchUp;

		// private String sms_name;
		private String[] CONTACT_PROJECTION = new String[] { PhoneLookup.DISPLAY_NAME };
		private int DISPLAY_NAME_COLUMN_INDEX = 0;
		// ImageView iv_arrow;
		// private CheckBox iv_checkbox;
		// ProgressDialog mProgressDialog;
		public LouHuaDao ldao;
		private RelativeLayout rl_check;
		SimpleDateFormat format_time;
		SimpleDateFormat format_compare;
		SimpleDateFormat format_today;

		public void setMetric(DisplayMetrics metric) {
			this.metric = metric;
		}

		public DCMyViewGroupItem(Context context, DisplayMetrics metric) {
			mContext = context;
			ldao = LouHuaDao.getInstance(context);
			this.metric = metric;
			inflater = LayoutInflater.from(context);

			item_length = (int) (metric.density * 80);
			item_length2 = (int) (metric.density * 65);
			total_length = 3 * metric.widthPixels;

			// yyyy.MM.dd HH:mm:ss
			format_time = new SimpleDateFormat("M月dd日");
			format_compare = new SimpleDateFormat("yyyyMMdd");
			format_today = new SimpleDateFormat("HH:mm");
			// init();

			gd_portrait = GDItemPortrait.getInstance(mContext);
		}

		boolean ttflag_sms = true;
		boolean ttflag_call = true;
		private GDItemPortrait gd_portrait;

		

		private void bingData(CheckBox iv_checkbox, /* RelativeLayout rl_check, */
				TextView tv_name, TextView tv_count, TextView tv_date,
				TextView tv_content, ImageView iv_weidu, BodyBean body,
				int lcount, boolean ischeck, ImageView gd_iv_portrait,
				RelativeLayout gd_rl_portrait, TextView gd_tv_portrait) {

			iv_checkbox.setTag(body.bodyID);
			if (sel_state != 0) {
				iv_checkbox.setVisibility(View.VISIBLE);
				// rl_check.setVisibility(View.VISIBLE);
				if (ischeck) {
					iv_checkbox.setChecked(true);
				} else {
					iv_checkbox.setChecked(false);
				}
			} else {
				iv_checkbox.setVisibility(View.INVISIBLE);
				// rl_check.setVisibility(View.GONE);
			}

			// if (metric.widthPixels > 600) {
			// // Tools.px2sp
			// tv_name.setTextSize(18);
			// tv_count.setTextSize(13);
			// tv_date.setTextSize(12);
			// tv_content.setTextSize(13);
			//
			// } else if (metric.widthPixels > 400) {
			// tv_name.setTextSize(17);
			// tv_count.setTextSize(12);
			// tv_date.setTextSize(11);
			// tv_content.setTextSize(12);
			// }
			String sms_name = null;
			// iv_weidu = (ImageView) view.findViewById(R.id.iv_head_weidu);
			
			String num=body.realnum;
			if(num==null){
				num=body.address;
			}
			
			
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(num));

			Cursor contactCurosr = mContext.getContentResolver().query(uri,
					CONTACT_PROJECTION, null, null, null);
			if (contactCurosr != null) {
				if (contactCurosr.moveToNext()) {
					sms_name = contactCurosr
							.getString(DISPLAY_NAME_COLUMN_INDEX);
				}
				contactCurosr.close();
			}

			// 头像
			gd_portrait.setItemPortrait(sms_name, num, gd_rl_portrait,
					gd_tv_portrait, gd_iv_portrait);

			// int lcount = ldao.findLouHuaCount(body.address, body.amonth);
//			int weiducount = ldao.queryAllLouhua_accordingtoNumberAndMonth(
//					num, body.amonth, 1, body.cdate);
			Bitmap bt_icon = ContactIconUtil.getInstance().NumberForIcon(
					body.address, mContext);
			if (bt_icon != null) {
				// iv_icon.setImageBitmap(bt_icon);
			}

			if (sms_name != null) {
				tv_name.setText(sms_name);
			} else {
				tv_name.setText(num);

				// if (weiducount > 0) {
				// // 显示未读标记
				// iv_weidu.setVisibility(View.VISIBLE);
				// } else {
				// iv_weidu.setVisibility(View.INVISIBLE);
				// }
				if (num.length() > 11) {
					String address = num.substring(0, 9) + "..";
					tv_name.setText(address);
				}

			}

			if (lcount > 0) {
				tv_count.setText("(" + lcount + ")");
				tv_count.setVisibility(View.VISIBLE);
			} else {
				tv_count.setVisibility(View.INVISIBLE);
			}

			tv_content.setText(body.content);
			String time1 = format_compare.format(new Date());
			String time2 = format_compare.format(body.cdate);
			if (!TextUtils.isEmpty(time1) && !TextUtils.isEmpty(time2)
					&& time1.equals(time2)) {
				tv_date.setText(format_today.format(body.cdate));// 时间
			} else {
				tv_date.setText(format_time.format(body.cdate));// 日期

			}

		}

		public void clearPortraitMaps() {
			gd_portrait.clearPortraitMaps();
		}
	}

	public void adapterClearPortraitMaps() {
		dcmyviewgroupitemtool.clearPortraitMaps();
	}

	public ArrayList<BodyBean> getSelteItems() {
		ArrayList<BodyBean> sleitem = new ArrayList<BodyBean>();
		// 0 不显示，隐藏 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据选中的来判断
		if (sel_state == 0) {

		} else if (sel_state == 1) {
			int size = data.size();
			for (int i = 0; i < size; i++) {
				ArrayList<BodyBean> list = data.get(i);
				int count = list.size();
				for (int j = 0; j < count; j++) {
					if (!canlebobysid.containsKey(list.get(j).bodyID)) {
						sleitem.add(list.get(j));
					}
				}

			}
		} else if (sel_state == 2) {

		} else if (sel_state == 3) {
			int size = data.size();
			for (int i = 0; i < size; i++) {
				ArrayList<BodyBean> list = data.get(i);
				int count = list.size();
				for (int j = 0; j < count; j++) {
					if (selbobysid.containsKey(list.get(j).bodyID)) {
						sleitem.add(list.get(j));
					}
				}
			}
		}

		return sleitem;
	}

}
