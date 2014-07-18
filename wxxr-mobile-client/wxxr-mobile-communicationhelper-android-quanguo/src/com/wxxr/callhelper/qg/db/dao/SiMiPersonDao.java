package com.wxxr.callhelper.qg.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.SiMiVO;
import com.wxxr.callhelper.qg.db.SMSLouHuaOpenHelper;
import com.wxxr.callhelper.qg.db.SiMiContactsOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SiMiPersonDao
{
	SiMiContactsOpenHelper dbOpenHelper;

	private static SiMiPersonDao csi = null;

	private SiMiPersonDao(Context context)
	{

		dbOpenHelper = new SiMiContactsOpenHelper(context);
	}

	public static SiMiPersonDao getInstance(Context context)
	{
		if (csi == null)
		{
			synchronized (SiMiPersonDao.class)
			{
				if (csi == null)
				{
					csi = new SiMiPersonDao(context);
				}
			}
		}
		return csi;
	}

	// public Integer mstyle;// 短信类型 收 发
	// public String address;// 短信号码
	// public String content;// 短信内容
	// public long cdate;// 短信时间
	// public Integer islock;// 是否解锁

	public boolean insert(SiMiVO body)
	{

		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("address", body.address);
		values.put("cdate", body.cdate);
		values.put("content", body.content);
		values.put("mstyle", body.mstyle);
		values.put("islock", body.islock);
		db.insert("table_simiperson", null, values); // insert into person
														// (name)
		db.close();
		return true;
	}

	public boolean isExistPerson(String telnumber)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		String sql = "select * from table_simiperson where address = '" + telnumber + "'";
		// db.open();
		Cursor cursor = db.rawQuery(sql, null);
		boolean b = false;
		if (cursor.getCount() > 0)
			b = true;
		if (!cursor.isClosed())
		{
			cursor.close();
		}
		return b;
	}

	public int findSiMiPersonCount(String number)
	{
		int num = 0;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("select * from table_simiperson where address = ? ", new String[]
			{ number });

			if (cursor != null && cursor.getCount() > 0)
			{
				num = cursor.getCount();

				cursor.close();
				db.close();
			}

		}
		return num;

	}

	//解锁
	public boolean updateSomeBodytolock(String telnumber)
	{

		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update table_simiperson set islock = 1 where address = ? ",

		new Object[]
		{  telnumber });

		db.close();
		return true;
	}
	
//	public boolean updateSomeBodytounlock(String telnumber)
//	{
//
//		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//		db.execSQL("update table_simiperson set islock = 0 where address = ? ",
//
//		new Object[]
//		{  telnumber });
//
//		db.close();
//		return true;
//	}
	// public void deleteSomeSMS(String content)
	// {
	// SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
	// if (db.isOpen())
	// {
	// db.execSQL("delete from tablelouhua where smscontent = ?", new Object[]
	// { content });
	// db.close();
	// }
	// }

	public void deleteSMSAccordingtoNumber(String telnumber)
	{
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen())
		{
			// sql = '" +telnumber + "'
			db.execSQL("delete from table_simiperson where address = ? ", new Object[]
			{ telnumber });

			db.close();
		}
	}

	
	public void deleteSomeSMS(String content)
	{
		
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen())
		{
			// sql = '" +telnumber + "'
			db.execSQL("delete from table_simiperson where content = ? ", new Object[]
			{ content });

			db.close();
		}
		
		
		
		
	}
	
	
	
	public ArrayList<SiMiVO> queryAllPerson()
	{
		ArrayList<SiMiVO> bodys = new ArrayList<SiMiVO>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("select * from table_simiperson  order by cdate desc", null);
			while (cursor.moveToNext())
			{
				SiMiVO bb = new SiMiVO();
				String address = cursor.getString(cursor.getColumnIndex("address"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				long cdate = cursor.getLong(cursor.getColumnIndex("cdate"));
				int mstyle = cursor.getInt(cursor.getColumnIndex("mstyle"));
				int islock = cursor.getInt(cursor.getColumnIndex("islock"));
				bb.address = address;
				bb.content = content;
				bb.cdate = cdate;
				bb.mstyle = mstyle;
				bb.islock = islock;
				if (islock != 1)// 假如私密人 解锁 不取出数据
				{
					bodys.add(bb);
				}
			}
			cursor.close();
			db.close();
		}
		return bodys;
	}

	public ArrayList<BodyBean> queryAllLouHua_Send()
	{
		ArrayList<BodyBean> bodys = new ArrayList<BodyBean>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("select * from tablelouhua where smsstyle = 1 order by smstime desc", null);
			while (cursor.moveToNext())
			{

				BodyBean bb = new BodyBean();
				String number = cursor.getString(cursor.getColumnIndex("smsnumber"));
				String content = cursor.getString(cursor.getColumnIndex("smscontent"));
				long date = cursor.getLong(cursor.getColumnIndex("smstime"));
				int smsstyle = cursor.getInt(cursor.getColumnIndex("smsstyle"));
				String amonth = cursor.getString(cursor.getColumnIndex("yuefen"));
				bb.mstyle = smsstyle;
				bb.content = content;
				bb.cdate = date;
				bb.address = number;
				bb.amonth = amonth;
				bodys.add(bb);

			}
			cursor.close();
			db.close();
		}
		return bodys;
	}

	public BodyBean queryMostNewLouHua()
	{
		// ArrayList<BodyBean> bodys = new ArrayList<BodyBean>();
		BodyBean bb = null;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen())
		{
			Cursor cursor = db.rawQuery("select * from tablelouhua where smsstyle = 0 order by smstime desc", null);
			if (cursor.moveToNext())
			{

				bb = new BodyBean();
				String number = cursor.getString(cursor.getColumnIndex("smsnumber"));
				String content = cursor.getString(cursor.getColumnIndex("smscontent"));
				long date = cursor.getLong(cursor.getColumnIndex("smstime"));
				int smsstyle = cursor.getInt(cursor.getColumnIndex("smsstyle"));
				String amonth = cursor.getString(cursor.getColumnIndex("yuefen"));
				bb.mstyle = smsstyle;
				bb.content = content;
				bb.cdate = date;
				bb.address = number;
				bb.amonth = amonth;
				// bodys.add(bb);

			}
			cursor.close();
			db.close();
		}
		return bb;
	}

	public ArrayList<SiMiVO> querySomeBodyAllSMS(String telnumber)
	{
		ArrayList<SiMiVO> bodys = new ArrayList<SiMiVO>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen())
		{
			String sql = "select * from table_simiperson where address ='" + telnumber + "'  order by cdate desc";
			// where current_user ='" +current_user +
			// "' order by datetime desc";
			Cursor cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext())
			{
				SiMiVO bb = new SiMiVO();
				String address = cursor.getString(cursor.getColumnIndex("address"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				long cdate = cursor.getLong(cursor.getColumnIndex("cdate"));
				int mstyle = cursor.getInt(cursor.getColumnIndex("mstyle"));
				int islock = cursor.getInt(cursor.getColumnIndex("islock"));
				bb.address = address;
				bb.cdate = cdate;
				bb.content = content;
				bb.mstyle = mstyle;
				bb.islock = islock;
				bodys.add(bb);

			}
			cursor.close();
			db.close();
		}
		return bodys;
	}

}
