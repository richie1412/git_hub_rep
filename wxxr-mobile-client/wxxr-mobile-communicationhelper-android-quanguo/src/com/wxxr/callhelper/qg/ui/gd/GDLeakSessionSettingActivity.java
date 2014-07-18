package com.wxxr.callhelper.qg.ui.gd;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.db.dao.RemindSettingDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;

public class GDLeakSessionSettingActivity extends BaseActivity {

	private TextView gd_tv_switch_name, gd_tv_detail, tv_titlebar_name;
	private RemindSettingDao rsdao;
	private ImageView gd_iv_leak_callrecord_switch;
	private int gd_lh_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_callrecord_leak_setting);

		findView();
		processLogic();
	}

	private void findView() {
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);

		gd_tv_switch_name = (TextView) findViewById(R.id.gd_tv_switch_name);
		gd_tv_detail = (TextView) findViewById(R.id.gd_tv_detail);
		gd_iv_leak_callrecord_switch = (ImageView) findViewById(R.id.gd_iv_leak_callrecord_switch);

		rsdao = RemindSettingDao.getInstance(this);
	}

	private void processLogic() {
		tv_titlebar_name.setText("设置");
		gd_tv_switch_name.setText("开启漏接电话提醒");
		gd_tv_detail.setText(R.string.gd_leak_detail);
		gd_iv_leak_callrecord_switch.setOnClickListener(this);

		gd_lh_status = rsdao.queryLouHuaSetting();

		if (gd_lh_status == 2) {
			// 开启
			gd_iv_leak_callrecord_switch
					.setBackgroundResource(R.drawable.gd_setting_switch_open);
		} else {
			// 关闭
			gd_iv_leak_callrecord_switch
					.setBackgroundResource(R.drawable.gd_setting_switch_close);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		case R.id.gd_iv_leak_callrecord_switch:
			gd_lh_status = rsdao.queryLouHuaSetting();
			if (gd_lh_status == 2) {
				gd_iv_leak_callrecord_switch
						.setBackgroundResource(R.drawable.gd_setting_switch_close);
				rsdao.updateLouHuaStatus(1);
			} else {
				gd_iv_leak_callrecord_switch
						.setBackgroundResource(R.drawable.gd_setting_switch_open);
				rsdao.updateLouHuaStatus(2);
			}
			break;
		}
	}
}
