package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.adapter.GDPrivateContactsAdapter;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.event.NumberAddedEvent;
import com.wxxr.callhelper.qg.event.PrivateSMEvent;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.AbstractFragment;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.ImprorDataProcessDialog;
import com.wxxr.callhelper.qg.ui.SiMiPersonContentActivity;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.event.api.IListenerChain;
import com.wxxr.mobile.core.event.api.IStreamEvent;
import com.wxxr.mobile.core.event.api.IStreamEventListener;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 私密联系人
 * @author yinzhen
 *
 */
public class GDPrivateContactsFragment extends AbstractFragment {
	
	private static final Trace log = Trace
			.register(GDPrivateContactsFragment.class);
	private GDPrivateContactsAdapter adapter;
	private GDSimiContactsActvity mContext;
	private List<PrivateSMSummary> lists, selectedLists;
	private ListView gd_lv_private_contacts;
	private LinearLayout gd_ll_private_contacts_empty;
	private PopupWindow pop;
	private IEventListener listener;
	private IStreamEventListener streamListener;
	private boolean askimport = false;
	private boolean stoplistendata = false;// 导入系统短信期间，不要监听，数据的改变了，
	private boolean needAddPerson;
	private ArrayList<String> addperson = new ArrayList<String>();
	private Hashtable<String, String> cacahelastperson = new Hashtable<String, String>();
	private String telphone;
	private View mView;

	public GDPrivateContactsFragment(GDSimiContactsActvity context) {
		this.mContext = context;
	}

	@Override
	protected Trace getLogger() {
		return log;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.gd_private_contacts, container,
				false);
		gd_lv_private_contacts = (ListView) view
				.findViewById(R.id.gd_lv_private_contacts);
		gd_ll_private_contacts_empty = (LinearLayout) view
				.findViewById(R.id.gd_ll_private_contacts_empty);

