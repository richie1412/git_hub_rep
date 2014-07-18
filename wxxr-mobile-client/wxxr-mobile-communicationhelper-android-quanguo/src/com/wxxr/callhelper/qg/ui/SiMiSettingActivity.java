package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.mobile.core.log.api.Trace;

public class SiMiSettingActivity extends BaseActivity implements
		OnClickListener {

	private static final Trace log = Trace.register(SiMiSettingActivity.class);

	ImageView iv_kaiguan;
	ImageView iv_finish;
	boolean lMRFlag = true;
	TextView tv_update;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.simiren_setting_dialog);

		iv_kaiguan = (ImageView) findViewById(R.id.iv_simi_kaiguan);
		tv_update = (TextView) findViewById(R.id.tv_update);

		iv_kaiguan.setOnClickListener(this);
		tv_update.setOnClickListener(this);
		try {
			this.lMRFlag = getService(IPrivateSMService.class)
					.isRingBellWhenReceiving() == false;
			if (this.lMRFlag) {
				iv_kaiguan.setBackgroundResource(R.drawable.close);
			} else {
				iv_kaiguan.setBackgroundResource(R.drawable.open);
			}
		} catch (Throwable t) {

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_simi_kaiguan:
			if (lMRFlag) {
				iv_kaiguan.setBackgroundResource(R.drawable.open);
				lMRFlag = false;
				doUpdate(true);
			} else {
				iv_kaiguan.setBackgroundResource(R.drawable.close);
				lMRFlag = true;
				doUpdate(false);
			}

			break;
		case R.id.iv_finish:

			finish();
			break;
		case R.id.tv_update:

			Intent intent = new Intent();
			// intent.putExtra("from_simi", "update_password");
			intent.setClass(SiMiSettingActivity.this,
					SiMiSuoHomePasswordActivity.class);
			intent.putExtra("resetpass","yes");
			startActivity(intent);
		//	finish();

			break;

		}

	}

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

}
