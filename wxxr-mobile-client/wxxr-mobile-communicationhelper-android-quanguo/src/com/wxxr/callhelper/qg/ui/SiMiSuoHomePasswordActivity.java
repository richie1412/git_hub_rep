package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.gd.GDSimiContactsActvity;
import com.wxxr.mobile.android.app.AppUtils;
/**
 * 
 * 设置私密锁的 密码，1次输入，1次确认
 *
 */
public class SiMiSuoHomePasswordActivity extends FragmentActivity 
{
	private String password;
	private ISimiPasswdSetupContext ctx = new ISimiPasswdSetupContext() {
		
		@Override
		public boolean updatePassword(String newPasswd) {
			if(!password.equals(newPasswd)){
				//两次密码不一致
//				showMessageBox("两次设置的密码不一致,请重新设置");
				Intent intent = new Intent(SiMiSuoHomePasswordActivity.this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.PWD_DIFFERENT);
				intent.putExtra(Constant.DIALOG_CONTENT, "绘制不成功，两次解锁图形不一致，是否重新绘制？");
				startActivityForResult(intent, 100);
				return false;
			}
			doUpdatePassword();
			return true;
		}
		
		@Override
		public void showMessageBox(String message) {
			BaseActivity.showMessageBox(SiMiSuoHomePasswordActivity.this,message);
		}
		
		@Override
		public Activity getActivity() {
			return SiMiSuoHomePasswordActivity.this;
		}
		
		@Override
		public void doPhase1() {
			password = null;
			switchFragement(new SiMiSetPassword1Fragment(this,resetpass), false);
		}
		@Override
		public void doPhase2(String newPasswd) {
			password = newPasswd;
			switchFragement(new SiMiSetPassword2Fragment(this,resetpass), true);
			
		}
		
		@Override
		public void cancelPasswdSetup() {
			setResult(RESULT_CANCELED);
			SiMiSuoHomePasswordActivity.this.finish();
		}
	};
	private boolean resetpass;
	private int value;
	
	private void switchFragement(Fragment newFragment, boolean add2Backstack) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.ll_setpasswd_main, newFragment);
		if(add2Backstack){
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword_activity);
		
		String presetpass=getIntent().getStringExtra("resetpass");
		if(presetpass!=null&&presetpass.length()>0){
			resetpass=true;
		}
		value = getIntent().getIntExtra("editPwd", 0);
		if(value == 1){
			//从检验码进入
			setResult(RESULT_OK);
		}
		
		this.ctx.doPhase1();
	}
	
	private void doUpdatePassword() {
		CMProgressMonitor monitor = new CMProgressMonitor(this) {			
			@Override
			protected void handleFailed(Throwable cause) {
				ctx.showMessageBox("私密锁初始化失败，原因：["+cause.getLocalizedMessage()+"]");
				setResult(RESULT_CANCELED);
				finish();
			}			
			@Override
			protected void handleDone(Object returnVal) {
				
				if(value == RESULT_OK){
					//从修改密码进入
					setResult(RESULT_OK);
					startActivity(new Intent(SiMiSuoHomePasswordActivity.this, GDSimiContactsActvity.class));
					Toast.makeText(SiMiSuoHomePasswordActivity.this, "密码修改成功！", Toast.LENGTH_SHORT).show();
				}
				else{
					startActivity(new Intent(SiMiSuoHomePasswordActivity.this, GDSimiContactsActvity.class));
				}
				finish();
			}
			/* (non-Javadoc)
			 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
			 */
			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在初始化私密锁,请稍侯...");
				return map;
			}
		};
		monitor.executeOnMonitor(new Callable<Object>() {				
			@Override
			public Object call() throws Exception {
				AppUtils.getService(IPrivateSMService.class).setupPassword(password);
				return null;
			}
		});

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode){
		case Constant.PWD_DIFFERENT://两次密码不一致，点击取消重置密码
			finish();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
