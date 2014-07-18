package com.wxxr.callhelper.qg.ui;

import com.wxxr.callhelper.qg.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SexySelectActivity extends Activity
{

	CheckBox cb1;
	CheckBox cb2;
	RelativeLayout rl1;
	RelativeLayout rl2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pingdao_sexsy_select);

		cb1 = (CheckBox) findViewById(R.id.sex_check1);
		cb2 = (CheckBox) findViewById(R.id.sex_check2);

//		cb1.setChecked(true);
//		cb2.setChecked(false);

		rl1 = (RelativeLayout) findViewById(R.id.rl_account_middle1);
		rl2 = (RelativeLayout) findViewById(R.id.rl_account_middle2);

		rl1.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (cb1.isChecked())
				{

					cb2.setChecked(false);

					Intent intent = new Intent();
					intent.putExtra("sexstyle", 1);
					setResult(155, intent);
					finish();

				}
				else
				{
					cb1.setChecked(true);
					cb2.setChecked(false);
					Intent intent = new Intent();
					intent.putExtra("sexstyle", 1);
					setResult(155, intent);
					finish();

				}

			}
		});
		rl2.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (cb2.isChecked())
				{

					cb1.setChecked(false);
					Intent intent = new Intent();
					intent.putExtra("sexstyle", 0);
					setResult(155, intent);
					finish();
				}
				else
				{
					cb2.setChecked(true);
					cb1.setChecked(false);
					Intent intent = new Intent();
					intent.putExtra("sexstyle", 0);
					setResult(155, intent);
					finish();
				}

			}
		});

	}

}
