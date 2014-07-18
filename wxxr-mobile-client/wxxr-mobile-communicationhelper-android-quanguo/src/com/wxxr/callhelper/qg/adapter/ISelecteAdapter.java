package com.wxxr.callhelper.qg.adapter;

import java.util.HashMap;
import java.util.List;
/**
 * 
 * @author cuizaixi
 * 
 * @param <T>
 */
public interface ISelecteAdapter<T> {
	HashMap<Integer, Boolean> getSelected();
	void setSelected(HashMap<Integer, Boolean> isSelected);
	void setData(List<T> data);
	public List<T> getData();
}
