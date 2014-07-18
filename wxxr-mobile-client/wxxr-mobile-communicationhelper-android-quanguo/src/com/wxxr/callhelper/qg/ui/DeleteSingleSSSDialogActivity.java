package com.wxxr.callhelper.qg.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.mobile.core.log.api.Trace;

public class DeleteSingleSSSDialogActivity extends BaseActivity
{
	private static final Trace log = Trace.register(DeleteSingleSSSDialogActivity.class);
	
	private TextMessageBean bean;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delete_single_dialog);
		
		
		Intent intent = getIntent();
		
		bean = (TextMessageBean)intent.getSerializableExtra("message");
		if(bean == null){
			finish();
		}
		findViewById(R.id.iv_finish).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		findViewById(R.id.bt_sure).setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				CMProgressMonitor monitor = new CMProgressMonitor(DeleteSingleSSSDialogActivity.this) {			
					@Override
					protected void handleFailed(Throwable cause) {
						log.warn("Failed to remove private message", cause);
						if(log.isDebugEnabled()){
							showMessageBox("消息删除失败，原因：["+cause.getLocalizedMessage()+"]");
						}else{
							showMessageBox("消息删除失败，请稍后再试...");
						}
					}			
					@Override
					protected void handleDone(Object returnVal) {
						showMessageBox("指定消息已成功删除");
						DeleteSingleSSSDialogActivity.this.finish();
					}
					/* (non-Javadoc)
					 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
					 */
					@Override
					protected Map<String, Object> getDialogParams() {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
						map.put(DIALOG_PARAM_KEY_MESSAGE, "正在删除私密消息,请稍侯...");
						return map;
					}
				};
				monitor.executeOnMonitor(new Callable<Object>() {				
					@Override
					public Object call() throws Exception {
						getService(IPrivateSMService.class).deleteMessage(bean);
						return null;
					}
				});
			
				
			}
		});

	}
}
