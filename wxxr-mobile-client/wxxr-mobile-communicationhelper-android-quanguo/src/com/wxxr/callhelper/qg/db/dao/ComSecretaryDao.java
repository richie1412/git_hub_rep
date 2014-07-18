package com.wxxr.callhelper.qg.db.dao;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.db.ComSecretaryOpenHelper;
/**
 * 小秘书
 * 
 * @author cuizaixi
 * 
 */
public class ComSecretaryDao implements IComeHelperDao<ComSecretaryBean> {
	private static ComSecretaryDao mInstance;
	private ComSecretaryOpenHelper mOpenHelper;
	ComSecretaryDao(Context context) {
		mOpenHelper = new ComSecretaryOpenHelper(context);
	}
	public static ComSecretaryDao getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ComSecretaryDao(context);
		}
		return mInstance;

	}
	public boolean insert(ComSecretaryBean bean) {
		SQLiteDatabase database = mOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ComSecretaryOpenHelper.DELAY_TIMES, bean.getDelayTime());
		values.put(ComSecretaryOpenHelper.KEY_ROWID, bean.getId());
		values.put(ComSecretaryOpenHelper.SECRET_TYPE, bean.getSecreType());
		values.put(ComSecretaryOpenHelper.TEL_NUMBER, bean.getTelnum());
		values.put(ComSecretaryOpenHelper.ALERT_TIME, bean.getAlertTime());
		values.put(ComSecretaryOpenHelper.STATE, bean.getState());
		database.insert(ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY, null,
				values);
		database.close();
		
		return true;
	}
	public List<ComSecretaryBean> findAll() {
		List<ComSecretaryBean> beans = new ArrayList<ComSecretaryBean>();
		SQLiteDatabase database = mOpenHelper.getReadableDatabase();
		if (database.isOpen()) {
			String sql = "select * from "
					+ ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY
					+ " order by " + ComSecretaryOpenHelper.STATE + " asc, "
					+ ComSecretaryOpenHelper.ALERT_TIME + " desc";
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				ComSecretaryBean bean = new ComSecretaryBean();
				long type = cursor.getLong(cursor
						.getColumnIndex(ComSecretaryOpenHelper.SECRET_TYPE));
				String telnum = cursor.getString(cursor
						.getColumnIndex(ComSecretaryOpenHelper.TEL_NUMBER));
				long alertTime = cursor.getLong(cursor
						.getColumnIndex(ComSecretaryOpenHelper.ALERT_TIME));
				int state = cursor.getInt(cursor
						.getColumnIndex(ComSecretaryOpenHelper.STATE));
				int id = cursor.getInt(cursor
						.getColumnIndex(ComSecretaryOpenHelper.KEY_ROWID));
				bean.setSecreType(type);
				bean.setTelnum(telnum);
				bean.setAlertTime(alertTime);
				bean.setId(id);
				bean.setState(state);
				beans.add(bean);
			}
			cursor.close();
			database.close();
		}
		return beans;
	}
	public ComSecretaryBean findByID(int id) {
		ComSecretaryBean bean = new ComSecretaryBean();
		SQLiteDatabase database = mOpenHelper.getReadableDatabase();
		if (database.isOpen()) {
			String sql = "select * from "
					+ ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY + " where "
					+ ComSecretaryOpenHelper.KEY_ROWID + "=" + id
					+ " order by " + ComSecretaryOpenHelper.ALERT_TIME
					+ " desc";
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				long type = cursor.getLong(cursor
						.getColumnIndex(ComSecretaryOpenHelper.SECRET_TYPE));
				String telnum = cursor.getString(cursor
						.getColumnIndex(ComSecretaryOpenHelper.TEL_NUMBER));
				long alertTime = cursor.getLong(cursor
						.getColumnIndex(ComSecretaryOpenHelper.ALERT_TIME));
				int delayTimes = cursor.getInt(cursor
						.getColumnIndex(ComSecretaryOpenHelper.DELAY_TIMES));
				bean.setSecreType(type);
				bean.setTelnum(telnum);
				bean.setAlertTime(alertTime);
				bean.setId(id);
				bean.setDelayTime(delayTimes);
			}
			cursor.close();
			database.close();
		}
		return bean;
	}
	public void update(ComSecretaryBean bean) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ComSecretaryOpenHelper.SECRET_TYPE, bean.getSecreType());
		values.put(ComSecretaryOpenHelper.TEL_NUMBER, bean.getTelnum());
		values.put(ComSecretaryOpenHelper.STATE, bean.getState());
		values.put(ComSecretaryOpenHelper.ALERT_TIME, bean.getAlertTime());
		values.put(ComSecretaryOpenHelper.DELAY_TIMES, bean.getDelayTime());
		db.update(ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY, values,
				"_id=?", new String[]{String.valueOf(bean.getId())});
		db.close();
	}
	public void delete(int id) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.execSQL("delete from "
					+ ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY
					+ " where _id = ?", new Object[]{id});
			db.close();
		}
	}
	public int getLastID() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sqlString = "select _id from "
				+ ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY;
		Cursor cursor = db.rawQuery(sqlString, null);
		if (cursor.moveToLast()) {
			int id1 = cursor.getInt(0);
			return id1 + 1;// 返回一个加一的数字
		}
		cursor.close();
		db.close();
		return 0;

	}
	/**
	 * 返回未提醒的数目
	 * 
	 * @return
	 */
	public int getUnhappenItemCount() {
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		String sqlString = "select * from "
				+ ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY + " where "
				+ ComSecretaryOpenHelper.STATE + "=" + 0;
		Cursor cursor = db.rawQuery(sqlString, null);
		int count = 0;
		if (cursor.moveToNext()) {
			count = cursor.getCount();
		}
		cursor.close();
		db.close();
		return count;

	}
	/**
	 * 
	 * @return 查询所有未读的提醒
	 */
	public List<ComSecretaryBean> getUnhappenItems() {
		List<ComSecretaryBean> beans = new ArrayList<ComSecretaryBean>();
		SQLiteDatabase database = mOpenHelper.getReadableDatabase();
		if (database.isOpen()) {
			String sql = "select * from "
					+ ComSecretaryOpenHelper.KEY_TABLE_COMSECRETARY + " where "
					+ ComSecretaryOpenHelper.STATE + " = 0" + " order by "
					+ ComSecretaryOpenHelper.ALERT_TIME + " desc";
			Cursor cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				ComSecretaryBean bean = new ComSecretaryBean();
				long type = cursor.getLong(cursor
						.getColumnIndex(ComSecretaryOpenHelper.SECRET_TYPE));
				String telnum = cursor.getString(cursor
						.getColumnIndex(ComSecretaryOpenHelper.TEL_NUMBER));
				long alertTime = cursor.getLong(cursor
						.getColumnIndex(ComSecretaryOpenHelper.ALERT_TIME));
				int state = cursor.getInt(cursor
						.getColumnIndex(ComSecretaryOpenHelper.STATE));
				int id = cursor.getInt(cursor
						.getColumnIndex(ComSecretaryOpenHelper.KEY_ROWID));
				bean.setSecreType(type);
				bean.setTelnum(telnum);
				bean.setAlertTime(alertTime);
				bean.setId(id);
				bean.setState(state);
				beans.add(bean);
			}
			cursor.close();
			database.close();
		}
		return beans;
	}
}
