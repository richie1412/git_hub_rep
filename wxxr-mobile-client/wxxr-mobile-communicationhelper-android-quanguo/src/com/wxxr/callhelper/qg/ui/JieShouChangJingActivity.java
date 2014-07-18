package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.exception.AppException;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

public class JieShouChangJingActivity extends BaseActivity implements OnClickListener
{
	private static final Trace log = Trace.register(JieShouChangJingActivity.class);
	
	private static final Integer TX_MODE = 2;
	private static final Integer QK_MODE = 3;
    TextView tv_title;
    RelativeLayout iv_finish;
    RelativeLayout bt_quankai;
    RelativeLayout bt_tixing;
    TextView lable_dqms;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.jieshouchangjing2);
		iv_finish = (RelativeLayout) findViewById(R.id.iv_finish);
		tv_title = (TextView) findViewById(R.id.tv_xuanzechangjing);
		bt_quankai = (RelativeLayout) findViewById(R.id.bt_quankaimm);
		bt_tixing = (RelativeLayout) findViewById(R.id.bt_tixingmm);
		lable_dqms=(TextView) findViewById(R.id.lable_dqms);
		
		iv_finish.setOnClickListener(this);
		bt_quankai.setOnClickListener(this);
		bt_tixing.setOnClickListener(this);
		
		Tools.changeTextViewBold(tv_title);
		updateMode();
	}
	
	
	private void updateMode() {
		final CMProgressMonitor monitor = new CMProgressMonitor(this,1) {
			
			@Override
			protected void handleFailed(Throwable cause) {
				if(cause instanceof AppException){
					showMessageBox(cause.getLocalizedMessage());
				}else{
					if(log.isDebugEnabled()){
						showMessageBox("无法下载你的模式设置，原因：【"+cause.getLocalizedMessage()+"】");
					}else{
						showMessageBox("无法下载你的模式设置，请稍后再试...");
					}
				}
				finish();
			}
			
			@Override
			protected void handleDone(final Object returnVal) {
				runOnUiThread(new Runnable() {							
					@Override
					public void run() {
						Integer mode = (Integer)returnVal;
							if(TX_MODE.equals(mode)){
								setTXUIMode();
							}else{
								setQKUIMode();
							}
					}});
			}

			/* (non-Javadoc)
			 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
			 */
			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "数据加载");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在加载频道订阅数据,请稍侯...");
				return map;
			}
		};
		monitor.executeOnMonitor(new Callable<Object>(){
			@Override
			public Object call() throws Exception{
					IDXHZSettingService service = getService(IDXHZSettingService.class);
					return service.getReceivingMode();
			}
		});

	}
	private void setQKUIMode() {
    	bt_quankai.setBackgroundColor(Color.parseColor("#D1EDFF"));
    	bt_tixing.setBackgroundColor(Color.parseColor("#ffffff"));
    	lable_dqms.setText("您当前的模式为全开模式");

	}
	
	private void setTXUIMode() {
		bt_quankai.setBackgroundColor(Color.parseColor("#ffffff"));
    	bt_tixing.setBackgroundColor(Color.parseColor("#D1EDFF"));
    	lable_dqms.setText("您当前的模式为提醒模式");
		
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.iv_finish:
			finish();
			break;
        case R.id.bt_quankaimm:
        	updateMode(QK_MODE,"全开模式");
			break;
         case R.id.bt_tixingmm:
        	updateMode(TX_MODE,"提醒模式");
			break;
		}
	}

	/**
	 * 
	 */
	protected void updateMode(final Integer mode,final String modeName) {
		getService(IDXHZSettingService.class).setReceivingMode(
				mode, new CMProgressMonitor(JieShouChangJingActivity.this) {
			
			@Override
			protected void handleFailed(Throwable cause) {
				runOnUiThread(new Runnable() {							
					@Override
					public void run() {
						showMessageBox(modeName+"设置失败，请稍后再试...");
					}
				});
			}
			
			@Override
			protected void handleDone(Object returnVal) {
				runOnUiThread(new Runnable() {							
					@Override
					public void run() {
						if(QK_MODE.equals(mode)){
							setQKUIMode();
						}else{
							setTXUIMode();
						}
			        	showMessageBox("您已成功设置"+modeName);
					}
				});
			}
		});
	}
}
