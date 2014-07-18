package com.wxxr.callhelper.qg;

import android.content.BroadcastReceiver;	
import android.content.Context;
import android.content.Intent;

public class CallPhoneReceiver extends BroadcastReceiver
{
	public static String outNumber = null;

	
	@Override
	public void onReceive(Context context, Intent intent)
	{

		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))

		{
			outNumber = null;
			outNumber = getResultData();

		}

	}

}
