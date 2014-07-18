package com.wxxr.callhelper.qg.db.dao;

import java.util.List;

/**
 * 
 * @author cuizaixi
 * @create 2014-3-18 上午11:40:08
 * @param <T>
 */
public interface IAndroidSqDao<T> {
	/**
	 * insert record item one by one
	 * 
	 * @param t
	 * @return
	 */
	boolean insert(T t);
	/**
	 * return all records presents by entity
	 * 
	 * @return
	 */
	List<T> findAll();
	/**
	 * return a single record by id
	 * 
	 * @param id
	 * @return
	 */
	T findByID(int id);
	/**
	 * update record which ware put in a contentvalues;
	 * 
	 * @param t
	 */
	void update(T t);
	/**
	 * delete record by id;
	 * 
	 * @param id
	 */
	void delete(int id);
	/**
	 * get the last item id.always by get the specific cursor and have the
	 * cursor move to the last item
	 */
}
