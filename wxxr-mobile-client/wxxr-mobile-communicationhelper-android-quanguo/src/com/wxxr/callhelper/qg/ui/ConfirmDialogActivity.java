package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.platformtools.Log;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.MsgType;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.exception.AppException;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.ClientInfo;
import com.wxxr.callhelper.qg.rpc.DXHZSetting;
import com.wxxr.callhelper.qg.service.DownAPK;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.ui.gd.GDGuessLikeListActivity;
import com.wxxr.callhelper.qg.ui.gd.GDPrivateZooLoginActivity;
import com.wxxr.callhelper.qg.ui.gd.GDSmsHuiZhiSettingActivity;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.StringUtils;

/**
 * 全国版
 * 
 * @author yinzhen
 * 
 */

public class ConfirmDialogActivity extends BaseActivity {
	private int mHour;
	private int mMinute;
	private Intent mintent;
	private int number;
	private String d2;
	private TextView tv_first_row, tv_second_row, tv_dialog_top,
			tv_dialog_content;
	private LinearLayout ll_confirm1, /* ll_confirm2, */ll_confirm3, ll_confirm4,
			ll_confirm5, ll_btn;
	private Button btn_dialog_cancel, btn_dialog_ok;
	private boolean lMRFlag = true;
	private ImageView iv_sm_switch;
	private int setting, stage_mode_value, callRecorder_switch;
	private ImageView iv_hand, iv_auto, iv_open_close, iv_remind_mode,
			iv_all_mode, iv_private_switch;
	private ManagerSP sp;
	private String sms_month, telnumber, smLockPosition,smsport;
	private long smstime;
	private int singleid;
	private HuiZhiBaoGaoDao hdao;
	private LinearLayout ll_order_prompt;
	private Button btn_remaining_sum;
	private Button btn_order_business;
	private Button btn_gprs_remaining_sum;
	private Button btn_sms_remaining_sum;
	private LinearLayout ll_confirm7;
	private Button btn_exit;
	private LinearLayout ll_confirm8;
	private Button btn_male;
	private Button btn_female;
	private LinearLayout ll_confirm9;
	private TextView tv_seletct;
	private Button btn_cancel;
	private LinearLayout ll_btn_bottom;
	private View vi_above_exit;
	private LinearLayout ll_top;
	private RelativeLayout rl_confirm10;
	private TextView tv_birthday;
	private View vi_tanchukuang;
	private LinearLayout ll_service_phone;

	private View ll_btn_confim_menuitems;
	private TextView ll_btn_confim_menuitem_1, ll_btn_confim_menuitem_2,
			ll_btn_confim_menuitem_3;
	private CheckBox gd_cb_dialog_inserttosys;
//	private LinearLayout gd_ll_dialog_inserttosys_div;
	private View tv_dialog_progressBardiv;
	private ProgressBar tv_dialog_progressBar;

	private LinearLayout gd_ll_dialog_content;

	private ImageView gd_iv_titlebar_icon, gd_iv_dialog_close;

	private String sendMsgContent, sendMsgPost;
	private DisplayMetrics metric;
	private static final Trace log = Trace
			.register(ConfirmDialogActivity.class);

