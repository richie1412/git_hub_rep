package com.wxxr.callhelper.qg.ui;

import com.wxxr.callhelper.qg.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

public class SheZhiErrorActivity extends Activity
{
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.pingdaoerror);

		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{

				finish();

			}

		};

		new Thread(new Runnable()
		{

			@Override
			public void run()
			{

				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}

				Message msg = handler.obtainMessage();

				handler.sendMessage(msg);

			}
		}).start();

	}

}
