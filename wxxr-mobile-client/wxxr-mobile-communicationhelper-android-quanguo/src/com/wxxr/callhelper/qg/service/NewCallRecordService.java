package com.wxxr.callhelper.qg.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewManager;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.platformtools.Log;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.R.drawable;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.bean.MyMediaRecorder;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.CSMainAcitivity;
import com.wxxr.callhelper.qg.ui.CSManagerActvity;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

public class NewCallRecordService extends Service implements OnClickListener {
	private static final Trace log = Trace.register(NewCallRecordService.class);

	public enum CallState {
		going, coming
	}; // 来电，去电

	public enum RecordState {
		run, down
	}; // 正在录音，停止录音

	private OutGoginCallPhoneReceiver outGoginCallPhoneReceiver;
	private TelephonyManager telephonyManager;
	private PhoneStateListener stateListener;
	private MyMediaRecorder recorder;
	private CallingState callingState; // 当前通话的号码，来电的状态
	SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
	private ManagerSP sp;
	private RelativeLayout view;
	private LayoutParams params;
	private ViewManager wManager;
	private ImageView iv_close;
	private Button iv_record;
	private TextView tv_location_region;
	// OutGoginCallPhoneReceiver 和 ring，顺序反了的处理
	public boolean errorOrder = false;
	private int callRecorderSetting;
	private int locationSetting;
	private int cs_setting;
	private int cs_need_show;

	@Override
	public void onCreate() {
		super.onCreate();
		sp = ManagerSP.getInstance(this);
		wManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		// 监听电话状态
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		stateListener = new LocalPhoneStateListener();
		telephonyManager.listen(stateListener,
				PhoneStateListener.LISTEN_CALL_STATE);
		// 监听去电号码
		IntentFilter localIntentFilter = new IntentFilter(
				Intent.ACTION_NEW_OUTGOING_CALL);
		outGoginCallPhoneReceiver = new OutGoginCallPhoneReceiver();
		registerReceiver(outGoginCallPhoneReceiver, localIntentFilter);
		// 录音
		recorder = new MyMediaRecorder();
		recorder.setState(RecordState.down);
	}

	private class LocalPhoneStateListener extends PhoneStateListener {

