package com.wxxr.callhelper.qg.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;
/**
 * 
 * 漏接短信 终端页长按
 *
 */
public class ComingRemindDialogActivity extends BaseActivity {

	private int position;
	private LouHuaDao ldao;
	private String phone, contactsName, smsContent;
	private TextView tv_reply, tv_call;

//	private Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 0:
//				ldao.deleteSMSLouHua(position);
//				finish();
//				break;
//			}
//		}
//	};
	private Intent intent;
private String deltip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coming_remind_dialog);

		findView();

		processLogic();

	}

	private void findView() {
		// 来电提醒操作方式
		findViewById(R.id.tv_delete).setOnClickListener(this);
		findViewById(R.id.tv_transmit).setOnClickListener(this);
		findViewById(R.id.tv_copy).setOnClickListener(this);
		
		tv_reply = (TextView) findViewById(R.id.tv_reply);
		tv_reply.setOnClickListener(this);
		tv_call = (TextView) findViewById(R.id.tv_call);
		tv_call.setOnClickListener(this);
	}

	private void processLogic() {
		Intent intent = getIntent();
		ldao = LouHuaDao.getInstance(this);
		position = intent.getIntExtra("singleid", 0);
		phone = intent.getStringExtra("number");
		contactsName = Tools.getContactsName(this, phone);
		smsContent = intent.getStringExtra("content");
		deltip = intent.getStringExtra(Constant.DIALOG_CONTENT);
		
		if (TextUtils.isEmpty(contactsName)) {
			tv_reply.setText("回复" + phone);
			tv_call.setText("呼叫" + phone);
		} else {
			tv_reply.setText("回复" + contactsName);
			tv_call.setText("呼叫" + contactsName);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.tv_delete:
			// 删除
			intent = new Intent(this, ConfirmDialogActivity.class);
			intent.putExtra(Constant.DIALOG_KEY, Constant.DETAIL_DELETE_RECORD);
			intent.putExtra(Constant.DIALOG_CONTENT, deltip);
			startActivityForResult(intent, 100);
			
			break;

		case R.id.tv_transmit:
			// 转发
//			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//					"smsto", "", null));
			intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
			intent.putExtra("sms_body", smsContent);
			startActivity(intent);
			finish();
			break;

		case R.id.tv_reply:
			// 回复
			finish();
			break;
		case R.id.tv_copy:
//			setResult(Constant.LONG_COPY_RECORD);
			Tools.copyToClipboard(this, smsContent);
			finish();
			break;
		case R.id.tv_call:
			// 呼叫
			intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + phone));
			startActivity(intent);
			finish();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Constant.DETAIL_DELETE_RECORD:
			setResult(Constant.LONG_DELETE_RECORD);
			break;
		}
		finish();
	}
}
