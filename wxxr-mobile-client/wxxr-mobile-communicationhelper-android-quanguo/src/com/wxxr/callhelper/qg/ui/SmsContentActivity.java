package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.security.auth.login.LoginException;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.adapter.ContentAdapter;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.constant.Sms;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.ResizeLayout;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 漏接电话 详细页面
 * 
 * @author zhengjincheng
 * 
 */
public class SmsContentActivity extends BaseActivity {
	Trace log = Trace.register(SmsContentActivity.class);
	TextView tv_name;
	TextView tv_address;
	LinearLayout ll_main;
	int type = 0;
	String sms_address;
	ListView lv_main;
	SimpleDateFormat format_time = new SimpleDateFormat("MM月dd日  HH:mm");
	ArrayList<BodyBean> arraymain;
	ContentAdapter madapter;
	TextView sendsms;
	EditText ed_smscontent;
	LouHuaDao ldao;
	DisplayMetrics metric;
	int padding;
	SimpleDateFormat format_month = new SimpleDateFormat("MM");
	String sms_month;
	//LinearLayout sms_relative;
	private PopupWindow pop;
	private boolean showsoftinput=false;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				madapter.notifyDataSetChanged();
				lv_main.setSelection(lv_main.getBottom());
				lv_main.setSelection(lv_main.getCount());
				break;

			}
		}
	};
	private View quickinputpanl;
	private View downicon;
	private View emptyline;
	private SmsContentActivity activity;
	private TextView gd_tv_menu_all, gd_tv_menu_cancel, gd_tv_menu_del;
	private View input_area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_content);
		if(null == activity){
			activity = this;
		}
		
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		padding = (int) (metric.density * 10);
		ldao = LouHuaDao.getInstance(this);

		arraymain = new ArrayList<BodyBean>();
//		ll_main = (LinearLayout) findViewById(R.id.ll_mainbackground);
//		Bitmap readBitmap = Tools.readBitmap(this,
//				R.drawable.sms_content_last_bg);
//		BitmapDrawable bitmapDrawable = new BitmapDrawable(readBitmap);
//		ll_main.setBackgroundDrawable(bitmapDrawable);
		Intent intent = getIntent();
		sms_month = intent.getStringExtra("smsmonth");
		sms_address = intent.getStringExtra(Constant.PHONE_NUMBER);
		// //收到信息刚好位于详情界面，置为已读
		// ActivityManager am = (ActivityManager)
		// this.getSystemService(Context.ACTIVITY_SERVICE);
		// ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		// String currentClassName = cn.getClassName();
		// if
		// (currentClassName.equals("com.wxxr.callhelper.ui.SmsContentActivity"))
		// {
		// HuiZhiBaoGaoDao.getInstance(getApplicationContext()).update(sms_address,
		// bb.cdate, 0);
		// }
		findView();

		getData();

		// handler = new Handler() {
		// @Override
		// public void handleMessage(Message msg) {
		// madapter.notifyDataSetChanged();
		// lv_main.setSelection(lv_main.getBottom());
		// lv_main.setSelection(lv_main.getCount());
		// }
		// };

	}

	public void getData() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (madapter) {
					arraymain.clear();
					ArrayList<BodyBean> content1 = ldao.queryAllByNumFromContent(sms_address);
					ArrayList<BodyBean> content2 = ldao
							.queryAllLouHua_Send(sms_address);
					arraymain.addAll(content1);
					arraymain.addAll(content2);

					Collections.sort(arraymain, new SortByTime());

					// Message msg = handler.obtainMessage();
					// handler.sendMessage(msg);
					Message msg = handler.obtainMessage();
					msg.what = 0;
					handler.sendMessage(msg);

				}
			}
		}).start();

	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	//
	// if (resultCode == 188) {
	// getData();
	// }
	//
	// super.onActivityResult(requestCode, resultCode, data);
	// }

	@Override
	protected void onResume() {
		// getData();
		super.onResume();
		madapter.notifyDataSetChanged();
	}

	private void findView() {
		
		
		View v = findViewById(R.id.root_resizediv);

		input_area=v.findViewById(R.id.input_area);
		
		ResizeLayout root = (ResizeLayout) v;

		root.setOnResizeListener(new ResizeLayout.OnResizeListener() {

			

			public void OnResize(int w, int h, int oldw, int oldh) {
				// int change = ResizeLayout.BIGGER;
				if (h - oldh < -200) {
					showsoftinput=true;
					findViewById(R.id.send_text_down).setBackgroundResource(R.drawable.quick_btn_pre);
				} else if (h - oldh > 200) {
					showsoftinput=false;
					// 键盘的高度应该会大于这个值， 删除一个汉字，也会小于这个值，所以变化在200之外才是键盘隐藏，
					// 否则是用键盘在删除文字，不用处理
					findViewById(R.id.send_text_down).setBackgroundResource(R.drawable.quick_btn);
				
				}

			}
		});
		
		
		
		findViewById(R.id.titlebar_calll).setOnClickListener(this);
		findViewById(R.id.huizhiback).setOnClickListener(this);
		
		findsendboxviews();
		
	
		
	//	sms_relative = (LinearLayout) findViewById(R.id.bt_sms_relative);
		Intent intent = getIntent();
		String tstyle = intent.getStringExtra("huadong");
//		if (tstyle != null && tstyle.equals("sliding")) {
//			sms_relative.setFocusable(true);
//			sms_relative.setFocusableInTouchMode(true);
//			sms_relative.requestFocus();
//		}
		tv_name = (TextView) findViewById(R.id.tv_titlename);
		String sms_name = Tools.getContactsName(this, sms_address);
		if (sms_name != null) {
			tv_name.setText(sms_name);
			tv_name.setVisibility(View.VISIBLE);
		} else {
			tv_name.setText(sms_address);
		}

		
		findViewById(R.id.tv_place);
		tv_address = (TextView) findViewById(R.id.tv_place);
		tv_address.setText(sms_address);
		
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
			tv_address.setText(sb.toString());
		} else {
			tv_address.setText("未知");
		}
		
		findViewById(R.id.text_notuse).requestFocus();
		
	//	Tools.changeTextViewBold(tv_name);

	//	final TextView guishudi = (TextView) findViewById(R.id.tv_client_place);
