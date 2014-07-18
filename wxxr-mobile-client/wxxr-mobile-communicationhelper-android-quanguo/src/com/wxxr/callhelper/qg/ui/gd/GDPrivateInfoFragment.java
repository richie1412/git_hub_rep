package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.adapter.SiMiHomeAdapter;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.AbstractFragment;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.SiMiPersonContentActivity;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 私密信息
 * @author yinzhen
 *
 */
public class GDPrivateInfoFragment extends AbstractFragment {

	private final static Trace log = Trace
			.register(GDPrivateInfoFragment.class);
	private SiMiHomeAdapter adapter;
	private List<PrivateSMSummary> lists;
	private GDSimiContactsActvity mContext;
	private ListView gd_lv_private_info;
	private LinearLayout gd_ll_private_info_empty;
	private List<PrivateSMSummary> selectedLists;
	private PopupWindow pop;
	private String telphone;
	private View mView;

	@Override
	protected Trace getLogger() {
		return log;
	}

	public GDPrivateInfoFragment(GDSimiContactsActvity context) {
		this.mContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.gd_private_info, container, false);
		gd_lv_private_info = (ListView) view
				.findViewById(R.id.gd_lv_private_info);
		gd_ll_private_info_empty = (LinearLayout) view
				.findViewById(R.id.gd_ll_private_info_empty);

		if (adapter == null) {
			adapter = new SiMiHomeAdapter(getActivity(), mContext, null);
			gd_lv_private_info.setAdapter(adapter);
		}

		gd_lv_private_info.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mView = view;
				String clickTel = ((PrivateSMSummary) parent
						.getItemAtPosition(position)).getPhoneNumber();
				setPrivateInfoReader(clickTel);
				Intent intent = new Intent(getActivity(), SiMiPersonContentActivity.class);
				intent.putExtra(Constant.PHONE_NUMBER, clickTel);
				startActivity(intent);
				view.setBackgroundColor(getResources().getColor(
						R.color.gd_callrecord_long_press));
			}
		});
		
		gd_lv_private_info
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> parent,
							View view, int position, long id) {
						mView = view;
						telphone = ((PrivateSMSummary) parent.getItemAtPosition(position)).getPhoneNumber();
						
						Intent intent = new Intent(getActivity(),
								GDItemLongListDialog.class);
						intent.putExtra(Constant.DIALOG_KEY, Constant.DEL_MANY_CHAT_ITME);
						intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人私密信息？");
						intent.putExtra(Constant.PHONE_NUMBER, telphone);//发送，呼叫使用
						startActivityForResult(intent, 100);
						view.setBackgroundColor(getResources().getColor(
								R.color.gd_callrecord_long_press));
						return true;
					}
				});

		return view;
	}

	@Override
	public void onResume() {
		getData();
		if(mView != null){
			mView.setBackgroundColor(getResources().getColor(R.color.white));
		}
		
		super.onResume();
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
							Collections.sort(lists, new SortByTime());
							adapter.setData(lists);
						} else {
							gd_lv_private_info.setVisibility(View.GONE);
							gd_ll_private_info_empty
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
				int	size = AppUtils.getFramework().getService(IPrivateSMService.class)
						.getAllUnreadSize();
			 if(size==0){
				NotificationManager nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
				nm.cancel(Constant.SIMI_NEWSMS_ID); 
				}
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

	/**
	 * 删除选中
	 */

	public void deleteSelect(PopupWindow pop) {

		this.pop = pop;

		selectedLists = adapter.getSelteItems();

		if (selectedLists.isEmpty()) {
			return;
		}
		// pIFragment.adapter.atdelete = true;

		Intent intent = new Intent(getActivity(), ConfirmDialogActivity.class);
		intent.putExtra(Constant.DIALOG_KEY, Constant.DELETE_RECORD);
		intent.putExtra(Constant.DIALOG_CONTENT, "删除联系人私密信息？");
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

	@Override
	public void onDestroy() {
		adapter.adapterClearPortrait();
		super.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		final ArrayList<String> list = new ArrayList<String>();
		switch (resultCode) {
		case Constant.DELETE_RECORD:

			for (int i = 0; i < selectedLists.size(); i++) {
				list.add(selectedLists.get(i).getPhoneNumber());
			}
			deleteData(list);
			break;

		case Constant.DEL_MANY_CHAT_ITME:
			// 长按删除操作
//			String num = data.getStringExtra(Constant.PHONE_NUMBER);
			if (!TextUtils.isEmpty(telphone)) {
				list.add(telphone);
				deleteData(list);
			}
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 删除数据
	 * @param list
	 */
	private void deleteData(final ArrayList<String> list) {
		if (null != list && !list.isEmpty()) {
			CMProgressMonitor monitor = new CMProgressMonitor(getActivity()) {
				@Override
				protected void handleFailed(Throwable cause) {
					log.warn("Failed to remove private number", cause);
					if (log.isDebugEnabled()) {
						BaseActivity.showMessageBox(mContext, "删除私密联系人失败，原因：["
								+ cause.getLocalizedMessage() + "]");
					} else {
						BaseActivity.showMessageBox(mContext,
								"删除私密联系人失败，请稍后再试...");
					}
				}

				@Override
				protected void handleDone(Object returnVal) {
					hideCheckbox();

					if (pop != null && pop.isShowing()) {
						pop.dismiss();
						pop = null;
					}
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
					map.put(DIALOG_PARAM_KEY_MESSAGE, "正在删除私密联系人信息,请稍侯...");
					return map;
				}
			};

			try {
				monitor.executeOnMonitor(new Callable<Object>() {
					@Override
					public Object call() throws Exception {
						int leng = list.size();
						int i = 0;
						for (String num : list) {
							((ApplicationManager) getActivity()
									.getApplication()).getFramework()
									.getService(IPrivateSMService.class)
									.deleteMessage(num, i == leng - 1);
							i++;
						}
						return null;
					}
				}).get(5, TimeUnit.SECONDS);
			} catch (Exception e) {
			}
		}
	}
	
	class SortByTime implements Comparator {
		public int compare(Object o1, Object o2) {
			PrivateSMSummary s1 = (PrivateSMSummary) o1;
			PrivateSMSummary s2 = (PrivateSMSummary) o2;
			if(s1.getLatestMessage() != null && s2.getLatestMessage() != null){
				long date1 = s1.getLatestMessage().getTimestamp();
				long date2 = s2.getLatestMessage().getTimestamp();

				if (date2 > date1)
					return 1;// 由大到小
				else
					return -1;// 由小到大
				
			}else if(s1.getLatestMessage() != null){
				return -1;
			}else if(s2.getLatestMessage() != null){
				return 1;
			}
			
			return 1;
			
		}
	}
}
