package com.wxxr.callhelper.qg.framework;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.IComHelperFramework;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.TwoSecondDialogActivity;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.IServiceAvailableCallback;

public abstract class BaseActivity extends Activity implements OnClickListener {
	
	private AtomicInteger seqNo = new AtomicInteger(10000);
	private Map<Integer, IProgressMonitor> monitors = new ConcurrentHashMap<Integer, IProgressMonitor>();

	@Override
	public void onClick(View v) {

	}
	public static void showMessageBox(Activity activity, String message){
		Intent intent = new Intent(activity, TwoSecondDialogActivity.class);
		intent.putExtra(Constant.DIALOG_MESSAGE, message);
		activity.startActivity(intent);

	}

	protected IComHelperFramework getFramework() {
		return ((ApplicationManager) getApplication()).getFramework();
	}

	protected <T> T getService(Class<T> clazz) {
		return getFramework().getService(clazz);
	}

	protected void callLater(final Runnable task, long delay, TimeUnit unit) {
		getFramework().invokeLater(task, delay, unit);
	}

	protected <T> void callWhenServiceReady(Class<T> serviceInterface,
			final Runnable task) {
		getFramework().checkServiceAvailable(serviceInterface,
				new IServiceAvailableCallback<T>() {

					@Override
					public void serviceAvailable(T service) {
						task.run();
					}
				});
	}

	public static void showMessageBox(Activity activity,int code) {
//		Intent intent = new Intent(activity, TwoSecondDialogActivity.class);
//		intent.putExtra(Constant.DIALOG_KEY, code);
//		activity.startActivity(intent);
	}
	
	protected void showMessageBox(int code) {
//		showMessageBox(this,code);
	}


	protected void showMessageBox(String message) {
//		showMessageBox(this,message);
	}

	protected void showMessageBox(int interactiveMode,String message, IProgressMonitor monitor) {
//		Intent intent = new Intent(this, TwoSecondDialogActivity.class);
//		intent.putExtra(Constant.DIALOG_MESSAGE, message);
//		intent.putExtra(Constant.DIALOG_INTERACTIVE_MODE, interactiveMode);
//		int requestCode = this.seqNo.getAndIncrement();
//		this.monitors.put(requestCode, monitor);
//		startActivityForResult(intent, requestCode);
	}

	protected void showActivity4Result(Class<?> activityClass,
			Map<String, String> params, IProgressMonitor monitor) {
		Intent intent = new Intent(this, activityClass);
		if (params != null) {
			for (Entry<String, String> entry : params.entrySet()) {
				intent.putExtra(entry.getKey(), entry.getValue());
			}
		}
		int requestCode = this.seqNo.getAndIncrement();
		this.monitors.put(requestCode, monitor);
		startActivityForResult(intent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IProgressMonitor monitor = this.monitors.remove(requestCode);
		if (monitor != null) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				monitor.done(data);
				break;
			case Activity.RESULT_CANCELED:
				monitor.taskCanceled(true);
				break;
			}
		}
	}

	protected void showToastMessage(CharSequence text, int duration) {
//		// Constant.DIALOG_MESSAGE, message
//		Toast result = new Toast(this);
//		LayoutInflater inflate = (LayoutInflater) this
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View v = inflate.inflate(R.layout.two_second_dialog, null);
//		TextView tv_dialog = (TextView) v.findViewById(R.id.tv_dialog);
//		ImageView alert_icon = (ImageView) v.findViewById(R.id.alert_icon);
//		TextView alert_title = (TextView) v.findViewById(R.id.alert_title);
//		((LinearLayout) v.findViewById(R.id.ll_btn_footer1)).setVisibility(View.GONE);
//		((LinearLayout) v.findViewById(R.id.ll_btn_footer2)).setVisibility(View.GONE);
//		tv_dialog.setText(text);
//		alert_icon.setBackgroundResource(R.drawable.dialog_icon_msg);
//		alert_title.setText("提  示");
//		result.setView(v);
//		result.setGravity(Gravity.CENTER, 0, 0);
//		result.setDuration(duration);
//		result.show();
	}
	
	protected PopupWindow getMenu(Context context, View view) {
		PopupWindow pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setAnimationStyle(R.style.pop_anim_style);
		return pop;
	}
	
	protected void setWebViewAttribute(WebView engine, Trace log){
		engine.getSettings().setJavaScriptEnabled(true);
		engine.getSettings().setBuiltInZoomControls(false);
//		engine.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
		// engine.getSettings().setLoadsImagesAutomatically(true);
		engine.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		engine.setInitialScale(1);

//		engine.getSettings().setDomStorageEnabled(true);
		// Set cache size to 8 mb by default. should be more than enough
//		engine.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
//		String appCachePath = this.getApplicationContext()
//				.getDir("cache", Context.MODE_PRIVATE).getPath();
//		engine.getSettings().setAppCachePath(appCachePath);
//		engine.getSettings().setAllowFileAccess(true);
//		engine.getSettings().setAppCacheEnabled(true);
//		IDataExchangeCoordinator cm = getService(IDataExchangeCoordinator.class);

//		if (cm.checkAvailableNetwork() < 0) {
//			engine.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
//			if (log.isDebugEnabled()) {
//				log.debug("set cache mode :LOAD_CACHE_ONLY");
//			}
//		} else {
//			engine.getSettings().setCacheMode(WebSettings.LOAD_NORMAL);
//			if (log.isDebugEnabled()) {
//				log.debug("set cache mode :LOAD_NORMAL");
//			}
//		}
	}

}
