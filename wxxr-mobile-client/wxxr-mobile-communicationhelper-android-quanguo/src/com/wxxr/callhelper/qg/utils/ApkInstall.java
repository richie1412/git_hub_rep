package com.wxxr.callhelper.qg.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.wxxr.callhelper.qg.constant.Constant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ApkInstall implements Runnable {

	private Thread thread;
	private Context context;
	private String url;
	private ManagerSP sp;

	public ApkInstall(Context context) {
		this.context = context;
		sp = ManagerSP.getInstance(context);
	}

	public boolean start(String url) {
		this.url = url;
		thread = new Thread(this);
		thread.start();
		return true;
	}

	/**
	 * 安装apk
	 * 
	 * @param filepath
	 */
	private void installApk(String filepath) {
		try {
			Log.i("filepath", filepath);
			File file = new File(filepath);
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			String type = "application/vnd.android.package-archive";
			intent.setDataAndType(Uri.fromFile(file), type);
			context.startActivity(intent);
		} catch (Exception e) {
		}
	}

	/**
	 * 下载apk
	 */
	public void getApk() {
		String downloadPath = Environment.getExternalStorageDirectory()
				.getPath() + "/download";
		String fileName = url.substring(url.lastIndexOf("/"));
		Log.i("fileName", fileName);
		File file = new File(downloadPath);
		if (!file.exists()) {
			file.mkdir();//创建目录
		}
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				InputStream is = httpResponse.getEntity().getContent();
				FileOutputStream fos = new FileOutputStream(downloadPath
						+ fileName);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) != -1) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
				sp.update("guide", 0);//重置新手指引
				sp.update(Constant.UNINSTALL, 1);
				Log.i("filelength", file.length() + "");
//				if(file.length()){
//					file.delete();
//					Toast.makeText(context, "下载失败", 1).show();
//				}else{
					installApk(downloadPath + fileName);
//				}
				// MainActivityGroup.instance.showNotification("下载完成","下载完成","文件完成...",R.drawable.download,R.drawable.download);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		getApk();
	}
}
