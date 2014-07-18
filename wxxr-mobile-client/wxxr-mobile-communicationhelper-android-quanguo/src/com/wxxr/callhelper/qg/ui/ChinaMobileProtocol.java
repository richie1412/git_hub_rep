package com.wxxr.callhelper.qg.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;

public class ChinaMobileProtocol extends BaseActivity {

	private TextView tv_titlebar_name;

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
	}

	public void processLogic() {
		tv_titlebar_name.setText("中国移动通信助手客户端协议");
		tv_titlebar_name.setTextColor(getResources().getColor(R.color.more_item));
	}

}
