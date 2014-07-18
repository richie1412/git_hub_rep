package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.exception.AppException;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.ICancellable;

public class PingDaoDingZhiActivity /*extends BaseActivity implements OnClickListener*/
{
//	private static final Trace log = Trace.register(PingDaoDingZhiActivity.class);
//	
//	private class GraphItem implements OnClickListener{
//		private ImageView img;
//		private TextView tv_tab;
//		private int selectedResourceId,unselectedResourceId;
//		private boolean selected;
//		private String channel;
//		private Class<?> dialogActivity;
//		
//		public void init(boolean bool){
//			this.selected = bool;
//			if(this.selected){
//				img.setBackgroundResource(selectedResourceId);
//				tv_tab.setTextColor(Color.parseColor("#7496B6"));
//			}else{
//				img.setBackgroundResource(unselectedResourceId);
//				tv_tab.setTextColor(Color.parseColor("#71b2ef"));
//			}
//			img.setOnClickListener(this);
//		}
//		
//		public void onClick(View v) {
//			if(selected){
//				img.setEnabled(false);
//				unsubscribeChannel();
//			}else{
//				img.setEnabled(false);
//				if(dialogActivity != null){
//					Map<String, String> params = null;
//					try {
//						params = getService(IDXHZSettingService.class).getSubscribedChannelParams(channel);
//					}catch(Throwable t){
//						log.warn("Caught exception when getSubscribedChannelParams", t);
//						showMessageBox(t.getLocalizedMessage());
//						return;
//					}
//					showActivity4Result(dialogActivity, params, new AbstractProgressMonitor() {
//
//						/* (non-Javadoc)
//						 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#done(java.lang.Object)
//						 */
//						@Override
//						public void done(Object arg0) {
//							Intent result = (Intent)arg0;
//							Map<String, String> map = new HashMap<String, String>();
//							Bundle bundle = result.getExtras();
//							if(!bundle.isEmpty()){
//								for (String key : bundle.keySet()) {
//									String val = bundle.getString(key);
//									map.put(key, val);
//								}
//							}
//							subscribeChannel(map);
//							String msg = result.getStringExtra("info_msg");
//							if(msg != null){
//								showMessageBox(msg);
//							}
//						}
//
//						/* (non-Javadoc)
//						 * @see com.wxxr.mobile.core.api.AbstractProgressMonitor#taskCanceled(boolean)
//						 */
//						@Override
//						public void taskCanceled(boolean arg0) {
//							img.setEnabled(true);
//						}
//					});
//				}else{
//					subscribeChannel(null);
//				}
//			}
//		}
//
//		/**
//		 * 
//		 */
//		protected void unsubscribeChannel() {
//			getService(IDXHZSettingService.class).unsubscriberChannel(channel,new CMProgressMonitor(PingDaoDingZhiActivity.this) {
//				
//				@Override
//				protected void handleFailed(Throwable cause) {
//					runOnUiThread(new Runnable() {							
//						@Override
//						public void run() {
//							showMessageBox("退订频道：【"+tv_tab.getText()+"】失败，请稍后再试...");
//							img.setEnabled(true);
//						}
//					});
//				}
//				
//				@Override
//				protected void handleDone(Object returnVal) {
//					runOnUiThread(new Runnable() {							
//						@Override
//						public void run() {
//							img.setBackgroundResource(unselectedResourceId);
//							tv_tab.setTextColor(Color.parseColor("#71b2ef"));
//							img.setEnabled(true);
//							selected = false;
//						}
//					});
//				}
//				
//			});
//		}
//		
//		/**
//		 * @param map
//		 */
//		protected void subscribeChannel(Map<String, String> map) {
//			getService(IDXHZSettingService.class).subscribeChannel(channel,map,new CMProgressMonitor(PingDaoDingZhiActivity.this) {
//				
//				@Override
//				protected void handleFailed(Throwable cause) {
//					runOnUiThread(new Runnable() {							
//						@Override
//						public void run() {
//							showMessageBox("订阅频道：【"+tv_tab.getText()+"】失败，请稍后再试...");
//							img.setEnabled(true);
//						}
//					});						
//				}
//				
//				@Override
//				protected void handleDone(Object returnVal) {
//					runOnUiThread(new Runnable() {							
//						@Override
//						public void run() {
//							img.setBackgroundResource(selectedResourceId);
//							tv_tab.setTextColor(Color.parseColor("#7496B6"));
//							img.setEnabled(true);
//							selected = true;
//						}
//					});
//				}
//			});
//		}
//
//	}
//	
//	Dialog dialog;
//	ImageView iv_cancel;
//	ViewPager mPager;
//	List<View> listViews;
//	LayoutInflater mInflater;
//	ImageView image1;
//	ImageView image2;
//	String username;
//	private Map<String, GraphItem> items = new HashMap<String, PingDaoDingZhiActivity.GraphItem>();
//	private static Map<String, Class<?>> dialogs = new HashMap<String, Class<?>>();
//	
//	static {
//		dialogs.put("FYPD", YuRRDialogActivity.class);
//		dialogs.put("HZMS", SiJiaZhiXunDialogActivity.class);
//	}
//	public static String[] channels = new String[]
//	{ "JKPD", "GATY", "CJPD", "GZSM", "MRPD", "SMPD", "JYPD", "TYPD", "XHPD", "MSTD", "TXPD", "FCPD", "JRRD", "FYPD",
//			"HZMS" };
//	
//	private static int[] imgResourceIds = new int[]
//	{ 
//		R.id.iv_pingdao_tab1, 
//		R.id.iv_pingdao_tab2,
//		R.id.iv_pingdao_tab3,
//		R.id.iv_pingdao_tab4,
//		R.id.iv_pingdao_tab5,
//		R.id.iv_pingdao_tab6,
//		R.id.iv_pingdao_tab7,
//		R.id.iv_pingdao_tab8,
//		R.id.iv_pingdao_tab9,
//		R.id.iv_pingdao_tab10,
//		R.id.iv_pingdao_tab11,
//		R.id.iv_pingdao_tab12,
//		R.id.iv_pingdao_tab13,
//		R.id.iv_pingdao_tab14,
//		R.id.iv_pingdao_tab15
//    };
//	
//	private static int[] textViewResourceIds = new int[]
//	{ 
//		R.id.ttv_pingdao_tab1, 
//		R.id.ttv_pingdao_tab2,
//		R.id.ttv_pingdao_tab3,
//		R.id.ttv_pingdao_tab4,
//		R.id.ttv_pingdao_tab5,
//		R.id.ttv_pingdao_tab6,
//		R.id.ttv_pingdao_tab7,
//		R.id.ttv_pingdao_tab8,
//		R.id.ttv_pingdao_tab9,
//		R.id.ttv_pingdao_tab10,
//		R.id.ttv_pingdao_tab11,
//		R.id.ttv_pingdao_tab12,
//		R.id.ttv_pingdao_tab13,
//		R.id.ttv_pingdao_tab14,
//		R.id.ttv_pingdao_tab15
//    };
//	
//	private static int[] unselectedResourceIds = new int[]
//	{ 
//		R.drawable.pingdao1, 
//		R.drawable.pingdao2,
//		R.drawable.pingdao3,
//		R.drawable.pingdao4,
//		R.drawable.pingdao5,
//		R.drawable.pingdao6,
//		R.drawable.pingdao7,
//		R.drawable.pingdao8,
//		R.drawable.pingdao9,
//		R.drawable.pingdao10,
//		R.drawable.pingdao11,
//		R.drawable.pingdao12,
//		R.drawable.pingdao13,
//		R.drawable.pingdao14,
//		R.drawable.pingdao15
//    };
//
//	private static int[] selectedResourceIds = new int[]
//	{ 
//		R.drawable.pingdaoh1, 
//		R.drawable.pingdaoh2,
//		R.drawable.pingdaoh3,
//		R.drawable.pingdaoh4,
//		R.drawable.pingdaoh5,
//		R.drawable.pingdaoh6,
//		R.drawable.pingdaoh7,
//		R.drawable.pingdaoh8,
//		R.drawable.pingdaoh9,
//		R.drawable.pingdaoh10,
//		R.drawable.pingdaoh11,
//		R.drawable.pingdaoh12,
//		R.drawable.pingdaoh13,
//		R.drawable.pingdaoh14,
//		R.drawable.pingdaoh15
//    };
//
//
//
//	int mmstyle = 0;
//	MyPagerAdapter mainAdapter;
//	@Override
//	protected void onCreate(Bundle savedInstanceState)
//	{
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.pingdaodingzhi);
//		mInflater = LayoutInflater.from(this);
//		findView();		
//	}
//
//	private void findView()
//	{
//		mPager = (ViewPager) findViewById(R.id.vPager);
//		listViews = new ArrayList<View>();
//
//		iv_cancel = (ImageView) findViewById(R.id.iv_cancel);
//
//		image1 = (ImageView) findViewById(R.id.gallery_iv1);
//		image2 = (ImageView) findViewById(R.id.gallery_iv2);
//
//		View view1 = mInflater.inflate(R.layout.pingdao_item1, null);
//		View view2 = mInflater.inflate(R.layout.pingdao_item2, null);
//		listViews.add(view1);
//		listViews.add(view2);
//
//		for(int i=0 ; i < channels.length ; i++){
//			String ch = channels[i];
//			int imgId = imgResourceIds[i];
//			int selectedResId = selectedResourceIds[i];
//			int unselectedResId = unselectedResourceIds[i];
//			int textViewResId = textViewResourceIds[i];
//			GraphItem item = new GraphItem();
//			item.channel = ch;
//			item.img = (ImageView) view1.findViewById(imgId);
//			if(item.img == null){
//				item.img = (ImageView) view2.findViewById(imgId);
//			}
//			item.tv_tab = (TextView) view1.findViewById(textViewResId);
//			if(item.tv_tab == null){
//				item.tv_tab = (TextView) view2.findViewById(textViewResId);
//			}
//			item.selected = false;
//			item.selectedResourceId = selectedResId;
//			item.unselectedResourceId = unselectedResId;
//			item.dialogActivity = dialogs.get(ch);
//			item.init(false);
//			items.put(ch, item);
//		}
//		final CMProgressMonitor monitor = new CMProgressMonitor(this,1) {
//			
//			@Override
//			protected void handleFailed(Throwable cause) {
//				if(cause instanceof AppException){
//					showMessageBox(cause.getLocalizedMessage());
//				}else{
//					showMessageBox("无法下载你的频道订阅数据，请稍后再试...");
//				}
//				finish();
//			}
//			
//			@Override
//			protected void handleDone(Object returnVal) {
//			}
//
//			/* (non-Javadoc)
//			 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
//			 */
//			@Override
//			protected Map<String, Object> getDialogParams() {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put(DIALOG_PARAM_KEY_TITLE, "数据加载");
//				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在加载频道订阅数据,请稍侯...");
//				return map;
//			}
//		};
//		monitor.executeOnMonitor(new Callable<Object>(){
//			@Override
//			public Object call() throws Exception{
//					IDXHZSettingService service = getService(IDXHZSettingService.class);
//					List<String> selectedChannels = service.getSubscribedChannels();
//					if(selectedChannels != null){
//						for (String key : selectedChannels) {
//							final GraphItem item = items.get(key);
//							if(item != null){
//								runOnUiThread(new Runnable() {
//									@Override
//									public void run() {
//										item.init(true);
//									}
//								});
//							}
//						}
//					}
//					return null;
//			}
//		});
//		iv_cancel.setOnClickListener(this);	
//		mainAdapter = new MyPagerAdapter(listViews);		
//		mPager.setAdapter(mainAdapter);	
//		mPager.setCurrentItem(0);
//		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
//	}
//
//	public void showDialog()
//	{
//
//		AlertDialog.Builder builder = new Builder(PingDaoDingZhiActivity.this);
//		View dialogview = mInflater.inflate(R.layout.pingdaoxinxi, null);
//		builder.setView(dialogview);
//
//		// Button bsure = (Button)
//		// dialogview.findViewById(R.id.bt_registersuccess);
//		// bsure.setOnClickListener(new OnClickListener()
//		// {
//		//
//		// @Override
//		// public void onClick(View v)
//		// {
//		//
//		// dialog.dismiss();
//		//
//		// finish();
//		//
//		// }
//		// });
//		dialog = builder.create();
//		dialog.show();
//
//	}
//
//	@Override
//	public void onClick(View v)
//	{
//		switch (v.getId())
//		{
//		case R.id.iv_cancel:
//
//			finish();
//
//			break;
//		}
//
//	}
//
//
//	public class MyPagerAdapter extends PagerAdapter
//	{
//		public List<View> mListViews;
//
//		public MyPagerAdapter(List<View> mListViews)
//		{
//			this.mListViews = mListViews;
//		}
//
//		@Override
//		public void destroyItem(View arg0, int arg1, Object arg2)
//		{
//			((ViewPager) arg0).removeView(mListViews.get(arg1));
//		}
//
//		@Override
//		public void finishUpdate(View arg0)
//		{
//		}
//
//		@Override
//		public int getCount()
//		{
//			return mListViews.size();
//		}
//
//		@Override
//		public Object instantiateItem(View arg0, int arg1)
//		{
//			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
//			return mListViews.get(arg1);
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1)
//		{
//			return arg0 == (arg1);
//		}
//
//		@Override
//		public void restoreState(Parcelable arg0, ClassLoader arg1)
//		{
//		}
//
//		@Override
//		public Parcelable saveState()
//		{
//			return null;
//		}
//
//		@Override
//		public void startUpdate(View arg0)
//		{
//		}
//	}
//
//	public class MyOnPageChangeListener implements OnPageChangeListener
//	{
//
//		@Override
//		public void onPageSelected(int arg0)
//		{
//			// Animation animation = null;
//			switch (arg0)
//			{
//			case 0:
//				image1.setBackgroundResource(R.drawable.select_dian);
//				image2.setBackgroundResource(R.drawable.unselect_dian);
//
//				break;
//			case 1:
//				image2.setBackgroundResource(R.drawable.select_dian);
//				image1.setBackgroundResource(R.drawable.unselect_dian);
//
//				break;
//
//			}
//
//		}
//
//		@Override
//		public void onPageScrolled(int arg0, float arg1, int arg2)
//		{
//			Log.i("position---", arg0 + "");
//		}
//
//		@Override
//		public void onPageScrollStateChanged(int arg0)
//		{
//		}
//	}

}
