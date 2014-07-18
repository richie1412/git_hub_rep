package com.wxxr.callhelper.qg.widget;

//import android.view.View.MeasureSpec;
import android.widget.GridView;
/**
 * 
 * 自定义的gridview，内容有多少，就展示多少，没有滚动条
 * @since 1.0
 */
public class MyGridView extends GridView   
{   
  public MyGridView(android.content.Context context,   
  android.util.AttributeSet attrs)   
  {   
  super(context, attrs);   
  }   
   
  /**  
  * 设置不滚动  
  */   
  public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)   
  {   
  int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,   
  MeasureSpec.AT_MOST);   
  super.onMeasure(widthMeasureSpec, expandSpec);   
   
  }   
   
}   