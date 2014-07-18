package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;		
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.adapter.IListDataProvider;
import com.wxxr.callhelper.qg.adapter.PrivateSMItemListAdapter;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.bean.TextMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.event.NewPrivateSMReceivedEvent;
import com.wxxr.callhelper.qg.event.PrivateSMDeletedEvent;
import com.wxxr.callhelper.qg.event.PrivateSMEvent;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.utils.ManagerSP;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.ResizeLayout;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.event.api.IListenerChain;
import com.wxxr.mobile.core.event.api.IStreamEvent;
import com.wxxr.mobile.core.event.api.IStreamEventListener;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 私密信息详情
 * @author yinzhen
 *
 */

public class SiMiPersonContentActivity extends BaseActivity {
	private static final Trace log = Trace
			.register(SiMiPersonContentActivity.class);
//	TextView tv_name;
//	TextView tv_address;
	// LinearLayout ll_main;
	// private Button sendsms;
//	private DisplayMetrics metric;
//	private int padding;
	
	// gd
	protected int curposition = -1;
	private TextView tv_titlename, tv_place;
	private ListView lv_main;
	private SimpleDateFormat format_time = new SimpleDateFormat("MM月dd日  HH:mm");
	private PrivateSMItemListAdapter adapter;
	private String sms_address;
	private EditText input_edittext;
	private IEventListener listener;
	private IStreamEventListener streamListener;
	private IListDataProvider<TextMessageBean> lists;
	private TextView send_text;
	private LinearLayout quickinputpanl;
	private RelativeLayout emptyline;
	private TextView downicon;
	private PopupWindow pop;
	private TextView gd_tv_menu_all, gd_tv_menu_cancel, gd_tv_menu_del;
	private SiMiPersonContentActivity activity;
	protected boolean showsoftinput;
	private View input_area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_content);
//		metric = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metric);

		// padding = (int) (metric.density * 10);
		// ll_main = (LinearLayout) findViewById(R.id.ll_mainbackground);

		// Bitmap readBitmap = Tools.readBitmap(this,
		// R.drawable.sms_content_last_bg);
		// BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
		// Drawable drawable = (Drawable)bitmapDrawable;
		// ll_main.setBackgroundDrawable(bitmapDrawable);

		findView();
		progressLogic();
	}

	private void findView() {

		if(null == activity){
			activity = this;
		}
		
		
		
		View v = findViewById(R.id.root_resizediv);

		ResizeLayout root = (ResizeLayout) v;

		root.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			
			public void OnResize(int w, int h, int oldw, int oldh) {
				// int change = ResizeLayout.BIGGER;
				if (h - oldh < -200) {
					showsoftinput=true;
				
				} else if (h - oldh > 200) {
					showsoftinput=false;
					// 键盘的高度应该会大于这个值， 删除一个汉字，也会小于这个值，所以变化在200之外才是键盘隐藏，
					// 否则是用键盘在删除文字，不用处理
				}

			}
		});
		
		
		
		
		
		
		
		
		
		
		
		
		
		// title
		findViewById(R.id.huizhiback).setOnClickListener(this);// 返回
		findViewById(R.id.titlebar_calll).setOnClickListener(this);// 电话
		tv_titlename = (TextView) findViewById(R.id.tv_titlename);//名字
		tv_place = (TextView) findViewById(R.id.tv_place);//地点
		input_area=findViewById(R.id.input_area);

		send_text = (TextView) findViewById(R.id.send_text);
		send_text.setOnClickListener(this);//发送
		input_edittext = (EditText) findViewById(R.id.input_edittext);//et
		quickinputpanl = (LinearLayout) findViewById(R.id.quick_input_pannel);
		emptyline = (RelativeLayout) findViewById(R.id.quick_input_empty_line);
		downicon = (TextView) findViewById(R.id.send_text_down);
		downicon.setOnClickListener(this);
		findViewById(R.id.quick_tel_1).setOnClickListener(this);
		findViewById(R.id.quick_tel_2).setOnClickListener(this);
		findViewById(R.id.quick_tel_3).setOnClickListener(this);
		findViewById(R.id.quick_tel_4).setOnClickListener(this);
		findViewById(R.id.quick_tel_5).setOnClickListener(this);
		findViewById(R.id.quick_cancel).setOnClickListener(this);
		
		lv_main = (ListView) findViewById(R.id.lv_sms_maincontentt);
		if(adapter == null)
			adapter = new PrivateSMItemListAdapter(this, activity, format_time);
		lv_main.setAdapter(adapter);

		Intent intent = getIntent();
		sms_address = intent.getStringExtra(Constant.PHONE_NUMBER);
		
		String sms_name = Tools.getContactsName(this, sms_address);
		if (sms_name != null) {
			tv_titlename.setText(sms_name);
			tv_titlename.setVisibility(View.VISIBLE);
		} else {
			if(sms_address!=null&&sms_address.length()>0){
			tv_titlename.setText(sms_address);
			tv_titlename.setVisibility(View.VISIBLE);
			}
		}
		
		Region  r=	getService(IGuiShuDiService.class).getRegionByDialogNumber(sms_address);
		if (r != null) {
		
			StringBuilder sb = new StringBuilder();
			if (r.getpRegionName() != null) {
				sb.append(r.getpRegionName() + " ");
			}
			if (r.getRegionName() != null) {
				sb.append(r.getRegionName() + " ");
			}
//			if (r.getBrandName() != null) {
//				sb.append(r.getBrandName());
//			}
			tv_place.setText(sb.toString());
		} else {
			tv_place.setText("未知");
		}
		
