package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.Callable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.R.color;
import com.wxxr.callhelper.qg.adapter.LouhuaCishuAdapter;
import com.wxxr.callhelper.qg.adapter.LouhuaSmsAdapter;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.receiver.SendMsgBroadcastReceiver;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.service.IMoblieBusiness;
import com.wxxr.callhelper.qg.ui.gd.GDCMProgressMonitor;
import com.wxxr.callhelper.qg.ui.gd.GDItemLongListDialog;
import com.wxxr.callhelper.qg.ui.gd.GDLeakSessionSettingActivity;
import com.wxxr.callhelper.qg.utils.NSharedPreferences;
import com.wxxr.callhelper.qg.utils.Tools;
 /**
  * 全国版
  * @author yinzhen
  *
  */
public class LouHuaMainActivity extends BaseActivity implements OnItemClickListener, OnItemLongClickListener {
	RelativeLayout rl_tab1;
	RelativeLayout rl_tab2;
	NSharedPreferences sp;
	PopupWindow popupWindow;
	LayoutInflater inflater;
	View huizhi_send;
	RelativeLayout rl_wei_songda;
	RelativeLayout rl_songda;
	TextView tv_tab1;
	TextView tv_tab2;
	DisplayMetrics metric;
	ListView ll_maincontent;
	LinearLayout ll_pingdao;
	LinearLayout ll_changjing;
	RelativeLayout ll_main_total;
	boolean click_style = false;
	PopupWindow pop;
	ArrayList<BodyBean> array_mainlist;
	ArrayList<ArrayList<BodyBean>> array_mainlist_main;

	SimpleDateFormat format_title = new SimpleDateFormat("yyyy年MM月");

	SimpleDateFormat format_weektitle = new SimpleDateFormat("yyyy年MM月dd日");
	final long WEEK_DAYTIME = 1000 * 60 * 60 * 24 * 7L;
	final long ONE_DAYTIME = 1000 * 60 * 60 * 24L;
	boolean tab_style = true;// true 漏接电话，false漏话次数
	RefreshContentReceiver rcrReceiver;
	LouHuaDao ldao;
//	private LinkedList<DCMyViewGroupItem> items = new LinkedList<DCMyViewGroupItem>();
//	private LinkedList<DCMyViewGroupLouHuaCiShu> itemsphonetime = new LinkedList<DCMyViewGroupLouHuaCiShu>();
	private View v_left, v_right;
	private TextView louhua_thisweek;
	private TextView louhua_all;
	private TextView louhua_thismonth;
	// key 为月份+日期+phonenumber，vlaue 是数量
	Hashtable<String, Integer> weekcount = new Hashtable<String, Integer>();
	Hashtable<String, String> address = new Hashtable<String, String>();
	LouhuaSmsAdapter smsAdapter;
//	LouhuaCishuAdapter cushuAdapter;
	View louhuacishu_filter_div;
	private SendMsgBroadcastReceiver sendMsgReceiver = new SendMsgBroadcastReceiver();
	private IntentFilter filter = new IntentFilter();
	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (tab_style) {
				// ll_maincontent.removeAllViews();
				// ll_maincontent.addView(getSecondView());
				louhuacishu_filter_div.setVisibility(View.GONE);
				
				if(array_mainlist_main.size()==0){
					if(TextUtils.isEmpty(spNumber) || TextUtils.isEmpty(sContent) ){
						getData();
					}
					ll_maincontent.setVisibility(View.GONE);
					louhua_empty.setVisibility(View.VISIBLE);
//					louhua_empty.setText("您还没有漏接电话记录");
					
				}else{
					ll_maincontent.setVisibility(View.VISIBLE);
					louhua_empty.setVisibility(View.GONE);
				smsAdapter.setMonthCountHashtable(tempmonthcountlist);
				smsAdapter
						.setMonthCountOfOnePeopleHashtable(tempmonth_person_countlist);
				smsAdapter.setData(array_mainlist_main);
				ll_maincontent.setPadding(0, 0, 0, 0);
				ll_maincontent.setAdapter(smsAdapter);
				smsAdapter.notifyDataSetChanged();
				}
			} 
			processbar.setVisibility(View.GONE);
			
