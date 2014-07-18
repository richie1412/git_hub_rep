package com.wxxr.callhelper.qg.widget;

import android.content.Context;	
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;

public class MYGallery extends Gallery
{

	public MYGallery(Context context)
	{
		super(context);
		
	}

	public MYGallery(Context context, AttributeSet attrs)
	{

		super(context, attrs);

	}

	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2)
	{
		return e2.getX() > e1.getX();
	}

	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		int kEvent;
		if (isScrollingLeft(e1, e2))
		{
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		}
		else
		{
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;
	}
	

}
