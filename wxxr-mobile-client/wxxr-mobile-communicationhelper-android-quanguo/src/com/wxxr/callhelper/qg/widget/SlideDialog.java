package com.wxxr.callhelper.qg.widget;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewManager;
import android.view.WindowManager;
/**
 * 可滑动的对话框(通话录音优化使用)
 * 
 * @author cuizaixi
 * @create 2014-3-20 下午5:29:15
 */
public class SlideDialog implements OnTouchListener, ViewManager {
	private Activity mActivity;
	private ViewGroup mFlibleView;
	private WindowManager.LayoutParams mLayoutParams;
	private WindowManager mWindowManager;
	private int mScaledTouchSlop;
	private onChildClickListener mChildClickListener;
	public SlideDialog(Activity activity, ViewGroup view,
			WindowManager.LayoutParams params) {
		mActivity = activity;
		mFlibleView = view;
		mLayoutParams = params;
	}
	public SlideDialog(Activity activity, ViewGroup view) {
		mActivity = activity;
		mFlibleView = view;
	}
	public void addFlibleView() {
		mWindowManager = mActivity.getWindowManager();
		mScaledTouchSlop = ViewConfiguration.get(mActivity)
				.getScaledTouchSlop();
		if (mFlibleView.getParent() != null) {
			throw new IllegalStateException("要添加的空间已经有父窗口了");
		}
		if (mLayoutParams == null) {
			mLayoutParams = getDefaultParams();
		}
		addView(mFlibleView, mLayoutParams);
		mFlibleView.setOnTouchListener(this);
		dispachClick2Child(mFlibleView);
	}
	/**
	 * 通过递归，将点击监听传递给每一个子控件
	 * 
	 * @param group
	 */
	public void dispachClick2Child(ViewGroup group) {
		int childCount = group.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View childAt = group.getChildAt(i);
			if (childAt instanceof ViewGroup) {
				dispachClick2Child((ViewGroup) childAt);
			}
			if (childAt.isClickable()) {
				childAt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (mChildClickListener != null) {
							mChildClickListener.onChildChick(v);
						}

					}
				});
			}
		}
	}
	/**
	 * 默认布局 位于屏幕的左上方，距离顶端100px位置
	 * 
	 * @return
	 */
	private static WindowManager.LayoutParams getDefaultParams() {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = 0;
		params.y = 100;
		params.format = PixelFormat.RGBA_8888;
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		return params;
	}
	float rawX;
	float rawY;
	int paramX, paramY;
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN :
				rawX = event.getRawX();
				rawY = event.getRawY();
				paramX = mLayoutParams.x;
				paramY = mLayoutParams.y;
				break;
			case MotionEvent.ACTION_MOVE :
				float rawX2 = event.getRawX();
				float rawY2 = event.getRawY();
				float distanceX = rawX2 - rawX;
				int distancey = (int) (rawY2 - rawY);
				// Log.i("Math.abs(distanceX)", Math.abs(distanceX) + "");
				// Log.i("mScaledTouchSlop)", mScaledTouchSlop+"");
				// if (Math.abs(distanceX) < mScaledTouchSlop) {
				// return false;
				// }
				mLayoutParams.x = (int) (distanceX + paramX);
				mLayoutParams.y = (int) (distancey + paramY);
				updateViewLayout(mFlibleView, mLayoutParams);
				break;
		}
		return true;
	}
	@Override
	public void addView(View view, LayoutParams params) {
		mWindowManager.addView(mFlibleView, mLayoutParams);
	}
	@Override
	public void updateViewLayout(View view, LayoutParams params) {
		mWindowManager.updateViewLayout(mFlibleView, mLayoutParams);
	}
	public void removeView() {
		removeView(null);
	}
	@Override
	public void removeView(View view) {
		if (mFlibleView != null) {
			mWindowManager.removeView(mFlibleView);
			mFlibleView = null;
			mWindowManager = null;
			mLayoutParams = null;
		}
	}
	public View getFlibleView() {
		return mFlibleView;
	}
	public void setOnChildClickListener(onChildClickListener l) {
		mChildClickListener = l;
	}
	interface onChildClickListener {
		void onChildChick(View v);
	}
}
