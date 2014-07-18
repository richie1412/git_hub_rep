/**
 * 
 */
package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;
import com.wxxr.callhelper.qg.utils.WeiboManager;
import com.wxxr.callhelper.qg.widget.RefreshableView;
import com.wxxr.callhelper.qg.wxapi.WXEntryActivity;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * @author neillin
 * 
 */
public abstract class WebViewFragement extends AbstractFragment {
	// private static final Trace log = Trace.register(WebViewFragement.class);

	private RefreshableView rview;
	private ProgressBar pb_webview, pb_webview2;
	private LinearLayout ll_webview_bottom, ll_minimsg_share;

	private ImageView iv_weibo_share, iv_mail_share, iv_msg_share,
			iv_minimsg_share;
	private WebView wv_home;
	private View iv_webview_share, iv_webview_back;
	private String rootUrl;
	// private boolean pullable;
	private final ReaderPanelContext fragementCloser;
	private Map<String, Map<String, String>> shareMaps = new HashMap<String, Map<String, String>>();
	private String currentUrl;
	private CountDownLatch latch;
	private boolean onShow;
	public WebViewFragement(ReaderPanelContext closer) {
		this.fragementCloser = closer;
	}

	protected abstract boolean isSharable(String url);

	protected abstract boolean isReloadable(String url);

	private class MyWebViewClient extends WebViewClient {

