package com.wxxr.callhelper.qg.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SiMiContactsOpenHelper extends SQLiteOpenHelper
{
	public static final String KEY_TABLE_SIMIPERSON = "table_simiperson";
	public static final String KEY_ROWID = "_id";
	public SiMiContactsOpenHelper(Context context)
	{
		super(context, "wxxr_simiperson.db", null, 1);
	}
    
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "CREATE TABLE " + KEY_TABLE_SIMIPERSON + "(" + KEY_ROWID + " INTEGER PRIMARY KEY,"
				+ "address" + " TEXT NOT NULL,"  + "cdate"+ " LONG,"+ "mstyle"+ " INTEGER,"+ "islock"+ " INTEGER," +  "content"
				+ " TEXT NOT NULL)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_SIMIPERSON);
			onCreate(db);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		
	}

}
