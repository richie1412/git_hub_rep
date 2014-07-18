/*
 * Copyright 2010-2013 WXXR Network Technology
 * Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.constant.ChannelStaticData;
import com.wxxr.callhelper.qg.constant.ChannelStaticData.ChannelInfo;
import com.wxxr.callhelper.qg.exception.AppException;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.mobile.core.api.AbstractProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.StringUtils;

/**
 * @class desc ChannelCustomActivity.
 * @author wangxuyang
 * @version $Revision: 1.2 $
 * @created time 2013-9-18 下午6:19:32
 */
public class ChannelCustomActivity extends BaseActivity {
	private static final Trace log = Trace
			.register(ChannelCustomActivity.class);

	private class GraphItem implements OnClickListener {
		private ImageView img;
		private TextView tv_tab;
		private int selectedResourceId, unselectedResourceId;
		private boolean selected;
		private String channel;
		private String channel_desc;
		private Class<?> dialogActivity;

		public void init(boolean bool) {
			this.selected = bool;
			if (this.selected) {
				img.setBackgroundResource(selectedResourceId);
				tv_tab.setTextColor(Color.parseColor("#1b1b1b"));
			} else {
				img.setBackgroundResource(unselectedResourceId);
				tv_tab.setTextColor(Color.parseColor("#828282"));
			}
			img.setOnClickListener(this);
		}

		public void onClick(View v) {
			if (selected) {
				img.setEnabled(false);
				unsubscribeChannel();
			} else {
				img.setEnabled(false);
				if (dialogActivity != null) {
					Map<String, String> params = null;
					try {
						params = getService(IDXHZSettingService.class)
								.getSubscribedChannelParams(channel);
					} catch (Throwable t) {
						log.warn(
								"Caught exception when getSubscribedChannelParams",
								t);
						showMessageBox(t.getLocalizedMessage());
						return;
					}
					showActivity4Result(dialogActivity, params,
							new AbstractProgressMonitor() {

								/*
								 * (non-Javadoc)
								 * 
								 * @see
								 * com.wxxr.mobile.core.api.AbstractProgressMonitor
								 * # done(java.lang.Object)
								 */
								@Override
								public void done(Object arg0) {
									Intent result = (Intent) arg0;
									Map<String, String> map = new HashMap<String, String>();
									Bundle bundle = result.getExtras();
									if (!bundle.isEmpty()) {
										for (String key : bundle.keySet()) {
											String val = bundle.getString(key);
											map.put(key, val);
										}
									}
									subscribeChannel(map);
									String msg = result
											.getStringExtra("info_msg");
									if (msg != null) {
										showMessageBox(msg);
									}
								}

								/*
								 * (non-Javadoc)
								 * 
								 * @see
								 * com.wxxr.mobile.core.api.AbstractProgressMonitor
								 * # taskCanceled(boolean)
								 */
								@Override
								public void taskCanceled(boolean arg0) {
									img.setEnabled(true);
								}
							});
				} else {
					subscribeChannel(null);
				}
			}
		}

		/**
		 * 
		 */
		protected void unsubscribeChannel() {
			getService(IDXHZSettingService.class).unsubscriberChannel(channel,
					new CMProgressMonitor(ChannelCustomActivity.this) {

						@Override
						protected void handleFailed(Throwable cause) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showMessageBox("退订频道：【" + tv_tab.getText()
											+ "】失败，请稍后再试...");
									img.setEnabled(true);
								}
							});
						}

