package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.wxxr.callhelper.qg.ui.gd.GDCMProgressMonitor;
import com.wxxr.callhelper.qg.utils.StringUtil;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;
/**
 * 消息加载模板
 * 
 * @author cuizaixi
 * 
 */

public abstract class IGetGuessLikeDetailModule {
	protected List<String> mLocalMessage;
	protected List<String> mRomoteMessage;
	protected List<String> mDisplayMessage;
	protected Activity mActivity;
	protected abstract List<String> getLocalMessage();
	protected abstract ISPCache<String> getService();
	protected abstract View getNetWorkNotAvalibleView();
	protected abstract View getCurrentView();
	protected abstract View getNoMessageView();
	protected abstract void setData(List<String> message);
	protected IGetGuessLikeDetailModule(Activity activity) {
		mDisplayMessage = new ArrayList<String>();
	}
	public void getMessage() {
		mLocalMessage = new ArrayList<String>();
		if (getLocalMessage() == null) {
			if (!netWorkAvaliable()) {
				getNetWorkNotAvalibleView().setVisibility(View.VISIBLE);
				getCurrentView().setVisibility(View.GONE);
				return;
			}
		} else {
			mLocalMessage = getLocalMessage();
			mDisplayMessage.addAll(mLocalMessage);
			setData(mDisplayMessage);
		}
		getRomoteMessage();
	}
	private boolean netWorkAvaliable() {
		//
		return false;
	}
	protected void getRomoteMessage() {
		GDCMProgressMonitor monitor = new GDCMProgressMonitor(mActivity) {

			@Override
			protected void handleFailed(Throwable cause) {
				String showMessage = getShowMessage();
				if (!StringUtil.isEmpty(showMessage)) {
					Toast.makeText(mActivity, showMessage, 1).show();
				}
				if (cause instanceof NullPointerException) {
					// ?????????????
				} else if (mLocalMessage == null) {
					getNoMessageView().setVisibility(View.VISIBLE);
				}
			}

			@Override
			protected void handleDone(Object returnVal) {
				if (returnVal == null) {
					@SuppressWarnings("unchecked")
					List<String> val = (List<String>) returnVal;
					mDisplayMessage.removeAll(mLocalMessage);
					mDisplayMessage.addAll(val);
					setData(mDisplayMessage);
				}
			}
		};
		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getService();
			}
		});
	};
	protected void onRefreshAction() {
		getRomoteMessage();
	};
	protected String getShowMessage() {
		return "";
	}
}
