package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.IComHelperFramework;
import com.wxxr.callhelper.qg.ui.ApplicationManager;

/**
 * 首页
 * 
 */
public class GDHomeActivity  extends FragmentActivity {
	private ViewPager mPager;// 页卡内容
	private List<Fragment> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView tab1_img, tab2_img, tab3_img, tab4_img,tab1_text, tab2_text, tab3_text, tab4_text;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_activity_main);
		InitTextView();
		InitViewPager();
		InitImageView();
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		tab1_img = (TextView) findViewById(R.id.gd_tabhome_img);
		tab2_img = (TextView) findViewById(R.id.gd_tabnews_img);
		tab3_img = (TextView) findViewById(R.id.gd_tabsquare_img);
		tab4_img = (TextView) findViewById(R.id.gd_tabperson_img);
		
		tab1_text = (TextView) findViewById(R.id.gd_tabhome_txt);
		tab2_text = (TextView) findViewById(R.id.gd_tabnews_txt);
		tab3_text = (TextView) findViewById(R.id.gd_tabsquare_txt);
		tab4_text = (TextView) findViewById(R.id.gd_tabperson_txt);
		
		
		setSelect(1);
		
		
		tab1_img.setOnClickListener(new MyOnClickListener(0));
		tab2_img.setOnClickListener(new MyOnClickListener(1));

		tab3_img.setOnClickListener(new MyOnClickListener(2));
		tab4_img.setOnClickListener(new MyOnClickListener(3));
	}

	private void setSelect(int i) {

		tab1_img.setSelected(i==1);
		tab2_img.setSelected(i==2);
		tab3_img.setSelected(i==3);
		tab4_img.setSelected(i==4);
		Resources  res=getResources();
		int sel=res.getColor(R.color.gd_home_tabfont_sel);
		int unsel=res.getColor(R.color.gd_home_tabfont);
		tab1_text.setTextColor(i==1? sel:unsel);
		tab2_text.setTextColor(i==2? sel:unsel);
		tab3_text.setTextColor(i==3? sel:unsel);
		tab4_text.setTextColor(i==4? sel:unsel);
		
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		
		mPager.setOnTouchListener(new View.OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});

		
		listViews = new ArrayList<Fragment>();
		LayoutInflater mInflater = getLayoutInflater();

		listViews.add(new FragmentMain());
		listViews.add(new FragmentNews());
	///	listViews.add(new FragmentSquare());
		listViews.add(new FragmentPersonCenter());

		mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), listViews));
		mPager.setOffscreenPageLimit(0);
		// mPager.set
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	private float position_one;
	private float position_two;

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		private TranslateAnimation animation;

		@Override
		public void onPageSelected(int select) {

			if (currIndex == select) {
				return;
			}

			if (currIndex == 0) {
				animation = new TranslateAnimation(offset, position_one
						* select, 0, 0);
			} else {
				animation = new TranslateAnimation(position_one * currIndex,
						position_one * select, 0, 0);
			}

			currIndex = select;
			animation.setFillAfter(true);
			animation.setDuration(100);
			cursor.startAnimation(animation);
			setSelect(currIndex+1);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		int screensize = 4;
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.user)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / screensize - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置

		offset = (int) ((screenW / screensize - 0) / 2);
		position_one = (int) (screenW / screensize) + 5;
		position_two = (position_one * 2);

	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends FragmentPagerAdapter {
		public List<Fragment> mListViews;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MyPagerAdapter(FragmentManager fm, List<Fragment> mListViews) {
			super(fm);
			this.mListViews = mListViews;
		}

		@Override
		public Fragment getItem(int arg0) {
			return mListViews.get(arg0);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

	}

	/**
	 * 两次退出
	 */
	private long exitTime = 0;
	private List<ImageView> points;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				// this.showToastMessage("再按一次返回键关闭程序", 300);
				Toast.makeText(this, "再按一次返回键关闭程序", 300).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected IComHelperFramework getFramework() {
		return ((ApplicationManager) getApplication()).getFramework();
	}

	protected <T> T getService(Class<T> clazz) {
		return getFramework().getService(clazz);
	}
}
