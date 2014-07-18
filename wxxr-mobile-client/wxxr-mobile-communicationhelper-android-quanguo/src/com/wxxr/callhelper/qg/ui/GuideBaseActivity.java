package com.wxxr.callhelper.qg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.ImageAdapter;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.widget.MYGallery;

public class GuideBaseActivity  extends BaseActivity {

//	private ImageView iv_guide_base_1, iv_guide_base_2, iv_guide_base_3;
//	private  	  final int[] images = { R.drawable.guide1, R.drawable.guide2, R.drawable.guide3_2};
//	private MYGallery mygallery_base;
//	private TextView tv_titlebar_name;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.guide_base);
//		
//		findView();
//		
//		processLogic();
//	}
//
//	public void findView() {
////		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
////		findViewById(R.id.ll_top_titlebar).setBackgroundResource(R.drawable.top_titlebar_light_bg);
//		
//		iv_guide_base_1 = (ImageView) findViewById(R.id.iv_guide_base_1);
//		iv_guide_base_2 = (ImageView) findViewById(R.id.iv_guide_base_2);
//		iv_guide_base_3 = (ImageView) findViewById(R.id.iv_guide_base_3);
//		
//		mygallery_base = (MYGallery) findViewById(R.id.mygallery_base);
//	}
//
//	public void processLogic() {
////		tv_titlebar_name.setText("功能说明");
////		tv_titlebar_name.setTextColor(getResources().getColor(R.color.more_item));
//		
//		mygallery_base.setAdapter(new ImageAdapter(this, images));
//		mygallery_base.setSelection(0);
//		
//		mygallery_base.setOnItemSelectedListener(new myItemSelectedListener());
//	}
//	
//	private class myItemSelectedListener implements OnItemSelectedListener{
//
//		@Override
//		public void onItemSelected(AdapterView<?> parent, View view,
//				int position, long id) {
//			if (position == 0)
//			{
//				iv_guide_base_1.setBackgroundResource(R.drawable.point_selected);
//				iv_guide_base_2.setBackgroundResource(R.drawable.point);
//				iv_guide_base_3.setBackgroundResource(R.drawable.point);
//			}
//			else if (position == 1)
//			{
//				iv_guide_base_1.setBackgroundResource(R.drawable.point);
//				iv_guide_base_2.setBackgroundResource(R.drawable.point_selected);
//				iv_guide_base_3.setBackgroundResource(R.drawable.point);
//				
//			}
//			else if (position == 2)
//			{
//				iv_guide_base_1.setBackgroundResource(R.drawable.point);
//				iv_guide_base_2.setBackgroundResource(R.drawable.point);
//				iv_guide_base_3.setBackgroundResource(R.drawable.point_selected);
//				
//			}
//			
//		}
//
//		@Override
//		public void onNothingSelected(AdapterView<?> parent) {
//			
//		}
//		
//	}
	
}
