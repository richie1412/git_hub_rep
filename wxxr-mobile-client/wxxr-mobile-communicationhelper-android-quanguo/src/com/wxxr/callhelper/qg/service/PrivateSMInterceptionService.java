package com.wxxr.callhelper.qg.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.wxxr.callhelper.qg.receiver.PrivateSMReceiver;
import com.wxxr.mobile.core.log.api.Trace;

public class PrivateSMInterceptionService extends Service
{
	private static final Trace log = Trace.register(PrivateSMInterceptionService.class);

	private PrivateSMReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		log.info("Creating PrivateSMInterceptionService ...");
		
		if(mReceiver != null)
		{
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		IntentFilter localIntentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		localIntentFilter.setPriority(Integer.MAX_VALUE);
		mReceiver = new PrivateSMReceiver();
		registerReceiver(mReceiver, localIntentFilter);
		
		
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		unregisterReceiver(mReceiver);
		Intent it = new Intent(this, PrivateSMInterceptionService.class);
		this.startService(it);
	}

}
