package com.wxxr.callhelper.qg.utils;

import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * 根据手机号码查询联系人头像的工具类
 * 
 * @author cuizaixi
 * 
 */
public class ContactIconUtil {
	private static ContactIconUtil instance = null;

	private ContactIconUtil() {
	}

	public synchronized static ContactIconUtil getInstance() {
		if (instance == null) {
			instance = new ContactIconUtil();
		}
		return instance;
	}

	public Bitmap NumberForIcon(String telnumber, Context context) {
		Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/"
				+ "data/phones/filter/" + telnumber);
		Cursor cursorCantacts = null;
		try {
			cursorCantacts = context.getContentResolver().query(
				uriNumber2Contacts, null, null, null, null);
			if (cursorCantacts.getCount() > 0) {
				cursorCantacts.moveToFirst();
				Long contactID = cursorCantacts.getLong(cursorCantacts
						.getColumnIndex("contact_id"));
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI, contactID);
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(context.getContentResolver(),
								uri);
				Bitmap btContactImage = BitmapFactory.decodeStream(input);
				return btContactImage;
			}
			return null;
		}finally{
			if(cursorCantacts != null){
				try {
				cursorCantacts.close();
				}catch(Throwable t){}
				cursorCantacts = null;
			}
		}
	}
}
