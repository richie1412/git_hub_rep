package com.wxxr.callhelper.qg.ui.gd;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.db.dao.RemindSettingDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.DXHZSetting;
import com.wxxr.callhelper.qg.ui.ChannelCustomActivity;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 短信回执设置
 * 
 * @author yinzhen
 * 
 */

public class GDSmsHuiZhiSettingActivity extends BaseActivity {

	private ImageView gd_iv_all_open, gd_iv_flashsms, gd_iv_talk,
			gd_iv_noannoy, gd_iv_hz_report;

	private static final Trace log = Trace
			.register(GDSmsHuiZhiSettingActivity.class);

	private RemindSettingDao rsdao;
	private int hz_status;
	private int receivingMode;
	private boolean ltmsMode;
	private boolean noannoyMode;
	private String rSStyle;
	private DXHZSetting hZSetting;
	// private int cacheReceivingMode;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			rsdao.updateHuiZhiStatus(hz_status);
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_hz_setting);

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
		TextView tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("设置");
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);

		findViewById(R.id.gd_ll_interest_info).setOnClickListener(this);// 感兴趣的附加资讯
		gd_iv_all_open = (ImageView) findViewById(R.id.gd_iv_all_open);// 全开模式
		gd_iv_flashsms = (ImageView) findViewById(R.id.gd_iv_flashsms);// 闪信模式
		gd_iv_talk = (ImageView) findViewById(R.id.gd_iv_talk);// 聊天模式
		gd_iv_noannoy = (ImageView) findViewById(R.id.gd_iv_noannoy);// 夜间免打扰
		gd_iv_hz_report = (ImageView) findViewById(R.id.gd_iv_hz_report);// 回执报告

		gd_iv_all_open.setOnClickListener(this);
		gd_iv_flashsms.setOnClickListener(this);
		gd_iv_talk.setOnClickListener(this);
		gd_iv_noannoy.setOnClickListener(this);
		gd_iv_hz_report.setOnClickListener(this);

		rsdao = RemindSettingDao.getInstance(this);

		if (hZSetting == null) {
			hZSetting = new DXHZSetting();
		}

	}

	private void processLogic() {

		getData();
	}

	private void getData() {

		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(final Object result) {
				if (null == result) {
					runOnUiThread(new Runnable() {
						public void run() {
							if (hz_status == 2) {// 回执报告
								setAllModesSwitch(gd_iv_hz_report, 1);
							} else {
								setAllModesSwitch(gd_iv_hz_report, 2);
							}

							if (receivingMode == 3) {// 全开模式
								setAllModesSwitch(gd_iv_all_open, 1);
							} else {
								setAllModesSwitch(gd_iv_all_open, 2);
							}

							if (ltmsMode) {// 聊天模式
								setAllModesSwitch(gd_iv_talk, 1);
							} else {
								setAllModesSwitch(gd_iv_talk, 2);
							}

							if (noannoyMode) {// 免打扰模式
								setAllModesSwitch(gd_iv_noannoy, 1);
							} else {
								setAllModesSwitch(gd_iv_noannoy, 2);
							}

							if (!TextUtils.isEmpty(rSStyle)
									&& rSStyle.equals("2")) {// 回执短信发送方式:1普通短信,2闪电短信
								setAllModesSwitch(gd_iv_flashsms, 1);
							} else {
								setAllModesSwitch(gd_iv_flashsms, 2);
							}
						}
					});
				}
			}

			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在获取模式数据,请稍侯...");
				return map;
			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				hz_status = rsdao.queryHuiZhiSetting();// 回执报告
				receivingMode = getService(IDXHZSettingService.class)
						.getReceivingMode();// 接收模式（全开）
				// cacheReceivingMode = receivingMode;
				ltmsMode = getService(IDXHZSettingService.class).getltms();// 聊天模式
				noannoyMode = getService(IDXHZSettingService.class)
						.getNoannoyMode();// 免打扰模式
				rSStyle = getService(IDXHZSettingService.class)
						.getReceiptSendStyle();// 闪信模式
				return null;
			}
		});
	}

	/**
	 * 控制所有模式开关
	 * 
	 * @param iv
	 * @param value
	 *            1：开启 2：关闭
	 */
	private void setAllModesSwitch(ImageView iv, int value) {
		if (value == 1) {
			// 开启
			iv.setBackgroundResource(R.drawable.gd_setting_switch_open);
		} else {
			// 关闭
			iv.setBackgroundResource(R.drawable.gd_setting_switch_close);
		}
	}

	private void setToast(String tContent) {
		Toast.makeText(this, tContent, 0).show();
	}

	/**
	 * 回执报告设置
	 */
	private void setHZReportStatus() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 设置全开模式
	 */
	private void setAllOpenMode() {
		getService(IDXHZSettingService.class).setReceivingMode(receivingMode,
				new CMProgressMonitor(this) {

					@Override
					protected void handleFailed(Throwable cause) {
						setToast("设置全开模式失败，请稍后再试...");
					}

					@Override
					protected void handleDone(Object returnVal) {
					}
				});
	}

	/**
	 * 闪信模式
	 */
	private void setFlashMsgMode() {
		getService(IDXHZSettingService.class).setReceiptSendStyle(rSStyle,
				new CMProgressMonitor(this) {

					@Override
					protected void handleFailed(Throwable cause) {
						setToast("设置闪信模式失败，请稍后再试...");
					}

					@Override
					protected void handleDone(Object returnVal) {
					}
				});
	}

	/**
	 * 聊天模式
	 */
	private void setTalkMode() {
		getService(IDXHZSettingService.class).setItms(ltmsMode,
				new CMProgressMonitor(this) {

					@Override
					protected void handleFailed(Throwable cause) {
						setToast("设置聊天模式失败，请稍后再试...");
					}

					@Override
					protected void handleDone(Object returnVal) {
					}
				});
	}

	/**
	 * 免打扰模式
	 */
	private void setNoannoy() {
		getService(IDXHZSettingService.class).setNoannoyMode(noannoyMode,
				new CMProgressMonitor(this) {

					@Override
					protected void handleFailed(Throwable cause) {
						setToast("设置免打扰模式失败，请稍后再试...");
					}

					@Override
					protected void handleDone(Object returnVal) {
					}
				});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onStop() {
		setHZReportStatus();
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.gd_iv_titlebar_left :
				finish();
				break;
			case R.id.gd_ll_interest_info :
				record();
				startActivity(new Intent(this, ChannelCustomActivity.class));
				break;
			case R.id.gd_iv_all_open :
				record();
				if (receivingMode == 3) {
					setAllModesSwitch(gd_iv_all_open, 2);
					receivingMode = 2;
				} else {
					setAllModesSwitch(gd_iv_all_open, 1);
					receivingMode = 3;
				}
				setAllOpenMode();
				break;
			case R.id.gd_iv_flashsms :
				record();
				if (!TextUtils.isEmpty(rSStyle) && rSStyle.equals("2")) {
					setAllModesSwitch(gd_iv_flashsms, 2);
					rSStyle = "1";
				} else {
					setAllModesSwitch(gd_iv_flashsms, 1);
					rSStyle = "2";
				}
				setFlashMsgMode();
				break;
			case R.id.gd_iv_talk :
				record();
				if (ltmsMode) {
					setAllModesSwitch(gd_iv_talk, 2);
					ltmsMode = false;
				} else {
					setAllModesSwitch(gd_iv_talk, 1);
					ltmsMode = true;
				}
				setTalkMode();
				break;
			case R.id.gd_iv_noannoy :
				record();
				if (noannoyMode) {
					setAllModesSwitch(gd_iv_noannoy, 2);
					noannoyMode = false;
				} else {
					setAllModesSwitch(gd_iv_noannoy, 1);
					noannoyMode = true;
				}
				setNoannoy();
				break;
			case R.id.gd_iv_hz_report :
				record();
				if (hz_status == 2) {
					setAllModesSwitch(gd_iv_hz_report, 2);
					hz_status = 1;
				} else {
					setAllModesSwitch(gd_iv_hz_report, 1);
					hz_status = 2;
				}
				break;
		}
	}
	private void record() {
		if (getService(IUserUsageDataRecorder.class) != null) {
			getService(IUserUsageDataRecorder.class).doRecord(
					ActivityID.USER_SETTING_INTERNAL.ordinal());
		}
	}

}
