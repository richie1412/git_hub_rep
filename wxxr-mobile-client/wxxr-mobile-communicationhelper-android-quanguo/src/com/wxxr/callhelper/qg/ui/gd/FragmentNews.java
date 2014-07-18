package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.PingDaoBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.ui.gd.FragmentNews.MyViewPagerAdapter;

public class FragmentNews extends Fragment implements OnClickListener,
		OnItemClickListener {
	String TAG = this.getClass().getSimpleName();
	private LinearLayout mainnews;
	private GridView gv_pingdao;
	private ArrayList<PingDaoBean> pingdaos;
	private GridView gv_pingdao_bottom;
	private LinearLayout ll_title_select;
	private PopupWindow selectPop;
	private TextView tv_zsbb;
	private TextView tv_cnxh, tv_gypd;
	private LinearLayout ll_select;
	private TextView tv_pingdao;
	private ImageView iv_arrow;
	// private Button btn_refresh;
	private ViewPager view_pager;
	private List<View> mViewList;
	private static LinearLayout ll_zsbb_gypd;
	private static LinearLayout ll_zsbb;

	Timer timer;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		findView(inflater, container);
		processLogic();
		return mainnews;
	}

	private void findView(LayoutInflater inflater, ViewGroup container) {
		mainnews = (LinearLayout) inflater.inflate(R.layout.gd_minnews_main,
				container, false);
		gv_pingdao = (GridView) mainnews.findViewById(R.id.gv_pingdao);
		ll_zsbb_gypd = (LinearLayout) inflater
				.inflate(R.layout.zsbb_gypd, null);
		ll_zsbb = (LinearLayout) inflater.inflate(R.layout.zsbb, null);
		iv_arrow = (ImageView) mainnews.findViewById(R.id.iv_arrow);
		// btn_refresh = (Button) mainnews.findViewById(R.id.btn_refresh);
		// btn_refresh.setOnClickListener(this);
		tv_pingdao = (TextView) mainnews.findViewById(R.id.tv_title);
		view_pager = (ViewPager) mainnews.findViewById(R.id.view_pager);
		gv_pingdao_bottom = (GridView) mainnews
				.findViewById(R.id.gv_pingdao_bottom);
		ll_title_select = (LinearLayout) mainnews
				.findViewById(R.id.ll_title_select);
		gv_pingdao.setOnItemClickListener(this);
		gv_pingdao_bottom.setOnItemClickListener(this);
		ll_title_select.setOnClickListener(this);
		ll_select = (LinearLayout) LinearLayout.inflate(getActivity(),
				R.layout.gd_zhushou_or_caini, null);
		tv_zsbb = (TextView) ll_select.findViewById(R.id.tv_zsbb);
		tv_cnxh = (TextView) ll_select.findViewById(R.id.tv_cnxh);
		tv_gypd = (TextView) ll_select.findViewById(R.id.tv_gypd);
		tv_zsbb.setOnClickListener(this);
		tv_cnxh.setOnClickListener(this);
		tv_gypd.setOnClickListener(this);

		ll_zsbb.findViewById(R.id.ll_zhushoubobao).setOnClickListener(this);
		ll_zsbb_gypd.findViewById(R.id.ll_zhushoubobao_gongyibobao)
				.setOnClickListener(this);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.obj instanceof Integer) {
				int current = (Integer) msg.obj;
				view_pager.setCurrentItem(current, true);
			}
			// Animation animation = new TranslateAnimation(0, 720, 0, 0);
			// animation.setDuration(1000);
			// animation.setFillAfter(true);
			// animation.setInterpolator(new LinearInterpolator());
			// view_pager.setAnimation(animation);
		};
	};
	int temp = 0;
	private MyViewPagerAdapter mPagerAdapter;

	@Override
	public void onResume() {
		super.onResume();
		temp = 0;
	}

	private void processLogic() {
		initData();
		setAdapters();
		mViewList = new ArrayList<View>();
		mPagerAdapter = new MyViewPagerAdapter();
		view_pager.setAdapter(mPagerAdapter);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Message message = mHandler.obtainMessage();
				int count = mPagerAdapter.getCount();
				if (temp <= count - 1) {
					message.obj = temp;
					temp++;
				} else if (temp > count - 1) {
					temp = 0;
					message.obj = temp;
				}
				// Log.i("xxxxxxx", temp + "");
				if (count != 0) {
					mHandler.sendMessage(message);
				}
			}
		};
		timer = new Timer();
		timer.schedule(task, 2000, 2000);

	}

	private void setAdapters() {
		gv_pingdao.setAdapter(new MyAdapter() {

			@Override
			public View getView() {
				return LinearLayout.inflate(getActivity(),
						R.layout.gd_minnews_item, null);
			}

			@Override
			public int getStartPosition() {
				return 0;
			}

			@Override
			public int count() {
				return 6;
			}

		});
		gv_pingdao_bottom.setAdapter(new MyAdapter() {

			@Override
			public View getView() {
				return LinearLayout.inflate(getActivity(),
						R.layout.gd_minnews_item_bottom, null);
			}

			@Override
			public int getStartPosition() {
				return 6;
			}

			@Override
			public int count() {
				return 10;
			}
		});
	}

	private class ViewHolder {
		ImageView head;
		TextView name;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		// case R.id.btn_refresh :
		// RefreshAll refreshAll = new RefreshAll(getActivity());
		// refreshAll.executeOnMonitor(new Callable<Object>() {
		//
		// @Override
		// public Object call() throws Exception {
		// AppUtils.getService(IFetchDefaultZhushouBobao.class)
		// .getDefaultZhushouBaobao();
		// AppUtils.getService(IOfficeLineHtmlProvideService.class)
		// .getGuessYouLikeHome(
		// AppUtils.getService(
		// IClientConfigManagerService.class)
		// .getGuessLikeInfoUrl(), null);
		// return null;
		// }
		// });
		// break;
		case R.id.ll_title_select:
			if (selectPop == null) {
				showSelectPop();
			} else {
				hideSelectPop();
			}
			break;
		case R.id.tv_zsbb:
		case R.id.ll_zhushoubobao:
			startActivity(new Intent(getActivity(), GDPushMsgActivity.class));
			hideSelectPop();
			break;
		case R.id.tv_cnxh:
			startActivity(new Intent(getActivity(),
					GDGuessLikeListActivity.class));
			hideSelectPop();
			break;
		case R.id.tv_gypd:
		case R.id.ll_zhushoubobao_gongyibobao:
			startActivity(new Intent(getActivity(), GDPushMsgActivity.class)
					.putExtra("gypd", true));
			break;

		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (view_pager != null) {
			mViewList.clear();
			view_pager.setVisibility(View.GONE);
			mPagerAdapter.destroyItem(view_pager, 0, ll_zsbb_gypd);
			mPagerAdapter.destroyItem(view_pager, 1, ll_zsbb);
		}

		if (timer != null) {
			timer.cancel();
		}
		hideSelectPop();
	}

	private class RefreshAll extends GDCMProgressMonitor {

		public RefreshAll(Context context) {
			super(context);
		}

		@Override
		protected void handleDone(Object returnVal) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
				}
			});
		}

		@Override
		protected void handleFailed(Throwable cause) {

		}

	}

	/**
	 * 隐藏pop的场景 1.点击标题栏 2.点击返回键 3.进入助手播报或者猜你喜欢模块 4.点击手机屏幕其他地方
	 * 隐藏pop的必要两个条件：1.设置背景 2.最后update 一下。
	 */
	private void hideSelectPop() {
		if (iv_arrow != null) {
			iv_arrow.setBackgroundResource(R.drawable.gd_mininew_down);
		}
		if (selectPop != null) {
			selectPop.dismiss();
			selectPop = null;
		}
	}

	private void showSelectPop() {
		iv_arrow.setBackgroundResource(R.drawable.gd_mininew_up);
		selectPop = new PopupWindow(ll_select,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		selectPop.setAnimationStyle(R.style.pop_anim_style);
		selectPop.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.gd_pingdao_pop));
		selectPop.showAsDropDown(tv_pingdao, -50, 5);
		// 使其聚集
		selectPop.setFocusable(true);
		// 设置允许在外点击消失
		selectPop.setOutsideTouchable(true);
		// 刷新状态
		selectPop.update();
	}

	private void initData() {
		pingdaos = new ArrayList<PingDaoBean>();
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_kjsm, "科技数码", "KJSM"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_rwls, "人文历史", "RWLS"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_xxyl, "休闲娱乐", "XHPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_shjk, "生活健康", "JKPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_lyms, "旅游美食", "MSTD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_cjkb, "财经快报", "CJPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_jyzc, "教育职场", "JYPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_tyqy, "体育前沿", "TYPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_mryr, "每日育儿", "FYPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_gaty, "关爱糖友", "GATY"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_jsqc, "军事汽车", "SMPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_yrfs, "伊人风尚", "MRPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_jjfc, "房产家居", "FCPD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_gzsm, "关注睡眠", "GZSM"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_wap, "WAP资讯", "JRRD"));
		pingdaos.add(new PingDaoBean(R.drawable.gd_news_jqqd, "敬请期待", "JQQD"));

	}

	static class MyViewPagerAdapter extends PagerAdapter {
		public MyViewPagerAdapter() {

		}

		@Override
		public int getCount() {
			return 2;

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@SuppressWarnings("deprecation")
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		
				container.removeView((View) object);
			
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
					if (position == 0) {
						container.addView(ll_zsbb_gypd);
						return ll_zsbb_gypd;
					} else {
						container.addView(ll_zsbb);
						return ll_zsbb;
					}
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	private abstract class MyAdapter extends BaseAdapter {
		protected abstract int count();

		protected abstract View getView();

		protected abstract int getStartPosition();

		@Override
		public int getCount() {
			return count();
		}

		@Override
		public Object getItem(int position) {
			return pingdaos.get(position).getChannalCode();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				if (position == 9) {
					convertView = (LinearLayout) LinearLayout.inflate(
							getActivity(),
							R.layout.gd_minnews_item_bottom_jqqd, null);
				} else {
					convertView = getView();
				}
				holder.head = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PingDaoBean pingdao = pingdaos.get(position + getStartPosition());
			holder.head.setBackgroundResource(pingdao.getSourceID());
			holder.name.setText(pingdao.getName());
			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (parent.getId() == R.id.gv_pingdao_bottom) {
			position = position + 6;
			// 如果是"敬请期待"，暂时没有效果;
			if (gv_pingdao_bottom.getAdapter().getItem(position).equals("JQQD")) {
				return;
			}
		}
		PingDaoBean bean = pingdaos.get(position);
		Intent intent = new Intent(getActivity(), GDGuessLikeListActivity.class);
		intent.putExtra(Constant.CLIENT_INFO, bean);
		startActivity(intent);

	}

	public void onKeyBack() {
		hideSelectPop();
	}

	@Override
	public void onPause() {
		super.onPause();
		hideSelectPop();
	}

	public void onBackPressed() {

	}

}
