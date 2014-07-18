package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.BodyBeanHuiZhi;
import com.wxxr.callhelper.qg.bean.DeleteVO;
import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.event.ImporDataOKEvent;
import com.wxxr.callhelper.qg.event.UserBoundEvent;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.receiver.SendMsgBroadcastReceiver;
import com.wxxr.callhelper.qg.service.ICopyOldData;
import com.wxxr.callhelper.qg.service.IMoblieBusiness;
import com.wxxr.callhelper.qg.ui.gd.GDCMProgressMonitor;
import com.wxxr.callhelper.qg.ui.gd.GDItemLongListDialog;
import com.wxxr.callhelper.qg.ui.gd.GDSmsHuiZhiSettingActivity;
import com.wxxr.callhelper.qg.utils.NSharedPreferences;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.DCMyViewGroupHuiZhiBaoGao;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;

/*
 * 回执 列表页
 */
public class SmsHuiZhiMainActivity extends BaseActivity {
	// private RelativeLayout rl_tab1;
	// private RelativeLayout rl_tab2;
	private NSharedPreferences sp;
	private PopupWindow popupWindow;
	private LayoutInflater infater;
	private View huizhi_send;
	private RelativeLayout rl_wei_songda;
	private RelativeLayout rl_songda;
	// private TextView tv_tab1;
	// private TextView tv_tab2;
	private DisplayMetrics metric;
	// LinearLayout ll_maincontent;
	private LinearLayout ll_pingdao;
	private LinearLayout ll_changjing;

	// private LinearLayout huizhi_gongnen_div;

	private ListView huizhi_listview_div;

	private HuizhiAdapter madapter;

	ArrayList<ArrayList<BodyBeanHuiZhi>> array_mainlist_main;
	private ArrayList<BodyBeanHuiZhi> all_reals;
	private ArrayList<BodyBeanHuiZhi> arrive;
	private ArrayList<BodyBeanHuiZhi> notarrive;
	private ArrayList<BodyBeanHuiZhi> reals;
	private SimpleDateFormat format_title = new SimpleDateFormat("yyyy年MM月");
	boolean click_style = false;// 点击功能设置
	private HuiZhiBaoGaoDao hdao;
	private String username;
	private final String str_all = "您还没有短信回执记录";;
	private RefreshContentReceiverHZ rcrReceiver;
	private PopupWindow pop;
	private RelativeLayout ll_main_total;
	// private LinkedList<DCMyViewGroupHuiZhiBaoGao> items = new
	// LinkedList<DCMyViewGroupHuiZhiBaoGao>();
	private int click_type = 0;// 0：全部回执，1，未送达，2，已送达
	private View vi_line1;
	private View vi_line2;
	private ImageView iv_title_sanjiao;
	private ArrayList<BodyBeanHuiZhi> all;
	private Hashtable<Long, BodyBeanHuiZhi> selitem = new Hashtable<Long, BodyBeanHuiZhi>();
	private boolean showsel = false;
	private boolean firstIn = true;
	private SendMsgBroadcastReceiver sendMsgReceiver = new SendMsgBroadcastReceiver();
	private IntentFilter filter = new IntentFilter();
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				all = hdao.queryAllHuiZhi();
				all_reals = getRealContent(all);
				break;

