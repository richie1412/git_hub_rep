package com.wxxr.callhelper.qg.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class InterceptContentOpenHelper extends SQLiteOpenHelper
{
	public static final String KEY_TABLE_Intercept = "tableinterceptecontent";
	public static final String KEY_ROWID = "_id";

	public InterceptContentOpenHelper(Context context)
	{
		super(context, "wxxr_inteceptcontent.db", null, 2);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql = "CREATE TABLE " + KEY_TABLE_Intercept + "(" + KEY_ROWID + " INTEGER PRIMARY KEY," + "smsstyle"
				+ " INTEGER," + "telnumber" + " TEXT NOT NULL," + "huizhi_number" + " TEXT NOT NULL,"+ "secondstyle"+ " INTEGER," + "smscontent" + " TEXT NOT NULL)";
		db.execSQL(sql);

		String h = "insert into tableinterceptecontent (smsstyle,telnumber,huizhi_number,secondstyle,smscontent) values";
		sql = h + "('1','--','--','0','hh')";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			db.execSQL("DROP TABLE IF EXISTS " + KEY_TABLE_Intercept);
			onCreate(db);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

	}

}
