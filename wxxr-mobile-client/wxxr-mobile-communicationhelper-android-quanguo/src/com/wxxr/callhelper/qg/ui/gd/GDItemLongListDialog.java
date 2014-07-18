package com.wxxr.callhelper.qg.ui.gd;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.SiMiPersonContentActivity;
import com.wxxr.callhelper.qg.ui.SmsContentActivity;
import com.wxxr.callhelper.qg.ui.SmsContentHuiZhiActivity2Version;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * 通话录音，漏话，回执，私密长按弹出框
 * 
 * @author yinzhen
 * 
 */

public class GDItemLongListDialog extends BaseActivity {

	private Intent intent;
	private String telphone, dialogContent, smsMonth, smsport;
	private long smsTime;
	private TextView gd_tv_item_long_dialog_contacts, gd_tv_item_long_dialog_delete, gd_tv_item_long_dialog_send;
	private int dialogKey;
	private String contacts;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.gd_item_long_list);

		findView();

		processLogic();
	}

	private void findView() {
		findViewById(R.id.gd_ll_item_long_dialog_call).setOnClickListener(this);
		findViewById(R.id.gd_ll_item_long_dialog_send).setOnClickListener(this);
		findViewById(R.id.gd_ll_item_long_dialog_contacts).setOnClickListener(this);
		findViewById(R.id.gd_ll_item_long_dialog_delete).setOnClickListener(this);

		gd_tv_item_long_dialog_contacts = (TextView) findViewById(R.id.gd_tv_item_long_dialog_contacts);
		gd_tv_item_long_dialog_delete = (TextView) findViewById(R.id.gd_tv_item_long_dialog_delete);
		gd_tv_item_long_dialog_send = (TextView) findViewById(R.id.gd_tv_item_long_dialog_send);
	}

	private void processLogic() {
		intent = getIntent();
		dialogContent = intent.getStringExtra(Constant.DIALOG_CONTENT);
		dialogKey = intent.getIntExtra(Constant.DIALOG_KEY, 0);

		switch (dialogKey) {
		case Constant.LH_DETAIL_LONG_DELETE_RECORD:
			// 漏话
			smsMonth = intent.getStringExtra("smsmonth");
			telphone = intent.getStringExtra(Constant.PHONE_NUMBER);
			smsTime = intent.getLongExtra("smstime", 0L);
			break;
		case Constant.HZ_DETAIL_LONG_DELETE_RECORD:
			// 回执
			smsMonth = intent.getStringExtra("smsmonth");
			telphone = intent.getStringExtra(Constant.PHONE_NUMBER);
			smsTime = intent.getLongExtra("smstime", 0L);
			smsport = intent.getStringExtra("smsport");
			break;
		case Constant.DEL_MANY_CHAT_ITME:
			//私密信息
			telphone = intent.getStringExtra(Constant.PHONE_NUMBER);
			gd_tv_item_long_dialog_send.setText("查看/发送私密信息");
			break;
		case Constant.LONG_SM_LOCK_OPEN:
			//私密联系人
			gd_tv_item_long_dialog_delete.setText("解锁");
			telphone = intent.getStringExtra(Constant.PHONE_NUMBER);
			gd_tv_item_long_dialog_send.setText("查看/发送私密信息");
			break;
			
		default:
			// 通话录音
			telphone = intent.getStringExtra("phoneNumber");
			smsMonth=intent.getStringExtra("smsmonth");
			break;
		}

		//判断是“查看”还是“添加”
		contacts = Tools.getContactsName(this, telphone);
		if (TextUtils.isEmpty(contacts)) {
			gd_tv_item_long_dialog_contacts.setText("添加联系人");
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_ll_item_long_dialog_call:
			Tools.call(this, telphone);
			finish();
			break;

		case R.id.gd_ll_item_long_dialog_send:
			switch (dialogKey) {
			case Constant.LH_DETAIL_LONG_DELETE_RECORD:
				// 漏话
				intent = new Intent(this, SmsContentActivity.class);
				intent.putExtra(Constant.PHONE_NUMBER, telphone);
				intent.putExtra("smsmonth",smsMonth);
				startActivity(intent);
				break;
			case Constant.HZ_DETAIL_LONG_DELETE_RECORD:
				// 回执
				intent = new Intent(this,SmsContentHuiZhiActivity2Version.class);
				intent.putExtra(Constant.PHONE_NUMBER, telphone);
				intent.putExtra("smsmonth",smsMonth);
				intent.putExtra("smsport",smsport);
				startActivity(intent);
				break;
			case Constant.DEL_MANY_CHAT_ITME:
				//私密信息
				intent = new Intent(this, SiMiPersonContentActivity.class);
				intent.putExtra(Constant.PHONE_NUMBER, telphone);
				startActivity(intent);
				break;
			case Constant.LONG_SM_LOCK_OPEN:
				//私密联系人
				intent = new Intent(this, SiMiPersonContentActivity.class);
				intent.putExtra(Constant.PHONE_NUMBER, telphone);
				startActivity(intent);
				break;
			default:
				// 录音
//				intent = new Intent(this, SmsContentActivity.class);
//				intent.putExtra(Constant.PHONE_NUMBER, telphone);
//				intent.putExtra("smsmonth",smsMonth);
//				startActivity(intent);
				intent = new Intent(Intent.ACTION_VIEW); 
				intent.putExtra("address", telphone); 
				intent.putExtra("sms_body", ""); 
				intent.setType("vnd.android-dir/mms-sms"); 
				startActivity(intent); 
				break;

			}
			finish();
			break;

		case R.id.gd_ll_item_long_dialog_contacts:
			if (TextUtils.isEmpty(contacts)) {
				// 添加联系人
				intent = new Intent(Intent.ACTION_INSERT);
				// intent.setType("vnd.android.cursor.dir/person");
				// intent.setType("vnd.android.cursor.dir/contact");
				intent.setType("vnd.android.cursor.dir/raw_contact");
				intent.putExtra(
						android.provider.ContactsContract.Intents.Insert.PHONE,
						telphone);
				startActivity(intent);
			} else {
				// 查看联系人
				intent = new Intent(Intent.ACTION_VIEW,
						Uri.parse("content://com.android.contacts/contacts/"
								+ Tools.getContactsId(this, telphone)));
				startActivity(intent);
			}
			finish();
			break;

		case R.id.gd_ll_item_long_dialog_delete:
			intent = new Intent(this, ConfirmDialogActivity.class);
			intent.putExtra(Constant.DIALOG_KEY, dialogKey);
			intent.putExtra(Constant.DIALOG_CONTENT, dialogContent);

			switch (dialogKey) {
			case Constant.LH_DETAIL_LONG_DELETE_RECORD:
				// 漏话
				intent.putExtra("smsmonth", smsMonth);
				intent.putExtra(Constant.PHONE_NUMBER, telphone);
				intent.putExtra("smstime", smsTime);
				startActivity(intent);
				finish();
				break;
			case Constant.HZ_DETAIL_LONG_DELETE_RECORD:
				// 回执
				intent.putExtra("smsmonth", smsMonth);
				intent.putExtra(Constant.PHONE_NUMBER, telphone);
				intent.putExtra(Constant.SMS_PORT,smsport);
				intent.putExtra("smstime", smsTime);
				startActivity(intent);
				finish();
				break;
			case Constant.DEL_MANY_CHAT_ITME:
				//私密信息删除
				startActivityForResult(intent, 101);
				break;
			case Constant.LONG_SM_LOCK_OPEN:
				//私密联系人
				startActivityForResult(intent, 102);
				break;
			default:
				// 录音
				startActivityForResult(intent, 100);
				break;

			}
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case Constant.LONG_DELETE_RECORD:
			setResult(Constant.LONG_DELETE_RECORD);
			break;
		case Constant.DEL_MANY_CHAT_ITME:
			setResult(Constant.DEL_MANY_CHAT_ITME);
			break;
		case Constant.LONG_SM_LOCK_OPEN:
			setResult(Constant.SM_LOCK_OPEN, data);
			break;
		}
		finish();
	}
}
