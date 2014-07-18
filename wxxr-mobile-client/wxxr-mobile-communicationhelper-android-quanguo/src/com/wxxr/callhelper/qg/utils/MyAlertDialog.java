package com.wxxr.callhelper.qg.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.ClientInfo;
import com.wxxr.callhelper.qg.service.DownAPK;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.mobile.android.app.AppUtils;

public class MyAlertDialog {

	private ManagerSP sp;
	private Context context;

	public MyAlertDialog(Context context) {
		this.sp = ManagerSP.getInstance(context);
		this.context = context;
	}

	public void isFirstEntry(int year, int month, int day) {
		String today = Tools.getToday();
		String[] ymd = today.split("-");
		int currYear = Integer.parseInt(ymd[0]);
		int currMonth = Integer.parseInt(ymd[1]);
		int currDay = Integer.parseInt(ymd[2]);

		if (currYear != year || currMonth != month || currDay != day) {
			sp.update("year", currYear);
			sp.update("month", currMonth);
			sp.update("day", currDay);

			this.runAlertDialog();
		}
	}

	public void runAlertDialog() {
		final String downloadUrl = sp.get("url", "");
		// String strNetworkVersion = sp.get("network_version", "");
		String description = sp.get("description", "");
		if (description.contains("wr")) {
			description = description.replace("wr", "\n");
		}
		if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class) != null) {
			AppUtils.getFramework().getService(IUserUsageDataRecorder.class)
					.doRecord(ActivityID.UPDATEDIALOG.ordinal());
		}

		Intent t = new Intent(context, ConfirmDialogActivity.class);
		ClientInfo clientInfo = new ClientInfo();

		clientInfo.setDescription(description);
		clientInfo.setUrl(downloadUrl);

		t.putExtra(Constant.DIALOG_KEY, Constant.VERSION);
		t.putExtra(Constant.CLIENT_INFO, clientInfo);
		t.putExtra(Constant.PUSH_IN, "iampush");
		context.startActivity(t);

		// new AlertDialog.Builder(context)
		// // .setIcon(R.drawable.ic_launcher)
		// .setTitle("发现新版本！")
		// .setMessage(
		// "\n更新内容：\n" + description)
		// .setPositiveButton("立即升级",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // sd卡是否可用，并且，是否为可读写
		// if (Environment.getExternalStorageState()
		// .equals(Environment.MEDIA_MOUNTED)) {
		//
		// if (!TextUtils.isEmpty(downloadUrl)) {
		// // ApkInstall install = new ApkInstall(
		// // context);
		// if (AppUtils.getFramework().getService(IUserUsageDataRecorder.class)
		// != null) {
		// AppUtils.getFramework().getService(IUserUsageDataRecorder.class).doRecord(ActivityID.UPDATENOW.ordinal());
		// }
		// Intent t = new Intent(context,
		// DownAPK.class);
		// t.putExtra("downurl", downloadUrl);
		// context.startService(t);
		// // if(Tools.hasNetwork(context)){
		// // install.start(downloadUrl);
		// // Tools.showToast(context,
		// // "正在下载，请稍候...");
		// // }else{
		// // Tools.showToast(context, "请检查网络...");
		// // }
		// } else {
		// Tools.showToast(context, "未获取到下载地址！");
		// Log.v("downloadUrl", "下载地址为空");
		// }
		//
		// } else {
		// Tools.showToast(context, "请检查SD卡是否可用后重试!");
		// }
		//
		// }
		// })
		// .setNegativeButton("稍候升级",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int which) {
		// }
		// }).show();
	}
}
