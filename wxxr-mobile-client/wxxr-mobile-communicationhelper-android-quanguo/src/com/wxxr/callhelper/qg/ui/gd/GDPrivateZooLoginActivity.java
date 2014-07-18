package com.wxxr.callhelper.qg.ui.gd;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.CountTextView;
import com.wxxr.mobile.android.app.AppUtils;
/**
 * 广东版——登录界面
 * 
 * @author cuizaixi
 * 
 */
public class GDPrivateZooLoginActivity extends BaseActivity {
	private EditText et_pwd;
	private EditText et_tel;
	private Button get_pwd;
	private CheckBox cb_agreen;
	private Intent mIntent;
	private Class<Context> redirect_activity;
	protected boolean atlogin=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_private_login);
		findView();
		processLogic();
		if (getIntent().getStringExtra("push") != null) {
			if (AppUtils.getFramework()
					.getService(IUserUsageDataRecorder.class) != null) {
				AppUtils.getFramework()
						.getService(IUserUsageDataRecorder.class)
						.doRecord(ActivityID.LOOKSYSNOTICE.ordinal());
			}
		}
		
		
	}

	private void findView() {
		et_tel = (EditText) findViewById(R.id.et_tel);
		et_pwd = (EditText) findViewById(R.id.et_dynamic_pwd);
		get_pwd = (Button) findViewById(R.id.ctv);
		findViewById(R.id.btn_login).setOnClickListener(this);
		findViewById(R.id.ll_privacy_item).setOnClickListener(this);
		cb_agreen = (CheckBox) findViewById(R.id.cb_agreen_item);

	}

	private void processLogic() {
		et_tel.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() == 0) {
					get_pwd.setBackgroundResource(R.drawable.gd_get_pwd_unclick);
					get_pwd.setClickable(false);
				} else {
					get_pwd.setBackgroundResource(R.drawable.gd_get_pwd_normal);
					get_pwd.setClickable(true);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		get_pwd.setOnClickListener(this);
		mIntent = getIntent();
		redirect_activity = (Class<Context>) mIntent
				.getSerializableExtra(Constant.REDIRECT_ACTIVITY);
	}
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.ll_privacy_item :
				Intent intent4 = new Intent(this,
						GDPrivateZooInformCommonActivity.class);
				intent4.putExtra(Constant.PRIVATE_INFORM_EDIT,
						Constant.PRIVACY_ITEM);
				startActivity(intent4);
				break;
			case R.id.ctv :
				get_pwd.setBackgroundResource(R.drawable.gd_get_pwd_press);
				if (Tools.isValidNumOrPwd(getApplicationContext(), et_tel
						.getText().toString())) {
					IUserActivationService service = getService(IUserActivationService.class);
					service.registerPassword(et_tel.getText().toString(),
							new CMProgressMonitor(
									GDPrivateZooLoginActivity.this) {

								@Override
								protected void handleFailed(Throwable cause) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Tools.showToast(
													getApplicationContext(),
													"无法获得激活密码，请稍后再试。");
										}
									});
								}

								@Override
								protected void handleDone(Object returnVal) {
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											Tools.showToast(
													getApplicationContext(),
													"系统已接收请求，请查收短信");

										}
									});
								}
								@Override
								protected Map<String, Object> getDialogParams() {
									Map<String, Object> map = new HashMap<String, Object>();
									map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
									map.put(DIALOG_PARAM_KEY_MESSAGE,
											"正在向服务器端申请新密码,请稍侯...");
									return map;
								}

							});
				}
				break;
			case R.id.btn_login :
				if (!cb_agreen.isChecked()) {
					Toast.makeText(getApplicationContext(), "请同意使用条款", 1)
							.show();
					return;
				}
				
				String tel = et_tel.getText().toString();
				String pwd = et_pwd.getText().toString();
				if (Tools.isValidNumOrPwd(getApplicationContext(), tel, pwd)) {
					if(atlogin){
						showMessageBox("正在努力登录中,请稍等");
						return ;
					}
					
					
					
					atlogin=true;
					getService(IUserActivationService.class)
					// .activateUser(
							.login(et_tel.getText().toString(),
									et_pwd.getText().toString(),
									new GDCMProgressMonitor(
											GDPrivateZooLoginActivity.this, 0) {

										@Override
										protected void handleFailed(
												final Throwable cause) {
											atlogin=false;
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													if (cause != null
															&& cause instanceof LoginException) {
														LoginException e = (LoginException) cause;
														Tools.showToast(
																getApplicationContext(),
																cause.getMessage());
													} else {
														Tools.showToast(
																getApplicationContext(),
																"登录失败");
													}
												}
											});
										}

										@Override
										protected void handleDone(
												Object returnVal) {
											AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.LOGIN.ordinal());
											atlogin=false;
											runOnUiThread(new Runnable() {
												@Override
												public void run() {
													Toast.makeText(
															getApplicationContext(),
															"登录成功", 1).show();
													if (redirect_activity != null) {
														startActivity(new Intent(
																GDPrivateZooLoginActivity.this,
																redirect_activity));
													}
													finish();
												}
											});
										}

										@Override
										protected Map<String, Object> getDialogParams() {
											Map<String, Object> map = new HashMap<String, Object>();
											map.put(DIALOG_PARAM_KEY_TITLE,
													"系统提示：");
											map.put(DIALOG_PARAM_KEY_MESSAGE,
													"正在登录客户端,请稍侯...");
											return map;
										}
									});
				}
				break;
		}
	}
}
