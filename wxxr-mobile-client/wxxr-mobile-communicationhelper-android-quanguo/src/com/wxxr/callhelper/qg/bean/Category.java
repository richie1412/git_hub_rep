package com.wxxr.callhelper.qg.bean;

import android.view.View;
import android.widget.Adapter;

public class Category {
	private String mTitle;
	private View mview;
	private Adapter mAdapter;

	public Category(String title, Adapter adapter) {
		mTitle = title;
		mAdapter = adapter;
	}

	public void setTile(String title) {
		mTitle = title;
	}

	public View getMview() {
		return mview;
	}

	public void setMview(View mview) {
		this.mview = mview;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setAdapter(Adapter adapter) {
		mAdapter = adapter;
	}

	public Adapter getAdapter() {
		return mAdapter;
	}

}
