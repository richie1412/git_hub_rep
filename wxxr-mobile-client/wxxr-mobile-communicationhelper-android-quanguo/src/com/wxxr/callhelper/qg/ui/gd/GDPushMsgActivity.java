package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IMessageProviderModule;
import com.wxxr.callhelper.qg.adapter.GDMiniInfoPushMsgAdapter;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.service.IGDDownWebService;
import com.wxxr.callhelper.qg.utils.GongYiPinDao;
import com.wxxr.mobile.android.app.AppUtils;

/**
 * 广东版——助手播报
 * 
 * @author cuizaixi
 * 
 */
public class GDPushMsgActivity extends BaseActivity {
	private ListView lv_mini_info;
	private GDMiniInfoPushMsgAdapter adapter;
	private boolean isGypd = false;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.gd_mininfo_main);
		isGypd = getIntent().getBooleanExtra("gypd", false);
		findView();
		processLogic();
	}

	private void findView() {
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		lv_mini_info = (ListView) findViewById(R.id.lv_mini_info);
		findViewById(R.id.gd_iv_titlebar_left);
		ImageView iv_right = (ImageView) findViewById(R.id.gd_iv_titlebar_right);
		iv_right.setVisibility(View.INVISIBLE);
		TextView tv_name = (TextView) findViewById(R.id.tv_titlebar_name);
		if (isGypd) {
			tv_name.setText("公益播报");
		} else {
			tv_name.setText("助手播报");
		}
	}

	private void processLogic() {
		((com.wxxr.callhelper.qg.widget.BottomTabBar) findViewById(R.id.home_bottom_tabbar))
				.setActivtiy(this, 1);
		adapter = new GDMiniInfoPushMsgAdapter(this, R.layout.gd_weblistitem);
		LinearLayout no_content = (LinearLayout) LinearLayout.inflate(this,
				R.layout.gd_network_not_access, null);
		lv_mini_info.setEmptyView(no_content);
		((ViewGroup) lv_mini_info.getParent()).addView(no_content);
		refreshView();
		lv_mini_info.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HtmlMessageBean messageBean = adapter.getItem(position);
				Intent intent = new Intent(GDPushMsgActivity.this,
						GDPushMsgDetailActivity.class);
				intent.putExtra(Constant.PINGDAO_INFO, messageBean);
				intent.putExtra(Constant.SHARE_ID,
						ActivityID.SAHRE_FROM_ZSBB.ordinal());
				if(isGypd){
					intent.putExtra("title","公益播报");					
				}
				startActivity(intent);
			}
		});
	}

	private void refreshView() {
		this.adapter.clear();
		loadInData();
	}

	private void loadInData() {
		CMProgressMonitor monitor = new CMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					}
				});
			}

			@Override
			protected void handleDone(final Object result) {
				if (result != null) {
					final HtmlMessageBean[] hmb = (HtmlMessageBean[]) result;
					List list=null;
					list=new  ArrayList<HtmlMessageBean>();
					GongYiPinDao  gonyi=GongYiPinDao.getInstance(getBaseContext());
					if(isGypd){
					
						int leng=hmb.length;
						for (int i=0;i<leng;i++) {
							String id=String.valueOf(hmb[i].getHtmlid());
							if(gonyi.isGYBBKeys(id)){
								list.add(hmb[i]);
							}
						}
					}else{						
						int leng=hmb.length;
						for (int i=0;i<leng;i++) {
							String id=String.valueOf(hmb[i].getHtmlid());
							if(!gonyi.isGYBBKeys(id)){
								list.add(hmb[i]);
							}
						}						
					}
					if (adapter != null) {
						adapter.addAll(list);
						lv_mini_info.setAdapter(adapter);
					}
					}
				
			
			if(!isGypd){
				ArrayList<HtmlMessageBean> defaultlist = getService(
						IGDDownWebService.class).getZhushouBobaoDefault();
				if (defaultlist != null) {
					adapter.addAll(defaultlist);
					lv_mini_info.setAdapter(adapter);
				}
				}
				adapter.notifyDataSetChanged();
				runOnUiThread(new Runnable() {
					public void run() {
						if (adapter != null && adapter.hasMore2Load()) {
						} else {
						}
						if (result != null) {
						} else {
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
				adapter.setTotalRecords(size);
				HtmlMessageBean[] htmlMsgBeans = AppUtils.getService(
						IMessageProviderModule.class).getMessageHistory(
						adapter.getCount(), 10);
				return htmlMsgBeans;
			}
		});

	}
}