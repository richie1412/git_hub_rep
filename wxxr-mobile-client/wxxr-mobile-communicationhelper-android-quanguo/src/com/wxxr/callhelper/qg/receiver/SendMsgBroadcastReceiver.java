package com.wxxr.callhelper.qg.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat.Action;
import android.widget.Toast;

public class SendMsgBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		switch(getResultCode()){
		case -1:
			Toast.makeText(context, "短信发送成功！", Toast.LENGTH_SHORT).show();
			break;
		case 1://Activity.RESULT_FIRST_USER(自定义)
			Toast.makeText(context, "短信发送成功！", Toast.LENGTH_SHORT).show();
			break;
		case 0:
			Toast.makeText(context, "短信发送失败！", Toast.LENGTH_SHORT).show();
			break;
		}
	}

}
