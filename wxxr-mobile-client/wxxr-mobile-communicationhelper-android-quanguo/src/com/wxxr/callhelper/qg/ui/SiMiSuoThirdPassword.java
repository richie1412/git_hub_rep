package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.IPrivateSimiNetService;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.gd.GDSimiContactsActvity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.LocusPassWordView;
import com.wxxr.callhelper.qg.widget.LocusPassWordView.OnCompleteListener;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
/**
 * 私密锁 解密的输入页面
 * @author yangrunfei
 *
 */
public class SiMiSuoThirdPassword extends BaseActivity implements OnClickListener
{
	private LocusPassWordView lpwv;
	TextView tv_forget;
//	private String isUpdate;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setpassword_activity3);
		
	
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);
		((TextView)findViewById(R.id.tv_titlebar_name)).setText("私密信息锁");
		
		
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.simi_main_back);
		Bitmap readBitmap = Tools.readBitmap(this, R.drawable.simi_background);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		rl.setBackgroundDrawable(bitmapDrawable);

//		Intent intent = getIntent();
//		isUpdate = intent.getStringExtra("from_simi");
		lpwv = (LocusPassWordView) this.findViewById(R.id.mLocusPassWordView3);
		lpwv.setOnCompleteListener(new OnCompleteListener()
		{
			@Override
			public void onComplete(String mPassword)
			{

				doVerifyPassword(mPassword);
			}
		});
		findView();
		fillData();

	}

	private void doVerifyPassword(final String passwd)
	{
		CMProgressMonitor monitor = new CMProgressMonitor(this)
		{
			@Override
			protected void handleFailed(Throwable cause)
			{
				showMessageBox("私密锁初始化失败，原因：[" + cause.getLocalizedMessage() + "]");
			}

			@Override
			protected void handleDone(Object returnVal)
			{
				boolean val = ((Boolean) returnVal).booleanValue();
				if (val)
				{

//					if (isUpdate != null && isUpdate.equals("update_password"))
//					{
//						Intent intent = new Intent();
//						intent.setClass(SiMiSuoThirdPassword.this, SiMiSuoHomePasswordActivity.class);
//						startActivity(intent);
//					}
//					else
//					{
						Intent intent = new Intent();
						intent.setClass(SiMiSuoThirdPassword.this, GDSimiContactsActvity.class);
						startActivity(intent);
						finish();
//					}
//
				}
				else
				{
//					showMessageBox("密码错误");
					Toast.makeText(SiMiSuoThirdPassword.this, "密码错误，请重新绘制！", 1).show();
					lpwv.clearPassword();
				}
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
			 */
			@Override
			protected Map<String, Object> getDialogParams()
			{
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在初始化私密锁,请稍侯...");
				return map;
			}
		};
		monitor.executeOnMonitor(new Callable<Object>()
		{
			@Override
			public Object call() throws Exception
			{
				return getService(IPrivateSMService.class).verifyPassword(passwd);
			}
		});

	}

	private void findView()
	{
		tv_forget = (TextView) findViewById(R.id.tv_wangjipassword);

		tv_forget.setOnClickListener(this);
	}

	private void fillData()
	{

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode){
		case RESULT_OK:
//			Intent intent = new Intent(this, GDSimiContactsActvity.class);
//			startActivity(intent);
			finish();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		
		case R.id.tv_wangjipassword:
			Intent intent = new Intent(this, SiMiContactsResetPasswordActivity.class);
			startActivityForResult(intent, 100);
			break;
		}
	}

}
