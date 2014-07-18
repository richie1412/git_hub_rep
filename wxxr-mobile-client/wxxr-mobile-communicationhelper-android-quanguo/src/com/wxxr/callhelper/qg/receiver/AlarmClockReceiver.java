package com.wxxr.callhelper.qg.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.ui.CSAlertActivity;
import com.wxxr.callhelper.qg.ui.CSMainAcitivity;
import com.wxxr.callhelper.qg.ui.CSManagerActvity;

public class AlarmClockReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 打开新的activity
		ComSecretaryBean bean = (ComSecretaryBean) intent
				.getSerializableExtra(CSManagerActvity.CS_BEAN);
		Intent i = new Intent(context, CSAlertActivity.class);
		i.putExtra(CSManagerActvity.CS_BEAN, bean);
		// 由于receiver他不是存在于任务栈中，在广播接收者里面去启动一个activity就必须指定标志位
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}
