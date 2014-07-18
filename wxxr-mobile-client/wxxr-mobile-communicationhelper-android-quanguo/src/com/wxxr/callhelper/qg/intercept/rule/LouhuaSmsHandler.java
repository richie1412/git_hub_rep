package com.wxxr.callhelper.qg.intercept.rule;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.db.dao.IntercepteDao;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.db.dao.RemindSettingDao;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.service.ISmsContentParseService;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.LouHuaRealBaoGaoActivity;

/**
 * 漏接电话
 * 
 * @author zhengjincheng
 * 
 */
public class LouhuaSmsHandler implements ISmsHandler {
    SimpleDateFormat format_month = new SimpleDateFormat("MM");
    private ISmsContentParseService service;
    private IGuiShuDiService guiShuDiService;
    private ISmsInterceptRule delegate;

    public LouhuaSmsHandler(ISmsInterceptRule delegate) {
        this.delegate = delegate;
    }

    public ISmsInterceptRule getDelegate() {
        return delegate;
    }

    public void setDelegate(ISmsInterceptRule delegate) {
        this.delegate = delegate;
    }

    public ISmsContentParseService getSmsContentParseService(Context context) {
        if (service == null) {
            if (context instanceof Service) {
                Service a = (Service) context;
                service = ((ApplicationManager) a.getApplication()).getFramework().getService(ISmsContentParseService.class);
                return service;
            }
        }
        return service;
    }

    public IGuiShuDiService getGuiShuDiService(Context context) {
        if (guiShuDiService == null) {
            if (context instanceof Service) {
                Service a = (Service) context;
                guiShuDiService = ((ApplicationManager) a.getApplication()).getFramework().getService(IGuiShuDiService.class);
                return guiShuDiService;
            }
        }
        return guiShuDiService;
    }

    /**
     * 1.判断是否是漏接电话，否返回false 2.如果是 则解析短信内容 判断已送达未送达，插入数据库 3.响铃，弹出框
     */
    @Override
    public boolean handle(final Context context, final String smsContent, final String number, final boolean dontstore) {
        if (!isMatch(smsContent, number)) {
            return false;
        }

        String parseBody = getSmsContentParseService(context).parseSmsContent(smsContent);

        LouHuaDao ldao = LouHuaDao.getInstance(context);
        BodyBean bb = new BodyBean();
        bb.address = number;
        bb.content = parseBody;
        bb.cdate = new Date().getTime();
        bb.mstyle = 0;
        String mmonth = format_month.format(bb.cdate);
        bb.amonth = mmonth;
        bb.state = 1;
        ldao.insert(bb);
        RemindSettingDao rsdao = RemindSettingDao.getInstance(context);
        int status = rsdao.queryLouHuaSetting();
        
        if(dontstore == false){
	        // 插入系统收件箱
	        ContentValues values = new ContentValues();
	        values.put("address", number);
	        values.put("type", "1");
	        if(status==2){
	        	   values.put("read", "1");// 已读
	        }else{
	        	   values.put("read", "0");// 未读
	        }
	     
	        values.put("body", smsContent);
	        values.put("date", new Date().getTime());
	        Uri uri = context.getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
        }
        Intent intent2 = new Intent();
        intent2.setAction("com.wxxr.home.refreshlouhuaorhuizhi");
        context.sendBroadcast(intent2);

      
        if (status == 2) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String currentClassName = cn.getClassName();
            sendNotifySound(context);
//            if (currentClassName.equals("com.wxxr.callhelper.ui.LouHuaRealBaoGaoActivity")) {
//                Log.e("---louhua222------::", "louhua22");
//                IntercepteDao ido = IntercepteDao.getInstance(context);
//                ido.updateSomeContent(parseBody, 1, number, "--", 1);
//                // ido.updateSomeContent(body, 1);
//                Intent intent3 = new Intent();
//                intent2.setAction("com.wxxr.sms.louhuamessage");
//                context.sendBroadcast(intent2);
//
//            } else {
                Intent in = new Intent();
                in.setClass(context, LouHuaRealBaoGaoActivity.class);
                in.putExtra("inter_number", number);
                in.putExtra("inter_body", parseBody);
                in.putExtra("infor_style", 1);
                in.putExtra("infor_month", mmonth);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(in);
//            }
        }
        return true;

    }

    /**
     * 获取当前手机号码的省份，根据省份找到相应的规则，只要满足其中的一条规则则返回ture，否则flase
     * @param smsContent
     * @param targetnumber
     * @return
     */
    private boolean isMatch(String smsContent, String targetnumber) {
        return delegate.isMatch(smsContent, targetnumber);
    }

    public void sendNotifySound(Context context) {
        Notification notifi = new Notification();
        notifi.defaults = Notification.DEFAULT_ALL;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.notify(0, notifi);
    }
}
