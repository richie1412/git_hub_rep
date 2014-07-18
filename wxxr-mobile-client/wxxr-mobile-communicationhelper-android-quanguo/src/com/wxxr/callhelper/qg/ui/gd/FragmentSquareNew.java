package com.wxxr.callhelper.qg.ui.gd;

import java.net.URLDecoder;
import java.util.concurrent.Callable;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.service.IHtmlMessageManager;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.utils.WeiboManager;
import com.wxxr.callhelper.qg.wxapi.WXEntryActivity;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;

public class FragmentSquareNew extends Fragment implements OnClickListener {

	public static long lasttime = 0;
	static final long TIME_OUT = 2 * 60 * 60 * 1000L;

	View main;
	WebView wv_home;

	private TextView titleView;

	Handler mhandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			wv_home.invalidate();
		};

	};

	private View no_network, emptyimg;

	private View cheadiv;

	private View cheapdivline;
	private View square_bot_sharebar;
	private int pagelevel = 1;
	public String sharetitle;
	public String shareurl;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 主界面控件
		main = inflater.inflate(R.layout.square_web, container, false);
		// if(wv_home!=null){
		// wv_home.stopLoading();
		// }

		wv_home = (WebView) main.findViewById(R.id.square_webview);
		emptyimg = main.findViewById(R.id.square_empty);
		wv_home.setWebViewClient(new MyWebViewClient());
		// 设置辅助功能
		wv_home.setWebChromeClient(new MyWebChromeClient());
		// wv_home.getSettings().setPluginsEnabled(true);
		wv_home.getSettings().setJavaScriptEnabled(true);

		// wv_home.getSettings().setJavaScriptEnabled(true);
		// wv_home.getSettings().setBuiltInZoomControls(false);
		// engine.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.116 Safari/537.36");
		wv_home.getSettings().setLoadsImagesAutomatically(true);
		// wv_home.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		// wv_home.setInitialScale(4);
		// wv_home.getLayoutParams().height=1600;
		no_network = main.findViewById(R.id.no_network);
		square_bot_sharebar = main.findViewById(R.id.square_bot_sharebar);
		square_bot_sharebar.findViewById(R.id.weblist_content_weibo)
				.setOnClickListener(this);
		square_bot_sharebar.findViewById(R.id.weblist_content_weixin)
				.setOnClickListener(this);
		square_bot_sharebar.findViewById(R.id.weblist_content_sms)
				.setOnClickListener(this);
		
		return main;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
        while(canBack()){
        	wv_home.goBack();
        }
        pagelevel=1;
		IDataExchangeCoordinator s = AppUtils
				.getService(IDataExchangeCoordinator.class);

		String squarurl = AppUtils
				.getService(IClientConfigManagerService.class).getSquarelUrl();
		HtmlMessage html = getLococalHtmlMessage(null);
		if (s.checkAvailableNetwork() < 0) {
			if (html != null) {
				refreshContent(false);
			} else {
				no_network.setVisibility(View.VISIBLE);
			}
		} else if (squarurl == null || squarurl.length() < 10) {
			emptyimg.setVisibility(View.VISIBLE);
			Toast.makeText(this.getActivity(), "该地区广场正在积极建设中，敬请期待！", 1).show();
		} else {
			long lastUpdateTime = lasttime;
			if ((System.currentTimeMillis() - lastUpdateTime) > (TIME_OUT)) {

				refreshContent(true);
			} else {
				refreshContent(false);
			}
			// hideProcesbar();
		}
       processBar();
	}

	public boolean canBack() {
		return wv_home.canGoBack();
	}

	public void goBack() {
		pagelevel=pagelevel-1;
		wv_home.goBack();
		processBar();
		
	}

	private void processBar() {
		
		if (pagelevel == 2) {
			square_bot_sharebar.setVisibility(View.VISIBLE);
		} else {
			square_bot_sharebar.setVisibility(View.GONE);
		}
	}

	protected void refreshContent(boolean refreshing) {

		if (!refreshing) {

			HtmlMessage html = getLococalHtmlMessage(null);
			if (html != null) {
				wv_home.loadUrl(((HtmlMessage) html).getUrl());
			}
		} else {

			final GDCMProgressMonitor pp = new GDCMProgressMonitor(
					this.getActivity()) {
				@Override
				protected void handleDone(final Object returnVal) {
					if (returnVal instanceof HtmlMessage) {
						lasttime = System.currentTimeMillis();
						if (getActivity() != null) {
							// TODO Auto-generated method stub
							wv_home.loadUrl(((HtmlMessage) returnVal).getUrl());
							titleView.setText(((HtmlMessage) returnVal)
									.getCheap21cityname());
						}

						// finishRefresh();
					} else {

						no_network.setVisibility(View.VISIBLE);
					}

				}

				@Override
				protected void handleFailed(Throwable cause) {
					// TODO Auto-generated method stub

				}

			};

			pp.executeOnMonitor(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					// TODO Auto-generated method stub
					return getHtmlFromServer0(pp);
				}
			});

		}

	}

	private HtmlMessage getHtmlFromServer0(AbstractProgressMonitor monitor2) {
		return AppUtils.getService(IOfficeLineHtmlProvideService.class)
				.getSquareHome(
						AppUtils.getService(IClientConfigManagerService.class)
								.getSquarelUrl(), monitor2);
	}

	protected HtmlMessage getLococalHtmlMessage(AbstractProgressMonitor minitor) {
		String code = AppUtils.getService(IClientCustomService.class)
				.getProviceCode();
		if (code != null) {
			code = code.toLowerCase();
		}
		HtmlMessage html = AppUtils.getService(IHtmlMessageManager.class)
				.getHtmlMessage("/squarehome/" + code + "/");

		if (html != null) {
			final String tilte = html.getCheap21cityname();
			if (getActivity() != null) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (tilte != null && tilte.length() > 0) {
							titleView.setText(tilte);
						}
					}
				});
			}
		}

		return html;

	}

	public void setViews(View pcheadiv, View pcheapdivline,
			android.widget.TextView cheaptitleView) {
		cheadiv = pcheadiv;
		cheapdivline = pcheapdivline;
		titleView = cheaptitleView;
		cheadiv.setOnClickListener(this);
		titleView.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_minimsg_21city_cheap:
		case R.id.ll_minimsg_21city_cheap_txt:
			// 快速切换 时 ，可能是null
			if (getActivity() != null) {
				Intent a = new Intent(getActivity(),
						GD21CityCheapListActivity.class);
				a.putExtra("title", titleView.getText());
				startActivity(a);
			}
			break;

		case R.id.weblist_content_weibo:
			WeiboManager wm = new WeiboManager(
					FragmentSquareNew.this.getActivity());
			String conent =sharetitle +"  "+shareurl;
			if (wm.isBind()) {
				try {
					wm.share2weibo(conent, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				wm.bindAndShare2weibo(conent, null);
			}
			break;
		case R.id.weblist_content_weixin:
			Intent in = new Intent();
			in.setClass(FragmentSquareNew.this.getActivity(),
					WXEntryActivity.class);
			in.putExtra("title", sharetitle);
		//	in.putExtra("description", htmlMessage.getAbstrct());
			in.putExtra("pageUrl", shareurl);
		//	in.putExtra("imgUrl", htmlMessage.getListimageUrl());
			startActivity(in);
			break;
		case R.id.weblist_content_sms:
			String content =sharetitle +"  "+shareurl;
			Tools.ShareBySMS(FragmentSquareNew.this.getActivity(), content);
			break;

		default:
			break;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if ("GD".equals(AppUtils.getService(IClientCustomService.class)
				.getProviceCode())) {
			String url = AppUtils.getService(IClientConfigManagerService.class)
					.getCityCheapHomeUrl();
			if (url != null && url.length() > 8) {
				cheadiv.setVisibility(View.VISIBLE);
				cheapdivline.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		cheadiv.setVisibility(View.GONE);
		cheapdivline.setVisibility(View.GONE);
        pagelevel=1;
	}

	private class MyWebViewClient extends WebViewClient {

		// 超链接
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			url = URLDecoder.decode(url);
			if (AppUtils.getService(IUserActivationService.class)
					.isUserActivated() && (url.contains(Constant.ACTION_LOGIN))) {
				Toast.makeText(AppUtils.getFramework().getAndroidApplication(),
						"您已登录，无需再次登录", Toast.LENGTH_LONG).show();
				return true;
			}

			if (Tools.processAction(url, getActivity().getBaseContext())) {
				return true;
			}
			String squareurl=AppUtils.getService(IClientConfigManagerService.class).getSquarelUrl();
			if(url.equals(squareurl)){
				pagelevel=1;
			}else{
				pagelevel=pagelevel+1;
				if(url.equals(shareurl)){
					pagelevel=2;
				}
		
			}
			if (pagelevel == 2) {
				shareurl = url;
			} 
			processBar();
			return false;
		}

		// 开始加载页面
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
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

			super.onPageFinished(view, url);

		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

			super.onReceivedError(view, errorCode, description, failingUrl);
		}

	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			if (pagelevel == 2) {
				sharetitle = title;
			}
			super.onReceivedTitle(view, title);
		}

	}
}
