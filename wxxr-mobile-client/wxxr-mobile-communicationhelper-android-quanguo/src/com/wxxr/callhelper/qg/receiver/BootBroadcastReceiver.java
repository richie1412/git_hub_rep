package com.wxxr.callhelper.qg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wxxr.callhelper.qg.service.LanJieSmsService;
import com.wxxr.callhelper.qg.service.NewCallRecordService;
import com.wxxr.callhelper.qg.service.NewLocationPhoneService;
import com.wxxr.callhelper.qg.service.StartCSService;

/*
 * 启动手机后，BootBroadcastReceiver将启动PhoneListenerService。
 */
public class BootBroadcastReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent)
	{		
		context.startService(new Intent(context, NewCallRecordService.class));
		context.startService(new Intent(context, NewLocationPhoneService.class));
		context.startService(new Intent(context, LanJieSmsService.class));
		context.startService(new Intent(context, StartCSService.class));
	}

}