			filter.addAction(Sms.SENT_SMS_ACTION);
			registerReceiver(sendMsgReceiver, filter);
		}

	};
	private TextView tv_titlebar_name;
	private LinearLayout louhua_empty;
	private View processbar;
	private String spNumber, sContent, bTitle;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.leak_main);
		if(null == activity){
			activity = this;
		}

		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this,0);
		sp = NSharedPreferences.getInstance(getApplicationContext());
		sp.get(Constant.HUIZHI_STYLE, 0);
		ldao = LouHuaDao.getInstance(this);
		inflater = LayoutInflater.from(this);
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		array_mainlist_main = new ArrayList<ArrayList<BodyBean>>();

		// LinearLayout ll = (LinearLayout)
		// findViewById(R.id.ll_huizhi_main_bg);
		// Bitmap readBitmap = Tools.readBitmap(this,
		// R.drawable.main_bg_communication);
		// BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		// ll.setBackgroundDrawable(bitmapDrawable);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		smsAdapter = new LouhuaSmsAdapter(this, metric, activity);
	//	cushuAdapter = new LouhuaCishuAdapter(this, metric);
		ll_maincontent = (ListView) findViewById(R.id.leaklistview_main_content);
		ll_maincontent.setAdapter(smsAdapter);
		ll_maincontent.setOnItemClickListener(this);
		ll_maincontent.setOnItemLongClickListener(this);
		ll_main_total = (RelativeLayout) findViewById(R.id.ll_leak_main);
		iv_bottom_shadow = (ImageView) findViewById(R.id.iv_bottom_shadow);
		iv_bottom_shadow.setVisibility(View.VISIBLE);

		findView();

		IntentFilter testAction = new IntentFilter(
				"com.wxxr.viewgroup.refreshlouhua");
		rcrReceiver = new RefreshContentReceiver();
		registerReceiver(rcrReceiver, testAction);
		
	}

	@Override
	protected void onStop() {

		if (pop != null) {
			pop.dismiss();
			pop = null;

//			for (DCMyViewGroupItem p : this.items) {
//				p.setCheck("1");
//			}

		}

		super.onStop();
	}

	@Override
	protected void onResume() {
		if (pop != null) {
			pop.dismiss();
			pop = null;
		}

		//清除头像集合
		if(smsAdapter != null){
			smsAdapter.adapterClearPortraitMaps();
		}
		
		smsAdapter.setHideSel(false);
		startQuery2();
		
		super.onResume();
	}

	private void showMenu() {
		
		view = View.inflate(LouHuaMainActivity.this, R.layout.menu, null);
		pop = getMenu(this, view);
		pop.showAtLocation(ll_main_total, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
		view.findViewById(R.id.ll_delete).setOnClickListener(this);
		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
		gd_tv_menu_all = (TextView) view.findViewById(R.id.gd_tv_menu_all);
		gd_tv_menu_cancel = (TextView) view.findViewById(R.id.gd_tv_menu_cancel);
		gd_tv_menu_del = (TextView) view.findViewById(R.id.gd_tv_menu_del);
		smsAdapter.setShowSel();
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setVisibility(View.GONE);
	}
	
	/**
	 * 单选 &全选状态
	 */
	public void pressAll(){
		gd_tv_menu_all.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_del.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
	}
	
	/**
	 * 初始化&取消全选状态
	 */
	public void pressCancelAll(){
		gd_tv_menu_all.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(R.color.gd_item_eighty));
		gd_tv_menu_del.setTextColor(getResources().getColor(R.color.gd_item_eighty));
	}

	private void findView() {
		
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("来电提醒");
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setOnClickListener(this);
		
		rl_tab2 = (RelativeLayout) findViewById(R.id.leak_tab2);
		rl_tab1 = (RelativeLayout) findViewById(R.id.leak_tab1);

		tv_tab1 = (TextView) findViewById(R.id.leak_text_tab1);
		tv_tab2 = (TextView) findViewById(R.id.leak_text_tab2);

		rl_tab1.setOnClickListener(this);
		rl_tab2.setOnClickListener(this);

		v_left = findViewById(R.id.v_left);
		v_right = findViewById(R.id.v_right);
		louhuacishu_filter_div = findViewById(R.id.louhuacishu_filter_div);
		louhua_thisweek = (TextView) findViewById(R.id.louhua_thisweek);
		louhua_thismonth = (TextView) findViewById(R.id.louhua_thismonth);
		louhua_all = (TextView) findViewById(R.id.louhua_all);

		louhua_thisweek.setOnClickListener(this);
		louhua_thismonth.setOnClickListener(this);
		louhua_all.setOnClickListener(this);
		louhua_empty=(LinearLayout)findViewById(R.id.louhua_empty);
		processbar=findViewById(R.id.leak_main_process);
		
		findViewById(R.id.gd_empty_lh_hz_btn).setOnClickListener(this);
		
		ImageView qg_iv_louhua_empty = (ImageView) findViewById(R.id.qg_iv_louhua_empty);
		TextView qg_tv_louhua_empty_text = (TextView) findViewById(R.id.qg_tv_louhua_empty_text);
		
		if(Constant.imgChangedProviceLists.contains(Tools.getProviceName())){
			qg_iv_louhua_empty.setImageResource(R.drawable.qg_special_louhua_empty_img);
			qg_tv_louhua_empty_text.setText("如果您没办理通信助手业务，无法使用此功能");
		}
		
	}

	private void getData() {
		GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {
			
			@Override
			protected void handleFailed(Throwable cause) {
				
			}
			
			@Override
			protected void handleDone(Object returnVal) {
				MoblieBusinessBean bean = (MoblieBusinessBean) returnVal;
				if(null != bean){
					spNumber = bean.getSpNumber();
					sContent = bean.getSmscode();
//					bTitle = bean.getTile();
				}
			}
			
		};
		
		monitor.executeOnMonitor(new Callable<Object>() {
			
			@Override
			public Object call() throws Exception {
				return getService(IMoblieBusiness.class).getLaiDianTiXing();
			}
		});
		
//		huizhi_send = inflater.inflate(R.layout.huizhi_send, null);
//		rl_wei_songda = (RelativeLayout) huizhi_send
//				.findViewById(R.id.rl_weisongda);
//		rl_songda = (RelativeLayout) huizhi_send.findViewById(R.id.rl_songda);
//		rl_wei_songda.setOnClickListener(this);
//		rl_songda.setOnClickListener(this);

	}

	private void startQuery2() {
	   new FetchData().execute( );

	}
	
	
	
	class FetchData extends AsyncTask<Context , Void, ArrayList<ArrayList<BodyBean>> >{
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (!tab_style) {
				if (timesle == ALLTIME) {				
					if (louhua_thisweek != null) {
						louhua_thisweek.setSelected(false);
						louhua_thismonth.setSelected(false);
						louhua_all.setSelected(true);
						louhua_all.setTextColor(Color.parseColor("#ffffff"));
						louhua_thismonth.setTextColor(Color.parseColor("#3995d6"));
						louhua_thisweek.setTextColor(Color.parseColor("#3995d6"));
					}
				} else if (timesle == THISMONTH) {				
					if (louhua_thisweek != null) {
						louhua_thisweek.setSelected(false);
						louhua_thismonth.setSelected(true);
						louhua_all.setSelected(false);
						
						louhua_all.setTextColor(Color.parseColor("#3995d6"));
						louhua_thismonth.setTextColor(Color.parseColor("#ffffff"));
						louhua_thisweek.setTextColor(Color.parseColor("#3995d6"));
					}
				} else if (timesle == THISWEEK) {			
					if (louhua_thisweek != null) {
						louhua_thisweek.setSelected(true);
						louhua_thismonth.setSelected(false);
						louhua_all.setSelected(false);
						
						louhua_all.setTextColor(Color.parseColor("#3995d6"));
						louhua_thismonth.setTextColor(Color.parseColor("#3995d6"));
						louhua_thisweek.setTextColor(Color.parseColor("#ffffff"));
					}
				}
			} 
			processbar.setVisibility(View.VISIBLE);
		}
		

		@Override
		protected ArrayList<ArrayList<BodyBean>> doInBackground(
				Context... params) {			
			return getRealContentSecond(LouHuaMainActivity.this);
		}
		
		@Override
		protected void onPostExecute(ArrayList<ArrayList<BodyBean>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			array_mainlist_main.clear();
			array_mainlist_main.addAll(result);
			Message msg = handler.obtainMessage();
			handler.sendMessage(msg);		
		}
		
		
	}
	
	Object lock=new Object();
