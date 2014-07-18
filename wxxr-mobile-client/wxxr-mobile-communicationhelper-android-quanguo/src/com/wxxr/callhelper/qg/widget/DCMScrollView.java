package com.wxxr.callhelper.qg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DCMScrollView extends ScrollView
{

	public DCMScrollView(Context context)
	{
		super(context);
	}

	public DCMScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	private float mStartX = 0;
	private float mStartY = 0;
	int mOffsetX;
	int mOffsetY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{

		switch (ev.getAction())
		{
		case MotionEvent.ACTION_DOWN:

			mStartX = ev.getX();
			mStartY = ev.getY();

			break;
		case MotionEvent.ACTION_MOVE:

			mOffsetX = (int) Math.abs(ev.getX() - mStartX);
			mOffsetY = (int) Math.abs(ev.getY() - mStartY);

			if (mOffsetX > mOffsetY)
			{
				return false;

			}

			break;

		case MotionEvent.ACTION_UP:

			break;

		}

		return super.onInterceptTouchEvent(ev);

	}

}
