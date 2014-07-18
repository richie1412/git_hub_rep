/**
 * 
 */
package com.wxxr.callhelper.qg.ui.gd;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.wxxr.callhelper.qg.widget.CMProgressBar;
import com.wxxr.callhelper.qg.widget.CMProgressDialog;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.api.ApplicationFactory;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.ICancellable;

/**
 * 根据广东版需求，将原先的对话框改成转圈形式。
 * 
 * @author cuizaixi
 */
public abstract class GDCMProgressMonitor extends AbstractProgressMonitor {

	private static final Trace log = Trace.register(GDCMProgressMonitor.class);

	public static final String DIALOG_PARAM_KEY_TITLE = "d_title";
	public static final String DIALOG_PARAM_KEY_MESSAGE = "d_message";
	public static final String DIALOG_PARAM_KEY_CANCEL = "d_cancel";
	public static final String DIALOG_PARAM_KEY_OK = "d_ok";
	public static final String DIALOG_PARAM_KEY_ICON = "d_icon";

	private ICancellable task, progess;
	private Context context;
	private int waitTimeInSeconds;// 默认没有延迟，任务启动，就开始转圈
	private Handler handler;

	public GDCMProgressMonitor(Context context, int waitTime) {
		this(context);
		this.waitTimeInSeconds = waitTime;
	}

	public GDCMProgressMonitor(Context context) {
		this.context = context;
		this.handler = new Handler(context.getMainLooper());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#beginTask(int)
	 */
	@Override
	public final void beginTask(int arg0) {

		if (this.waitTimeInSeconds == 0) {
			progess = showProgressBar();
		} else if (this.waitTimeInSeconds < 0) {
			return;
		} else {
			this.task = ApplicationFactory.getInstance().getApplication()
					.invokeLater(new Runnable() {

						public void run() {
							progess = showProgressBar();
						}
					}, waitTimeInSeconds, TimeUnit.SECONDS);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.core.api.AbstractProgressMonitor#done(java.lang.Object)
	 */
	@Override
	public final void done(final Object arg0) {
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (this.progess != null) {
			this.progess.cancel();
			this.progess = null;
		}
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					handleDone(arg0);

				}
			});
		} else {
			handleDone(arg0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.core.api.AbstractProgressMonitor#taskCanceled(boolean)
	 */
	@Override
	public void taskCanceled(final boolean arg0) {
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (this.progess != null) {
			this.progess.cancel();
			this.progess = null;
		}
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					handleConceled(arg0);
				}
			});
		} else {
			handleConceled(arg0);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.core.api.AbstractProgressMonitor#taskFailed(java.lang
	 * .Throwable)
	 */
	@Override
	public void taskFailed(final Throwable e) {
		log.warn("Caught exception when executing task", e);
		if (task != null) {
			task.cancel();
			task = null;
		}
		if (this.progess != null) {
			this.progess.cancel();
			this.progess = null;
		}
		if (context instanceof Activity) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					handleFailed(e);
				}
			});
		} else {
			handleFailed(e);
		}
	}

	protected abstract void handleDone(Object returnVal);

	protected void handleConceled(boolean bool) {

	}

	protected abstract void handleFailed(Throwable cause);
	boolean cancelled = false;
	protected ICancellable showProgressBar() {
		final Dialog[] p = new Dialog[1];

		this.handler.post(new Runnable() {
			@Override
			public void run() {
				boolean isshow=true;
				if(context instanceof Activity){
					isshow=!((Activity)context).isFinishing();
				}
				
				if((!cancelled)&&isshow){
				p[0] = createProgressDialog();
				if (p[0] != null) {
					p[0].show();
				}
				}
			}
		});
		return new ICancellable() {
			
			@Override
			public boolean isCancelled() {
				return cancelled;
			}

			@Override
			public void cancel() {
				if (p[0] != null) {
					handler.post(new Runnable() {
						@Override
						public void run() {
							p[0].dismiss();
						}
					});
				}
				cancelled = true;
			}
		};
	}

	/**
	 * @param p
	 */
	protected Dialog createProgressDialog() {
		CMProgressBar bar = new CMProgressBar(context);
		int resourceID = getResourceID();
		if (resourceID > 0) {
			bar.setDrawable(resourceID);
		}
		return bar;
	}
	// 自定义转圈图片
	protected int getResourceID() {
		return -1;
	}
	protected Map<String, Object> getDialogParams() {
		return null;
	}
	public Future<?> executeOnMonitor(final Callable<Object> task) {
		return ApplicationFactory.getInstance().getApplication().getExecutor()
				.submit(new Runnable() {

					@Override
					public void run() {
						beginTask(-1);
						try {
							Object retVal = task.call();
							done(retVal);
						} catch (Throwable t) {
							taskFailed(t);
						}
					}
				});
	}
}
