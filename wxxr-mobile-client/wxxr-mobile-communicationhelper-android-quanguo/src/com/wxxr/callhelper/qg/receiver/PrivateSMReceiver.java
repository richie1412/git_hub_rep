/**
 * 
 */
package com.wxxr.callhelper.qg.receiver;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.mobile.core.api.ApplicationFactory;
import com.wxxr.mobile.core.api.IApplication;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * @author neillin
 *
 */
public class PrivateSMReceiver extends BroadcastReceiver {
	private static final Trace log = Trace.register(PrivateSMReceiver.class);
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onReceive(Context context, Intent intent) {
		
		IApplication framework = ApplicationFactory.getInstance().getApplication();
		IPrivateSMService service = (IPrivateSMService)framework.getService(IPrivateSMService.class);
		if(service == null){
			return;
		}
		List<String> privateNumbers = service.getAllPrivateNumber();
		if((privateNumbers == null)||privateNumbers.isEmpty()){
			return;
		}
		Bundle bundle = intent.getExtras();
		if (bundle != null)
		{
			if(log.isDebugEnabled()){
				log.debug("Receiving SM message ...");
			}
			Object[] mypdus = (Object[]) bundle.get("pdus");
			SmsMessage[] messages = new SmsMessage[mypdus.length];
			String body = "";
			for (int i = 0; i < mypdus.length; i++)
			{
				messages[i] = SmsMessage.createFromPdu((byte[]) mypdus[i]);
			}
			if(log.isDebugEnabled()){
				log.debug("Received SM message :",messages[0]);
			}
			String number = messages[0].getDisplayOriginatingAddress();
			String mo = number;
			if (number.startsWith("+86"))
			{
				number = number.substring(3);
			}
			if(number.startsWith("12520")){
				number = number.substring(5);
			}
			if(!privateNumbers.contains(number)){
				return;
			}
			for (SmsMessage mess : messages)
			{

				String smscontent = mess.getDisplayMessageBody();
				body += smscontent;
			}
			TextMessageBean msg = new TextMessageBean();
			msg.setNumber(number);
			msg.setContent(body);
			msg.setMo(mo);
			msg.setTimestamp(System.currentTimeMillis());
			msg.setReadtext("no");
			if(log.isDebugEnabled()){
				log.debug("Received SM :", msg);
			}
			try {
				service.onSMReceived(msg);
				abortBroadcast();
			}catch(Throwable t){
				log.warn("Caught exception when processing incoming SM :"+msg, t);
			}
		}
	}

}
