package com.wxxr.callhelper.qg.db.dao;

import java.util.List;

/**
 * 
 * @author cuizaixi
 * @create 2014-3-18 上午11:20:38
 * @param <T>
 * @see IAndroidSqDao;
 */
public interface IComeHelperDao<T> extends IAndroidSqDao<T> {
	/**
	 * get the last item id.always by get the specific cursor and have the
	 * cursor move to the last item
	 */
	int getLastID();
	/**
	 * get the records whose state still in originally ,never changed .
	 * 
	 * @return
	 */
	int getUnhappenItemCount();
	/**
	 * get the unhappenitems ;
	 * 
	 * @see IDao#getUnhappenItemCount();
	 * @return
	 */
	List<T> getUnhappenItems();
}
