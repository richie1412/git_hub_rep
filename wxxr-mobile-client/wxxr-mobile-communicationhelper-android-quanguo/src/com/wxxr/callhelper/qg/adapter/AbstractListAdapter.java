package com.wxxr.callhelper.qg.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
/**
 * 可复选的ListView 使用Adapter.
 * 
 * @author cuizaixi
 * 
 * @param <T>
 */
public abstract class AbstractListAdapter<T> extends AbstractCommonAdapter<T>
		implements
			ISelecteAdapter<T> {
	protected HashMap<Integer, Boolean> mIsSelected;
	private List<T> mCheckedItem;
	private boolean IsShowCB;
	private onSelecteChangeListener mListener;
	protected AbstractListAdapter(List<T> data, Context context) {
		super(data, context);
	}
	protected AbstractListAdapter(Context context) {
		super(context);
	}
	public HashMap<Integer, Boolean> getSelected() {
		return this.mIsSelected;
	}
	public void setSelected(HashMap<Integer, Boolean> isSelected) {
		this.mIsSelected = isSelected;
	}
	@SuppressLint("UseSparseArrays")
	public void setData(List<T> data) {
		this.mData = data;
		init();
	}
	@SuppressLint("UseSparseArrays")
	private void init() {
		this.mIsSelected = new HashMap<Integer, Boolean>();
		mCheckedItem = new ArrayList<T>();
		for (int i = 0; i < mData.size(); i++) {
			this.mIsSelected.put(i, false);
		}
	}
	public List<T> getCheckedItem() {

		for (int i = 0; i < mIsSelected.size(); i++) {
			if (mIsSelected.get(i)) {
				mCheckedItem.add(this.mData.get(i));
			}
		}
		return mCheckedItem;
	}
	public boolean atLeastOnChecked() {
		for (int i = 0; i < mIsSelected.size(); i++) {
			if (mIsSelected.get(i)) {
				return true;
			}
		}
		return false;
	}
	public View getView(final int position, View convertView, ViewGroup parent,
			final CheckBox checkBox) {
		if (checkBox != null) {
			if (IsShowCB) {
				checkBox.setVisibility(View.VISIBLE);
			} else {
				checkBox.setVisibility(View.GONE);
			}
			for (int i = 0; i < mIsSelected.size(); i++) {
				if (mIsSelected.get(position)) {
					checkBox.setChecked(true);
				} else {
					checkBox.setChecked(false);
				}
			}
			setOnClickListener(checkBox, position);
		}
		return convertView;
	}
	private void setOnClickListener(final CheckBox checkBox, final int position) {
		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkBox.isChecked()) {
					mIsSelected.put(position, true);
				} else {
					mIsSelected.put(position, false);
				}
				mListener.onSelecteChange(mIsSelected);
			}

		});
	}
	/**
	 * 显示选择框
	 */
	public void showCheckBox() {
		this.IsShowCB = true;
		notifyDataSetChanged();
	};
	/**
	 * 隐藏选择框
	 */

	public void hideCheckBox() {
		this.IsShowCB = false;
		notifyDataSetChanged();
		disSelectAll();
	}
	/**
	 * 全选
	 */
	public void selecteAll() {
		int size = mIsSelected.size();
		for (int i = 0; i < size; i++) {
			mIsSelected.put(i, true);
		}
		notifyDataSetChanged();
	}
	/**
	 * 取消全选
	 */

	public void disSelectAll() {
		int size = mIsSelected.size();
		for (int i = 0; i < size; i++) {
			mIsSelected.put(i, false);
		}
		notifyDataSetChanged();
	}
	/**
	 * 反向选择
	 */
	public void oppsiteSelect() {
		int size = mIsSelected.size();
		for (int i = 0; i < size; i++) {
			if (mIsSelected.get(i)) {
				mIsSelected.put(i, false);
			} else {
				mIsSelected.put(i, true);
			}
		}
	}
	public onSelecteChangeListener setOnSelecteChangeListener(
			onSelecteChangeListener Listener) {
		return mListener = Listener;
	}
	public interface onSelecteChangeListener {
		void onSelecteChange(HashMap<Integer, Boolean> selecte);
	}
}
