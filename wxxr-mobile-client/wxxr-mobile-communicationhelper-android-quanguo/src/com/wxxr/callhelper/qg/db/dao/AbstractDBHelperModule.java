package com.wxxr.callhelper.qg.db.dao;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.wxxr.callhelper.qg.utils.StringUtil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * @author cuizaixi
 * 
 */
public abstract class AbstractDBHelperModule extends SQLiteOpenHelper {
	public static final String LONG = "LONG";
	public static final String TEXT = "TEXT NOT NULL";
	public static final String INTEGER = "INTEGER";
	public static final String KEY_ROWID = "_id";
	public AbstractDBHelperModule(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	/**
	 * 
	 * @param db
	 *            数据库
	 * @param tabName
	 *            表名
	 * @param keyId
	 *            主键名称
	 * @param valuses
	 *            字段名称，以键值对的形式传递
	 */
	protected void createTable(SQLiteDatabase db, String tabName, String keyId,
			Map<String, String> valuses) {
		if (StringUtil.isEmpty(tabName) || valuses == null || valuses.isEmpty()) {
			throw new IllegalArgumentException(
					"the values  can not be null,at least  the name and  the table to be created,please check it befor");
		}
		StringBuilder builder = new StringBuilder();
		if (StringUtil.isEmpty(keyId)) {
			keyId = "_id";
		}
		builder.append("CREATE Table " + tabName + "(	" + keyId
				+ " INTEGER PRIMARY KEY,");
		Set<Entry<String, String>> set = valuses.entrySet();
		if (set != null) {
			Iterator<Entry<String, String>> iterator = set.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String key = entry.getKey();
				String value = entry.getValue();
				if (!value.contains(")")) {
					builder.append(key + " " + value + ",");
				} else {
					builder.append(key + " " + value);
				}

			}
		}
		db.execSQL(builder.toString());
	}
}
