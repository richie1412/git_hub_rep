package com.wxxr.callhelper.qg.ui;
//package com.wxxr.callhelper.ui;
//
//import com.wxxr.callhelper.qg.R;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//public class LouHuaBaoGaoActivity extends Activity implements OnClickListener
//{
//	ImageView iv_finish;
//	RelativeLayout rl_louhuabaogao;
//	RelativeLayout rl_huifu;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.louhua_baogao_dialog);
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
//
//		switch (v.getId())
//		{
//		case R.id.iv_finish:
//
//			finish();
//			break;
//
//		case R.id.rl_huizhibao:
//
//			Intent intent = new Intent();
//			intent.setClass(this, SmsHuiZhiMainActivity.class);
//			intent.putExtra("shouye", "shouye");
//			startActivity(intent);
//			
//			//finish();
//			
//			
//			break;
//
//		case R.id.rl_huifu:
//
//			Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + ""));
//
//			// sendIntent.putExtra("sms_body", body); //���ڸ���������ݣ��ɲ���
//
//			startActivity(sendIntent);
//
//			break;
//
//		}
//	}
//
//}
