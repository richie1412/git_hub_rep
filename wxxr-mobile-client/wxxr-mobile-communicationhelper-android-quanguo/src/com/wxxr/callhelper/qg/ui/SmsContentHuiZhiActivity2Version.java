package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.BodyBeanHuiZhi;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.ResizeLayout;

/**
 * 点击首页中我的记录 、回执报告中的列表、及短信回执弹出框的查看 都可以进入本页 短信回执详细页面（显示该号码（回执内容中的号码） 该月 接受的短信回执
 * 和发送出去的短信（发给该号码的和发到短信回执平台） ）
 * 
 * 短信回执 终端页
 * 
 * @author zhengjincheng
 * 
 */
public class SmsContentHuiZhiActivity2Version extends BaseActivity {
	TextView tv_name;
	TextView tv_address;
	// LinearLayout ll_main;
	String sms_address;
	ListView lv_main;
	// yyyy.MM.dd HH:mm:ss
	SimpleDateFormat format_time = new SimpleDateFormat("yyyy-M-d-HH-mm");
	ArrayList<BodyBeanHuiZhi> arraymain;
	ContentAdapter madapter;
	Button sendsms;
	EditText ed_smscontent;
	HuiZhiBaoGaoDao hdao;
	DisplayMetrics metric;
	int padding;
	int arrivestyle = 0;
	String sms_month;
	long smstime;
	final static long AFTER_TIME_SHOW_TIME = 1000 * 60 * 5;
	SimpleDateFormat format_month = new SimpleDateFormat("MM");
	RelativeLayout sms_relative;
	// LinearLayout llmaincontent;
	LayoutInflater inflater;
	private int msgId = 0;
	private PopupWindow pop;
	private boolean isShowMenu;
	private boolean all_choose;
	private List<Integer> checklists = null;
	private ActivityManager am;
	private String currentClassName;
	private int click_mstyle;
	private String toportstr = "";
	final static int TO_PERSON = 0;
	final static int TO_PORT = 1;
	private int SMS_SEND_STYLE = -1;// 0 发给联系人，1 发给端口
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// llmaincontent.removeAllViews();

