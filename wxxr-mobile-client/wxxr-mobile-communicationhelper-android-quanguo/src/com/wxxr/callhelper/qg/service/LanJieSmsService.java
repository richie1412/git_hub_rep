package com.wxxr.callhelper.qg.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.wxxr.callhelper.qg.receiver.InterceptSMSReceiverNew;

public class LanJieSmsService extends Service
{

	InterceptSMSReceiverNew mReceiver;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		Log.e("onCreate", "onCreateLanJieSmsService");
		
		if(mReceiver != null)
		{
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		IntentFilter localIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		localIntentFilter.setPriority(2147483647);
		mReceiver = new InterceptSMSReceiverNew();
		registerReceiver(mReceiver, localIntentFilter);
		
		
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		

		Log.e("onStart", "onStartLanJieSmsService");
		
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy()
	{
		unregisterReceiver(mReceiver);
		Intent it = new Intent(LanJieSmsService.this, LanJieSmsService.class);
		this.startService(it);
	}

}
