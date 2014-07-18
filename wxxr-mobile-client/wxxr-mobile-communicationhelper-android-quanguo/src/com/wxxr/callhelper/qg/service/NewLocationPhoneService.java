package com.wxxr.callhelper.qg.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.mobile.core.log.api.Trace;

public class NewLocationPhoneService /*extends Service */{
//	private static final Trace log = Trace
//			.register(NewLocationPhoneService.class);
//
//	private TelephonyManager tManager;
//	private PhoneStateListener listener;
//	private WindowManager wManager;
//	private View view;// 当前显示归属地及品牌的view
//	private OutGoginCallPhoneReceiver outGoginCallPhoneReceiver;
//	WindowManager.LayoutParams params;
//	private CloseReceicer closeReceicer;
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		return null;
//	}
//
//	@Override
//	public void onCreate() {
//		tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//		listener = new LocalPhoneStateListener();
//		tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
//		wManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//		IntentFilter localIntentFilter = new IntentFilter(
//				Intent.ACTION_NEW_OUTGOING_CALL);
//		outGoginCallPhoneReceiver = new OutGoginCallPhoneReceiver();
//		registerReceiver(outGoginCallPhoneReceiver, localIntentFilter);
//		closeReceicer = new CloseReceicer();
//		IntentFilter closefilter = new IntentFilter(
//				"com.wxxr.broadcast.receiver.CLOSE");
//		registerReceiver(closeReceicer, closefilter);
//	}
//
//	private class LocalPhoneStateListener extends PhoneStateListener {
//		public void onCallStateChanged(int state, String incomingNumber) {
//			if (log.isDebugEnabled()) {
//				log.debug("state =" + String.valueOf(state)
//						+ ",incomingNumber = " + String.valueOf(incomingNumber));
//			}
//
//			switch (state) {
//			case TelephonyManager.CALL_STATE_IDLE:
//
//				if (view != null) {
//					view.setVisibility(View.INVISIBLE);
//				}
//				break;
//
//			case TelephonyManager.CALL_STATE_OFFHOOK:
//
//				break;
//
//			case TelephonyManager.CALL_STATE_RINGING:
//
////				fillData(incomingNumber);
//
//				break;
//			}
//		}
//
//	}
//
//	private void fillData(String number) {
//		// 如果没有sim卡，则返回
//		if (TelephonyManager.SIM_STATE_READY != tManager.getSimState()) {
//			return;
//		}
//		// 获取是否显示归属地
//		int locationPhone = ManagerSP.getInstance(NewLocationPhoneService.this)
//				.get(Constant.LOCATION_PHONE, 0);
//		Region r = ((ApplicationManager) getApplication()).getFramework()
//				.getService(IGuiShuDiService.class)
//				.getRegionByDialogNumber(number);
//		if (view == null) {
//			view = View.inflate(NewLocationPhoneService.this,
//					R.layout.location_record_float_window, null);
//			params = new WindowManager.LayoutParams();
//			params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//			params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//			params.format = PixelFormat.RGBA_8888;
//			params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// TYPE_VOLUME_OVERLAY;
//			params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//					| LayoutParams.FLAG_NOT_FOCUSABLE;
//			wManager.addView(view, params);
//		}
//		view.setOnTouchListener(new OnTouchListener() {
//			int lastX, lastY;
//			int paramX, paramY;
//
//			public boolean onTouch(View v, MotionEvent event) {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					lastX = (int) event.getRawX();
//					lastY = (int) event.getRawY();
//					paramX = params.x;
//					paramY = params.y;
//					break;
//				case MotionEvent.ACTION_MOVE:
//					int dx = (int) event.getRawX() - lastX;
//					int dy = (int) event.getRawY() - lastY;
//					params.x = paramX + dx;
//					params.y = paramY + dy;
//					wManager.updateViewLayout(view, params);
//					break;
//				}
//				return true;
//			}
//		});
//
//		TextView tv_location_region = (TextView) view
//				.findViewById(R.id.tv_region);
//		if (locationPhone != 0) {
//			tv_location_region.setVisibility(View.GONE);
//		}
//		if (r != null) {
//			tv_location_region.setText(r.getpRegionName() + " "
//					+ r.getRegionName());
//		} else {
//			tv_location_region.setText("未知");
//		}
//	}
//
//	public class OutGoginCallPhoneReceiver extends BroadcastReceiver {
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//				String outNumber = getResultData();
////				fillData(outNumber);
//			}
//		}
//	}
//
//	@Override
//	public void onDestroy() {
//		tManager.listen(listener, PhoneStateListener.LISTEN_NONE);
//		this.unregisterReceiver(outGoginCallPhoneReceiver);
//		outGoginCallPhoneReceiver = null;
//		this.unregisterReceiver(closeReceicer);
//		closeReceicer = null;
//		startService(new Intent(this, NewLocationPhoneService.class));
//		view = null;
//	}
//
//	private class CloseReceicer extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (view != null) {
//				wManager.removeView(view);
//				view = null;
//			}
//		}
//	}
}