//		int msgFlag = ManagerSP.getInstance(this).get(Constant.LOCATION_MSG, 0);
//		if (msgFlag == 0) {
//			getService(IGuiShuDiService.class).getRegionByPhoneNumber(
//					sms_address,
//					new CMProgressMonitor(SmsContentActivity.this) {
//
//						@Override
//						protected void handleFailed(Throwable cause) {
//
//						}
//
//						@Override
//						protected void handleDone(Object returnVal) {
//							Region r = (Region) returnVal;
//							if (r != null && r.getpRegionName() != null) {
//								guishudi.setText(r.getpRegionName() + " "
//										+ r.getRegionName());
//							}
//
//						}
//					});
//
//		} else {
//			guishudi.setVisibility(View.GONE);
//		}

		lv_main = (ListView) findViewById(R.id.lv_sms_maincontentt);
		madapter = new ContentAdapter(this, activity, arraymain, format_time);

		lv_main.setAdapter(madapter);
		
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

				Intent intent = new Intent(SmsContentActivity.this,
						ComingRemindDialogActivity.class);
				intent.putExtra("singleid", arraymain.get(position).getBodyID());
				intent.putExtra("content", arraymain.get(position).getContent());
				String tel_incontent = Tools.getMisdnByContent(arraymain.get(
						position).getContent());
				if (tel_incontent != null) {
					intent.putExtra("number", tel_incontent);
				} else {
					intent.putExtra("number", sms_address);
				}
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人漏接电话信息？");
				view.setBackgroundColor(getResources().getColor(
						R.color.gd_callrecord_long_press));
				startActivityForResult(intent, position);
				// intent.putExtra("louhua", true);
				// intent.setClass(SmsContentActivity.this,
				// DeleteSingleSMSDialogActivity.class);
				// startActivityForResult(intent, 200);

				return false;
			}
		});

	}

	private void findsendboxviews() {
		sendsms = (TextView)findViewById(R.id.send_text);
		sendsms.setOnClickListener(this);
		ed_smscontent = (EditText) findViewById(R.id.input_edittext);
		
		quickinputpanl=findViewById(R.id.quick_input_pannel);
		emptyline=findViewById(R.id.quick_input_empty_line);
		downicon=findViewById(R.id.send_text_down);
		downicon.setVisibility(View.VISIBLE);
	
		sendsms.setVisibility(View.VISIBLE);
		findViewById(R.id.temp_topdiv).setVisibility(View.VISIBLE);
		findViewById(R.id.input_area00).setVisibility(View.VISIBLE);
		
		
		downicon.setOnClickListener(this);
		findViewById(R.id.quick_tel_1).setOnClickListener(this);
		findViewById(R.id.quick_tel_2).setOnClickListener(this);
		findViewById(R.id.quick_tel_3).setOnClickListener(this);
		findViewById(R.id.quick_tel_4).setOnClickListener(this);
		findViewById(R.id.quick_tel_5).setOnClickListener(this);
		findViewById(R.id.quick_cancel).setOnClickListener(this);
		findViewById(R.id.input_edittext).setOnClickListener(this);
		
	}

	class SortByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			BodyBean s1 = (BodyBean) o1;
			BodyBean s2 = (BodyBean) o2;

			Date date1 = new Date(s1.getCdate());
			Date date2 = new Date(s2.getCdate());

			if (date2.after(date1))
				return -1;// 由大到小
			else
				return 1;// 由小到大

		}
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
			if (madapter.getCount() > 0) {
				if (pop == null) {
					if((!showsoftinput)&&quickinputpanl.getVisibility()==View.GONE){
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
			}else if(quickinputpanl.getVisibility()==View.VISIBLE){
				hideQuickInput();
				return true;
			}
		}

		return super.onKeyDown(keyCode, event);

	}

	private void showMenu() {
		madapter.setChecked(true);
		madapter.notifyDataSetChanged();
		setMenu();
		if(showsoftinput){
			Tools.toggleSoftInput(this, ed_smscontent.getWindowToken());
		}
		input_area.setVisibility(View.INVISIBLE);
	}

	private void hideMenu() {
		madapter.setChecked(false);
		madapter.notifyDataSetChanged();
		if(pop!=null){
		pop.dismiss();
		pop = null;
		}
		input_area.setVisibility(View.VISIBLE);
	}

	private boolean isDelete() {
		return madapter.getSelected().isEmpty() == false;
	}

	@Override
	protected void onActivityResult(final int requestCode, int resultCode,
			final Intent data) {
		switch (resultCode) {

		case Constant.DETAIL_MULT_DELETE_RECORD:

			getFramework().getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					synchronized (madapter) {
						List<BodyBean> selected = madapter.getSelected();
						for (BodyBean bb : selected) {
							ldao.deleteSMSLouHua(bb.getBodyID());
							madapter.remove(bb);
						}
						madapter.selectNone();
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								madapter.notifyDataSetChanged();
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
					synchronized (madapter) {
						BodyBean bb = (BodyBean) madapter.getItem(requestCode);
						if (bb != null) {
							ldao.deleteSMSLouHua(bb.getBodyID());
							madapter.remove(bb);
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								madapter.notifyDataSetChanged();
							}
						});
					}
				}
			});

			break;
			
			
