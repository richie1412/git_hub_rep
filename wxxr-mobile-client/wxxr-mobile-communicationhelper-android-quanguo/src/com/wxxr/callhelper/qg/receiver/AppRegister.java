package com.wxxr.callhelper.qg.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wxxr.callhelper.qg.utils.WXManager;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
	     System.out.println("------------------zjc onReceive wx-------------");

		final IWXAPI api = WXAPIFactory.createWXAPI(context, null);

		api.registerApp(WXManager.APP_ID);
	}
}
