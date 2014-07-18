package com.wxxr.callhelper.qg.ui.gd;

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import android.provider.ContactsContract.Data;
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
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.utils.ContactIconUtil;
import com.wxxr.callhelper.qg.utils.Tools;

public class GDLouhuaSmsAdapter extends BaseAdapter {
	ArrayList<ArrayList<BodyBean>> data;

	Hashtable<String, SoftReference<Bitmap>>  cachehead=new Hashtable<String, SoftReference<Bitmap>>();
	
	LayoutInflater layoutInflater;

	SimpleDateFormat format_yue = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat format_day = new SimpleDateFormat("MMdd");
	SimpleDateFormat format_title = new SimpleDateFormat("yyyy年MM月");

	public GDLouhuaSmsAdapter(Context pcontext, DisplayMetrics metric) {
		layoutInflater = LayoutInflater.from(pcontext);
		dcmyviewgroupitemtool = new DCMyViewGroupItem(pcontext, metric);
	}

	Hashtable<String, Integer> monthcountlist=new Hashtable<String, Integer>();
	Hashtable<String, Integer> monthofonecountlist=new Hashtable<String, Integer>();
	/**
	 * 0,不显示，隐藏， 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据sel来判断被勾选的内容，
	 */
	int sel_state = 0;

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
		if(notify){
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
			View view = layoutInflater.inflate(R.layout.gd_louhuasms_listitem, null);
			// TODO Auto-generated method stub

			holder.iv_checkbox = (CheckBox) view
					.findViewById(R.id.dc_group_check);
			holder.rl_check = (RelativeLayout) view
					.findViewById(R.id.check_relative);
			holder.iv_checkbox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if(buttonView.getTag()!=null){
							if (isChecked) {
								if(sel_state==2){
									sel_state=3;
								}
								selbobysid.put((Integer) buttonView.getTag(),
										"");
								canlebobysid.remove((Integer) buttonView
										.getTag());
							} else {
								selbobysid.remove((Integer) buttonView.getTag());
								canlebobysid.put((Integer) buttonView.getTag(),
										"");
							}
							}
						}
					});

			holder.tv_name = (TextView) view.findViewById(R.id.tv_head_name);
			holder.tv_count = (TextView) view.findViewById(R.id.tv_head_count);
			holder.tv_date = (TextView) view.findViewById(R.id.tv_head_date);
			holder.tv_content = (TextView) view
					.findViewById(R.id.tv_head_content);
			holder.titlediv = view.findViewById(R.id.louhua_sms_title);
			holder.titletexview = (TextView) view
					.findViewById(R.id.sms_timetext);
			holder.iv_weidu = (ImageView) view.findViewById(R.id.iv_head_weidu);
			holder.louhua_sms_item_divline = view.findViewById(R.id.louhua_sms_item_divline);
			holder.louhua_sms_item_botline=view.findViewById(R.id.louhua_list_item_botline);
			
			holder.titlele_divline=view.findViewById(R.id.dc_sms_time_divline);
			
			
			
			view.setTag(holder);
			convertView = view;
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		int realsize = 0;
		int count = data.size();

		BodyBean body = null;
		boolean showtitle = false;
		boolean showdivline=true;
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
				if(one_pos==bean.size()-1){
					showdivline=false;
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

			holder.titletexview.setText(format_title
					.format(new Date(body.cdate)) + " " + num + "条");
			holder.titlediv.setVisibility(View.VISIBLE);
			holder.titlele_divline.setVisibility(View.VISIBLE);
		} else {
			holder.titlele_divline.setVisibility(View.GONE);
			holder.titlediv.setVisibility(View.GONE);
		}
		int personsmscount = 0;
		synchronized (monthofonecountlist) {
			String key=format_yue.format(new Date(body.cdate)) + "-"+ body.address;
			if(monthofonecountlist.containsKey(key)){
			personsmscount = monthofonecountlist.get(key);
			}
		}

		if(showdivline){
			holder.louhua_sms_item_divline.setVisibility(View.VISIBLE);
		}else{
			holder.louhua_sms_item_divline.setVisibility(View.INVISIBLE);
		}
		
		
		dcmyviewgroupitemtool.bingData(holder.iv_checkbox, holder.rl_check,
				holder.tv_name, holder.tv_count, holder.tv_date,
				holder.tv_content, holder.iv_weidu, body, personsmscount,
				getSelstate(body.bodyID));
		if(position==getCount()-1){
			holder.louhua_sms_item_botline.setVisibility(View.VISIBLE);
		//	holder.louhua_sms_item_divline.setVisibility(View.GONE);
		}else{
			holder.louhua_sms_item_botline.setVisibility(View.GONE);
		//	holder.louhua_sms_item_divline.setVisibility(View.VISIBLE);
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
		public View titlele_divline;
		public View louhua_sms_item_botline;
		public View louhua_sms_item_divline;
		CheckBox iv_checkbox;
		RelativeLayout rl_check;
		TextView tv_name;
		TextView tv_count;
		TextView tv_date;
		TextView tv_content;
		ImageView iv_weidu;
		View titlediv;
		TextView titletexview;

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
		private String[] CONTACT_PROJECTION = new String[] { PhoneLookup.DISPLAY_NAME,PhoneLookup.PHOTO_ID };
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
			format_time = new SimpleDateFormat("MM月dd日");
			format_compare = new SimpleDateFormat("yyyyMMdd");
			format_today = new SimpleDateFormat("HH:mm");
			// init();
		}

		boolean ttflag_sms = true;
		boolean ttflag_call = true;


		private void bingData(CheckBox iv_checkbox, RelativeLayout rl_check,
				TextView tv_name, TextView tv_count, TextView tv_date,
				TextView tv_content, ImageView iv_weidu, BodyBean body,
				int lcount, boolean ischeck) {
		
			iv_checkbox.setTag(body.bodyID);
			if (sel_state != 0) {
				rl_check.setVisibility(View.VISIBLE);
				if (ischeck) {
					iv_checkbox.setChecked(true);
				} else {
					iv_checkbox.setChecked(false);
				}
			} else {
				rl_check.setVisibility(View.GONE);
			}
	
			if (metric.widthPixels > 600) {
				// Tools.px2sp
				tv_name.setTextSize(18);
				tv_count.setTextSize(13);
				tv_date.setTextSize(12);
				tv_content.setTextSize(13);

			} else if (metric.widthPixels > 400) {
				tv_name.setTextSize(17);
				tv_count.setTextSize(12);
				tv_date.setTextSize(11);
				tv_content.setTextSize(12);
			}
			String sms_name = null;
			Bitmap head=null;
			// iv_weidu = (ImageView) view.findViewById(R.id.iv_head_weidu);
			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(body.address));
			String sms_imgid = null;
			Cursor contactCurosr = mContext.getContentResolver().query(uri,
					CONTACT_PROJECTION, null, null, null);
			
				if (contactCurosr.moveToFirst()) {
					sms_name = contactCurosr
							.getString(DISPLAY_NAME_COLUMN_INDEX);					
					sms_imgid = contactCurosr.getString(1);
					if(sms_imgid!=null&&sms_imgid.length()>0){
						if(cachehead.get(body.address)!=null){
							head=cachehead.get(body.address).get();
						}else{		
							head=Tools.getContactsHeadBitmap(mContext,body.address);					 
					    cachehead.put(body.address, new SoftReference<Bitmap>(head));
					}
				
					}
					}
				
				contactCurosr.close();
		
			
			
			if(head!=null){
				//有头像
				
			}else if(sms_name!=null){
				//有名字
				
			}else{
				//没头像，没名字
			}
			

			// int lcount = ldao.findLouHuaCount(body.address, body.amonth);
			int weiducount = ldao.queryAllLouhua_accordingtoNumberAndMonth(
					body.address, body.amonth, 1,body.cdate);
			Bitmap bt_icon = ContactIconUtil.getInstance().NumberForIcon(
					body.address, mContext);
			if (bt_icon != null) {
				// iv_icon.setImageBitmap(bt_icon);
			}
			if (sms_name != null) {
				if (lcount > 0) {
					tv_name.setText(sms_name);
					tv_count.setText("(" + lcount + ")");
				} else {
					tv_name.setText(sms_name);
				}
			} else {
				if (lcount > 0) {
					tv_count.setText("(" + lcount + ")");
					tv_name.setText(body.address);

				}
				if (weiducount > 0) {
					// 显示未读标记
					iv_weidu.setVisibility(View.VISIBLE);
				} else {
					iv_weidu.setVisibility(View.INVISIBLE);
				}
				if (body.address.length() > 11) {
					String address = body.address.substring(0, 9) + "..";
					tv_name.setText(address);
				}

			}
			tv_content.setText(body.content);
			String time1 = format_compare.format(new Date());
			String time2 = format_compare.format(body.cdate);
			if (time1 != null && time2 != null && time1.equals(time2)) {
				// tv_date.setText(format_today.format(body.cdate));
				tv_date.setText(format_time.format(body.cdate));
			} else {
				tv_date.setText(format_time.format(body.cdate));

			}
			
		}
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
