/*
 * @(#)WeiboManager.java	 2012-5-29
 *
 * Copyright 2004-2012 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.utils;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;

/**
 * @class desc A WeiboManager.新浪微博管理
 * 
 * @author liuzhongnan
 * @version $Revision: 1.2 $
 * @created time 2012-5-29 上午10:16:24
 */
public class WeiboManager {
	private Activity   activity;

	// 设置appkey及appsecret，如何获取新浪微博appkey和appsecret请另外查询相关信息，此处不作介绍
	private static final String CONSUMER_KEY = "2125600800";// 替换为开发者的appkey，例如"1646212960";
	private static final String CONSUMER_SECRET = "ca5cb8acf5d0e79559d47c152ec6f7d9";// 替换为开发者的appkey，例如"94098772160b6f8ffc1315374d8861f9";

	public WeiboManager(Activity a) {
		activity = a;
	}

	/** 判断是否绑定 */
	public boolean isBind() {
		if (Weibo.getInstance().getAccessToken() == null)
			return false;
		return true;
	}

	/** 绑定 */
	public void bind() {
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);

		// Oauth2.0
		// 隐式授权认证方式

		// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
		// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
		// 应用回调页不可为空

		// Oauth2.0 隐式授权认证方式
		weibo.setRedirectUrl("http://www.wxxr.com.cn/wxxr.html");
		weibo.authorize(activity, new AuthDialogListener());
	}

	private String content = null;
	private String picPath = null;
	private boolean needShare = false;

	public String temptoken;

	public String tempexpires_in;

	/** 绑定并发送微博 */
	public void bindAndShare2weibo(String content, String picPath) {
		this.content = content;
		this.picPath = picPath;
		needShare = true;
		Weibo weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);

		// Oauth2.0
		// 隐式授权认证方式

		// 对应的应用回调页可在开发者登陆新浪微博开发平台之后，
		// 进入我的应用--应用详情--应用信息--高级信息--授权设置--应用回调页进行设置和查看，
		// 应用回调页不可为空

		// Oauth2.0 隐式授权认证方式
		weibo.setRedirectUrl("http://www.wxxr.com.cn/wxxr.html");
		weibo.authorize(activity, new AuthDialogListener());
	}

	/** 解除绑定 */
	public void removeBind() {
		Utility.clearCookies(activity);
		Weibo.getInstance().setAccessToken(null);
		Toast.makeText(activity, "解除绑定成功", Toast.LENGTH_LONG).show();
	}

	class AuthDialogListener implements WeiboDialogListener {

		@Override
		public void onComplete(Bundle values) {
			try {
				String token = values.getString("access_token");
				String expires_in = values.getString("expires_in");
				temptoken=token;
				tempexpires_in=expires_in;
				// mToken.setText("access_token : " + token + "  expires_in: "
				// + expires_in);
				AccessToken accessToken = new AccessToken(token,
						CONSUMER_SECRET);
				accessToken.setExpiresIn(expires_in);
				Weibo.getInstance().setAccessToken(accessToken);
				share2weibo(content, picPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Intent intent = new Intent();
			// intent.setClass(ArticleActivity.this, LogoActivity.class);
			// startActivity(intent);

			// File file = Environment.getExternalStorageDirectory();
			// String sdPath = file.getAbsolutePath();
			// 请保证SD卡根目录下有这张图片文件
			// String picPath = sdPath + "/" + "abc.jpg";
			// File picFile = new File(picPath);
			// if (!picFile.exists()) {
			// Toast.makeText(ArticleActivity.this, "图片" + picPath + "不存在！",
			// Toast.LENGTH_SHORT)
			// .show();
			// picPath = null;
			// }

			// try {

			// if (BindWeiboActivity.instance != null) {
			// BindWeiboActivity.instance.setState();
			// }

			// if (needShare) {
			// try {
			// share2weibo(content, picPath);
			// } catch (WeiboException e) {
			// }
			// }

			// share2weibo(content, null);
			// Intent i = new Intent(activity, ShareActivity.class);
			// activity.startActivity(i);

			// } catch (WeiboException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } finally {
			//
			// }
		}

		@Override
		public void onError(DialogError e) {
			Toast.makeText(activity, "Auth error : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			// Toast.makeText(getApplicationContext(), "Auth cancel",
			// Toast.LENGTH_LONG).show();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			Toast.makeText(activity, "Auth exception : " + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}

	}

	public void share2weibo(String content, String picPath) {
		try {
			Weibo weibo = Weibo.getInstance();
			if(temptoken!=null){
				AccessToken accessToken = new AccessToken(temptoken, CONSUMER_SECRET);
				accessToken.setExpiresIn(tempexpires_in);	
				weibo.setAccessToken(accessToken);
			}
			weibo.share2weibo(activity, weibo.getAccessToken().getToken(),
					weibo.getAccessToken().getSecret(), content, picPath, null);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
