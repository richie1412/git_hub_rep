package com.wxxr.callhelper.qg.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.wxxr.callhelper.qg.constant.Constant;

import android.content.Context;

public class AppConfig {

	public static void setMagnolia(Context context){
		try {
			InputStream is = context.getAssets().open("server.properties");
			Properties mProps = new Properties();
			mProps.load(is);
			Constant.MAGNOLIA_URL = mProps.getProperty("magnolia").trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setBuildNumber(Context context){
		try {
			InputStream is = context.getAssets().open("buildnumber.properties");
			Properties mProps = new Properties();
			mProps.load(is);
			Constant.BUILDTIME = mProps.getProperty("buildtime").trim();
			Constant.TARGET = mProps.getProperty("target").trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
