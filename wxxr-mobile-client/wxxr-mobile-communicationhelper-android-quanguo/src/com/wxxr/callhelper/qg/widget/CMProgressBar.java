package com.wxxr.callhelper.qg.widget;

import com.wxxr.callhelper.qg.R;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
/**
 * 自定义 ProgressBar
 * 
 * @since 1.0
 * @author cuizaixi
 * 
 */
public class CMProgressBar extends ProgressDialog {
	private Context mContext;
	private ImageView mIcon;
	private LayoutParams lp;
	private int mSourceId;
	private boolean mCanceledOnTouchOutside;
	private static final int REPEA_TCOUNT = Integer.MAX_VALUE;// 旋转的次数
	public CMProgressBar(Context context) {
		super(context);
		this.mContext = context;
		this.setCanceledOnTouchOutside(false);
		lp = getWindow().getAttributes();
		lp.dimAmount = 0; // 去背景遮盖
		lp.alpha = 1.0f;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setAttributes(lp);
		
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cm_progress_bar);
		mIcon = (ImageView) findViewById(R.id.iv_icon);
		if (mSourceId > 0) {
			mIcon.setBackgroundResource(mSourceId);
		}
		Animation animation = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setRepeatCount(REPEA_TCOUNT);
		animation.setDuration(1000);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());
		mIcon.setAnimation(animation);
		animation.start();
	}
	public void setDrawable(int id) {
		this.mSourceId = id;
	}
}
