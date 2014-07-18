package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IMessageProviderModule;
import com.wxxr.callhelper.qg.adapter.MiniInfoPushMsgAdapter;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;
import com.wxxr.callhelper.qg.utils.WeiboManager;
import com.wxxr.callhelper.qg.widget.MyListView;
import com.wxxr.callhelper.qg.widget.RefreshableView;
import com.wxxr.callhelper.qg.wxapi.WXEntryActivity;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.api.ApplicationFactory;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.web.grabber.model.URLCanonicalizer;

public class MiniInformationActivity extends BaseActivity implements
		IWXAPIEventHandler {

//	private TextView leak_text_tab1, leak_text_tab2;
//	private View v_left, v_right;
//	private LinearLayout ll_main_content, ll_webview_bottom, ll_minimsg_share;
//	private ProgressBar pb_webview, pb_webview2;
//	private ImageView iv_weibo_share, iv_mail_share, iv_msg_share,
//			iv_minimsg_share;
//	private WebView wv_home;
//	// private Intent intent;
//	// private boolean isHyperLink;
//	private String pageUrl, title, shareUrl, abstrct, imgUrl;
//	private boolean flag = true;
//	private int currentTab = 1;
////	private int endpoint;
////	private List<HtmlMessageBean> msgLists;
//
//	private static final Trace log = Trace
//			.register(MiniInformationActivity.class);
//
//	// private Handler handler = new Handler() {
//	//
//	// public void handleMessage(Message msg) {
//	// switch (msg.what) {
//	// case 0:
//	// Log.e("列表数据", isHyperLink + hyperlink_address);
//	// if (isHyperLink) {
//	// setHyperlink(wv_home, hyperlink_address);
//	// }
//	// break;
//	//
//	// case 1:
//	// wv_home.stopLoading();
//	// break;
//	//
//	// case 2:
//	// pb_webview2.setVisibility(View.VISIBLE);
//	// break;
//	//
//	// case 3:
//	// pb_webview2.setVisibility(View.GONE);
//	// break;
//	// }
//	// }
//	// };
//	private MiniInfoPushMsgAdapter adapter;
//	private DisplayMetrics metrics;
//	private TextView tv_mini_info_more, tv_mini_info_loading;
//	private ProgressBar pb_mini_info_push, pb_mini_info_list;
////	private int currentCount;
//	// private ScrollView sv_mini_info_push;
//	private MyListView lv_mini_msg_push;
////	private List<HtmlMessageBean> lists;
////	private int listsTotal;
//	private RefreshableView rview;
//	private LinearLayout ll_mini_info_push_bottom;
//	private int currentFlag;
//
//	private boolean pullable=true;
//	private Intent intent;
//	private boolean isHyperLink;
//	private boolean push_isHyperLink;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.leak_main);
//
//		findView();
//
//		processLogic();
//	}
//
//	private void findView() {
//		intent = getIntent();
////		msgLists = new ArrayList<HtmlMessageBean>();
//		leak_text_tab1 = (TextView) findViewById(R.id.leak_text_tab1);
//		leak_text_tab2 = (TextView) findViewById(R.id.leak_text_tab2);
//		findViewById(R.id.leak_tab1).setOnClickListener(this);
//		findViewById(R.id.leak_tab2).setOnClickListener(this);
//		v_left = findViewById(R.id.v_left);
//		v_right = findViewById(R.id.v_right);
//
//		ll_main_content = (LinearLayout) findViewById(R.id.ll_main_content);
//		// pb_webview2 = (ProgressBar) findViewById(R.id.pb_mini_info2);
//
//		metrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//	}
//
//	private void processLogic() {
//		leak_text_tab1.setText("每日悦读");
//		leak_text_tab2.setText("助手播报");
//
//		isHyperLink = intent.getBooleanExtra(Constant.ISHYPERLINK, false);
//		push_isHyperLink = intent.getBooleanExtra(Constant.PUSH_ISHYPERLINK, false);
//		Log.e("是否是传过来：", isHyperLink + "");
//		Log.e("是否推送过来", push_isHyperLink + "");
//		// ll_main_content.addView(getFirstView());
//	}
//
//	/**
//	 * 每日悦读
//	 * 
//	 * @return
//	 */
//	private View getFirstView() {
//		rview = new RefreshableView(this, null);
//		final View view = View.inflate(this, R.layout.home_2, null);
//		ll_webview_bottom = (LinearLayout) view
//				.findViewById(R.id.ll_webview_bottom);
//		pb_webview = (ProgressBar) view.findViewById(R.id.pb_webview);
//		pb_webview2 = (ProgressBar) view.findViewById(R.id.pb_webview2);
//		view.findViewById(R.id.iv_webview_share).setOnClickListener(this);
//		view.findViewById(R.id.iv_webview_back).setOnClickListener(this);
//		ll_minimsg_share = (LinearLayout) view
//				.findViewById(R.id.ll_minimsg_share);// 分享栏
//		iv_weibo_share = (ImageView) view.findViewById(R.id.iv_weibo_share);
//		iv_mail_share = (ImageView) view.findViewById(R.id.iv_mail_share);
//		iv_msg_share = (ImageView) view.findViewById(R.id.iv_msg_share);
//		iv_minimsg_share = (ImageView) view.findViewById(R.id.iv_minimsg_share);
//		wv_home = (WebView) view.findViewById(R.id.wv);
//
//		iv_weibo_share.setOnClickListener(MiniInformationActivity.this);
//		iv_minimsg_share.setOnClickListener(MiniInformationActivity.this);
//		iv_msg_share.setOnClickListener(MiniInformationActivity.this);
//		iv_mail_share.setOnClickListener(MiniInformationActivity.this);
//
//		getFirstViewImpl();
//		rview.setPullableView(new AbstractPullableView() {
//
//			@Override
//			public void setOnTouchListener(OnTouchListener listener) {
//				wv_home.setOnTouchListener(listener);
//			}
//
//			@Override
//			public boolean isPullable(View v, MotionEvent event) {
//				// int top = wv_home.getTop();
//				if (!pullable || wv_home.getScrollY() > 0) {
//					return false;
//				}
//				return true;
//			}
//
//			@Override
//			public View getView() {
//				return view;
//			}
//
//		});
//		rview.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
//
//			@Override
//			public void onRefresh() {
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						refreshDailyNews(true);
//					}
//				});
//			}
//		}, 2);
//		return rview;
//	}
//
//	private View getSimpleWebView() {
//		final View view = View.inflate(this, R.layout.home_2, null);
//		ll_webview_bottom = (LinearLayout) view
//				.findViewById(R.id.ll_webview_bottom);
//		pb_webview = (ProgressBar) view.findViewById(R.id.pb_webview);
//		pb_webview2 = (ProgressBar) view.findViewById(R.id.pb_webview2);
//		view.findViewById(R.id.iv_webview_share).setOnClickListener(this);
//		view.findViewById(R.id.iv_webview_back).setOnClickListener(this);
//		ll_minimsg_share = (LinearLayout) view
//				.findViewById(R.id.ll_minimsg_share);// 分享栏
//		iv_weibo_share = (ImageView) view.findViewById(R.id.iv_weibo_share);
//		iv_mail_share = (ImageView) view.findViewById(R.id.iv_mail_share);
//		iv_msg_share = (ImageView) view.findViewById(R.id.iv_msg_share);
//		iv_minimsg_share = (ImageView) view.findViewById(R.id.iv_minimsg_share);
//		wv_home = (WebView) view.findViewById(R.id.wv);
//
//		iv_weibo_share.setOnClickListener(MiniInformationActivity.this);
//		iv_minimsg_share.setOnClickListener(MiniInformationActivity.this);
//		iv_msg_share.setOnClickListener(MiniInformationActivity.this);
//		iv_mail_share.setOnClickListener(MiniInformationActivity.this);
//
//		getFirstViewImpl();
//		return view;
//	}
//
//	private void getFirstViewImpl() {
//		// boolean isFileHyperLink = getIntent().getBooleanExtra(
//		// Constant.FILE_HYPERLINK_ADDRESS, false);
//		// boolean isHyperLink =
//		// getIntent().getBooleanExtra(Constant.ISHYPERLINK, false);
//		// Log.e("是否是传过来：", isHyperLink + "");
//		// if (!isFileHyperLink) {
//		// hyperlink_address = Constant.MAGNOLIA_URL
//		// + intent.getStringExtra(Constant.HYPERLINK_ADDRESS);
//		// } else {
//		// hyperlink_address = intent
//		// .getStringExtra(Constant.HYPERLINK_ADDRESS);
//		// }
//
//		// if(isHyperLink)
//		// hyperlink_address =
//		// intent.getStringExtra(Constant.HYPERLINK_ADDRESS);
//		//
//		// if (hyperlink_address != null)
//		// Log.e("超链接地址：", hyperlink_address);
//
//		setupWebView(wv_home);
//
//		wv_home.addJavascriptInterface(new Object() {
//
//			public void getWebHtmlInfo(final String title,
//					final String description, final String imgUrl) {
//				// 微信分享
//				iv_minimsg_share.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Log.e("wx", title + "\n" + pageUrl + "\n" + imgUrl
//								+ "\n" + description);
//						runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								Intent in = new Intent();
//								in.setClass(MiniInformationActivity.this,
//										WXEntryActivity.class);
//								in.putExtra("title", title);
//								in.putExtra("description", description);
//								in.putExtra("pageUrl", pageUrl);
//								in.putExtra(
//										"imgUrl",
//										URLCanonicalizer.getCanonicalURL(imgUrl, pageUrl));
//								if(log.isDebugEnabled()){
//									log.debug("url:"+URLCanonicalizer.getCanonicalURL(imgUrl, pageUrl));
//
//								}
//								startActivity(in);
//								// WXManager wx = new WXManager(
//								// MiniInformationActivity.this);
//								// try {
//								// IWXAPI api = wx.regToWx();
//								// // wx.sendReqToWx(title + pageUrl);
//								// wx.sendReqToWx(title ,description,pageUrl,
//								// imgUrl);
//								// api.handleIntent(getIntent(),
//								// MiniInformationActivity.this);
//								// } catch (Exception e) {
//								// e.printStackTrace();
//								// }
//
//							}
//						});
//					}
//				});
//
//				// 微博分享
//				iv_weibo_share.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Log.e("weibo", title + pageUrl);
//						WeiboManager wm = new WeiboManager(
//								MiniInformationActivity.this);
//						if (wm.isBind()) {
//							try {
//								wm.share2weibo(title + pageUrl, null);
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						} else {
//							// wm.bindAndShare2weibo(title + pageUrl, null);
//							wm.bind();
//						}
//					}
//				});
//
//				// 短信分享
//				iv_msg_share.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Log.e("msg", title + "\n" + description + "\n" + imgUrl);
//						// Uri uri = Uri.parse("smsto:");
//						// Intent intent = new Intent(Intent.ACTION_SENDTO,
//						// uri);
//						Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
//								.fromParts("smsto", "", null));
//						intent.putExtra("sms_body", title + pageUrl);
//						startActivity(intent);
//					}
//				});
//
//				// 邮件分享
//				iv_mail_share.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Log.e("mail", title + pageUrl);
//						Intent intent = new Intent(Intent.ACTION_SEND);// 调用系统发邮件
//						intent.setType("text/plain");// 设置文本格式
//						intent.putExtra(Intent.EXTRA_EMAIL, "");// 设置对方邮件地址
//						intent.putExtra(Intent.EXTRA_SUBJECT, title);// 设置标题内容
//						intent.putExtra(Intent.EXTRA_TEXT, pageUrl);// 设置邮件文本内容
//						startActivity(Intent.createChooser(intent, "请选择邮件发送"));
//						// startActivity(intent);
//					}
//				});
//
//			}
//		}, "webHtemInfo");
//
//		// refreshDailyNews(false);
//	}
//
//	private void refreshDailyNews(final boolean refreshing) {
//		if (!refreshing)
//			pb_webview.setVisibility(View.VISIBLE);
//
//		// if (!TextUtils.isEmpty(Constant.MAGNOLIA_URL)) {
//		// String url = Constant.MAGNOLIA_URL +
//		// getService(IClientCustomService.class).getEveryDayInfoUrl();
//		String url = getService(IClientConfigManagerService.class).getEveryDayInfoUrl();
//		HtmlMessage htmlMessage = getService(
//				IOfficeLineHtmlProvideService.class).getMicrNews(url,
//				new AbstractProgressMonitor() {
//
//					/*
//					 * (non-Javadoc)
//					 * 
//					 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#
//					 * beginTask(int)
//					 */
//					@Override
//					public void beginTask(int arg0) {
//						runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								if (!refreshing)
//									pb_webview2.setVisibility(View.VISIBLE);
//							}
//						});
//					}
//
//					/*
//					 * (non-Javadoc)
//					 * 
//					 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#
//					 * done(java.lang.Object)
//					 */
//					@Override
//					public void done(final Object returnVal) {
//						runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								pb_webview2.setVisibility(View.GONE);
//								if (returnVal instanceof HtmlMessage) {
//									// 新的本地页
//									wv_home.loadUrl(((HtmlMessage) returnVal)
//											.getUrl());
//									// wv_home.reload();
//								}
//								rview.finishRefreshing();
//								if (log.isDebugEnabled()) {
//									log.debug("loaded done");
//								}
//
//							}
//						});
//					}
//
//					/*
//					 * (non-Javadoc)
//					 * 
//					 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#
//					 * taskFailed(java.lang.Throwable)
//					 */
//					@Override
//					public void taskFailed(Throwable e) {
//						runOnUiThread(new Runnable() {
//
//							@Override
//							public void run() {
//								pb_webview2.setVisibility(View.GONE);
//							}
//						});
//						if (log.isDebugEnabled()) {
//							log.debug("loaded failed", e);
//						}
//					}
//
//				});
//
//		if (htmlMessage != null) {
//			wv_home.loadUrl(htmlMessage.getUrl());
//		}
//		// else {
//		// wv_home.loadUrl(url);
//		// }
//		// }
//		//
//		// runOnUiThread(new Runnable() {
//		//
//		// @Override
//		// public void run() {
//		// if (isHyperLink) {
//		// Log.e("跳转", hyperlink_address);
//		// setHyperlink(wv_home, hyperlink_address);
//		// }
//		// }
//		// });
//		// try {
//		// new Thread(new Runnable() {
//		//
//		// @Override
//		// public void run() {
//		// Message msg = handler.obtainMessage();
//		// msg.what = 0;
//		// handler.sendMessage(msg);
//		// }
//		// }).start();
//		// } catch (Exception e) {
//		// }
//
//	}
//
//	private void setupWebView(WebView engine) {
//		engine.setWebViewClient(new MyWebViewClient());
//		// 设置辅助功能
//		engine.setWebChromeClient(new MyWebChromeClient());
//
//		setWebViewAttribute(engine, log);
//	}
//
//	/**
//	 * 设置超链接
//	 */
//	private void setHyperlink(WebView view, String url) {
//		pullable=false;
//		pb_webview.setVisibility(View.VISIBLE);
//		ll_webview_bottom.setVisibility(View.VISIBLE);
//		view.loadUrl(url);
//		pageUrl = url;
//	}
//
//	private class MyWebViewClient extends WebViewClient {
//
//		// 超链接
//		@Override
//		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			if (log.isDebugEnabled()) {
//				log.debug("Loading content :" + url);
//			}
//
//			setHyperlink(view, url);
//			return super.shouldOverrideUrlLoading(view, url);
//		}
//
//		// 开始加载页面
//		@Override
//		public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//			if (log.isDebugEnabled()) {
//				log.debug("start loading page :" + url);
//			}
//
//			// callLater(new Runnable() {
//			// @Override
//			// public void run() {
//			//
//			// // 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
//			//
//			// if (MiniInformationActivity.this.wv_home.getProgress() < 100) {
//			// Message msg = new Message();
//			// msg.what = 1;
//			// handler.sendMessage(msg);
//			// }
//			// }
//			// }, 60, TimeUnit.SECONDS);
//		}
//
//		/*
//		 * (non-Javadoc)
//		 * 
//		 * @see
//		 * android.webkit.WebViewClient#onLoadResource(android.webkit.WebView,
//		 * java.lang.String)
//		 */
//		@Override
//		public void onLoadResource(WebView view, String url) {
//			if (log.isDebugEnabled()) {
//				log.debug("Loading content :" + url);
//			}
//			super.onLoadResource(view, url);
//		}
//	}
//
//	private class MyWebChromeClient extends WebChromeClient {
//		@Override
//		public void onProgressChanged(WebView view, int newProgress) {
//			if (newProgress == 100) {
//				pb_webview.setVisibility(View.GONE);
//			}
//		}
//
//		@Override
//		public void onReachedMaxAppCacheSize(long spaceNeeded,
//				long totalUsedQuota, QuotaUpdater quotaUpdater) {
//			quotaUpdater.updateQuota(spaceNeeded * 2);
//		}
//	}
//
//	/**
//	 * 助手播报
//	 * 
//	 * @return
//	 */
//	private View getSecondView() {
//		adapter = null;
//		View view = View.inflate(this, R.layout.mini_info_push_msg, null);
//		tv_mini_info_more = (TextView) view
//				.findViewById(R.id.tv_mini_info_more);
//		pb_mini_info_push = (ProgressBar) view
//				.findViewById(R.id.pb_mini_info_push);
//		pb_mini_info_list = (ProgressBar) view
//				.findViewById(R.id.pb_mini_info_list);
//
//		lv_mini_msg_push = (MyListView) view
//				.findViewById(R.id.lv_mini_msg_push);
//		// sv_mini_info_push = (ScrollView) view
//		// .findViewById(R.id.sv_mini_info_push);
//		ll_mini_info_push_bottom = (LinearLayout) view
//				.findViewById(R.id.ll_mini_info_push_bottom);
//		
//		tv_mini_info_loading = (TextView) view.findViewById(R.id.tv_mini_info_loading);
//
//		if (adapter == null) {
////			Log.i("getsecondview：", msgLists.size() + "");
//			adapter = new MiniInfoPushMsgAdapter(this, R.layout.push_msg_content);
//			lv_mini_msg_push.setAdapter(adapter);
//		}
//
//		tv_mini_info_more.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
////				start += 10;
////				currentCount = 1;
//				tv_mini_info_more.setVisibility(View.GONE);
//				pb_mini_info_push.setVisibility(View.VISIBLE);
//				getData();
//			}
//		});
//
//		lv_mini_msg_push.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//				Log.i("onitemclick:", position + "");
//
//				// leak_text_tab1.setTextColor(getResources().getColor(
//				// R.color.white));
//				// leak_text_tab2.setTextColor(getResources().getColor(
//				// R.color.leak_msg_bg));
//				// v_left.setVisibility(View.VISIBLE);
//				// v_right.setVisibility(View.INVISIBLE);
////				currentTab = 1;
//				// if (currentTab == 2) {
//				// ll_main_content.removeAllViews();
//				// ll_main_content.addView(getFirstView());
//				// }
//				
//				currentFlag = 1;
//				ll_main_content.removeAllViews();
//				ll_main_content.addView(getSimpleWebView());
//				HtmlMessageBean bean = adapter.getItem(position);
//				final String hyperlink_address = 
//						bean.getHtmlMessage().getUrl();
//				title = bean.getHtmlMessage().getTitle();
//				abstrct = bean.getHtmlMessage().getAbstrct();
//				imgUrl = bean.getHtmlMessage().getImage();
//				shareUrl = bean.getRemoteURL();
//				Log.i("分享地址", shareUrl);
//				runOnUiThread(new Runnable() {
//
//					@Override
//					public void run() {
//						if (log.isDebugEnabled()) {
//							log.debug("列表数据", hyperlink_address);
//						}
//
//						setHyperlink(wv_home, hyperlink_address);
//					}
//				});
//				// onResume();
//			}
//		});
//
//		return view;
//	}
//
//	private void getData() {
//
//		CMProgressMonitor monitor = new CMProgressMonitor(this) {
//
//			@Override
//			protected void handleFailed(Throwable cause) {
//				runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						pb_mini_info_push.setVisibility(View.GONE);
//						tv_mini_info_more.setVisibility(View.VISIBLE);
//					}
//				});
//			}
//
//			@Override
//			protected void handleDone(final Object result) {
//				if (result != null) {
//					HtmlMessageBean[] hmb = (HtmlMessageBean[]) result;
//					adapter.addAll(Arrays.asList(hmb));
////					Log.i("msgLists:", msgLists.size() + "");
//				}
////				Log.i("集合总数", listsTotal + "");
////				if (msgLists.size() == listsTotal) {
////					// 无数据
////					lists = null;
////				}
//				runOnUiThread(new Runnable() {
//					public void run() {
//						if (adapter.hasMore2Load()) {
//							ll_mini_info_push_bottom.setVisibility(View.VISIBLE);
//						}else{
//							ll_mini_info_push_bottom.setVisibility(View.GONE);
//						}
//						pb_mini_info_push.setVisibility(View.GONE);
//						tv_mini_info_more.setVisibility(View.VISIBLE);
////						adapter.updateData(msgLists);
//						tv_mini_info_loading.setVisibility(View.GONE);
//					}
//				});
//
//			}
//
//			@Override
//			protected Map<String, Object> getDialogParams() {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put(DIALOG_PARAM_KEY_TITLE, "提示：");
//				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在获取数据,请稍侯...");
//				return map;
//			}
//		};
//
//		monitor.executeOnMonitor(new Callable<Object>() {
//
//			@Override
//			public Object call() throws Exception {
//				adapter.setTotalRecords(getService(IMessageProviderModule.class)
//						.getTotalMessageHistorySize());
//				HtmlMessageBean[] htmlMsgBeans = getService(
//						IMessageProviderModule.class).getMessageHistory(adapter.getCount(),
//						10);
//				return htmlMsgBeans;
//			}
//		});
//
//	}
//
//	/**
//	 * 返回上一页
//	 */
//	private void goBackLast() {
//		wv_home.goBack();
//		ll_webview_bottom.setVisibility(View.GONE);
//		ll_minimsg_share.setVisibility(View.GONE);
//		pullable=true;
//	}
//
//	@Override
//	protected void onResume() {
//		this.ll_main_content.removeAllViews();	
//		 if (isHyperLink) {
//			this.ll_main_content.addView(getSimpleWebView());
//			String hyperlink_address = intent
//					.getStringExtra(Constant.HYPERLINK_ADDRESS);
//			setHyperlink(wv_home, hyperlink_address);
//		} else if(push_isHyperLink){
//			this.ll_main_content.addView(getSimpleWebView());
//			String hyperlink_address = intent.getStringExtra(Constant.HYPERLINK_ADDRESS);
//			title = intent.getStringExtra(Constant.PUSH_TITLE);
//			abstrct = intent.getStringExtra(Constant.PUSH_ABSTRCT);
//			imgUrl = intent.getStringExtra(Constant.PUSH_IMAGE);
//			shareUrl = intent.getStringExtra(Constant.PUSH_SHARE_ADDRESS);
//			setHyperlink(wv_home, hyperlink_address);
//			currentFlag = 1;
//			currentTab = 2;
//			leak_text_tab2.setTextColor(getResources().getColor(R.color.white));
//			leak_text_tab1.setTextColor(getResources().getColor(R.color.leak_msg_bg));
//			v_right.setVisibility(View.VISIBLE);
//			v_left.setVisibility(View.INVISIBLE);
//		} else if (currentTab == 1) {
//			pullable=true;
//			this.ll_main_content.addView(getFirstView());
//			refreshDailyNews(false);
//		}else{
//			this.ll_main_content.addView(getSecondView());
//			this.adapter.clear();
////			if (msgLists != null && msgLists.size() > 0) {
////				msgLists.clear();
////			}
//			getData();
//		}
//
//		// if (adapter != null && msgLists != null && currentTab == 2) {
//		// adapter.setData(msgLists);
//		// adapter.notifyDataSetChanged();
//		// sv_mini_info_push.smoothScrollTo(0, 0);
//		// }
//
//		super.onResume();
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.leak_tab1:
//			leak_text_tab1.setTextColor(getResources().getColor(R.color.white));
//			leak_text_tab2.setTextColor(getResources().getColor(
//					R.color.leak_msg_bg));
//			v_left.setVisibility(View.VISIBLE);
//			v_right.setVisibility(View.INVISIBLE);
//			if (currentTab == 2) {
//				currentTab = 1;
//				push_isHyperLink = false;
//				currentFlag = 0;
////				intent.removeExtra(Constant.PUSH_ISHYPERLINK);
//				// ll_main_content.removeAllViews();
//				// ll_main_content.addView(getFirstView());
//				// refreshDailyNews(false);
//				onResume();
//			}
//			break;
//
//		case R.id.leak_tab2:
//			leak_text_tab2.setTextColor(getResources().getColor(R.color.white));
//			leak_text_tab1.setTextColor(getResources().getColor(
//					R.color.leak_msg_bg));
//			v_right.setVisibility(View.VISIBLE);
//			v_left.setVisibility(View.INVISIBLE);
//			// pb_webview2.setVisibility(View.GONE);
//			if (currentTab == 1) {
//				currentTab = 2;
//				isHyperLink = false;
//				// ll_main_content.removeAllViews();
//				// ll_main_content.addView(getSecondView());
//				onResume();
//			}
//			break;
//
//		case R.id.iv_webview_share:
//			if (flag) {
//				ll_minimsg_share.setVisibility(View.VISIBLE);
//				flag = false;
//			} else {
//				ll_minimsg_share.setVisibility(View.GONE);
//				flag = true;
//			}
//			break;
//
//		case R.id.iv_webview_back:
//			if (wv_home.canGoBack())
//				goBackLast();
//			else if (currentFlag == 1) {
//				currentFlag = 0;
//				push_isHyperLink = false;
//				onResume();
//			} else
//				finish();
//			break;
//
//		case R.id.iv_msg_share:
//			Log.e("短信分享：", title + shareUrl);
//			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//					"smsto", "", null));
//			intent.putExtra("sms_body", title + shareUrl);
//			startActivity(intent);
//			currentFlag = 0;
//			break;
//
//		case R.id.iv_mail_share:
//			Log.e("邮件分享：", title + shareUrl);
//			intent = new Intent(Intent.ACTION_SEND);// 调用系统发邮件
//			intent.setType("text/plain");// 设置文本格式
//			intent.putExtra(Intent.EXTRA_EMAIL, "");// 设置对方邮件地址
//			intent.putExtra(Intent.EXTRA_SUBJECT, title);// 设置标题内容
//			intent.putExtra(Intent.EXTRA_TEXT, shareUrl);// 设置邮件文本内容
//			startActivity(Intent.createChooser(intent, "请选择邮件发送"));
//			currentFlag = 0;
//			break;
//
//		case R.id.iv_minimsg_share:
//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					Log.e("微信分享：", title + shareUrl + abstrct + imgUrl);
//					Intent in = new Intent();
//					in.setClass(MiniInformationActivity.this,
//							WXEntryActivity.class);
//					in.putExtra("title", title);
//					in.putExtra("description", abstrct);
//					in.putExtra("pageUrl", shareUrl);
//					in.putExtra("imgUrl", imgUrl);
//					startActivity(in);
//				}
//			});
//			currentFlag = 0;
//			break;
//
//		case R.id.iv_weibo_share:
//			Log.e("weibo分享：", title + shareUrl);
//			WeiboManager wm = new WeiboManager(MiniInformationActivity.this);
//			if (wm.isBind()) {
//				try {
//					wm.share2weibo(title + shareUrl, null);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} else {
//				wm.bind();
//			}
//			currentFlag = 0;
//			break;
//		}
//	}
//
	@Override
	public void onReq(BaseReq req) {
//		if (log.isDebugEnabled()) {
//			log.debug("wx onReq :" + req.toString());
//		}
	}

	@Override
	public void onResp(BaseResp resp) {
//		if (log.isDebugEnabled()) {
//			log.debug("wx onResp :" + resp.toString());
//		}
	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			if (wv_home.canGoBack()) {
//				goBackLast();
//				return true;
//			}else if(currentFlag == 1){
//				currentFlag = 0;
//				push_isHyperLink = false;
//				onResume();
//				return true;
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	@Override
//	protected void onDestroy() {
//		// hyperlink_address = null;
//		// isHyperLink = false;
//		adapter = null;
//		super.onDestroy();
//	}

}
