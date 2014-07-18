package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

public class NewConfirmDialogActivity extends BaseActivity {

	private Intent intent;
	private int number;
	private TextView tv_new_dialog_content, tv_new_dialog_prompt, tv_first_row,
			tv_second_row;
	private LinearLayout ll_confirm1, ll_confirm2, ll_confirm3, ll_confirm4, ll_btn;
	private Button btn_new_dialog_remeber;
	private boolean lMRFlag = true;
	private ImageView iv_sm_switch;
	private int setting;
	private ImageView iv_hand, iv_auto, iv_close;
	private ManagerSP sp;
	
	private static final Trace log = Trace.register(NewConfirmDialogActivity.class); 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_dialog);

		findView();

		processLogic();
	}

	private void findView() {
		sp = ManagerSP.getInstance(this); 
		intent = getIntent();
		findViewById(R.id.btn_new_dialog_cancel).setOnClickListener(this);
		findViewById(R.id.btn_new_dialog_ok).setOnClickListener(this);
		tv_new_dialog_prompt = (TextView) findViewById(R.id.tv_new_dialog_prompt);
		// 文本删除
		tv_new_dialog_content = (TextView) findViewById(R.id.tv_new_dialog_content);
		// 文本两行
		ll_confirm1 = (LinearLayout) findViewById(R.id.ll_confirm1);
		tv_first_row = (TextView) findViewById(R.id.tv_first_row);
		tv_second_row = (TextView) findViewById(R.id.tv_second_row);
		// 文本特别提示
		ll_confirm2 = (LinearLayout) findViewById(R.id.ll_confirm2);
		ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
		btn_new_dialog_remeber = (Button) findViewById(R.id.btn_new_dialog_remeber);
		// 私密锁提示
		ll_confirm3 = (LinearLayout) findViewById(R.id.ll_confirm3);
		iv_sm_switch = (ImageView) findViewById(R.id.iv_sm_switch);
		findViewById(R.id.tv_modi_pwd).setOnClickListener(this);
		// 录音设置
		ll_confirm4 = (LinearLayout) findViewById(R.id.ll_confirm4);
		iv_hand = (ImageView) findViewById(R.id.iv_hand);
		iv_auto =(ImageView) findViewById(R.id.iv_auto);
		iv_close = (ImageView) findViewById(R.id.iv_close);
		
		findViewById(R.id.ll_auto).setOnClickListener(this);
		findViewById(R.id.ll_hand).setOnClickListener(this);
		findViewById(R.id.ll_close).setOnClickListener(this);
		
	}

	private void processLogic() {
		number = intent.getIntExtra(Constant.DIALOG_KEY, 0);

		switch (number) {

		case Constant.DO_ACTIVE:
			tv_new_dialog_content.setText("客户端未登录，是否去登录账号？");
			break;

		case Constant.DE_ACTIVE:
			tv_new_dialog_content.setText("确定退出账号？");
			break;

		case Constant.SERVICE_PHONE:
			ll_confirm1.setVisibility(View.VISIBLE);
			tv_new_dialog_content.setVisibility(View.GONE);
			tv_first_row.setTextSize(20);
			tv_first_row.setText("010-57302471");
			tv_second_row.setTextSize(20);
			tv_second_row.setText("是否呼叫？");
			break;

		case Constant.NOW_OPEN:
			ll_confirm1.setVisibility(View.VISIBLE);
			tv_new_dialog_content.setVisibility(View.GONE);
			tv_first_row.setTextSize(19);
			tv_first_row.setText("马上开通通信助手业务?");
			tv_second_row.setTextSize(16);
			tv_second_row
					.setText("马上开通通信助手？本操作将发送短信“BLTXZS”到10086申请开通业务，请注意稍后手机二次确认短信，按提示回复，即可激活本客户端。"
							+ "\n" + "（本条短信免费）");
			break;

//		case Constant.SPECIAL_PROMPT:
//			ll_confirm2.setVisibility(View.VISIBLE);
//			tv_new_dialog_content.setVisibility(View.GONE);
//			btn_new_dialog_remeber.setVisibility(View.VISIBLE);
//			ll_btn.setVisibility(View.GONE);
//			btn_new_dialog_remeber.setOnClickListener(this);
//
//			tv_new_dialog_prompt.setText("特别提示");
//			break;

		case Constant.SM_LOCK_OPEN:
			tv_new_dialog_content.setText("解锁联系人？");
			break;

		case Constant.SM_LOCK_SETTING_DIALOG:
			ll_confirm3.setVisibility(View.VISIBLE);
			tv_new_dialog_content.setVisibility(View.GONE);
			btn_new_dialog_remeber.setVisibility(View.VISIBLE);
			ll_btn.setVisibility(View.GONE);

			btn_new_dialog_remeber.setText("取消");
			btn_new_dialog_remeber.setOnClickListener(this);
			iv_sm_switch.setOnClickListener(this);

			try {
				this.lMRFlag = getService(IPrivateSMService.class).isRingBellWhenReceiving() == false;
				if (this.lMRFlag) {
					iv_sm_switch.setBackgroundResource(R.drawable.close);
				} else {
					iv_sm_switch.setBackgroundResource(R.drawable.open);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case Constant.CALLRECORDER_SETTING_DIALOG:
			ll_confirm4.setVisibility(View.VISIBLE);
			tv_new_dialog_content.setVisibility(View.GONE);
			
			setting = sp.get(Constant.CALLRECORD_SETTING, 1);
			
			if(setting == 0){
				iv_auto.setBackgroundResource(R.drawable.callrecordsetting_press);
				iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
				iv_close.setBackgroundResource(R.drawable.callrecordsetting);
				
			}else if(setting == 1){
				iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
				iv_hand.setBackgroundResource(R.drawable.callrecordsetting_press);
				iv_close.setBackgroundResource(R.drawable.callrecordsetting);
				
			}else{
				iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
				iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
				iv_close.setBackgroundResource(R.drawable.callrecordsetting_press);
			}
			
			tv_new_dialog_prompt.setText("录音设置");
			break;

		}

		registerReceiver(sentReceiver, new IntentFilter(Sms.SENT_SMS_ACTION));
		registerReceiver(deliveryReceiver, new IntentFilter(
				Sms.DELIVERED_SMS_ACTION));
	}

	private BroadcastReceiver sentReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				showMessageBox("短信发送成功!");
				break;
			default:
				showMessageBox("短信发送失败!");
				break;
			}
		}
	};

	private BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			showMessageBox("对方接收成功!");
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_new_dialog_cancel:
			finish();
			break;

		case R.id.btn_new_dialog_remeber:
			finish();
			break;

		case R.id.tv_modi_pwd:
			Intent intent = new Intent();
			intent.setClass(NewConfirmDialogActivity.this, SiMiSuoHomePasswordActivity.class);
			startActivity(intent);
			finish();
			break;

		case R.id.iv_sm_switch:
			if (lMRFlag) {
				iv_sm_switch.setBackgroundResource(R.drawable.open);
				lMRFlag = false;
				doUpdate(true);
			} else {
				iv_sm_switch.setBackgroundResource(R.drawable.close);
				lMRFlag = true;
				doUpdate(false);
			}
			break;
			
		case R.id.ll_auto:
			iv_auto.setBackgroundResource(R.drawable.callrecordsetting_press);
			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
			iv_close.setBackgroundResource(R.drawable.callrecordsetting);
			setting = 0;
			break;
			
		case R.id.ll_hand:
			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
			iv_hand.setBackgroundResource(R.drawable.callrecordsetting_press);
			iv_close.setBackgroundResource(R.drawable.callrecordsetting);
			setting = 1;
			break;
			
		case R.id.ll_close:
			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
			iv_close.setBackgroundResource(R.drawable.callrecordsetting_press);
			setting = 2;
			break;

		case R.id.btn_new_dialog_ok:
			switch (number) {

			case Constant.DO_ACTIVE:
				setResult(Constant.DO_ACTIVE);
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

			case Constant.SERVICE_PHONE:
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ "01057302471")));
				finish();
				break;

			case Constant.NOW_OPEN:
				String content = "BLTXZS";
				String address = "10086";
				Tools.sendMsg(this, content, address,0);
				finish();
				break;

			case Constant.SM_LOCK_OPEN:
				setResult(Constant.SM_LOCK_OPEN);
				finish();
				break;
				
			case Constant.CALLRECORDER_SETTING_DIALOG:
				sp.update(Constant.CALLRECORD_SETTING, setting);
				finish();
				break;

			}

			break;
		}
	}
	
	private void doUpdate(final boolean bool){
		new CMProgressMonitor(this) {			
			@Override
			protected void handleFailed(Throwable cause) {
				log.error("Caught exception when sending SM message", cause);
				showMessageBox("提醒设置失败，原因：["+cause.getLocalizedMessage()+"]");
			}			
			@Override
			protected void handleDone(Object returnVal) {
				showMessageBox("提醒设置成功");
			}
			/* (non-Javadoc)
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
				getService(IPrivateSMService.class).setRingBellWhenReceiving(bool);
				return null;
			}
		});
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(sentReceiver);
		unregisterReceiver(deliveryReceiver);
		super.onDestroy();
	}
}
