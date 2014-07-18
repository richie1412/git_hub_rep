package com.wxxr.callhelper.qg.adapter;

import java.util.List;	

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class HomePagerAdapter extends PagerAdapter {

	private List<View> listViews;

	public HomePagerAdapter(List<View> listViews) {
		this.listViews = listViews;
	}

	@Override
	public int getCount() {
		return listViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(listViews.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(listViews.get(position), 0);
		return listViews.get(position);
	}
	@Override
	public void finishUpdate(View arg0) {
		
	}


	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		
	}

}
