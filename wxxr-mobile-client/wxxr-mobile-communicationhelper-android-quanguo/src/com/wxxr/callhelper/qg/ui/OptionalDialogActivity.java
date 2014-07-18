package com.wxxr.callhelper.qg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.OptionalDialogBean;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
/**
 * 
 * @author cuizaixi
 * 
 */
public class OptionalDialogActivity extends BaseActityExtend {
	private TextView tv_content;
	private Intent mIntent;
	private TextView tv_dialog_title_name;
	private OptionalDialogBean mBean;
	public static final String OPTIONAL_BEAN = "optional_bean";
	private String mTitleName;
	private String mContent;
	private LinearLayout ll_titile;
	private Button btn_left;
	private Button btn_right;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContent(R.layout.optional_dialog);
		findView();
		processLogic();
	}
	@Override
	protected void findView() {
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_dialog_title_name = (TextView) findViewById(R.id.tv_dialog_title_name);
		ll_titile = (LinearLayout) findViewById(R.id.ll_dialog_title);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_left = (Button) findViewById(R.id.btn_left);
	}

	@Override
	protected void processLogic() {
		mIntent = getIntent();
		mBean = (OptionalDialogBean) mIntent.getParcelableExtra(OPTIONAL_BEAN);
		if (mBean != null) {
			mTitleName = mBean.getTitleName();
			mContent = mBean.getContent();
		}
		if (mTitleName == null) {
			ll_titile.setVisibility(View.GONE);
		} else {
			tv_dialog_title_name.setText(mTitleName);
		}
		if (mContent != null) {
			tv_content.setText(mContent);
		}
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_OK);
				finish();
			}
		});
		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
}
