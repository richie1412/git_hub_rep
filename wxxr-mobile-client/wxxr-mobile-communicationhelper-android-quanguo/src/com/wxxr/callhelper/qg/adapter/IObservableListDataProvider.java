/**
 * 
 */
package com.wxxr.callhelper.qg.adapter;

/**
 * @author neillin
 *
 */
public interface IObservableListDataProvider<T> extends IListDataProvider<T> {
	void onDataChanged(IDataChangedListener listener);
}