	Handler processhanler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			int proce = (Integer) msg.obj;
			tv_dialog_progressBar.setProgress(proce);
			Message msg0 = processhanler.obtainMessage();
			msg0.obj = new Integer(proce + 10);
			processhanler.sendMessageDelayed(msg0, 100);
		};

	};
	private String importnum;
	private String ispush;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.confirm_dialog);
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		hdao = HuiZhiBaoGaoDao.getInstance(this);
		findView();
		processLogic();
	}

	private void findView() {
		sp = ManagerSP.getInstance(this);
		mintent = getIntent();
		vi_tanchukuang = (View) findViewById(R.id.vi_private_zoo_tanchukuang_line);
		btn_dialog_cancel = (Button) findViewById(R.id.btn_dialog_cancel);
		btn_dialog_cancel.setOnClickListener(this);
		btn_dialog_ok = (Button) findViewById(R.id.btn_dialog_ok);
		btn_dialog_ok.setOnClickListener(this);
		tv_dialog_top = (TextView) findViewById(R.id.tv_dialog_top);
		ll_top = (LinearLayout) findViewById(R.id.ll_dialog_top);
		ll_btn_bottom = (LinearLayout) findViewById(R.id.ll_btn_bottom);
		// 文本删除
		gd_ll_dialog_content = (LinearLayout) findViewById(R.id.gd_ll_dialog_content);
		tv_dialog_content = (TextView) findViewById(R.id.tv_dialog_content);
		// 文本两行(办理业务)
		ll_confirm1 = (LinearLayout) findViewById(R.id.ll_confirm1);
		tv_first_row = (TextView) findViewById(R.id.tv_first_row);
		tv_second_row = (TextView) findViewById(R.id.tv_second_row);
		gd_iv_titlebar_icon = (ImageView) findViewById(R.id.gd_iv_titlebar_icon);
		gd_iv_dialog_close = (ImageView) findViewById(R.id.gd_iv_dialog_close);
		// 文本特别提示
		// ll_confirm2 = (LinearLayout) findViewById(R.id.ll_confirm2);
		// 私密锁设置
		ll_confirm3 = (LinearLayout) findViewById(R.id.ll_confirm3);
		iv_private_switch = (ImageView) findViewById(R.id.iv_private_switch);
		iv_private_switch.setOnClickListener(this);
		findViewById(R.id.tv_private_lock_modi).setOnClickListener(this);
		// 录音设置
		ll_confirm4 = (LinearLayout) findViewById(R.id.ll_confirm4);
		iv_hand = (ImageView) findViewById(R.id.iv_hand);
		iv_auto = (ImageView) findViewById(R.id.iv_auto);
		iv_open_close = (ImageView) findViewById(R.id.iv_open_close);

		findViewById(R.id.ll_auto).setOnClickListener(this);
		findViewById(R.id.ll_hand).setOnClickListener(this);
		findViewById(R.id.iv_open_close).setOnClickListener(this);
		// 客服电话
		ll_service_phone = (LinearLayout) findViewById(R.id.ll_service_phone);
		findViewById(R.id.btn_service_phone).setOnClickListener(this);
		findViewById(R.id.btn_service_app).setOnClickListener(this);
		// 场景设置
		ll_confirm5 = (LinearLayout) findViewById(R.id.ll_confirm5);
		iv_remind_mode = (ImageView) findViewById(R.id.iv_remind_mode);
		iv_all_mode = (ImageView) findViewById(R.id.iv_all_mode);
		// 注册成功提示语
		ll_order_prompt = (LinearLayout) findViewById(R.id.ll_order_prompt);
		findViewById(R.id.ll_remind_mode).setOnClickListener(this);
		findViewById(R.id.ll_all_mode).setOnClickListener(this);
		// 移动业务查询
		ll_confirm7 = (LinearLayout) findViewById(R.id.ll_confirm7);
		btn_remaining_sum = (Button) findViewById(R.id.btn_private_zoo_remaining_sum);
		btn_order_business = (Button) findViewById(R.id.btn_private_zoo_order_business);
		btn_gprs_remaining_sum = (Button) findViewById(R.id.btn_private_zoo_gprs_remaining_sum);
		btn_sms_remaining_sum = (Button) findViewById(R.id.btn_private_zoo_sms_remaining_sum);
		btn_exit = (Button) findViewById(R.id.btn_dialog_exit);
		vi_above_exit = (View) findViewById(R.id.vi_private_zoo_above_exit);
		btn_gprs_remaining_sum.setOnClickListener(this);
		btn_order_business.setOnClickListener(this);
		btn_sms_remaining_sum.setOnClickListener(this);
		btn_remaining_sum.setOnClickListener(this);
		btn_exit.setOnClickListener(this);
		// 性别选择
		ll_confirm8 = (LinearLayout) findViewById(R.id.ll_confirm8);
		btn_male = (Button) findViewById(R.id.btn_private_zoo_gender_male);
		btn_female = (Button) findViewById(R.id.btn_private_zoo_gender_female);
		btn_male.setOnClickListener(this);
		btn_female.setOnClickListener(this);
		// 头像选择
		ll_confirm9 = (LinearLayout) findViewById(R.id.ll_confirm9);
		tv_seletct = (TextView) findViewById(R.id.tv_private_zoo_icon_seletct);
		btn_cancel = (Button) findViewById(R.id.btn_private_zoo_icon_cancel);
		tv_seletct.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		// 年龄选择
		rl_confirm10 = (RelativeLayout) findViewById(R.id.rl_confirm10);
		tv_birthday = (TextView) findViewById(R.id.tv_private_zoo_text_input_birthday);
		tv_birthday.setOnClickListener(this);
		// 删除单挑聊天记录，

		ll_btn_confim_menuitems = findViewById(R.id.ll_btn_confim_menuitems);
		ll_btn_confim_menuitem_1 = (TextView) findViewById(R.id.ll_btn_confim_menuitem_1);
		ll_btn_confim_menuitem_2 = (TextView) findViewById(R.id.ll_btn_confim_menuitem_2);
		ll_btn_confim_menuitem_3 = (TextView) findViewById(R.id.ll_btn_confim_menuitem_3);

		ll_btn_confim_menuitem_1.setOnClickListener(this);
		ll_btn_confim_menuitem_2.setOnClickListener(this);
		ll_btn_confim_menuitem_3.setOnClickListener(this);

		// 解锁私密联系人
//		gd_ll_dialog_inserttosys_div = (LinearLayout)findViewById(R.id.gd_ll_dialog_inserttosys_div);
		gd_tv_contacts_unlock = (TextView) findViewById(R.id.gd_tv_contacts_unlock);
		gd_cb_dialog_inserttosys = (CheckBox) findViewById(R.id.gd_cb_dialog_inserttosys);
		gd_private_contacts_unlock = (LinearLayout) findViewById(R.id.gd_private_contacts_unlock);

		// tv_dialog_progressBardiv=findViewById(R.id.tv_dialog_progressBardiv);
		// tv_dialog_progressBar=(ProgressBar)findViewById(R.id.confirmdialog_progressBar1);

	}

	private void processLogic() {
		number = mintent.getIntExtra(Constant.DIALOG_KEY, 0);
		content = mintent.getStringExtra(Constant.DIALOG_CONTENT);
		ispush=mintent.getStringExtra(Constant.PUSH_IN);
		
		if (ispush!=null&&AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
			AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.LOOKSYSNOTICE.ordinal());
		}
		
		tv_dialog_content.setText(content);

		switch (number) {

		case Constant.DELETE_SINGLE_ITEM:
			tv_dialog_content.setText("是否确认删除该条充值提醒短信?");
			break;
		case Constant.CONGZHI_MENU_DELETE:
			tv_dialog_content.setText("是否确认删除充值提醒短信?");
			break;
		case Constant.PWD_EDIT_SUCCESS:
			// 密码修改成功
			ll_btn_bottom.setVisibility(View.GONE);
			btn_exit.setVisibility(View.VISIBLE);
			vi_above_exit.setVisibility(View.VISIBLE);
			btn_exit.setText("我知道了");
			break;
		// case Constant.FIRST_TIME_SHOW :
		// tv_dialog_top.setText("温馨提示");
		// tv_dialog_content.setText(this.getResources().getString(
		// R.string.chinamobile_6));
		// btn_dialog_ok.setText("设备管理器");
		// break;
		case Constant.DO_ACTIVE:
			tv_dialog_content.setText("客户端未登录，是否去登录账号？");
			break;
		case Constant.MOBILE_QUERY:
			gd_ll_dialog_content.setVisibility(View.GONE);
			ll_confirm7.setVisibility(View.VISIBLE);
			ll_btn_bottom.setVisibility(View.GONE);
			tv_dialog_top.setText("移动业务查询");
			btn_exit.setVisibility(View.VISIBLE);
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.COUNT_QUERY:
			tv_dialog_top.setText("账户余额");
			tv_dialog_content.setText("我们将帮您代发查询短信口令至10086，确定后请注意查收短信结果。");
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.ORDER_BUSINESS:
			tv_dialog_top.setText("订购数据业务");
			tv_dialog_content.setText("我们将帮您代发查询短信口令至10086，确定后请注意查收短信结果。");
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.GPRS_REMANNING:
			tv_dialog_top.setText("GPRS月套餐数据流量");
			tv_dialog_content.setText("我们将帮您代发查询短信口令至10086，确定后请注意查收短信结果。");
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.CALL_QUERY:
			btn_dialog_ok.setText("呼 叫");
			tv_dialog_top.setText("查询套餐剩余短信");
			tv_dialog_content.setText("是否呼叫1008611?");
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.GENDER:
			boolean isMale = mintent.getBooleanExtra("gender", true);
			if (isMale) {
				btn_male.setBackgroundResource(R.drawable.private_zoo_gender_press);
			} else {
				btn_female
						.setBackgroundResource(R.drawable.private_zoo_gender_press);
			}
			tv_dialog_top.setText("性 别");
			ll_btn_bottom.setVisibility(View.GONE);
			gd_ll_dialog_content.setVisibility(View.GONE);
			ll_confirm8.setVisibility(View.VISIBLE);
			btn_exit.setVisibility(View.VISIBLE);
			btn_exit.setTextColor(Color.parseColor("#bdbdc5"));
			btn_exit.setText("关  闭");
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.ICON:
			tv_dialog_top.setText("选取方式");
			ll_btn_bottom.setVisibility(View.GONE);
			gd_ll_dialog_content.setVisibility(View.GONE);
			ll_confirm9.setVisibility(View.VISIBLE);
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.AGE:
			rl_confirm10.setVisibility(View.VISIBLE);
			gd_ll_dialog_content.setVisibility(View.GONE);
			tv_dialog_top.setText("个人信息");
			int birday = mintent.getIntExtra("birday", 0);
			if (birday != 0) {
				String str_birday = String.valueOf(birday);
				StringBuilder sb = new StringBuilder();
				sb.append(str_birday.substring(0, 4)).append("-")
						.append(str_birday.substring(4, 6)).append("-")
						.append(str_birday.substring(6, 8));
				tv_birthday.setText(sb.toString());
			}
			btn_dialog_ok.setTextColor(Color.parseColor("#bdbdc5"));
			btn_dialog_ok.setClickable(false);
			vi_tanchukuang.setVisibility(View.VISIBLE);
			break;
		case Constant.DE_ACTIVE:
			tv_dialog_content.setText("是否确定退出当前账号？");
			break;
		case Constant.SCUCESS_REGISTER:
			tv_dialog_top.setText("注册成功");
			btn_dialog_ok.setText("已订购");
			btn_dialog_cancel.setText("未订购");
			gd_ll_dialog_content.setVisibility(View.GONE);
			ll_order_prompt.setVisibility(View.VISIBLE);
			break;
		case Constant.SERVICE_PHONE:
			ll_service_phone.setVisibility(View.VISIBLE);
			gd_ll_dialog_content.setVisibility(View.GONE);
			ll_btn_bottom.setVisibility(View.GONE);
			tv_dialog_top.setText("客服电话");
			break;

		case Constant.NOW_OPEN:
			String proviceName = Tools.getProviceName();
			String businessType = mintent.getStringExtra(Constant.DIALOG_PROVICE_TITLE);
			sendMsgContent = mintent.getStringExtra(Constant.QG_BUSINESS_CODE);
			sendMsgPost = mintent.getStringExtra(Constant.QG_BUSINESS_POST);
			
			if(Constant.imgChangedProviceLists.contains(proviceName)){
				tv_dialog_top.setText("办理通信助手");
			}else{
				tv_dialog_top.setText("办理" + businessType);
			}
			gd_ll_dialog_content.setVisibility(View.GONE);
			gd_iv_titlebar_icon
					.setBackgroundResource(R.drawable.gd_dialog_open_now_icon);
			gd_iv_dialog_close.setVisibility(View.VISIBLE);
			gd_iv_dialog_close.setOnClickListener(this);
			ll_confirm1.setVisibility(View.VISIBLE);
			String text = "将协助您发送免费短信" + sendMsgContent + "到"+ sendMsgPost +"。需要您查收并回复" + sendMsgPost + "发送到您手机的确认短信，才能成功办理。";
			tv_first_row.setText(Tools.ToDBC(text));
			tv_second_row.setText("本操作只对" + proviceName + "移动客户有效");
//			getKTCommand();
			break;

		// case Constant.SPECIAL_PROMPT :
		// tv_dialog_top.setText("特别提示");
		// tv_dialog_content.setText("您已成功添加私密联系人，密码锁当前已生效。若今后忘记密码，输入您添加过的任意私密联系人号码即可解锁。");
		// ll_confirm2.setVisibility(View.VISIBLE);
		// gd_ll_dialog_content.setVisibility(View.GONE);
		// break;

		case Constant.SM_LOCK_SETTING_DIALOG:
			// 私密锁设置
			tv_dialog_top.setText("设 置");
			ll_confirm3.setVisibility(View.VISIBLE);
			gd_ll_dialog_content.setVisibility(View.GONE);
			this.lMRFlag = getService(IPrivateSMService.class)
					.isRingBellWhenReceiving() == false;
			if (this.lMRFlag) {
				iv_private_switch.setBackgroundResource(R.drawable.close);
			} else {
				iv_private_switch.setBackgroundResource(R.drawable.open);
			}
			break;

		case Constant.SM_LOCK_OPEN:
			// 解锁私密联系人
			// jieSuoSimi();
			gd_iv_titlebar_icon
					.setBackgroundResource(R.drawable.gd_dialog_private_contacts_icon);
			tv_dialog_top.setText("解锁私密联系人");
			gd_ll_dialog_content.setVisibility(View.GONE);
			gd_private_contacts_unlock.setVisibility(View.VISIBLE);

			break;

		case Constant.CALLRECORDER_SETTING_DIALOG:

			tv_dialog_top.setText("录音设置");
			ll_confirm4.setVisibility(View.VISIBLE);
			gd_ll_dialog_content.setVisibility(View.GONE);

			setting = sp.get(Constant.CALLRECORD_SETTING, 1);
			callRecorder_switch = sp.get(Constant.CALLRECORDER_OPEN_CLOSE, 0);

			if (setting == 0) {
				iv_auto.setBackgroundResource(R.drawable.callrecordsetting_press);
				iv_hand.setBackgroundResource(R.drawable.callrecordsetting);

			} else if (setting == 1) {
				iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
				iv_hand.setBackgroundResource(R.drawable.callrecordsetting_press);

			}
			if (callRecorder_switch == 0) {
				// 0:open 1:close
				iv_open_close.setBackgroundResource(R.drawable.open);
			} else {
				iv_open_close.setBackgroundResource(R.drawable.close);
			}

			break;

		case Constant.STAGE_MODE:
			tv_dialog_top.setText("设置短信回执接收场景");
			ll_confirm5.setVisibility(View.VISIBLE);
			gd_ll_dialog_content.setVisibility(View.GONE);

			updateMode();

			break;

		case Constant.LH_DETAIL_LONG_DELETE_RECORD:
			sms_month = mintent.getStringExtra("smsmonth");
			telnumber = mintent.getStringExtra(Constant.PHONE_NUMBER);
			smstime = mintent.getLongExtra("smstime", 0);
			break;

		case Constant.HZ_DETAIL_LONG_DELETE_RECORD:
			sms_month = mintent.getStringExtra("smsmonth");
			telnumber = mintent.getStringExtra(Constant.PHONE_NUMBER);
			smsport = mintent.getStringExtra(Constant.SMS_PORT);
			smstime = mintent.getLongExtra("smstime", 0);
			break;
		case Constant.HZ_DETAIL_DELETE_SINGLE_RECORD:
			singleid = mintent.getIntExtra("singleid", 0);
			break;

		case Constant.DEL_ONE_CHAT_ITME:
			ll_btn_bottom.setVisibility(View.GONE);
			gd_ll_dialog_content.setVisibility(View.GONE);
			tv_dialog_top.setText("短信记录处理方式");
			ll_btn_confim_menuitems.setVisibility(View.VISIBLE);
			break;

		case Constant.LONG_SM_LOCK_OPEN:
			// 解锁私密联系人
			gd_iv_titlebar_icon
					.setBackgroundResource(R.drawable.gd_dialog_private_contacts_icon);
			tv_dialog_top.setText("解锁私密联系人");
			gd_ll_dialog_content.setVisibility(View.GONE);
			gd_private_contacts_unlock.setVisibility(View.VISIBLE);
			break;

		// case Constant.DEL_MANY_CHAT_ITME :
		// smLockPosition = mintent.getStringExtra(Constant.PHONE_NUMBER);
		// break;

		case Constant.CONFIRM_DOIMPORSYS:
			Message msg0 = processhanler.obtainMessage();
			msg0.obj = new Integer(10);
			processhanler.sendMessageDelayed(msg0, 100);
			break;

		case Constant.CONFIRM_IMPORSYS:
			if(metric.widthPixels < 550){
				//800分辨率
				tv_dialog_content.setMinHeight(70);
			}else if(metric.widthPixels > 900){
				//1920分辨率
				tv_dialog_content.setMinHeight(150);
			}
			gd_iv_titlebar_icon.setBackgroundResource(R.drawable.gd_dialog_private_contacts_icon);
			tv_dialog_top.setText("导入短信");
			tv_dialog_content
					.setText("确定添加后，您与私密联系人的短信（包括今后的和历史的）都将仅存储到通信助手私密信箱中。");
			importnum = mintent.getStringExtra(Constant.IMPORT_NUM);
			break;

		case Constant.DETAIL_DELETE_RECORD:
			tv_dialog_content.setText(content);
			break;

		case Constant.DETAIL_MULT_DELETE_RECORD:
			tv_dialog_content.setText(content);
			break;

		case Constant.VERSION:
			gd_ll_dialog_content.setVisibility(View.GONE);
			findViewById(R.id.gd_new_vision).setVisibility(View.VISIBLE);
			tv_dialog_top.setText("发现新版本");
			gd_iv_titlebar_icon
					.setBackgroundResource(R.drawable.gd_new_version);
			ClientInfo clientInfo = (ClientInfo) mintent
					.getSerializableExtra(Constant.CLIENT_INFO);
			String description = clientInfo.getDescription();
			// tv_dialog_content.setText("\n更新内容：\n" + description);
			((TextView) findViewById(R.id.tv_new_version)).setText("更新内容：\n"
					+ description);
			btn_dialog_ok.setText("升级");
			break;
		}

