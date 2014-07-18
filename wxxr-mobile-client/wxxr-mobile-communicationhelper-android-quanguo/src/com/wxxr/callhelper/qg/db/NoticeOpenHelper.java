package com.wxxr.callhelper.qg.db;

import java.util.Date;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoticeOpenHelper  extends SQLiteOpenHelper {
	public static final String KEY_TABLE_CONGZHIBAOGAO = "tablenotice";
	public static final String KEY_ROWID = "_id";

	public NoticeOpenHelper(Context context) {
		super(context, "wxxr_notice.db", null, 1);
	}
	
//	  /**
//     * @Fields title : 通知消息内容
//     */
//    private String title;
//
//    /**
//     * @Fields content : 内容
//     */
//    private String content;
//
//    /**
//     * @Fields type : 通知类型：如升级通知， 广场活动通知， 登录通知
//     */
//    private Integer type;
//
//    /**
//     * @Fields startTime : 通知开始时间
//     */
//    private Date startTime;
//
//    /**
//     * @Fields endTime : 通知结束时间
//     */
//    private Date endTime;
//
//    /**
//     * @Fields showTime : 滚动速度(秒/字)
//     */
//    private Byte showTime;
//
//    /**
//     * @Fields toURL : 跳转地址
//     */
//    private String toURL;
//
//    /**
//     * @Fields isActive : 是否生效
//     */
//    private Byte isActive;
//
//    /**
//     * @Fields createDate : 创建时间
//     */
//    private Date createDate;
//
//    /**
//     * @Fields createBy : 创建者
//     */
//    private String createBy;
//
//    /**
//     * @Fields updateDate : 更新时间
//     */
//    private Date updateDate;
//
//    /**
//     * @Fields updateBy : 更新者
//     */
//    private String updateBy;
//
//    /**
//     * @Fields forwardType : 转向页面类型
//     */
//    private int forwardType;
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + KEY_TABLE_CONGZHIBAOGAO + "("
				+ KEY_ROWID + " INTEGER PRIMARY KEY, title  TEXT, content  TEXT ,starttime  LONG,endtime  LONG,type  BYTE,"
				+ "isActive BYTE,  tourl TEXT,   showtime BYTE,forwardType BYTE  ,noticeid LONG,showcount LONG,provincecode TEXT  )";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