		// 超链接
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Loading content :" + url);
			}
			return shouldOverrideContentLoading(view, url);
		}

		// 开始加载页面
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			onPageLoadingStarted(view, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
		 * java.lang.String)
		 */
		@Override
		public void onLoadResource(WebView view, String url) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Loading content :" + url);
			}
			super.onLoadResource(view, url);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onPageFinished(android.webkit.WebView,
		 * java.lang.String)
		 */
		@Override
		public void onPageFinished(WebView view, String url) {
			onPageLoadingCompleted(view, url);
			super.onPageFinished(view, url);
			
		}
		
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			onPageLoadErr();
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		
	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {
				closeLatch();
				pb_webview.setVisibility(View.GONE);
				if (shareMaps.get(currentUrl) == null) {
					iv_webview_share.setVisibility(View.INVISIBLE);
				} else {
					iv_webview_share.setVisibility(View.VISIBLE);
				}
			}
		}
		
		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
			receivedTitle(view,title);
		}
		
	}

	protected void onPageLoadingStarted(WebView view, String url) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("start loading page :" + url);
		}
		if (isSharable(url)) {
			ll_webview_bottom.setVisibility(View.VISIBLE);
		} else {
			ll_webview_bottom.setVisibility(View.GONE);
			ll_minimsg_share.setVisibility(View.GONE);
		}
		currentUrl = url;
	}

	public void onPageLoadErr() {
		// TODO Auto-generated method stub
		
	}

	public void receivedTitle(WebView view, String title) {
		
	}

	protected void onPageLoadingCompleted(WebView view, String url) {

	}

	protected boolean shouldOverrideContentLoading(WebView view, String url) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("onCreateView ...");
		}
		this.rview = new RefreshableView(getActivity(), null);
		final View view = inflater.inflate(R.layout.downweb_webview, container, false);
		ll_webview_bottom = (LinearLayout) view
				.findViewById(R.id.ll_webview_bottom);
		pb_webview = (ProgressBar) view.findViewById(R.id.pb_webview_new);
	//	pb_webview2 = (ProgressBar) view.findViewById(R.id.pb_webview2);
		ll_minimsg_share = (LinearLayout) view
				.findViewById(R.id.ll_minimsg_share);// 分享栏
		iv_weibo_share = (ImageView) view.findViewById(R.id.iv_weibo_share);
		iv_mail_share = (ImageView) view.findViewById(R.id.iv_mail_share);
		iv_msg_share = (ImageView) view.findViewById(R.id.iv_msg_share);
		iv_minimsg_share = (ImageView) view.findViewById(R.id.iv_minimsg_share);
		wv_home = (WebView) view.findViewById(R.id.wv);
		iv_webview_share = view.findViewById(R.id.iv_webview_share);
		iv_webview_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ll_minimsg_share.getVisibility() == View.GONE) {
					ll_minimsg_share.setVisibility(View.VISIBLE);
				} else {
					ll_minimsg_share.setVisibility(View.GONE);
				}
			}
		});
		iv_webview_back = view.findViewById(R.id.iv_webview_back);
		iv_webview_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (wv_home.canGoBack()) {
					wv_home.goBack();
				} else {
					fragementCloser.closeFragement(WebViewFragement.this);
				}

			}
		});

		iv_weibo_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> map = shareMaps.get(currentUrl);
				String title = map.get("title");
				String shareUrl = map.get("pageUrl");
				if (getLogger().isDebugEnabled()) {
					getLogger().debug("weibo分享：", title + shareUrl);
				}
				WeiboManager wm = new WeiboManager(getActivity());
				if (wm.isBind()) {
					try {
						wm.share2weibo(title + shareUrl, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					wm.bindAndShare2weibo(title + shareUrl, null);
				}
			}
		});
		iv_minimsg_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> map = shareMaps.get(currentUrl);
				String title = map.get("title");
				String shareUrl = map.get("pageUrl");
				String abstrct = map.get("description");
				String imgUrl = map.get("imgUrl");
				Log.e("微信分享：", title + shareUrl + abstrct + imgUrl);
				Intent in = new Intent();
				in.setClass(getActivity(), WXEntryActivity.class);
				in.putExtra("title", title);
				in.putExtra("description", abstrct);
				in.putExtra("pageUrl", shareUrl);
				in.putExtra("imgUrl", imgUrl);
				startActivity(in);
			}
		});
		iv_msg_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> map = shareMaps.get(currentUrl);
				String title = map.get("title");
				String shareUrl = map.get("pageUrl");
				Log.e("短信分享：", title + shareUrl);
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"smsto", "", null));
				intent.putExtra("sms_body", title + shareUrl);
				startActivity(intent);
			}
		});
		iv_mail_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Map<String, String> map = shareMaps.get(currentUrl);
				String title = map.get("title");
				String shareUrl = map.get("pageUrl");
				Log.e("邮件分享：", title + shareUrl);
				Intent intent = new Intent(Intent.ACTION_SEND);// 调用系统发邮件
				intent.setType("text/plain");// 设置文本格式
				intent.putExtra(Intent.EXTRA_EMAIL, "");// 设置对方邮件地址
				intent.putExtra(Intent.EXTRA_SUBJECT, title);// 设置标题内容
				intent.putExtra(Intent.EXTRA_TEXT, shareUrl);// 设置邮件文本内容
				startActivity(Intent.createChooser(intent, "请选择邮件发送"));
			}
		});
		getFirstViewImpl();
		rview.setPullableView(new AbstractPullableView() {

			@Override
			public void setOnTouchListener(OnTouchListener listener) {
				wv_home.setOnTouchListener(listener);
			}

			@Override
			public boolean isPullable(View v, MotionEvent event) {
				// int top = wv_home.getTop();
				if (!isReloadable(currentUrl) || wv_home.getScrollY() > 0) {
					return false;
				}
				return true;
			}

			@Override
			public View getView() {
				return view;
			}

		});
		rview.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {

			@Override
			public void onRefresh() {
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if (getLogger().isDebugEnabled()) {
								getLogger().debug(
										"Refresh content from listener ...");
							}
							onRefreshAction();
							refreshContent(true);
						}
					});
				}
			}
		}, 2);
		initViews(rview);
		return rview;
	}

	protected void initViews(View view) {
		// TODO Auto-generated method stub

	}

	private void setupWebView(WebView engine) {
		engine.setWebViewClient(new MyWebViewClient());
		// 设置辅助功能
		engine.setWebChromeClient(new MyWebChromeClient());

		setWebViewAttribute(engine, getLogger());
	}

	protected void setWebViewAttribute(WebView engine, Trace log) {
		engine.getSettings().setJavaScriptEnabled(true);
		engine.getSettings().setBuiltInZoomControls(false);
		// engine.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
		// engine.getSettings().setLoadsImagesAutomatically(true);
		engine.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		engine.setInitialScale(1);
		engine.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

	}

	private void getFirstViewImpl() {

		setupWebView(wv_home);

		wv_home.addJavascriptInterface(new Object() {

			@SuppressWarnings("unused")
			public void getWebHtmlInfo(final String title,
					final String description, final String imgUrl) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("title", title);
				map.put("description", description);
				map.put("imgUrl", imgUrl);
				map.put("pageUrl", currentUrl);
				shareMaps.put(currentUrl, map);
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"Set share info from web page :[" + map + "]");
				}
			}
		}, "webHtemInfo");

		// refreshDailyNews(false);
	}

	protected HtmlMessage getLococalHtmlMessage(AbstractProgressMonitor monitor) {
		return AppUtils.getService(IOfficeLineHtmlProvideService.class)
				.getMicrNews(getRootUrl(), monitor);
	}

	protected void refreshFromLocalContent() {
		rview.hideHeader();
		HtmlMessage htmlMessage = getLococalHtmlMessage(null);
		if (htmlMessage != null) {
			Log.i("getUrl", htmlMessage.getUrl() + getSpecificUrl());
			wv_home.loadUrl(htmlMessage.getUrl() + getSpecificUrl());
		} else {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Refresh content from remote  ...");
			}
			refreshContent(true);
		}
	}
	
	public void hideHeader(){
		rview.hideHeaderDirect();
	}
	
	
	protected void refreshFromLocalContent0() {
		rview.hideHeaderDirect();
		HtmlMessage htmlMessage = getLococalHtmlMessage(null);
		if (htmlMessage != null) {
			Log.i("getUrl", htmlMessage.getUrl() + getSpecificUrl());
			wv_home.loadUrl(htmlMessage.getUrl() + getSpecificUrl());
			wv_home.setVisibility(View.VISIBLE);
		} else {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Refresh content from remote  ...");
			}
			refreshContent(true);
		}
	}

	AbstractProgressMonitor Monitor = new AbstractProgressMonitor() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor# beginTask(int)
		 */
		@Override
		public void beginTask(int arg0) {
			// if(getActivity() != null)
			// getActivity().runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// if (!refreshing)
			// pb_webview2.setVisibility(View.VISIBLE);
			// }
			// });
			if (latch != null) {
				if (getLogger().isDebugEnabled()) {
					getLogger()
							.debug("Waiting for webview finish loading ....");
				}
				try {
					latch.await(20, TimeUnit.SECONDS);
				} catch (Throwable e) {
				}
				if (getLogger().isDebugEnabled()) {
					getLogger().debug(
							"start loading content from remote server ....");
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#
		 * done(java.lang.Object)
		 */
		@Override
		public void done(final Object returnVal) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Refresh news done !");
			}
			if (getActivity() != null)
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// pb_webview2.setVisibility(View.GONE);
						if (returnVal instanceof HtmlMessage) {
							// 新的本地页
							wv_home.loadUrl(((HtmlMessage) returnVal).getUrl());
							// wv_home.reload();
						}
						rview.finishRefreshing();
						pb_webview.setVisibility(View.GONE);
						if (getLogger().isDebugEnabled()) {
							getLogger().debug("loaded done");
						}

					}
				});
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#
		 * taskFailed(java.lang.Throwable)
		 */
		@Override
		public void taskFailed(Throwable e) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Refresh news Failed !");
			}

			if (getActivity() != null)
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						rview.finishRefreshing();
						pb_webview.setVisibility(View.GONE);
					}
				});
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("loaded failed", e);
			}
		}

	};

	protected void refreshContent(final boolean refreshing) {
		if (!isReloadable(getRootUrl())) {
			pb_webview.setVisibility(View.VISIBLE);
			wv_home.loadUrl(getRootUrl());
			return;
		}
		//yangrunfei  第一加载的时候，不要显示正在刷新,  下拉刷新不走这里
		if (!refreshing){
		//	rview.setOnRefreshing();
		}
		createLatch();
		AppUtils.getFramework().invokeLater(new Runnable() {

			@Override
			public void run() {
				closeLatch();
			}
		}, 20, TimeUnit.SECONDS);

		HtmlMessage htmlMessage = getLococalHtmlMessage(Monitor);
		
		getHtmlFromServer(Monitor);


		if (htmlMessage != null) {
			Log.i("getUrl", htmlMessage.getUrl() + getSpecificUrl());
			wv_home.loadUrl(htmlMessage.getUrl() + getSpecificUrl());
		} else {
			closeLatch();
		}
	}

	protected void getHtmlFromServer(AbstractProgressMonitor monitor2) {
		AppUtils.getService(IOfficeLineHtmlProvideService.class).getMicrNews(
				getRootUrl(), monitor2);		
	}

	/**
	 * 
	 */
	protected void createLatch() {
		latch = new CountDownLatch(1);
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("created loading latch :" + latch);
		}
	}

	/**
	 * 
	 */
	protected void closeLatch() {
		if (this.latch != null) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("close loading latch " + latch);
			}
			this.latch.countDown();
			this.latch = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		clear();
		super.onDestroyView();
	}

	protected long getLastUpdateTime() {
		return rview.getLastUpdateTime();
	}

	protected String getCurrentUrl() {
		return this.currentUrl;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		super.onResume();
		this.onShow = true;
	}

	public void loadWebContent(String url, Map<String, String> shareMap) {
		this.rootUrl = url;
		if (shareMap != null) {
			this.shareMaps.put(url, shareMap);
		}
		if (this.onShow) {
			if (getLogger().isDebugEnabled()) {
				getLogger().debug("Do initial content refreshing ...");
			}

			refreshContent(false);
		}
	}

	public void clear() {
		this.rootUrl = null;
		this.shareMaps.clear();
		this.currentUrl = null;
	}

	public String getRootUrl() {
		return this.rootUrl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.callhelper.ui.AbstractFragment#onBackPressed()
	 */
	@Override
	public boolean onBackPressed() {
		if (getLogger().isDebugEnabled()) {
			getLogger().debug("Back button pressed ...");
		}
		if (wv_home.canGoBack()) {
			wv_home.goBack();
			return true;
		}
		return false;
	}
	protected String getSpecificUrl() {
		return "";
	}
	protected void onRefreshAction() {

	}
	
	protected void  showPorocesbar() {
		pb_webview.setVisibility(View.VISIBLE);
	}
	
	protected void hideProcesbar() {
		pb_webview.setVisibility(View.GONE);
	}
	
	
	
}