//		 findViewById(R.id.iv_callbody).setOnClickListener(this);
//		 sendsms = (Button) findViewById(R.id.bt_sms_send);
//		 ed_smscontent = (EditText) findViewById(R.id.bt_sms_content);

//		 tv_name = (TextView) findViewById(R.id.tv_client_name);

//		tv_address = (TextView) findViewById(R.id.tv_client_address);
//		tv_address.setText(sms_address);
//		TextView guishudi = (TextView) findViewById(R.id.tv_client_place);
//		int msgFlag = ManagerSP.getInstance(this).get(Constant.LOCATION_MSG, 0);
//		if (msgFlag == 0) {
//			getService(IGuiShuDiService.class).getRegionByPhoneNumber(sms_address,
//					new CMProgressMonitor(SiMiPersonContentActivity.this) {
//
//						@Override
//						protected void handleFailed(Throwable cause) {
//
//						}
//
//						@Override
//						protected void handleDone(Object returnVal) {
//							Region r = (Region) returnVal;
//							if ((r != null) && (r.getpRegionName() != null)) {
//								tv_place.setText(r.getpRegionName() + " "
//										+ r.getRegionName());
//							}
//
//						}
//					});
//		} else {
//			tv_place.setVisibility(View.GONE);
//		}

		lv_main.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				view.setBackgroundColor(getResources().getColor(
						R.color.transparent));
			}
		});
		
		
		lv_main.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Intent edit = new Intent(SiMiPersonContentActivity.this,
//						ConfirmDialogActivity.class);
//				edit.putExtra(Constant.DIALOG_KEY, Constant.DEL_ONE_CHAT_ITME);
//				startActivityForResult(edit, Constant.DEL_ONE_CHAT_ITME);
				curposition = position;
				Intent intent = new Intent(SiMiPersonContentActivity.this,
						ComingRemindDialogActivity.class);
				intent.putExtra("singleid", lists.getItem(position).getId());
				intent.putExtra("content", lists.getItem(position).getContent());
				String tel_incontent = Tools.getMisdnByContent(lists.getItem(position).getContent());
				if (tel_incontent != null) {
					intent.putExtra("number", tel_incontent);
				} else {
					intent.putExtra("number", sms_address);
				}
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人私密信息？");
				view.setBackgroundColor(getResources().getColor(
						R.color.gd_callrecord_long_press));
				startActivityForResult(intent, position);
				return true;
			}
		});

	}

	private void progressLogic() {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		input_area.setVisibility(View.VISIBLE);
		getData();
	}
	
	private void getData() {

		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {
				showMessageBox("无法加载指定的私密信息，请稍后再试...");
				finish();
			}

			@Override
			protected void handleDone(final Object result) {
				// lv_main.smoothScrollToPosition(madapter.getCount());
				runOnUiThread(new Runnable() {
					public void run() {
						if (result != null) {
							lists = (IListDataProvider<TextMessageBean>) result;
							adapter.setData(lists);
							adapter.update();
							lv_main.setSelection(lists.getItemCounts());
						}
					}
				});
			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {
			public Object call() {
				return getService(IPrivateSMService.class)
						.getMessageDataProvider(sms_address);
			}
		});
	}

	protected void deleteMessage(final TextMessageBean bean) {
		CMProgressMonitor monitor = new CMProgressMonitor(this) {
			@Override
			protected void handleFailed(Throwable cause) {
				log.warn("Failed to remove private message", cause);
				if (log.isDebugEnabled()) {
					showMessageBox("消息删除失败，原因：[" + cause.getLocalizedMessage()
							+ "]");
				} else {
					showMessageBox("消息删除失败，请稍后再试...");
				}
			}

			@Override
			protected void handleDone(Object returnVal) {
				showMessageBox("指定消息已成功删除");
			}

			/*
			 * (non-Javadoc)
			 * 
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
	
	private void setMenu() {
		View view = View.inflate(this, R.layout.menu, null);
		pop = getMenu(this, view);
		pop.showAtLocation(findViewById(R.id.rl_menu), Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
		view.findViewById(R.id.ll_delete).setOnClickListener(this);
		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
		gd_tv_menu_all = (TextView) view.findViewById(R.id.gd_tv_menu_all);
		gd_tv_menu_cancel = (TextView) view.findViewById(R.id.gd_tv_menu_cancel);
		gd_tv_menu_del = (TextView) view.findViewById(R.id.gd_tv_menu_del);
		input_area.setVisibility(View.GONE);
	}

	/**
	 * 单选 &全选状态
	 */
	public void pressAll(){
		gd_tv_menu_all.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_del.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
	}
	
	/**
	 * 初始化&取消全选状态
	 */
	public void pressCancelAll(){
		gd_tv_menu_all.setTextColor(getResources().getColor(R.color.gd_titlebar_text));
		gd_tv_menu_cancel.setTextColor(getResources().getColor(R.color.gd_item_eighty));
		gd_tv_menu_del.setTextColor(getResources().getColor(R.color.gd_item_eighty));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (adapter.getCount() > 0) {
				if (pop == null) {
					if(!showsoftinput){
					showMenu();
					}
				} else {
					hideMenu();
				}
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (pop != null && pop.isShowing()) {
				hideMenu();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);

	}
	
	private void showMenu() {
		adapter.setChecked(true);
		adapter.notifyDataSetChanged();
		setMenu();
	}

	private void hideMenu() {
		adapter.setChecked(false);
		adapter.notifyDataSetChanged();
		input_area.setVisibility(View.VISIBLE);
		if(pop!=null){
		pop.dismiss();
		pop = null;
		}
	}
	
	private boolean isDelete() {
		return adapter.getSelected().isEmpty() == false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (resultCode) {

		case Constant.DETAIL_MULT_DELETE_RECORD:

			getFramework().getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					synchronized (adapter) {
						List<TextMessageBean> selected = adapter.getSelected();
						for (TextMessageBean bb : selected) {
							deleteMessage(bb);
						}
						adapter.selectNone();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});
					}
				}
			});

			break;
			
		case Constant.LONG_DELETE_RECORD:
			getFramework().getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					synchronized (adapter) {
						TextMessageBean bean = adapter.getItem(curposition);
						if (bean != null) {
							deleteMessage(bean);
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});
					}
				}
			});

			break;
			
		}
         hideMenu();
		super.onActivityResult(requestCode, resultCode, data);
		
		
