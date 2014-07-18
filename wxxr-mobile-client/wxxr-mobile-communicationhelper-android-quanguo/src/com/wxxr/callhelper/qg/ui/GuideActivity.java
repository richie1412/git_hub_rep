package com.wxxr.callhelper.qg.ui;

import android.content.Intent;	
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.ImageAdapter;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.widget.MYGallery;

public class GuideActivity  /* extends BaseActivity*/{
	
//	private ImageView iv_guide_1, iv_guide_2, iv_guide_3;
//	final int[] images = { R.drawable.guide1, R.drawable.guide2, R.drawable.guide3 };
//	private MYGallery myGallery;
//	private ImageView iv_start;
//	private RelativeLayout rl_guide_bottom_point;
//	private ManagerSP sp;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE); 
//		setContentView(R.layout.guide);
//		
//		findView();
//		
//		processLogic();
//	}
//
//	public void findView() {
//		sp = ManagerSP.getInstance(getApplicationContext());
//		
//		iv_guide_1 = (ImageView) findViewById(R.id.iv_guide_1);
//		iv_guide_2 = (ImageView) findViewById(R.id.iv_guide_2);
//		iv_guide_3 = (ImageView) findViewById(R.id.iv_guide_3);
//		
//		myGallery = (MYGallery) findViewById(R.id.mygallery);
//		iv_start = (ImageView) findViewById(R.id.iv_start);
//		rl_guide_bottom_point = (RelativeLayout) findViewById(R.id.rl_guide_bottom_point);
//	}
//
//	public void processLogic() {
//		iv_start.setOnClickListener(this);
//		
//		myGallery.setAdapter(new ImageAdapter(this, images));
//		myGallery.setSelection(0);
//		
//		myGallery.setOnItemSelectedListener(new myItemSelectedListener());
//	}
//	
//	private class myItemSelectedListener implements OnItemSelectedListener{
//
//		@Override
//		public void onItemSelected(AdapterView<?> parent, View view,
//				int position, long id) {
//			if (position == 0)
//			{
//				iv_guide_1.setBackgroundResource(R.drawable.point_selected);
//				iv_guide_2.setBackgroundResource(R.drawable.point);
//				iv_guide_3.setBackgroundResource(R.drawable.point);
//				
//				iv_start.setVisibility(View.GONE);
//				rl_guide_bottom_point.setVisibility(View.VISIBLE);
//
//			}
//			else if (position == 1)
//			{
//				iv_guide_1.setBackgroundResource(R.drawable.point);
//				iv_guide_2.setBackgroundResource(R.drawable.point_selected);
//				iv_guide_3.setBackgroundResource(R.drawable.point);
//				
//				iv_start.setVisibility(View.GONE);
//				rl_guide_bottom_point.setVisibility(View.VISIBLE);
//			}
//			else{
//				iv_start.setVisibility(View.VISIBLE);
//				rl_guide_bottom_point.setVisibility(View.GONE);
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
//	
//	@Override
//	public void onClick(View v) {
//		switch(v.getId()){
//		case R.id.iv_start:
//			sp.update("guide", 1);
//			sp.update(Constant.UNINSTALL, 0);
//			startActivity(new Intent(GuideActivity.this, HomeActivity.class));
//			finish();
//			break;
//		}
//	}
//	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//		if (keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			System.exit(1);
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