		public void onCallStateChanged(int state, String incomingNumber) {
			// 获取用户当前设置参数
			initSettingParams();
			if (incomingNumber == null) {
				// Toast.makeText(getApplicationContext(),
				// "你可能由于未办理来电提醒的业务，无法显示相关信息", 1).show();
				return;
			}
			if (TelephonyManager.SIM_STATE_ABSENT == telephonyManager
					.getSimState()) {
				// Toast.makeText(getApplicationContext(),
				// "SIM卡状态未知，无法使用通信助手相关功能", 1).show();
				return;
			}
			switch (state) {
				case TelephonyManager.CALL_STATE_IDLE :// 0
					stopRecord();
					showCSdialog();
					sp.update(Constant.CS_NEED_SHOW, 0);
					if (view != null) {
						wManager.removeView(view);
						view = null;
					}
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK :// 2
					showView();
					if (callingState == null) {
						errorOrder = true;
						return;
					}
					preformLogic(callingState.phoneNumber, "offhook");
					break;
				case TelephonyManager.CALL_STATE_RINGING :// 1
					setCallingState(new CallingState(incomingNumber,
							CallState.coming));
					showView();
					preformLogic(incomingNumber, "ring");
					break;
			}
		}
	}
	private void showCSdialog() {
		if (callingState == null) {
			return;
		}
		if (callingState.getPhoneNumber() == null) {
			return;
		}

		if (cs_setting == 1) {
			return;
		}
		if (cs_need_show == 1) {
			return;
		}
		sp.update(Constant.CS_NEED_SHOW, 0);
		Intent intent = new Intent(NewCallRecordService.this,
				CSManagerActvity.class);
		intent.putExtra(CSManagerActvity.CS_BEAN, new ComSecretaryBean(0,
				callingState.getPhoneNumber(), 0, 0, 0));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(intent);
	}
	public class OutGoginCallPhoneReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
				String outNumber = getResultData();
				// 设置去电的号码
				callingState = new CallingState(Tools.checking(outNumber),
						CallState.going);
				setCallingState(callingState);
				if (log.isDebugEnabled()) {
					log.debug("------outNumber -------- " + outNumber);
				}
				if (errorOrder) {
					errorOrder = false;
					stateListener.onCallStateChanged(
							TelephonyManager.CALL_STATE_OFFHOOK, outNumber);
				}

			}

		}

	}

	// 界面显示
	private void showView() {
		if (locationSetting == 1 && callRecorderSetting == 1) {
			if (log.isDebugEnabled()) {
				log.debug("用户未打开录音和归属地显示");
			}
			return;
		}
		if (view == null) {
			view = (RelativeLayout) View.inflate(NewCallRecordService.this,
					R.layout.location_record_float_window, null);
			params = new WindowManager.LayoutParams();
			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
			params.gravity = Gravity.TOP | Gravity.LEFT;
			params.x = 0;
			params.y = 100;
			params.format = PixelFormat.RGBA_8888;
			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
			params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
					| LayoutParams.FLAG_NOT_FOCUSABLE;
			wManager.addView(view, params);
		}
		iv_record = (Button) view.findViewById(R.id.btn_record);
		// if (callRecorderSetting == 1) {
		// iv_record.setVisibility(View.GONE);
		// } else {
		// iv_record.setVisibility(View.VISIBLE);
		// }
		iv_close = (ImageView) view.findViewById(R.id.iv_close);
		iv_record.setOnClickListener(this);
		iv_close.setOnClickListener(this);
		tv_location_region = (TextView) view.findViewById(R.id.tv_region);
		view.setOnTouchListener(new OnTouchListener() {
			int lastX, lastY;
			int paramX, paramY;

			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN :
						lastX = (int) event.getRawX();
						lastY = (int) event.getRawY();
						paramX = params.x;
						paramY = params.y;
						break;
					case MotionEvent.ACTION_MOVE :
						int dx = (int) event.getRawX() - lastX;
						int dy = (int) event.getRawY() - lastY;
						params.x = paramX + dx;
						params.y = paramY + dy;
						wManager.updateViewLayout(view, params);
						break;
				}
				return true;
			}
		});
	}

	/**
	 * 获得用户设定的录音及归属地参数 callRecorderSetting 0，开启录音 1，关闭录音 autoOrHandSetting0，自动录音
	 * 1，手动录音 locationSetting 0显示归属地 1，关闭归属地
	 */
	private void initSettingParams() {
		callRecorderSetting = sp.get(Constant.CALLRECORDER_OPEN_CLOSE, 0);
		locationSetting = sp.get(Constant.LOCATION_PHONE, 0);
		cs_setting = sp.get(Constant.CS_SETTING_KEY, 1);
		cs_need_show = sp.get(Constant.CS_NEED_SHOW, 0);
	}

	// 全国版——处理用户选择后的逻辑。
	// private void preformLogic(String number, String phoneState) {
	// if (phoneState.equals("offhook")) {
	// if (callingState == null) {
	// errorOrder = true;
	// return;
	// }
	// if (callRecorderSetting == 1) {
	// iv_record.setBackgroundResource(R.drawable.not_record);
	// iv_record.setClickable(false);
	// } else {
	// if (autoOrHandSetting == 1) {
	// iv_record.setBackgroundResource(R.drawable.start_record);
	// iv_record.setClickable(true);
	// } else if (autoOrHandSetting == 0) {
	// iv_record.setBackgroundResource(R.drawable.not_record);
	// iv_record.setClickable(false);
	// startRecord();
	// }
	// }
	// if (log.isDebugEnabled()) {
	// log.debug("CALL_STATE_OFFHOOK");
	// }
	// Log.i("CALL_STATE", "CALL_STATE_OFFHOOK");
	// } else if (phoneState.equals("ring")) {
	// // 有电话进入才会出现响铃状态，在没有接通电话前默认不用录音
	// iv_record.setBackgroundResource(R.drawable.not_record);
	// iv_record.setClickable(false);
	// if (log.isDebugEnabled()) {
	// log.debug("CALL_STATE_RING");
	// }
	// Log.i("CALL_STATE", "CALL_STATE_RING");
	// }
	// if (locationSetting == 1) {
	// tv_location_region.setVisibility(View.GONE);
	// return;
	// }
	// String realNumber = Tools.checking(number);
	// Region r = ((ApplicationManager) getApplication()).getFramework()
	// .getService(IGuiShuDiService.class)
	// .getRegionByDialogNumber(realNumber);
	// if (r != null) {
	// tv_location_region.setText(r.getpRegionName() + " "
	// + r.getRegionName());
	// if (log.isDebugEnabled()) {
	// log.debug(r.getRegionName());
	// }
	// } else {
	// tv_location_region.setText("未知");
	// }
	//
	// }
	// 广东版——处理用户选择后的逻辑。
	private void preformLogic(String number, String phoneState) {
		if (locationSetting == 1 && callRecorderSetting == 1) {
			if (log.isDebugEnabled()) {
				log.debug("用户未打开录音和归属地显示");
			}
			return;
		}
		if (phoneState.equals("offhook")) {
			if (callRecorderSetting == 1) {
				iv_record.setVisibility(View.GONE);
			} else {
				iv_record.setBackgroundResource(R.drawable.start_record);
				iv_record.setClickable(true);
			}
			if (log.isDebugEnabled()) {
				log.debug("CALL_STATE_OFFHOOK");
			}
		} else if (phoneState.equals("ring")) {
			// 有电话进入才会出现响铃状态，在没有接通电话前默认不用录音
			// iv_record.setBackgroundResource(R.drawable.not_record);
			// iv_record.setClickable(false);
			if (log.isDebugEnabled()) {
				log.debug("CALL_STATE_RING");
			}
		}
		if (locationSetting == 1) {
			tv_location_region.setVisibility(View.GONE);
			return;
		}
		String realNumber = Tools.checking(number);
		String r = ((ApplicationManager) getApplication()).getFramework()
				.getService(IAdvaceRegionQueryService.class)
				.getRegionWithoutBrand(realNumber);
		if (r != null) {
			tv_location_region.setText(r);
			if (log.isDebugEnabled()) {
				log.debug(r);
			}
		} else {
			tv_location_region.setText("未知");
		}

	}

	public void startRecord() {
		if (RecordState.run.equals(recorder.getState())) {
			return;
		}
		recorder.reset();

		if (log.isDebugEnabled()) {
			log.debug("------startRecord--------");
		}
		recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// 设置临时文件
		// String phoneNumber= "未知" + "_" + "going";
		recorder.setDate(new Date());
		File f = AppUtils.getService(ICallRecorderService.class)
				.generateOutputFile(
						callingState.getPhoneNumber(),
						callingState.state == CallState.coming
								? ICallRecorderService.CALL_TYPE_INCOMING
								: ICallRecorderService.CALL_TYPE_OUTGOING,
						recorder.date);
		recorder.targetFile = f;
		try {
			String fileName = f.getCanonicalPath() + ".tmp";
			recorder.setFileName(fileName);
			recorder.setOutputFile(fileName);
			recorder.prepare();
			recorder.start();
			recorder.setState(RecordState.run);
		} catch (Exception e) {
			log.error(" recorder.startRecord() error!", e);
		}

	}

	public void stopRecord() {
		if (RecordState.down.equals(recorder.getState())) {
			return;
		}
		if (log.isDebugEnabled()) {
			log.debug("------stopRecord--------");
		}
		try {
			recorder.stop();
			recorder.reset();
			recorder.setState(RecordState.down);
			// rename File
			File f = new File(recorder.getFileName());
			f.renameTo(recorder.targetFile);
			AppUtils.getFramework().invokeLater(new Runnable() {

				@Override
				public void run() {
					AppUtils.getService(ICallRecorderService.class)
							.refreshTracks();
				}
			}, 100, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			log.error(" recorder.stop() error!", e);
		}
		Toast.makeText(this, "录音结束，登录通信助手收听", 1).show();
	}
	public CallingState getCallingState() {
		return callingState;
	}

	public void setCallingState(CallingState callingState) {
		this.callingState = callingState;
	}

	// 当前来电去电的状态
	private class CallingState {
		String phoneNumber;
		CallState state;

		public CallingState(String ph, CallState s) {
			phoneNumber = ph;
			state = s;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public CallState getState() {
			return state;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_close :
				if (view != null) {
					wManager.removeView(view);
					view = null;
					stopRecord();
				}
				break;
			case R.id.btn_record :
				if (recorder != null) {
					if (recorder.getState().equals(RecordState.down)) {
						if (log.isDebugEnabled()) {
							log.debug("---callrecord_stop---icon-----");
						}
						iv_record.setBackgroundResource(R.drawable.stop_record);
						startRecord();
					} else if (recorder.getState().equals(RecordState.run)) {
						if (log.isDebugEnabled()) {
							log.debug("---callrecord_play---icon-----");
						}
						iv_record
								.setBackgroundResource(R.drawable.start_record);
						stopRecord();
					}
				}
				break;
			default :
				break;
		}
	}

	@Override
	public void onDestroy() {
		if (log.isDebugEnabled()) {
			log.debug("------onDestroy --------");
		}
		// 监听电话
		telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_NONE);
		this.unregisterReceiver(outGoginCallPhoneReceiver);
		recorder.reset();
		recorder = null;
		outGoginCallPhoneReceiver = null;
		if (view != null) {
			wManager.removeView(view);
			view = null;
		}
		startService(new Intent(this, NewCallRecordService.class));
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
