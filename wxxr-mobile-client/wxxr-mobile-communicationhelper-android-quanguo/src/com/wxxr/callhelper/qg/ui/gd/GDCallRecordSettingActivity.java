package com.wxxr.callhelper.qg.ui.gd;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.ManagerSP;
/**
 * 广东版——通话录音设置
 * 
 * @author cuizaixi
 * 
 */
public class GDCallRecordSettingActivity extends BaseActivity {
	private ManagerSP sp;
	private ImageView gd_iv_leak_callrecord_switch;
	private TextView tv_titlebar_name;
	private int setting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_callrecord_leak_setting);
		findView();
		processLogic();
	}

	private void findView() {
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		gd_iv_leak_callrecord_switch = (ImageView) findViewById(R.id.gd_iv_leak_callrecord_switch);
		gd_iv_leak_callrecord_switch.setOnClickListener(this);
	}

	private void processLogic() {
		tv_titlebar_name.setText("通话录音设置");
		sp = ManagerSP.getInstance(this);
		setting = sp.get(Constant.CALLRECORDER_OPEN_CLOSE, 0);
		if (setting == 0) {
			gd_iv_leak_callrecord_switch
					.setBackgroundResource(R.drawable.gd_setting_switch_open);
		} else if (setting == 1) {
			gd_iv_leak_callrecord_switch
					.setBackgroundResource(R.drawable.gd_setting_switch_close);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;

			case R.id.gd_iv_leak_callrecord_switch :
				if (setting == 0) {
					gd_iv_leak_callrecord_switch
							.setBackgroundResource(R.drawable.gd_setting_switch_close);
					sp.update(Constant.CALLRECORDER_OPEN_CLOSE, 1);
					setting = 1;
				} else if (setting == 1) {
					gd_iv_leak_callrecord_switch
							.setBackgroundResource(R.drawable.gd_setting_switch_open);
					sp.update(Constant.CALLRECORDER_OPEN_CLOSE, 0);
					setting = 0;
				}
				Log.i("setting", sp.get(Constant.CALLRECORDER_OPEN_CLOSE, 0)
						+ "");
				break;
		}

	}
}
