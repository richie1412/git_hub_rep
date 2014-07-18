package com.wxxr.callhelper.qg.ui.gd;

import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;
/**
 * BaseActivity的扩展类
 * 
 * @author cuizaixi
 * 
 */
public abstract class BaseActityExtend extends BaseActivity {
	protected ImageView iv_left;
	protected ImageView iv_right;
	protected TextView tv_title_name;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
	}
	protected abstract void findView();
	protected abstract void processLogic();
	protected void setContent(int id) {
		setContentView(id);
		initTitleBar();
	}
	protected void showToast(String text) {
		Toast.makeText(this, text, 1).show();
	}
	protected void initTitleBar() {
		iv_left = (ImageView) findViewById(R.id.gd_iv_titlebar_left);
		iv_right = (ImageView) findViewById(R.id.gd_iv_titlebar_right);
		tv_title_name = (TextView) findViewById(R.id.tv_titlebar_name);
	}
}
