package com.wxxr.callhelper.qg.widget;

import com.wxxr.callhelper.qg.R;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * 自定义的一个可以显示一分钟倒计时的TextView;
 * 
 * @author cuizaixi
 * 
 */
public class CountTextView extends TextView {
	private int mCount;
	private  CharSequence mTitle;
	private OnCountCompleteListener mCompleteListener;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			mCount = msg.arg1;
			if (mCount == 0) {
				if (mCompleteListener!=null) {
					mCompleteListener.onCountComplete();
				}
				CountTextView.this.setText(mTitle);
				CountTextView.this
						.setBackgroundResource(R.drawable.private_zoo_dynamic_pwd);
				CountTextView.this.setEnabled(true);
			} else {
				CountTextView.this
						.setBackgroundResource(R.drawable.user_authcode_unenable);
				CountTextView.this.setTextColor(getResources().getColor(
						R.color.user_authcode_unenable_text));
				CountTextView.this.setText(mCount + "秒");
			}

		};
	};

	public CountTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setGravity(Gravity.CENTER);
	}

	public void setTitle(CharSequence title) {
		this.mTitle=title;
	}

	public void setOnCountCompleteListener(OnCountCompleteListener listener) {
		this.mCompleteListener = listener;
	}

	// 倒数完成的监听，可以在倒数完成后选择相对应的操作
	public interface OnCountCompleteListener {
		void onCountComplete();
	};

	public void countDown() {
		this.setText("59秒");
		this.setEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {

				int num = 60;

				while (num > 0) {

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					num = num - 1;

					Message msg = new Message();
					msg.arg1 = num;

					mHandler.sendMessage(msg);
				}

			}
		}).start();
	}

}
