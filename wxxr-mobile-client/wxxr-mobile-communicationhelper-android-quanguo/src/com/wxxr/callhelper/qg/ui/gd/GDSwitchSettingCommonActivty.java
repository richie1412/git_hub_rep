package com.wxxr.callhelper.qg.ui.gd;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.SwitchSettingBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.utils.ManagerSP;

/**
 * 开关设置界面通用activity
 * 
 * @author cuizaixi
 * 
 */
public class GDSwitchSettingCommonActivty extends BaseActityExtend {
	private TextView tv_description;
	private TextView tv_name;
	private RadioButton rb_switch;
	private ManagerSP sp;
	public final static String SWITCH_SETTING = "switch_setting";
	private SwitchSettingBean mBean;
	private int setting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContent(R.layout.gd_switch_setting_common);
		findView();
		processLogic();
	}

	protected void findView() {
		tv_name = (TextView) findViewById(R.id.gd_tv_switch_name);
		tv_description = (TextView) findViewById(R.id.gd_tv_description_below);
		rb_switch = (RadioButton) findViewById(R.id.gd_iv_switch);
		rb_switch.setOnClickListener(this);
		iv_right.setVisibility(View.INVISIBLE);
	}

	protected void processLogic() {
		mBean = (SwitchSettingBean) getIntent().getSerializableExtra(
				SWITCH_SETTING);
		sp = ManagerSP.getInstance();
		if (mBean == null) {
			return;
		}
		if (mBean.getTitle() != null) {
			tv_title_name.setText(mBean.getTitle());
		}
		if (mBean.getLable() != null) {
			tv_name.setText(mBean.getLable());
		}
		if (mBean.getDescriptionId() > 0) {
			tv_description.setText(getResources().getString(
					mBean.getDescriptionId()));
		}
		if (mBean.getDefaultSetting() > 0) {
			setting = sp.get(mBean.getSPKey(), mBean.getDefaultSetting());
			if (setting == 0) {
				rb_switch.setChecked(true);
			} else if (setting == 1) {
				rb_switch.setChecked(false);
			}
		}
		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		rb_switch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (setting == 0) {
					rb_switch.setChecked(false);
					sp.update(mBean.getSPKey(), 1);
					setting = 1;
				} else {
					rb_switch.setChecked(true);
					sp.update(mBean.getSPKey(), 0);
					setting = 0;
				}
			}
		});
	}
}
