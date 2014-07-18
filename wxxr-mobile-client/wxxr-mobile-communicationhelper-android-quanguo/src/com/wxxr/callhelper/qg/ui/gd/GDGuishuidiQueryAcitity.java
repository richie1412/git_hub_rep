package com.wxxr.callhelper.qg.ui.gd;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IAdvaceRegionQueryService;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.mobile.core.log.api.Trace;
/**
 * 广东版—— 归属地查询
 * 
 * @author cuizaixi
 * 
 */
public class GDGuishuidiQueryAcitity extends BaseActivity {
	private static final Trace log = Trace
			.register(GDGuishuidiQueryAcitity.class);
	private TextView tv_guishudi;
	private EditText et_number;
	private ManagerSP sp;
	private int phoneFlag;
	private Button btn_switch;
	private Button btn_query;
	private boolean continueQuery = true;// 如果已经查到相关结果，则停止调用查询方法。
	int startLength = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_guishuidi_query);
		findView();
		processLogic();
		((com.wxxr.callhelper.qg.widget.BottomTabBar) findViewById(R.id.home_bottom_tabbar))
				.setActivtiy(this, 0);
	}

	private void findView() {
		btn_query = (Button) findViewById(R.id.btn_guishudi_query);
		btn_switch = (Button) findViewById(R.id.btn_guishuidi_switch);
		tv_guishudi = (TextView) findViewById(R.id.tv_guishudi);
		et_number = (EditText) findViewById(R.id.et_input_number);
		TextView tv_mid = (TextView) findViewById(R.id.tv_titlebar_name);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		tv_mid.setText("号码归属地");
		btn_switch.setOnClickListener(this);
		btn_query.setOnClickListener(this);
	}
	private void processLogic() {
		sp = ManagerSP.getInstance();
		phoneFlag = sp.get(Constant.LOCATION_PHONE, 0);
		if (phoneFlag == 0) {
			btn_switch.setBackgroundResource(R.drawable.setting_switch_on);
		} else {
			btn_switch.setBackgroundResource(R.drawable.gd_setting_switch_close);
		}
		btn_query.setBackgroundResource(R.drawable.guishudi_btn_unclickble);
		// 点击后改变hint内容和按钮样式。
		et_number.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					et_number.setText("");
					btn_query.setClickable(true);
					btn_query
							.setBackgroundResource(R.drawable.guishudi_btn_selector);
				}
			}
		});
		// 号码字数监听，匹配
		et_number.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				int endLength = s.toString().length();
				if (s.length() == 0) {
					tv_guishudi.setText("归属地信息");
					tv_guishudi.setTextColor(getApplicationContext()
							.getResources().getColor(
									R.color.gd_callrecord_play_time));
					btn_query
							.setBackgroundResource(R.drawable.guishudi_btn_unclickble);
					btn_query.setClickable(false);
					continueQuery = true;
				} else if ((s.toString().startsWith("1") && s.length() <= 11
						&& s.length() >= 5 && s.length() != 6)
						|| (s.toString().startsWith("0") && s.length() >= 3 && s
								.length() < 5)
						|| (s.toString().startsWith("9") && s.length() == 5)) {
					btn_query
							.setBackgroundResource(R.drawable.guishudi_btn_press);
					btn_query.setClickable(true);
					if (endLength < startLength) {
						continueQuery = true;
					}
					if (continueQuery) {
						searchChecking(s.toString());
						startLength = s.toString().length();
					}
				} else if (s.toString().length() > 0) {
					btn_query
							.setBackgroundResource(R.drawable.guishudi_btn_press);
					btn_query.setClickable(true);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.gd_iv_titlebar_left :
				this.finish();
				break;
			case R.id.btn_guishudi_query :
				String value = et_number.getText().toString();
				if ((value.toString().startsWith("1") && value.length() >= 5 && value
						.length() <= 11)
						|| ((value.toString().startsWith("0")
								&& value.length() > 2 && value.length() < 5))) {
					searchChecking(value.toString());
				} else {
					if (continueQuery) {
						tv_guishudi.setText("归属地信息");
						tv_guishudi.setTextColor(getApplicationContext()
								.getResources().getColor(
										R.color.gd_callrecord_play_time));
						Toast.makeText(getApplicationContext(), "您输入的有误，请重新输入",
								1).show();
					}
				}
				break;
			case R.id.btn_guishuidi_switch :
				phoneFlag = sp.get(Constant.LOCATION_PHONE, 0);
				if (phoneFlag == 0) {
					phoneFlag=1;
					btn_switch
							.setBackgroundResource(R.drawable.gd_setting_switch_close);
					sp.update(Constant.LOCATION_PHONE, 1);

				} else {
					phoneFlag=0;
					btn_switch
							.setBackgroundResource(R.drawable.setting_switch_on);
					sp.update(Constant.LOCATION_PHONE, 0);
				}
				Log.i("phoneFlag", sp.get(Constant.LOCATION_PHONE, 0)+"");
				break;
		}
	}
	private void searchChecking(String value) {
		if (StringUtil.isEmpty(value)) {
			Toast.makeText(getApplicationContext(), "请输入手机号码或区号", 1).show();
			return;
		}
		String region = getService(IAdvaceRegionQueryService.class)
				.getRegionByNum(et_number.getText().toString(), this);
		if (region != null) {
			continueQuery = false;
			tv_guishudi.setText(region);
			tv_guishudi.setTextColor(Color.parseColor("#000000"));
		} else if (et_number.length() > 11) {
			Toast.makeText(getApplicationContext(), "查询不到，请校验号码", 1).show();
		}
	}
}
