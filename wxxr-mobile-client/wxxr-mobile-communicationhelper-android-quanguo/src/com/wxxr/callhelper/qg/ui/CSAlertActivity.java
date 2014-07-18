package com.wxxr.callhelper.qg.ui;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.wxxr.callhelper.qg.ICSAlarmManager;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CSAlarmManager;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.db.dao.IComeHelperDao;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
import com.wxxr.callhelper.qg.utils.ContactsUtil;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
/**
 * 提醒事项到期的弹出框
 * 
 * @author cuizaixi
 * 
 */
public class CSAlertActivity extends BaseActityExtend {
	private TextView tv_alert_content;
	private Intent mIntent;
	private IComeHelperDao<ComSecretaryBean> mComeHelperDao;
	private ICSAlarmManager mAlarmManager;
	private ComSecretaryBean mBean;
	private Button btn_right;
	private Button btn_left;
	private long mType;
	private int mDelayTimes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cs_alert);
		findView();
		processLogic();
	}
	@Override
	public void findView() {
		((TextView) findViewById(R.id.tv_dialog_title_name))
				.setText("通信小秘书提醒您");
		findViewById(R.id.iv_dialog_close).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mBean.setState(1);
						mComeHelperDao.update(mBean);
						finish();
					}
				});
		tv_alert_content = (TextView) findViewById(R.id.tv_alert_content);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);
	}
	@Override
	public void processLogic() {
		Tools.sendSystemTone(this);
		mComeHelperDao = ComSecretaryDao.getInstance(getApplicationContext());
		mAlarmManager = CSAlarmManager.getInstance(this);
		mIntent = getIntent();
		if (mIntent == null) {
			return;
		}
		mBean = (ComSecretaryBean) mIntent
				.getSerializableExtra(CSManagerActvity.CS_BEAN);
		if (mBean == null) {
			return;
		}
		buildContent(mBean);
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果前两次出现，则推迟5分钟
				if (mBean.getDelayTime() < 2) {
					mBean.setAlertTime(mBean.getAlertTime() + 5 * 1000 * 60);
					mBean.setDelayTime(mBean.getDelayTime() + 1);
					mAlarmManager.update(mBean);

				} else {
					// 否则进入到修改事项的界面
					Intent intent = new Intent(CSAlertActivity.this,
							CSManagerActvity.class);
					intent.putExtra(CSManagerActvity.CS_BEAN, mBean);
					startActivity(intent);
				}
				finish();
			}
		});
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mType == 0) {
					ManagerSP.getInstance().update(Constant.CS_NEED_SHOW, 1);
					ContactsUtil.call(CSAlertActivity.this, mBean.getTelnum());
				} else if (mType == 1) {
					ContactsUtil.goSendMsg(CSAlertActivity.this,
							mBean.getTelnum());
				}
				// 点击说明已经提醒过了，需要更改状态
				mBean.setState(1);
				mComeHelperDao.update(mBean);
				finish();
			}
		});
	}
	private void buildContent(ComSecretaryBean bean) {
		StringBuilder builder = new StringBuilder();
		builder.append("您该");
		mType = mBean.getSecreType();
		if (mType == 0) {
			builder.append("回拨电话");
			btn_right.setText("回电话");
		} else if (mType == 1) {
			builder.append("发送短信");
			btn_right.setText("回短信");
		}
		mDelayTimes = mBean.getDelayTime();
		if (mDelayTimes == 2) {
			btn_left.setText("修改提醒");
		} else {
			btn_left.setText("5分钟后再提醒");
		}
		builder.append("给" + mBean.getTelnum());
		String contactName = Tools.getContactsName(this, mBean.getTelnum());
		if (contactName != null) {
			builder.append("(" + contactName + ")");
		}
		builder.append("啦!");
		tv_alert_content.setText(builder.toString());
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mBean.setState(1);
			mComeHelperDao.update(mBean);
		}
		return super.onKeyDown(keyCode, event);
	}
}
