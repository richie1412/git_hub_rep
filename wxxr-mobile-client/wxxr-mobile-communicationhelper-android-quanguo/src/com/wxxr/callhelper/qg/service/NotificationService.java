package com.wxxr.callhelper.qg.service;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * @author xijiadeng
 * */
public class NotificationService extends AbstractModule<ComHelperAppContext> implements INotificationService{

	private static final Trace log = Trace.register(NotificationService.class);

	private Intent notiIntent = null;
	private Bundle bundle = null;
	private PendingIntent contentIntent;
	private int noti_id = 1;
	private Notification notification = null;
	private NotificationManager manager = null;
	private Application app = null;

	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {
		context.registerService(INotificationService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(INotificationService.class, this);
	}
	
	/**
	 * soundFlag = true 响铃 false 不响铃
     * @param icon          The resource id of the icon to put in the status bar.f the number is 0 or negative, 
     * 						it is not shown in the status bar.
     * @param tickerText    The text that flows by in the status bar when the notification first
     *                      activates.
     * Text to scroll across the screen when this item is added to
     * the status bar.
     */
	@Override
	public void setNotification(String tickerText, String message, int icon, boolean soundFlag, Class<?> cls) {
		app = context.getApplication().getAndroidApplication();
		if(log.isDebugEnabled()){
			log.debug("TickerText===="+tickerText+"  Message::"+message+"  Sound=="+soundFlag);
		}
		if(cls instanceof Class<?>){
			notiIntent = new Intent(app.getApplicationContext(), cls);
		}
		manager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
		bundle = new Bundle();
		notification = new Notification();
		bundle.putString("n_title", tickerText);
		bundle.putString("n_message", message);
		notiIntent.putExtras(bundle);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		contentIntent = PendingIntent.getActivity(app.getApplicationContext(), noti_id, notiIntent,PendingIntent.FLAG_CANCEL_CURRENT);
		if(soundFlag){
			notification.defaults = Notification.DEFAULT_ALL;
		}
		showNotification(tickerText,message,icon);
	}

	@Override
	public void setNotification(String tickerText, String message,boolean soundFlag, Class<?> cls) {
		setNotification(tickerText, message, 0, soundFlag, cls);
	}

	@Override
	public void setNotification(int icon, String message, boolean soundFlag, Class<?> cls) {
		setNotification(null, message, icon, soundFlag, cls);
	}
	
	private void showNotification(String tickerText, String message, int icon){
		if(tickerText != null && icon > 0){
			notification.tickerText = tickerText;
			notification.icon = icon;
		}else if(tickerText == null && icon > 0){
			notification.tickerText = "";
			notification.icon = icon;
		}else if(tickerText != null && icon == 0){
			notification.tickerText = tickerText;
			notification.icon = R.drawable.ic_launcher;
		}
		if(message != null){
			notification.setLatestEventInfo(app.getApplicationContext(), "信息内容:", message, contentIntent);
		}
		notification.when = System.currentTimeMillis();
		manager.notify(noti_id, notification);
	}

	@Override
	public void cancelNotification() {
		if(manager!=null){
			manager.cancel(noti_id);
		}
	}
}