			if (arraymain.isEmpty()) {
				if (pop != null) {
					pop.dismiss();
					pop = null;
				}
			} else {

			}
			madapter.notifyDataSetChanged();
			mListView.setSelection(arraymain.size() == 0 ? 0
					: arraymain.size() - 1);			
		}
	};
	private ListView mListView;
	private View quick_input_pannel;
	private View empty_line;
	private TextView toPerson;
	private TextView toPort;
	protected boolean showsoftinput = false;
	private TextView gd_tv_menu_all, gd_tv_menu_cancel, gd_tv_menu_del;
	private String titlename;
	protected boolean firstFocus = true;
	private View input_area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_huizhi_content);
		mListView = (ListView) findViewById(R.id.ll_maincontent);

		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				view.setBackgroundColor(getResources().getColor(
						R.color.transparent));
			}
		});
		
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Intent intent = new Intent();
				// intent.putExtra("singleid", bb.bodyID);
				// intent.putExtra("huizhi", true);
				// intent.setClass(SmsContentHuiZhiActivity2Version.this,
				// DeleteSingleSMSDialogActivity.class);
				// startActivity(intent);
				// Log.i("SmsContentHuiZhiActivity2Version", "接收移动短信:"
				// + bb.bodyID);
				// msgId = bb.bodyID;
				// Intent intent = new Intent(
				// SmsContentHuiZhiActivity2Version.this,
				// NewConfirmDialogActivity.class);
				// intent.putExtra(Constant.DIALOG_KEY,
				// Constant.LONG_DELETE_RECORD);
				// startActivityForResult(intent, 100);
				// Intent intent = new Intent();
				// intent.putExtra("singleid", msgId);
				// intent.putExtra("huizhi", true);
				// intent.setClass(
				// SmsContentHuiZhiActivity2Version.this,
				// DeleteSingleSMSDialogActivity.class);
				Intent intent = new Intent(
						SmsContentHuiZhiActivity2Version.this,
						ComingRemindDialogActivity.class);
				intent.putExtra("singleid", arraymain.get(position).bodyID);
				intent.putExtra("content", arraymain.get(position).content);
				String tel_incontent = Tools.getMisdnByContent(arraymain
						.get(position).content);
				if (tel_incontent != null) {
					intent.putExtra("number", tel_incontent);
				} else {
					intent.putExtra("number", sms_address);
				}
				msgId = arraymain.get(position).bodyID;
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人短信回执信息？");
				view.setBackgroundColor(getResources().getColor(
						R.color.gd_callrecord_long_press));
				startActivityForResult(intent, position);
				return false;
			}

		});

		madapter = new ContentAdapter(this);

		mListView.setAdapter(madapter);
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		padding = (int) (metric.density * 10);
		hdao = HuiZhiBaoGaoDao.getInstance(this);
		arraymain = new ArrayList<BodyBeanHuiZhi>();
		inflater = LayoutInflater.from(this);
		// ll_main = (LinearLayout) findViewById(R.id.ll_mainbackground);
		// Bitmap readBitmap = Tools.readBitmap(this,
		// R.drawable.coming_remind_detail_bg);
		// BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		// ll_main.setBackgroundDrawable(bitmapDrawable);
		Intent intent = getIntent();
		arrivestyle = intent.getIntExtra("smsarrive_style", 0);
		sms_address = intent.getStringExtra(Constant.PHONE_NUMBER);
		sms_month = intent.getStringExtra("smsmonth");
		smstime = intent.getLongExtra("smstime", 0);
		toportstr = intent.getStringExtra("smsport");

		if (toportstr == null || toportstr.length() == 0) {
			toportstr = sms_address;
		}

		click_mstyle = intent.getIntExtra("mstyle", 0);
		if (sms_month == null) {
			// 从 LouHuaRealBaoGaoActivity 进入的没有传递 sms_month，
			SimpleDateFormat format_month = new SimpleDateFormat("MM");
			sms_month = format_month.format(new Date().getTime());
			smstime = new Date().getTime();
		}

		findView();

	}

	public void getData() {

		new Thread(new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				arraymain.clear();
				if (click_mstyle == 0) {
					// ArrayList<BodyBeanHuiZhi> content1 = hdao
					// .queryAllHuiZhiAccordingto_TelnumberAndMonth(
					// sms_address, sms_month,smstime);
					ArrayList<BodyBeanHuiZhi> content1 = hdao
							.queryAll(sms_address);
					arraymain.addAll(content1);

					// ArrayList<BodyBeanHuiZhi> content2 = hdao
					// .queryAllOfPortNotSend(toportstr);
					// arraymain.addAll(content2);
					// 广东版，每个回执里，都有电话号码， 就不用下边的查询条件了，
					// if (!toportstr.equals(sms_address)) {
					// ArrayList<BodyBeanHuiZhi> content3 = hdao
					// .queryPortListOfTelnum(sms_address, toportstr);
					// // int size= arraymain.size();
					// // int portsize=content3.size();
					// // for(int i=0;i<portsize;i++){
					// // boolean find=false;
					// // BodyBeanHuiZhi bean= content3.get(i);
					// //
					// //
					// // }
					// arraymain.addAll(content3);
					// }
					Collections.sort(arraymain, new SortByTime());
				}

				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);

				// if (arrivestyle == 1) {
				// ArrayList<BodyBeanHuiZhi> content1 = hdao
				// .queryAllHuiZhiAccordingto_TelnumberAndMonth(
				// sms_address, sms_month);
				// ArrayList<BodyBeanHuiZhi> content2 = hdao
				// .queryAllHuiZhiSend(sms_address);
				// arraymain.addAll(content1);
				// arraymain.addAll(content2);
				// Log.e("arraymaingetData", arraymain.size() + "");
				// Collections.sort(arraymain, new SortByTime());
				//
				// } else if (arrivestyle == 2) {
				//
				// ArrayList<BodyBeanHuiZhi> content1 = hdao
				// .queryAllHuiZhiArrive_accordingtoNumber(
				// sms_address, sms_month);
				// ArrayList<BodyBeanHuiZhi> content2 = hdao
				// .queryAllHuiZhiSend(sms_address);
				// arraymain.addAll(content1);
				// arraymain.addAll(content2);
				// Collections.sort(arraymain, new SortByTime());
				// } else if (arrivestyle == 3) {
				// ArrayList<BodyBeanHuiZhi> content1 = hdao
				// .queryAllHuiZhiNotArrive_accordingtoNumber(
				// sms_address, sms_month);
				// ArrayList<BodyBeanHuiZhi> content2 = hdao
				// .queryAllHuiZhiSend(sms_address);
				// arraymain.addAll(content1);
				// arraymain.addAll(content2);
				// Collections.sort(arraymain, new SortByTime());
				// }
				//
				// Message msg = handler.obtainMessage();
				// handler.sendMessage(msg);
				//
			}
		}).start();

	}

	class SortByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			BodyBeanHuiZhi s1 = (BodyBeanHuiZhi) o1;
			BodyBeanHuiZhi s2 = (BodyBeanHuiZhi) o2;

			Date date1 = new Date(s1.cdate);
			Date date2 = new Date(s2.cdate);

			if (date2.after(date1))
				return -1;// 由大到小
			else
				return 1;// 由小到大
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) {

		case Constant.LONG_DELETE_RECORD:
			hdao.deleteSMSHuiZhi(msgId);
			break;

		case Constant.DETAIL_MULT_DELETE_RECORD:
			for (int i = 0; i < checklists.size(); i++) {
				hdao.deleteSMSHuiZhi(checklists.get(i));
			}
			break;
		case 0:
			checklists.clear();
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		isShowMenu = false;
		all_choose = false;

		if (pop != null) {
			pop.dismiss();
			pop = null;
		}
		getData();
		input_area.setVisibility(View.VISIBLE);
		super.onResume();
	}

	private void findView() {

		View v = findViewById(R.id.root_resizediv);

		ResizeLayout root = (ResizeLayout) v;

		root.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			public void OnResize(int w, int h, int oldw, int oldh) {
				// int change = ResizeLayout.BIGGER;
				if (h - oldh < -200) {
					showsoftinput = true;
					findViewById(R.id.send_text_down).setBackgroundResource(
							R.drawable.quick_btn_pre);
				} else if (h - oldh > 200) {
					showsoftinput = false;
					// 键盘的高度应该会大于这个值， 删除一个汉字，也会小于这个值，所以变化在200之外才是键盘隐藏，
					// 否则是用键盘在删除文字，不用处理
					findViewById(R.id.send_text_down).setBackgroundResource(
							R.drawable.quick_btn);
				}

			}
		});

		findViewById(R.id.text_notuse).requestFocus();
		quick_input_pannel = findViewById(R.id.quick_input_pannel);
		empty_line = findViewById(R.id.quick_input_empty_line);

		findViewById(R.id.huizhiback).setOnClickListener(this);
		;
		findViewById(R.id.quick_sms).setOnClickListener(this);

		findViewById(R.id.titlebar_calll).setOnClickListener(this);

		toPerson = (TextView) findViewById(R.id.quick_sms_to_person);
		toPerson.setOnClickListener(this);
		toPort = (TextView) findViewById(R.id.quick_sms_to_port);
		toPort.setOnClickListener(this);
		if (toportstr.length() > 11) {
			toPort.setText(toPort.getText().toString()
					.replace("port", toportstr.substring(0, 11) + "..."));
		} else {
			toPort.setText(toPort.getText().toString()
					.replace("port", toportstr));
		}

		findViewById(R.id.quick_cancel).setOnClickListener(this);
		findViewById(R.id.send_text_down).setOnClickListener(this);
		findViewById(R.id.send_text).setOnClickListener(this);

		ed_smscontent = (EditText) findViewById(R.id.input_edittext);

		input_area = findViewById(R.id.input_area);
		ed_smscontent.setInputType(InputType.TYPE_NULL);
		ed_smscontent.setOnClickListener(this);
		ed_smscontent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && firstFocus) {
					firstFocus = false;
					porcessOnClick(R.id.send_text_down);
				}

			}

		});

		TextView tv_title = (TextView) findViewById(R.id.tv_titlename);

		titlename = Tools.getContactsName(this, sms_address);
		if (titlename != null) {
			tv_title.setText(titlename);
			toPerson.setText(toPerson.getText().toString()
					.replace("name", titlename));
		} else {
			tv_title.setText(sms_address);
			toPerson.setText(toPerson.getText().toString()
					.replace("name", sms_address));
		}
		toPerson.requestFocus();

		TextView tv_place = (TextView) findViewById(R.id.tv_place);

		Region r = getService(IGuiShuDiService.class).getRegionByDialogNumber(
				sms_address);
		if (r != null) {

			StringBuilder sb = new StringBuilder();
			if (r.getpRegionName() != null) {
				sb.append(r.getpRegionName() + " ");
			}
			if (r.getRegionName() != null) {
				sb.append(r.getRegionName() + " ");
			}
			// if (r.getBrandName() != null) {
			// sb.append(r.getBrandName());
			// }
			tv_place.setText(sb.toString());
		} else {
			tv_place.setText("未知");
		}

		checklists = new ArrayList<Integer>();

		// sendsms = (Button) findViewById(R.id.bt_sms_send);
		// sendsms.setOnClickListener(this);
		// ed_smscontent = (EditText) findViewById(R.id.bt_sms_content);
		// sms_relative = (RelativeLayout) findViewById(R.id.bt_sms_relative);
		//
		// tv_name = (TextView) findViewById(R.id.tv_client_name);
		// String sms_name = Tools.getContactsName(this, sms_address);
		// if (sms_name != null)
		// {
		// tv_name.setText(sms_name);
		// tv_name.setVisibility(View.VISIBLE);
		// }
		// else
		// {
		// tv_name.setVisibility(View.GONE);
		// }
		//
		// tv_address = (TextView) findViewById(R.id.tv_client_address);
		// tv_address.setText(sms_address);
		// Tools.changeTextViewBold(tv_name);

		// lv_main = (ListView) findViewById(R.id.lv_sms_maincontentt);
		//
		// madapter = new ContentAdapter(this);
		//
		// lv_main.setAdapter(madapter);
		// lv_main.setOnItemLongClickListener(new OnItemLongClickListener()
		// {
		//
		// @Override
		// public boolean onItemLongClick(AdapterView<?> parent, View view, int
		// position, long id)
		// {
		// Intent intent = new Intent();
		// intent.putExtra("singleid", arraymain.get(position).bodyID);
		// intent.putExtra("huizhi", true);
		// intent.setClass(SmsContentHuiZhiActivity2Version.this,
		// DeleteSingleSMSDialogActivity.class);
		// startActivityForResult(intent, 200);
		//
		// return false;
		// }
		// });

	}

	private void setMenu() {
		View view = View.inflate(this, R.layout.menu, null);
		pop = getMenu(this, view);
		pop.showAtLocation(findViewById(R.id.rl_menu), Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
		view.findViewById(R.id.ll_delete).setOnClickListener(this);
		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
		gd_tv_menu_all = (TextView) view.findViewById(R.id.gd_tv_menu_all);
		gd_tv_menu_cancel = (TextView) view
				.findViewById(R.id.gd_tv_menu_cancel);
		gd_tv_menu_del = (TextView) view.findViewById(R.id.gd_tv_menu_del);
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

	private void showMenu() {
		isShowMenu = true;
		setMenu();
		refresh();
		if (showsoftinput) {
			Tools.toggleSoftInput(this, ed_smscontent.getWindowToken());
		}
		input_area.setVisibility(View.INVISIBLE);
	}

	private void hideMenu() {
		isShowMenu = false;
		all_choose = false;
		pop.dismiss();
		pop = null;
		refresh();
		input_area.setVisibility(View.VISIBLE);
	}

	private void refresh() {
		Message message = handler.obtainMessage();
		handler.sendMessage(message);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (arraymain != null && !arraymain.isEmpty()) {
				if (pop == null) {
					if ((!showsoftinput)
							&& (quick_input_pannel.getVisibility() == View.GONE)) {
						showMenu();
					}
				} else {
					hideMenu();
				}
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (pop != null && pop.isShowing()) {
				hideMenu();
				return true;
			} else if (quick_input_pannel.getVisibility() == View.VISIBLE) {
				porcessOnClick(R.id.quick_cancel);
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {

		porcessOnClick(v.getId());

	}

	private void porcessOnClick(int id) {
		switch (id) {

		case R.id.titlebar_calll:
			Intent tel = new Intent();
			tel.setAction(Intent.ACTION_CALL);
			tel.setData(Uri.parse("tel:" + sms_address));
			startActivity(tel);

			break;

		case R.id.input_edittext:
			if (ed_smscontent.getInputType() == InputType.TYPE_NULL) {
				porcessOnClick(R.id.send_text_down);
			}
			break;

		case R.id.huizhiback:
			finish();
			break;
		case R.id.ll_delete:
			if (checklists != null && !checklists.isEmpty()) {
				Intent intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY,
						Constant.DETAIL_MULT_DELETE_RECORD);
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人短信回执信息？");
				startActivityForResult(intent, 102);
			}
			break;

		case R.id.ll_cancle:
			all_choose = false;
			checklists.clear();
			refresh();
			break;

		case R.id.ll_all_choose:
			all_choose = true;
			for (BodyBeanHuiZhi bean : arraymain) {
				checklists.add(bean.bodyID);
			}

			refresh();
			break;
		// findViewById(R.id.quick_sms).setOnClickListener(this);
		// findViewById(R.id.quick_sms_to_person).setOnClickListener(this);
		// findViewById(R.id.quick_sms_to_port).setOnClickListener(this);
		// findViewById(R.id.quick_cancel).setOnClickListener(this);
		// findViewById(R.id.send_text_down).setOnClickListener(this);
		case R.id.quick_sms:

			break;

		case R.id.quick_sms_to_person:
			SMS_SEND_STYLE = TO_PERSON;
			ed_smscontent.setInputType(InputType.TYPE_CLASS_TEXT);
			if (titlename != null) {
				ed_smscontent.setHint("TO:" + titlename);
			} else {
				ed_smscontent.setHint("TO:" + sms_address);
			}

			findViewById(R.id.send_text_down).setBackgroundResource(
					R.drawable.quick_btn);
			quick_input_pannel.setVisibility(View.GONE);
			empty_line.setVisibility(View.GONE);
			Tools.toggleSoftInput(this, ed_smscontent.getWindowToken());
			break;

		case R.id.quick_sms_to_port:
			SMS_SEND_STYLE = TO_PORT;
			ed_smscontent.setInputType(InputType.TYPE_CLASS_TEXT);
			if (toportstr != null) {
				if (toportstr.length() > 11) {
					ed_smscontent.setHint("TO:" + toportstr.substring(0, 11)
							+ "...");
				} else {
					ed_smscontent.setHint("TO:" + toportstr);
				}

			} else {
				ed_smscontent.setHint("TO:" + sms_address);
			}
			findViewById(R.id.send_text_down).setBackgroundResource(
					R.drawable.quick_btn);
			quick_input_pannel.setVisibility(View.GONE);
			empty_line.setVisibility(View.GONE);
			Tools.toggleSoftInput(this, ed_smscontent.getWindowToken());

			break;

		case R.id.quick_cancel:
			if (SMS_SEND_STYLE != -1) {
				ed_smscontent.setInputType(InputType.TYPE_CLASS_TEXT);
			}
			findViewById(R.id.send_text_down).setBackgroundResource(
					R.drawable.quick_btn);
			quick_input_pannel.setVisibility(View.GONE);
			empty_line.setVisibility(View.GONE);
			break;

		case R.id.send_text_down:
			if (!showsoftinput) {
				ed_smscontent.setInputType(InputType.TYPE_NULL);
				findViewById(R.id.send_text_down).setBackgroundResource(
						R.drawable.quick_btn_preup);
				empty_line.setVisibility(View.VISIBLE);
				quick_input_pannel.setVisibility(View.VISIBLE);
			}
			break;

		// case R.id.iv_callbody:
		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_CALL);
		// intent.setData(Uri.parse("tel:" + sms_address));
		// startActivity(intent);

		// break;
		case R.id.send_text:

			String msgContent = ed_smscontent.getText().toString();

			if ("".equals(msgContent) || msgContent == null) {
				Toast.makeText(this, "请输入短信内容", Toast.LENGTH_SHORT).show();
				return;
			}

			SmsManager mSmsManager = SmsManager.getDefault();
			ArrayList<String> messages = mSmsManager.divideMessage(msgContent);
			ContentValues values = new ContentValues();
			String temptoaddress = sms_address;
			if (SMS_SEND_STYLE == TO_PERSON) {
				temptoaddress = sms_address;
			} else {
				temptoaddress = toportstr;
			}
			for (String message : messages) {
				mSmsManager.sendTextMessage(temptoaddress, null, message, null,
						null);
				Uri uri = Sms.Sent.CONTENT_URI;
				values.put(Sms.ADDRESS, temptoaddress);
				values.put(Sms.BODY, message);
				getContentResolver().insert(uri, values);
			}
			
			BodyBeanHuiZhi bb = new BodyBeanHuiZhi();
			bb.address = temptoaddress;
			// 当有多个地方给 端口发送短信的时候，区别是在哪个手机号发出去的，
			if (SMS_SEND_STYLE == TO_PORT) {
				bb.tosomebody = sms_address;
			} else {
				bb.tosomebody = sms_address;
			}

			bb.content = msgContent;
			bb.cdate = new Date().getTime();
			bb.mstyle = 3;
			String mmonth = format_month.format(bb.cdate);
			bb.amonth = mmonth;
			hdao.insert(bb);
			
			getData();
			ed_smscontent.setText("");
			// sms_relative.setFocusable(true);
			// sms_relative.setFocusableInTouchMode(true);
			// sms_relative.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(ed_smscontent.getWindowToken(), 0);
			// showMessageBox("短信发送成功");

			break;

		}

	}

	public class ContentAdapter extends BaseAdapter {
		TextView tv_time;
		TextView tv_content;
		private LayoutInflater inflater;
		Context context;

		OnCheckedChangeListener check = new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					pressAll();
					checklists.add((Integer) buttonView.getTag());
				} else {
					checklists.remove(buttonView.getTag());
					if (checklists.isEmpty()) {
						pressCancelAll();
					}
				}

			}

		};

		public ContentAdapter(Context context) {
			super();
			inflater = LayoutInflater.from(context);

			this.context = context;

		}

		@Override
		public int getCount() {
			if (arraymain == null) {
				return 0;
			} else {
				return arraymain.size();
			}

		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// //短信类型 收 发 0:已送达 1：未送达 3：发送出去的短信
			return arraymain.get(position).mstyle / 2;
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			// ViewHolder1 holderright=null;
			int type = getItemViewType(position);
			if (convertView == null) {

				holder = new ViewHolder();
				if (type == 0) {

					convertView = inflater.inflate(
							R.layout.sms_detail_item_left, null);
					holder.view=convertView;
					holder.cb_check = (CheckBox) convertView
							.findViewById(R.id.cb_leak_detail_left);
					holder.tv_time = (TextView) convertView
							.findViewById(R.id.tv_detail_time_left);
					holder.tv_content = (TextView) convertView
							.findViewById(R.id.tv_detail_content_left);
					holder.emptyline = convertView
							.findViewById(R.id.empty_line_left);
					convertView.setTag(holder);

					holder.cb_check.setOnCheckedChangeListener(check);

				} else {

					convertView = inflater.inflate(
							R.layout.sms_detail_item_right, null);
					holder.view=convertView;
					holder.cb_check = (CheckBox) convertView
							.findViewById(R.id.cb_leak_detail_right);
					holder.tv_time = (TextView) convertView
							.findViewById(R.id.tv_detail_time_right);
					holder.tv_content = (TextView) convertView
							.findViewById(R.id.tv_detail_content_right);
					holder.emptyline = convertView
							.findViewById(R.id.empty_line_right);
					convertView.setTag(holder);

					holder.cb_check.setOnCheckedChangeListener(check);

				}

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.view.setBackgroundColor(context.getResources().getColor(R.color.transparent));
			
			BodyBeanHuiZhi bean = arraymain.get(position);

			// int style =bean.mstyle;

			String content = bean.content;
			String realtel = Tools.getMisdnByContent(content);

			String name = null;
			if (realtel != null) {
				name = Tools.getContactsName(context, realtel);
			}

			if (name != null) {
				content = content
						.replace("(" + realtel + ")", "(" + name + ")");
			}

			if (type == 0) {
				holder.tv_content.setText("From:" + bean.address + "\n"
						+ content);
			} else {//1 是发送出去的，使用  bean.address ，就可以了
				String toname = Tools.getContactsName(context, bean.address);
				if (toname != null) {
					holder.tv_content.setText("To:" + toname + "\n" + content);
				} else {
					holder.tv_content.setText("To:" + bean.address + "\n"
							+ content);
				}

			}

			if (position > 0) {
				if (bean.cdate - arraymain.get(position - 1).cdate > AFTER_TIME_SHOW_TIME) {
					String realTime = Tools.getChatTime(
							format_time.format(new Date()),
							format_time.format(new Date(bean.cdate)));
					holder.tv_time.setVisibility(View.VISIBLE);
					holder.tv_time.setText(realTime);
					holder.emptyline.setVisibility(View.GONE);
				} else {
					holder.emptyline.setVisibility(View.VISIBLE);
					holder.tv_time.setVisibility(View.GONE);
				}
			} else {
				String realTime = Tools.getChatTime(
						format_time.format(new Date()),
						format_time.format(new Date(bean.cdate)));
				holder.emptyline.setVisibility(View.GONE);
				holder.tv_time.setVisibility(View.VISIBLE);
				holder.tv_time.setText(realTime);
			}

			if (isShowMenu) {
				holder.cb_check.setVisibility(View.VISIBLE);
				holder.cb_check.setTag(bean.bodyID);
				holder.cb_check.setChecked(checklists.contains(bean.bodyID));
			} else {
				holder.cb_check.setVisibility(View.GONE);
			}
			return convertView;

		}

		class ViewHolder {
			public View view;
			TextView tv_time;
			TextView tv_content;
			CheckBox cb_check;
			View emptyline;
		}

	}

}