//		registerReceiver(sentReceiver, new IntentFilter(Sms.SENT_SMS_ACTION));
//		registerReceiver(deliveryReceiver, new IntentFilter(
//				Sms.DELIVERED_SMS_ACTION));
	}

	private void jieSuoSimi() {
		// LinearLayout.LayoutParams ll_dialog_content = new
		// LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.FILL_PARENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// ll_dialog_content.setMargins(70, 40, 0, 0);
		// vi_tanchukuang.setVisibility(View.VISIBLE);
		// tv_dialog_top.setText("解锁私密联系人");
		// tv_dialog_content.setText("确定解锁此私密联系人吗？");
		// tv_dialog_content.setTextSize(20);
		// tv_dialog_content.setLayoutParams(ll_dialog_content);
		// // tv_dialog_content.setTextColor(0x504f4f);
		// LinearLayout.LayoutParams ll_inserttosys_div = new
		// LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.FILL_PARENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT);
		// ll_inserttosys_div.setMargins(70, 40, 0, 68);
		// tv_dialog_inserttosys_div.setVisibility(View.VISIBLE);
		// tv_dialog_inserttosys_div.setLayoutParams(ll_inserttosys_div);
		// // btn_dialog_cancel.setTextColor(0x504f4f);
		// btn_dialog_ok.setText("解锁");
		// btn_dialog_cancel.setText("取消");
		// btn_dialog_ok.setTextColor(0x504f4f);
	}