						@Override
						protected void handleDone(Object returnVal) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									img.setBackgroundResource(unselectedResourceId);
									tv_tab.setTextColor(Color
											.parseColor("#828282"));
									img.setEnabled(true);
									selected = false;
								}
							});
						}

					});
		}

		/**
		 * @param map
		 */
		protected void subscribeChannel(Map<String, String> map) {
			getService(IDXHZSettingService.class).subscribeChannel(channel,
					map, new CMProgressMonitor(ChannelCustomActivity.this) {

						@Override
						protected void handleFailed(Throwable cause) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showMessageBox("订阅频道：【" + tv_tab.getText()
											+ "】失败，请稍后再试...");
									img.setEnabled(true);
								}
							});
						}

						@Override
						protected void handleDone(Object returnVal) {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									img.setBackgroundResource(selectedResourceId);
									tv_tab.setTextColor(Color
											.parseColor("#1b1b1b"));
									img.setEnabled(true);
									selected = true;
								}
							});
						}
					});
		}

	}

	Dialog dialog;
	// View iv_cancel;
	// ViewPager mPager;
	// List<View> listViews;
	LayoutInflater mInflater;
	// LinearLayout gallery_frame;
	String username;
	private Map<String, GraphItem> items = new HashMap<String, ChannelCustomActivity.GraphItem>();
	private static Map<String, Class<?>> dialogs = new HashMap<String, Class<?>>();
	// private LinearLayout.LayoutParams gallery_layoutparams = new
	// LinearLayout.LayoutParams(
	// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	private final static String IMAGE_VIEW_TAG_PREFIX = "iv_";
	private final static String TEXT_VIEW_TAG_PREFIX = "ttv_";
	private final static int DEFAULT_SELECTED_IMAGE_RESID = R.drawable.gd_channel_default_img;
	private final static int DEFAULT_UNSELECTED_IMAGE_RESID = R.drawable.gd_channel_default_img;
	static {
		dialogs.put("FYPD", YuRRDialogActivity.class);
		dialogs.put("HZMS", SiJiaZhiXunDialogActivity.class);
	}

	int mmstyle = 0;
	// MyPagerAdapter mainAdapter;
	private ProgressBar pb_channel;
	private LinearLayout gd_ll_channel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel_custom_frame);
		mInflater = LayoutInflater.from(this);
		findView();
	}

	private void findView() {
		// mPager = (ViewPager) findViewById(R.id.vPager);
		// listViews = new ArrayList<View>();

		// iv_cancel = findViewById(R.id.iv_cancel);
		// iv_cancel.setOnClickListener(this);
		// gallery_frame = (LinearLayout) findViewById(R.id.gallery_frame);

		TextView tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("选择回执频道");
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		findViewById(R.id.gd_iv_titlebar_right).setVisibility(View.INVISIBLE);

		pb_channel = (ProgressBar) findViewById(R.id.pb_channel);
		gd_ll_channel = (LinearLayout) findViewById(R.id.gd_ll_channel);

		new AsyncTask<Void, Void, String[]>() {

//			Map<String, String> all_channels = null;

			@Override
			protected void onPreExecute() {
				pb_channel.setVisibility(View.VISIBLE);
			}

			@Override
			protected String[] doInBackground(Void... params) {

				String[] channels = null;
//				try {
//					all_channels = getService(IDXHZSettingService.class)
//							.getAllChannels();
//				} catch (Exception e) {
//					log.info("Failed to load all channels, will be set default value.");
//				}
//				if (all_channels == null || all_channels.isEmpty()) {
					channels = ChannelStaticData.getInstance()
							.getAllStaticChannelKeys();
//				} else {
//					channels = all_channels.keySet().toArray(
//							new String[all_channels.size()]);
//				}
				return channels;
			}

			@Override
			protected void onPostExecute(String[] result) {

				/* int pages = result.length / 9;
				 if (result.length % 9 != 0) {
				 pages = pages + 1;
				 }*/
				View view_page = mInflater.inflate(R.layout.channel_page, null);
				for (int i = 0; i < result.length && i < 15; i++) {
				/*	 ImageView img = new
					 ImageView(ChannelCustomActivity.this);
					 for (int j = 1; j <= 9; j++) {
					 int k = i * 9 + j;*/
					int k = i + 1;
					ImageView img_view = (ImageView) view_page
							.findViewById(getImageViewId(k));
					TextView text_view = (TextView) view_page
							.findViewById(getTextViewId(k));
					if (k > result.length) {
						img_view.setVisibility(View.INVISIBLE);
						text_view.setVisibility(View.INVISIBLE);
					} else {
						String ch = result[k - 1];
						ChannelInfo chInfo = ChannelStaticData.getInstance()
								.getStaticChannelInfo(ch);
						GraphItem item = new GraphItem();
						item.channel = ch;
//						if (all_channels != null) {
//							item.channel_desc = all_channels.get(ch);
//						}
						if (chInfo == null) {
							item.selectedResourceId = DEFAULT_SELECTED_IMAGE_RESID;
							item.unselectedResourceId = DEFAULT_UNSELECTED_IMAGE_RESID;
						} else {
							item.selectedResourceId = chInfo
									.getSelected_img_resId();
							item.unselectedResourceId = chInfo
									.getUnselected_img_resId();
							if (StringUtils.isBlank(item.channel_desc)) {
								item.channel_desc = chInfo.getDesc();
							}
						}

						item.selected = false;
						item.dialogActivity = dialogs.get(ch);
						text_view.setText(item.channel_desc);
						text_view.setTag(TEXT_VIEW_TAG_PREFIX + result[k - 1]);
						 /*img.setTag(IMAGE_VIEW_TAG_PREFIX + result[k - 1]);*/
						item.img = img_view;
						item.tv_tab = text_view;
						item.init(false);
						items.put(ch, item);
					}
					// }

					/* if (i == 0) {
					 img.setBackgroundResource(R.drawable.cycle_seleted);
					 } else {
					 img.setBackgroundResource(R.drawable.cycle_unseleted);
					 gallery_layoutparams.leftMargin = 15;
					 }
					 gallery_frame.addView(img, gallery_layoutparams);
					 listViews.add(view_page);*/
				}
				gd_ll_channel.addView(view_page);

				/**
				 * 得到选中的频道
				 */
				final CMProgressMonitor monitor = new CMProgressMonitor(
						ChannelCustomActivity.this, 1) {

					@Override
					protected void handleFailed(Throwable cause) {
						if (cause instanceof AppException) {
							showMessageBox(cause.getLocalizedMessage());
						} else {
							showMessageBox("无法下载你的频道订阅数据，请稍后再试...");
						}
						finish();
					}

					@Override
					protected void handleDone(Object returnVal) {
					}

					@Override
					protected Map<String, Object> getDialogParams() {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put(DIALOG_PARAM_KEY_TITLE, "数据加载");
						map.put(DIALOG_PARAM_KEY_MESSAGE, "正在加载频道订阅数据,请稍侯...");
						return map;
					}
				};
				monitor.executeOnMonitor(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						IDXHZSettingService service = getService(IDXHZSettingService.class);
						List<String> selectedChannels = service
								.getSubscribedChannels();
						if (selectedChannels != null) {
							for (String key : selectedChannels) {
								final GraphItem item = items.get(key);
								if (item != null) {
									runOnUiThread(new Runnable() {
										@Override
										public void run() {
											item.init(true);
										}
									});
								}
							}
						}
						return null;
					}
				});
				/* mainAdapter = new MyPagerAdapter(listViews);
				 mPager.setAdapter(mainAdapter);
				 mPager.setCurrentItem(0);
				 mPager.setOnPageChangeListener(new MyOnPageChangeListener());*/
				pb_channel.setVisibility(View.GONE);
			}

		}.execute();

	}

	private int getImageViewId(int pos) {
		switch (pos) {
		case 1:
			return R.id.iv_channel_1;
		case 2:
			return R.id.iv_channel_2;
		case 3:
			return R.id.iv_channel_3;
		case 4:
			return R.id.iv_channel_4;
		case 5:
			return R.id.iv_channel_5;
		case 6:
			return R.id.iv_channel_6;
		case 7:
			return R.id.iv_channel_7;
		case 8:
			return R.id.iv_channel_8;
		case 9:
			return R.id.iv_channel_9;
		case 10:
			return R.id.iv_channel_10;
		case 11:
			return R.id.iv_channel_11;
		case 12:
			return R.id.iv_channel_12;
		case 13:
			return R.id.iv_channel_13;
		case 14:
			return R.id.iv_channel_14;
		case 15:
			return R.id.iv_channel_15;
		default:
			return 0;
		}
	}

	private int getTextViewId(int pos) {
		switch (pos) {
		case 1:
			return R.id.ttv_channel_1;
		case 2:
			return R.id.ttv_channel_2;
		case 3:
			return R.id.ttv_channel_3;
		case 4:
			return R.id.ttv_channel_4;
		case 5:
			return R.id.ttv_channel_5;
		case 6:
			return R.id.ttv_channel_6;
		case 7:
			return R.id.ttv_channel_7;
		case 8:
			return R.id.ttv_channel_8;
		case 9:
			return R.id.ttv_channel_9;
		case 10:
			return R.id.ttv_channel_10;
		case 11:
			return R.id.ttv_channel_11;
		case 12:
			return R.id.ttv_channel_12;
		case 13:
			return R.id.ttv_channel_13;
		case 14:
			return R.id.ttv_channel_14;
		case 15:
			return R.id.ttv_channel_15;
		default:
			return 0;
		}
	}

	// public void showDialog() {
	//
	// AlertDialog.Builder builder = new Builder(ChannelCustomActivity.this);
	// View dialogview = mInflater.inflate(R.layout.pingdaoxinxi, null);
	// builder.setView(dialogview);
	// dialog = builder.create();
	// dialog.show();
	//
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gd_iv_titlebar_left:
			finish();
			break;
		}

	}

	// public class MyPagerAdapter extends PagerAdapter {
	// public List<View> mListViews;
	//
	// public MyPagerAdapter(List<View> mListViews) {
	// this.mListViews = mListViews;
	// }
	//
	// @Override
	// public void destroyItem(View arg0, int arg1, Object arg2) {
	// ((ViewPager) arg0).removeView(mListViews.get(arg1));
	// }
	//
	// @Override
	// public void finishUpdate(View arg0) {
	// }
	//
	// @Override
	// public int getCount() {
	// return mListViews.size();
	// }
	//
	// @Override
	// public Object instantiateItem(View arg0, int arg1) {
	// ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
	// return mListViews.get(arg1);
	// }
	//
	// @Override
	// public boolean isViewFromObject(View arg0, Object arg1) {
	// return arg0 == (arg1);
	// }
	//
	// @Override
	// public void restoreState(Parcelable arg0, ClassLoader arg1) {
	// }
	//
	// @Override
	// public Parcelable saveState() {
	// return null;
	// }
	//
	// @Override
	// public void startUpdate(View arg0) {
	// }
	// }

	// public class MyOnPageChangeListener implements OnPageChangeListener {
	//
	// @Override
	// public void onPageSelected(int arg0) {
	// for (int i = 0; i < gallery_frame.getChildCount(); i++) {
	// ImageView view = (ImageView) gallery_frame.getChildAt(i);
	// if (i == arg0) {
	// view.setBackgroundResource(R.drawable.cycle_seleted);
	// } else {
	// view.setBackgroundResource(R.drawable.cycle_unseleted);
	// }
	// }
	// }
	//
	// @Override
	// public void onPageScrolled(int arg0, float arg1, int arg2) {
	// Log.i("position---", arg0 + "");
	// }
	//
	// @Override
	// public void onPageScrollStateChanged(int arg0) {
	// }
	// }

}
