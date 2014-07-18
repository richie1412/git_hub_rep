package com.wxxr.callhelper.qg.receiver;

import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.SmsMessage.MessageClass;

import com.wxxr.callhelper.qg.intercept.rule.ISmsHandler;
import com.wxxr.callhelper.qg.service.ILouHuaHuizhiService;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 拦截漏接电话及短信回执的短信
 * 
 * @author zhengjincheng
 * 
 */
public class InterceptSMSReceiverNew extends BroadcastReceiver {
	private static final Trace log = Trace.register(InterceptSMSReceiverNew.class);
	
	private ILouHuaHuizhiService service;
	public ILouHuaHuizhiService getLouHuaHuizhiService(Context context){
		if (service==null){
			if (context instanceof Service)
			{
				Service a = (Service) context;
				service = ((ApplicationManager) a.getApplication()).getFramework().getService(ILouHuaHuizhiService.class);
				return service;
			}
		}
		return service;
	}
	public void onReceive(final Context context, Intent intent) {
		Bundle bun = intent.getExtras();
		String number = null;// 接收到的短信号码
		String body = "";// 接收到的短信内容
		boolean dontstore = false;
		if (bun != null) {
			Object[] mypdus = (Object[]) bun.get("pdus");
			SmsMessage[] messages = new SmsMessage[mypdus.length];
			for (int i = 0; i < mypdus.length; i++) {
				messages[i] = SmsMessage.createFromPdu((byte[]) mypdus[i]);
			}
			number = messages[0].getDisplayOriginatingAddress();
			dontstore = (messages[0].getMessageClass() == MessageClass.CLASS_0);
			if (number.contains("+86")) {
				number = number.substring(3);
			}
			for (SmsMessage mess : messages) {

				String smscontent = mess.getDisplayMessageBody();
				body += smscontent;
			}
			if(log.isDebugEnabled()){
				log.debug("Received SM :["+body+"], from :["+number+"], dontstore :"+dontstore);
			}
			boolean intercepted=false;
			List<ISmsHandler> l=getLouHuaHuizhiService(context).getSmsHandlers();
			if  (l!=null && !l.isEmpty()){
				
				for (ISmsHandler handler :l){
					if (handler.handle(context, body, number,dontstore)){
						intercepted=true;
						break;
					}
				}
				
			}
			if (intercepted){
				abortBroadcast();
			}
		}
	}
}