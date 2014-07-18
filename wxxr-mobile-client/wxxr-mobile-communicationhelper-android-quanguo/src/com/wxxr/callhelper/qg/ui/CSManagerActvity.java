package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wxxr.callhelper.qg.ICSAlarmManager;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CSAlarmManager;
import com.wxxr.callhelper.qg.adapter.CSAlertAdapter;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.bean.ContactsInfoBean;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
import com.wxxr.callhelper.qg.utils.ContactsUtil;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.PopMenu;
/**
 * 小秘书添加和修改界面
 * 
 * @author cuizaixi
 * 
 */
public class CSManagerActvity extends BaseActityExtend {
	private EditText et_about_to;
	private EditText et_person;
	private EditText et_alert_time;
	private PopMenu popMenu;
	public static final String CS_BEAN = "cs_bean";
	public static final String CONTACTS_NUMBERE = "contacts_numbere";
	private Map<Integer, Integer> mTimeMap;
	private Map<Integer, String> mAlertMap;
	private Map<Integer, String> mPersonMap;
	private Button btn_concel;
	private Button btn_ok;
	private ComSecretaryBean mBean;
	private ICSAlarmManager mAlarmManager;
	private TextView tv_add_or_upate;
	private long mAlertTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cs_dialog);
		findView();
		processLogic();
	}
	public void findView() {
		tv_add_or_upate = (TextView) findViewById(R.id.tv_add_or_update);
		et_about_to = (EditText) findViewById(R.id.tv_about_to);
		et_person = (EditText) findViewById(R.id.tv_person);
		et_alert_time = (EditText) findViewById(R.id.tv_alert_time);
		btn_concel = (Button) findViewById(R.id.btn_left);
		btn_ok = (Button) findViewById(R.id.btn_right);
	}
	public void processLogic() {
		mAlarmManager = CSAlarmManager.getInstance(this);
		mBean = (ComSecretaryBean) getIntent().getSerializableExtra(CS_BEAN);
		if (mBean == null) {
			return;
		}
		mAlertTime = mBean.getAlertTime();
		if (mAlertTime != 0) {
			tv_add_or_upate.setText("修改提醒");
			int state = mBean.getState();
			if (state == 0) {
				et_alert_time.setText(String.valueOf(CSAlertAdapter
						.parseDate(mBean.getAlertTime())));
			} else if (state == 1) {
				et_alert_time.setText(CSAlertAdapter.parseDate(System
						.currentTimeMillis()));
				mBean.setAlertTime(System.currentTimeMillis());
			}
		} else {
			tv_add_or_upate.setText("添加新提醒");
			mBean.setAlertTime(System.currentTimeMillis());
		}
		long type = mBean.getSecreType();
		if (type == 0) {
			et_about_to.setText("回拨电话");
		} else if (type == 1) {
			et_about_to.setText("回复短信");
		}
		if (mBean.getTelnum() != null) {
			String nameByNumber = ContactsUtil.getContactNameByNumber(this,
					mBean.getTelnum());
			if (nameByNumber != null) {
				et_person.setText(nameByNumber);
			} else {
				et_person.setText(mBean.getTelnum());
			}
		}
		mTimeMap = new HashMap<Integer, Integer>();
		mAlertMap = new HashMap<Integer, String>();
		mPersonMap = new HashMap<Integer, String>();
		mTimeMap.put(0, 5 * 1000 * 60);
		mTimeMap.put(1, 15 * 1000 * 60);
		mTimeMap.put(2, 30 * 1000 * 60);
		mTimeMap.put(3, 60 * 1000 * 60);
		mTimeMap.put(4, 120 * 1000 * 60);
		mTimeMap.put(5, 180 * 1000 * 60);
		mTimeMap.put(6, 240 * 1000 * 60);
		mTimeMap.put(7, 300 * 1000 * 60);
		mAlertMap.put(0, "回拨电话");
		mAlertMap.put(1, "回复短信");
		mPersonMap.put(0, "从通讯录添加");
		mPersonMap.put(1, "输入号码添加");
		et_person.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu = new PopMenu(CSManagerActvity.this);
				popMenu.addItems(new String[]{"从通讯录添加", "输入号码添加"});
				popMenu.setOnItemClickListener(personListener);
				popMenu.showAsDropDown(et_person);
			}
		});
		// 菜单项点击监听器
		et_about_to.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu = new PopMenu(CSManagerActvity.this);
				popMenu.addItems(new String[]{"回拨电话", "回复短信"});
				popMenu.setOnItemClickListener(aboutToListener);
				popMenu.showAsDropDown(et_about_to);
			}
		});
		et_alert_time.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				popMenu = new PopMenu(CSManagerActvity.this);
				popMenu.addItems(new String[]{"5分钟后", "15分钟后", "30分钟后", "1小时后",
						"2小时后", "3小时后", "4小时后", "5小时后"});
				popMenu.setOnItemClickListener(timeListener);
				popMenu.showAsDropDown(et_alert_time);
			}
		});
		btn_concel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		btn_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mBean.setAlertTime(System.currentTimeMillis() + selectedTime);
				if (mAlertTime == 0) {
					if (TextUtils.isEmpty(et_about_to.getText().toString())) {
						showToast("请选择待办事项");
						return;
					}

					if (TextUtils.isEmpty(et_person.getText().toString())) {
						showToast("请选择联系人");
						return;
					}
					if (TextUtils.isEmpty(et_alert_time.getText().toString())) {
						showToast("请选择提醒时间");
						return;
					}
					mAlarmManager.insert(mBean);
				} else {
					mBean.setState(0);
					mBean.setDelayTime(0);// 即使之前弹出修改提醒，此处重新置为0
					mAlarmManager.update(mBean);
				}
				finish();
			}
		});
	}
	// 弹出菜单监听器
	OnItemClickListener aboutToListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mBean.setSecreType(position);
			et_about_to.setText(mAlertMap.get(position));
			popMenu.dismiss();
		}
	};
	long selectedTime;
	// 弹出菜单监听器
	OnItemClickListener timeListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectedTime = mTimeMap.get(position);
			long trigleTime = selectedTime + System.currentTimeMillis();
			mBean.setAlertTime(trigleTime);
			et_alert_time.setText(CSAlertAdapter.parseDate(trigleTime));
			popMenu.dismiss();
		}

	};
	// 弹出菜单监听器
	OnItemClickListener personListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				// 进入联系人列表
				Intent intent = new Intent(CSManagerActvity.this,
						ContactsListActivity.class);
				startActivityForResult(intent, 0);
			} else if (position == 1) {
				// 弹出手动选择弹出框
				Intent intent = new Intent(CSManagerActvity.this,
						ContactsHandAddAcitivity.class);
				startActivityForResult(intent, 0);
			}
			popMenu.dismiss();
		}

	};
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String number = data.getStringExtra(CONTACTS_NUMBERE);
			String realNum = Tools.goneSpace(number);
			String name = Tools.getContactsName(this, realNum);
			if (name != null) {
				et_person.setText(name);
			} else {
				et_person.setText(realNum);
			}
			mBean.setTelnum(realNum);
		}
	};
}
