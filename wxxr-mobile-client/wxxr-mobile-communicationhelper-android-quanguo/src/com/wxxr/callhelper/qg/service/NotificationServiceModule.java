/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.INotificationService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.ui.HomeActivity;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.event.api.IListenerChain;
import com.wxxr.mobile.core.event.api.IStreamEvent;
import com.wxxr.mobile.core.event.api.IStreamEventListener;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * @author neillin
 *
 */
public class NotificationServiceModule extends AbstractModule<ComHelperAppContext> implements INotificationService{

	private IStreamEventListener listener;
	private Map<Class<?>, Integer> notificationStyles = new ConcurrentHashMap<Class<?>, Integer>();
	
	@Override
	protected void initServiceDependency() {
		addRequiredService(IEventRouter.class);
	}
	private void doNotification(int style) {
		Application app = context.getApplication().getAndroidApplication();
		NotificationManager nm = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);

		//新建状态栏通知 
		Notification baseNF=new Notification(); 
		//设置通知在状态栏显示的图标 
		baseNF.icon=R.drawable.gd_img_text_dialog_icon; 
		//通知时在状态栏显示的内容 
		baseNF.tickerText="您有新的信息，请查看!"; 
		//通知的默认参数DEFAULT_SOUND,DEFAULT_VIBRATE,DEFAULT_LIGHTS. 
		//如果要全部采用默认值,用DEFAULT_ALL. 
		//此处采用默认声音 
		baseNF.defaults=Notification.DEFAULT_SOUND; 
		baseNF.flags=Notification.FLAG_AUTO_CANCEL; 
		PendingIntent contentIntent = PendingIntent.getActivity(app.getApplicationContext(), Constant.SIMI_NEWSMS_ID, new Intent(app.getApplicationContext(), HomeActivity.class), 0);//
		baseNF.setLatestEventInfo(app.getApplicationContext(),"通信助手","您有新信息",contentIntent); 
		//发出状态栏通知 
		//ThefirstparameteristheuniqueIDfortheNotification 
		//andthesecondistheNotificationobject. 
		nm.notify(Constant.SIMI_NEWSMS_ID,baseNF); 

	}
//	private void doNotification(int style) {
//		Application app = context.getApplication().getAndroidApplication();
//		NotificationManager notificationManager = (NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);
//
//		//Define sound URI
//		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(app.getApplicationContext())
//		        .setContentText("私密消息")
//		        .setSound(soundUri); //This sets the sound to play
//
//		//Display notification
//		notificationManager.notify(0, mBuilder.build());	
//	}

	@Override
	protected void startService() {
		this.listener = new IStreamEventListener() {
			
			@Override
			public void onEvent(IStreamEvent event, IListenerChain chain) {
				Integer style = notificationStyles.get(event.getClass());
				if(style != null){
					doNotification(style);
				}
				
			}
		};
		getService(IEventRouter.class).addListenerLast(listener);
		context.registerService(INotificationService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(INotificationService.class, this);
		if((this.listener != null)&&(getService(IEventRouter.class) != null)){
			getService(IEventRouter.class).removeListener(listener);
			this.listener = null;
		}
		
	}
	
	public void registerNotificationEvent(Class<? extends IStreamEvent> clazz, int style){
		this.notificationStyles.put(clazz, style);
	}
	
	public void unregisterNotificationEvent(Class<? extends IStreamEvent> clazz){
		this.notificationStyles.remove(clazz);
	}


}
