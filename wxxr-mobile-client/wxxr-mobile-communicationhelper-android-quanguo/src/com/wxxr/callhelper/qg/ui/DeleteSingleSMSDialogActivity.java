package com.wxxr.callhelper.qg.ui;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;

public class DeleteSingleSMSDialogActivity extends Activity
{
	String single_address;
	ProgressDialog mProgressDialog;
	LouHuaDao ldao;
	int singleid = 0;
	boolean huizhi = false;
	//boolean louhua = false;
	HuiZhiBaoGaoDao hdao;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delete_single_dialog);
		ldao = LouHuaDao.getInstance(this);
		hdao = HuiZhiBaoGaoDao.getInstance(this);
		Intent intent = getIntent();
		singleid = intent.getIntExtra("singleid", 0);
		huizhi = intent.getBooleanExtra("huizhi", false);

		findViewById(R.id.iv_finish).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		findViewById(R.id.bt_sure).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{

				new Thread(new Runnable()
				{

					@Override
					public void run()
					{

						if (huizhi)
						{
							hdao.deleteSMSHuiZhi(singleid);
							finish();
						}
						else
						{
							ldao.deleteSMSLouHua(singleid);
							finish();
						}

					}
				}).start();

				
			}
		});

	}



}
