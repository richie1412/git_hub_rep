package com.wxxr.callhelper.qg.ui;

import com.wxxr.callhelper.qg.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class PingDaoXinXiActivity extends Activity
{
	Handler handler;
	TextView tv_pingdao_content;
	int pingdao_style = 0;
    String child_information;
    String sijiazixun_information;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pingdaoxinxi);
		tv_pingdao_content = (TextView) findViewById(R.id.tv_text1);
		Intent intent = getIntent();
		// intent.putExtra("pingdao_style", 1);
		pingdao_style = intent.getIntExtra("pingdao_style", 0);
		child_information =  intent.getStringExtra("child_information");
		sijiazixun_information =  intent.getStringExtra("sijiazixun_information");
		// tv_pingdao_content.setText(getResources().getIdentifier("pingdaostring1",
		// "string", "com.wxxr.callhelper"));

		switch (pingdao_style)
		{
		case 1:
			tv_pingdao_content.setText(R.string.pingdaostring1);
			break;
		case 2:
			tv_pingdao_content.setText(R.string.pingdaostring2);
			break;
		case 3:
			tv_pingdao_content.setText(R.string.pingdaostring3);
			break;
		case 4:
			tv_pingdao_content.setText(R.string.pingdaostring4);
			break;
		case 5:
			tv_pingdao_content.setText(R.string.pingdaostring5);
			break;
		case 6:
			tv_pingdao_content.setText(R.string.pingdaostring6);
			break;
		case 7:
			tv_pingdao_content.setText(R.string.pingdaostring7);
			break;
		case 8:
			tv_pingdao_content.setText(R.string.pingdaostring8);
			break;
		case 9:
			tv_pingdao_content.setText(R.string.pingdaostring9);
			break;
		case 10:
			tv_pingdao_content.setText(R.string.pingdaostring10);
			break;
		case 11:
			tv_pingdao_content.setText(R.string.pingdaostring11);
			break;
		case 12:
			tv_pingdao_content.setText(R.string.pingdaostring12);
			break;
		case 13:
			tv_pingdao_content.setText(R.string.pingdaostring13);
			break;
		case 14:
			tv_pingdao_content.setText(child_information);
			break;
		case 15:
			tv_pingdao_content.setText(sijiazixun_information);
			break;

		}

		
		handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				int result = msg.what;

				if (result == 1)
				{
					finish();
				}

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

				msg.what = 1;

				handler.sendMessage(msg);

			}
		}).start();

	}

}
