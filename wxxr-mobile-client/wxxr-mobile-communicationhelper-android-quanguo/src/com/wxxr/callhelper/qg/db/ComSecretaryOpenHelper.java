package com.wxxr.callhelper.qg.db;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.wxxr.callhelper.qg.db.dao.AbstractDBHelperModule;

public class ComSecretaryOpenHelper extends AbstractDBHelperModule {
	public final static String KEY_TABLE_COMSECRETARY = "key_table_comsecretary";
	public static final String SECRET_TYPE = "secret_type";
	public static final String TEL_NUMBER = "tel_number";
	public static final String ALERT_TIME = "alert_time";
	public static final String STATE = "state";
	public static final String DELAY_TIMES = "delay_times";
	public ComSecretaryOpenHelper(Context context) {
		super(context, KEY_TABLE_COMSECRETARY, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(SECRET_TYPE, LONG);
		map.put(TEL_NUMBER, TEXT);
		map.put(ALERT_TIME, LONG);
		map.put(STATE, INTEGER);
		map.put(DELAY_TIMES, INTEGER + ")");
		createTable(db, KEY_TABLE_COMSECRETARY, KEY_ROWID, map);
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
