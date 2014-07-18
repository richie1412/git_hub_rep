package com.wxxr.callhelper.qg.widget;

import com.wxxr.callhelper.qg.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class MyLetterListView extends View
{

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	String[] b =
	{ "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
			"V", "W", "X", "Y", "Z" };
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;

	public MyLetterListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public MyLetterListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MyLetterListView(Context context)
	{
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (showBkg)
		{
//			canvas.drawColor(getResources().getColor(R.color.leak_msg_content));
			canvas.drawColor(getResources().getColor(android.R.color.transparent));
		}

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / b.length;
		int screenWidth = canvas.getWidth();
		
		for (int i = 0; i < b.length; i++)
		{
			if (i == choose)
			{
				paint.setColor(getResources().getColor(R.color.contactsLetter_click));
				paint.setFakeBoldText(true);
			}else{
//				paint.setColor(getResources().getColor(R.color.contactsLetter));
				paint.setColor(getResources().getColor(R.color.gd_private_lock_text_press));
				paint.setFakeBoldText(false);
			}
			
		//	paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setTypeface(Typeface.DEFAULT);

			if(screenWidth > 900){
				paint.setTextSize(45);
			}else if(screenWidth > 600){
				paint.setTextSize(32);
			}else if(screenWidth > 400){
				paint.setTextSize(16);
			}
			paint.setAntiAlias(true);
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;//
		final int c = (int) (y / getHeight() * b.length);

		switch (action)
		{
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null)
			{
				if (c > 0 && c < b.length)
				{
					listener.onTouchingLetterChanged(b[c]);// 对象执行接口方法.传入的是随时改变的值,接口对象.接口方法.
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null)
			{
				if (c > 0 && c < b.length)
				{
					listener.onTouchingLetterChanged(b[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
//			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
	{
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener
	{
		public void onTouchingLetterChanged(String s);
	}

}
