package com.wxxr.mobile.callhelper.app.model;

import java.util.List;

import android.graphics.Bitmap;

/**
 * @author fudapeng
 */
public class ContactPerson {
	/**
	 * 名字 
	 */
	private String fristName;
	
	/**
	 * 姓
	 */
	private String lastName;
	
	/**
	 * 头像
	 */
	private Bitmap portrait;
	
	/**
	 * 电话号码
	 */
	private List<String> phoneNumbers;
}