//		if (resultCode == R.id.ll_btn_confim_menuitem_1) {
//			if (curposition != -1) {
//				TextMessageBean bean = adapter.getItem(curposition);
//				deleteMessage(bean);
//			}
//		} else if (resultCode == R.id.ll_btn_confim_menuitem_2) {
//			if (curposition != -1) {
//				TextMessageBean bean = adapter.getItem(curposition);
//				ArrayList<TextMessageBean> list = new ArrayList<TextMessageBean>();
//				list.add(bean);
//				// 这就是一条，不用出进度条了
//				Tools.remove_to_sysbox.put(String.valueOf(bean.getTimestamp()),
//						"aa");
//				Tools.inserToSysmsg(this.getBaseContext(), list);
//				getService(IPrivateSMService.class).deleteMessage(bean);
//			}
//		} else if (resultCode == R.id.ll_btn_confim_menuitem_3) {
//			curposition = -1;
//		}

	}
	
	
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.send_text:

			final String msgContent = input_edittext.getText().toString();

			if ("".equals(msgContent) || msgContent == null) {
				showMessageBox("请输入短信内容");
				return;
			}
			CMProgressMonitor monitor = new CMProgressMonitor(this) {
				@Override
				protected void handleFailed(Throwable cause) {
					log.error("Caught exception when sending SM message", cause);
					showMessageBox("短信发送失败，原因：[" + cause.getLocalizedMessage()
							+ "]");
				}

				@Override
				protected void handleDone(Object returnVal) {
					// showMessageBox("短信发送成功");
					runOnUiThread(new Runnable() {
						public void run() {
							input_edittext.setText("");
							lv_main.setSelection(lv_main.getBottom());
							lv_main.setSelection(lv_main.getCount());
						}
					});

				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams()
				 */
				@Override
				protected Map<String, Object> getDialogParams() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在发送短信,请稍侯...");
					return map;
				}
			};
			monitor.executeOnMonitor(new Callable<Object>() {
				@Override
				public Object call() throws Exception {

					SmsManager mSmsManager = SmsManager.getDefault();
					ArrayList<String> messages = mSmsManager
							.divideMessage(msgContent);
					for (String message : messages) {
						mSmsManager.sendTextMessage(sms_address, null, message,
								null, null);
					}
					TextMessageBean bean = new TextMessageBean();
					bean.setContent(msgContent);
					bean.setMt(sms_address);
					bean.setNumber(sms_address);
					bean.setTimestamp(System.currentTimeMillis());
					getService(IPrivateSMService.class).onSMReceived(bean);
					return null;
				}
			});
			break;

		case R.id.huizhiback:
			finish();
			break;
			
		case R.id.titlebar_calll:
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + sms_address));
			startActivity(intent);
			break;
			
		case R.id.ll_all_choose:
			synchronized (adapter) {
				adapter.selectAll();
				adapter.notifyDataSetChanged();
			}

			break;

		case R.id.ll_cancle:
			synchronized (adapter) {
				adapter.selectNone();
				adapter.notifyDataSetChanged();
			}
			break;

		case R.id.ll_delete:
			if (isDelete()) {
				intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.DETAIL_MULT_DELETE_RECORD);
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人私密信息？");
				startActivityForResult(intent, 100);
			}
			break;
			
		case R.id.send_text_down:
			downicon.setVisibility(View.INVISIBLE);
			quickinputpanl.setVisibility(View.VISIBLE);
			emptyline.setVisibility(View.VISIBLE);
			break;
			
		case R.id.quick_tel_1:
			input_edittext.setText(((TextView)v).getText().toString());
			downicon.setVisibility(View.VISIBLE);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			
		case R.id.quick_tel_2:
			input_edittext.setText(((TextView)v).getText().toString());
			downicon.setVisibility(View.VISIBLE);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			
		case R.id.quick_tel_3:
			input_edittext.setText(((TextView)v).getText().toString());
			downicon.setVisibility(View.VISIBLE);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			
		case R.id.quick_tel_4:
			input_edittext.setText(((TextView)v).getText().toString());
			downicon.setVisibility(View.VISIBLE);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			
		case R.id.quick_tel_5:
			input_edittext.setText(((TextView)v).getText().toString());
			downicon.setVisibility(View.VISIBLE);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			
		case R.id.quick_cancel:
			downicon.setVisibility(View.VISIBLE);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			

		}

	}

	protected void tearDownListener() {
		if (log.isDebugEnabled()) {
			log.debug("tear down listener ...");
		}
		if (this.listener != null) {
			if (getService(IEventRouter.class) != null) {
				getService(IEventRouter.class).unregisterEventListener(
						PrivateSMEvent.class, listener);
			}
			this.listener = null;
		}
		if (this.streamListener != null) {
			if (getService(IEventRouter.class) != null) {
				getService(IEventRouter.class).removeListener(
						this.streamListener);
			}
			this.streamListener = null;
		}

	}

	/**
	 * 
	 */
	protected void setupListener() {
		if (log.isDebugEnabled()) {
			log.debug("setup listener ...");
		}
		this.listener = new IEventListener() {

			@Override
			public void onEvent(final IBroadcastEvent event) {
				if (log.isDebugEnabled()) {
					log.debug("Receiving event :" + event);
				}
				boolean dataChanged = false;
				if (event instanceof PrivateSMDeletedEvent) {
					PrivateSMDeletedEvent evt = (PrivateSMDeletedEvent) event;
					dataChanged = evt.getMessage().getNumber()
							.equals(sms_address);
				}
				if (dataChanged) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							adapter.notifyDataSetChanged();
						}
					});
				}
			}
		};
		this.streamListener = new IStreamEventListener() {

			@Override
			public void onEvent(IStreamEvent event, IListenerChain chain) {
				if (event instanceof NewPrivateSMReceivedEvent) {
					NewPrivateSMReceivedEvent evt = (NewPrivateSMReceivedEvent) event;
					if (evt.getMessage().getNumber().equals(sms_address)) {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								adapter.notifyDataSetChanged();
							}
						});
						return;
					}
				}
				chain.invokeNext(event);
			}
		};
		getService(IEventRouter.class).addListenerFirst(streamListener);
		getService(IEventRouter.class).registerEventListener(
				PrivateSMEvent.class, listener);
		if (this.adapter != null) {
			this.adapter.notifyDataSetChanged();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		setupListener();
		super.onStart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		setupListener();
		super.onRestart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		tearDownListener();
		super.onStop();
	}

}