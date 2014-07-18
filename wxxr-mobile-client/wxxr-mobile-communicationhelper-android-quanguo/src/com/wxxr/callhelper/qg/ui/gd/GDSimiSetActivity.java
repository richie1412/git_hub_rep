package com.wxxr.callhelper.qg.ui.gd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.SiMiSuoHomePasswordActivity;

public class GDSimiSetActivity extends BaseActivity {

	private boolean lMRFlag;
	private View iv_private_switch;
	private Application mContext;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getApplication();
		this.lMRFlag = getService(IPrivateSMService.class)
				.isRingBellWhenReceiving() == false;
		setContentView(R.layout.gd_simisuo_set);

		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);

		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);

		((TextView) findViewById(R.id.tv_titlebar_name)).setText("设置");

		findViewById(R.id.gd_simisuo_tipopen).setOnClickListener(this);
		findViewById(R.id.gd_simi_changeemai).setOnClickListener(this);
		findViewById(R.id.gd_simisuo_resetpass).setOnClickListener(this);
		iv_private_switch = findViewById(R.id.gd_simisuo_tipopen);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Toast.makeText(this, "密码修改成功！", Toast.LENGTH_SHORT).show();
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;

		case R.id.gd_simisuo_tipopen:
			if (lMRFlag) {
				iv_private_switch
						.setBackgroundResource(R.drawable.gd_setting_switch_open);
				lMRFlag = false;
				doUpdate(true);
			} else {
				iv_private_switch
						.setBackgroundResource(R.drawable.gd_setting_switch_close);
				lMRFlag = true;
				doUpdate(false);
			}
			break;

		case R.id.gd_simi_changeemai:

			intent = new Intent(this, GDSiMiSuoResetEmail.class);
			startActivity(intent);

			break;

		case R.id.gd_simisuo_resetpass:

			intent = new Intent(mContext, SiMiSuoHomePasswordActivity.class);
			intent.putExtra("editPwd", RESULT_OK);
			startActivityForResult(intent, 100);
			break;

		}

	}

	/**
	 * 私密锁设置对话框
	 * 
	 * @param bool
	 */
	private void doUpdate(final boolean bool) {
		new CMProgressMonitor(this) {
			@Override
			protected void handleFailed(Throwable cause) {
				// log.error("Caught exception when sending SM message", cause);
				// showMessageBox("提醒设置失败，原因：[" + cause.getLocalizedMessage() +
				// "]");
				Toast.makeText(GDSimiSetActivity.this,
						"提醒设置失败，原因：[" + cause.getLocalizedMessage() + "]", 0)
						.show();
			}

			@Override
			protected void handleDone(Object returnVal) {
				// showMessageBox("提醒设置成功");
				Toast.makeText(GDSimiSetActivity.this, "提醒设置成功", 0).show();
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
}