//		case Constant.LONG_COPY_RECORD:
			
//			Tools.copyToClipboard(this, ((BodyBean) madapter.getItem(requestCode)).getContent());
			
      //     Toast.makeText(this, ((BodyBean) madapter.getItem(requestCode)).getContent(), 1).show();

//			break;
			
			
		}
         hideMenu();
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
//		case R.id.input_edittext:
//			if(quickinputpanl.getVisibility()!=View.VISIBLE){
//				ed_smscontent.setInputType(InputType.TYPE_CLASS_TEXT);
//				Tools.showSoftInput(this, ed_smscontent.getWindowToken());				
//			}			
//			break;
		
		case R.id.send_text:

			final String msgContent = ed_smscontent.getText().toString();

			if ("".equals(msgContent) || msgContent == null) {
				Toast.makeText(this, "请输入短信内容", Toast.LENGTH_SHORT).show();
				return;
			}
			sendsms.setTextColor(getResources().getColor(R.color.gd_detail_sendcolorpress));
			new CMProgressMonitor(this) {
				@Override
				protected Map<String, Object> getDialogParams() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(DIALOG_PARAM_KEY_TITLE, "提示：");
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在发送短信,请稍侯...");
					return map;
				}

				@Override
				protected void handleFailed(final Throwable cause) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							sendsms.setTextColor(getResources().getColor(R.color.gd_detail_sendcolor));
							InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(
									ed_smscontent.getWindowToken(), 0);
							if (cause != null
									&& cause instanceof LoginException) {
								LoginException e = (LoginException) cause;
								showMessageBox("短信发送失败" + cause.getMessage());

							} else {
								showMessageBox("短信发送失败，请稍后再试");
							}
						}
					});
				}

				@Override
				protected void handleDone(Object returnVal) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// todo 跳回首页
							sendsms.setTextColor(getResources().getColor(R.color.gd_detail_sendcolor));
							Uri uri = Sms.Sent.CONTENT_URI;
							ContentValues values = new ContentValues();
							values.put(Sms.ADDRESS, sms_address);
							values.put(Sms.BODY, msgContent);
							getContentResolver().insert(uri, values);

							BodyBean bb = new BodyBean();
							bb.setAddress(sms_address);
							bb.setContent(msgContent);
							bb.setCdate(System.currentTimeMillis());
							bb.setMstyle(1);
							String mmonth = format_month.format(bb.getCdate());
							bb.setAmonth(mmonth);
							ldao.insert(bb);

							getData();
							ed_smscontent.setText("");
