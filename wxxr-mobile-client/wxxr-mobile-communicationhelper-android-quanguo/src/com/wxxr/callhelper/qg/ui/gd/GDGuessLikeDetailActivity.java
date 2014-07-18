package com.wxxr.callhelper.qg.ui.gd;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.adapter.GDGuessLikeDetailAdapter;
import com.wxxr.callhelper.qg.bean.PingDaoBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.exception.NetworkNotAvailableException;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.ChannelMsg;
import com.wxxr.callhelper.qg.rpc.ChannelMsgPageVo;
import com.wxxr.callhelper.qg.service.IGuessLikeDetailCache;
import com.wxxr.callhelper.qg.service.IMicoNewsService;
import com.wxxr.callhelper.qg.service.NewCallRecordService;
import com.wxxr.callhelper.qg.ui.AbstractPullableView;
import com.wxxr.callhelper.qg.widget.RefreshableView;
import com.wxxr.callhelper.qg.widget.RefreshableView.PullToRefreshListener;
import com.wxxr.mobile.core.log.api.Trace;

public class GDGuessLikeDetailActivity extends BaseActivity {
	private static final Trace log = Trace.register(NewCallRecordService.class);
	private RefreshableView rv;
	private ListView lv_detail;
	private PingDaoBean mPingdao;
	private PullListView pullView;
	private int refreshCount = 1;
	private GDGuessLikeDetailAdapter adapter;
	private boolean hasMoreMessage = true;
	private List<List<String>> mDisplyCahcedMessage;// 最终展示的消息
	private List<String> mLocalMessage;// 获取本地缓存信息
	private LinearLayout no_net_work;
	private ProgressBar pb;
	private TextView no_message;
	public static final String GUESS_LIKE_MESSAGE = "guess_like_message";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guesslike_detail);
		findView();
		processLogic();
	}

	private void findView() {
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		ImageView iv_right = (ImageView) findViewById(R.id.gd_iv_titlebar_right);
		iv_right.setVisibility(View.INVISIBLE);
		no_net_work = (LinearLayout) findViewById(R.id.gd_network_not_access);
		no_net_work.setVisibility(View.GONE);
		no_message = (TextView) findViewById(R.id.tv_no_message);
		rv = (RefreshableView) findViewById(R.id.rf_main);
		pb = (ProgressBar) findViewById(R.id.pb_msg_detail);
		lv_detail = new ListView(this);
		lv_detail.setCacheColorHint(Color.TRANSPARENT);
		lv_detail.setSelector(getResources().getDrawable(R.drawable.white_bg));
		lv_detail.setFadingEdgeLength(0);
		pullView = new PullListView();
		rv.setPullableView(pullView);
		lv_detail.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String content = (String) lv_detail.getAdapter().getItem(
						position);
				Intent intent = new Intent(GDGuessLikeDetailActivity.this,
						GDItemMenuActivity.class);
				intent.putExtra(GUESS_LIKE_MESSAGE, content);
				startActivity(intent);
			}
		});
	}
	private void processLogic() {
		Intent intent = getIntent();
		mPingdao = (PingDaoBean) intent
				.getSerializableExtra(Constant.PINGDAO_INFO);
		try {
			((TextView) findViewById(R.id.tv_titlebar_name)).setText(URLDecoder
					.decode(mPingdao.getName(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// web资讯正在建设中
		if (mPingdao.getChannalCode().equals("JRRD")) {
			no_message.setVisibility(View.VISIBLE);
			rv.setVisibility(View.GONE);
			pb.setVisibility(View.GONE);
			return;
		}
		adapter = new GDGuessLikeDetailAdapter(this);
		mDisplyCahcedMessage = new ArrayList<List<String>>();
		mLocalMessage = getService(IGuessLikeDetailCache.class)
				.getCachedMessage(mPingdao.getChannalCode());
		if (mLocalMessage != null && !mLocalMessage.isEmpty()) {
			mDisplyCahcedMessage.add(mLocalMessage);
			pb.setVisibility(View.GONE);
			adapter.setData(mLocalMessage);
			if (log.isDebugEnabled()) {
				for (String msg : mLocalMessage) {
					log.debug(msg);
				}
			}
		}
		getChanleDetail("1");

		lv_detail.setAdapter(adapter);
		rv.setOnRefreshListener(new PullToRefreshListener() {

			@Override
			public void onRefresh() {
				refreshCount += 1;
				if (refreshCount > 5) {
					refreshCount = 5;
				}
				if (hasMoreMessage) {
					getChanleDetail(String.valueOf(refreshCount));
				} else {
					rv.hideHeader();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(), "没有更多资讯了",
									1).show();
						}
					});
				}
			}
		}, 2);

	}
	private void getChanleDetail(final String curpage) {
		CMProgressMonitor monitor = new CMProgressMonitor(
				getApplicationContext()) {

			@Override
			protected void handleFailed(final Throwable cause) {
				runOnUiThread(new Runnable() {
					public void run() {
						pb.setVisibility(View.GONE);
						rv.hideHeader();
						if (cause instanceof NetworkNotAvailableException) {
							if (mLocalMessage == null) {
								no_net_work.setVisibility(View.VISIBLE);
								rv.setVisibility(View.GONE);
							} else {
								Toast.makeText(getApplicationContext(),
										"没有网络连接，无法加载更多资讯，请配置网络连接", 1).show();
							}
						} else {
							if (mLocalMessage != null) {
							} else {
								Toast.makeText(getApplicationContext(),
										"网络加载遇到问题，请稍后再试", 1).show();
							}
						}
					}
				});
			}

			@Override
			protected void handleDone(Object returnVal) {
				ChannelMsgPageVo vo = (ChannelMsgPageVo) (returnVal);
				if (vo != null) {
					if (vo.getHasNextPage() == 0) {
						hasMoreMessage = false;
					}
					if (mLocalMessage != null) {
						mDisplyCahcedMessage.remove(mLocalMessage);
					}
					getService(IGuessLikeDetailCache.class).clearCachedMessage(
							mPingdao.getChannalCode());
					mDisplyCahcedMessage.add(parseMessage(vo));
					List<String> tempList = new ArrayList<String>();
					for (List<String> list : mDisplyCahcedMessage) {
						for (String string : list) {
							tempList.add(string);
							if (log.isDebugEnabled()) {
								log.debug(string);
							}
						}
					}
					adapter.setData(tempList);
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();
							rv.hideHeader();
							pb.setVisibility(View.GONE);
						}
					});
				}
			}
		};
		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return getService(IMicoNewsService.class)
						.getChanleDetailMessage(mPingdao.getChannalCode(),
								"10", curpage);
			}
		});
	}
	private List<String> parseMessage(ChannelMsgPageVo vo) {
		ChannelMsg[] msgs = vo.getMsgs();
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < msgs.length; i++) {
			list.add(msgs[i].getMsg());
		}
		return list;
	}
	private class PullListView extends AbstractPullableView {

		@Override
		public View getView() {
			return lv_detail;
		}

		@Override
		public void setOnTouchListener(OnTouchListener listener) {
			lv_detail.setOnTouchListener(listener);
		}

		@Override
		public boolean isPullable(View view, MotionEvent event) {
			return true;
		}

	}
	@Override
	protected void onDestroy() {
		if (mDisplyCahcedMessage != null && !mDisplyCahcedMessage.isEmpty()) {
			getService(IGuessLikeDetailCache.class).setCachedMessage(
					mDisplyCahcedMessage, mPingdao.getChannalCode());
			mDisplyCahcedMessage.clear();
		}
		super.onDestroy();
	}
}
