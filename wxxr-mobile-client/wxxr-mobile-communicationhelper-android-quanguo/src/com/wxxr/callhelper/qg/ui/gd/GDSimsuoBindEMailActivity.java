package com.wxxr.callhelper.qg.ui.gd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IPrivateSimiNetService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.SiMiSuoHomePasswordActivity;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * 邮件绑 定的页面
 * 
 * @author yangrunfei
 * 
 */

public class GDSimsuoBindEMailActivity extends BaseActivity {

	protected static final int SET_PASS = 253;
	private EditText emailText;
	private Context mContext;
	private TextView gd_simihome_ok, gd_simihome_cancle, gd_simihome_check_ok, gd_simihome_check_cancle;
	private static boolean flag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getApplicationContext();
		setContentView(R.layout.gd_simisuohome);
		emailText = (EditText) findViewById(R.id.gd_simihome_inputeemail);
		gd_simihome_ok = (TextView) findViewById(R.id.gd_simihome_ok);
		gd_simihome_ok.setOnClickListener(this);
		gd_simihome_check_ok = (TextView) findViewById(R.id.gd_simihome_check_ok);
		gd_simihome_check_ok.setOnClickListener(this);

		((TextView) findViewById(R.id.tv_titlebar_name)).setText("私密信息锁");
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		gd_simihome_cancle = (TextView) findViewById(R.id.gd_simihome_cancle);
		gd_simihome_cancle.setOnClickListener(this);
		gd_simihome_check_cancle = (TextView) findViewById(R.id.gd_simihome_check_cancle);
		gd_simihome_check_cancle.setOnClickListener(this);

		emailText.addTextChangedListener(new MyTextWatch());
	}

	private class MyTextWatch implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			flag = true;
			if (s.length() > 0) {
				gd_simihome_check_ok.setVisibility(View.VISIBLE);
				gd_simihome_check_cancle.setVisibility(View.VISIBLE);
				gd_simihome_ok.setVisibility(View.GONE);
				gd_simihome_cancle.setVisibility(View.GONE);
			} else {
				gd_simihome_check_ok.setVisibility(View.GONE);
				gd_simihome_check_cancle.setVisibility(View.GONE);
				gd_simihome_ok.setVisibility(View.VISIBLE);
				gd_simihome_cancle.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

	}

//	@Override
//	protected void onResume() {
//		flag = true;
//		super.onResume();
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		case R.id.gd_simihome_cancle:
			finish();
			break;
		case R.id.gd_simihome_check_cancle:
			finish();
			break;
		case R.id.gd_simihome_ok:
				checkEmailAddress();
			break;
			
		case R.id.gd_simihome_check_ok:
			if(flag){
				checkEmailAddress();
			}else{
				Toast.makeText(this, "请求已发出，正在处理中", 1).show();
			}
			break;
			
		}
	}

	/**
	 * 检查邮箱
	 */
	private void checkEmailAddress() {
		String email = emailText.getText().toString();
		if (email.length() == 0) {
			// 未输入邮箱
			Intent intent = new Intent(this, ConfirmDialogActivity.class);
			intent.putExtra(Constant.DIALOG_KEY, Constant.EMAIL_EMPTY);
			intent.putExtra(Constant.DIALOG_CONTENT, "您尚未输入邮箱，无法使用私密信箱锁");
			startActivity(intent);
		} else if (Tools.isEMailStr(email)) {
			GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {

				@Override
				protected void handleDone(Object returnVal) {
					if (returnVal instanceof Boolean) {
						if ((Boolean) returnVal) {
							if (getService(IUserUsageDataRecorder.class) != null) {					
								 getService(IUserUsageDataRecorder.class).doRecord(ActivityID.SIMISUO_OK.ordinal());
							 }
							getService(IPrivateSMService.class).setBindedEmail(
									emailText.getText().toString());
							Intent intent = new Intent(mContext, SiMiSuoHomePasswordActivity.class);
							startActivity(intent);
							finish();
//							startActivityForResult(new Intent(mContext,
//									SiMiSuoHomePasswordActivity.class),
//									SET_PASS);
						} else {
							Toast.makeText(mContext, "获得授权值不正确", 0).show();
						}
					}

				}

				@Override
				protected void handleFailed(Throwable cause) {
					Toast.makeText(mContext, "授权失败，请稍候再试", 0).show();
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
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在验证邮箱，请稍侯...");
					return map;
//					Toast.makeText(GDSimsuoBindEMailActivity.this, "正在验证邮箱,请稍侯...", 1).show();
//					return null;
				}

			};

			monitor.executeOnMonitor(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					flag = false;
					return getService(IPrivateSimiNetService.class)
							.addEmailBinding(getFramework().getDeviceId(),
									emailText.getText().toString());
				}

			});

		} else {
			Toast.makeText(this, "输入邮箱格式有误，请重新输入", 0).show();
		}
	}

//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (SET_PASS == requestCode) {
//			if (resultCode == Activity.RESULT_OK) {
//				startActivity(new Intent(GDSimsuoBindEMailActivity.this,
//						GDSimiContactsActvity.class));
//				finish();
//			}
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
}
