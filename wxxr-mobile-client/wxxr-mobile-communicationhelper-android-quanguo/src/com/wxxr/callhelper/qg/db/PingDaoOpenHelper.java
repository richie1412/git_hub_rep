package com.wxxr.callhelper.qg.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class PingDaoOpenHelper extends SQLiteOpenHelper
{
	public static final String KEY_TABLE_PINGDAO = "tablepingdao";
	public static final String KEY_ROWID = "_id";
	public PingDaoOpenHelper(Context context)
	{
		super(context, "wxxr_pingdao.db", null, 1);
	}
    //pingdaoname     yuertime   sexy   pingdaostyle
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "CREATE TABLE " + KEY_TABLE_PINGDAO + "(" + KEY_ROWID + " INTEGER PRIMARY KEY,"
				+ "pingdaoname" + " TEXT NOT NULL,"+ "needtime"+ " TEXT NOT NULL,"+ "telnumber"+ " TEXT NOT NULL,"+ "pingdaostyle"+ " INTEGER,"+ "sexy"+ " INTEGER)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_PINGDAO);
			onCreate(db);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
