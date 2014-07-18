package com.wxxr.callhelper.qg.ui.gd;

import java.util.concurrent.Callable;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IPrivateSimiNetService;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;

public class GDSiMiSuoResetEmail extends BaseActivity {
	private EditText edittext;
	private static boolean flag = true;//控制重复提交的

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_simisuo_changeemail);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);

		findViewById(R.id.gd_iv_titlebar_right).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setBackgroundResource(
				R.drawable.gd_title_right_selector);

		((TextView) findViewById(R.id.tv_titlebar_name)).setText("修改邮箱");
		((TextView) findViewById(R.id.gd_simisuo_nowemail)).setText("已关联："
				+ getService(IPrivateSMService.class).getBindedEmail());
		edittext = (EditText) findViewById(R.id.gd_simisuo_newemail);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		case R.id.gd_iv_titlebar_right:
			if (flag) {
				final String email = edittext.getText().toString();
				if (email.length() == 0) {
					Toast.makeText(this, "请输入邮箱", 0).show();
				} else if (!Tools.isEMailStr(email)) {
					Toast.makeText(this, "输入邮箱格式有误，请重新输入", 0).show();
				} else {
					GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {

						@Override
						protected void handleDone(Object returnVal) {
							if (returnVal instanceof Boolean) {
								if ((Boolean) returnVal) {
									Toast.makeText(getApplicationContext(),
											"邮箱修改成功", 0).show();
									getService(IPrivateSMService.class)
											.setBindedEmail(email);
									flag=true;
									finish();
								} else {
									flag=true;
									Toast.makeText(getApplicationContext(),
											"邮箱修改失败", 0).show();
								}
							}
						}

						@Override
						protected void handleFailed(Throwable cause) {
							Toast.makeText(getApplicationContext(), "邮箱修改失败", 0)
									.show();
							flag = true;
						}

					};

					monitor.executeOnMonitor(new Callable<Object>() {
						@Override
						public Object call() throws Exception {
							flag = false;
							return getService(IPrivateSimiNetService.class)
									.updateEmailBinding(
											getFramework().getDeviceId(), email);
						}
					});
				}
			}

			break;
		}
	}

}
