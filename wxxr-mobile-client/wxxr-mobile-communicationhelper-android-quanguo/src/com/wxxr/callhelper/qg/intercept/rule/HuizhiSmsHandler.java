package com.wxxr.callhelper.qg.intercept.rule;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.wxxr.callhelper.qg.bean.BodyBeanHuiZhi;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.db.dao.IntercepteDao;
import com.wxxr.callhelper.qg.db.dao.RemindSettingDao;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.service.ISmsContentParseService;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.LouHuaRealBaoGaoActivity;

/**
 * 短信回执
 * 
 * @author zhengjincheng
 * 
 */
public class HuizhiSmsHandler implements ISmsHandler {
	SimpleDateFormat format_month = new SimpleDateFormat("MM");
	private ISmsContentParseService service;
	private IGuiShuDiService guiShuDiService;
	private Pattern px = Pattern.compile("\\s*|\t|\r|\n");

	public String init(String str) {
		Matcher m = px.matcher(str);
		String after = m.replaceAll("");
		return after;
	}
	public ISmsContentParseService getSmsContentParseService(Context context) {
		if (service == null) {
			if (context instanceof Service) {
				Service a = (Service) context;
				service = ((ApplicationManager) a.getApplication())
						.getFramework().getService(
								ISmsContentParseService.class);
				return service;
			}
		}
		return service;
	}

	public IGuiShuDiService getGuiShuDiService(Context context) {
		if (guiShuDiService == null) {
			if (context instanceof Service) {
				Service a = (Service) context;
				guiShuDiService = ((ApplicationManager) a.getApplication())
						.getFramework().getService(IGuiShuDiService.class);
				return guiShuDiService;
			}
		}
		return guiShuDiService;
	}

	/**
	 * 1.判断是否是短信回执该拦截的短信，否返回false 2.如果是 则解析短信内容 判断已送达未送达，插入数据库 3.响铃，弹出框
	 */
	public boolean handle(final Context context, String smsContent,
			String targetnumber, boolean dontstore) {

		String str = init(smsContent);
		if (!isMatch(str, targetnumber)) {
			return false;
		}
		Integer mstyle = 0;
		String huizhi_number = null;
		String psmsContent = getSmsContentParseService(context)
				.parseSmsContent(smsContent);
		Region r = getGuiShuDiService(context)
				.getRegionBySmsContent(smsContent);
		if (r != null) {
			huizhi_number = r.getPhoneNumber();
		} else {
			return false;
		}
		if (isArrivedHuizhiSms(str, targetnumber)) {
			mstyle = Constant.HUIZHI_ARRIVED_MSTYLE;// 已送达
		}
		if (isNotArriveHuizhiSms(str, targetnumber)) {
			mstyle = Constant.HUIZHI_NOT_ARRIVED_MSTYLE;// 未送达
		}
		BodyBeanHuiZhi b = createBean(psmsContent, targetnumber, huizhi_number,
				mstyle);
		HuiZhiBaoGaoDao hdao = HuiZhiBaoGaoDao.getInstance(context);
		hdao.insert(b);
		
		RemindSettingDao rsdao = RemindSettingDao.getInstance(context);
		int status = rsdao.queryHuiZhiSetting();
		// 插入系统收件箱
		if (dontstore == false) {
			ContentValues values = new ContentValues();
			values.put("address", targetnumber);
			values.put("type", "1");
			 if(status==2){
	        	   values.put("read", "1");// 已读
	        }else{
	        	   values.put("read", "0");// 未读
	        }
			
			values.put("body", smsContent);
			values.put("date", new Date().getTime());
			Uri uri = context.getApplicationContext().getContentResolver()
					.insert(Uri.parse("content://sms/inbox"), values);
		}
	
		if (status == 2) {
			sendNotifySound(context);
			ActivityManager am = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
			String currentClassName = cn.getClassName();
			// if (currentClassName
			// .equals("com.wxxr.callhelper.ui.LouHuaRealBaoGaoActivity")) {
			// IntercepteDao ido = IntercepteDao.getInstance(context);
			// ido.updateSomeContent(psmsContent, 1, targetnumber,
			// huizhi_number, 2);
			// // ido.updateSomeContent(body, 1);
			// Intent intent2 = new Intent();
			// intent2.setAction("com.wxxr.sms.louhuamessage");
			// context.sendBroadcast(intent2);
			// } else {
			Intent in = new Intent();
			in.setClass(context, LouHuaRealBaoGaoActivity.class);
			in.putExtra("huizhi_number", huizhi_number);
			in.putExtra("inter_number", targetnumber);
			in.putExtra("inter_body", psmsContent);
			in.putExtra("infor_style", 2);
			in.putExtra("state", 1);
			in.putExtra("infor_month",
					format_month.format(new Date().getTime()));
			in.putExtra("inter_date", new Date().getTime());

			// in.putExtra("intercept_style", "huizhi");
			in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(in);
			// }
		}
		return true;
	}

	/**
	 * 是否是回执的拦截条件
	 * 
	 * @param smsContent
	 * @param targetnumber
	 * @return
	 */
	public boolean isMatch(String smsContent, String targetnumber) {
		return isArrivedHuizhiSms(smsContent, targetnumber)
				|| isNotArriveHuizhiSms(smsContent, targetnumber);
	}

	/**
	 * 是否
	 * 
	 * @param smsContent
	 * @param targetnumber
	 * @return
	 */
	protected boolean isArrivedHuizhiSms(String smsContent, String targetnumber) {
		if (smsContent != null && targetnumber != null)
			return (smsContent.contains("短信送达") || smsContent.contains("成功"))&& targetnumber.startsWith("10658007");
		else {
			return false;
		}
	}

	protected boolean isNotArriveHuizhiSms(String smsContent,
			String targetnumber) {
		if (smsContent != null && targetnumber != null)
			return (smsContent.contains("未收到") || smsContent.contains("暂未") || smsContent
					.contains("失败")) && targetnumber.startsWith("10658007");
		else {
			return false;
		}
	}

	public BodyBeanHuiZhi createBean(String smsContent, String targetnumber,
			String huizhi_number, Integer mstyle) {
		BodyBeanHuiZhi bb = new BodyBeanHuiZhi();
		bb.address = targetnumber;
		bb.tosomebody = huizhi_number;
		bb.content = smsContent;
		bb.cdate = new Date().getTime();
		bb.mstyle = mstyle;
		bb.amonth = format_month.format(bb.cdate);
		bb.state = 1;
		return bb;
	}

	public void sendNotifySound(Context context) {
		Notification notifi = new Notification();
		notifi.defaults = Notification.DEFAULT_ALL;
		NotificationManager nMgr = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		nMgr.notify(0, notifi);
	}
}
