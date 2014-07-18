/**
 * 
 */
package com.wxxr.callhelper.qg.widget;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author neillin
 *
 */
public interface IPullableView {
	View getView();
	void setOnTouchListener(OnTouchListener listener);
	boolean isPullable(View view,MotionEvent event);
	void pullStarted(View view);
	void pullEnded(View view);
}
