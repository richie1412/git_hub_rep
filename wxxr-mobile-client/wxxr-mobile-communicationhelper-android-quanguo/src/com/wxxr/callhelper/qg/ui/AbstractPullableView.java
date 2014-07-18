/**
 * 
 */
package com.wxxr.callhelper.qg.ui;

import com.wxxr.callhelper.qg.widget.IPullableView;

import android.view.View;

/**
 * @author neillin
 *
 */
public abstract class AbstractPullableView implements IPullableView{
	private View currentView;
	private boolean isClickable,focusable,focusableInTouchMode;
	
	@Override
	public void pullStarted(View view) {
		this.currentView = view;
		this.isClickable = view.isClickable();
		this.focusable = view.isFocusable();
		this.focusableInTouchMode = view.isFocusableInTouchMode();
		view.setPressed(false);
		view.setFocusable(false);
		view.setFocusableInTouchMode(false);
		
	}

	@Override
	public void pullEnded(View view) {
		if(this.currentView == view){
			view.setClickable(this.isClickable);
			view.setFocusable(focusable);
			view.setFocusableInTouchMode(focusableInTouchMode);
		}
		this.currentView = null;
	}

}
