package com.wxxr.callhelper.qg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;

public class AboutUsActivity extends BaseActivity {

	private TextView tv_titlebar_name, tv_say_hello;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.china_mobile_protocol);
		
		findView();
		
		processLogic();
	}

	public void findView() {
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
//		findViewById(R.id.ll_top_titlebar).setBackgroundResource(R.drawable.top_titlebar_light_bg);
		
		tv_say_hello =(TextView) findViewById(R.id.tv_say_hello);
	}

	public void processLogic() {
		tv_titlebar_name.setText("关于我们");
		tv_titlebar_name.setTextColor(getResources().getColor(R.color.white));

		tv_say_hello.setVisibility(View.GONE);
	}

}
