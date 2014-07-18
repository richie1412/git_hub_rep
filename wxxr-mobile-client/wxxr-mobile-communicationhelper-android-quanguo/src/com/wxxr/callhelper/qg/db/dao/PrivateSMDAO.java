package com.wxxr.callhelper.qg.db.dao;

import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxxr.callhelper.qg.ClosableIteratable;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.db.PrivateSMDataSource;

public class PrivateSMDAO {
	private PrivateSMDataSource dataSource;
	private SQLiteDatabase db;

	public PrivateSMDAO(Context context) {
		dataSource = new PrivateSMDataSource(context);
	}

	public void openDB() {
		db = dataSource.getWritableDatabase();
	}

	public void close() {
		if (db != null) {
			try {
				db.close();
			} catch (Throwable t) {
			}
			db = null;
		}
	}

	public boolean insert(TextMessageBean bean) {

		ContentValues values = createContentValues(bean);
		long id = db.insertOrThrow(PrivateSMDataSource.TABLE_NAME, "_id",
				values);
		bean.setId(Integer.valueOf(id + ""));
		return true;
	}

	public boolean delete(Integer id) {
		return db.delete(PrivateSMDataSource.TABLE_NAME, "_id = " + id, null) > 0;
	}

	public boolean deleteByNumber(String number) {
		return db.delete(PrivateSMDataSource.TABLE_NAME, " number = '" + number
				+ "'", null) > 0;
	}

	public ClosableIteratable<TextMessageBean> findByNumber(String number) {
		final Cursor cusor = db.rawQuery(
				PrivateSMDataSource.SELECT_BY_NUMBER_SQL,
				new String[] { number });
		return new ClosableIteratable<TextMessageBean>() {

			@Override
			public Iterator<TextMessageBean> iterator() {
				return new Iterator<TextMessageBean>() {

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override
					public TextMessageBean next() {
						return retrieveBean(cusor);
					}

					@Override
					public boolean hasNext() {
						return cusor.moveToNext();
					}
				};
			}

			@Override
			public void close() {
				try {
					cusor.close();
				} catch (Throwable t) {
				}
			}
		};
	}

	public int findUnreadOfAll() {
		int count = 0;
		Cursor cusor = db.rawQuery(PrivateSMDataSource.SELECT_UNREAD_ALL_SQL,
				null);
		if (cusor != null && cusor.moveToFirst()) {
			count = cusor.getCount();
		}
		cusor.close();
		return count;
	}

	public int findUnreadOfPhonenumber(String number) {
		int count = 0;
		Cursor cusor = db.rawQuery(
				PrivateSMDataSource.SELECT_UNREAD_BY_NUMBER_SQL,
				new String[] { number });
		if (cusor != null && cusor.moveToFirst()) {
			count = cusor.getCount();
		}
		return count;
	}

	public void setReadOfPhonenumber(String number) {
		try {
			db.execSQL(PrivateSMDataSource.SET_READ_SQL,
					new String[] { number });
		} catch (Exception ee) {

		}
	}

	public ClosableIteratable<TextMessageBean> findAll() {
		final Cursor cusor = db.rawQuery(PrivateSMDataSource.SELECT_ALL_SQL,
				new String[] {});
		return new ClosableIteratable<TextMessageBean>() {

			@Override
			public Iterator<TextMessageBean> iterator() {
				return new Iterator<TextMessageBean>() {

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}

					@Override
					public TextMessageBean next() {
						return retrieveBean(cusor);
					}

					@Override
					public boolean hasNext() {
						return cusor.moveToNext();
					}
				};
			}

			@Override
			public void close() {
				try {
					cusor.close();
				} catch (Throwable t) {
				}
			}
		};
	}

	/**
	 * @param bean
	 * @return
	 */
	protected ContentValues createContentValues(TextMessageBean bean) {
		ContentValues values = new ContentValues();
		values.put("number", bean.getNumber());
		values.put("mo", bean.getMo());
		values.put("mt", bean.getMt());
		values.put("timestamp", bean.getTimestamp());
		values.put("content", bean.getContent());
		values.put("readtext", bean.getReadtext());
		return values;
	}

	protected TextMessageBean retrieveBean(Cursor resultSet) {
		TextMessageBean bean = new TextMessageBean();
		bean.setNumber(resultSet.getString(0));
		bean.setMo(resultSet.getString(1));
		bean.setMt(resultSet.getString(2));
		bean.setTimestamp(resultSet.getLong(3));
		bean.setContent(resultSet.getString(4));
		bean.setId(resultSet.getInt(5));
		return bean;
	}

}
