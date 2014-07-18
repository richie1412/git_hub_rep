package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.AlarmManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.CSAlarmManager;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IMessageProviderModule;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.bean.MsgType;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.gd.GDGuessLikeListActivity;
import com.wxxr.callhelper.qg.ui.gd.GDPrivateZooLoginActivity;
import com.wxxr.callhelper.qg.ui.gd.GDPushMsgActivity;
import com.wxxr.callhelper.qg.ui.gd.GDSmsHuiZhiSettingActivity;
import com.wxxr.callhelper.qg.utils.GongYiPinDao;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.HorizontalPager;
import com.wxxr.mobile.core.log.api.Trace;

public class LouHuaRealBaoGaoActivity extends BaseActivity {
	String tel_number;// 短信的对方号码（短信回执的下发端口号/漏接电话的对方号码）
	String sms_body;
	String huizhi_number;// 短信回执内容中号码
	RelativeLayout rl_louhuabaogao;
	RelativeLayout rl_huifu;
	// TextView tv_number;
	// ArrayList<View> listViews;
	ArrayList<View> listViews2;
	LayoutInflater mInflater;
	// TextView tv_count, tv_count_total;
	// MLouHuaReceiver chReceiver;
	LinearLayout ll_main;
	DisplayMetrics metrics;
	HorizontalPager hpp;
	// ViewPager mPager;
	int infor_style = 0;
	SimpleDateFormat format_month = new SimpleDateFormat("MM");
	private String huizhi_month;
	private boolean firsttime;
	private Intent intent;
	// private LinearLayout ll_push_msg_content;
	// private ImageView iv_push_call_phone;
	// private TextView tv_push_msg_title_tel;
	// private EditText et_push_send_msg;
	// private LinearLayout ll_push_msg_reply;
	private LouHuaDao ldao;
	// private RelativeLayout rl_new_push_msg_dialog, rl_push_msg_dialog;
	// private MyPageAdapter mainAdapter;
	private String sms_content_before, sms_content_after;
	// private TextView tv_titlebar;
	private String real_telphonenum = null;// 内容中的手机号，用来拨号的，

	private TextView tv_push_sms_content, tv_tel_content,
			tv_mini_info_push_date, tv_push_msg_content_title,
			tv_push_msg_content_abstrct;
	private ImageView iv_push_msg_img;
	private LinearLayout ll_push_msg_bg, gd_ll_old_dialog_bg;
	private Button gd_btn_new_dialog_reply_phone, gd_btn_new_dialog_reply_sms,
			qg_moment_reply_call;
	private String contactName;

	private static final Trace log = Trace
			.register(LouHuaRealBaoGaoActivity.class);
	private View qg_btn_middle_line;

	// private MLouHuaReceiver chReceiver;

