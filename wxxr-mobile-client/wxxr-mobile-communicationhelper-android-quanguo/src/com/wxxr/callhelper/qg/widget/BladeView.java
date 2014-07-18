package com.wxxr.callhelper.qg.widget;

import com.wxxr.callhelper.qg.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BladeView extends View {
	private OnTextItemClickListener mOnItemClickListener;
	String[] bladeViewSections = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z"};
	String[] newBladeVeiwSections = null;
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;
	private PopupWindow mPopupWindow;
	private TextView mPopupText;
	private Handler handler = new Handler();
	private Context mContext;

	private String bladeViewTitle;
	public BladeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
	}

	public BladeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;

	}

	public BladeView(Context context) {
		super(context);
		this.mContext = context;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (getBladeViewTitle() != null) {
			int length = bladeViewSections.length;
			newBladeVeiwSections = new String[length + 1];
			for (int i = 0; i < length + 1; i++) {
				if (i == 0) {
					newBladeVeiwSections[0] = getBladeViewTitle();
				} else {
					newBladeVeiwSections[i] = bladeViewSections[i - 1];
				}
			}
		} else {
			newBladeVeiwSections = bladeViewSections;
		}
		if (showBkg) {
			canvas.drawColor(Color.parseColor("#00000000"));
		}

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / newBladeVeiwSections.length;
		for (int i = 0; i < newBladeVeiwSections.length; i++) {
			paint.setColor(Color.parseColor("#828282"));
			// paint.setTypeface(Typeface.DEFAULT_BOLD);
			// paint.setFakeBoldText(true);
			paint.setAntiAlias(true);
			paint.setTextSize(mContext.getResources().getDimension(
					R.dimen.blade_view_text_size));
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
			}
			float xPos = width / 2 - paint.measureText(newBladeVeiwSections[i])
					/ 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(newBladeVeiwSections[i], xPos, yPos, paint);
			paint.reset();
		}

	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * newBladeVeiwSections.length);

		switch (action) {
			case MotionEvent.ACTION_DOWN :
				showBkg = true;
				if (oldChoose != c) {
					if (c > 0 && c < newBladeVeiwSections.length) {
						performItemClicked(c);
						choose = c;
						invalidate();
					}
				}

				break;
			case MotionEvent.ACTION_MOVE :
				if (oldChoose != c) {
					if (c > 0 && c < newBladeVeiwSections.length) {
						performItemClicked(c);
						choose = c;
						invalidate();
					}
				}
				break;
			case MotionEvent.ACTION_UP :
				showBkg = false;
				choose = -1;
				dismissPopup();
				invalidate();
				break;
		}
		return true;
	}

	private void showPopup(int item) {
		if (mPopupWindow == null) {
			handler.removeCallbacks(dismissRunnable);
			mPopupText = new TextView(getContext());
			mPopupText.setBackgroundColor(Color.GRAY);
			mPopupText.setTextColor(Color.CYAN);
			mPopupText.setTextSize(50);
			mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			mPopupWindow = new PopupWindow(mPopupText, 100, 100);
		}

		String text = "";
		if (item == 0) {
			text = "#";
		} else {
			text = Character.toString((char) ('A' + item - 1));
		}
		mPopupText.setText(text);
		if (mPopupWindow.isShowing()) {
			mPopupWindow.update();
		} else {
			mPopupWindow.showAtLocation(getRootView(),
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}

	private void dismissPopup() {
		handler.postDelayed(dismissRunnable, 800);
	}

	Runnable dismissRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
			}
		}
	};

	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	public void setOnItemClickListener(OnTextItemClickListener listener) {
		mOnItemClickListener = listener;
	}

	private void performItemClicked(int item) {
		if (mOnItemClickListener != null) {
			mOnItemClickListener.onItemClick(newBladeVeiwSections[item]);
			// showPopup(item);
		}
	}

	public interface OnTextItemClickListener {
		void onItemClick(String s);
	}
	/**
	 * 得到开头
	 * 
	 * @return
	 */
	public String getBladeViewTitle() {
		return bladeViewTitle;
	}
	/**
	 * 设置搜索边框的开头，联系人用#开头，地区选择用“热门开头”；
	 * 
	 * @param bladeViewTitle
	 */
	public void setBladeViewTitle(String bladeViewTitle) {
		this.bladeViewTitle = bladeViewTitle;
		invalidate();
	}
}
