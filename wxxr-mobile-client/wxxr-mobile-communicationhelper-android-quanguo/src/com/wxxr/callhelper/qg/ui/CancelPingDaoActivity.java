package com.wxxr.callhelper.qg.ui;

import com.wxxr.callhelper.qg.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.TextView;

public class CancelPingDaoActivity extends Activity
{
	Handler handler;
	TextView tv_content;
    int pingdao_style = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pingdaoxinxi);

		Intent intent = getIntent();
		
		pingdao_style = intent.getIntExtra("pingdao_style", 0);
		
		tv_content = (TextView) findViewById(R.id.tv_text1);
		
		switch (pingdao_style)
		{
		case 1:
			tv_content.setText(R.string.cancelpingdao1);
			break;
		case 2:
			tv_content.setText(R.string.cancelpingdao2);
			break;
		case 3:
			tv_content.setText(R.string.cancelpingdao3);
			break;
		case 4:
			tv_content.setText(R.string.cancelpingdao4);
			break;
		case 5:
			tv_content.setText(R.string.cancelpingdao5);
			break;
		case 6:
			tv_content.setText(R.string.cancelpingdao6);
			break;
		case 7:
			tv_content.setText(R.string.cancelpingdao7);
			break;
		case 8:
			tv_content.setText(R.string.cancelpingdao8);
			break;
		case 9:
			tv_content.setText(R.string.cancelpingdao9);
			break;
		case 10:
			tv_content.setText(R.string.cancelpingdao10);
			break;
		case 11:
			tv_content.setText(R.string.cancelpingdao11);
			break;
		case 12:
			tv_content.setText(R.string.cancelpingdao12);
			break;
		case 13:
			tv_content.setText(R.string.cancelpingdao13);
			break;
		case 14:
			tv_content.setText(R.string.cancelpingdao14);
			break;
		case 15:
			tv_content.setText(R.string.cancelpingdao15);
			break;

		}
		
		
		
		
		
	     //tv_content.setText("您已取消XX频道");
		
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