			case 1:
				// ll_maincontent.removeAllViews();
				View view = getFirstView(all_reals, str_all, click_type);
				// if (view != null) {
				// ll_maincontent.addView(getFirstView(all_reals, str_all,
				// click_type));
				// }
				break;

			}

		}

	};
	private View gongnengline;
	DCMyViewGroupHuiZhiBaoGao binddatatool;
	private LinearLayout huizhi_empty_text;
	private View huizhiprocess;
	private String spNumber, sContent, bTitle;

	private IEventListener listener = new IEventListener() {
		@Override
		public void onEvent(IBroadcastEvent event) {
			if (event instanceof ImporDataOKEvent) {
				if (!isFinishing()) {
					new FetchData().execute();
				}
			}

		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.huizhi_mainpage22);
		((com.wxxr.callhelper.qg.widget.BottomTabBar) findViewById(R.id.home_bottom_tabbar))
				.setActivtiy(this, 0);

		sp = NSharedPreferences.getInstance(getApplicationContext());
		sp.get(Constant.HUIZHI_STYLE, 0);
		sp.get("menu_open", false);
		sp.update("menu_open", false);
		hdao = HuiZhiBaoGaoDao.getInstance(this);
		array_mainlist_main = new ArrayList<ArrayList<BodyBeanHuiZhi>>();
		all_reals = new ArrayList<BodyBeanHuiZhi>();

		// all = hdao.queryAllHuiZhi();
		// all_reals = getRealContent(all);

		// queryData();
		// LinearLayout ll = (LinearLayout)
		// findViewById(R.id.ll_huizhi_main_bg);
		// Bitmap readBitmap = Tools.readBitmap(this,
		// R.drawable.main_bg_communication);
		// BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		// ll.setBackgroundDrawable(bitmapDrawable);

		ll_main_total = (RelativeLayout) findViewById(R.id.ll_huizhi_main_bg);
		infater = LayoutInflater.from(this);
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		binddatatool = DCMyViewGroupHuiZhiBaoGao.getInstance(this, metric,
				null, 0);
		// ll_maincontent = (LinearLayout) findViewById(R.id.ll_main_content);

		madapter = new HuizhiAdapter();

		findView();

		Intent intent = getIntent();
		String flag = intent.getStringExtra("shouye");
		// ll_maincontent.removeAllViews();

		// huizhi_empty_text.setText(str_all);
		// if (flag == null) {
		// View view = getFirstView(all_reals, str_all, click_type);
		// // if (view != null) {
		// // ll_maincontent.addView(view);
		// // }
		//
		// } else if (flag.equals("shouye")) {
		// View view = getFirstView(all_reals, str_all, click_type);
		// // if (view != null) {
		// // ll_maincontent.addView(getFirstView(all_reals, str_all,
		// // click_type));
		// // }
		//
		// click_style = false;
		//
		// // rl_tab1.setBackgroundResource(R.drawable.title_qian_xuanzhong);
		// // rl_tab2.setBackgroundDrawable(null);
		//
		// } else {
		// // ll_maincontent.addView(getSecondView());
		//
		// }

		IntentFilter testAction = new IntentFilter(
				"com.wxxr.viewgroup.refreshhuizhiall");
		rcrReceiver = new RefreshContentReceiverHZ();
		registerReceiver(rcrReceiver, testAction);

	}

	private void findView() {

		TextView tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("短信回执");

		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setOnClickListener(this);

		findViewById(R.id.gd_empty_lh_hz_btn).setOnClickListener(this);

		// rl_tab2 = (RelativeLayout) findViewById(R.id.rl_tab2);
		// rl_tab1 = (RelativeLayout) findViewById(R.id.rl_tab1);

		// tv_tab1 = (TextView) findViewById(R.id.huizhi_text_tab1);
		// tv_tab2 = (TextView) findViewById(R.id.huizhi_text_tab2);
		vi_line1 = findViewById(R.id.v_left);
		vi_line2 = findViewById(R.id.v_right);
		iv_title_sanjiao = (ImageView) findViewById(R.id.iv_title_sanjiao);
		// tv_tab1.setTextColor(Color.parseColor("#ffffff"));
		// tv_tab2.setTextColor(Color.parseColor("#013c79"));
		// rl_tab1.setOnClickListener(this);
		// rl_tab2.setOnClickListener(this);

		huizhiprocess = findViewById(R.id.huizhi_process);

		// huizhi_gongnen_div = (LinearLayout)
		// findViewById(R.id.huizhi_gongnen_div);
		huizhi_listview_div = (ListView) findViewById(R.id.huizhi_listview_div);
		huizhi_listview_div.setAdapter(madapter);
		huizhi_empty_text = (LinearLayout) findViewById(R.id.huizhi_empty);
		ImageView qg_iv_empty = (ImageView) findViewById(R.id.qg_iv_huizhi_empty);
		TextView qg_tv_huizhi_empty_text = (TextView) findViewById(R.id.qg_tv_huizhi_empty_text);
		
		if(Constant.imgChangedProviceLists.contains(Tools.getProviceName())){
			qg_iv_empty.setImageResource(R.drawable.qg_special_huizhi_empty_img);
			qg_tv_huizhi_empty_text.setText("如果您没办理通信助手业务，无法使用此功能");
		}

		huizhi_listview_div
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (pop == null) {
							BodyBeanHuiZhi body = (BodyBeanHuiZhi) madapter
									.getItem(position);
							Intent intent = new Intent();
							intent.putExtra("smsarrive_style", body.mstyle);
							intent.putExtra(Constant.PHONE_NUMBER,
									body.tosomebody);
							intent.putExtra("smsport", body.address);
							intent.putExtra("smsmonth", body.amonth);
							intent.putExtra("smstime", body.cdate);
							intent.setClass(SmsHuiZhiMainActivity.this,
									SmsContentHuiZhiActivity2Version.class);
							intent.putExtra("mstyle", click_type);
							// intent.setClass(mContext,
							// NewHuiZhiContentActivity.class);
							hdao.update(body.tosomebody, body.amonth, 0);
							startActivity(intent);
							view.setBackgroundColor(getResources().getColor(
									R.color.gd_callrecord_long_press));
						}
					}

				});

		huizhi_listview_div
				.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						if (pop == null) {
							BodyBeanHuiZhi body = (BodyBeanHuiZhi) madapter
									.getItem(position);

							Intent intent = new Intent(
									SmsHuiZhiMainActivity.this,
									GDItemLongListDialog.class);
							intent.putExtra(Constant.DIALOG_KEY,
									Constant.HZ_DETAIL_LONG_DELETE_RECORD);
							intent.putExtra(Constant.DIALOG_CONTENT,
									"删除联系人短信回执记录？");
							intent.putExtra(Constant.PHONE_NUMBER,
									body.tosomebody);
							intent.putExtra("smsport", body.address);
							intent.putExtra("smsmonth", body.amonth);
							intent.putExtra("smstime", body.cdate);
							startActivity(intent);
							view.setBackgroundColor(getResources().getColor(
									R.color.gd_callrecord_long_press));
						}
						return true;
					}

				});
	}

	private void getData() {
		GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(Object returnVal) {
				MoblieBusinessBean bean = (MoblieBusinessBean) returnVal;
				if (null != bean) {
					spNumber = bean.getSpNumber();
					sContent = bean.getSmscode();
//					bTitle = bean.getTile();
				}
			}

		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getService(IMoblieBusiness.class).getDuanXinHuiZhi();
			}
		});

		// huizhi_send = infater.inflate(R.layout.huizhi_send, null);
		// rl_wei_songda = (RelativeLayout) huizhi_send
		// .findViewById(R.id.rl_weisongda);
		// rl_songda = (RelativeLayout)
		// huizhi_send.findViewById(R.id.rl_songda);
		// rl_wei_songda.setOnClickListener(this);
		// rl_songda.setOnClickListener(this);

	}

	private void queryData() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				Message msg = handler.obtainMessage();
				msg.what = 0;
				handler.sendMessage(msg);
			}
		}).start();
	}

	// public View getSecondView() {
	// View view = infater.inflate(R.layout.huizhi_home, huizhi_gongnen_div);
	// LinearLayout llmain = (LinearLayout) view
	// .findViewById(R.id.ll_gongneng_back);
	//
	// Bitmap readBitmap = Tools.readBitmap(this,
	// R.drawable.huizhi_gongneng_mainback);
	// BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
	// llmain.setBackgroundDrawable(bitmapDrawable);
	//
	// ll_pingdao = (LinearLayout) view.findViewById(R.id.ll_pingdaodingzhi);
	// ll_changjing = (LinearLayout) view
	// .findViewById(R.id.ll_jieshouchangjing);
	// ll_pingdao.setOnClickListener(this);
	// ll_changjing.setOnClickListener(this);
	//
	// return view;
	//
	// }

	/**
	 * 
	 * @param reals
	 *            全部短信、未送达、已送达的集合
	 * @param str
	 *            没有相关短信时显示的提示语
	 * @return
	 */
	public View getFirstView(ArrayList<BodyBeanHuiZhi> reals, String nodatatip,
			int clicktype) {

		// huizhi_gongnen_div.setVisibility(View.GONE);
		huizhi_listview_div.setVisibility(View.VISIBLE);
		if (reals.size() != 0) {
			madapter.setData(reals);
			madapter.notifyDataSetChanged();
			huizhi_listview_div.setVisibility(View.VISIBLE);
			huizhi_empty_text.setVisibility(View.GONE);
		} else {
			if(TextUtils.isEmpty(spNumber) || TextUtils.isEmpty(sContent) ){
				getData();
			}
			huizhi_listview_div.setVisibility(View.GONE);
			huizhi_empty_text.setVisibility(View.VISIBLE);
			// huizhi_empty_text.setText(nodatatip);
		}
		
		filter.addAction(Sms.SENT_SMS_ACTION);
		registerReceiver(sendMsgReceiver, filter);
		// this.items.clear();
		// View view2 = null;
		// array_mainlist_main.clear();
		// // hdao = HuiZhiBaoGaoDao.getInstance(this);
		// view2 = infater.inflate(R.layout.dc_def_main, null);
		// RelativeLayout lvTop = (RelativeLayout) view2
		// .findViewById(R.id.ll_main_top);
		// android.widget.RelativeLayout.LayoutParams anzhiLP = new
		// RelativeLayout.LayoutParams(
		// LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// anzhiLP.addRule(RelativeLayout.CENTER_IN_PARENT);
		// if (reals != null && reals.size() > 0) {
		// array_mainlist_main.addAll(reals);
		// LinearLayout lv = (LinearLayout) view2.findViewById(R.id.ll_main);
		// for (int i = 0; i < array_mainlist_main.size(); i++) {
		//
		// ArrayList<BodyBeanHuiZhi> arrayList = array_mainlist_main
		// .get(i);
		//
		// if (arrayList.size() > 0) {
		// lv.addView(getListChildView2(arrayList, clicktype));
		// }
		//
		// }
		//
		// } else {
		// sp.update("menu_open", true);// 关闭menu
		// TextView mTextView = new TextView(SmsHuiZhiMainActivity.this);//
		// 构造textView对象
		// mTextView.setTextSize(22.0f);
		// mTextView.setTextColor(Color.parseColor("#8a99c4"));
		// mTextView.setText(str);
		// mTextView.setGravity(Gravity.CENTER);
		// lvTop.addView(mTextView, anzhiLP);
		// }
		return null;

	}

	@Override
	protected void onResume() {
		Log.e("onResume", "onResume");
		// tv_tab1.setText("回执报告");
		if (pop != null) {
			pop.dismiss();
			pop = null;
		}
		// queryData();
		if (binddatatool != null) {
			binddatatool.clearPortraitMaps();
		}
		madapter.clearCacheNum();
		all_reals.clear();
		showsel = false;
		selitem.clear();
		click_type = 0;
		// ll_maincontent.removeAllViews();

		if (firstIn && (!getService(ICopyOldData.class).isImportFinish())) {
			getService(IEventRouter.class).registerEventListener(
					ImporDataOKEvent.class, listener);
		}

		if (click_style) {
			// if (gongnengline == null) {
			// gongnengline = getSecondView();
			// }
			// huizhi_gongnen_div.setVisibility(View.VISIBLE);
			// huizhi_listview_div.setVisibility(View.GONE);
			// huizhi_empty_text.setVisibility(View.GONE);

		} else {
			refetchData();
		}
		Log.i("click_type", click_type + "");

		firstIn = false;

		super.onResume();
	}

	/**
	 * 是否激活
	 */
	private void isActivation() {
		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(Object returnVal) {
				boolean isActivation = (Boolean) returnVal;
				if (isActivation) {
					// true 已激活
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.USER_SETTING.ordinal());
					}
					startActivity(new Intent(SmsHuiZhiMainActivity.this,
							GDSmsHuiZhiSettingActivity.class));
				} else {
					// false 未激活
					// Intent intent = new Intent(SmsHuiZhiMainActivity.this,
					// HomeActivity.class);
					// intent.putExtra("homeindex", 3);
					// startActivity(intent);
					Tools.Login(SmsHuiZhiMainActivity.this,
							GDSmsHuiZhiSettingActivity.class);
				}
			}

			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在操作激活数据,请稍侯...");
				return map;
			}

		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getService(IUserActivationService.class)
						.isUserActivated();
			}
		});
	}

	class FetchData extends AsyncTask<Context, Void, ArrayList<BodyBeanHuiZhi>> {

		@Override
		protected void onPostExecute(ArrayList<BodyBeanHuiZhi> ppresult) {
			// TODO Auto-generated method stub
			super.onPostExecute(ppresult);
			if (!click_style) {// tab在回执报告这边
				if (click_type == 0) {
					getFirstView(ppresult, str_all, click_type);
				}
			}
			huizhiprocess.setVisibility(View.GONE);
		}

		@Override
		protected ArrayList<BodyBeanHuiZhi> doInBackground(Context... params) {

			if (click_type == 0) {
				all = hdao.queryAllHuiZhi();
				all_reals = getRealContent(all);
				return all_reals;
			} else if (click_type == 1) {
				// 更新数据
				notarrive = hdao.queryAllHuiZhiNotArrive();
				reals = getRealContent(notarrive);
				return reals;

			} else if (click_type == 2) {
				arrive = hdao.queryAllHuiZhiArrive();
				reals = getRealContent(arrive);
				return reals;
			}

			return null;
		}

	}

	@Override
	protected void onStop() {
		Log.e("onStop", "onStop");
		if (popupWindow != null) {
			popupWindow.dismiss();
			popupWindow = null;
			iv_title_sanjiao.setBackgroundResource(R.drawable.titelbar_sanjiao);
		}

		if (pop != null) {
			pop.dismiss();
			pop = null;

			// for (DCMyViewGroupHuiZhiBaoGao p : this.items) {
			// p.setCheck("1");
			// }

		}
		sp.update("menu_open", false);
		getService(IEventRouter.class).unregisterEventListener(
				ImporDataOKEvent.class, listener);
		super.onStop();
	}

	private void showMenu() {
		showsel = true;
		View view = this.getLayoutInflater().inflate(R.layout.menu, null);
		pop = getMenu(this, view);
		pop.showAtLocation(ll_main_total, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
		view.findViewById(R.id.ll_delete).setOnClickListener(this);
		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
		gd_tv_menu_all = (TextView) view.findViewById(R.id.gd_tv_menu_all);
		gd_tv_menu_cancel = (TextView) view
				.findViewById(R.id.gd_tv_menu_cancel);
		gd_tv_menu_del = (TextView) view.findViewById(R.id.gd_tv_menu_del);
		((com.wxxr.callhelper.qg.widget.BottomTabBar) findViewById(R.id.home_bottom_tabbar))
				.setVisibility(View.GONE);
	}

	/**
	 * 单选 &全选状态
	 */
	public void pressAll() {
		gd_tv_menu_all.setTextColor(getResources().getColor(
				R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(
				R.color.gd_titlebar_text));
		gd_tv_menu_del.setTextColor(getResources().getColor(
				R.color.gd_titlebar_text));
	}

	/**
	 * 初始化&取消全选状态
	 */
	public void pressCancelAll() {
		gd_tv_menu_all.setTextColor(getResources().getColor(
				R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(
				R.color.gd_item_eighty));
		gd_tv_menu_del.setTextColor(getResources().getColor(
				R.color.gd_item_eighty));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (Tools.isPortrait(this)) {
			if (keyCode == KeyEvent.KEYCODE_MENU) {

				if (!sp.get("menu_open", false)) {

					if (pop != null && pop.isShowing()) {
						hideMenu();

						madapter.notifyDataSetChanged();
					} else {
						showMenu();

						madapter.notifyDataSetChanged();
						// for (DCMyViewGroupHuiZhiBaoGao p : this.items) {
						// p.setCheck("2");
						// }

					}
				}

			} else if (keyCode == KeyEvent.KEYCODE_BACK) {

				if (pop != null && pop.isShowing()) {
					hideMenu();
					selitem.clear();
					madapter.notifyDataSetChanged();
					return true;
				}
			}
		}

		return super.onKeyDown(keyCode, event);

	}

	private void hideMenu() {
		showsel = false;
		pop.dismiss();
		pop = null;
		selitem.clear();
		((com.wxxr.callhelper.qg.widget.BottomTabBar) findViewById(R.id.home_bottom_tabbar))
				.setVisibility(View.VISIBLE);
		// for (DCMyViewGroupHuiZhiBaoGao p : this.items) {
		// p.setCheck("1");
		// }
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		return false;
	}

	@Override
	protected void onDestroy() {
		sp.update(Constant.HUIZHI_STYLE, 0);
		unregisterReceiver(rcrReceiver);
		unregisterReceiver(sendMsgReceiver);
		if (all != null) {
			all.clear();
		}
		if (reals != null) {
			reals.clear();
		}
		madapter.clearCacheNum();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.gd_empty_lh_hz_btn:
			if(!TextUtils.isEmpty(spNumber) && !TextUtils.isEmpty(sContent)){
				intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.NOW_OPEN);
				intent.putExtra(Constant.DIALOG_PROVICE_TITLE, "短信回执");
				intent.putExtra(Constant.QG_BUSINESS_CODE, sContent);
				intent.putExtra(Constant.QG_BUSINESS_POST, spNumber);
				startActivity(intent);
			} else {
				Toast.makeText(this, "系统忙，请稍候再试！", 0).show();
			}
			break;

		case R.id.gd_iv_titlebar_left:
			finish();
			break;

		case R.id.gd_iv_titlebar_right:
			isActivation();
			break;

		case R.id.ll_pingdaodingzhi:
			if (this.isActivated()) {
				intent = new Intent();
				// intent.setClass(SmsHuiZhiMainActivity.this,PingDaoDingZhiActivity.class);
				intent.setClass(SmsHuiZhiMainActivity.this,
						ChannelCustomActivity.class);
				startActivity(intent);
			} else {
				Intent i = new Intent(SmsHuiZhiMainActivity.this,
						ConfirmDialogActivity.class);
				i.putExtra(Constant.DIALOG_KEY, Constant.DO_ACTIVE);
				startActivityForResult(i, 100);
			}
			break;

		case R.id.ll_jieshouchangjing:
			if (this.isActivated()) {
				intent = new Intent();
				// intent2.setClass(SmsHuiZhiMainActivity.this,JieShouChangJingActivity.class);
				intent.setClass(SmsHuiZhiMainActivity.this,
						ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.STAGE_MODE);
				startActivityForResult(intent, 100);
			} else {
				intent = new Intent(SmsHuiZhiMainActivity.this,
						ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.DO_ACTIVE);
				startActivityForResult(intent, 100);
			}
			break;

		case R.id.ll_all_choose:

			// for (DCMyViewGroupHuiZhiBaoGao p : this.items) {
			// p.setCheck("all");
			// }
			//
			if (click_type == 0) {

				for (BodyBeanHuiZhi item1 : all_reals) {
					selitem.put(item1.cdate, item1);
				}
			}
			madapter.notifyDataSetChanged();
			break;

		case R.id.ll_delete:
			// todo yang
			// dls = new ArrayList<DeleteVO>();
			// for (DCMyViewGroupHuiZhiBaoGao p : this.items) {
			// if (p.isChecked()) {
			// DeleteVO d = new DeleteVO();
			// d.telnumber = p.body.tosomebody;
			// d.month = p.body.amonth;
			// dls.add(d);
			// }
			// }
			if (selitem.keySet().isEmpty()) {
				return;
			}
			intent = new Intent(this, ConfirmDialogActivity.class);
			intent.putExtra(Constant.DIALOG_KEY, Constant.DELETE_RECORD);
			intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人短信回执记录？");
			startActivityForResult(intent, 100);
			// ApplicationManager am = (ApplicationManager)
			// getApplication();
			// am.dls = dls;
			// Intent intent6 = new Intent();
			// intent6.putExtra(Constant.PHONE_NUMBER, "delete_select");
			// intent6.setClass(this, DeleteHuiZhiALLDialogActivity.class);
			// startActivity(intent6);

			break;

		case R.id.ll_cancle:

			// for (DCMyViewGroupHuiZhiBaoGao p : this.items) {
			// p.setCheck("cancel");
			// }
			selitem.clear();
			madapter.notifyDataSetChanged();

			break;

		}

	}

	private void refetchData() {
		huizhiprocess.setVisibility(View.VISIBLE);
		if (firstIn && getService(ICopyOldData.class).isImportFinish()) {
			new FetchData().execute();
		} else {
			new FetchData().execute();
		}
	}

	SimpleDateFormat format_yue = new SimpleDateFormat("yyyyMM");
	private ArrayList<DeleteVO> dls;
	private Intent intent;
	private TextView gd_tv_menu_all, gd_tv_menu_cancel, gd_tv_menu_del;

	public ArrayList<BodyBeanHuiZhi> getRealContent(List<BodyBeanHuiZhi> alast) {
		ArrayList<ArrayList<BodyBeanHuiZhi>> bodyss = new ArrayList<ArrayList<BodyBeanHuiZhi>>();
		// 按照月份，记录每个人的最新一条
		Hashtable<String, ArrayList<BodyBeanHuiZhi>> timelist = new Hashtable<String, ArrayList<BodyBeanHuiZhi>>();
		// 记录每个人的最新一条记录
		Hashtable<String, String> time_person = new Hashtable<String, String>();

		ArrayList<BodyBeanHuiZhi> personlist = new ArrayList<BodyBeanHuiZhi>();

		for (BodyBeanHuiZhi item : alast) {
			// String rdate = format_yue.format(new Date(item.cdate));

			ArrayList<BodyBeanHuiZhi> list = null;
			// String key = rdate + "-" + item.address;
			String key = item.tosomebody;
			if (time_person.get(key) == null) {
				personlist.add(item);
				time_person.put(key, "");
			} else {

			}

		}

		// java.util.Collections.sort(list)

		return personlist;
	}

	class sort implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			return (int) (Long.parseLong(rhs) - Long.parseLong(lhs));
		}
	}

	private class RefreshContentReceiverHZ extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Message msg = SmsHuiZhiMainActivity.this.handler.obtainMessage();
			msg.what = 1;
			SmsHuiZhiMainActivity.this.handler.sendMessage(msg);
			madapter.clearCacheNum();
		}

	}

	public boolean isActivated() {
		boolean isActivated = getService(IUserActivationService.class)
				.isUserActivated();
		return isActivated;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case Constant.STAGE_MODE:
			int stage_mode_value = Integer.parseInt(sp.get(
					Constant.STAGE_MODE_VALUE, "2"));

			if (stage_mode_value == 2) {
				updateMode(stage_mode_value, "提醒模式");
			} else {
				updateMode(stage_mode_value, "全开模式");
			}

			break;

		case Constant.DELETE_RECORD:

			Enumeration<Long> keys = selitem.keys();
			if (keys.hasMoreElements()) {
				do {
					BodyBeanHuiZhi bean = selitem.get(keys.nextElement());
					hdao.deleteAll(bean.tosomebody, bean.address);
				} while (keys.hasMoreElements());
			}

			selitem.clear();

			refetchData();

			// for (DeleteVO vo : dls) {
			//
			// hdao.deleteSMSAccordingtoNumberAndMonth(vo.telnumber,
			// vo.month);
			// }
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void updateMode(final Integer mode, final String modeName) {
		getService(IDXHZSettingService.class).setReceivingMode(mode,
				new CMProgressMonitor(this) {

					@Override
					protected void handleFailed(Throwable cause) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showMessageBox(modeName + "设置失败，请稍后再试...");
							}
						});
					}

					@Override
					protected void handleDone(Object returnVal) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								showMessageBox("您已成功设置" + modeName);
							}
						});
					}
				});
	}

	class HuizhiItemHolder {
		TextView title;

		View huizhi_item_title;

		public CheckBox iv_checkbox;

		public TextView tv_name;

		public TextView tv_count;

		public TextView tv_date;

		public TextView tv_content;

		// public RelativeLayout ll_check;

		// public LinearLayout llclick;

		public ImageView iv_head_weidu;

		// public View huizhi_divline;

		// public View huizhi_list_item_botline;

		private ImageView gd_iv_item_portrait;
		private RelativeLayout gd_rl_item_portrait;
		private TextView gd_tv_portrait_text;

	}

	class HuizhiAdapter extends BaseAdapter {

		ArrayList<BodyBeanHuiZhi> mdata;
		int total = 0;

		public void setData(ArrayList<BodyBeanHuiZhi> pdata) {
			mdata = pdata;
			total = pdata.size();

		}

		public void clearCacheNum() {
			binddatatool.clearCache();
			total = 0;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (mdata == null) {
				return 0;
			}
			return total;
		}

		@Override
		public Object getItem(int position) {

			return mdata.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HuizhiItemHolder holder = null;
			if (convertView == null) {
				convertView = infater.inflate(R.layout.huizhi_listitem, null);
				holder = new HuizhiItemHolder();
				holder.title = (TextView) convertView
						.findViewById(R.id.sms_timetext);
				holder.huizhi_item_title = convertView
						.findViewById(R.id.huizhi_item_title);

				holder.iv_checkbox = (CheckBox) convertView
						.findViewById(R.id.dc_group_check);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.tv_head_name);
				holder.tv_count = (TextView) convertView
						.findViewById(R.id.tv_head_count);
				holder.tv_date = (TextView) convertView
						.findViewById(R.id.tv_head_date);
				holder.tv_content = (TextView) convertView
						.findViewById(R.id.tv_head_content);
				holder.iv_head_weidu = (ImageView) convertView
						.findViewById(R.id.iv_head_weidu);
				// holder.llclick = (LinearLayout) convertView
				// .findViewById(R.id.ll_click);
				// holder.ll_check = (RelativeLayout) convertView
				// .findViewById(R.id.check_relative);
				// holder.huizhi_divline = convertView
				// .findViewById(R.id.huizhi_divline);

				// holder.huizhi_list_item_botline = convertView
				// .findViewById(R.id.huizhi_list_item_botline);

				// 头像
				holder.gd_iv_item_portrait = (ImageView) convertView
						.findViewById(R.id.gd_iv_item_portrait);
				holder.gd_rl_item_portrait = (RelativeLayout) convertView
						.findViewById(R.id.gd_rl_item_portrait);
				holder.gd_tv_portrait_text = (TextView) convertView
						.findViewById(R.id.gd_tv_portrait_text);

				holder.iv_checkbox
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									pressAll();
									selitem.put(
											(Long) buttonView.getTag(),
											(BodyBeanHuiZhi) buttonView
													.getTag(R.string.huizhi_onclick_bean));
								} else {
									selitem.remove((Long) buttonView.getTag());
									if (selitem.isEmpty()) {
										pressCancelAll();
									}
								}
								// notifyDataSetChanged();

							}

						});

				convertView.setTag(holder);
			} else {

				holder = (HuizhiItemHolder) convertView.getTag();
			}

			int size = mdata.size();
			int j = 0;// 记录从2维数组，转1维的下标
			boolean needshowtitle = false;// 是否需要 显示tittle
			boolean needshowdivline = true;// 是否需要 显示分割线
			BodyBeanHuiZhi body = mdata.get(position);// 记录被选中的对象

			holder.huizhi_item_title.setVisibility(View.GONE);

			// ArrayList<BodyBeanHuiZhi> array= mdata.get(position);
			binddatatool.init(holder.iv_checkbox, holder.tv_name,
					holder.tv_count, holder.tv_date, holder.tv_content,
					/* holder.iv_head_weidu, *//*
												 * holder.ll_check,
												 * holder.llclick,
												 */
					body, click_type, showsel, selitem.containsKey(body.cdate),
					holder.gd_iv_item_portrait, holder.gd_rl_item_portrait,
					holder.gd_tv_portrait_text);

			// if (needshowdivline) {
			// holder.huizhi_divline.setVisibility(View.VISIBLE);
			// } else {
			// holder.huizhi_divline.setVisibility(View.INVISIBLE);
			// }

			// if (position == getCount() - 1) {
			//
			// holder.huizhi_list_item_botline.setVisibility(View.VISIBLE);
			//
			// } else {
			// holder.huizhi_list_item_botline.setVisibility(View.GONE);
			//
			// }

			return convertView;
		}

	}

}
