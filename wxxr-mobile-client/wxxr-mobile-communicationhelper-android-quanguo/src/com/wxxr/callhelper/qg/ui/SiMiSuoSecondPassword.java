package com.wxxr.callhelper.qg.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.LocusPassWordView;
import com.wxxr.callhelper.qg.widget.LocusPassWordView.OnCompleteListener;

public class SiMiSuoSecondPassword extends BaseActivity implements OnClickListener
{
	private LocusPassWordView lpwv;
	private String firstPassword,secondPassword;
	RelativeLayout rl_cancel;
	RelativeLayout rl_sure;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword_activity2);
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.simi_main_back);
		Bitmap readBitmap = Tools.readBitmap(this, R.drawable.simi_background);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		rl.setBackgroundDrawable(bitmapDrawable);
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView2);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{
				secondPassword = mPassword;
			}
		});
		findView();
		fillData();

	}

	private void findView()
	{
		rl_cancel = (RelativeLayout) findViewById(R.id.rl_cancel);
		rl_sure = (RelativeLayout) findViewById(R.id.rl_sure);
		rl_sure.setOnClickListener(this);
		rl_cancel.setOnClickListener(this);
	}

	private void fillData()
	{
		Intent intent = getIntent();
		this.firstPassword = intent.getStringExtra("password");
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.rl_cancel:
			finish();
			break;
		case R.id.rl_sure:

			if (firstPassword.equals(secondPassword))
			{
				
				setResult(RESULT_OK);
				finish();
			}
			else
			{
				showMessageBox("两次设置的密码不一致,请重新设置");
				lpwv.clearPassword();
				secondPassword = "";
				return;
			}
			break;
		}
	}

}
