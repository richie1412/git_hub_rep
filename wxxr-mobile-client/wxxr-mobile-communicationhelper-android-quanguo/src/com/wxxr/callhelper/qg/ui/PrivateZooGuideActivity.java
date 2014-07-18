package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.utils.ManagerSP;

/**
 * 归属地引导界面
 * 
 * @author cuizaixi
 * 
 */
public class PrivateZooGuideActivity extends BaseActivity {
	public static final String TAG = "PrivateZooGuideActivity";
	final int[] navigation_res = {R.drawable.welcomebj_01,
			R.drawable.welcomebj_02, R.drawable.welcomebj_03};
	private ViewPager viewpager;
	private boolean end;
	private ArrayList<View> pageViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.private_zoo_guide);
		pageViews = new ArrayList<View>();
		for (int i = 0; i < navigation_res.length; i++) {
			LinearLayout out = new LinearLayout(this);
			ImageView a = new ImageView(PrivateZooGuideActivity.this);
			a.setBackgroundResource(navigation_res[i]);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT);
			out.addView(a, params);
			pageViews.add(out);
		}

		viewpager = (ViewPager) findViewById(R.id.shownew);
		end = false;
		viewpager.setOnTouchListener(new OnTouchListener() {
			private float startx;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {

					startx = event.getX();
					if (viewpager.getCurrentItem() == navigation_res.length - 1) {
						end = true;
					} else {
						end = false;
					}
				}

				if (action == MotionEvent.ACTION_UP) {
					if (startx - event.getX() > 100 && end) {
						ManagerSP sp = ManagerSP.getInstance();
						sp.update("isFromLoading", 1);
						Intent intent = null;
						String code = getService(IClientCustomService.class)
								.getProviceCode();
						if (TextUtils.isEmpty(code)) {
							intent = new Intent(PrivateZooGuideActivity.this,
									PrivateZooLocationActivity.class);
							intent.putExtra("from", "guide");
						} else {
							intent = new Intent(PrivateZooGuideActivity.this,
									HomeActivity.class);
						}
						startActivity(intent);
						finish();

					}
				}

				return false;
			}

		});

		ImaAdapter adapter = new ImaAdapter(navigation_res, 0, false, true);
		viewpager.setAdapter(adapter);
		viewpager.setCurrentItem(0);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	public class ImaAdapter extends PagerAdapter {
		private int res[];

		ImaAdapter(int[] pres, int rowindex, boolean ploop, boolean pisnav) {
			res = pres;
		}

		@Override
		public int getCount() {
			return res.length;
		}

		// @Override
		// public Object getItem(int position) {
		// // FIXME getItem
		// return null;
		// }
		//
		// @Override
		// public long getItemId(int position) {
		// // FIXME getItemId
		// return position;
		// }
		//
		// @Override
		// public View getView(int position, View convertView, ViewGroup parent)
		// {
		// // LinearLayout
		// ImageView a = new ImageView(PrivateZooGuideActivity.this);
		// Gallery.LayoutParams params = new
		// Gallery.LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT);
		// a.setLayoutParams(params);
		// a.setBackgroundDrawable(getResources().getDrawable(res[position]));
		// return a;
		// }

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}
}
