/**
 * 
 */
package com.wxxr.callhelper.qg.ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IMessageProviderModule;
import com.wxxr.callhelper.qg.adapter.MiniInfoPushMsgAdapter;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.widget.MyListView;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author neillin
 *
 */
public class PushMsgListFragement extends AbstractFragment {
	private static final Trace log = Trace.register(PushMsgListFragement.class);
	private final ReaderPanelContext readerContext;
	
	private MiniInfoPushMsgAdapter adapter;
	private TextView tv_mini_info_more, tv_mini_info_loading;
	private ProgressBar pb_mini_info_push;
	private MyListView lv_mini_msg_push;
	private LinearLayout ll_mini_info_push_bottom;

	
	public PushMsgListFragement(ReaderPanelContext ctx) {
		this.readerContext = ctx;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(log.isDebugEnabled()){
			log.debug("onCreateView ...");
		}
		View view = inflater.inflate(R.layout.mini_info_push_msg, container,false);
		tv_mini_info_more = (TextView) view
				.findViewById(R.id.tv_mini_info_more);
		pb_mini_info_push = (ProgressBar) view
				.findViewById(R.id.pb_mini_info_push);
//		pb_mini_info_list = (ProgressBar) view
//				.findViewById(R.id.pb_mini_info_list);

		lv_mini_msg_push = (MyListView) view
				.findViewById(R.id.lv_mini_msg_push);
		// sv_mini_info_push = (ScrollView) view
		// .findViewById(R.id.sv_mini_info_push);
		ll_mini_info_push_bottom = (LinearLayout) view
				.findViewById(R.id.ll_mini_info_push_bottom);
		
		tv_mini_info_loading = (TextView) view.findViewById(R.id.tv_mini_info_loading);

		if (adapter == null) {
			adapter = new MiniInfoPushMsgAdapter(getActivity(), R.layout.zhushoubobao_item);
			lv_mini_msg_push.setAdapter(adapter);
		}

		tv_mini_info_more.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				tv_mini_info_more.setVisibility(View.GONE);
				pb_mini_info_push.setVisibility(View.VISIBLE);
				loadInData();
			}
		});

		lv_mini_msg_push.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				log.info("onitemclick:", position + "");
				HtmlMessageBean bean = adapter.getItem(position);
				readerContext.onPushMsgClicked(bean);
			}
		});
//		getActivity().runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				refreshView();
//			}
//		});
		return view;
	}

	
	private void loadInData() {
		tv_mini_info_loading.setText("正在加载数据......");
		CMProgressMonitor monitor = new CMProgressMonitor(getActivity()) {

			@Override
			protected void handleFailed(Throwable cause) {
				if(getActivity() != null)
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						pb_mini_info_push.setVisibility(View.GONE);
						tv_mini_info_more.setVisibility(View.VISIBLE);
						tv_mini_info_loading.setText(R.string.no_receive_hz);
					}
				});
			}

			@Override
			protected void handleDone(final Object result) {
				if (result != null) {
					final HtmlMessageBean[] hmb = (HtmlMessageBean[]) result;
					if(adapter!=null){
						adapter.addAll(Arrays.asList(hmb));

					}
				}
					if(getActivity() != null)
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								if (adapter!=null &&adapter.hasMore2Load()) {
									ll_mini_info_push_bottom.setVisibility(View.VISIBLE);
								}else{
									ll_mini_info_push_bottom.setVisibility(View.GONE);
								}
								pb_mini_info_push.setVisibility(View.GONE);
								tv_mini_info_more.setVisibility(View.VISIBLE);
								if (result != null ) {
									tv_mini_info_loading.setVisibility(View.GONE);
								}else{
									tv_mini_info_loading.setText(R.string.no_receive_hz);
								}
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
				int size = AppUtils.getService(IMessageProviderModule.class)
						.getTotalMessageHistorySize();
				if(log.isDebugEnabled()){
					log.debug("Message provider return total push message size :"+size);
				}
				adapter.setTotalRecords(size);
				HtmlMessageBean[] htmlMsgBeans = AppUtils.getService(
						IMessageProviderModule.class).getMessageHistory(adapter.getCount(),
						10);
				if(log.isDebugEnabled()){
					log.debug("Message provider return push message size :"+(htmlMsgBeans != null ? htmlMsgBeans.length : 0));
				}
				return htmlMsgBeans;
			}
		});

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		clear();
		this.adapter=null;
		super.onDestroyView();
	}

	
	public void refreshView() {
		this.adapter.clear();
		loadInData();
	}
	
	public void clear() {
		this.adapter.clear();
	}

	@Override
	protected Trace getLogger() {
		return log;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.ui.AbstractFragment#onResume()
	 */
	@Override
	public void onResume() {
		readerContext.showTitle();
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.ui.AbstractFragment#onStart()
	 */
	@Override
	public void onStart() {
		refreshView();
		super.onStart();
	}
}
