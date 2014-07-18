package com.wxxr.callhelper.qg.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
/**
 * 
 * 自定义的listview，内容有多少，就展示多少，没有滚动条
 *
 * @since 1.0
 */
public class MyListView extends ListView {

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
	
	public void setListViewHeightBasedOnChildren( ) {    
//        ListAdapter listAdapter = getAdapter();    
//        if (listAdapter == null) {    
//            return;    
//        }    
//        int totalHeight = 0;    
//        for (int i = 0; i < listAdapter.getCount(); i++) {    
//            View listItem = listAdapter.getView(i, null, this);    
//            listItem.measure(0, 0);   
//            totalHeight += listItem.getMeasuredHeight();    
//        }    
//        ViewGroup.LayoutParams params = getLayoutParams();    
//        params.height = totalHeight    
//                + (getDividerHeight() * (listAdapter.getCount() + 1));    
//        setLayoutParams(params);    
    }    
}
