/*
 * @(#)ApplicationManager.java	 2011-12-16
 *
 * Copyright 2004-2011 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.ui;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Level;

import android.app.Application;
import android.content.Intent;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ComHelperFramework;
import com.wxxr.callhelper.qg.bean.DeleteVO;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.event.ApplicationExittingEvent;
import com.wxxr.callhelper.qg.service.LanJieSmsService;
import com.wxxr.callhelper.qg.service.NewCallRecordService;
import com.wxxr.callhelper.qg.service.PrivateSMInterceptionService;
import com.wxxr.callhelper.qg.service.SMSOutgoingMonitorService;
import com.wxxr.callhelper.qg.utils.AppConfig;
import com.wxxr.mobile.android.log.Log4jConfigurator;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.log.spi.ILoggerFactory;
import com.wxxr.mobile.core.log.spi.Log4jLoggerFactory;

public class ApplicationManager extends Application
{
	public static ApplicationManager instance;
	private Log4jConfigurator logConfig = new Log4jConfigurator();
	private ComHelperFramework application;
	public ArrayList<DeleteVO> dls ;
	@Override
	public void onCreate()
	{
		ILoggerFactory.DefaultFactory.setLoggerFactory(new Log4jLoggerFactory());
		logConfig.configure();
		super.onCreate();
		this.application = new ComHelperFramework(this);
		if(this.application.isInDebugMode()){
			logConfig.configureLogCatAppender("/",Level.INFO);
			logConfig.configureLogCatAppender("com.wxxr.callhelper.ui",Level.DEBUG);
			logConfig.configureLogCatAppender("com.wxxr.callhelper.receiver",Level.DEBUG);
//			logConfig.configureLogCatAppender("com.wxxr.callhelper.widget",Level.DEBUG);
//			logConfig.configureLogCatAppender("com.wxxr.mobile.core.rpc.http",Level.DEBUG);
//			logConfig.configureLogCatAppender("com.wxxr.mobile.web.grabber",Level.DEBUG);
		}else{
			logConfig.configureFileAppender("/",Level.WARN);
			logConfig.configureLogCatAppender("/", Level.WARN);
		}
		this.application.start();
		
		//设置magnolia
		AppConfig.setMagnolia(this);
		
		Intent in = new Intent(this, LanJieSmsService.class);
		startService(in);
		startService(new Intent(this,PrivateSMInterceptionService.class));
		startService(new Intent(this,SMSOutgoingMonitorService.class));
		//开启录音服务
		startService(new Intent(this, NewCallRecordService.class));
		
		//开启归属地服务
	//	startService(new Intent(this, NewLocationPhoneService.class));
		

//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);
		

		createLocationDB();
	}

	private void createLocationDB() {
		File file = new File(Constant.SDPATH_LOCATION);
		if(!file.exists()){
			file.mkdirs();
		}
		
		String path = Constant.SDPATH_LOCATION + "/" + Constant.DB_NAME;
		
		if(!(new File(path).exists())){
			
			byte[] buffer = new byte[1024];
			int readCount = 0;

			try {
				InputStream is = getResources().openRawResource(R.raw.phone_range_zip);
				ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(is));
				
				zipInputStream.getNextEntry();   
				OutputStream os = new FileOutputStream(path);
				BufferedInputStream b = new BufferedInputStream(zipInputStream);
				
				while ((readCount = b.read(buffer)) != -1) {
					os.write(buffer, 0, readCount);
				}
				os.flush();
				os.close();
			} catch (Throwable e) {
				Trace.getTrace(ApplicationManager.class).error("Failed to initialize phone range db at :"+path, e);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		getFramework().getService(IEventRouter.class).routeEvent(new ApplicationExittingEvent());
		if(this.application != null){
			this.application.stop();
			this.application = null;
		}
		super.onTerminate();
	}
	
	public ComHelperFramework getFramework() {
		return this.application;
	}
	
}
