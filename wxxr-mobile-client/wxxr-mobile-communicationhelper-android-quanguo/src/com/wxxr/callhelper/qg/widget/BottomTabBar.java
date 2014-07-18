package com.wxxr.callhelper.qg.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.ui.HomeActivity;
import com.wxxr.mobile.android.app.AppUtils;

public class BottomTabBar extends LinearLayout {
	private ImageView tab1_img, tab2_img, tab3_img, tab4_img;
	private TextView tab1_text, tab2_text, tab3_text, tab4_text;// 页卡头标
	private Activity mActivity;
	private int mindex;
	private TextView siminews;
	public BottomTabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.bottom_tab_bar, this);
		InitTextView();

	}
	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		tab1_img = (ImageView) findViewById(R.id.gd_tabhome_img);
		tab2_img = (ImageView) findViewById(R.id.gd_tabnews_img);
		tab3_img = (ImageView) findViewById(R.id.gd_tabsquare_img);
		tab4_img = (ImageView) findViewById(R.id.gd_tabperson_img);

		tab1_text = (TextView) findViewById(R.id.gd_tabhome_txt);
		tab2_text = (TextView) findViewById(R.id.gd_tabnews_txt);
		tab3_text = (TextView) findViewById(R.id.gd_tabsquare_txt);
		tab4_text = (TextView) findViewById(R.id.gd_tabperson_txt);

		siminews = (TextView) findViewById(R.id.gd_tabhome_simi_new);

		setSelect(1);

		MyOnClickListener one = new MyOnClickListener(0);
		MyOnClickListener two = new MyOnClickListener(1);
		MyOnClickListener three = new MyOnClickListener(2);
		MyOnClickListener four = new MyOnClickListener(3);

		tab1_img.setOnClickListener(one);
		tab2_img.setOnClickListener(two);
		tab3_img.setOnClickListener(three);
		tab4_img.setOnClickListener(four);

		findViewById(R.id.gd_home_tabhome_div).setOnClickListener(one);
		findViewById(R.id.gd_home_tabnews_div).setOnClickListener(two);
		findViewById(R.id.gd_home_tabsquare_div).setOnClickListener(three);
		findViewById(R.id.gd_home_tabperson_div).setOnClickListener(four);

		findViewById(R.id.gd_tabhome_txt).setOnClickListener(one);
		findViewById(R.id.gd_tabnews_txt).setOnClickListener(two);
		findViewById(R.id.gd_tabsquare_txt).setOnClickListener(three);
		findViewById(R.id.gd_tabperson_txt).setOnClickListener(four);

	}

	public void setSelect(int i) {

		tab1_img.setSelected(i == 0);
		tab2_img.setSelected(i == 1);
		tab3_img.setSelected(i == 2);
		tab4_img.setSelected(i == 3);
		Resources res = getResources();
		int sel = res.getColor(R.color.gd_home_tabfont_sel);
		int unsel = res.getColor(R.color.gd_home_tabfont);
		tab1_text.setTextColor(i == 0 ? sel : unsel);
		tab2_text.setTextColor(i == 1 ? sel : unsel);
		tab3_text.setTextColor(i == 2 ? sel : unsel);
		tab4_text.setTextColor(i == 3 ? sel : unsel);

		if (AppUtils.getFramework().getService(IPrivateSMService.class) != null) {
			int size = AppUtils.getFramework()
					.getService(IPrivateSMService.class).getAllUnreadSize();
			if (size != 0) {
				siminews.setVisibility(View.VISIBLE);
				siminews.setText("" + size);
			} else {
				siminews.setVisibility(View.GONE);
			}

		}

	}

	public void setActivtiy(Activity pactivity, int i) {
		mActivity = pactivity;
		mindex = i;
		setSelect(i);
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;
		private int action_id = -1;
		public MyOnClickListener(int i) {
			index = i;
			if (i == 0) {
				action_id = ActivityID.TXZHUSHOU.ordinal();
			} else if (i == 1) {
				action_id = ActivityID.MESSAGE.ordinal();
			} else if (i == 2) {
				action_id = ActivityID.SQUARE.ordinal();
			} else if (i == 3) {
				action_id = ActivityID.SETTING.ordinal();
			}
		}

		@Override
		public void onClick(View v) {
			if (mindex != index) {
				if (action_id != -1
						&& AppUtils.getService(IUserUsageDataRecorder.class) != null) {
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(
							action_id);
				}
				if (mActivity == null) {
					throw new NullPointerException(
							"the  original  acitivity can not be null");
				}
				Intent t = new Intent(mActivity, HomeActivity.class);
				t.putExtra("homeindex", index);
				mActivity.startActivity(t);
				mActivity.finish();
			}
		}
	};
}
