package com.wxxr.callhelper.qg.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.BodyBeanHuiZhi;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.event.ImporDataOKEvent;
import com.wxxr.callhelper.qg.utils.SMSFilter;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
/**
 * 导入历史里的 短信回执和漏电提醒
 * @author yangrunfei
 *
 */
public class ImportOldDataService  extends AbstractModule<ComHelperAppContext> implements ICopyOldData {
    private static final Trace  log = Trace.register(ImportOldDataService.class);
    private boolean needimport=false;
    private boolean finishImport=true;
    @Override
    protected void initServiceDependency() {
       addRequiredService(IGuiShuDiService.class);   
       addRequiredService(ISmsContentParseService.class);      
       addRequiredService(ILouHuaHuizhiService.class);         
    }

    @Override
    protected void startService() {
       // TODO Auto-generated method stub
       context.registerService(ICopyOldData.class, this);      
    }

    @Override
    protected void stopService() {
       context.unregisterService(ICopyOldData.class, this);    
    }
    
    @Override
    public void importolddata() {
       if(needimport){
           if(log.isDebugEnabled()){
              log.debug("invoke  import data value============");
           }
           try{
       context.getExecutor().execute(new Runnable() {          
           @Override
           public void run() {
              try{
              needimport=false;
              finishImport=false;
              SimpleDateFormat format_month = new SimpleDateFormat("MM");
              String number = "";
              String body = "";
              String huizhi_number = "";
              int smstate = -1;// 0 成功的回执，1失败，-1，不是回执的短信
              long date;
              Context mcontext =context.getApplication().getAndroidApplication().getBaseContext();
                  HuiZhiBaoGaoDao   hdao = HuiZhiBaoGaoDao.getInstance(mcontext);
                  LouHuaDao  ldao = LouHuaDao.getInstance(mcontext);
                  IGuiShuDiService iguishudiservice = context
                         .getService(IGuiShuDiService.class);
                  ISmsContentParseService ismscontentparseservice = context
                         .getService(ISmsContentParseService.class);

                  // ////////////////////////处理漏接电话内容复杂，所以和短信放一起了，遍历一遍数据库////////////////////////////
                  Cursor cursorloudian = mcontext.getContentResolver().query(
                         Sms.Inbox.CONTENT_URI, null, SMSFilter.FILTER_LOUHUA_SQL,
                         null, null);
                 int sms=       0;
                 int hua=0;
                  if (cursorloudian.moveToFirst()) {
                     int numberindex = cursorloudian.getColumnIndex(Sms.ADDRESS);
                     int bodyindex = cursorloudian.getColumnIndex(Sms.BODY);
                     int dateindex = cursorloudian.getColumnIndex(Sms.DATE);

                     do {
                         number = cursorloudian.getString(numberindex);
                         body = cursorloudian.getString(bodyindex);
                         date = cursorloudian.getLong(dateindex);
                         huizhi_number = "";

                         Region r = iguishudiservice.getRegionBySmsContent(body);
                         if (r != null) {
                            huizhi_number = r.getPhoneNumber();
                         }
                         
                         if( SMSFilter.isSmsFailHuizhi(body, number)||(SMSFilter.isSmsOKHuizhi(body, number))) {
                            if(!hdao.hasRec(date)){
                            if(SMSFilter.isSmsFailHuizhi(body, number)){                          
                            smstate = 1;
                             } else if (SMSFilter.isSmsOKHuizhi(body, number)) {
                            smstate = 0;                           
                             }
                            
                            String parseBody = ismscontentparseservice
                                   .parseSmsContent(body);
                            BodyBeanHuiZhi bb = new BodyBeanHuiZhi();
                            bb.address = number;
                            if (StringUtil.isEmpty(huizhi_number)) {
                                bb.tosomebody = number;
                            } else {
                                bb.tosomebody = huizhi_number;
                            }
                            bb.content = parseBody;
                            Date day = new Date(date);
                            day.setTime(date);
                            bb.cdate = day.getTime();
                            bb.mstyle = smstate;// 未送达
                            String mmonth = format_month.format(bb.cdate);
                            bb.amonth = mmonth;
                            // System.out.println("content==" + bb.content
                            // + "bb.cdate=" + bb.cdate + "bb.mstyle"
                            // + bb.mstyle + "tosomebody" + bb.tosomebody);
                            hdao.insert(bb);
                            sms++;
                            }
                         }else   if(getService(ILouHuaHuizhiService.class).isMatch(body, number)){
                        log.debug("find   ILouHuaHuizhiService............"+body);
                        if(!ldao.hasRecord(date)){
                         String parseBody = ismscontentparseservice
                                .parseSmsContent(body);
                         BodyBean bb = new BodyBean();
                         bb.address = number;
                         bb.content = parseBody;
                         Date day = new Date(date); 
                         day.setTime(date);
                         bb.cdate = day.getTime();
                         bb.mstyle = 0;
                         String mmonth = format_month.format(bb.cdate);
                         bb.amonth = mmonth;
                         ldao.insert(bb);
                         hua++;
                         }
                         }
                         // System.out.println("@@@@@@@@@@@content==" + bb.content
                         // + "bb.cdate=" + bb.cdate + "bb.mstyle" + bb.mstyle);
                     } while (cursorloudian.moveToNext());
                  }

                  cursorloudian.close();   
                  }catch(Exception eee){                	 
                     if(log.isDebugEnabled()){
                         eee.printStackTrace();
                         log.debug(" import data value  exception eee============"+eee.toString());
                     }
                  }
              getService(IEventRouter.class).routeEvent(new ImporDataOKEvent());
              
              finishImport=true;
           }
       });
           }catch(Exception eee){
        	   finishImport=true;
              if(log.isDebugEnabled()){
                  eee.printStackTrace();
                  log.debug("import data value  exception ffffff======"+eee.toString());
              }
           }
       }else{
           if(log.isDebugEnabled()){
              log.debug("invoke  import data value  false ============");
           }
           finishImport=true;
       }
    }

    @Override
    public void setNeedImport(boolean need) {
       if(log.isDebugEnabled()){
           log.debug("set import data value============", ""+need);
       }
       needimport=need;     
    }

	@Override
	public boolean isImportFinish() {
		// TODO Auto-generated method stub
		return finishImport;
	}

}
