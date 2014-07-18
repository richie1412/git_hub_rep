package com.wxxr.callhelper.qg.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
/**
 * 
 * @author cuizaixi
 * @param <T>
 */
public abstract class AbstractCommonAdapter<T> extends BaseAdapter {
	protected List<T> mData;
	protected Context mContext;
	protected LayoutInflater mInflater;
	private Object mLock = new Object();
	public AbstractCommonAdapter(List<T> data, Context context) {
		this.mData = data;
		this.mContext = context;
	}
	public AbstractCommonAdapter(Context context) {
		this.mContext = context;
	}
	protected LayoutInflater getInflater() {
		return mInflater != null ? mInflater : (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	protected void clearData() {
		synchronized (mLock) {
			if (this.mData != null) {
				this.mData.clear();
				notifyDataSetChanged();
			}
		}
	}
	public void setData(List<T> data) {
		synchronized (mLock) {
			this.mData = data;
			notifyDataSetChanged();
		}
	}
	// 重载setData 方法，当传入集合数据时使用
	public void setData(T[] array) {
		synchronized (mLock) {
			this.mData = Arrays.asList(array);
		}
	}
	public List<T> getData() {
		synchronized (mLock) {
			return this.mData;
		}
	}
	protected void addData(List<T> data) {
		synchronized (mLock) {
			this.mData.addAll(data);
			this.notifyDataSetChanged();
		}
	}
	protected void addData2Top(List<T> data) {
		synchronized (mLock) {
			this.mData.addAll(0, data);
			this.notifyDataSetChanged();
		}
	}
	@Override
	public int getCount() {
		return mData == null ? 0 : mData.size();
	}
	@Override
	public T getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	// protected abstract View newView();
	// protected abstract void bindData();
}