	// private Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message msg) {
	// IntercepteDao ido = IntercepteDao
	// .getInstance(LouHuaRealBaoGaoActivity.this);
	//
	// InterceptVO ivo = ido.querySomeContent(1);
	//
	// if (ivo.secondstyle == 1) {
	// listViews.add(getContentLouHua(ivo.telnumber, ivo.icontent));
	// } else {
	// listViews.add(getContentHuiZhi(ivo.telnumber, ivo.icontent,
	// ivo.huizhi_number));
	//
	// }
	//
	// refreshData2();
	//
	// }
	//
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.push_msg_dialog);

		findView();
		// findViewById(R.id.push_msg_dialog_root).getLayoutParams().width =
		// metrics.widthPixels;
		processLogic();

	}

	private void findView() {

		// new推送对话框
		intent = getIntent();
		firsttime = firstTime();
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		ldao = LouHuaDao.getInstance(this);
		// ll_push_msg_content = (LinearLayout)
		// findViewById(R.id.ll_push_msg_content);
		// iv_push_call_phone = (ImageView)
		// findViewById(R.id.iv_push_call_phone);
		// iv_push_call_phone.setOnClickListener(this);
		// tv_push_msg_title_tel = (TextView)
		// findViewById(R.id.tv_push_msg_title_tel);
		// findViewById(R.id.rl_push_reply).setOnClickListener(this);
		// et_push_send_msg = (EditText) findViewById(R.id.et_push_send_msg);
		// ll_push_msg_reply = (LinearLayout)
		// findViewById(R.id.ll_push_msg_reply);
		// tv_titlebar = (TextView) findViewById(R.id.tv_titlebar);
		// rl_new_push_msg_dialog = (RelativeLayout)
		// findViewById(R.id.rl_new_push_msg_dialog);

		/** 全国 */
		tv_push_sms_content = (TextView) findViewById(R.id.tv_push_sms_content);
		tv_push_sms_content.setOnClickListener(this);// 上部分内容

		findViewById(R.id.iv_push_msg_close).setOnClickListener(this);

		gd_btn_new_dialog_reply_phone = (Button) findViewById(R.id.gd_btn_new_dialog_reply_phone);
		gd_btn_new_dialog_reply_phone.setOnClickListener(this);// 下部分内容
		gd_btn_new_dialog_reply_sms = (Button) findViewById(R.id.gd_btn_new_dialog_reply_sms);
		gd_btn_new_dialog_reply_sms.setOnClickListener(this);

		iv_push_msg_img = (ImageView) findViewById(R.id.iv_push_msg_img);
		tv_mini_info_push_date = (TextView) findViewById(R.id.tv_mini_info_push_date);
		tv_push_msg_content_title = (TextView) findViewById(R.id.tv_push_msg_content_title);
		tv_push_msg_content_abstrct = (TextView) findViewById(R.id.tv_push_msg_content_abstrct);

		ll_push_msg_bg = (LinearLayout) findViewById(R.id.ll_push_msg_bg);
		ll_push_msg_bg.setOnClickListener(this);// 图文中间部分内容

		gd_ll_old_dialog_bg = (LinearLayout) findViewById(R.id.gd_ll_old_dialog_bg);
		gd_ll_old_dialog_bg.setOnClickListener(this);// 普通中间部分内容

		tv_tel_content = (TextView) findViewById(R.id.tv_tel_content);

		/** 全国 */
		qg_moment_reply_call = (Button) findViewById(R.id.qg_btn_new_dialog_moment_reply_call);
		qg_moment_reply_call.setOnClickListener(this);
		qg_btn_middle_line = findViewById(R.id.qg_btn_middle_line);

		// 推送对话框
		// mPager = (ViewPager) findViewById(R.id.vPager_huizhi);
		// mInflater = (LayoutInflater)
		// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// listViews = new ArrayList<View>();

		// tv_count = (TextView) findViewById(R.id.tv_count);
		// tv_count_total = (TextView) findViewById(R.id.tv_count_total);

		// rl_push_msg_dialog = (RelativeLayout)
		// findViewById(R.id.rl_push_msg_dialog);

		// findViewById(R.id.iv_interecpte_sms_close).setOnClickListener(this);

		/** 全国 */
		// tv_tel_number = (TextView) findViewById(R.id.tv_tel_number);
		// tv_tel_number.setOnClickListener(this);
		// findViewById(R.id.gd_btn_old_dialog_reply_phone).setOnClickListener(this);
		// findViewById(R.id.gd_btn_old_dialog_reply_sms).setOnClickListener(this);

	}

	private void processLogic() {

		// 在安装应用后第一次拦截到短信提醒用户已安装本应用，以后不再提醒
		// if (firsttime) {
		// Intent intent = new Intent(this, ConfirmDialogActivity.class);
		// intent.putExtra(Constant.DIALOG_KEY, Constant.FIRST_TIME_SHOW);
		// startActivity(intent);
		// ManagerSP.getInstance().update("firsttime", 1);
		// }

		tel_number = intent.getStringExtra("inter_number");

		huizhi_number = intent.getStringExtra("huizhi_number");
		sms_body = intent.getStringExtra("inter_body");
		huizhi_month = intent.getStringExtra("infor_month");
		infor_style = intent.getIntExtra("infor_style", 0); // 1为漏接电话的，2 为短信回执
		real_telphonenum = Tools.getMisdnByContent(sms_body);// 短信的对方号码
		if (real_telphonenum == null) {
			real_telphonenum = tel_number;
		}
		Log.i("LouHuaRealBaoGaoActivity", tel_number + "---" + huizhi_month
				+ "---" + huizhi_number + "---" + infor_style + "---"
				+ sms_body);

		if (sms_body.contains("\n")) {
			int position = sms_body.indexOf("\n");
			sms_content_before = sms_body.substring(0, position);
			sms_content_after = sms_body.substring(position + 1);
			Log.i("截取内容前部分", sms_content_before);
			log.info("截取内容后部分", sms_content_after);
		} else {
			sms_content_before = sms_body;
		}

		tv_push_sms_content.setText(sms_content_before);// 上部分内容

		contactName = Tools.getContactsName(this, tel_number);

		if (contactName == null) {
			// 无联系人
			contactName = tel_number;
		}

		if (infor_style == 1) {
			// 来电提醒
			if (getService(IUserUsageDataRecorder.class) != null) {
				getService(IUserUsageDataRecorder.class).doRecord(
						ActivityID.CALLDIALOG.ordinal());
			}

			qg_moment_reply_call.setVisibility(View.VISIBLE);
			qg_btn_middle_line.setVisibility(View.GONE);
			Tools.changeTextViewBold(qg_moment_reply_call);
			ll_push_msg_bg.setVisibility(View.GONE);
			gd_btn_new_dialog_reply_sms.setText("回短信");
			// tv_titlebar.setText("中国移动通信助手来电提醒");
			// iv_push_call_phone.setVisibility(View.VISIBLE);
			// ll_push_msg_reply.setVisibility(View.VISIBLE);
			// String contactName = Tools.getContactsName(this, tel_number);
			// if (contactName != null) {
			// tv_push_msg_title_tel.setText(contactName);
			// } else {
			// tv_push_msg_title_tel.setText(tel_number);
			// }

			// tv_tel_content.setText(sms_body);

			// 推送框漏话
			// listViews.add(getContentLouHua(tel_number,
			// sms_body,real_telphonenum));
			// getContentLouHua(tel_number, sms_body,real_telphonenum);

		} else if (infor_style == 2) {
			// 短信回执
			if (getService(IUserUsageDataRecorder.class) != null) {
				getService(IUserUsageDataRecorder.class).doRecord(
						ActivityID.HUIZHIDIALOG.ordinal());
			}
			if (sms_body.contains("\n")) {
				gd_ll_old_dialog_bg.setVisibility(View.VISIBLE);
				tv_tel_content.setText(sms_content_after);
			} else {
				gd_ll_old_dialog_bg.setVisibility(View.GONE);
			}

			ll_push_msg_bg.setVisibility(View.GONE);
			gd_btn_new_dialog_reply_sms.setText("回执设置");
			// tv_titlebar.setText("中国移动通信助手短信回执");
			// iv_push_call_phone.setVisibility(View.GONE);
			// ll_push_msg_reply.setVisibility(View.GONE);
			// tv_push_msg_title_tel.setText(tel_number);
			// tv_push_sms_content.setText(sms_body);
			// String contactName = Tools.getContactsName(this, tel_number);
			// if (contactName != null) {
			// tv_tel_number.setText(contactName);
			// } else {
			// tv_tel_number.setText(tel_number);
			// }

			// TextView tv_content = (TextView) view1
			// .findViewById(R.id.tv_tel_content);
			// tv_tel_content.setText(sms_body);

			// 推送框回执
			// listViews.add(getContentHuiZhi(tel_number, sms_body,
			// huizhi_number));
			// getContentHuiZhi(tel_number, sms_body, huizhi_number);

		}

		getData();

		// 推送框viewpager
		// mainAdapter = new MyPageAdapter(listViews);
		// mPager.setAdapter(mainAdapter);
		// mPager.setOnPageChangeListener(new MyOnPageChangeListener());

		// IntentFilter testAction = new
		// IntentFilter("com.wxxr.sms.louhuamessage");
		// chReceiver = new MLouHuaReceiver();
		// registerReceiver(chReceiver, testAction);

	}

	private void getData() {

		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(final Object result) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (result != null) {
							HtmlMessageBean hMsg = (HtmlMessageBean) result;
							Log.i("LouHuaRealBaoGaoActivity", hMsg.toString());
							long time = System.currentTimeMillis();
							getService(IMessageProviderModule.class)
									.notifyMessageRead(hMsg.getMsgId(), time);
							hMsg.setReadTime(time);
							setPushMsgContent(hMsg);
							ll_push_msg_bg.setVisibility(View.VISIBLE);
							gd_ll_old_dialog_bg.setVisibility(View.GONE);
							// if (infor_style == 2) {
							// // 短信回执去广告
							// tv_push_sms_content.setText(sms_content);
							// }
						}
					}
				});

			}

			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在获取数据,请稍侯...");
				return map;
			}

		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				HtmlMessageBean hMsg = getService(IMessageProviderModule.class)
						.getNextMessage(MsgType.SSHX);
				return hMsg;
			}
		});
	}

	private void setPushMsgContent(final HtmlMessageBean hMsg) {
		Bitmap bitmap = null;
		// View view = View.inflate(this, R.layout.push_msg_content, null);
		// view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// (int) metrics.density * 300));
		// LinearLayout ll_push_msg_bg = (LinearLayout) view
		// .findViewById(R.id.ll_push_msg_bg);
		// ImageView iv_push_msg_img = (ImageView) view
		// .findViewById(R.id.iv_push_msg_img);
		// LinearLayout ll_push_msg_content_title_bg = (LinearLayout) view
		// .findViewById(R.id.ll_push_msg_content_title_bg);
		// TextView tv_push_msg_content_title = (TextView) view
		// .findViewById(R.id.tv_push_msg_content_title);
		// TextView tv_push_msg_content_abstrct = (TextView) view
		// .findViewById(R.id.tv_push_msg_content_abstrct);

		String path = hMsg.getHtmlMessage().getImage();
		if (!TextUtils.isEmpty(path)) {

			if (path.contains("file://")) {
				// 本地图片路径
				String subPath = path.substring("file://".length());
				Log.i("本地图片路径", subPath);
				bitmap = Tools.getBitmap(subPath);
			} else {
				// 网络图片路径
				Log.i("网络图片路径", path);
				bitmap = Tools.getBitmap(path);
			}

			iv_push_msg_img.setBackgroundDrawable(new BitmapDrawable(bitmap));

		} else {

			// bitmap = Tools.readBitmap(this, R.raw.push_default_img);
			iv_push_msg_img.setBackgroundResource(R.drawable.push_default_img);
		}
		String aa = Tools.longToString(hMsg.getReadTime(), "MM月dd日  HH:mm");
		tv_mini_info_push_date.setText(aa);
		tv_push_msg_content_title.setText(hMsg.getHtmlMessage().getTitle());
		String abstrct = hMsg.getHtmlMessage().getAbstrct();
		if (null != abstrct && abstrct.length() > 40) {
			abstrct = abstrct.substring(0, 40) + "【更多】";
		}
		tv_push_msg_content_abstrct.setText(abstrct);

		ll_push_msg_bg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("推送数据",
						hMsg.getHtmlMessage().getUrl() + "\n"
								+ hMsg.getRemoteURL() + "\n"
								+ hMsg.getHtmlMessage().getAbstrct() + "\n"
								+ hMsg.getHtmlMessage().getTitle() + "\n"
								+ hMsg.getHtmlMessage().getImage());
				// Intent intent = new Intent(LouHuaRealBaoGaoActivity.this,
				// ReaderPanelActivity.class);
				Intent intent = new Intent(LouHuaRealBaoGaoActivity.this,
						GDPushMsgActivity.class);
				intent.putExtra(
						"gypd",
						GongYiPinDao.getInstance(getBaseContext()).isGYBBKeys(
								"" + hMsg.getHtmlid()));
				intent.putExtra(Constant.PUSH_ISHYPERLINK, true);
				intent.putExtra(Constant.PUSH_ISHYPERLINK, true);
				intent.putExtra(Constant.HYPERLINK_ADDRESS, hMsg
						.getHtmlMessage().getUrl());// 本地地址
				intent.putExtra(Constant.PUSH_SHARE_ADDRESS,
						hMsg.getRemoteURL());// 分享地址
				intent.putExtra(Constant.PUSH_ABSTRCT, hMsg.getHtmlMessage()
						.getAbstrct());// 内容摘要
				intent.putExtra(Constant.PUSH_TITLE, hMsg.getHtmlMessage()
						.getTitle());// 内容标题
				intent.putExtra(Constant.PUSH_IMAGE, hMsg.getHtmlMessage()
						.getImage());// 图片
				startActivity(intent);

				GDRecordReport();

				finish();
			}
		});

		// ll_push_msg_content.addView(view);
	}

	/**
	 * 推送漏话框内容
	 * 
	 * @param telnumber
	 * @param content
	 * @return
	 */
	public void getContentLouHua(final String telnumber, String content,
			final String realtelnum) {

		// View view1 = mInflater.inflate(R.layout.louhua_baogao_dialog_new,
		// null);
		// TextView tv_number = (TextView)
		// view1.findViewById(R.id.tv_tel_number);
		// String contactName = Tools.getContactsName(this, telnumber);
		// if (contactName != null) {
		// tv_tel_number.setText(contactName);
		// } else {
		// tv_tel_number.setText(telnumber);
		// }
		//
		// // TextView tv_content = (TextView) view1
		// // .findViewById(R.id.tv_tel_content);
		// tv_tel_content.setText(content);

		// view1.findViewById(R.id.rl_huizhibao).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent in = new Intent();
		// in.putExtra("smsmonth", format_month.format(new Date()));
		// in.putExtra(Constant.PHONE_NUMBER, telnumber);
		// in.setClass(LouHuaRealBaoGaoActivity.this,
		// SmsContentActivity.class);
		// startActivity(in);
		// LouHuaDao.getInstance(getApplicationContext()).update(
		// tel_number, huizhi_month, 0,
		// new Date().getTime());
		// finish();
		// }
		// });
		// view1.findViewById(R.id.rl_huifu).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_CALL);
		// intent.setData(Uri.parse("tel:" + realtelnum));
		// startActivity(intent);
		// LouHuaDao.getInstance(getApplicationContext()).update(
		// tel_number, huizhi_month, 0,
		// new Date().getTime());
		// finish();
		// }
		// });
		//
		// return view1;
	}

	/**
	 * 推送回执框内容
	 * 
	 * @param telnumber
	 * @param content
	 * @param huizhi_number
	 * @return
	 */
	public void getContentHuiZhi(final String telnumber, String content,
			final String huizhi_number) {

		// View view1 = mInflater.inflate(R.layout.huizhi_intecept_dialog,
		// null);

		// TextView tv_number = (TextView)
		// view1.findViewById(R.id.tv_tel_number);
		// String contactName = Tools.getContactsName(this, telnumber);
		// if (contactName != null) {
		// tv_tel_number.setText(contactName);
		// } else {
		// tv_tel_number.setText(telnumber);
		// }
		//
		// // TextView tv_content = (TextView) view1
		// // .findViewById(R.id.tv_tel_content);
		// tv_tel_content.setText(content);

		// view1.findViewById(R.id.rl_huizhibao).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// HuiZhiBaoGaoDao.getInstance(getApplicationContext())
		// .update(huizhi_number, huizhi_month, 0);
		// Intent intent = new Intent();
		// intent.putExtra("smsarrive_style", 1);
		// intent.putExtra(Constant.PHONE_NUMBER, huizhi_number);
		// intent.setClass(LouHuaRealBaoGaoActivity.this,
		// SmsContentHuiZhiActivity2Version.class);
		// startActivity(intent);
		//
		// finish();
		// }
		// });

		// view1.findViewById(R.id.rl_huifu).setOnClickListener(
		// new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// String month = format_month.format(new Date());
		// HuiZhiBaoGaoDao.getInstance(getApplicationContext())
		// .update(huizhi_number, huizhi_month, 0);
		// Intent intent = new Intent();
		// intent.putExtra("address", tel_number);
		// intent.putExtra("tosomebody", huizhi_number);
		// intent.setClass(LouHuaRealBaoGaoActivity.this,
		// SmsContentHuiZhiSend2Activity.class);
		// startActivity(intent);
		// finish();
		// }
		// });
		//
		// return view1;
	}

	// public void refreshData2() {
	// mainAdapter.setListViews(listViews);// 重构adapter对象
	// mainAdapter.notifyDataSetChanged();//
	// int count = listViews.size();
	// tv_count_total.setText(String.valueOf(count));
	// }

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
					startActivity(new Intent(LouHuaRealBaoGaoActivity.this,
							GDSmsHuiZhiSettingActivity.class));
				} else {
					// false 未激活
					// startActivity(new Intent(LouHuaRealBaoGaoActivity.this,
					// GDPrivateZooLoginActivity.class));
					Tools.Login(LouHuaRealBaoGaoActivity.this,
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

	@Override
	protected void onDestroy() {
		// unregisterReceiver(chReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 推送消息关闭
		// case R.id.iv_interecpte_sms_close:
		// finish();
		// if (infor_style == 1) {// 这个是实时的选择现在的时间，就好了，不会跨年的
		// LouHuaDao.getInstance(getApplicationContext()).update(
		// tel_number, huizhi_month, 0, new Date().getTime());
		// } else if (infor_style == 2) {
		// HuiZhiBaoGaoDao.getInstance(getApplicationContext()).update(
		// huizhi_number, huizhi_month, 0);
		// }
		//
		// break;

			case R.id.qg_btn_new_dialog_moment_reply_call :
				// 全国 稍后回电
				Intent intent = new Intent(LouHuaRealBaoGaoActivity.this,
						DialogMenuActivity.class);
				intent.putExtra(DialogMenuActivity.ITEM_LABLE_LIST,
						new String[]{"1小时后提醒我回拨", "进入通信小秘书", "关闭"});
				startActivityForResult(intent, 1);
				break;

			case R.id.iv_push_msg_close :
				// new 推送消息关闭
				GDRecordReport();
				if (infor_style == 1) {
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.CLOSE_LOUHUA.ordinal());
					}

				} else if (infor_style == 2) {
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.CLOSE_HUIZHI.ordinal());
					}
				}
				finish();
				break;

			case R.id.tv_push_sms_content :
				// new push 上部分
				GDRecordReport();
				if (infor_style == 1) {
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.TEL_CONTENT.ordinal());
					}
					// 漏话详情页
					intent = new Intent(this, SmsContentActivity.class);
					intent.putExtra("smsmonth", format_month.format(new Date()));
					intent.putExtra(Constant.PHONE_NUMBER, real_telphonenum);
					startActivity(intent);
					finish();

				} else if (infor_style == 2) {
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.HUIZHI_CONTENT.ordinal());
					}
					// 短信详情页
					intent = new Intent(this,
							SmsContentHuiZhiActivity2Version.class);
					intent.putExtra("smsarrive_style", 1);
					intent.putExtra(Constant.PHONE_NUMBER, huizhi_number);
					intent.putExtra("smsport", tel_number);
					startActivity(intent);
					finish();
				}
				break;

			case R.id.gd_btn_new_dialog_reply_phone :
				// new push 回电话
				GDRecordReport();
				if (getService(IUserUsageDataRecorder.class) != null) {
					if (infor_style == 1) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.TEL_TEL.ordinal());
					} else if (infor_style == 2) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.HUIZHI_TEL.ordinal());
					}
				}
				intent = new Intent();
				intent.setAction(Intent.ACTION_CALL);
				intent.setData(Uri.parse("tel:" + real_telphonenum));
				startActivity(intent);
				finish();
				break;

			case R.id.gd_btn_new_dialog_reply_sms :
				// new push 回短信/回执设置
				GDRecordReport();
				if (infor_style == 1) {
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.TEL_SMS.ordinal());
					}

					// 漏话回短信
					intent = new Intent(this, SmsContentActivity.class);
					intent.putExtra("smsmonth", format_month.format(new Date()));
					intent.putExtra(Constant.PHONE_NUMBER, real_telphonenum);
					startActivity(intent);
					finish();

				} else if (infor_style == 2) {
					if (getService(IUserUsageDataRecorder.class) != null) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.HUIZHI_SET.ordinal());
					}
					// 短信回执设置
					// startActivity(new Intent(this,
					// GDSmsHuiZhiSettingActivity.class));
					isActivation();
					finish();
				}
				break;

			case R.id.gd_ll_old_dialog_bg :
				// 转到猜你喜欢列表
				GDRecordReport();
				if (getService(IUserUsageDataRecorder.class) != null) {
					if (infor_style == 1) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.TEL_PICTURE.ordinal());
					} else if (infor_style == 2) {
						getService(IUserUsageDataRecorder.class).doRecord(
								ActivityID.HUIZHI_PICTRUE.ordinal());
					}
				}
				startActivity(new Intent(this, GDGuessLikeListActivity.class));
				finish();
				break;

		// case R.id.iv_push_call_phone:
		//
		// break;

		// case R.id.rl_push_reply:
		//
		// LouHuaDao.getInstance(getApplicationContext()).update(tel_number,
		// huizhi_month, 0, new Date().getTime());
		//
		// replyMessage(tel_number);
		//
		// break;
		}

	}

	/**
	 * 漏话记载
	 */
	private void GDRecordReport() {
		if (infor_style == 1) {
			LouHuaDao.getInstance(getApplicationContext()).update(tel_number,
					huizhi_month, 0, new Date().getTime());
		} else if (infor_style == 2) {
			HuiZhiBaoGaoDao.getInstance(getApplicationContext()).update(
					huizhi_number, huizhi_month, 0);
		}

	}

	/**
	 * 回复短信
	 * 
	 * @param tel_number
	 */
	private void replyMessage(final String tel_number) {

		// final String msgContent = et_push_send_msg.getText().toString();
		//
		// if (TextUtils.isEmpty(msgContent)) {
		// showMessageBox("请输入短信内容");
		// return;
		// }

		// CMProgressMonitor monitor = new CMProgressMonitor(this) {
		//
		// @Override
		// protected void handleFailed(final Throwable cause) {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// InputMethodManager imm = (InputMethodManager)
		// getSystemService(INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(
		// et_push_send_msg.getWindowToken(), 0);
		// if (cause != null && cause instanceof LoginException) {
		// LoginException e = (LoginException) cause;
		// showMessageBox("短信发送失败" + cause.getMessage());
		//
		// } else {
		// showMessageBox("短信发送失败，请稍后再试");
		// }
		// }
		// });
		// }

		// @Override
		// protected void handleDone(Object returnVal) {
		// runOnUiThread(new Runnable() {
		// @Override
		// public void run() {
		// // todo 跳回首页
		// Uri uri = Sms.Sent.CONTENT_URI;
		// ContentValues values = new ContentValues();
		// values.put(Sms.ADDRESS, tel_number);
		// values.put(Sms.BODY, msgContent);
		// getContentResolver().insert(uri, values);
		//
		// BodyBean bb = new BodyBean();
		// bb.address = tel_number;
		// bb.content = msgContent;
		// bb.cdate = new Date().getTime();
		// bb.mstyle = 1;
		// String mmonth = format_month.format(bb.cdate);
		// bb.amonth = mmonth;
		// ldao.insert(bb);
		//
		// et_push_send_msg.setText("");
		// ll_push_msg_reply.setFocusable(true);
		// ll_push_msg_reply.setFocusableInTouchMode(true);
		// ll_push_msg_reply.requestFocus();
		// // showMessageBox("短信发送成功");
		// }
		// });
		// }

		// @Override
		// protected Map<String, Object> getDialogParams() {
		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put(DIALOG_PARAM_KEY_TITLE, "提示：");
		// map.put(DIALOG_PARAM_KEY_MESSAGE, "正在发送短信,请稍侯...");
		// return map;
		// }
		// };

		// monitor.executeOnMonitor(new Callable<Object>() {
		//
		// @Override
		// public Object call() throws Exception {
		// SmsManager mSmsManager = SmsManager.getDefault();
		// ArrayList<String> parts = mSmsManager.divideMessage(msgContent);
		// int numParts = parts.size();
		// if (numParts == 1) {
		// mSmsManager.sendTextMessage(tel_number, null, msgContent,
		// null, null);
		// } else {
		// mSmsManager.sendMultipartTextMessage(tel_number, null,
		// parts, null, null);
		// }
		// return null;
		// }
		// });
	}

	// public class MyPageAdapter extends PagerAdapter {
	// private ArrayList<View> listViews;// content
	//
	// private int size;// 页数
	//
	// public MyPageAdapter(ArrayList<View> listViews) {// 构造函数
	// // 初始化viewpager的时候给的一个页面
	// this.listViews = listViews;
	// size = listViews == null ? 0 : listViews.size();
	// }
	//
	// public void setListViews(ArrayList<View> listViews) {// 自己写的一个方法用来添加数据
	// this.listViews = listViews;
	// size = listViews == null ? 0 : listViews.size();
	// }
	//
	// @Override
	// public int getCount() {// 返回数量
	// return size;
	// }
	//
	// @Override
	// public void destroyItem(View arg0, int arg1, Object arg2) {// 销毁view对象
	// ((ViewPager) arg0).removeView(listViews.get(arg1 % size));
	// }
	//
	// @Override
	// public void finishUpdate(View arg0) {
	// }
	//
	// @Override
	// public Object instantiateItem(View arg0, int arg1) {// 返回view对象
	// try {
	// ((ViewPager) arg0).addView(listViews.get(arg1 % size), 0);
	// } catch (Exception e) {
	// Log.e("zhou", "exception：" + e.getMessage());
	// }
	// return listViews.get(arg1 % size);
	// }
	//
	// @Override
	// public boolean isViewFromObject(View arg0, Object arg1) {
	// return arg0 == arg1;
	// }
	//
	// @Override
	// public void restoreState(Parcelable arg0, ClassLoader arg1) {
	//
	// }
	//
	// @Override
	// public Parcelable saveState() {
	// return null;
	// }
	//
	// @Override
	// public void startUpdate(View arg0) {
	//
	// }
	// }
	//
	// public class MyOnPageChangeListener implements OnPageChangeListener {
	//
	// @Override
	// public void onPageSelected(int arg0) {
	// // Animation animation = null;
	//
	// int num = arg0 + 1;
	//
	// tv_count.setText(String.valueOf(num));
	//
	// }
	//
	// @Override
	// public void onPageScrolled(int arg0, float arg1, int arg2) {
	// HuiZhiBaoGaoDao.getInstance(getApplicationContext()).update(
	// tel_number, huizhi_month, 0);
	// LouHuaDao.getInstance(getApplicationContext()).update(tel_number,
	// huizhi_month, 0, new Date().getTime());
	//
	// }
	//
	// @Override
	// public void onPageScrollStateChanged(int arg0) {
	//
	// }
	// }

	// public class MLouHuaReceiver extends BroadcastReceiver {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// Message msg = LouHuaRealBaoGaoActivity.this.handler.obtainMessage();
	// LouHuaRealBaoGaoActivity.this.handler.sendMessage(msg);
	// }
	//
	// }
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int id = data.getIntExtra(DialogMenuActivity.SELETED_ITEM, -1);
			if (id == 1) {
				// 一小时后提醒
				CSAlarmManager.getInstance(this).insert(
						new ComSecretaryBean(0, real_telphonenum, System
								.currentTimeMillis() + 60 * 1000 * 60, 0, 0));
				this.finish();
			} else if (id == 2) {
				// 进入通讯小秘书
				Intent intent = new Intent(LouHuaRealBaoGaoActivity.this,
						CSMainAcitivity.class);
				startActivity(intent);
				this.finish();
			} else if (id == 3) {
				// 保留本界面
			}
		}
	};
	public boolean firstTime() {
		return ManagerSP.getInstance().get("firsttime", 0) == 0 ? true : false;
	}
}
