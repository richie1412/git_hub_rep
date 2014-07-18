package com.wxxr.callhelper.qg.adapter;

import java.util.List;

import android.widget.Filterable;

public interface IFilterExt<T> extends Filterable {
	boolean needFilter(boolean b);
	List<T> getFilteResult();
}
