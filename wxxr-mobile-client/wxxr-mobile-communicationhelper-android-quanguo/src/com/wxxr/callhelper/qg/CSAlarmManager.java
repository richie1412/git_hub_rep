package com.wxxr.callhelper.qg;
import java.lang.ref.WeakReference;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.db.dao.IComeHelperDao;
import com.wxxr.callhelper.qg.receiver.AlarmClockReceiver;
import com.wxxr.callhelper.qg.ui.CSManagerActvity;
/**
 * 
 * @author cuizaixi 管理两个模块 1.管理提醒事项在数据库中的存储 2.管理闹钟服务
 */
public class CSAlarmManager implements ICSAlarmManager {
	private static CSAlarmManager mInstance;
	private Context mContext;
	private AlarmManager mAlarmManager;
	private WeakReference<IComeHelperDao<ComSecretaryBean>> wr_am;
	CSAlarmManager(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) mContext
				.getSystemService(Context.ALARM_SERVICE);
		wr_am = new WeakReference<IComeHelperDao<ComSecretaryBean>>(
				ComSecretaryDao.getInstance(mContext));
	}
	public static CSAlarmManager getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new CSAlarmManager(context);
		}
		return mInstance;
	}
	/**
	 * 删除闹钟
	 */
	@Override
	public void deleteAlarm(int id) {
		Intent intent = new Intent(mContext, AlarmClockReceiver.class);
		PendingIntent operation = PendingIntent.getBroadcast(mContext, id,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.cancel(operation);
	}
	/**
	 * 增加闹钟
	 */
	@Override
	public void addAlarm(ComSecretaryBean bean) {
		Intent intent = new Intent(mContext, AlarmClockReceiver.class);
		intent.putExtra(CSManagerActvity.CS_BEAN, bean);
		PendingIntent operation = PendingIntent.getBroadcast(mContext,
				bean.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 单次闹钟
		mAlarmManager.set(AlarmManager.RTC_WAKEUP, bean.getAlertTime(),
				operation);
	}
	@Override
	public boolean insert(ComSecretaryBean bean) {
		IComeHelperDao<ComSecretaryBean> dao = wr_am.get();
		bean.setId(dao.getLastID());
		dao.insert(bean);
		addAlarm(bean);
		return true;
	}
	@Override
	public List<ComSecretaryBean> findAll() {
		return null;
	}
	@Override
	public ComSecretaryBean findByID(int id) {
		return null;
	}
	@Override
	public void update(ComSecretaryBean bean) {
		IComeHelperDao<ComSecretaryBean> dao = wr_am.get();
		dao.update(bean);
		deleteAlarm(bean.getId());
		addAlarm(bean);
	}
	@Override
	public void delete(int id) {
		IComeHelperDao<ComSecretaryBean> dao = wr_am.get();
		dao.delete(id);
		deleteAlarm(id);
	}
}