//	private boolean atprocess=false;
	@Override
	public void onClick(View v) {
		
//		synchronized(lock){
//         if(!atprocess){
//        	 atprocess=true;
		switch (v.getId()) {
		
		case R.id.gd_empty_lh_hz_btn:
			if(!TextUtils.isEmpty(spNumber) && !TextUtils.isEmpty(sContent)){
				Intent intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.NOW_OPEN);
				intent.putExtra(Constant.DIALOG_PROVICE_TITLE, "来电提醒");
				intent.putExtra(Constant.QG_BUSINESS_POST, spNumber);
				intent.putExtra(Constant.QG_BUSINESS_CODE, sContent);
				startActivity(intent);
			} else {
				Toast.makeText(this, "暂不支持此业务！", 0).show();
			}
			break;
		
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
			
		case R.id.gd_iv_titlebar_right:
			startActivity(new Intent(this, GDLeakSessionSettingActivity.class));
			break;
			
		case R.id.leak_tab1:

			click_style = false;
			rl_tab1.setEnabled(false);
			rl_tab2.setEnabled(true);
			tv_tab1.setTextColor(getResources().getColor(R.color.white));
			tv_tab2.setTextColor(getResources().getColor(R.color.leak_msg_bg));
			v_left.setVisibility(View.VISIBLE);
			v_right.setVisibility(View.INVISIBLE);
			// rl_tab1.setBackgroundResource(R.drawable.title_qian);
			// rl_tab2.setBackgroundDrawable(null);
			// ll_maincontent.removeAllViews();
			tab_style = true;
			startQuery2();

			break;

		case R.id.leak_tab2:

			click_style = true;
			rl_tab1.setEnabled(true);
			tv_tab2.setTextColor(getResources().getColor(R.color.white));
			tv_tab1.setTextColor(getResources().getColor(R.color.leak_msg_bg));
			v_right.setVisibility(View.VISIBLE);
			v_left.setVisibility(View.INVISIBLE);
			// rl_tab2.setBackgroundResource(R.drawable.title_qian);
			// rl_tab1.setBackgroundDrawable(null);
			// ll_maincontent.removeAllViews();
			tab_style = false;
			if (pop != null) {
				 
				pop.dismiss();
				pop = null;
				smsAdapter.setHideSel(false);
				// for (DCMyViewGroupItem p : this.items) {
				// p.setCheck("1");
				// }
			}
			startQuery2();
			break;

		case R.id.ll_all_choose:

			// for (DCMyViewGroupItem p : this.items) {
			// p.setCheck("all");
			// }
			smsAdapter.setSelAll();
			break;

		case R.id.ll_delete:

//			dls = new ArrayList<DeleteVO>();
//			for (DCMyViewGroupItem p : this.items) {
//				if (p.isChecked()) {
//					DeleteVO d = new DeleteVO();
//					d.telnumber = p.body.address;
//					d.month = p.body.amonth;
//					dls.add(d);
//				}
//			}
			
			dls=smsAdapter.getSelteItems();
			
			if (dls.size() == 0) {
				return;
			}
			// ApplicationManager am = (ApplicationManager) getApplication();
			// am.dls = dls;
			Intent intent6 = new Intent(this, ConfirmDialogActivity.class);
			intent6.putExtra(Constant.DIALOG_KEY, Constant.DELETE_RECORD);
			intent6.putExtra(Constant.DIALOG_CONTENT, "删除联系人漏接电话记录？");
			startActivityForResult(intent6, 100);
			// intent6.putExtra(Constant.PHONE_NUMBER, "delete_select");
			// intent6.setClass(this, DeleteHuiHuaDialogActivity.class);
			// startActivity(intent6);

			break;

		case R.id.ll_cancle:

			// for (DCMyViewGroupItem p : this.items) {
			// p.setCheck("cancel");
			// }

			smsAdapter.setCancelAll();
			break;

		case R.id.louhua_thisweek:

			timesle = THISWEEK;
			startQuery2();
			break;

		case R.id.louhua_thismonth:

			timesle = THISMONTH;
			startQuery2();
			break;

		case R.id.louhua_all:

			timesle = ALLTIME;
			startQuery2();
			break;
		}
