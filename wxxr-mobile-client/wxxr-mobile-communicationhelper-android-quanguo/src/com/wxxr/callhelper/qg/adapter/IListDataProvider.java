/**
 * 
 */
package com.wxxr.callhelper.qg.adapter;


/**
 * @author neillin
 *
 */
public interface IListDataProvider<T> {
	int getItemCounts();
	T getItem(int i);
}
