package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.AbstractMonitorRunnable;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.util.StringUtils;

public class GuiShuDiService extends AbstractModule<ComHelperAppContext>
		implements
			IGuiShuDiService {
	private static final Trace log = Trace.register(GuiShuDiService.class);
	private final String path = Constant.SDPATH_LOCATION + "/"
			+ Constant.DB_NAME;
	SQLiteDatabase db;

	protected SQLiteDatabase getDatabase() {
		if (db == null || !db.isOpen()) {
			db = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}
		return db;
	}

	@Override
	public void getRegionByPhoneNumber(final String phoneNumber,
			IProgressMonitor m) {
		log.info("begin getRegionByPhoneNumber " + phoneNumber);
		context.getExecutor().execute(new AbstractMonitorRunnable(m, log) {
			@Override
			protected Object executeTask() throws Throwable {
				Region r = null;
				// （广东版前七位开始匹配，不做11位的手机号判断）判断是否是手机号,如果有根据手机号码查找归属地，如果没有则取三位，或者四位作为区号查询归属地
				// String msisdn = Tools.getMisdnByContent(phoneNumber);
				// if (StringUtils.isBlank(msisdn)) {
				// r = getRegionbyAreaCode(msisdn);
				//
				// } else {
				// r = getRegionByMsisdn(msisdn);
				// }
				if (phoneNumber.startsWith("0")) {
					r = getRegionbyAreaCode(phoneNumber);

				} else if (phoneNumber.startsWith("1")) {
					r = getRegionByMsisdn(phoneNumber);
				}
				return r;
			}
		});
	}

	public void getRegionByDialogNumber(final String dialogNumber,
			IProgressMonitor m) {
		log.info("begin getRegionByPhoneNumber " + dialogNumber);
		context.getExecutor().execute(new AbstractMonitorRunnable(m, log) {
			@Override
			protected Object executeTask() throws Throwable {
				Region r = null;
				// 判断是否是手机号,如果有根据手机号码查找归属地，如果没有则取三位，或者四位作为区号查询归属地
				String msisdn = Tools.getMisdnByContent(dialogNumber);
				if (StringUtils.isBlank(msisdn)) {
					if (dialogNumber.startsWith("0")) {
						if (dialogNumber.length() >= 4) {
							r = getRegionbyAreaCode(dialogNumber
									.substring(0, 4));
							if (r == null) {
								r = getRegionbyAreaCode(dialogNumber.substring(
										0, 3));
							}
						}

					}
				} else {
					r = getRegionByMsisdn(msisdn);
				}
				return r;
			}
		});
	}

	public Region getRegionByDialogNumber(String dialogNumber) {
		Region r = null;
		// 判断是否是手机号,如果有根据手机号码查找归属地，如果没有则取三位，或者四位作为区号查询归属地
		String msisdn = Tools.getMisdnByContent(dialogNumber);
		if (StringUtils.isBlank(msisdn)) {
			if (dialogNumber.startsWith("0")) {
				if (dialogNumber.length() >= 4) {
					r = getRegionbyAreaCode(dialogNumber.substring(0, 4));
					if (r == null) {
						r = getRegionbyAreaCode(dialogNumber.substring(0, 3));
					}
				}

			}
		} else {
			r = getRegionByMsisdn(msisdn);
		}
		return r;
	}
	@Override
	public void getRegionBySmsContent(final String smsContent,
			IProgressMonitor m) {
		context.getExecutor().execute(new AbstractMonitorRunnable(m, log) {
			@Override
			protected Object executeTask() throws Throwable {
				Region r = getRegionBySmsContent(smsContent);
				return r;
			}
		});
	}

	/**
	 * 根据手机号码（必须是11位的手机号码）
	 * 
	 * @param phoneNum
	 * @return
	 */

	public Region getRegionByMsisdn(String phoneNum) {
		Cursor c = null;
		Region r = null;
		if (phoneNum.length() > 10) {
			phoneNum = phoneNum.substring(phoneNum.length() - 11,
					phoneNum.length());
		}
		if (phoneNum.length() < 7) {
			return null;
		}
		try {
			String sql = "select r.name , b.brandname,r.region_id ,r.p_region_id from phone_range p , region r , brand b where p.range_id = ? and p.brand_id = b.brandid and p.region_id = r.region_id";
			String value = phoneNum.substring(0, 7);
			c = getDatabase().rawQuery(sql, new String[]{value});
			r = new Region();

			r.setPhoneNumber(phoneNum);

			if (c.moveToNext()) {
				r.setRegionName(c.getString(0));
				r.setBrandName(c.getString(1));
				r.setRegionId(c.getLong(2));
				r.setpRegionId(c.getLong(3));
				// 查找上一级归属地
				Region p = getRegionbyParentRegionid(r.getpRegionId());
				r.setpRegionName(p.getRegionName());
			} else {
				return null;
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return r;

	}

	// 查找上一级归属地
	protected Region getRegionbyParentRegionid(long regionId) {
		Cursor c = null;
		try {
			String sql = "select r.region_id,r.name ,r.p_region_id from region r  where r.region_id = ? ";
			c = getDatabase().rawQuery(sql,
					new String[]{String.valueOf(regionId)});

			if (c.moveToNext()) {
				Region r = new Region();
				r.setRegionId(c.getLong(0));
				r.setRegionName(c.getString(1));
				r.setpRegionId(c.getLong(2));
				return r;
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return null;
	}

	/**
	 * 根据用户拨打的号码找出区号，再根据区号找归属地
	 * 
	 * @param phonenumber
	 * @return
	 */
	public Region getRegionbyAreaCode(String areaCode) {

		if (StringUtils.isNotBlank(areaCode)) {
			Region r = null;
			if (areaCode.length() == 4 || areaCode.length() == 3) {
				r = getRegionbycode(areaCode.substring(0, areaCode.length()));
			}
			return r;
		}
		return null;
	}

	// 根据区号查找归属地
	protected Region getRegionbycode(String code) {
		log.info("begin getRegionbycode code=" + code);
		Cursor c = null;
		try {
			String sql = "select r.region_id,r.name ,r.p_region_id from region r  where r.code = ? ";
			c = getDatabase().rawQuery(sql, new String[]{String.valueOf(code)});

			if (c.moveToNext()) {
				Region r = new Region();
				r.setRegionId(c.getLong(0));
				r.setRegionName(c.getString(1));
				r.setpRegionId(c.getLong(2));
				// 查找上一级的归属地名称
				Region p = getRegionbyParentRegionid(r.getpRegionId());
				r.setpRegionName(p.getRegionName());
				log.info("getRegionbycode Region=" + r.toString());

				return r;
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return null;
	}
	// 是否是移动的手机号码
	public boolean isChinaMobileMsisdn(String msisdn) {
		log.info("begin isChinaMobileMsisdn code=" + msisdn);
		if (StringUtils.isBlank(msisdn) || msisdn.length() != 11) {
			return false;
		}

		String prefix = msisdn.substring(0, 7);
		Cursor c = null;
		try {
			String sql = "select a.telecom from phone_range a where   a.range_id = ? ";
			c = getDatabase().rawQuery(sql,
					new String[]{String.valueOf(prefix)});
			if (c.moveToNext()) {
				return "1".equals(c.getString(0));// 1 是移动 2是电信
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}

		return false;
	}

	public Region getRegionBySmsContent(String smsContent) {
		String msisdn = Tools.getMisdnByContent(smsContent);
		if (StringUtils.isNotBlank(msisdn)) {
			Region r = this.getRegionByMsisdn(msisdn);
			if (r != null) {
				return r;
			} else {
				r = new Region();
				r.setPhoneNumber(msisdn);
				return r;
			}
		}
		return null;
	}

	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {
		context.registerService(IGuiShuDiService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IGuiShuDiService.class, this);
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

}
