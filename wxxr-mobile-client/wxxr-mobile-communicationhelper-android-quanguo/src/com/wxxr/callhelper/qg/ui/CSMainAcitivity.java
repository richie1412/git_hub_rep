package com.wxxr.callhelper.qg.ui;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wxxr.callhelper.qg.ICSAlarmManager;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CSAlarmManager;
import com.wxxr.callhelper.qg.adapter.CSAlertAdapter;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.bean.SwitchSettingBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.ComSecretaryDao;
import com.wxxr.callhelper.qg.db.dao.IComeHelperDao;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
import com.wxxr.callhelper.qg.ui.gd.GDSwitchSettingCommonActivty;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.widget.BottomTabBar;
/**
 * 事项列表页
 * 
 * @author cuizaixi
 * 
 */
public class CSMainAcitivity extends BaseActityExtend {
	private ListView lv_mian;
	private IComeHelperDao<ComSecretaryBean> mComeHelperDao;
	private CSAlertAdapter mCsAlertAdapter;
	private ComSecretaryBean mSeletedItem;
	public static final String ITEM_ID = "item_id";
	private ICSAlarmManager mAlarmManager;
	private LinearLayout empty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContent(R.layout.cs_mian);
		findView();
		processLogic();
	}

	protected void findView() {
		findViewById(R.id.btn_add_new_alert_item).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(new Intent(
								CSMainAcitivity.this, CSManagerActvity.class));
						intent.putExtra(CSManagerActvity.CS_BEAN,
								new ComSecretaryBean(-1, null, 0, 0, 0));
						// 为了不影响其他窗口弹出，此处开启新的任务栈。
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
				});
		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		iv_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CSMainAcitivity.this,
						GDSwitchSettingCommonActivty.class);
				intent.putExtra(GDSwitchSettingCommonActivty.SWITCH_SETTING,
						new SwitchSettingBean("设置", "每次通话后添加提醒",
								R.string.cs_setting_description,
								Constant.CS_SETTING_KEY, 1));
				startActivity(intent);
			}
		});
		tv_title_name.setText("通信小秘书");
		lv_mian = (ListView) findViewById(R.id.lv_alert_mian);
		empty = (LinearLayout) this.getLayoutInflater().inflate(
				R.layout.cs_main_empty, null);
		View footer = this.getLayoutInflater().inflate(R.layout.gd_divide_gray,
				null);
		lv_mian.addFooterView(footer);
		BottomTabBar btb = (BottomTabBar) findViewById(R.id.home_bottom_tabbar);
		btb.setActivtiy(this, 0);
	}
	protected void processLogic() {
		mComeHelperDao = ComSecretaryDao.getInstance(getApplicationContext());
		mAlarmManager = CSAlarmManager.getInstance(this);
		List<ComSecretaryBean> all = mComeHelperDao.getUnhappenItems();
		mCsAlertAdapter = new CSAlertAdapter(all, getApplicationContext());
		((ViewGroup) lv_mian.getParent()).addView(empty);
		lv_mian.setEmptyView(empty);
		lv_mian.setAdapter(mCsAlertAdapter);
		lv_mian.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSeletedItem = (ComSecretaryBean) mCsAlertAdapter
						.getItem(position);
				Intent intent = new Intent(CSMainAcitivity.this,
						DialogMenuActivity.class);
				intent.putExtra(DialogMenuActivity.ITEM_LABLE_LIST,
						new String[]{"修改提醒", "删除"});
				startActivityForResult(intent, 1);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		refreshPage();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			int id = data.getIntExtra(DialogMenuActivity.SELETED_ITEM, -1);
			if (id == 1) {
				// 修改提醒
				Intent intent = new Intent(new Intent(CSMainAcitivity.this,
						CSManagerActvity.class));
				intent.putExtra(CSManagerActvity.CS_BEAN, mSeletedItem);
				startActivity(intent);
			} else if (id == 2) {
				// 删除提醒
				mAlarmManager.delete(mSeletedItem.getId());
				refreshPage();
			}
		}
	}
	private void refreshPage() {
		mCsAlertAdapter.setData(mComeHelperDao.findAll());
		mCsAlertAdapter.notifyDataSetChanged();
	}
	// 在退出之后把已经到时间的置为已提醒。
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}
}
