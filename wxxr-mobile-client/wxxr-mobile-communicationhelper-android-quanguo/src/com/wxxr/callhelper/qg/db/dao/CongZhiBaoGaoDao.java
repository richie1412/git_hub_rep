package com.wxxr.callhelper.qg.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.wxxr.callhelper.qg.bean.BodyBeanCongzhi;
import com.wxxr.callhelper.qg.bean.BodyBeanHuiZhi;
import com.wxxr.callhelper.qg.db.CongZhiOpenHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CongZhiBaoGaoDao {
	CongZhiOpenHelper dbOpenHelper;

	private static CongZhiBaoGaoDao csi = null;

	private CongZhiBaoGaoDao(Context context) {
		dbOpenHelper = new CongZhiOpenHelper(context);
	}

	public static CongZhiBaoGaoDao getInstance(Context context) {
		if (csi == null) {
			synchronized (CongZhiBaoGaoDao.class) {
				if (csi == null) {
					csi = new CongZhiBaoGaoDao(context);
				}
			}
		}
		return csi;
	}
	public boolean insert(BodyBeanCongzhi body) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("address", body.address);
		values.put("smstime", body.cdate);
		values.put("smscontent", body.content);
		values.put("month", body.month);
		values.put("type", body.type);
		db.insert("tablecongzhi", null, values);
		db.close();
		return true;
	}
	public List<BodyBeanCongzhi> findAll() {
		ArrayList<BodyBeanCongzhi> bodys = new ArrayList<BodyBeanCongzhi>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db
					.rawQuery(
							"select * from tablecongzhi where type in (1,2,3,4) order by smstime desc",
							null);
			while (cursor.moveToNext()) {
				BodyBeanCongzhi body = new BodyBeanCongzhi();
				String number = cursor.getString(cursor
						.getColumnIndex("address"));
				String content = cursor.getString(cursor
						.getColumnIndex("smscontent"));
				long date = cursor.getLong(cursor.getColumnIndex("smstime"));
				int smsstyle = cursor.getInt(cursor.getColumnIndex("type"));
				String amonth = cursor
						.getString(cursor.getColumnIndex("month"));
				body.type = smsstyle;
				body.content = content;
				body.cdate = date;
				body.address = number;
				body.month = amonth;
				bodys.add(body);
			}
			cursor.close();
			db.close();
		}
		return bodys;
	}
	public void deleteSingleSms(long  date) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from tablecongzhi where smstime = ?",
					new Object[]{date});
			db.close();
		}
	}
}
