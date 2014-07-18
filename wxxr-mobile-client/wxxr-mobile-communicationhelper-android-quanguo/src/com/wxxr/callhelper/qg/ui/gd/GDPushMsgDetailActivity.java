package com.wxxr.callhelper.qg.ui.gd;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.WeiboManager;
import com.wxxr.callhelper.qg.wxapi.WXEntryActivity;
import com.wxxr.mobile.android.app.AppUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 广东版——助手播报详情页面
 * 
 * @author cuizaixi
 * 
 */
public class GDPushMsgDetailActivity extends BaseActivity {
	private ImageView iv_pingdao_icon;
	private ImageView iv_content_icon;
	private TextView tv_title;
	private TextView tv_date;
	private TextView tv_from;
	private TextView tv_content_abstract;
	private HtmlMessageBean bean;
	private TextView tv_name;
	private WebView wv;
	private String initURL;
	private View root;
	private View head;
	private int shareaction;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.gd_zhushubobao_detail);
		root = findViewById(R.id.sharebar_root);
		head = findViewById(R.id.ll_top_titlebar);
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
		wv = (WebView) findViewById(R.id.wv);
		iv_right.setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_network_not_access).setVisibility(View.GONE);
		tv_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_name.setText("助手播报");
		if (getIntent().getStringExtra("title") != null) {
			tv_name.setText(getIntent().getStringExtra("title"));
		}
		// iv_pingdao_icon = (ImageView)
		// findViewById(R.id.gd_weblist_title_icon);
		// tv_title = (TextView) findViewById(R.id.weblist_title);
		// tv_date = (TextView) findViewById(R.id.weblist_content_date);
		// tv_from = (TextView) findViewById(R.id.weblist_from);
		// tv_content_abstract = (TextView)
		// findViewById(R.id.weblist_content_abstract);
		// iv_content_icon = (ImageView) findViewById(R.id.weblist_content_img);
	}

	private void processLogic() {
		bean = (HtmlMessageBean) getIntent().getSerializableExtra(
				Constant.PINGDAO_INFO);
		shareaction=getIntent().getIntExtra(Constant.SHARE_ID, -1);
		if (bean != null) {
			initURL = bean.getHtmlMessage().getOrigUrl();
			wv.loadUrl(bean.getHtmlMessage().getOrigUrl());
			wv.setWebChromeClient(new MyWebChromeClient());
			wv.getSettings().setJavaScriptEnabled(true);
			wv.setWebViewClient(new MyWebViewClient());
			final HtmlMessage htmlMessage = bean.getHtmlMessage();
			// 微博分享
			findViewById(R.id.weblist_content_weibo).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(shareaction!=-1&&AppUtils.getService(IUserUsageDataRecorder.class)!=null){
								AppUtils.getService(IUserUsageDataRecorder.class).doRecord(shareaction);								
							}
							
							WeiboManager wm = new WeiboManager(
									GDPushMsgDetailActivity.this);
							String conent = htmlMessage.getTitle() + "  "
									+ htmlMessage.getOrigUrl() + "  "
									+ htmlMessage.getAbstrct();
							if (wm.isBind()) {
								try {
									wm.share2weibo(conent, null);
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								wm.bindAndShare2weibo(conent, null);
							}
						}
					});
			// 微信分享
			findViewById(R.id.weblist_content_weixin).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(shareaction!=-1&&AppUtils.getService(IUserUsageDataRecorder.class)!=null){
								AppUtils.getService(IUserUsageDataRecorder.class).doRecord(shareaction);								
							}
							Intent in = new Intent();
							in.setClass(GDPushMsgDetailActivity.this,
									WXEntryActivity.class);
							in.putExtra("title", htmlMessage.getTitle());
							in.putExtra("description", htmlMessage.getAbstrct());
							in.putExtra("pageUrl", htmlMessage.getOrigUrl());
							in.putExtra("imgUrl", htmlMessage.getListimageUrl());
							startActivity(in);
						}
					});
			// 短信分享
			findViewById(R.id.weblist_content_sms).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							if(shareaction!=-1&&AppUtils.getService(IUserUsageDataRecorder.class)!=null){
								AppUtils.getService(IUserUsageDataRecorder.class).doRecord(shareaction);								
							}
							Intent intent = new Intent(Intent.ACTION_SENDTO,
									Uri.fromParts("smsto", "", null));
							intent.putExtra("sms_body", htmlMessage.getTitle()
									+ htmlMessage.getOrigUrl());
							startActivity(intent);
						}
					});
		}
	}
	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			wv.setVisibility(View.INVISIBLE);
			findViewById(R.id.gd_network_not_access)
					.setVisibility(View.VISIBLE);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Intent intent = new Intent(GDPushMsgDetailActivity.this,
					GDPushMsgWebLinkActivity.class);
			intent.putExtra(Constant.DIALOG_KEY, Constant.LOAD_DETAIL);
			intent.putExtra("url", url);
			startActivityIfNeeded(intent, 1);
			return true;
		}
	}
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			if (view.getUrl().equals(initURL)) {
				head.setVisibility(View.VISIBLE);
				root.setVisibility(View.VISIBLE);
			} else {
				head.setVisibility(View.GONE);
				root.setVisibility(View.GONE);
			}

		}
	}
}