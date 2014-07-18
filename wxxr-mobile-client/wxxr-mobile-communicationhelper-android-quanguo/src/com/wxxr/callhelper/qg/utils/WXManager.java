package com.wxxr.callhelper.qg.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.BitmapFactory;

import com.tencent.mm.sdk.openapi.GetMessageFromWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.wxxr.mobile.core.log.api.Trace;

public class WXManager {
	private static final Trace log = Trace.register(WXManager.class);

	private static final int THUMB_SIZE = 150;

	public static final String APP_ID = "wx287af3024207f8ba";
	private Context context;
	private IWXAPI api;

	public WXManager(Context context) {
		this.context = context;
	}

	// 应用ID注册到微信
	public IWXAPI regToWx() {
		api = WXAPIFactory.createWXAPI(context, APP_ID, true);
		api.registerApp(APP_ID);
		return api;
	}

	// 发送请求到微信
	public void sendReqToWx(String title,String desciption,String weburi,String imageUri) {
		WXWebpageObject webobj = new WXWebpageObject(weburi);
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = webobj;
		msg.description = desciption;
		msg.title=title;
		Bitmap bmp;
		InputStream is=null;
		try {
			bmp = BitmapFactory.decodeStream(new URL(URLDecoder.decode(URLEncoder.encode(imageUri))).openStream());
			Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//			if (log.isDebugEnabled()){
//				log.debug("thumbBmp getByteCount=" +thumbBmp.getByteCount());
//			}
			bmp.recycle();
			msg.thumbData = bmpToByteArray(thumbBmp, true);
		} catch (Exception e) {
			log.error("微信分享失败"+e.getMessage());
		}finally{
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					
				}
			}
		}

			// 构造一个request
			SendMessageToWX.Req req = new SendMessageToWX.Req();
			req.transaction = buildTransaction("webpage");
			req.message = msg;
//		req.scene = SendMessageToWX.Req.WXSceneSession;//消息发送至微信的会话内
			req.scene = SendMessageToWX.Req.WXSceneTimeline;//消息发送至朋友圈
			api.sendReq(req);
			
	}

	// 发送响应到微信
    public void sendRespToWx(String text) {
    	
		WXTextObject textObj = new WXTextObject();
		textObj.text = text;
		
		WXMediaMessage msg = new WXMediaMessage();
		msg.mediaObject = textObj;
		
		GetMessageFromWX.Resp resp = new GetMessageFromWX.Resp();
		resp.transaction = new GetMessageFromWX.Req().transaction;
		resp.message = msg;
		api.sendResp(resp);
			
	}
    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 50, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
    private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
}
