package com.wxxr.callhelper.qg.utils;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.wxxr.callhelper.qg.bean.ContactsInfoBean;
import com.wxxr.callhelper.qg.constant.Sms;

public class ContactsUtil {
	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[]{
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID};

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 联系人的ID **/
	public static List<ContactsInfoBean> getAllContacts(Context context) {
		List<ContactsInfoBean> bean = new ArrayList<ContactsInfoBean>();
		ContentResolver resolver = context.getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				bean.add(new ContactsInfoBean(contactName, phoneNumber));
			}
			phoneCursor.close();
		}
		return bean;
	}
	public static String getContactNameByNumber(Context context,
			String telnumber) {
		String phonename = null;
		Cursor cursor = context.getContentResolver()
				.query(Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
						telnumber),
						new String[]{PhoneLookup._ID, PhoneLookup.NUMBER,
								PhoneLookup.DISPLAY_NAME, PhoneLookup.TYPE,
								PhoneLookup.LABEL}, null, null, null);

		if (cursor.getCount() == 0) {
			return null;
		} else if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			phonename = cursor.getString(2); // 获取姓名
			cursor.close();
		}
		return phonename;
	}
	/**
	 * 打电话
	 */
	public static void call(Context context, String phoneNumer) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.CALL");
		intent.setData(Uri.parse("tel:" + phoneNumer));
		context.startActivity(intent);
	}

	/**
	 * 发送短信
	 * 
	 * @param context
	 * @param msgContent
	 * @param address
	 * @param type
	 *            如果type=1,不需要弹出发送成功或者失败的提示框
	 */
	public static void sendMsg(Context context, String msgContent,
			String address, int type) {
		SmsManager mSmsManager = SmsManager.getDefault();
		Intent sent = new Intent(Sms.SENT_SMS_ACTION);
		PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, sent,
				0);

		Intent delivery = new Intent(Sms.DELIVERED_SMS_ACTION);
		PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0,
				delivery, 0);

		ArrayList<String> messages = mSmsManager.divideMessage(msgContent);
		for (String message : messages) {
			if (type == 1) {
				mSmsManager.sendTextMessage(address, null, message, null, null);
			} else {
				mSmsManager.sendTextMessage(address, null, message, sentIntent,
						deliveryIntent);
			}
			// save message
			Uri uri = Sms.Sent.CONTENT_URI;
			ContentValues values = new ContentValues();
			// address and body
			values.put(Sms.ADDRESS, address);
			values.put(Sms.BODY, message);
			context.getContentResolver().insert(uri, values);
		}
	}
	public static void goSendMsg(Activity activity, String address) {
		if (address == null) {
			return;
		}
		Uri smsToUri = Uri.parse("smsto:" + address);
		Intent mIntent = new Intent(android.content.Intent.ACTION_SENDTO,
				smsToUri);
		activity.startActivity(mIntent);
	}
}
