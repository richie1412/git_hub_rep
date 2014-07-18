package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;	
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.ILouHuaHuizhiService;
import com.wxxr.callhelper.qg.utils.SMSFilter;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

public class ImprorDataProcessDialog  extends BaseActivity {
    private static final Trace log = Trace.register(ImprorDataProcessDialog.class);
 //   com.wxxr.callhelper.widget.MyProcesBar processbar;
    SeekBar processbar;
    TextView processtip;
    ArrayList<String> addperson;
    private Context mcontext;
    private int requestcode;
    private String tip;
    Handler hanler=new Handler(){
    	public void handleMessage(android.os.Message msg) {    
    		if(processbar.getMax()!=0){
    		processtip.setText(tip + (processbar.getProgress()*100/processbar.getMax())+"%");
    		}
    	};
    };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // TODO Auto-generated method stub
         
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.import_sysmes_processbar);
       requestcode=getIntent().getIntExtra(Constant.REQUEST_CODE, -1);
    
       processbar =(SeekBar)findViewById(R.id.my_progressbar1);
    
       processtip = (TextView)findViewById(R.id.my_progressbar_tip);
       
       addperson=getIntent().getStringArrayListExtra(Constant.IMPORT_OR_DEL_NUMS);
       mcontext=this.getBaseContext();
       if(getIntent().getStringExtra(Constant.IS_IMPORT_NUMS).length()>0){
    	   tip="正在导入联系人";
        new ImportSysmes().execute( );
       }else{
    	   tip="正在导出联系人";      
    	   findViewById(R.id.gd_iv_titlebar_icon).setBackgroundResource(R.drawable.gd_dialog_private_contacts_icon_out);
           new ExportToSysmes().execute();
       }
    }
    
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    //	super.onBackPressed();
    }
    
    
    @Override
    protected void onResume() {
       // TODO Auto-generated method stub
       super.onResume();
    }
    
    
    class ImportSysmes extends AsyncTask<Context, Void, Void> {

       @Override
       protected void onPreExecute() {
           // TODO Auto-generated method stub
           super.onPreExecute();
       

       }

       @Override
       protected void onPostExecute(Void result) {      
           super.onPostExecute(result);
            finish();
       }

       @Override
       protected Void doInBackground(Context... params) {
           int size = addperson.size();
           int total = 0;
           // 计算信息总数
           for (int i = 0; i < size; i++) {
              total = +Tools.getSmsOfPhoneNum(mcontext, null,
                     addperson.get(i));
           }

           processbar.setMax(total);
           processbar.setProgress(1);
           // 开始导入，不要用列表，避免内存不足
           IPrivateSMService service = getService(IPrivateSMService.class);
           boolean needbell=service.isRingBellWhenReceiving();
           service.setRingBellWhenReceiving(false);
           int curvalue = 0;
           for (int i = 0; i < size; i++) {
              String phonenum = addperson.get(i);
              if(phonenum.length()>10){
      			phonenum=phonenum.substring(phonenum.length()-11, phonenum.length());
      		}
              Cursor c = mcontext
                     .getApplicationContext()
                     .getContentResolver()
                     .query(Sms.CONTENT_URI, null,
                            Sms.ADDRESS + " like '%" + phonenum+"%'", null, null);
              if (c.moveToFirst()) {
                  if(log.isDebugEnabled()){
                  log.debug("find sms of ==========", phonenum);
                  }
                  String threadid = c.getString(c
                         .getColumnIndex(Sms.THREAD_ID));
                  
                  Cursor cursor = mcontext
                         .getApplicationContext()
                         .getContentResolver()
                         .query(Sms.CONTENT_URI, null,
                                Sms.THREAD_ID + " = " + threadid, null,
                                null);
                  ContentResolver contentResolver=    mcontext.getApplicationContext().getContentResolver();
                  StringBuffer sb=new  StringBuffer();
                  if (cursor.moveToFirst()) {
                     if(log.isDebugEnabled()){
                         log.debug("ImprorDataProcessDialog cont==", cursor.getCount());
                     }
                     int bodyindex = cursor.getColumnIndex(Sms.BODY);
                     int dateindex = cursor.getColumnIndex(Sms.DATE);
                     int typeindex = cursor.getColumnIndex(Sms.TYPE);
                     int numindex = cursor.getColumnIndex(Sms.ADDRESS);
                     int idindex=  cursor.getColumnIndex(Sms._ID);
                  
                     do {
                         TextMessageBean bean = new TextMessageBean();
                         String strnum = cursor.getString(numindex);
                         String body=cursor.getString(bodyindex);
                         //回执和漏电短信都不用要导入
                         if(SMSFilter.isSmsFailHuizhi(body, strnum)||SMSFilter.isSmsOKHuizhi(body, strnum)
                                ||getService(ILouHuaHuizhiService.class).isMatch(body, strnum)){
                            
                         }else{   
                        	 //去除前缀，只保留后边的11位
                        	   if(strnum.endsWith(phonenum)){
                        		   strnum=phonenum;
                        	   }
                         int type = cursor.getInt(typeindex);
                         if (type == 1) {                       
                            bean.setMo(strnum);
                         } else if (type == 2) {                       
                            bean.setMt(strnum);
                         }

                         bean.setNumber(strnum);
                         bean.setTimestamp(cursor.getLong(dateindex));
                         bean.setContent(body);
                         service.onSMReceived(bean,false,true);     
                         sb.append(cursor.getString(idindex)).append(",");
                         }
                         curvalue++;
                         processbar.setProgress(curvalue);
                         processbar.setMax(total);                    
                         hanler.sendEmptyMessage(0);
                         if(log.isDebugEnabled()){
                            log.debug("ImprorDataProcessDialog text==", cursor.getString(bodyindex));
                         }
                     } while (cursor.moveToNext());
                     cursor.close();
                     if(sb.toString().length()>0){
                    	 sb.deleteCharAt(sb.length()-1);
                    	 String ids=sb.toString();
                         contentResolver.delete(Sms.CONTENT_URI, Sms._ID +" in ("+ids+")", null);
                     }
                  }

              }else{
                  if(log.isDebugEnabled()){
                     log.debug("not find sms of $$$$$$$$ ", phonenum);
                     }
              }
              c.close();    
              service.setRingBellWhenReceiving(needbell);
             setResult(requestcode);
           }

           return null;
       }

    }
    
    
    
    class ExportToSysmes extends AsyncTask<Context, Void, Void> {

       @Override
       protected void onPreExecute() {
           // TODO Auto-generated method stub
           super.onPreExecute();
       

       }

       @Override
       protected void onPostExecute(Void result) {
       showMessageBox("成功导出联系人信息");
       super.onPostExecute(result);
       finish();
       }
       @Override
       protected Void doInBackground(Context... params) {
           int size = addperson.size();
           int total = 0;
           // 计算信息总数
           for (int i = 0; i<size; i++) {
              List<TextMessageBean> list = getService(
                     IPrivateSMService.class).getAllMessageOf(addperson.get(i));
              if(list!=null){
              total +=list.size();
              list=null;
              }
           }

           processbar.setMax(total);
           processbar.setProgress(1);
           int curcount=0;
           for(int j=0;j<size;j++){
           String num=addperson.get(j);
                  List<TextMessageBean> list = getService(
                         IPrivateSMService.class).getAllMessageOf(num);
                  if (list != null) {
                     Tools.remove_to_sysbox.put(num, "");
                     int leng=list.size();
                     for(int k=0;k<leng;k++){
                         Tools.inserToSysmsgOne(list.get(k),mcontext);
                         curcount++;
                         hanler.sendEmptyMessage(0);
                         processbar.setProgress(curcount);
                     }
                     
                     getService(IPrivateSMService.class).deleteMessage(
                            num, j == size - 1);
                  }
                  getService(IPrivateSMService.class)
                  .removePrivateNumber(num);
              }
           return null;
       }

    }
    
}



