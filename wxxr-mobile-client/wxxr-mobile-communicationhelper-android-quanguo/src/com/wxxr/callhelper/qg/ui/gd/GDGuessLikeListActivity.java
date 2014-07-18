package com.wxxr.callhelper.qg.ui.gd;

import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.bean.PingDaoBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.service.IOfficeLineHtmlProvideService;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.ReaderPanelActivity;
import com.wxxr.callhelper.qg.ui.ReaderPanelContext;
import com.wxxr.callhelper.qg.ui.WebViewFragement;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.log.api.Trace;

public class GDGuessLikeListActivity extends FragmentActivity {
	private ImageView iv_right;
	private PingDaoBean pingdao;
	
	
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
	private View no_net_div;

	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guess_like_main);
		findView();
		processLogic();
		((com.wxxr.callhelper.qg.widget.BottomTabBar) findViewById(R.id.home_bottom_tabbar))
				.setActivtiy(this, 1);
	}

	private void findView() {
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		TextView tv_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_name.setText("猜你喜欢");
		no_net_div=findViewById(R.id.no_net_div);
		if(	AppUtils.getService(IDataExchangeCoordinator.class).checkAvailableNetwork()<=0){
			no_net_div.setVisibility(View.VISIBLE);
			findViewById(R.id.ll_main).setVisibility(View.GONE);
	    }
		/*
		 * iv_right.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { if
		 * (AppUtils.getService(IUserActivationService.class)
		 * .isUserActivated()) {
		 * iv_right.setBackgroundResource(R.drawable.caini_titlebar_press);
		 * startActivity(new Intent(GDGuessLikeListActivity.this,
		 * GDSmsHuiZhiSettingActivity.class)); } else { Intent intent = new
		 * Intent(GDGuessLikeListActivity.this, ConfirmDialogActivity.class);
		 * intent.putExtra(Constant.DIALOG_KEY, Constant.DO_ACTIVE);
		 * startActivity(intent); } } });
		 */
	}
	private void processLogic() {
		Intent intent = getIntent();
		if (intent != null) {
			pingdao = (PingDaoBean) intent
					.getSerializableExtra(Constant.CLIENT_INFO);
		}
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.ll_main, new GuessLike(readerCtx));
		transaction.commit();
	}
	private class GuessLike extends WebViewFragement {
		private final Trace log = Trace.getLogger(GuessLike.class);
		String rootUrl = AppUtils.getService(IClientConfigManagerService.class)
				.getGuessLikeInfoUrl();
		String specificUrl = initSpecificUrl();
		public GuessLike(ReaderPanelContext closer) {
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
		protected void getHtmlFromServer(AbstractProgressMonitor monitor2) {
			AppUtils.getService(IOfficeLineHtmlProvideService.class)
					.getGuessYouLikeHome(
							AppUtils.getService(
									IClientConfigManagerService.class)
									.getGuessLikeInfoUrl(), monitor2);
		}

		@Override
		protected HtmlMessage getLococalHtmlMessage(
				AbstractProgressMonitor monitor) {
			return AppUtils.getService(IOfficeLineHtmlProvideService.class)
					.getGuessYouLikeHome(
							AppUtils.getService(
									IClientConfigManagerService.class)
									.getGuessLikeInfoUrl(), monitor);
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
			super.onResume();
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

		}

		@Override
		protected void onPageLoadingStarted(WebView view, String url) {
			if (isReloadable(url)) {
			} else {
			}
			super.onPageLoadingStarted(view, url);
		}

		@Override
		protected boolean shouldOverrideContentLoading(WebView view, String url) {
			if (url.contains("channelCd")) {
				Intent intent = new Intent(getBaseContext(),
						GDGuessLikeDetailActivity.class);
				// channelCd=RWLS&amp;channelName=人文历史
				HashMap<String, String> keys = Tools.divParameFromURL(url);
				PingDaoBean bean = new PingDaoBean(keys.get("channelCd"),
						keys.get("channelName"));
				intent.putExtra(Constant.PINGDAO_INFO, bean);
				startActivity(intent);
				return true;
			}
			return false;
		}
		@Override
		protected String getSpecificUrl() {
			super.getSpecificUrl();
			return this.specificUrl;
		}
		@Override
		protected void onRefreshAction() {
			super.onRefreshAction();
			specificUrl = "";
		}
	}
	private String initSpecificUrl() {
		return pingdao == null ? "" : "#" + pingdao.getChannalCode();
	}
}