//	private BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			showMessageBox("对方接收成功!");
//		}
//	};
	private ArrayList<TextMessageBean> delsmslist;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.gd_iv_dialog_close:
			finish();
			break;

		case R.id.btn_dialog_cancel:
			switch (number) {
			// case Constant.NOW_OPEN:
			// Intent intent2 = new
			// Intent(ConfirmDialogActivity.this,
			// PrivateZooActivity.class);
			// startActivity(intent2);
			// finish();
			// break;

			case Constant.PWD_DIFFERENT:
				setResult(Constant.PWD_DIFFERENT);
				break;
			case Constant.SCUCESS_REGISTER:
				notOrder();
				Intent intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.NOW_OPEN);
				startActivity(intent);
				break;
			case Constant.CONFIRM_IMPORSYS:
				setResult(Constant.CONFIRM_NOTIMPORSYS);
				break;

			}
			finish();
			break;

		case R.id.iv_private_switch:
			if (lMRFlag) {
				iv_private_switch.setBackgroundResource(R.drawable.open);
				lMRFlag = false;
				doUpdate(true);
			} else {
				iv_private_switch.setBackgroundResource(R.drawable.close);
				lMRFlag = true;
				doUpdate(false);
			}
			break;

		case R.id.tv_private_lock_modi:
			Intent intent = new Intent(this, SiMiSuoHomePasswordActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.ll_auto:
			iv_auto.setBackgroundResource(R.drawable.callrecordsetting_press);
			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
			setting = 0;
			break;

		case R.id.ll_hand:
			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
			iv_hand.setBackgroundResource(R.drawable.callrecordsetting_press);
			setting = 1;
			break;

		case R.id.iv_open_close:
			callRecorder_switch = sp.get(Constant.CALLRECORDER_OPEN_CLOSE, 0);

			if (callRecorder_switch == 0) {
				iv_open_close.setBackgroundResource(R.drawable.close);
				sp.update(Constant.CALLRECORDER_OPEN_CLOSE, 1);
			} else {
				iv_open_close.setBackgroundResource(R.drawable.open);
				sp.update(Constant.CALLRECORDER_OPEN_CLOSE, 0);
			}
			break;

		case R.id.ll_remind_mode:
			iv_remind_mode
					.setBackgroundResource(R.drawable.callrecordsetting_press);
			iv_all_mode.setBackgroundResource(R.drawable.callrecordsetting);
			stage_mode_value = 2;
			break;

		case R.id.ll_all_mode:
			iv_all_mode
					.setBackgroundResource(R.drawable.callrecordsetting_press);
			iv_remind_mode.setBackgroundResource(R.drawable.callrecordsetting);
			stage_mode_value = 3;
			break;
		// 客服电话
		case R.id.btn_service_phone:
			Tools.call(ConfirmDialogActivity.this, "10086");
			finish();
			break;
		case R.id.btn_service_app:
			Tools.call(ConfirmDialogActivity.this, "01057302552");
			finish();
			break;
		// 账户余额、数据业务、GPRS、剩余短信

		case R.id.btn_private_zoo_remaining_sum:
			Intent intent3 = new Intent(this, ConfirmDialogActivity.class);
			intent3.putExtra(Constant.DIALOG_KEY, Constant.COUNT_QUERY);
			startActivity(intent3);
			finish();
			break;
		case R.id.btn_private_zoo_order_business:
			Intent intent4 = new Intent(this, ConfirmDialogActivity.class);
			intent4.putExtra(Constant.DIALOG_KEY, Constant.ORDER_BUSINESS);
			startActivity(intent4);
			finish();
			break;
		case R.id.btn_private_zoo_gprs_remaining_sum:
			Intent intent5 = new Intent(this, ConfirmDialogActivity.class);
			intent5.putExtra(Constant.DIALOG_KEY, Constant.GPRS_REMANNING);
			startActivity(intent5);
			finish();
			break;
		case R.id.btn_private_zoo_sms_remaining_sum:
			Intent intent6 = new Intent(this, ConfirmDialogActivity.class);
			intent6.putExtra(Constant.DIALOG_KEY, Constant.CALL_QUERY);
			startActivity(intent6);
			finish();
			break;
		// 性别选择
		case R.id.btn_private_zoo_gender_male:
			Intent data = new Intent();
			data.putExtra("gender", "男");
			setResult(Constant.GENDER, data);
			finish();
			break;
		case R.id.btn_private_zoo_gender_female:
			Intent data1 = new Intent();
			data1.putExtra("gender", "女");
			setResult(Constant.GENDER, data1);
			finish();
			break;
		// 头像选择
		case R.id.tv_private_zoo_icon_seletct:
			Intent in_image = new Intent();
			setResult(RESULT_OK, in_image);
			finish();
			break;
		// 年龄选择
		case R.id.tv_private_zoo_text_input_birthday:

			showDialog(Constant.DATE_DIALOG_ID);
			break;
		case R.id.btn_private_zoo_icon_cancel:
			finish();
			break;
		case R.id.btn_dialog_exit:
			switch (number) {
			case Constant.PWD_EDIT_SUCCESS:
				setResult(Constant.PWD_EDIT_SUCCESS);
				break;
			}
			finish();
			break;

		case R.id.ll_btn_confim_menuitem_1:
			setResult(R.id.ll_btn_confim_menuitem_1);
			finish();
			break;
		case R.id.ll_btn_confim_menuitem_2:
			setResult(R.id.ll_btn_confim_menuitem_2);
			finish();

			break;
		case R.id.ll_btn_confim_menuitem_3:
			setResult(R.id.ll_btn_confim_menuitem_3);
			finish();
			break;

		case R.id.btn_dialog_ok:
			switch (number) {
			case Constant.PWD_DIFFERENT:
//				setResult(Constant.PWD_DIFFERENT);
//				startActivity(new Intent(this,
//						SiMiSuoHomePasswordActivity.class));
				finish();
				break;
			case Constant.EMAIL_EMPTY:
				finish();
				break;
			// case Constant.FIRST_TIME_SHOW :
			// Intent in_device = new Intent(
			// Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
			// startActivityForResult(in_device, 0);
			// finish();
			// break;
			case Constant.SPECIAL_PROMPT:
				finish();
				break;
			case Constant.DELETE_SINGLE_ITEM:
				setResult(Constant.DELETE_SINGLE_ITEM);
				finish();
				break;
			case Constant.CONGZHI_MENU_DELETE:
				setResult(Constant.CONGZHI_MENU_DELETE);
				finish();
				break;
			case Constant.DO_ACTIVE:
				startActivity(new Intent(ConfirmDialogActivity.this,
						GDPrivateZooLoginActivity.class));
				finish();
				break;

			case Constant.DE_ACTIVE:
				setResult(Constant.DE_ACTIVE);
				finish();
				break;

			case Constant.DELETE_RECORD:
				setResult(Constant.DELETE_RECORD);
				finish();
				break;

			case Constant.LONG_DELETE_RECORD:
				setResult(Constant.LONG_DELETE_RECORD);
				finish();
				break;
			case Constant.COUNT_QUERY:
				Tools.sendMsg(this, "101", "10086", 1);
				finish();
				break;
			case Constant.ORDER_BUSINESS:
				Tools.sendMsg(this, "0000", "10086", 1);
				finish();
				break;
			case Constant.GPRS_REMANNING:
				Tools.sendMsg(this, "112", "10086", 1);
				finish();
				break;
			case Constant.CALL_QUERY:
				Tools.call(ConfirmDialogActivity.this, "1008611");
				finish();
				break;
			case Constant.AGE:
				Intent intent2 = new Intent();
				intent2.putExtra("date", d2);
				setResult(Constant.AGE, intent2);
				finish();
				break;
			case Constant.SERVICE_PHONE:
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ "01057302471")));
				finish();
				break;

			case Constant.NOW_OPEN:
				// intent = new Intent(ConfirmDialogActivity.this,
				// PrivateZooActivity.class);
				// startActivity(intent);
				// setResult(Constant.NOW_OPEN);
				// String content = "kttxzsa";
				if (!TextUtils.isEmpty(sendMsgContent) && !TextUtils.isEmpty(sendMsgPost))
					Tools.sendMsg(this, sendMsgContent, sendMsgPost, 0);
				else
					Toast.makeText(this, "系统忙，请稍候再试...", 0).show();
				finish();

			case Constant.SM_LOCK_SETTING_DIALOG:
				finish();
				break;

			case Constant.SM_LOCK_OPEN:
				intent = new Intent();
				intent.putExtra(Constant.SAVE_SMS_TOSYS,
						gd_cb_dialog_inserttosys.isChecked());
				setResult(Constant.SM_LOCK_OPEN, intent);
				finish();
				break;

			case Constant.LONG_SM_LOCK_OPEN:
				intent = new Intent();
				intent.putExtra(Constant.SAVE_SMS_TOSYS,
						gd_cb_dialog_inserttosys.isChecked());
				setResult(Constant.LONG_SM_LOCK_OPEN, intent);
				finish();
				break;

			case Constant.DEL_MANY_CHAT_ITME:
				// Intent result = new Intent();
				// if (smLockPosition != null) {
				// result.putExtra(Constant.PHONE_NUMBER, smLockPosition);
				// }
				// setResult(Constant.DEL_MANY_CHAT_ITME, result);
				setResult(Constant.DEL_MANY_CHAT_ITME);
				finish();
				break;

			case Constant.CALLRECORDER_SETTING_DIALOG:
				sp.update(Constant.CALLRECORD_SETTING, setting);
				finish();
				break;

			case Constant.STAGE_MODE:
				sp.update(Constant.STAGE_MODE_VALUE, stage_mode_value);
				setResult(Constant.STAGE_MODE);
				finish();
				break;

			case Constant.LH_DETAIL_LONG_DELETE_RECORD:
				LouHuaDao ldao = LouHuaDao.getInstance(this);
				ldao.deleteSMSAccordingtoNumberAndMonth(telnumber, sms_month,
						smstime);
				// 发送漏话页面刷新的广播
				intent = new Intent();
				intent.setAction("com.wxxr.viewgroup.refreshlouhua");
				sendBroadcast(intent);
				finish();
				break;

			case Constant.HZ_DETAIL_LONG_DELETE_RECORD:
//				hdao.deleteSMSAccordingtoNumberAndMonth(telnumber, sms_month,
//						smstime);
				hdao.deleteAll(telnumber, smsport);
				// 发送回执页面刷新的广播
				intent = new Intent();
				intent.setAction("com.wxxr.viewgroup.refreshhuizhiall");
				sendBroadcast(intent);
				finish();
				break;
			case Constant.HZ_DETAIL_DELETE_SINGLE_RECORD:
				hdao.deleteSMSHuiZhi(singleid);
				// 删除单条短信后，发送刷新广播
				Intent in_delete = new Intent();
				in_delete.setAction("com.wxxr.viewgroup.refreshhuizhiall");
				sendBroadcast(in_delete);
				finish();
				break;

			case Constant.SCUCESS_REGISTER:
				alreadyOrder();
				finish();
				break;

			case Constant.CONFIRM_IMPORSYS:
				// mintent.putExtra(Constant.DIALOG_KEY,
				// Constant.CONFIRM_DOIMPORSYS);
				// processLogic();
				setResult(Constant.CONFIRM_DOIMPORSYS);
				finish();
				break;

			case Constant.DETAIL_DELETE_RECORD:
				// mintent.putExtra(Constant.DIALOG_KEY,
				// Constant.CONFIRM_DOIMPORSYS);
				// processLogic();
				setResult(Constant.DETAIL_DELETE_RECORD);
				finish();
				break;

			case Constant.DETAIL_MULT_DELETE_RECORD:
				// mintent.putExtra(Constant.DIALOG_KEY,
				// Constant.CONFIRM_DOIMPORSYS);
				// processLogic();
				setResult(Constant.DETAIL_MULT_DELETE_RECORD);
				finish();
				break;
			case Constant.VERSION:
				if(ispush!=null){
					Intent t = new Intent(ConfirmDialogActivity.this, DownAPK.class);
					ClientInfo clientInfo = (ClientInfo) mintent
							.getSerializableExtra(Constant.CLIENT_INFO);
					t.putExtra("downurl", clientInfo.getUrl());
					startService(t);
					if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
						AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.UPDATENOW.ordinal());
					}
				}else{
				  setResult(RESULT_OK);
				}
				finish();
				break;
			}

			break;
		}
	}

	/**
	 * 删除私密锁联系人信息
	 */
	private void doDeleteNumber() {
		if (gd_cb_dialog_inserttosys.isChecked()) {
			Intent t = new Intent(this, ImprorDataProcessDialog.class);
			String[] numbers = StringUtils.split(smLockPosition, ',');
			ArrayList<String> list = new ArrayList<String>();
			for (String num : numbers) {
				list.add(num);
			}
			t.putExtra(Constant.IS_IMPORT_NUMS, "");
			t.putExtra(Constant.IMPORT_OR_DEL_NUMS, list);
			t.putExtra(Constant.REQUEST_CODE, Constant.DEL_SMMSG_ATDIALG);
			startActivityForResult(t, Constant.DEL_SMMSG_ATDIALG);
			finish();

		} else {
			CMProgressMonitor monitor = new CMProgressMonitor(this) {
				@Override
				protected void handleFailed(Throwable cause) {
					log.warn("Failed to remove private number", cause);
					if (log.isDebugEnabled()) {
						showMessageBox("删除私密联系人失败，原因：["
								+ cause.getLocalizedMessage() + "]");
					} else {
						showMessageBox("删除私密联系人失败，请稍后再试...");
					}
				}

				@Override
				protected void handleDone(Object returnVal) {
					showMessageBox("删除私密联系人信息成功");
					finish();
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
				 */
				@Override
				protected Map<String, Object> getDialogParams() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在删除私密联系人,请稍侯...");
					return map;
				}
			};
			monitor.executeOnMonitor(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					String[] numbers = StringUtils.split(smLockPosition, ',');
					int leng = numbers.length;
					int i = 0;
					for (String num : numbers) {
						i++;

						// if (tv_dialog_inserttosys.isChecked()) {
						// List<TextMessageBean> list = getService(
						// IPrivateSMService.class).getAllMessageOf(num);
						// if (list != null) {
						// Tools.remove_to_sysbox.put(num, "");
						// Tools.inserToSysmsg(ConfirmDialogActivity.this
						// .getApplicationContext(), list);
						// getService(IPrivateSMService.class).deleteMessage(
						// num, i == leng - 1);
						// }
						// }
						getService(IPrivateSMService.class)
								.removePrivateNumber(num);
					}
					return null;
				}
			});
		}

	}

	/**
	 * 私密锁设置对话框 移动到 gdsimiset 里了
	 * 
	 * @param bool
	 */
	private void doUpdate(final boolean bool) {
		new CMProgressMonitor(this) {
			@Override
			protected void handleFailed(Throwable cause) {
				log.error("Caught exception when sending SM message", cause);
				showMessageBox("提醒设置失败，原因：[" + cause.getLocalizedMessage()
						+ "]");
			}

			@Override
			protected void handleDone(Object returnVal) {
				showMessageBox("提醒设置成功");
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
			 */
			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在更新提醒设置,请稍侯...");
				return map;
			}
		}.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				getService(IPrivateSMService.class).setRingBellWhenReceiving(
						bool);
				return null;
			}
		});
	}

	/**
	 * 订购引导--未订购
	 * 
	 */
	private void notOrder() {
		getService(IUserActivationService.class).addOrderStatus("0",
				new CMProgressMonitor(ConfirmDialogActivity.this) {
					@Override
					protected void handleFailed(Throwable cause) {

					}

					@Override
					protected void handleDone(Object returnVal) {
						// runOnUiThread(new Runnable(){
						// @Override
						// public void run() {
						// showMessageBox("确认未订购成功！");
						// }
						//
						// });
					}
				});
	}

	/**
	 * 订购引导——已订购
	 */
	private void alreadyOrder() {
		getService(IUserActivationService.class).addOrderStatus("1",
				new CMProgressMonitor(ConfirmDialogActivity.this) {
					@Override
					protected void handleFailed(Throwable cause) {

					}

					@Override
					protected void handleDone(Object returnVal) {
						// runOnUiThread(new Runnable(){
						// @Override
						// public void run() {
						// showMessageBox("确认已订购成功！");
						// }
						//
						// });
					}
				});
	}

	/**
	 * 接收场景模式
	 */
	private void updateMode() {
		final CMProgressMonitor monitor = new CMProgressMonitor(this, 1) {

			@Override
			protected void handleFailed(Throwable cause) {
				if (cause instanceof AppException) {
					showMessageBox(cause.getLocalizedMessage());
				} else {
					if (log.isDebugEnabled()) {
						showMessageBox("无法下载你的模式设置，原因：【"
								+ cause.getLocalizedMessage() + "】");
					} else {
						showMessageBox("无法下载你的模式设置，请稍后再试...");
					}
				}
				finish();
			}

			@Override
			protected void handleDone(final Object returnVal) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (returnVal != null) {
							stage_mode_value = (Integer) returnVal;
						}

						if (2 == stage_mode_value) {
							// 提醒模式
							iv_remind_mode
									.setBackgroundResource(R.drawable.callrecordsetting_press);
							iv_all_mode
									.setBackgroundResource(R.drawable.callrecordsetting);
						} else {
							// 全开模式
							iv_all_mode
									.setBackgroundResource(R.drawable.callrecordsetting_press);
							iv_remind_mode
									.setBackgroundResource(R.drawable.callrecordsetting);
						}

					}
				});
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
			 */
			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "数据加载");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在加载频道订阅数据,请稍侯...");
				return map;
			}
		};
		monitor.executeOnMonitor(new Callable<Object>() {
			@Override
			public Object call() throws Exception {
				IDXHZSettingService service = getService(IDXHZSettingService.class);
				return service.getReceivingMode();
			}
		});

	}

	/**
	 * 办理通信助手
	 * 
	 * @return
	 */
	public void getKTCommand() {

		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(final Object result) {
				runOnUiThread(new Runnable() {
					public void run() {
						if (null != result) {
							sendMsgContent = (String) result;
							String text = "将协助您发送免费短信" + sendMsgContent
									+ "到10086。需要您查收并回复10086发送到您手机的确认短信，才能成功办理。";
							tv_first_row.setText(Tools.ToDBC(text));
						}
					}
				});
			}

			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在操作数据,请稍侯...");
				return map;
			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getService(IClientCustomService.class).getKTCommand();
			}
		});
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Constant.DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, 1980, 0, 1);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case Constant.DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(1980, 0, 1);
			break;
		}
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			btn_dialog_ok.setClickable(true);
			btn_dialog_ok.setTextColor(Color.parseColor("#000000"));

			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fmt2 = new SimpleDateFormat("yyyyMMdd");
			Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
			// d1、d2分别是两种不同的日期格式，d1为yyyy-MM-dd，用来显示，d2为yyyyMMdd，个人中心设置birthday使用。
			String d1 = fmt.format(date);
			d2 = fmt2.format(date);
			try {
				Calendar calendar = Calendar.getInstance();
				Date today = calendar.getTime();
				int a = Integer.parseInt(d2);
				int b = Integer.parseInt(fmt2.format(today));
				if (a - b > 0) {
					Toast.makeText(getApplicationContext(), "不能选择大于当前的日期", 1)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			tv_birthday.setText(d1);
		}
	};
	private String content;
	private LinearLayout gd_private_contacts_unlock;
	private TextView gd_tv_contacts_unlock;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (number == Constant.PWD_EDIT_SUCCESS) {
				setResult(Constant.PWD_EDIT_SUCCESS);
				finish();
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
//		unregisterReceiver(deliveryReceiver);
		super.onDestroy();
	}

}
