package com.wxxr.callhelper.qg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.Version;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.ICopyOldData;
import com.wxxr.callhelper.qg.ui.gd.GDHomeActivity;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

public class LogoActivity extends BaseActivity {
	private ManagerSP sp;
	private boolean showDirect = false;
	private String updatetip;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int guideValue = sp.get("guide", 0);
			switch (msg.what) {
				case 0 :
					int isFromLoading = sp.get("isFromLoading", 0);
					getService(ICopyOldData.class).setNeedImport(
							isFromLoading == 0);
					if (isFromLoading == 0 || showDirect) {
						startActivity(new Intent(LogoActivity.this,
								PrivateZooGuideActivity.class));
						finish();
					} else if (isFromLoading == 1) {
						Intent intent = new Intent(LogoActivity.this,
								HomeActivity.class);
						intent.putExtra("updatetip", updatetip);
						startActivity(intent);
						finish();
					}
					break;
				case -1 :
					finish();
					break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.logo);
		if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
			AppUtils.getFramework().getService(IUserUsageDataRecorder.class)
					.doRecord(ActivityID.ENTER_CLIENT.ordinal());
		}
		findView();
		updatetip = getIntent().getStringExtra("updatetip");
		processLogic();

	}

	public void findView() {

		sp = ManagerSP.getInstance(this);

		String oldversion = sp.get("location_version", "");
		if (!Version.getVersionNumber().equals(oldversion)) {
			showDirect = true;
		}
		sp.update(Constant.VERSION_STATUS, 0);
		sp.update("location_version", Version.getVersionNumber());
	}

	public void processLogic() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message message = new Message();
				try {
					Thread.sleep(2000);
					message.what = 0;
				} catch (InterruptedException e) {
					message.what = -1;
				} finally {
					handler.sendMessage(message);
				}
			}
		}).start();

	}
}