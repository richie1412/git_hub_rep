package com.wxxr.callhelper.qg.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PrivateSMDataSource extends SQLiteOpenHelper
{
	public static final String TABLE_NAME = "privateSM";
	public static final String KEY_ROWID = "_id";
	
	public static final String CREATE_TABLE_SQL =  "CREATE TABLE " + TABLE_NAME + "( _id INTEGER PRIMARY KEY,"
			+ "number TEXT NOT NULL, mo TEXT, mt TEXT, timestamp LONG, content TEXT NOT NULL,readtext  TEXT)";
	
	public static final String SELECT_ALL_SQL = "SELECT number, mo, mt, timestamp, content, _id from "+TABLE_NAME;

	
	public static final String SELECT_BY_NUMBER_SQL = "SELECT number, mo, mt, timestamp, content, _id from "+TABLE_NAME + " WHERE number = ?";
	
	public static final String SELECT_UNREAD_BY_NUMBER_SQL = "SELECT number, mo, mt, timestamp, content, _id from "+TABLE_NAME + "  WHERE readtext = 'no' and   number = ?";

	
	public static final String SELECT_UNREAD_ALL_SQL = "SELECT number, mo, mt, timestamp, content, _id from "+TABLE_NAME + " WHERE readtext = 'no' ";

	public static final String SET_READ_SQL = "UPDATE "+TABLE_NAME + " SET  readtext='yes'  WHERE number = ? ";


	public PrivateSMDataSource(Context context)
	{
		super(context, "private_sm.db", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
//			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//			onCreate(db);
		
		if(oldVersion<2){
			try {
				String sql = "ALTER TABLE " + TABLE_NAME  + " ADD  readtext  TEXT DEFAULT yes";
				db.execSQL(sql);
			} catch (Exception ee) {
				
			}
		}
		
		
	}

}
