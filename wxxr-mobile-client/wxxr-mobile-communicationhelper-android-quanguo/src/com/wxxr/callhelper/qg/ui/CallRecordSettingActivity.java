package com.wxxr.callhelper.qg.ui;

import android.os.Bundle;	
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.ManagerSP;

public class CallRecordSettingActivity extends BaseActivity {

//	private int setting;
//	private ImageView iv_hand, iv_auto, iv_close;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
////		setContentView(R.layout.callrecord_setting);
//		
//		findView();
//		
//		processLogic();
//	}
//	
//	public void findView() {
//		iv_hand = (ImageView) findViewById(R.id.iv_hand);
//		iv_auto =(ImageView) findViewById(R.id.iv_auto);
//		iv_close = (ImageView) findViewById(R.id.iv_close);
//		
//		findViewById(R.id.ll_auto).setOnClickListener(this);
//		findViewById(R.id.ll_hand).setOnClickListener(this);
//		findViewById(R.id.ll_close).setOnClickListener(this);
//		
////		findViewById(R.id.btn_callrecord_setting).setOnClickListener(this);
////		findViewById(R.id.iv_callrecord_cancle).setOnClickListener(this);
//	}
//
//	public void processLogic() {
//		setting = ManagerSP.getInstance(this).get(Constant.CALLRECORD_SETTING, 1);
//		
//		if(setting == 0){
//			iv_auto.setBackgroundResource(R.drawable.callrecordsetting_press);
//			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_close.setBackgroundResource(R.drawable.callrecordsetting);
//			
//		}else if(setting == 1){
//			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_hand.setBackgroundResource(R.drawable.callrecordsetting_press);
//			iv_close.setBackgroundResource(R.drawable.callrecordsetting);
//			
//		}else{
//			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_close.setBackgroundResource(R.drawable.callrecordsetting_press);
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch(v.getId()){
//		case R.id.ll_auto:
//			iv_auto.setBackgroundResource(R.drawable.callrecordsetting_press);
//			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_close.setBackgroundResource(R.drawable.callrecordsetting);
//			setting = 0;
//			break;
//		case R.id.ll_hand:
//			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_hand.setBackgroundResource(R.drawable.callrecordsetting_press);
//			iv_close.setBackgroundResource(R.drawable.callrecordsetting);
//			setting = 1;
//			break;
//		case R.id.ll_close:
//			iv_auto.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_hand.setBackgroundResource(R.drawable.callrecordsetting);
//			iv_close.setBackgroundResource(R.drawable.callrecordsetting_press);
//			setting = 2;
//			break;
//			
////		case R.id.btn_callrecord_setting:
////			ManagerSP.getInstance(this).update(Constant.CALLRECORD_SETTING, setting);
////			finish();
////			break;
////			
////		case R.id.iv_callrecord_cancle:
////			finish();
////			break;
//		}
//		super.onClick(v);
//	}
}
