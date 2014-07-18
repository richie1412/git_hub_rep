package com.wxxr.callhelper.qg.db.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxxr.callhelper.qg.db.NoticeOpenHelper;
import com.wxxr.phone.helper.bo.NotifyMessage;

public class NoticeDao {
	private static NoticeDao csi = null;
	private NoticeOpenHelper dbOpenHelper;

	private NoticeDao(Context context) {
		dbOpenHelper = new NoticeOpenHelper(context);
	}

	public static NoticeDao getInstance(Context context) {
		if (csi == null) {
			synchronized (NoticeDao.class) {
				if (csi == null) {
					csi = new NoticeDao(context);
				}
			}
		}                                
		return csi;
	}

	public synchronized void inserData(NotifyMessage[] data,String provincecode) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		// title TEXT, content TEXT ,starttime LONG,endtime LONG,type BYTE"
		// + "isActive BYTE, tourl TEXT, showtime BYTE ,forwardType BYTE
		// ,noticeid LONG

		for (NotifyMessage notifyMessage : data) {
			values.clear();
			values.put("title", notifyMessage.getTitle());
			values.put("content", notifyMessage.getContent());
			values.put("starttime", notifyMessage.getStartTime().getTime());
			values.put("endtime", notifyMessage.getEndTime().getTime());
			values.put("type", notifyMessage.getType());
			values.put("isActive", notifyMessage.getIsActive());
			values.put("tourl", notifyMessage.getToURL());
			values.put("showtime", notifyMessage.getShowTime());
			values.put("forwardType", notifyMessage.getForwardType());
			values.put("noticeid", notifyMessage.getId());
			values.put("provincecode", provincecode);
			if (!db.isOpen()) {
				db = dbOpenHelper.getWritableDatabase();
			}

			if (isUpdate(notifyMessage.getId(),provincecode, db)) {
				db.update("tablenotice", values,
						" noticeid = " + notifyMessage.getId() +" provincecode ="+provincecode, null);
			} else {
				values.put("showcount", 0);
				db.insert("tablenotice", null, values);
			}

		}
		db.close();

	}

	private boolean isUpdate(long id,String provincecode, SQLiteDatabase db) {
		Cursor cur = db.rawQuery("select * from tablenotice where noticeid = '"
				+ id+"'  and provincecode = '"+provincecode+"'"
				, null);
		boolean resule = cur.moveToFirst();
	//	cur.close();
		return resule;
	}
	
	
	
	public void setColseByUser(long id,String provincecode) {
		
		try{		
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
	    db.execSQL("update tablenotice set showcount = 1 where noticeid = '"+id+"' and provincecode = '"+provincecode+"'");
		}catch(Exception ee){
			
		}
	}
	
	
	
	
	public   synchronized  NotifyMessage   gefetchHomeNoticeDataByType( String provincecode) {
		long time=System.currentTimeMillis();
		NotifyMessage bean = new NotifyMessage();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("select * from tablenotice where type = 1 and showcount = 0 and provincecode ='" +
				provincecode+"' and starttime < "
				+time + " and  endtime > "+ time , null);
		if (cur.moveToFirst()) {
			toBean(cur, bean);
		}
		cur.close();
		return bean;
	}

	
	
	public   synchronized  NotifyMessage   getTitleNotifyMessDataByType(String provincecode ) {
		long time=System.currentTimeMillis();
		NotifyMessage bean = new NotifyMessage();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("select * from tablenotice where type = 2 and showcount = 0 and provincecode = '"
		+provincecode+"' and starttime < "
				+time + " and  endtime > "+ time, null);
		if (cur.moveToFirst()) {
			toBean(cur, bean);
		}
		cur.close();
		return bean;
	}

	
	
	

//	public  synchronized NotifyMessage fetchDataByType(int tpye) {
//		NotifyMessage bean = new NotifyMessage();
//		long now =System.currentTimeMillis();
//		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//		Cursor cur = db.rawQuery("select * from tablenotice where type = "
//				+ tpye  "", null);
//		if (cur.moveToFirst()) {
//			toBean(cur, bean);
//		}
//		cur.close();
//		return bean;
//	}

	private void toBean(Cursor cur, NotifyMessage notifyMessage) {
		notifyMessage.setTitle(cur.getString(cur.getColumnIndex("title")));
		notifyMessage.setContent(cur.getString(cur.getColumnIndex("content")));
		notifyMessage.setStartTime(new Date(cur.getLong((cur
				.getColumnIndex("starttime")))));
		notifyMessage.setEndTime(new Date(cur.getLong(cur
				.getColumnIndex("endtime"))));
		notifyMessage.setType(cur.getInt(cur.getColumnIndex("type")));
		notifyMessage.setIsActive((byte) cur.getShort(cur
				.getColumnIndex("isActive")));
		notifyMessage.setToURL(cur.getString(cur.getColumnIndex("tourl")));
		notifyMessage.setShowTime((byte) cur.getShort(cur
				.getColumnIndex("showtime")));
		notifyMessage.setForwardType(cur.getInt(cur
				.getColumnIndex("forwardType")));
		;
		notifyMessage.setId(cur.getLong(cur.getColumnIndex("noticeid")));

	}

	public List<Long> fetchAllData(String key) { 
		List<Long> ids= new ArrayList<Long>();
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("select * from tablenotice  where provincecode ='"+key+"'", null);
		if (cur.moveToFirst()) {
			do{
		     ids.add(cur.getLong(cur.getColumnIndex("noticeid")));
			}while(cur.moveToNext());
		}
		cur.close();	
		return ids;
	}

	public NotifyMessage fetchByID(Long msgId,String provincecode) {
		NotifyMessage bean=null;
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		Cursor cur = db.rawQuery("select * from tablenotice  where  noticeid = '"+msgId+"' and provincecode = '"+provincecode+"'", null);
		if (cur.moveToFirst()) {
			bean=new NotifyMessage();
			toBean(cur, bean);
		}
		cur.close();	
		return bean;
	}

}
