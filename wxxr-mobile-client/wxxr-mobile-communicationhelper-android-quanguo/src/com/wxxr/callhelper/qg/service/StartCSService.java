package com.wxxr.callhelper.qg.service;


import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.ui.CSMainAcitivity;
import com.wxxr.callhelper.qg.utils.Tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * 开机后弹出未提醒的小秘书
 * 
 * @author cuizaixi
 * @create 2014-3-17 上午10:47:56
 */
public class StartCSService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		int count = ComSecretaryDao.getInstance(this).getUnhappenItemCount();
		if (count > 0) {
			Tools.sendSystemNofy(this, null, R.drawable.app_icon_small, "提醒",
					"您有" + String.valueOf(count) + "个过期未提醒事项",
					CSMainAcitivity.class, true);
		}

	}
}