		gd_lv_private_contacts.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mContext.hideTopPop();
				return false;
			}
		});
		
		if (adapter == null) {
			adapter = new GDPrivateContactsAdapter(getActivity(), mContext);
			gd_lv_private_contacts.setAdapter(adapter);
		}

		gd_lv_private_contacts
				.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						mView = view;
						telphone = ((PrivateSMSummary) parent
								.getItemAtPosition(position)).getPhoneNumber();
						Intent intent = new Intent(getActivity(),
								GDItemLongListDialog.class);
						intent.putExtra(Constant.DIALOG_KEY,
								Constant.LONG_SM_LOCK_OPEN);
						intent.putExtra(Constant.DIALOG_CONTENT, "解锁私密联系人？");
						intent.putExtra(Constant.PHONE_NUMBER, telphone);
						startActivityForResult(intent, 100);
						view.setBackgroundColor(getResources().getColor(
								R.color.gd_callrecord_long_press));
					}
				});

		return view;
	}

	@Override
	public void onResume() {
		getData();
		if(mView != null)
			mView.setBackgroundColor(getResources().getColor(R.color.white));

		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
		setupListener();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 从联系人呢添加，还需要保留这个监听，所以放在了这里,而不是 onStop
		tearDownListener();
	}

	private void getData() {
		CMProgressMonitor monitor = new CMProgressMonitor(getActivity()) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(final Object result) {
				mContext.runOnUiThread(new Runnable() {
					public void run() {
						if (result != null) {
							lists = (List<PrivateSMSummary>) result;
							if (needAddPerson) {
								int size = lists.size();
								int oldesize = adapter.getCount();
								for (int i = 0; i < size; i++) {
									PrivateSMSummary item = lists.get(i);
									boolean find = false;
									for (int j = 0; j < oldesize; j++) {
										String old = ((PrivateSMSummary) adapter
												.getItem(j)).getPhoneNumber();
										if (old.equals(item.getPhoneNumber())) {
											find = true;
											break;
										}
									}
									if (!find) {
										addperson.add(item.getPhoneNumber());
									}
								}
							}

							if (addperson.size() != 0 && (!askimport)) {
								askimport = true;
								Intent t = new Intent(getActivity(),
										ConfirmDialogActivity.class);
								t.putExtra(Constant.DIALOG_KEY,
										Constant.CONFIRM_IMPORSYS);
								// t.putExtra(Constant.IMPORT_NUM,number);

								startActivityForResult(t, 100);
							}

							adapter.setData(lists);
							
							if (lists != null && !lists.isEmpty()) {
								gd_ll_private_contacts_empty
										.setVisibility(View.GONE);
								gd_lv_private_contacts
										.setVisibility(View.VISIBLE);
							}

						} else {
							if (lists != null && !lists.isEmpty())
								lists.clear();
							gd_lv_private_contacts.setVisibility(View.GONE);
							gd_ll_private_contacts_empty
									.setVisibility(View.VISIBLE);
						}
						refreshData();
					}
				});
			}

			@Override
			protected Map<String, Object> getDialogParams() {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(DIALOG_PARAM_KEY_TITLE, "提示：");
				map.put(DIALOG_PARAM_KEY_MESSAGE, "正在获取数据,请稍侯...");
				return map;
			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				return ((ApplicationManager) getActivity().getApplication())
						.getFramework().getService(IPrivateSMService.class)
						.getSummarys();
			}
		});

	}
	
	/**
	 * 设置私密信息已读
	 */
	private void setPrivateInfoReader(final String telphone){
		CMProgressMonitor monitor = new CMProgressMonitor(getActivity()) {
			
			@Override
			protected void handleFailed(Throwable cause) {
				
			}
			
			@Override
			protected void handleDone(Object returnVal) {
				
			}
		};
		
		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				((ApplicationManager) getActivity().getApplication())
				.getFramework().getService(IPrivateSMService.class).setReadMsgOfPhoneNumber(telphone);
				return null;
			}
		});
	}

	public void refreshData() {
		if (adapter != null)
			adapter.updateData();
	}

	/**
	 * 选择所有
	 */
	public void selectAll() {
		adapter.setSelAll();
	}

	/**
	 * 取消所有
	 */

	public void cancelAll() {
		adapter.setCancelAll();
	}

	public void delSelected(PopupWindow pop) {

		this.pop = pop;
		selectedLists = adapter.getSelteItems();

		if (selectedLists.isEmpty()) {
			return;
		}
		Intent intent = new Intent(getActivity(), ConfirmDialogActivity.class);
		intent.putExtra(Constant.DIALOG_KEY, Constant.SM_LOCK_OPEN);
		startActivityForResult(intent, 100);
	}

	/**
	 * 是否有数据
	 */
	public boolean isHasData() {
		if (lists != null && !lists.isEmpty())
			return true;
		else
			return false;
	}

	/**
	 * 隐藏checkbox
	 */
	public void hideCheckbox() {
		if (adapter != null)
			adapter.setHideSel(true);
	}

	/**
	 * 显示checkbox
	 */
	public void showCheckbox() {
		if (lists != null && !lists.isEmpty()) {
			adapter.setShowSel();
		}
	}

	protected void setupListener() {
		this.listener = new IEventListener() {

			@Override
			public void onEvent(final IBroadcastEvent event) {
				if (log.isDebugEnabled()) {
					log.debug("Receiving event :" + event);
				}

				if (!stoplistendata) {

					if (event instanceof NumberAddedEvent) {
						needAddPerson = true;
					} else {
						cacahelastperson.clear();
						needAddPerson = false;
					}
				}
				// runOnUiThread(new Runnable() {
				//
				// @Override
				// public void run() {
				// fillData();
				// }
				// });
			}
		};
		this.streamListener = new IStreamEventListener() {

			@Override
			public void onEvent(IStreamEvent event, IListenerChain chain) {
				mContext.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getData();
					}
				});
				chain.invokeNext(event);
			}
		};
		((ApplicationManager) getActivity().getApplication()).getFramework()
				.getService(IEventRouter.class)
				.registerEventListener(PrivateSMEvent.class, listener);
		((ApplicationManager) getActivity().getApplication()).getFramework()
				.getService(IEventRouter.class)
				.addListenerFirst(streamListener);
	}

	private void tearDownListener() {
		if (this.listener != null) {
			if (((ApplicationManager) getActivity().getApplication())
					.getFramework().getService(IEventRouter.class) != null) {
				((ApplicationManager) getActivity().getApplication())
						.getFramework()
						.getService(IEventRouter.class)
						.unregisterEventListener(PrivateSMEvent.class, listener);
			}
			this.listener = null;
		}
		if (this.streamListener != null) {
			if (((ApplicationManager) getActivity().getApplication())
					.getFramework().getService(IEventRouter.class) != null) {
				((ApplicationManager) getActivity().getApplication())
						.getFramework().getService(IEventRouter.class)
						.removeListener(this.streamListener);
			}
			this.streamListener = null;
		}

	}

	private void importSystemSms(String phonenum) {
		Intent t = new Intent(getActivity(), ImprorDataProcessDialog.class);
		t.putStringArrayListExtra(Constant.IMPORT_OR_DEL_NUMS, addperson);
		t.putExtra(Constant.IS_IMPORT_NUMS, "aaaa");
		t.putExtra(Constant.REQUEST_CODE, Constant.IMPORT_SYSMES);
		startActivityForResult(t, Constant.IMPORT_SYSMES);
		stoplistendata = true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		final ArrayList<String> list = new ArrayList<String>();

		switch (resultCode) {
		case Constant.SM_LOCK_OPEN:
			final boolean savetosys = data.getBooleanExtra(
					Constant.SAVE_SMS_TOSYS, false);

			if (null != selectedLists && !selectedLists.isEmpty()) {
				//选择解锁
				for (int i = 0; i < selectedLists.size(); i++) {
					list.add(selectedLists.get(i).getPhoneNumber());
				}

			}else{
				//长按解锁
				list.add(telphone);
			}
			
			if (list.isEmpty()) {
				return;
			}

			if (savetosys) {
				Intent t = new Intent(getActivity(),
						ImprorDataProcessDialog.class);
				t.putExtra(Constant.IS_IMPORT_NUMS, "");
				t.putExtra(Constant.IMPORT_OR_DEL_NUMS, list);
				t.putExtra(Constant.REQUEST_CODE, Constant.DEL_SMMSG);
				startActivityForResult(t, Constant.DEL_SMMSG);
			} else {
				deleteData(list);
			}
			break;

		case Constant.CONFIRM_DOIMPORSYS:
			//确定导入
			importSystemSms(addperson.get(0));
			break;
		case Constant.CONFIRM_NOTIMPORSYS:
			//取消导入
			deleteData(addperson);
			needAddPerson = false;
			addperson.clear();
			stoplistendata = false;
			askimport = false;
			break;
		case Constant.IMPORT_SYSMES:
			needAddPerson = false;
			askimport = false;
			addperson.clear();
//			BaseActivity.showMessageBox(getActivity(), "成功导入联系人信息");
			stoplistendata = false;
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 删除数据
	 */
	private void deleteData(final ArrayList<String> list) {
		if (null != list && !list.isEmpty()) {
			CMProgressMonitor monitor = new CMProgressMonitor(getActivity()) {
				@Override
				protected void handleFailed(Throwable cause) {
					log.warn("Failed to remove private number", cause);
					if (log.isDebugEnabled()) {
						BaseActivity.showMessageBox(getActivity(),
								"删除私密联系人失败，原因：[" + cause.getLocalizedMessage()
										+ "]");
					} else {
						BaseActivity.showMessageBox(getActivity(),
								"删除私密联系人失败，请稍后再试...");
					}
				}

				@Override
				protected void handleDone(Object returnVal) {

					hideCheckbox();
					adapter.clearNameCache();
					if (pop != null && pop.isShowing()) {
						pop.dismiss();
						pop = null;
					}
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see com.wxxr.callhelper.CMProgressMonitor#getDialogParams ()
				 */
				@Override
				protected Map<String, Object> getDialogParams() {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(DIALOG_PARAM_KEY_TITLE, "系统提示：");
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在删除私密联系人,请稍侯...");
					return map;
				}
			};
			try {
				monitor.executeOnMonitor(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						// int leng = list.size();
						// int i = 0;
						for (String num : list) {
							((ApplicationManager) getActivity()
									.getApplication()).getFramework()
									.getService(IPrivateSMService.class)
									.removePrivateNumber(num);
							// i++;
						}
						return null;
					}
				}).get(5, TimeUnit.SECONDS);
			} catch (Exception e) {
			}
		}
	}

}
