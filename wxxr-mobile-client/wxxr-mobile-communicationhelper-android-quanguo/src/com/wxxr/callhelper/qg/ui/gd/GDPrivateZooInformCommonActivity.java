package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.platformtools.Log;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IFeedBackService;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.adapter.AppsRecommendAdapter;
import com.wxxr.callhelper.qg.bean.AppsRecommend;
import com.wxxr.callhelper.qg.bean.UserBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.UserDetailVO;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 广东版——个人信息编辑通用activity
 * 
 * @author cuizaixi
 * 
 */
public class GDPrivateZooInformCommonActivity extends BaseActivity {
	private Intent mIntent;
	private int mEditType;
	private LinearLayout ll_nick_name;
	private LinearLayout ll_gendar;
	private LinearLayout ll_idea_back;
	private TextView tv_titlebar_name;
	private EditText et_nick_name;
	private UserDetailVO user;
	private EditText et_tel;
	private EditText et_feedback;
	private ImageView iv_male;
	private ImageView iv_femal;
	private UserBean mCachedUser;
	private ImageView iv_titlebar_left;
	private View iv_titlebar_right;
	private ListView lv_apps_recommend;
	private WebView wv_apps_page;
	private LinearLayout no_network;
	private ProgressDialog progressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_private_inform_common);
		findView();
		getInts();
		processLogic();
	}
	private void findView() {
		// 标题控件
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		iv_titlebar_right = findViewById(R.id.gd_iv_titlebar_right);
		iv_titlebar_left = (ImageView) findViewById(R.id.gd_iv_titlebar_left);
		iv_titlebar_left.setOnClickListener(this);
		iv_titlebar_right.setOnClickListener(this);
		findViewById(R.id.iv_clear).setOnClickListener(this);
		// 相关模块控件(昵称、性别、反馈)
		ll_nick_name = (LinearLayout) findViewById(R.id.rl_nick_name);
		ll_gendar = (LinearLayout) findViewById(R.id.ll_gendar);
		ll_idea_back = (LinearLayout) findViewById(R.id.ll_idea_back);
		// 子控件———反馈
		et_nick_name = (EditText) findViewById(R.id.et_nick_name);
		et_nick_name.addTextChangedListener(mTextWatcher);
		et_tel = (EditText) findViewById(R.id.et_tel);
		et_feedback = (EditText) findViewById(R.id.et_feedback);
		// 子控件———修改性别
		findViewById(R.id.ll_male).setOnClickListener(this);
		findViewById(R.id.ll_female).setOnClickListener(this);
		iv_male = (ImageView) findViewById(R.id.iv_male);
		iv_femal = (ImageView) findViewById(R.id.iv_femal);
		iv_male.setOnClickListener(this);
		iv_femal.setOnClickListener(this);
		// 子控件———移动应用推荐
		lv_apps_recommend = (ListView) findViewById(R.id.lv_apps_recommend);
		wv_apps_page = (WebView) findViewById(R.id.wv_downlaod_page);
		no_network = (LinearLayout) findViewById(R.id.no_network);
		no_network.setVisibility(View.GONE);
	}
	private void getInts() {
		mIntent = this.getIntent();
		mEditType = mIntent.getIntExtra(Constant.PRIVATE_INFORM_EDIT, 0);
		switch (mEditType) {
			case Constant.NICK_NAME_EIDT :
				ll_nick_name.setVisibility(View.VISIBLE);
				tv_titlebar_name.setText("编辑昵称");
				break;
			case Constant.GENDAR_EIDT :
				ll_gendar.setVisibility(View.VISIBLE);
				tv_titlebar_name.setText("修改性别");
				break;
			case Constant.IDEA_BACK :
				ll_idea_back.setVisibility(View.VISIBLE);
				tv_titlebar_name.setText("意见反馈");
				if(getIntent().getStringExtra("push")!=null){
					if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
						AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.LOOKSYSNOTICE.ordinal());
					}
				}
				
				if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
					AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.SUBIDEAR.ordinal());
				}
				
				break;
			case Constant.APP_RECOMMENED :
				lv_apps_recommend.setVisibility(View.VISIBLE);
				tv_titlebar_name.setText("移动应用推荐");
				iv_titlebar_right.setVisibility(View.INVISIBLE);
				setAdapter();
				break;
			case Constant.PRIVACY_ITEM :
				tv_titlebar_name.setText("使用条款及隐私政策");
				iv_titlebar_right.setVisibility(View.INVISIBLE);
				wv_apps_page.setVisibility(View.VISIBLE);
				wv_apps_page.loadUrl("file:///android_asset/"+getResources().getString(R.string.mianzefilename));
			default :
				break;
		}
	}
	private void setAdapter() {
		final List<AppsRecommend> apps = new ArrayList<AppsRecommend>();
		apps.add(new AppsRecommend(
				"彩印",
				R.drawable.apps_caiyin,
				"个性的通话签名",
				"http://a.10086.cn/pams2/m/s.do?gId=100002333204927100001077696300001754912&c=172&j=l&lnpsk=彩印&lnpsid=b12a3e36427140de969d2482c5a7026f&lnpspn=1&lnpsr=1&lnpst=0&lnpsgt=all&blockId=17314&p=72&cuid=33118853"));
		apps.add(new AppsRecommend(
				"中国天气通",
				R.drawable.apps_tianqitong,
				"气象局官方服务工具",
				"http://a.10086.cn/pams2/m/s.do?gId=100011701271061100001831909300002624197&c=172&j=l&lnpsk=%E4%B8%AD%E5%9B%BD%E5%A4%A9%E6%B0%94%E9%80%9A&lnpsid=678d8a9853cf4151b7a3ac92aca0c125&lnpspn=1&lnpsr=1&lnpst=0&lnpsgt=all&blockId=17314&p=72&cuid=78840985"));
		apps.add(new AppsRecommend("飞信", R.drawable.apps_feixin, "快乐生活，指尖传递",
				"http://a.10086.cn/ad.do?adCode=&cntid=300000004294"));
		apps.add(new AppsRecommend("Moblie Market", R.drawable.apps_mm,
				"国内第一家手机应用商城",
				"http://a.10086.cn/ad.do?adCode=&cntid=300000863435"));
		apps.add(new AppsRecommend("手机阅读", R.drawable.apps_shoujiyuedu,
				"享受随身阅读的乐趣",
				"http://a.10086.cn/ad.do?adCode=&cntid=300000013959"));
		apps.add(new AppsRecommend("咪咕音乐", R.drawable.apps_migu, "让生活更动听",
				"http://a.10086.cn/ad.do?adCode=&cntid=300000004296"));
		apps.add(new AppsRecommend("灵犀", R.drawable.apps_linxi, "听话的语音小秘书",
				"http://a.10086.cn/ad.do?adCode=&cntid=300002734449"));
		// apps.add(new AppsRecommend("彩云通讯录", R.drawable.apps_caiyun,
		// "绿色安全的通讯管理软件",
		// "http://a.10086.cn/ad.do?adCode=&cntid=300002575008.html"));
		AppsRecommendAdapter adapter = new AppsRecommendAdapter(this);
		adapter.setData(apps);
		lv_apps_recommend.setAdapter(adapter);
		lv_apps_recommend.setDivider(getResources().getDrawable(
				R.drawable.gd_gray_divider));
		lv_apps_recommend.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// lv_apps_recommend.setVisibility(View.GONE);
				wv_apps_page.setVisibility(View.VISIBLE);
				String url = apps.get(position).getDownloadUrl();
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				// AppsRecommend app = apps.get(position);
				// progressBar = ProgressDialog.show(
				// GDPrivateZooInformCommonActivity.this, null,
				// "正在进入网页，请稍后…");
				// tv_titlebar_name.setText(app.getName());
				// wv_apps_page.loadUrl(app.getDownloadUrl());
				// wv_apps_page.setWebViewClient(new MyWebViewClient());
			}
		});
	}
	private void processLogic() {
		user = new UserDetailVO();
		mCachedUser = (UserBean) mIntent
				.getSerializableExtra(Constant.CACHEDUSER);
		if (mCachedUser == null) {
			return;
		}
		String nickName = mCachedUser.getNickName();
		String gendar = mCachedUser.getGendar();
		if (nickName != null) {
			et_nick_name.setText(nickName);
			et_nick_name.setSelection(nickName.length());
		}
		if (gendar != null) {
			if (gendar.equals("男")) {
				iv_male.setVisibility(View.VISIBLE);
				iv_femal.setVisibility(View.GONE);
			} else {
				iv_male.setVisibility(View.GONE);
				iv_femal.setVisibility(View.VISIBLE);
			}
		}
	}
	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			temp = s;
		}

		@Override
		public void afterTextChanged(Editable s) {
			int num = 0;
			if (s.toString().matches("[\\u4E00-\\u9FA5]+")) {
				num = 6;
			} else {
				num = 12;
			}
			editStart = et_nick_name.getSelectionStart();
			editEnd = et_nick_name.getSelectionEnd();
			if (temp.length() > num) {
				s.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				et_nick_name.setText(s);
				et_nick_name.setSelection(tempSelection);// 设置光标在最后
				// Toast.makeText(getApplicationContext(), "昵称不能超过12个字符",
				// 1).show();
			}
		}

	};
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;
			case R.id.gd_iv_titlebar_right :
				switch (mEditType) {
					case Constant.IDEA_BACK :
						if (CheckContent()) {
							Toast.makeText(getApplicationContext(),
									"您输入的意见内容为空，请重新输入", 1).show();
							return;
						} else {
							sendIdea();
						}
						break;
					case Constant.GENDAR_EIDT :
						user.setBirthday(mCachedUser.getBirthday());
						updateUser(user);
						break;
					case Constant.NICK_NAME_EIDT :
						String nick_name = et_nick_name.getText().toString();
						if (nick_name.length() == 0) {
							Toast.makeText(getApplicationContext(),
									"昵称为空，未能保存", 1).show();
							return;
						}
						user.setNickName(et_nick_name.getText().toString());
						updateUser(user);
						break;
				}
				break;
			case R.id.ll_male :
				iv_male.setVisibility(View.VISIBLE);
				iv_femal.setVisibility(View.GONE);
				user.setMale(true);
				Log.i("R.id.ll_male", user.toString());
				break;
			case R.id.ll_female :
				iv_male.setVisibility(View.GONE);
				iv_femal.setVisibility(View.VISIBLE);
				user.setMale(false);
				break;
			case R.id.iv_clear :
				et_nick_name.setText(null);
				break;
		}
	}
	private boolean CheckContent() {
		return et_feedback.getText().toString().length() == 0 ? true : false;
	}
	private void updateUser(final UserDetailVO user) {
		getService(IUserActivationService.class).syncUserDetail(user,
				new CMProgressMonitor(GDPrivateZooInformCommonActivity.this) {

					@Override
					protected void handleFailed(Throwable cause) {
						Toast.makeText(getApplicationContext(), "更新失败", 1)
								.show();
					}
					@Override
					protected void handleDone(Object returnVal) {
						Toast.makeText(getApplicationContext(), "更新成功", 1)
								.show();
						Intent data = new Intent();
						switch (mEditType) {
							case Constant.NICK_NAME_EIDT :
								data.putExtra("nickname", user.getNickName());
								setResult(Constant.NICKNAME, data);
								break;
							case Constant.GENDAR_EIDT :
								data.putExtra("gendar", user.isMale());
								setResult(Constant.GENDER, data);
								break;
						}
						finish();
					}
				});
	}
	private void sendIdea() {
		TelephonyManager tManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String content = et_feedback.getText().toString();
		String phone = et_tel.getText().toString();
		String deviceId = tManager.getDeviceId();
		Trace.getTrace(GDPrivateZooInformCommonActivity.class).info(
				phone + content + "设备号：" + deviceId);
		getService(IFeedBackService.class).addFeedBack(phone + content,
				deviceId, 1, new GDCMProgressMonitor(this) {

					@Override
					protected void handleFailed(Throwable cause) {
						Toast.makeText(getApplicationContext(), "提交失败，请稍后再试..",
								1).show();
					}

					@Override
					protected void handleDone(Object returnVal) {
						Toast.makeText(getApplicationContext(), "成功提交，感谢您的意见",
								1).show();
						finish();
					}
				});
	}
	private class MyWebViewClient extends WebViewClient {
		// @Override
		// public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// // view.loadUrl(url);
		// return super.shouldOverrideUrlLoading(view, url);
		// }

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			wv_apps_page.setVisibility(View.GONE);
			no_network.setVisibility(View.VISIBLE);
		}
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (progressBar.isShowing()) {
				progressBar.dismiss();
			}
		}
	}
}
