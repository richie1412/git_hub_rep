package com.wxxr.callhelper.qg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CongZhiOpenHelper extends SQLiteOpenHelper {
	public static final String KEY_TABLE_CONGZHIBAOGAO = "tablecongzhi";
	public static final String KEY_ROWID = "_id";

	public CongZhiOpenHelper(Context context) {
		super(context, "wxxr_congzhi.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + KEY_TABLE_CONGZHIBAOGAO + "("
				+ KEY_ROWID + " INTEGER PRIMARY KEY," + "address"
				+ " TEXT NOT NULL," + "month" + " TEXT NOT NULL," + "smstime"
				+ " LONG," + "type" + " INTEGER," + "smscontent"
				+ " TEXT NOT NULL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
