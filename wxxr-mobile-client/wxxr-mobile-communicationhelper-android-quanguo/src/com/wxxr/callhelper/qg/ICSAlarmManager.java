package com.wxxr.callhelper.qg;

import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.db.dao.IAndroidSqDao;
/**
 * 
 * @author cuizaixi
 * @create 2014-3-18 下午12:04:41
 */
public interface ICSAlarmManager extends IAndroidSqDao<ComSecretaryBean> {
	/**
	 * 仅仅是删除一个闹钟，并没有删除数据库里面的记录
	 * 
	 * @see ICSAlarmManager#addAlarm(ComSecretaryBean);
	 * @see CSAlarmManager#insert(ComSecretaryBean);
	 * @param id
	 */
	void deleteAlarm(int id);
	/**
	 * 作用和删除相反，实现逻辑相同。
	 * 
	 * @param bean
	 */
	void addAlarm(ComSecretaryBean bean);
}
