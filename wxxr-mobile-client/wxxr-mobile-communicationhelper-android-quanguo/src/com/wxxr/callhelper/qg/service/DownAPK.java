/*
 * @(#)DownAPK.java	 May 21, 2012
 *
 * Copyright 2004-2012 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;
/**
 * 
 * 下载 客户端的升级包
 *
 * <p>detailed comment
 * @see
 * @since 1.0
 */
public class DownAPK extends Service {
	private final int DownApk = 100;
	private String apkPath;
	private static final int DOWNLOAD_PREPARE = 0;
	private static final int DOWNLOAD_WORK = 1;
	private static final int DOWNLOAD_OK = 2;
	private static final int DOWNLOAD_ERROR = 3;
	private boolean downloadOk = false;
	private String TAG = "DownAPK";
	private Notification notification;
	private boolean isrun = false;
	/**
	 * 下载的url
	 */
	private String url = "";
	private String filePath;

	/**
	 * 文件大小
	 */
	int fileSize = 0;

	/**
	 * 下载的大小
	 */
	int downloadSize = 0;
	NotificationManager mNotifiManager;

	@Override
	public void onCreate() {
		super.onCreate();
		
		//filePath = Constant.			
			
		// showNotification();
		// apkPath = HttpResource.getInstallApk();
		// mNotifiManager.cancel(DownApk);
		// installApk(apkPath);
		// DownAPK.this.stopSelf();

	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		
		super.onStart(intent, startId);
		if(!isrun){
		url=intent.getStringExtra("downurl");		
		String downloadPath = Environment.getExternalStorageDirectory()
				.getPath() + "/download";
		String fileName = url.substring(url.lastIndexOf("/"));
		Log.i("fileName", fileName);
		File file = new File(downloadPath);
		if (!file.exists()) {
			file.mkdir();//创建目录
		}
		filePath = downloadPath+fileName;
				
	//	QLog.debug(TAG, filePath);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				downloadFile();
			}
		});
		thread.start();
		if (downloadOk) // 下载完成后 ，把图片显示在ImageView上面
		{
			installApk(apkPath);
		}
		isrun=true;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// FIXME onBind
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mNotifiManager.cancel(DownApk);
		DownAPK.this.stopSelf();
	}

	/**
	 * handler
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_PREPARE:
				mNotifiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notification = new Notification(R.drawable.updatelogo,
						"通信助手新版本下载中……", System.currentTimeMillis());
				
				// CharSequence contentTitle = "辣妈育儿更新包下载";
				// CharSequence contentText = "Hello World!";
				PendingIntent contentIntent = PendingIntent.getActivity(
						DownAPK.this, 0, new Intent(),
						PendingIntent.FLAG_UPDATE_CURRENT);
				// notification.setLatestEventInfo(this, contentTitle,
				// contentText, contentIntent);
				RemoteViews contentView = new RemoteViews(
						DownAPK.this.getPackageName(), R.layout.download_notifi);
				notification.contentView = contentView;
				notification.contentView.setProgressBar(R.id.pb, fileSize, 0,
						false);
				notification.contentIntent = contentIntent;
				mNotifiManager.notify(DownApk, notification);
				break;
			case DOWNLOAD_WORK:
				if(System.currentTimeMillis() % 100 == 0){
				//	QLog.debug(TAG, "已经下载:" + downloadSize);
					notification.contentView.setProgressBar(R.id.pb, fileSize,
							downloadSize, false);
					int res = downloadSize * 100 / fileSize;
					notification.contentView.setTextViewText(R.id.downing, "已下载："
							+ res + "%");
					mNotifiManager.notify(DownApk, notification);
				}
				break;
			case DOWNLOAD_OK:
				downloadOk = true;
				downloadSize = 0;
				fileSize = 0;
				mNotifiManager.cancel(DownApk);
				Toast.makeText(DownAPK.this, "下载成功", Toast.LENGTH_SHORT).show();
				installApk(filePath);
				DownAPK.this.stopSelf();
				break;
			case DOWNLOAD_ERROR:
				downloadSize = 0;
				fileSize = 0;
				Toast.makeText(DownAPK.this, "下载失败", Toast.LENGTH_SHORT).show();
				
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** * 下载文件 */
	private void downloadFile() {
		try {
			URL u = new URL(url);
			URLConnection conn = u.openConnection();
			InputStream is = conn.getInputStream();
			fileSize = conn.getContentLength();
		//	QLog.debug(TAG, String.valueOf(fileSize));
			if (fileSize < 1 || is == null) {
				sendMessage(DOWNLOAD_ERROR);
			} else {
				sendMessage(DOWNLOAD_PREPARE);
				FileOutputStream fos = new FileOutputStream(filePath);
				byte[] bytes = new byte[1024];
				int len = -1;
				while ((len = is.read(bytes)) != -1) {
					fos.write(bytes, 0, len);
					fos.flush();
					downloadSize += len;
					sendMessage(DOWNLOAD_WORK);
				}
				sendMessage(DOWNLOAD_OK);
				is.close();
				fos.close();
			}
		} catch (Exception e) {
			sendMessage(DOWNLOAD_ERROR);
			e.printStackTrace();
		}
	}

	/*** * 得到文件的路径 * * @return */
	public String getFilePath() {
		return filePath;
	}

	/** * @param what */
	private void sendMessage(int what) {
		Message m = new Message();
		m.what = what;
		//if(System.currentTimeMillis() % 100 == 0 && what == DOWNLOAD_WORK){
			handler.sendMessage(m);
		//}
	}

	private void installApk(String filename) {
		File file = new File(filename);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW); // 浏览网页的Action(动作)
		String type = "application/vnd.android.package-archive";
		intent.setDataAndType(Uri.fromFile(file), type); // 设置数据类型
		startActivity(intent);
	}

	private final IBinder mBinder = new Binder() {
		@Override
		protected boolean onTransact(int code, Parcel data, Parcel reply,
				int flags) throws RemoteException {
			return super.onTransact(code, data, reply, flags);
		}
	};

}
