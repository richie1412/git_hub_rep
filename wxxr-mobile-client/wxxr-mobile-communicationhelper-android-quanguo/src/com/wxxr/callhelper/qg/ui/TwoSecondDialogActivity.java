package com.wxxr.callhelper.qg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ITaskHandler;
import com.wxxr.callhelper.qg.Version;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.mobile.core.util.StringUtils;

public class TwoSecondDialogActivity extends BaseActivity {
	

	private TextView tv_dialog, alert_title;
	private String message;
	private ImageView alert_icon;
	private LinearLayout ll_btn_footer1,ll_btn_footer2,ll_btn_footer3;
	private int interactiveMode = 0;
	private Button btn_ok1,btn_ok2,btn_cancel,btn_31,btn_32,btn_33;
	private LinearLayout alert_header;
	private static final String DIALOG_ERROR = "error";
	private static final String DIALOG_MESSAGE = "info";
	private static final String DIALOG_WARN = "warn";
	private static final String DIALOG_LOGO = "LOGO";
//	private String versionName, buildtime, target;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.two_second_dialog);
		init();
		updateView();
	}

	private void init() {
//		try {
//			versionName = getPackageManager().getPackageInfo(
//					this.getPackageName(), 0).versionName;
//			// 设置buildtime
//			Config.setBuildNumber(this);
//
//			Trace.getTrace(TwoSecondDialogActivity.class).info(
//					"versionName:" + versionName);
//
//			if (!TextUtils.isEmpty(Constant.BUILDTIME)) {
//				buildtime = Constant.BUILDTIME;
//			}
//			if (!TextUtils.isEmpty(Constant.TARGET)) {
//				target = Constant.TARGET;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		tv_dialog = (TextView) findViewById(R.id.tv_dialog);
		alert_header = (LinearLayout) findViewById(R.id.alert_header);
		alert_icon = (ImageView) findViewById(R.id.alert_icon);
		alert_title = (TextView) findViewById(R.id.alert_title);
		ll_btn_footer1 = (LinearLayout)findViewById(R.id.ll_btn_footer1);
		ll_btn_footer2 = (LinearLayout)findViewById(R.id.ll_btn_footer2);
		ll_btn_footer3 = (LinearLayout)findViewById(R.id.ll_btn_footer3);
		btn_ok1 = (Button)findViewById(R.id.btn_dialog_ok1);
		btn_ok2 = (Button)findViewById(R.id.btn_dialog_ok2);
		btn_cancel = (Button)findViewById(R.id.btn_dialog_cancel);
		btn_31 = (Button)findViewById(R.id.btn_dialog_btn31);
		btn_32 = (Button)findViewById(R.id.btn_dialog_btn32);
		btn_33 = (Button)findViewById(R.id.btn_dialog_btn33);

	}

	public void updateView() {
		Intent intent = getIntent();
		int number = intent.getIntExtra(Constant.DIALOG_KEY, 0);
		this.interactiveMode = intent.getIntExtra(Constant.DIALOG_INTERACTIVE_MODE, 0);
		message = intent.getStringExtra(Constant.DIALOG_MESSAGE);
		if (message == null) {
			switch (number) {
			case Constant.VERSION:
				message = "您已是最新版本V" + Version.getVersionNumber() + "\n" + "BuildNumber: "
						+ Version.getBuildNumber();
				break;
			case Constant.TELPHONE:
				message = "您所输入的手机号码错误";
				break;
			case Constant.AUTHCODE:
				message = "您所输入的验证码错误";
				break;
			case Constant.TERM:
				message = "您必须勾选同意客户使用协议";
				break;
			case Constant.CALLRECORD_EXIT:
				message = "请先停止播放";
				break;
			case Constant.PASSWORLD_LENGTH:
				message = "您输入的密码长度不正确，必须为6-12位字符";
				break;
			case Constant.PASSWORLD_NULL:
				message = "您输入的密码长度不能为空";
				break;
			case Constant.PASSWORLD_NOT_SAME:
				message = "您两次输入的密码不一致，请重新输入";
				break;
			}
		}
		tv_dialog.setText(message);
		String msgIcon = intent.getStringExtra(Constant.DIALOG_ICON_KEY);
		String msgTitle = intent.getStringExtra(Constant.DIALOG_TITLE_KEY);
		boolean headerFlag = intent.getBooleanExtra(Constant.DIALOG_HEADER_KEY,
				true);
		if (!headerFlag) {
			alert_header.setVisibility(View.GONE);
		} else {
			if (msgTitle != null) {
				alert_title.setText(msgTitle);
			} else {
				alert_title.setText("提示");
			}
			if (msgIcon != null) {
				if (msgIcon.equals(DIALOG_ERROR)) {
					alert_icon.setImageDrawable(getResources().getDrawable(
							R.drawable.dialog_icon_error));
				} else if (msgIcon.equals(DIALOG_MESSAGE)) {
					alert_icon.setImageDrawable(getResources().getDrawable(
							R.drawable.dialog_icon_msg2));
				} else if (msgIcon.equals(DIALOG_WARN)) {
					alert_icon.setImageDrawable(getResources().getDrawable(
							R.drawable.dialog_icon_warn));
				}else if (msgIcon.equals(DIALOG_LOGO)){
					alert_icon.setImageDrawable(getResources().getDrawable(
							R.drawable.push_msg_title_img));
				}
			} else {
				alert_icon.setImageDrawable(getResources().getDrawable(
						R.drawable.dialog_icon_msg2));
			}
		}
		String okText = intent.getStringExtra("OK_TEXT");
		String cancelText = intent.getStringExtra("CANCEL_TEXT");
		String s = intent.getStringExtra("OK_HANDLER");
		Long okId = null;
		Long cancelId = null;
		if(s != null){
			try {
				okId = Long.valueOf(s);
			}catch(Throwable t){}
		}
		s  = intent.getStringExtra("CANCEL_HANDLER");
		if(s != null){
			try {
				cancelId = Long.valueOf(s);
			}catch(Throwable t){}
		}
		final Long okHandlerId = okId;
		final Long cancelHandlerId = cancelId;
		switch(this.interactiveMode){
		case Constant.DIALOG_INTERACTIVE_MODE_OK:
			this.ll_btn_footer1.setVisibility(View.VISIBLE);
			this.ll_btn_footer2.setVisibility(View.GONE);
			this.ll_btn_footer3.setVisibility(View.GONE);
			if(!StringUtils.isBlank(okText)){
				this.btn_ok1.setText(okText);
			}
			this.btn_ok1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ITaskHandler handler = (okHandlerId != null) ? getFramework().getTaskHandler(okHandlerId) : null;
					if(handler != null){
						handler.doExecute(TwoSecondDialogActivity.this);
					}
					setResult(RESULT_OK);
					finish();
				}
			});
			break;
		case Constant.DIALOG_INTERACTIVE_MODE_OK_CANCEL:
			this.ll_btn_footer1.setVisibility(View.GONE);
			this.ll_btn_footer2.setVisibility(View.VISIBLE);
			this.ll_btn_footer3.setVisibility(View.GONE);
			if(!StringUtils.isBlank(okText)){
				this.btn_ok2.setText(okText);
			}
			if(!StringUtils.isBlank(cancelText)){
				this.btn_cancel.setText(cancelText);
			}
			this.btn_ok2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ITaskHandler handler = (okHandlerId != null) ? getFramework().getTaskHandler(okHandlerId) : null;
					if(handler != null){
						handler.doExecute(TwoSecondDialogActivity.this);
					}
					setResult(RESULT_OK);
					finish();
				}
			});
			this.btn_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ITaskHandler handler = (cancelHandlerId != null) ? getFramework().getTaskHandler(cancelHandlerId) : null;
					if(handler != null){
						handler.doExecute(TwoSecondDialogActivity.this);
					}
					setResult(RESULT_CANCELED);
					finish();
				}
			});

			break;
		case Constant.DIALOG_INTERACTIVE_MODE_3BUTTON:
			this.ll_btn_footer1.setVisibility(View.GONE);
			this.ll_btn_footer2.setVisibility(View.GONE);
			this.ll_btn_footer3.setVisibility(View.VISIBLE);
			String btn31Text = intent.getStringExtra("BTN31_TEXT");
			String btn32Text = intent.getStringExtra("BTN32_TEXT");
			String btn33Text = intent.getStringExtra("BTN33_TEXT");

			if(!StringUtils.isBlank(btn31Text)){
				this.btn_31.setText(btn31Text);
			}
			if(!StringUtils.isBlank(btn32Text)){
				this.btn_32.setText(btn32Text);
			}
			if(!StringUtils.isBlank(btn33Text)){
				this.btn_33.setText(btn33Text);
			}
			Long h1 = null;
			Long h2 = null;
			Long h3 = null;
			s  = intent.getStringExtra("BTN31_HANDLER");
			if(s != null){
				try {
					h1 = Long.valueOf(s);
				}catch(Throwable t){}
			}
			s  = intent.getStringExtra("BTN32_HANDLER");
			if(s != null){
				try {
					h2 = Long.valueOf(s);
				}catch(Throwable t){}
			}
			s  = intent.getStringExtra("BTN33_HANDLER");
			if(s != null){
				try {
					h3 = Long.valueOf(s);
				}catch(Throwable t){}
			}
			final Long handler1 = h1,handler2 = h2, handler3 = h3;
			this.btn_31.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ITaskHandler handler = (handler1 != null) ? getFramework().getTaskHandler(handler1) : null;
					if(handler != null){
						handler.doExecute(TwoSecondDialogActivity.this);
					}
					setResult(RESULT_OK);
					finish();
				}
			});
			this.btn_32.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ITaskHandler handler = (handler2 != null) ? getFramework().getTaskHandler(handler2) : null;
					if(handler != null){
						handler.doExecute(TwoSecondDialogActivity.this);
					}
					setResult(RESULT_OK);
					finish();
				}
			});
			this.btn_33.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ITaskHandler handler = (handler3 != null) ? getFramework().getTaskHandler(handler3) : null;
					if(handler != null){
						handler.doExecute(TwoSecondDialogActivity.this);
					}
					setResult(RESULT_OK);
					finish();
				}
			});

			break;

		case Constant.DIALOG_INTERACTIVE_MODE_EMPTY:
		default:
			this.ll_btn_footer1.setVisibility(View.GONE);
			this.ll_btn_footer2.setVisibility(View.GONE);
			processLogic();
			break;
//			this.ll_btn_footer1.setVisibility(View.GONE);
//			if(!StringUtils.isBlank(okText)){
//				this.btn_ok1.setText(okText);
//			}
//			this.btn_ok1.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					setResult(RESULT_OK);
//					finish();
//				}
//			});
//			this.ll_btn_footer2.setVisibility(View.GONE);
//			processLogic();
//			break;
		}
	}

	private void processLogic() {
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				setResult(RESULT_OK);
				if(!isFinishing()){
					finish();
				}
			}
		}, 2000);
	}
}
