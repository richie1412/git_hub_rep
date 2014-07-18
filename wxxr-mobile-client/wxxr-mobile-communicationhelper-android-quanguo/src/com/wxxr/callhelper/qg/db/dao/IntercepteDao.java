package com.wxxr.callhelper.qg.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxxr.callhelper.qg.bean.InterceptVO;
import com.wxxr.callhelper.qg.db.InterceptContentOpenHelper;

public class IntercepteDao
{
	InterceptContentOpenHelper dbOpenHelper;

	private static IntercepteDao csi = null;

	private IntercepteDao(Context context)
	{

		dbOpenHelper = new InterceptContentOpenHelper(context);
	}

	public static IntercepteDao getInstance(Context context)
	{
		if (csi == null)
		{
			synchronized (IntercepteDao.class)
			{
				if (csi == null)
				{
					csi = new IntercepteDao(context);
				}
			}
		}
		return csi;
	}

//	public boolean insert(InterceptVO pp)
//	{
//		try
//		{
//			SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
//			ContentValues values = new ContentValues();
//			values.put("smscontent", pp.icontent);
//			values.put("smsstyle", pp.isyle);
//			db.insert("tableinterceptecontent", null, values); // insert into person
//			db.close();
//			return true;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			return false;
//		}
//	}

	// public boolean updateSomeBodyChannel(String telnumber, PChannel pp)
	// {
	// try
	// {
	// SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
	// ContentValues values = new ContentValues();
	// values.put("pingdaoname", pp.pingdaoname);
	// values.put("needtime", pp.needtime);
	// values.put("telnumber", pp.telnumber);
	// values.put("pingdaostyle", pp.pingdaostyle);
	// values.put("sexy", pp.sexy);
	// // db.insert("tablepingdao", null, values); // insert into person
	// db.update("tablepingdao", values, "telnumber = ? and pingdaoname = ?",
	// new String[]
	// { telnumber, pp.pingdaoname });
	// db.close();
	// return true;
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// return false;
	// }
	//
	// }
//(smsstyle,telnumber,secondstyle,smscontent)
	public boolean updateSomeContent(String mcontent, int msytle,String telnumber,String huizhi_number,int secondstyle)
	{

		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		db.execSQL("update tableinterceptecontent set smscontent = ?,telnumber = ?,huizhi_number=? ,secondstyle = ? where smsstyle = ? ",

		new Object[]
		{ mcontent,telnumber,huizhi_number,secondstyle,msytle });

		db.close();
		return true;
	}

	public InterceptVO querySomeContent( int msytle)
	{
		InterceptVO inv = null;
		
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen())
		{
			Cursor cursor = null;
			try {
			cursor = db.rawQuery("select * from tableinterceptecontent where smsstyle = ?",
					new String[]
					{ String.valueOf(msytle)});
			if (cursor != null && cursor.getCount() > 0)
			{
				if (cursor.moveToNext())
				{
					String mcontent = cursor.getString(cursor.getColumnIndex("smscontent"));
					String telnumber = cursor.getString(cursor.getColumnIndex("telnumber"));
					int sss = cursor.getInt(cursor.getColumnIndex("secondstyle"));
					int mmm = cursor.getInt(cursor.getColumnIndex("smsstyle"));
					inv  = new InterceptVO();
					inv.icontent = mcontent;
					inv.secondstyle = sss;
					inv.telnumber = telnumber;
					inv.isyle = mmm;
				}
			}
			}finally{
				if(cursor != null){
					try {
						cursor.close();
					}catch(Throwable t){}
				}
			}

		}
		return inv;
	}

	
	

}
