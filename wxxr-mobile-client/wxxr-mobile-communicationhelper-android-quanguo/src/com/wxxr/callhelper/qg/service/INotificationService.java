package com.wxxr.callhelper.qg.service;

/**
 * @author xijiadeng
 * */
public interface INotificationService {
	void setNotification(String tickerText, String message, int icon, boolean soundFlag, Class<?> cls);
	void setNotification(String tickerText, String message, boolean soundFlag, Class<?> cls);
	void setNotification(int icon, String message, boolean soundFlag, Class<?> cls);
	void cancelNotification();
}
