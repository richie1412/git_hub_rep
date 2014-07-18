package com.wxxr.callhelper.qg.ui.gd;

import java.util.HashMap;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;
import com.wxxr.callhelper.qg.ui.ReaderPanelContext;
import com.wxxr.callhelper.qg.ui.WebViewFragement;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.CMProgressBar;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.rule.api.Handle;

public class GD21CityCheapListActivity extends FragmentActivity implements OnClickListener {
	private View no_net;
	
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

          Handler   mhandler=new  Handler(){
        	  
        	  public void handleMessage(android.os.Message msg) {
        		  if(msg.what==0){
        		  if(!loadover){
        			ll_main.removeAllViews();
        			ll_main.setVisibility(View.GONE);
        			no_net.setVisibility(View.VISIBLE);
        			
        		  }
        		  }else{
        			
        		  }
        		  
        	  };   	  
        	  
        	  
        	  
          };

	private CheaplistWeb myweb;

	private ViewGroup ll_main;
	
	private boolean loadover=false;
	CMProgressBar  prcessbar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guess_like_main);
	
		mhandler.sendEmptyMessageDelayed(0, 1000*20);
		
		String tt = getIntent().getStringExtra("title");
		if(tt.endsWith(">")){
			tt=tt.substring(0, tt.length()-1);
		}
		
		((TextView) findViewById(R.id.tv_titlebar_name)).setText(tt);
		
		 no_net= findViewById(R.id.no_net_div);
		
		 ll_main= (ViewGroup)findViewById(R.id.ll_main);
		 findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		 findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		
		findView();
		processLogic();
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this,2);
	}

	private void findView() {
	}

	private void processLogic() {

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		myweb      =	new CheaplistWeb(readerCtx);
		transaction.replace(R.id.ll_main,myweb);
		transaction.commit();
	}

	private class CheaplistWeb extends WebViewFragement {
		private final Trace log = Trace.getLogger(CheaplistWeb.class);
		private String rootUrl = AppUtils.getService(
				IClientConfigManagerService.class).getCityCheapHomeUrl();

		public CheaplistWeb(ReaderPanelContext closer) {
			super(closer);
		}

		@Override
		public void onPageLoadErr() {
			// TODO Auto-generated method stub		
		
		}
				
		@Override
		protected void onPageLoadingCompleted(WebView view, String url) {
			// TODO Auto-generated method stub
			loadover=true;
			mhandler.sendEmptyMessage(1);
			super.onPageLoadingCompleted(view, url);
		}
		
		
		@Override
		protected void getHtmlFromServer(AbstractProgressMonitor monitor2) {
			AppUtils.getService(IOfficeLineHtmlProvideService.class)
			.get21CheapHome(
					AppUtils.getService(
							IClientConfigManagerService.class)
							.getCityCheapHomeUrl(), monitor2);
		}
		
		
		@Override
		protected HtmlMessage getLococalHtmlMessage(
				AbstractProgressMonitor minitor) {
			return AppUtils.getService(IOfficeLineHtmlProvideService.class)
					.get21CheapHome(
							AppUtils.getService(
									IClientConfigManagerService.class)
									.getCityCheapHomeUrl(), minitor);

		}

		@Override
		protected boolean isSharable(String url) {
			return false;
		}

		/**
		 * @param url
		 * @return
		 */
		protected String getLocalUrl(String url) {
			return rootUrl;
		}

		@Override
		protected boolean isReloadable(String url) {
			return true;
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
			} else {
			}
			super.onPageLoadingStarted(view, url);
		}

		@Override
		protected boolean shouldOverrideContentLoading(WebView view, String url) {
			if (url.contains("area=")) {
				
				if(!url.contains("area=null")){
				
				HashMap<String, String> keys = Tools.divParameFromURL(url);
				//
				// channelCd=RWLS&amp;channelName=人文历史
				// PingDaoBean bean = new PingDaoBean(keys.get("channelCd"),
				// keys.get("channelName"));

				Intent t = new Intent(getBaseContext(),
						GD21CityCheapListOfOneCityActivity.class);
				t.putExtra("area", keys.get("area"));
				t.putExtra("city", keys.get("city"));
				startActivity(t);
				}
				return true;
			}
			return false;
		}

	}

	@Override
	public void onClick(View v) {
	switch (v.getId()) {
	case R.id.gd_iv_titlebar_left:
		finish();
		break;
	default:
		break;
	}
		
	}

}
