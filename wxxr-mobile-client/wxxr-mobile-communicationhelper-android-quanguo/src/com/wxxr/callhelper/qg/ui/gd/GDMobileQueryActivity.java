package com.wxxr.callhelper.qg.ui.gd;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;
/**
 * 移动查询
 * 
 * @author cuizaixi
 * 
 */
public class GDMobileQueryActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_mobile_query);
		findView();
		processLogic();
		((com.wxxr.callhelper.qg.widget.BottomTabBar)findViewById(R.id.home_bottom_tabbar)).setActivtiy(this, 0);
	}

	private void findView() {
		findViewById(R.id.btn_account_remaining).setOnClickListener(this);
		findViewById(R.id.btn_order_service).setOnClickListener(this);
		findViewById(R.id.btn_gprs_remainning).setOnClickListener(this);
		findViewById(R.id.btn_sms_remainning).setOnClickListener(this);
		TextView tv_mid = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		tv_mid.setText("移动业务查询");
	}
	private void processLogic() {

	}
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;
			case R.id.btn_account_remaining :
				ensureSIMIsOk();
				Tools.sendMsg(this, "101", "10086", 1);
				Toast.makeText(getApplicationContext(), "已代发免费短信，请查收手机短信", 1)
						.show();
				break;
			case R.id.btn_order_service :
				ensureSIMIsOk();
				Tools.sendMsg(this, "0000", "10086", 1);
				Toast.makeText(getApplicationContext(), "已代发免费短信，请查收手机短信", 1)
						.show();
				break;
			case R.id.btn_gprs_remainning :
				ensureSIMIsOk();
				Tools.sendMsg(this, "CXGPRSTC", "10086", 1);
				Toast.makeText(getApplicationContext(), "已代发免费短信，请查收手机短信", 1)
						.show();
				break;
			case R.id.btn_sms_remainning :
				ensureSIMIsOk();
				Tools.call(GDMobileQueryActivity.this, "1008611");
				Toast.makeText(getApplicationContext(), "将代拨免费电话，请查收手机短信", 1)
						.show();
				break;
			default :
				break;
		}

	}
	private void ensureSIMIsOk() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (TelephonyManager.SIM_STATE_ABSENT == telephonyManager.getSimState()
				|| TelephonyManager.SIM_STATE_UNKNOWN == telephonyManager
						.getSimState()) {
			Toast.makeText(getApplicationContext(), "没有sim卡,或者sim卡状态未知", 1)
					.show();
			return;
		}
	}
}