//		atprocess=false;
//		}
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case Constant.DELETE_RECORD:
			for (BodyBean vo : dls) {
				ldao.deleteSMSAccordingtoNumber(vo.address);
			}
			dls.clear();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Tools.isPortrait(this)) {

			if (keyCode == KeyEvent.KEYCODE_MENU) {

				if (tab_style) {
					if (pop != null && pop.isShowing()) {
						hideMenu();

					} else {
						showMenu();
//						for (DCMyViewGroupItem p : this.items) {
//							p.setCheck("2");
//						}

					}

				}

			} else if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (pop != null && pop.isShowing()) {
					hideMenu();
					return true;
				}
			}
		}

		return super.onKeyDown(keyCode, event);

	}

	private void hideMenu() {
		pop.dismiss();
		pop = null;

		// for (DCMyViewGroupItem p : this.items) {
		// p.setCheck("1");
		// }

		smsAdapter.setHideSel(true);
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setVisibility(View.VISIBLE);

	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return false;
	}

	

	SimpleDateFormat format_yue = new SimpleDateFormat("yyyyMM");
	SimpleDateFormat format_yue_day = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat format_day = new SimpleDateFormat("MMdd");

	private ArrayList<BodyBean> dls;
	private ImageView iv_bottom_shadow;
	private int ALLTIME = 1;
	private int THISWEEK = 2;
	private int THISMONTH = 3;
	int timesle = THISWEEK;
	private Hashtable<String, Integer> tempmonthcountlist;
	private Hashtable<String, Integer> tempmonth_person_countlist;
	private TextView gd_tv_menu_all, gd_tv_menu_cancel, gd_tv_menu_del;
	private LouHuaMainActivity activity;
	private View view;

	public ArrayList<ArrayList<BodyBean>> getRealContentSecond(Context context) {
		ArrayList<ArrayList<BodyBean>> bodyss = new ArrayList<ArrayList<BodyBean>>();
		LouHuaDao ldao = LouHuaDao.getInstance(context);
		long startime = 0, endtime = 0;
		if (!tab_style) {
			if (timesle == ALLTIME) {
				startime = -1;
				endtime = -1;				
			} else if (timesle == THISMONTH) {
				Date d = new Date();
				startime = Tools.getMaxMillisByDay(d.getYear() + 1900 + "",
						d.getMonth() + 1 + "", "1");
				endtime = Tools.getMillisByDay(d.getYear() + 1900 + "",
						d.getMonth() + 1 + "", "" + Tools.getlastDay());
				
			} else if (timesle == THISWEEK) {
				startime = Tools.getMondayOfWeek();
				endtime = Tools.getSundayOfWeek();			
			}
		} else {
			startime = -1;
			endtime = -1;
		}
		Date d = new Date();
		d.setTime(startime);

		d.setTime(endtime);

		ArrayList<BodyBean> alast = ldao.queryAllLouHua(startime, endtime);

		Hashtable<String, ArrayList<BodyBean>> timelist = new Hashtable<String, ArrayList<BodyBean>>();

		// 统计每个月份有多少条
		if (tempmonthcountlist != null) {
		//	monthcountlist.clear();
		}
		tempmonthcountlist = new Hashtable<String, Integer>();

		// 统计每个月份，每个人有多少条
		if (tempmonth_person_countlist != null) {
		//	month_person_countlist.clear();
		}
		tempmonth_person_countlist = new Hashtable<String, Integer>();
                //记录每个人，每月的最新一条，或者是 每天的最新一条
		Hashtable<String, String> time_person = new Hashtable<String, String>();

		for (BodyBean item : alast) {
			String rdate = "";
			if (!tab_style && timesle == THISWEEK) {
				rdate = format_yue_day.format(new Date(item.cdate));
			} else {
				rdate = format_yue.format(new Date(item.cdate));
			}
			ArrayList<BodyBean> list = null;
//			String key = rdate + "-" + item.address;
			String key = item.address;
			
			if (time_person.get(key) == null) {
				if (timelist.get(rdate) == null) {
					list = new ArrayList<BodyBean>();
					timelist.put(rdate, list);
				}

				list = timelist.get(rdate);
				list.add(item);
				time_person.put(key, "");
			} else {

			}

			// 计入每个人的每月的数量
			if (tempmonth_person_countlist.get(key) == null) {
				tempmonth_person_countlist.put(key, 1);
			} else {
				tempmonth_person_countlist.put(key, tempmonth_person_countlist.get(key)
						.intValue() + 1);
			}

			// 计入的每月的总数量
			if (tempmonthcountlist.get(rdate) == null) {
				tempmonthcountlist.put(rdate, 1);
			} else {
				tempmonthcountlist.put(rdate,
						tempmonthcountlist.get(rdate).intValue() + 1);
			}
		}

		// java.util.Collections.sort(list)

		Enumeration<String> keys = timelist.keys();
		ArrayList<String> list = new ArrayList<String>();

		while (keys.hasMoreElements()) {
			list.add(keys.nextElement());
		}

		java.util.Collections.sort(list, new sort());
		int cont = list.size();
		for (int i = 0; i < cont; i++) {
			bodyss.add(timelist.get(list.get(i)));
		}

		return bodyss;
	}

	class sort implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			return (int) (Long.parseLong(rhs) - Long.parseLong(lhs));
		}
	}

	private class RefreshContentReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// startQuery2();
		}

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(rcrReceiver);
		unregisterReceiver(sendMsgReceiver);
		super.onDestroy();
	}

	public static ArrayList<BodyBean> singleElement(ArrayList<BodyBean> al) {
		ArrayList<BodyBean> alt = new ArrayList<BodyBean>();
		Iterator<BodyBean> it = al.iterator();
		while (it.hasNext()) {
			BodyBean obj = it.next();
			if (!alt.contains(obj))
				alt.add(obj);
		}
		return alt;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (tab_style) {
			BodyBean body = (BodyBean) smsAdapter.getItem(position);
			Intent intent = new Intent();
			intent.putExtra("smsmonth", body.amonth);
			String number="";
			if(body.realnum!=null){
				number=body.realnum;				
			}else{
				number=body.address;			
			}
			intent.putExtra(Constant.PHONE_NUMBER,number);
			intent.putExtra("huadong", "sliding");
			intent.setClass(this, SmsContentActivity.class);
			startActivity(intent);
			ldao.update(number, body.amonth, 0,body.cdate);
			view.setBackgroundColor(getResources().getColor(R.color.gd_callrecord_long_press));
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		if (tab_style) {
			BodyBean body = (BodyBean) smsAdapter.getItem(position);
			Intent intent = new Intent(this, GDItemLongListDialog.class);
			intent.putExtra(Constant.DIALOG_KEY,Constant.LH_DETAIL_LONG_DELETE_RECORD);
			intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人漏接电话记录？");
			intent.putExtra("smsmonth", body.amonth);
			if(body.realnum!=null){
				intent.putExtra(Constant.PHONE_NUMBER, body.realnum);
			}else{
				intent.putExtra(Constant.PHONE_NUMBER, body.address);
			}
		
			intent.putExtra("smstime", body.cdate);
			startActivity(intent);
			view.setBackgroundColor(getResources().getColor(R.color.gd_callrecord_long_press));
		}
		return false;
	}
	
}
