package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

public class ReaderPanelActivity extends FragmentActivity
		implements
			IWXAPIEventHandler {
	private static final Trace log = Trace.register(ReaderPanelActivity.class);

	private TextView tv_dailyNews, tv_pushMsg;
	private View v_left, v_right;
	private DisplayMetrics metrics;
	private Intent pushIntent, newsIntent;
	// private boolean isHyperLink;
	// private boolean push_isHyperLink;
	private ReaderPanelContext readerCtx = new ReaderPanelContext() {

		@Override
		public void onPushMsgClicked(HtmlMessageBean bean) {
			showPushMsg(bean);

		}

		@Override
		public void closeFragement(Fragment fragement) {
			getSupportFragmentManager().popBackStack();
		}

		@Override
		public void showTitle() {
			ReaderPanelActivity.this.showTitle();
		}

		@Override
		public void hideTitle() {
			ReaderPanelActivity.this.hideTitle();
		}
	};

	private View ll_leak_title;

	private View ll_leak_title_divline;

	private View ll_leak_title_divline2;

	private class DailyNewsReader extends WebViewFragement {
		private final Trace log = Trace.getLogger(DailyNewsReader.class);
		private String rootUrl = AppUtils.getService(
				IClientConfigManagerService.class).getEveryDayInfoUrl();

		public DailyNewsReader(ReaderPanelContext closer) {
			super(closer);
		}

		@Override
		protected boolean isSharable(String url) {
			if (url == null) {
				return false;
			}
			String webUrl = getLocalUrl(url);
			if (log.isDebugEnabled()) {
				log.debug("localUrl :" + webUrl + ", currentUrl :" + url);
			}
			boolean result = url.equals(webUrl) == false;
			// if(!result){
			// ((ReaderPanelActivity)this.getActivity()).showTitle();
			// }else{
			// ((ReaderPanelActivity)this.getActivity()).hideTitle();
			// }

			return result;
		}

		/**
		 * @param url
		 * @return
		 */
		protected String getLocalUrl(String url) {
			HtmlMessage html = AppUtils.getService(
					IOfficeLineHtmlProvideService.class).getMicrNews(url, null);
			String webUrl = html != null ? html.getUrl() : url;
			return webUrl;
		}

		@Override
		protected boolean isReloadable(String url) {
			if (log.isDebugEnabled()) {
				log.debug("isReloadable : loading url :" + url + ", rootUrl :"
						+ getRootUrl());
			}
			return url == null || isRootUrl(url);
		}

		protected boolean isRootUrl(String url) {
			return url != null
					&& (url.equals(getRootUrl()) || url
							.equals(getLocalUrl(getRootUrl())));
		}

		@Override
		protected Trace getLogger() {
			return log;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wxxr.callhelper.ui.WebViewFragement#getRootUrl()
		 */
		@Override
		public String getRootUrl() {
			return rootUrl;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			String currentUrl = getCurrentUrl();
			if ((currentUrl == null)
					|| currentUrl.equals(getLocalUrl(getRootUrl()))) {
				long lastUpdateTime = getLastUpdateTime();
				if ((System.currentTimeMillis() - lastUpdateTime) > (5 * 60 * 1000L)) {
					refreshContent(false);
				} else {
					refreshFromLocalContent();
				}
			}
			super.onResume();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.wxxr.callhelper.ui.WebViewFragement#onPageLoadingStarted(android
		 * .webkit.WebView, java.lang.String)
		 */
		@Override
		protected void onPageLoadingStarted(WebView view, String url) {
			if (isReloadable(url)) {
				showTitle();
			} else {
				hideTitle();
			}
			super.onPageLoadingStarted(view, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.wxxr.callhelper.ui.WebViewFragement#shouldOverrideContentLoading
		 * (android.webkit.WebView, java.lang.String)
		 */
		@Override
		protected boolean shouldOverrideContentLoading(WebView view, String url) {
			if (url == null || isRootUrl(url)) {
				return false;
			}
			showPushMsg(url);
			return true;
		}

	};

	private class PushMsgView extends WebViewFragement {
		private final Trace log = Trace.register(PushMsgView.class);

		public PushMsgView(ReaderPanelContext closer) {
			super(closer);
		}

		@Override
		protected boolean isSharable(String url) {
			// ((ReaderPanelActivity)this.getActivity()).hideTitle();
			return true;
		}

		@Override
		protected boolean isReloadable(String url) {
			return false;
		}

		@Override
		protected Trace getLogger() {
			return log;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wxxr.callhelper.ui.WebViewFragement#onResume()
		 */
		@Override
		public void onResume() {
			super.onResume();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.wxxr.callhelper.ui.WebViewFragement#onPageLoadingStarted(android
		 * .webkit.WebView, java.lang.String)
		 */
		@Override
		protected void onPageLoadingStarted(WebView view, String url) {
			hideTitle();
			super.onPageLoadingStarted(view, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wxxr.callhelper.ui.AbstractFragment#onStart()
		 */
		@Override
		public void onStart() {
			refreshContent(false);
			super.onStart();
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reader_panel);

		findView();

		processLogic();

		switch2NewsReader();
	}

	public void showTitle() {
		ll_leak_title.setVisibility(View.VISIBLE);

		ll_leak_title_divline.setVisibility(View.VISIBLE);

		ll_leak_title_divline2.setVisibility(View.VISIBLE);

	}

	public void hideTitle() {
		ll_leak_title.setVisibility(View.GONE);
		ll_leak_title_divline.setVisibility(View.GONE);
		ll_leak_title_divline2.setVisibility(View.GONE);
	}

	/**
	 * 
	 */
	protected void switch2NewsReader() {
		DailyNewsReader reader = new DailyNewsReader(readerCtx);
		switchFragement(reader, false);

		// String url =
		// AppUtils.getService(IClientConfigManagerService.class).getEveryDayInfoUrl();
		// reader.loadWebContent(url, null);
	}

	private void switchFragement(Fragment newFragment, boolean add2Backstack) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.ll_main_content, newFragment);
		if (add2Backstack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void findView() {
		// intent = getIntent();
		// msgLists = new ArrayList<HtmlMessageBean>();
		tv_dailyNews = (TextView) findViewById(R.id.leak_text_tab1);
		tv_pushMsg = (TextView) findViewById(R.id.leak_text_tab2);
		ll_leak_title = findViewById(R.id.ll_leak_title);
		ll_leak_title_divline = findViewById(R.id.ll_leak_title_divline);
		ll_leak_title_divline2 = findViewById(R.id.ll_leak_title_divline2);
		findViewById(R.id.leak_tab1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2DailyNewsTab();
			}
		});
		findViewById(R.id.leak_tab2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch2PushMsgTab();
			}
		});
		v_left = findViewById(R.id.v_left);
		v_right = findViewById(R.id.v_right);

		// ll_main_content = (LinearLayout) findViewById(R.id.ll_main_content);
		// pb_webview2 = (ProgressBar) findViewById(R.id.pb_mini_info2);

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

	}

	private void processLogic() {
		tv_dailyNews.setText("每日悦读");
		tv_pushMsg.setText("助手播报");

		Intent intent = getIntent();
		if (intent.getBooleanExtra(Constant.PUSH_ISHYPERLINK, false)) {
			if (log.isDebugEnabled()) {
				log.debug("show push message detail , intent :[" + intent + "]");
			}
			this.pushIntent = intent;
		} else if (intent.getBooleanExtra(Constant.ISHYPERLINK, false)) {
			if (log.isDebugEnabled()) {
				log.debug("show head news detail , intent :[" + intent + "]");
			}
			this.newsIntent = intent;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (this.pushIntent != null) {
			switch2PushMsgTab();
			final Intent intent = this.pushIntent;
			this.pushIntent = null;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showPushMsg(
							intent.getStringExtra(Constant.HYPERLINK_ADDRESS),
							intent.getStringExtra(Constant.PUSH_TITLE),
							intent.getStringExtra(Constant.PUSH_ABSTRCT),
							intent.getStringExtra(Constant.PUSH_IMAGE),
							intent.getStringExtra(Constant.PUSH_SHARE_ADDRESS));

				}
			});
		} else if (this.newsIntent != null) {
			switch2DailyNewsTab();
			final Intent intent = this.newsIntent;
			this.newsIntent = null;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showHeadNews(intent
							.getStringExtra(Constant.HYPERLINK_ADDRESS));

				}
			});
		}
	}

	@Override
	public void onReq(BaseReq req) {
		if (log.isDebugEnabled()) {
			log.debug("wx onReq :" + req.toString());
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		if (log.isDebugEnabled()) {
			log.debug("wx onResp :" + resp.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		Fragment frag = getSupportFragmentManager().findFragmentById(
				R.id.ll_main_content);
		if (frag instanceof AbstractFragment) {
			if (((AbstractFragment) frag).onBackPressed()) {
				return;
			}
		}
		super.onBackPressed();
	}

	/**
	 * @param bean
	 */
	protected void showPushMsg(HtmlMessageBean bean) {
		showPushMsg(bean.getHtmlMessage().getUrl(), bean.getHtmlMessage()
				.getTitle(), bean.getHtmlMessage().getAbstrct(), bean
				.getHtmlMessage().getImage(), bean.getRemoteURL());
	}

	protected void showPushMsg(String hyperlink_address, String title,
			String description, String imgUrl, String pageUrl) {
		PushMsgView view = new PushMsgView(readerCtx);
		switchFragement(view, true);
		Map<String, String> map = new HashMap<String, String>();
		map.put("title", title);
		map.put("description", description);
		map.put("imgUrl", imgUrl);
		map.put("pageUrl", pageUrl);
		view.loadWebContent(hyperlink_address, map);
	}

	protected void showPushMsg(String hyperlink_address) {
		PushMsgView view = new PushMsgView(readerCtx);
		switchFragement(view, true);
		view.loadWebContent(hyperlink_address, null);
	}

	protected void showHeadNews(String hyperlink_address) {
		PushMsgView view = new PushMsgView(readerCtx);
		switchFragement(view, true);
		view.loadWebContent(hyperlink_address, null);
	}

	/**
	 * 
	 */
	protected void switch2DailyNewsTab() {
		if (v_left.getVisibility() == View.VISIBLE) {
			return;
		}
		tv_dailyNews.setTextColor(getResources().getColor(R.color.white));
		tv_pushMsg.setTextColor(getResources().getColor(R.color.leak_msg_bg));
		showTitle();
		v_left.setVisibility(View.VISIBLE);
		v_right.setVisibility(View.INVISIBLE);
		switch2NewsReader();
	}

	/**
	 * 
	 */
	protected void switch2PushMsgTab() {
		if (v_right.getVisibility() == View.VISIBLE) {
			return;
		}
		tv_pushMsg.setTextColor(getResources().getColor(R.color.white));
		tv_dailyNews.setTextColor(getResources().getColor(R.color.leak_msg_bg));
		showTitle();
		v_right.setVisibility(View.VISIBLE);
		v_left.setVisibility(View.INVISIBLE);
		switchFragement(new PushMsgListFragement(readerCtx), false);
	}
}
