package com.wxxr.callhelper.qg.ui.gd;

import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.BodyBeanCongzhi;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.intercept.rule.CongzhiSmsHandler;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.utils.WeiboManager;
import com.wxxr.callhelper.qg.wxapi.WXEntryActivity;
import com.wxxr.mobile.android.app.AppUtils;
//广东版——对话框
public class GDShareDialog extends BaseActivity {

	private LinearLayout ll_titile;
	private TextView tv_sms_content;
	private LinearLayout ll_sms_description;
	private View divide_bottom;
	private TextView tv_titile;
	private LinearLayout ll_share;
	private LinearLayout ll_sms;
	private LinearLayout ll_button_bottom;
	private TextView tv_cancel;
	private Intent mIntent;
	private int mKey;
	private TextView tv_commpond;
	private TextView tv_sub_description;
	private TextView mTv_left;
	private TextView mTv_right;
	private TextView tv_content;
	private View divide_top;
	private int action_id;
	private ImageView iv_icon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gd_share_dialog);
		findView();
		processLogic();
	}

	private void findView() {
		findViewById(R.id.iv_title_close).setOnClickListener(this);
		tv_titile = (TextView) findViewById(R.id.tv_title);
		divide_bottom = (View) findViewById(R.id.divide_bottom);
		divide_top = (View) findViewById(R.id.divide_top);
		mTv_left = (TextView) findViewById(R.id.tv_left);
		mTv_right = (TextView) findViewById(R.id.tv_right);
		mTv_left.setOnClickListener(this);
		mTv_right.setOnClickListener(this);
		// 分享操作控件
		findViewById(R.id.share_sms).setOnClickListener(this);
		findViewById(R.id.share_weixin).setOnClickListener(this);
		findViewById(R.id.share_weibo).setOnClickListener(this);
		findViewById(R.id.tv_cancel).setOnClickListener(this);
		// 充值短信
		tv_commpond = (TextView) findViewById(R.id.tv_title_name);
		iv_icon =(ImageView) findViewById(R.id.iv_icon);
		ll_titile = (LinearLayout) findViewById(R.id.ll_title);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		tv_content = (TextView) findViewById(R.id.tv_content);
		ll_sms = (LinearLayout) findViewById(R.id.ll_sms);
		ll_button_bottom = (LinearLayout) findViewById(R.id.ll_button_bottom);
		tv_sms_content = (TextView) findViewById(R.id.tv_sms_content);
		tv_sub_description = (TextView) findViewById(R.id.tv_sub_description);
		ll_sms_description = (LinearLayout) findViewById(R.id.ll_sms_sub_description);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
	}

	private void processLogic() {
		mIntent = getIntent();
		mKey = mIntent.getIntExtra(Constant.DIALOG_KEY, 0);
		action_id = mIntent.getIntExtra(Constant.SHARE_ID, -1);
		switch (mKey) {
			case Constant.CONFIRMED_CONGZHI :
				iv_icon.setVisibility(View.VISIBLE);
				iv_icon.setBackgroundResource(R.drawable.gd_tel_icon_deep_ring);
				tv_commpond.setText("电话充值");
				tv_commpond.setVisibility(View.VISIBLE);
				mTv_left.setText("取消");
				mTv_right.setText("确定");
				divide_top.setBackgroundColor(getResources().getColor(
						R.color.gd_dialog_line));
				divide_bottom.setBackgroundColor(getResources().getColor(
						R.color.gd_dialog_line));
				tv_content.setVisibility(View.VISIBLE);
				tv_content
						.setText("将协助您拨打10086，进入语音主菜单后，需要您根据提示按键2再按键1，才能进行电话充值。");
				tv_sub_description.setVisibility(View.VISIBLE);
				ll_titile.setVisibility(View.VISIBLE);
				break;
			case Constant.DIALOG_CHONGZHI :
				BodyBeanCongzhi body = (BodyBeanCongzhi) mIntent
						.getSerializableExtra(CongzhiSmsHandler.SMS_INFO);
				divide_bottom.setVisibility(View.VISIBLE);
				ll_titile.setVisibility(View.VISIBLE);
				if (body != null) {
					tv_sms_content.setText(body.getContent());
				}
				iv_icon.setVisibility(View.VISIBLE);
				tv_commpond.setText("电话充值提醒");
				tv_commpond.setVisibility(View.VISIBLE);
				ll_sms.setVisibility(View.VISIBLE);
				ll_sms_description.setVisibility(View.VISIBLE);
				mTv_left.setText("电话充值");
				mTv_right.setText("余额查询");

				if (getService(IUserUsageDataRecorder.class) != null) {
					getService(IUserUsageDataRecorder.class).doRecord(
							ActivityID.CHONGZHIDIALOG.ordinal());
				}
				break;
			case Constant.DIALOG_SHARE :
				divide_bottom.setVisibility(View.VISIBLE);
				tv_titile.setVisibility(View.VISIBLE);
				ll_share.setVisibility(View.VISIBLE);
				tv_cancel.setVisibility(View.VISIBLE);
				ll_button_bottom.setVisibility(View.GONE);
				break;
		}

	}
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
			case R.id.iv_title_close :

				if ((mKey == Constant.DIALOG_CHONGZHI)
						&& getService(IUserUsageDataRecorder.class) != null) {
					getService(IUserUsageDataRecorder.class).doRecord(
							ActivityID.CLOSE_CHONGZHI.ordinal());
				}

				this.finish();
				break;
			case R.id.tv_left :
				switch (mKey) {
					case Constant.CONFIRMED_CONGZHI :
						this.finish();
						break;
					case Constant.DIALOG_CHONGZHI :
						Tools.call(this, "10086");
						Toast.makeText(this, "语音充值，进入主菜单后按2再按1", 1).show();
						if (getService(IUserUsageDataRecorder.class) != null) {
							getService(IUserUsageDataRecorder.class).doRecord(
									ActivityID.DIANHUA_CHONGZHI.ordinal());
						}
						break;
				}
				break;
			case R.id.tv_right :
				switch (mKey) {
					case Constant.CONFIRMED_CONGZHI :
						Tools.call(this, "10086");
						Toast.makeText(this, "语音充值，进入主菜单后按2再按1", 1).show();
						this.finish();
						break;
					case Constant.DIALOG_CHONGZHI :
						Tools.sendMsg(this, "101", "10086", 1);
						Toast.makeText(this, "请到手机收件箱查收10086余额短信", 1).show();
						if (getService(IUserUsageDataRecorder.class) != null) {
							getService(IUserUsageDataRecorder.class).doRecord(
									ActivityID.YUE_CHAXUN.ordinal());
						}
						this.finish();
						break;
				}

				break;
			case R.id.tv_cancel :
				this.finish();
				break;
			case R.id.share_sms :
				if (action_id != -1) {
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(
							action_id);
				}
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"smsto", "", null));
				String content=getResources().getString(R.string.share_sms);
				intent.putExtra("sms_body", content);
				startActivity(intent);
				finish();
				break;
			case R.id.share_weixin :
				if (action_id != -1) {
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(
							action_id);
				}
				String t =getResources().getString(R.string.share_weixi_title);
				String shareUrl = getResources().getString(R.string.share_weixi_pageUrl);
				String abstrct = getResources().getString(R.string.share_weixi_description);
				Intent in = new Intent();
				in.setClass(GDShareDialog.this, WXEntryActivity.class);
				in.putExtra("title", t);
				in.putExtra("description", abstrct);
				in.putExtra("pageUrl", shareUrl);
				startActivity(in);
				finish();
				break;
			case R.id.share_weibo :
				if (action_id != -1) {
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(
							action_id);
				}
				String title=getResources().getString(R.string.share_weibo);
				WeiboManager wm = new WeiboManager(GDShareDialog.this);
				if (wm.isBind()) {
					try {
						wm.share2weibo(title, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					wm.bindAndShare2weibo(title, null);
				}
				// finish();
				break;
		}
	}
}