//							sms_relative.setFocusable(true);
//							sms_relative.setFocusableInTouchMode(true);
//							sms_relative.requestFocus();
							// showMessageBox("短信发送成功");
						}
					});
				}
			}.executeOnMonitor(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					SmsManager mSmsManager = SmsManager.getDefault();
					ArrayList<String> parts = mSmsManager
							.divideMessage(msgContent);
					int numParts = parts.size();
					if (numParts == 1) {
						mSmsManager.sendTextMessage(sms_address, null,
								msgContent, null, null);
					} else {
						mSmsManager.sendMultipartTextMessage(sms_address, null,
								parts, null, null);
					}
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
          if(arraymain.size()>0){
			String tel_incontent = Tools.getMisdnByContent(arraymain.get(
					arraymain.size() - 1).getContent());
			if (tel_incontent != null) {
				intent.setData(Uri.parse("tel:" + tel_incontent));
			} else {
				intent.setData(Uri.parse("tel:" + sms_address));
			}
          }else{
        	  intent.setData(Uri.parse("tel:" + sms_address));
          }
			startActivity(intent);
			break;

		case R.id.ll_all_choose:
			synchronized (madapter) {
				madapter.selectAll();
				madapter.notifyDataSetChanged();
			}

			break;

		case R.id.ll_cancle:
			synchronized (madapter) {
				madapter.selectNone();
				madapter.notifyDataSetChanged();
			}
			break;

		case R.id.ll_delete:
			if (isDelete()) {
				intent = new Intent(this, ConfirmDialogActivity.class);
				intent.putExtra(Constant.DIALOG_KEY, Constant.DETAIL_MULT_DELETE_RECORD);
				
				intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人漏接电话信息？");
				startActivityForResult(intent, 100);
			}
			break;
			
			
		case R.id.send_text_down:
			if(!showsoftinput){
				ed_smscontent.setInputType(InputType.TYPE_NULL);
			downicon.setBackgroundResource(R.drawable.quick_btn_preup);
			quickinputpanl.setVisibility(View.VISIBLE);
			emptyline.setVisibility(View.VISIBLE);
			}
			break;
			
		case R.id.quick_tel_1:		
		case R.id.quick_tel_2:		
		case R.id.quick_tel_3:		
		case R.id.quick_tel_4:			
		case R.id.quick_tel_5:
			ed_smscontent.setText(((TextView)v).getText().toString());
			ed_smscontent.setInputType(InputType.TYPE_CLASS_TEXT);
			downicon.setBackgroundResource(R.drawable.quick_btn);
			quickinputpanl.setVisibility(View.GONE);
			emptyline.setVisibility(View.GONE);
			break;
			
		case R.id.quick_cancel:
			hideQuickInput();
			
			break;
			

		}

	}
//隐藏 快捷输入的的页面
	private void hideQuickInput() {
		ed_smscontent.setInputType(InputType.TYPE_CLASS_TEXT);
		downicon.setBackgroundResource(R.drawable.quick_btn);
		quickinputpanl.setVisibility(View.GONE);
		emptyline.setVisibility(View.GONE);
		
	}

}
