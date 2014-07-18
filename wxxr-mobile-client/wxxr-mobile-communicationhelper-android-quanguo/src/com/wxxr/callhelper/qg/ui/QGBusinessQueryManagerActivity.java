package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;		
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.QGBusinessQueryManagerAdapter;
import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.receiver.SendMsgBroadcastReceiver;
import com.wxxr.callhelper.qg.service.IMoblieBusiness;
import com.wxxr.callhelper.qg.ui.gd.GDCMProgressMonitor;

/**
 * 业务查询&办理
 * 
 * @author yinzhen
 * 
 */

public class QGBusinessQueryManagerActivity extends BaseActivity {

	private List<MoblieBusinessBean> businessQueryLists = new ArrayList<MoblieBusinessBean>();
	private ListView lv_business;
	private TextView tv_titlebar_name, qg_tv_first_detail, qg_tv_second_detail;
	private QGBusinessQueryManagerAdapter adapter;
	private String type;
	private SendMsgBroadcastReceiver sendMsgReceiver = new SendMsgBroadcastReceiver();
	private IntentFilter filter = new IntentFilter();
	private View nonetdiv;
	private View business_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.qg_business_query_manager);

		findView();

		processLogic();
	}

	private void findView() {
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this, 0);
		Intent intent = getIntent();
		type = intent.getStringExtra(Constant.QG_BUSINESS_TYPE);
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		lv_business = (ListView) findViewById(R.id.lv_business);
		qg_tv_first_detail = (TextView) findViewById(R.id.qg_tv_first_detail);
		qg_tv_second_detail = (TextView) findViewById(R.id.qg_tv_second_detail);
		nonetdiv=findViewById(R.id.no_network);
		business_content=findViewById(R.id.bussiness_content);
	}

	private void processLogic() {
		
		tv_titlebar_name.setText("移动业务" + type);
		
		qg_tv_first_detail.setText("点击" + type + "后，通信助手代您发送" + type + "短信到");
		qg_tv_second_detail.setText("中国移动，请稍后到手机收件箱查结果。");
		
		if (null == adapter)
			adapter = new QGBusinessQueryManagerAdapter(this);
		lv_business.setAdapter(adapter);
		
		getData();
		filter.addAction(Sms.SENT_SMS_ACTION);
		registerReceiver(sendMsgReceiver, filter);
		
	}

	private void getData() {
		GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {
				nonetdiv.setVisibility(View.VISIBLE);
				business_content.setVisibility(View.GONE);
			}

			@Override
			protected void handleDone(Object returnVal) {
				businessQueryLists = (List<MoblieBusinessBean>) returnVal;
				if (null != businessQueryLists && !businessQueryLists.isEmpty()) {
					adapter.setData(businessQueryLists, type);
					adapter.updateData();
				}else{
					nonetdiv.setVisibility(View.VISIBLE);
					business_content.setVisibility(View.GONE);
				}
			}

//			@Override
//			protected Map<String, Object> getDialogParams() {
//				
//				return super.getDialogParams();
//			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				
//				List<MoblieBusinessBean> lists = new ArrayList<MoblieBusinessBean>();
				if (type.equals("查询")) {
					return getService(IMoblieBusiness.class)
							.getBusinessCheckFromLocal(
									QGBusinessQueryManagerActivity.this);
				} else if (type.equals("办理")) {
					return getService(IMoblieBusiness.class)
							.getBusinessProcessFromLocal(
									QGBusinessQueryManagerActivity.this);
				}
				return null;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		adapter.clearLists();
		businessQueryLists = null;
		unregisterReceiver(sendMsgReceiver);
		super.onDestroy();
	}

}
