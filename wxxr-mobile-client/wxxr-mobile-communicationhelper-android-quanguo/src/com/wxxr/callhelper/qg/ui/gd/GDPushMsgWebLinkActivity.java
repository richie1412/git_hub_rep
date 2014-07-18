package com.wxxr.callhelper.qg.ui.gd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
/**
 * 广东版——网页链接浏览activity。
 * 
 * @author cuizaixi
 * 
 */
public class GDPushMsgWebLinkActivity extends BaseActivity {
	public String initurl = null;
	private WebView wv;
	private TextView tv_titlebar_name;
	private ImageView iv_titlebar_left;
	private LinearLayout no_network;
	private String titleFromInt;
	private View titlediv;
	private Intent mIntent;
	private String mKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd__push_msg_web_link);
		wv = (WebView) findViewById(R.id.wv);
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("加载中...");
		ImageView iv_titlebar_right = (ImageView) findViewById(R.id.gd_iv_titlebar_right);
		iv_titlebar_right.setVisibility(View.INVISIBLE);
		iv_titlebar_left = (ImageView) findViewById(R.id.gd_iv_titlebar_left);
		iv_titlebar_left.setOnClickListener(this);
		no_network = (LinearLayout) findViewById(R.id.no_network);
		no_network.setVisibility(View.INVISIBLE);
		mIntent = getIntent();
		if (getIntent().getStringExtra("push") != null
				&& AppUtils.getFramework().getService(
						IUserUsageDataRecorder.class) != null) {
			AppUtils.getFramework().getService(IUserUsageDataRecorder.class)
					.doRecord(ActivityID.LOOKSYSNOTICE.ordinal());
		}

		mKey = mIntent.getStringExtra(Constant.DIALOG_KEY);
		wv.getSettings().setJavaScriptEnabled(true);
		wv.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		if (!(getService(IDataExchangeCoordinator.class)
				.checkAvailableNetwork() > 0)) {
			wv.setVisibility(View.GONE);
			no_network.setVisibility(View.VISIBLE);

			if (FragmentPersonCenter.MOBLIE_MARKET.equals(mKey)) {
				tv_titlebar_name.setText("市场评分");

			} else if (Constant.MOBLE_APP_RECOMMEND.equals(mKey)) {
				tv_titlebar_name.setText("移动应用推荐");
			}
		} else {
			if (FragmentPersonCenter.MOBLIE_MARKET.equals(mKey)) {

				wv.loadUrl("http://mm.10086.cn/android/info/221039.html?from=www&stag=cT0lRTklODAlOUElRTQlQkYlQTElRTUlOEElQTklRTYlODklOEImcD0xJnQ9JUU1JTg1JUE4JUU5JTgzJUE4JnNuPTEmYWN0aXZlPTE%3D");
				wv.setWebChromeClient(new MyWebChromeClient());
				wv.setWebViewClient(new MyWebViewClient());
			} else if (Constant.MOBLE_APP_RECOMMEND.equals(mKey)) {

				wv.loadUrl(getService(IClientConfigManagerService.class)
						.getApp_RecommendUrl());
				wv.setWebChromeClient(new MyWebChromeClient());
				wv.setWebViewClient(new MyWebViewClient());
				wv.setDownloadListener(new MyDownLoadListener());

			} else if (Constant.LOAD_DETAIL.equals(mKey)) {
				String url = mIntent.getStringExtra("url");
				wv.loadUrl(url);
				wv.setWebChromeClient(new MyWebChromeClient());
				wv.setWebViewClient(new MyWebViewClient());
				wv.setDownloadListener(new MyDownLoadListener());
			}
		}
	}
	
	
	
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;
		}
	}

	@Override
	public void onBackPressed() {
		if (wv.canGoBack()) {
			wv.goBack();
		} else {
			finish();
		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			wv.setVisibility(View.GONE);
			no_network.setVisibility(View.VISIBLE);

		}
	}
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			tv_titlebar_name.setText(title);
			if ((!wv.canGoBack())&&Constant.MOBLE_APP_RECOMMEND.equals(mKey)) {
				tv_titlebar_name.setText("移动应用推荐");
			}
		}
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				if (FragmentPersonCenter.MOBLIE_MARKET.equals(mKey)) {
					tv_titlebar_name.setText("Mobile Market 评分");
				}
			}
		}
	}
	private class MyDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			// if (!Environment.getExternalStorageState().equals(
			// Environment.MEDIA_MOUNTED)) {
			// Toast t = Toast.makeText(GDPushMsgWebLinkActivity.this,
			// "需要SD卡。", Toast.LENGTH_LONG);
			// t.setGravity(Gravity.CENTER, 0, 0);
			// t.show();
			// return;
			// }
			// DownloaderTask task = new DownloaderTask();
			// task.execute(url);
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

		// 自定义的下载器，容易出现问题，暂时不用。
		// } // 内部类
		// private class DownloaderTask extends AsyncTask<String, Void, String>
		// {
		//
		// public DownloaderTask() {
		// }
		//
		// @Override
		// protected String doInBackground(String... params) {
		// // TODO Auto-generated method stub
		// String url = params[0];
		// // Log.i("tag", "url="+url);
		// String fileName = url.substring(url.lastIndexOf("/") + 1);
		// fileName = URLDecoder.decode(fileName);
		// Log.i("tag", "fileName=" + fileName);
		//
		// File directory = Environment.getExternalStorageDirectory();
		// File file = new File(directory, fileName);
		// if (file.exists()) {
		// Log.i("tag", "The file has already exists.");
		// return fileName;
		// }
		// try {
		// HttpClient client = new DefaultHttpClient();
		// //
		// client.getParams().setIntParameter("http.socket.timeout",3000);//设置超时
		// HttpGet get = new HttpGet(url);
		// HttpResponse response = client.execute(get);
		// if (HttpStatus.SC_OK == response.getStatusLine()
		// .getStatusCode()) {
		// HttpEntity entity = response.getEntity();
		// InputStream input = entity.getContent();
		//
		// writeToSDCard(fileName, input);
		//
		// input.close();
		// // entity.consumeContent();
		// return fileName;
		// } else {
		// return null;
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// return null;
		// }
		// }
		//
		// @Override
		// protected void onCancelled() {
		// // TODO Auto-generated method stub
		// super.onCancelled();
		// }
		//
		// @Override
		// protected void onPostExecute(String result) {
		// // TODO Auto-generated method stub
		// super.onPostExecute(result);
		// closeProgressDialog();
		// if (result == null) {
		// Toast t = Toast.makeText(GDPushMsgWebLinkActivity.this,
		// "连接错误！请稍后再试！", Toast.LENGTH_LONG);
		// t.setGravity(Gravity.CENTER, 0, 0);
		// t.show();
		// return;
		// }
		//
		// Toast t = Toast.makeText(GDPushMsgWebLinkActivity.this, "已保存到SD卡。",
		// Toast.LENGTH_LONG);
		// t.setGravity(Gravity.CENTER, 0, 0);
		// t.show();
		// File directory = Environment.getExternalStorageDirectory();
		// File file = new File(directory, result);
		// Log.i("tag", "Path=" + file.getAbsolutePath());
		// Intent intent = getFileIntent(file);
		// startActivity(intent);
		//
		// }
		//
		// @Override
		// protected void onPreExecute() {
		// // TODO Auto-generated method stub
		// super.onPreExecute();
		// showProgressDialog();
		// }
		//
		// @Override
		// protected void onProgressUpdate(Void... values) {
		// // TODO Auto-generated method stub
		// super.onProgressUpdate(values);
		// }
		//
		// }
		// private ProgressDialog mDialog;
		// private void showProgressDialog() {
		// if (mDialog == null) {
		// mDialog = new ProgressDialog(GDPushMsgWebLinkActivity.this);
		// mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
		// mDialog.setMessage("正在加载 ，请等待...");
		// mDialog.setIndeterminate(false);// 设置进度条是否为不明确
		// mDialog.setCancelable(true);// 设置进度条是否可以按退回键取消
		// mDialog.setCanceledOnTouchOutside(false);
		// mDialog.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface dialog) {
		// // TODO Auto-generated method stub
		// mDialog = null;
		// }
		// });
		// mDialog.show();
		//
		// }
		// }
		// private void closeProgressDialog() {
		// if (mDialog != null) {
		// mDialog.dismiss();
		// mDialog = null;
		// }
		// }
		// public Intent getFileIntent(File file) {
		// // Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
		// Uri uri = Uri.fromFile(file);
		// String type = getMIMEType(file);
		// Log.i("tag", "type=" + type);
		// Intent intent = new Intent("android.intent.action.VIEW");
		// intent.addCategory("android.intent.category.DEFAULT");
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setDataAndType(uri, type);
		// return intent;
		// }
		//
		// public void writeToSDCard(String fileName, InputStream input) {
		//
		// if (Environment.getExternalStorageState().equals(
		// Environment.MEDIA_MOUNTED)) {
		// File directory = Environment.getExternalStorageDirectory();
		// File file = new File(directory, fileName);
		// // if(file.exists()){
		// // Log.i("tag", "The file has already exists.");
		// // return;
		// // }
		// try {
		// FileOutputStream fos = new FileOutputStream(file);
		// byte[] b = new byte[2048];
		// int j = 0;
		// while ((j = input.read(b)) != -1) {
		// fos.write(b, 0, j);
		// }
		// fos.flush();
		// fos.close();
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// } else {
		// Log.i("tag", "NO SDCard.");
		// }
		// }
		//
		// private String getMIMEType(File f) {
		// String type = "";
		// String fName = f.getName();
		// /* 取得扩展名 */
		// String end = fName
		// .substring(fName.lastIndexOf(".") + 1, fName.length())
		// .toLowerCase();
		//
		// /* 依扩展名的类型决定MimeType */
		// if (end.equals("pdf")) {
		// type = "application/pdf";//
		// } else if (end.equals("m4a") || end.equals("mp3") ||
		// end.equals("mid")
		// || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
		// type = "audio/*";
		// } else if (end.equals("3gp") || end.equals("mp4")) {
		// type = "video/*";
		// } else if (end.equals("jpg") || end.equals("gif") ||
		// end.equals("png")
		// || end.equals("jpeg") || end.equals("bmp")) {
		// type = "image/*";
		// } else if (end.equals("apk")) {
		// /* android.permission.INSTALL_PACKAGES */
		// type = "application/vnd.android.package-archive";
		// }
		// // else if(end.equals("pptx")||end.equals("ppt")){
		// // type = "application/vnd.ms-powerpoint";
		// // }else if(end.equals("docx")||end.equals("doc")){
		// // type = "application/vnd.ms-word";
		// // }else if(end.equals("xlsx")||end.equals("xls")){
		// // type = "application/vnd.ms-excel";
		// // }
		// else {
		// // /*如果无法直接打开，就跳出软件列表给用户选择 */
		// type = "*/*";
		// }
		// return type;
	}
}
