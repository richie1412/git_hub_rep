package com.wxxr.callhelper.qg.ui;

import java.util.List;

import android.content.Intent;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IComHelperFramework;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.service.ICopyOldData;
import com.wxxr.callhelper.qg.service.IHomeNotice;
import com.wxxr.callhelper.qg.ui.gd.FragmentMain;
import com.wxxr.callhelper.qg.ui.gd.FragmentNews;
import com.wxxr.callhelper.qg.ui.gd.FragmentPersonCenter;
import com.wxxr.callhelper.qg.ui.gd.FragmentSquareNew;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.MyAlertDialog;
import com.wxxr.callhelper.qg.widget.BottomTabBar;
import com.wxxr.mobile.android.app.AppUtils;

/**
 * 首页
 * 
 * @author yinzhen
 * 
 */
public class HomeActivity extends FragmentActivity implements OnClickListener {
	private ViewPager mPager;// 页卡内容
	private List<Fragment> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片

	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度

	FragmentMain main;
	FragmentNews news;
	FragmentPersonCenter personcenter;
	FragmentSquareNew square;
	private BottomTabBar bottombar;
	
	private ManagerSP sp;
	

	private ReaderPanelContext readerCtx = new ReaderPanelContext() {

		@Override
		public void onPushMsgClicked(HtmlMessageBean bean) {

		}

		@Override
		public void closeFragement(Fragment fragement) {
		}

		@Override
		public void showTitle() {
		}

		@Override
		public void hideTitle() {
		}
	};
	private int selindex;
	private View cheadiv;
	private View cheapdivline;
	private Object TextView;
	private TextView cheaptitleView;
	private String updatetip;

	
	protected void onResume() {
		super.onResume();
		checkVersion();
		bottombar.setSelect(selindex);
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_activity_main);
		selindex = getIntent().getIntExtra("homeindex", 0);
		
		cheadiv=findViewById(R.id.ll_minimsg_21city_cheap);
		cheapdivline=findViewById(R.id.cheapdivline);
		cheaptitleView = (TextView)findViewById(R.id.ll_minimsg_21city_cheap_txt);
		sp=sp.getInstance();
		main = new FragmentMain();
		news = new FragmentNews();
		personcenter = new FragmentPersonCenter();
		square = new FragmentSquareNew();
		square.setViews(cheadiv,cheapdivline,cheaptitleView);
		// ((BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this,0);

		// ((BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this,0);

		bottombar = (BottomTabBar) findViewById(R.id.home_bottom_tabbar);
		setSelectTab(selindex);
		findViewById(R.id.gd_tabhome_img).setOnClickListener(this);
		findViewById(R.id.gd_tabnews_img).setOnClickListener(this);
		findViewById(R.id.gd_tabsquare_img).setOnClickListener(this);
		findViewById(R.id.gd_tabperson_img).setOnClickListener(this);
		
		findViewById(R.id.gd_home_tabhome_div).setOnClickListener(this);
		findViewById(R.id.gd_home_tabnews_div).setOnClickListener(this);
		findViewById(R.id.gd_home_tabsquare_div).setOnClickListener(this);
		findViewById(R.id.gd_home_tabperson_div).setOnClickListener(this);
		
		
		findViewById(R.id.gd_tabperson_txt).setOnClickListener(this);
		findViewById(R.id.gd_tabsquare_txt).setOnClickListener(this);
		findViewById(R.id.gd_tabnews_txt).setOnClickListener(this);
		findViewById(R.id.gd_tabhome_txt).setOnClickListener(this);
		
		
		
		

		// if (ManagerSP.getInstance().get("isFromLoading", 0)==0) {
		getService(ICopyOldData.class).importolddata();
		// // FirstStartPreferences.getInstance(this).setFirstInHome("nono");
		// }

	
	
		
		if(getIntent().getStringExtra("push")!=null){
			if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
				AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.LOOKSYSNOTICE.ordinal());
			}
		}
	
//		NotifyMessage bb=	NoticeDao.getInstance(this).fetchDataByType(1);
//		NotifyMessage cc=	NoticeDao.getInstance(this).fetchDataByType(2);
		
//	    if(cc!=null){
//	    	cc.setTitle("aaa");
//		Tools.showNitify(this,cc);
//	    }
		updatetip=getIntent().getStringExtra("updatetip");
	
	}
	
	private void checkVersion() {
		int year = sp.get("year", 0);
		int month = sp.get("month", 0);
		int day = sp.get("day", 0);
		if (sp.hasUpdatedApp()) {
			new MyAlertDialog(this).isFirstEntry(year, month, day);
		}else if(updatetip!=null){
			Toast.makeText(this, updatetip, 1).show();
			updatetip=null;
		}
	}

	private void setSelectTab(int selindex) {
		if (selindex == 0) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.homeframe, main).commit();
		} else if (selindex == 1) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.homeframe, news).commit();
		} else if (selindex == 2) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.homeframe, square).commit();
		} else if (selindex == 3) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.homeframe, personcenter).commit();
		}
		bottombar.setSelect(selindex);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		selindex=intent.getIntExtra("homeindex", 0);
		updatetip=intent.getStringExtra("updatetip");
		setSelectTab(selindex);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		ActivityID actId=null;
		switch (v.getId()) {
		    case R.id.gd_home_tabhome_div :
			case R.id.gd_tabhome_img :
			case R.id.gd_tabhome_txt :
				actId=ActivityID.TXZHUSHOU;
				selindex=0;
				bottombar.setSelect(0);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.homeframe, main).commit();
				break;
			case R.id.gd_tabnews_img :
			case R.id.gd_home_tabnews_div :
			case R.id.gd_tabnews_txt :
				actId=ActivityID.MESSAGE;
				selindex=1;
				bottombar.setSelect(1);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.homeframe, news).commit();
				break;

			case R.id.gd_tabsquare_img :
			case R.id.gd_home_tabsquare_div :
			case R.id.gd_tabsquare_txt :
				actId=ActivityID.SQUARE;
				selindex=2;
				bottombar.setSelect(2);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.homeframe, square).commit();
				break;

			case R.id.gd_tabperson_img :
			case R.id.gd_home_tabperson_div :
			case R.id.gd_tabperson_txt :
				actId=ActivityID.SETTING;
				selindex=3;
				bottombar.setSelect(3);
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.homeframe, personcenter).commit();
				break;
		}
		
		
		if ((actId != null) && getService(IUserUsageDataRecorder.class) != null) {
			getService(IUserUsageDataRecorder.class).doRecord(actId.ordinal());
		}
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
			// setSelect(currIndex+1);
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
			
			if(square.isVisible()&&square.canBack()){
				square.goBack();
				return true;
			}
			
			
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				// this.showToastMessage("再按一次返回键关闭程序", 300);
				Toast.makeText(this, "再按一次返回键关闭程序", 300).show();
				exitTime = System.currentTimeMillis();
			} else {
				 if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
						AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.EXIT_CLIENT.ordinal());
					}
				finish();
				// System.exit(0);
			}
			news.onKeyBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public IComHelperFramework getFramework() {
		return ((ApplicationManager) getApplication()).getFramework();
	}

	public <T> T getService(Class<T> clazz) {
		getFramework().getDeviceUUID();

		return getFramework().getService(clazz);
	}
}
