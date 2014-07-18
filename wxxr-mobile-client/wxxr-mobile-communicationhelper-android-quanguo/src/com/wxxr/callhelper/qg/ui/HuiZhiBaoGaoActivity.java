package com.wxxr.callhelper.qg.ui;

import com.wxxr.callhelper.qg.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
//已经不用了
public class HuiZhiBaoGaoActivity extends Activity/* implements OnClickListener*/
{
//	ImageView iv_finish;
//
//	RelativeLayout rl_louhuabaogao;
//	RelativeLayout rl_huifu;
//	
//	TextView tv_number;
//	TextView tv_content;
//	
//	String tel_number;
//	String sms_body;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.huizhi_baogao_dialog);
//		
////		in.putExtra("inter_number", number);
////		in.putExtra("inter_body", body);
//		Intent intent =  getIntent();
//		
//		tel_number =  intent.getStringExtra("inter_number");
//		sms_body = intent.getStringExtra("inter_body");
//		
//		tv_number = (TextView) findViewById(R.id.tv_tel_number);
//		tv_content = (TextView) findViewById(R.id.tv_tel_content);
//		
//		tv_number.setText(tel_number);
//		tv_content.setText(sms_body);
//		
//		
//		
//		
//		
//		iv_finish = (ImageView) findViewById(R.id.iv_finish);
//		rl_louhuabaogao = (RelativeLayout) findViewById(R.id.rl_huizhibao);
//		rl_huifu = (RelativeLayout) findViewById(R.id.rl_huifu);
//
//		iv_finish.setOnClickListener(this);
//		rl_louhuabaogao.setOnClickListener(this);
//		rl_huifu.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v)
//	{
//		switch (v.getId())
//		{
//		case R.id.iv_finish:
//
//			finish();
//			break;
//
//		case R.id.rl_huizhibao:
//
//			//finish();
//			Intent in = new Intent();
//			in.setClass(this, LouHuaMainActivity.class);
////			in.putExtra("inter_number", number);
////			in.putExtra("inter_body", body);
//			// in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		//	in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			startActivity(in);
//			
//			
//			break;
//
//		case R.id.rl_huifu:
//
//			Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+tel_number));
//
//			// sendIntent.putExtra("sms_body", body); //
//
//			startActivity(sendIntent);
//
//			break;
//
//		}
//		
//	}

	
	
}
