package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IPrivateSimiNetService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.gd.GDCMProgressMonitor;
import com.wxxr.callhelper.qg.ui.gd.GDSimiContactsActvity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;

/**
 * 充值密码的弹出框 ， 和 验证 失败 的弹出框
 * 
 * @author yangrunfei
 * 
 */
public class SiMiContactsResetPasswordActivity extends BaseActivity {

	private EditText tv_contact_num;
	private TextView ll_concel, ll_sure, ll_sure_check;
	private boolean showErrTip = false;
	private TextView tv_simi_errcodetip;
	private LinearLayout gd_simi_input_div;
	private TextView tv_title_name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gd_resetpass);
		String err = getIntent().getStringExtra("errcheckcode");
		tv_simi_errcodetip = (TextView) findViewById(R.id.tv_simi_errcodetip);
		gd_simi_input_div = (LinearLayout) findViewById(R.id.gd_simi_input_div);
		tv_title_name = (TextView) findViewById(R.id.tv_dialog_top);
		
		if (err != null && err.length() > 0) {
			tv_title_name.setText("检验码有错误");
			gd_simi_input_div.setVisibility(View.GONE);
			tv_simi_errcodetip.setVisibility(View.VISIBLE);

		} else {

			TextView tip = (TextView) findViewById(R.id.et_simi_divline);
			String text = tip.getText().toString();
			String email_address = getService(IPrivateSMService.class)
					.getBindedEmail();
			text = text.replace("#", email_address);
			tip.setText(text);
		}

		ll_concel = (TextView) findViewById(R.id.ll_simi_add_concel);
		ll_concel.setOnClickListener(this);
		ll_sure_check = (TextView) findViewById(R.id.ll_simi_add_check);
		
		ll_sure = (TextView) findViewById(R.id.ll_simi_add_sure);
		ll_sure.setOnClickListener(this);
		
		tv_contact_num = (EditText) findViewById(R.id.et_simi_authcode);
		tv_contact_num.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 0){
					ll_sure.setVisibility(View.VISIBLE);
					ll_sure_check.setVisibility(View.GONE);
				}else{
					ll_sure.setVisibility(View.GONE);
					ll_sure_check.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
		});
		
		sendEmailMsg();
	}

	void showVercodeErr() {
		showErrTip = true;
		tv_title_name.setText("检验码有错误");
		gd_simi_input_div.setVisibility(View.GONE);
		tv_simi_errcodetip.setText(Tools.ToDBC(tv_simi_errcodetip.getText().toString()));
		tv_simi_errcodetip.setVisibility(View.VISIBLE);
		ll_sure.setVisibility(View.VISIBLE);
		ll_sure_check.setVisibility(View.GONE);
		ll_concel.setText("需要客服协助");
		ll_sure.setText("返回");
	}
	
	private void sendEmailMsg() {
		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleDone(Object returnVal) {
				if (returnVal instanceof Boolean) {
					if ((Boolean) returnVal) {
						// goInputCodepage();
					} else {
//						showMessageBox("发送验证码邮件失败，请重试");
						Toast.makeText(SiMiContactsResetPasswordActivity.this, "发送验证码邮件失败，请重试", 1).show();
					}
				} else {
//					showMessageBox("发送验证码邮件失败，请重试");
					Toast.makeText(SiMiContactsResetPasswordActivity.this, "发送验证码邮件失败，请重试", 1).show();
				}
			}

			@Override
			protected void handleFailed(Throwable cause) {

			}

			// @Override
			// protected Map<String, Object> getDialogParams() {
			// Map<String, Object> map = new HashMap<String, Object>();
			// map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
			// map.put(DIALOG_PARAM_KEY_MESSAGE,"正在发送验证邮件，请稍等");
			// return map;
			// }

		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				String email = getService(IPrivateSMService.class)
						.getBindedEmail();
				return getService(IPrivateSimiNetService.class)
						.generateCheckCode(getFramework().getDeviceId(), email);
			}
		});

	}

	protected void goServerCheck(final String number) {
		GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {

			@Override
			protected void handleDone(Object returnVal) {

				if (returnVal instanceof Boolean) {
					if ((Boolean) returnVal) {
						tv_contact_num.setText("");
						Intent intent = new Intent(getApplicationContext(),
								SiMiSuoHomePasswordActivity.class);
						intent.putExtra("editPwd", 1);
						startActivityForResult(intent, 1000);
						
					} else {
//						showCheckFail();
						showVercodeErr();
					}
				} else {
//					showCheckFail();
					showVercodeErr();
				}

			}

			@Override
			protected void handleFailed(Throwable cause) {
//				showCheckFail();
				showVercodeErr();

			}

		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				// TODO Auto-generated method stub
				String email = getService(IPrivateSMService.class)
						.getBindedEmail();
				return getService(IPrivateSimiNetService.class)
						.verifyCheckCode(getFramework().getDeviceId(), email,
								number);
			}
		});

	}

	protected void showCheckFail() {

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				showVercodeErr();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1000) {
			switch(resultCode){
			case RESULT_OK:
				setResult(RESULT_OK);
				finish();
				break;
//			case Constant.PWD_EDIT_SUCCESS:
//				setResult(RESULT_OK);
//				finish();
//				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_simi_add_sure:
			if (showErrTip) {
				// 返回
				finish();
				// startActivity(new Intent(this, SiMiSuoThirdPassword.class));
			} else {
				String editContent = tv_contact_num.getText().toString();
				if(!TextUtils.isEmpty(editContent)){
					goServerCheck(editContent);
					ll_sure.setVisibility(View.GONE);
					ll_sure_check.setVisibility(View.VISIBLE);
				}else{
					Toast.makeText(this, "请输入校验码", 0).show();
				}
			}
			break;
		case R.id.ll_simi_add_concel:
			if (showErrTip) {
				// 客服
				Tools.call(this, "01082652086");
			} else {
				finish();
			}
			break;

		}

	}

}
