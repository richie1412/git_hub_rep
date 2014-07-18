package com.wxxr.callhelper.qg.ui.gd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.CMProgressMonitor;
import com.wxxr.callhelper.qg.IPrivateSMService;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.ui.ConfirmDialogActivity;
import com.wxxr.callhelper.qg.ui.ContactsActivity;
import com.wxxr.callhelper.qg.ui.SiMiContactsAddActivity;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * 私密信息锁
 * @author yinzhen
 *
 */
public class GDSimiContactsActvity extends FragmentActivity implements
		OnClickListener {
	private PopupWindow pop, toppopupMenuWindow;
	private int iPrivateLockType = 0;// 0：私密信息 1：私密联系人
	private TextView tv_titlebar_name, gd_tv_private_info,
			gd_tv_private_contacts;
	private View gd_view_private_info_line, gd_view_private_contacts_line;
	private ImageView gd_iv_titlebar_right;
	private LinearLayout gd_ll_menu, gd_ll_private_lock_panel;
	private GDPrivateInfoFragment pIFragment;

	private GDPrivateContactsFragment pCFragment;
	private LayoutInflater LayoutInflater;
	private DisplayMetrics metric;
	private GDSimiContactsActvity gd_simiContacts;
	private Intent intent;
	private static final Trace log = Trace
			.register(GDSimiContactsActvity.class);
	private TextView gd_tv_menu_del, gd_tv_menu_all, gd_tv_menu_cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gd_simi_contacts);
		findView();
		processLogic();

	}

	private void findView() {
		tv_titlebar_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_titlebar_name.setText("私密信息锁");
		findViewById(R.id.gd_iv_titlebar_left).setOnClickListener(this);
		gd_iv_titlebar_right = (ImageView) findViewById(R.id.gd_iv_titlebar_right);
		gd_iv_titlebar_right.setOnClickListener(this);

		// 按钮部分
		findViewById(R.id.gd_rl_private_info).setOnClickListener(this);
		findViewById(R.id.gd_rl_private_contacts).setOnClickListener(this);
		gd_tv_private_info = (TextView) findViewById(R.id.gd_tv_private_info);
		gd_tv_private_contacts = (TextView) findViewById(R.id.gd_tv_private_contacts);
		gd_view_private_info_line = findViewById(R.id.gd_view_private_info_line);
		gd_view_private_contacts_line = findViewById(R.id.gd_view_private_contacts_line);

		gd_ll_menu = (LinearLayout) findViewById(R.id.gd_ll_menu);
		gd_ll_private_lock_panel = (LinearLayout) findViewById(R.id.gd_ll_private_lock_panel);

		if (null == gd_simiContacts) 
			gd_simiContacts = this;
		
	}

	private void processLogic() {

		switch2PrivateInfoFragment();

		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		
//		gd_ll_private_lock_panel.setFocusable(true);
//		gd_ll_private_lock_panel.setFocusableInTouchMode(true);
//		gd_ll_private_lock_panel.requestFocus();
//		gd_ll_private_lock_panel.requestFocusFromTouch();
		gd_ll_private_lock_panel.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideTopPop();
				return false;
			}
		});

	}

	@Override
	protected void onResume() {
		// if (iPrivateLockType == 0) {
		// switch2PrivateInfoFragment();
		//
		// // pIFragment.refreshData();
		// } else {
		// switch2PrivateContactsFragment();
		// // pCFragment.refreshData();
		// }
		hideMenu();
		if (toppopupMenuWindow != null && toppopupMenuWindow.isShowing()) 
			hideTopPop();
		super.onResume();
	}


	/**
	 * 私密信息
	 */
	private void switch2PrivateInfoFragment() {
		gd_iv_titlebar_right
				.setBackgroundResource(R.drawable.gd_titlebar_right_selector);

		gd_tv_private_info.setTextColor(getResources().getColor(
				R.color.gd_private_lock_text_press));
		gd_tv_private_contacts.setTextColor(getResources().getColor(
				R.color.gd_titlebar_text));
		gd_view_private_info_line.setBackgroundColor(getResources().getColor(
				R.color.gd_private_lock_text_press));
		gd_view_private_contacts_line.setBackgroundColor(getResources()
				.getColor(R.color.white));
		pIFragment = new GDPrivateInfoFragment(gd_simiContacts);
		switchFragement(pIFragment, false);
	}

	/**
	 * 私密联系人
	 */
	private void switch2PrivateContactsFragment() {
		gd_iv_titlebar_right
				.setBackgroundResource(R.drawable.gd_private_lock_add_selector);

		gd_tv_private_contacts.setTextColor(getResources().getColor(
				R.color.gd_private_lock_text_press));
		gd_tv_private_info.setTextColor(getResources().getColor(
				R.color.gd_titlebar_text));
		gd_view_private_contacts_line.setBackgroundColor(getResources()
				.getColor(R.color.gd_private_lock_text_press));
		gd_view_private_info_line.setBackgroundColor(getResources().getColor(
				R.color.white));
		pCFragment = new GDPrivateContactsFragment(gd_simiContacts);
		switchFragement(pCFragment, false);
	}

	private void switchFragement(Fragment newFragment, boolean add2Backstack) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.gd_ll_private_lock_panel, newFragment);
		if (add2Backstack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private void hidePrivateLock() {
		if (iPrivateLockType == 0) {
			// 私密信息
			pIFragment.hideCheckbox();
		} else {
			// 私密联系人
			pCFragment.hideCheckbox();
		}
	}

	private void showPrivateLock() {
		if (iPrivateLockType == 0) {
			// 私密信息
			pIFragment.showCheckbox();
		} else {
			// 私密联系人
			pCFragment.showCheckbox();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (null != pop && pop.isShowing()) {
				hideMenu();
			}else if(null != toppopupMenuWindow && toppopupMenuWindow.isShowing()){
				hideTopPop();
			}else{
				finish();
			}
			
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (pop != null && pop.isShowing()) {
				hideMenu();
			} else {
				if (iPrivateLockType == 0 && pIFragment.isHasData())
					showMenu();
				else if (iPrivateLockType == 1 && pCFragment.isHasData()) {
					showMenu();
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}

	private View getTopPop() {
		View view = View.inflate(this, R.layout.simi_send, null);
		view.findViewById(R.id.rl_contacts_add).setOnClickListener(this);
		view.findViewById(R.id.rl_hand_add).setOnClickListener(this);
		view.findViewById(R.id.simi_send_view).setBackgroundColor(getResources().getColor(R.color.gd_private_lock_text_press));
		return view;
	}

	private void showTopPop() {
		int width = 0, height = 0, x = 0;
		int screenWidth = metric.widthPixels;
		if(screenWidth > 900){
			width = 538;
			height = 328;
			x = -440;
		}else if(screenWidth > 600){
			width = 354;
			height = 216;
			x = -275;
		}else if(screenWidth > 400){
			width = 236;
			height = 144;
			x = -195;
		}
		toppopupMenuWindow = new PopupWindow(getTopPop(), width, height);
		toppopupMenuWindow.showAsDropDown(gd_iv_titlebar_right, x, 0);
		toppopupMenuWindow.setAnimationStyle(R.style.pop_anim_style);
		gd_iv_titlebar_right
				.setBackgroundResource(R.drawable.gd_private_lock_add_press);
	}
	
	public void hideTopPop(){
		if (toppopupMenuWindow != null && toppopupMenuWindow.isShowing()) {
			toppopupMenuWindow.dismiss();
			toppopupMenuWindow = null;
			gd_iv_titlebar_right.setBackgroundResource(R.drawable.gd_private_lock_add_normal);
		}
	}

	private PopupWindow getMenu(Context context, View view) {
		PopupWindow pop = new PopupWindow(view,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		pop.setAnimationStyle(R.style.pop_anim_style);
		return pop;
	}

	private void showMenu() {
		View view = this.getLayoutInflater().inflate(R.layout.menu, null);
		pop = getMenu(this, view);
		pop.showAtLocation(gd_ll_menu, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
		view.findViewById(R.id.ll_delete).setOnClickListener(this);
		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
		gd_tv_menu_del = (TextView) view.findViewById(R.id.gd_tv_menu_del);
		gd_tv_menu_all = (TextView) view.findViewById(R.id.gd_tv_menu_all);
		gd_tv_menu_cancel = (TextView) view.findViewById(R.id.gd_tv_menu_cancel);
		if (iPrivateLockType == 0)
			gd_tv_menu_del.setText("删  除");
		else
			gd_tv_menu_del.setText("解  锁");
		showPrivateLock();
	}

	private void hideMenu() {
		if (pop != null && pop.isShowing()) {
			pop.dismiss();
			pop = null;
			hidePrivateLock();
		}
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
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.gd_iv_titlebar_left:
			finish();
			break;

		case R.id.gd_iv_titlebar_right:
			if (iPrivateLockType == 0) {
				// 私密信息设置
				startActivity(new Intent(getBaseContext(),
						GDSimiSetActivity.class));
			} else {
				// 联系人添加
				if(null != toppopupMenuWindow && toppopupMenuWindow.isShowing())
					hideTopPop();
				else
					showTopPop();
			}
			break;

		case R.id.gd_rl_private_info:
			// 私密信息
			iPrivateLockType = 0;
			switch2PrivateInfoFragment();
			hideMenu();
			break;

		case R.id.gd_rl_private_contacts:
			// 私密联系人
			iPrivateLockType = 1;
			switch2PrivateContactsFragment();
			hideMenu();
			break;

		case R.id.ll_all_choose:
			if (iPrivateLockType == 0) {
				pIFragment.selectAll();
			} else {
				pCFragment.selectAll();
			}
			break;
		case R.id.ll_cancle:
			if (iPrivateLockType == 0) {
				pIFragment.cancelAll();
			} else {
				pCFragment.cancelAll();
			}
			break;
		case R.id.ll_delete:
			if (iPrivateLockType == 0) {
				pIFragment.deleteSelect(pop);
			} else {
				pCFragment.delSelected(pop);
			}
			break;

		case R.id.rl_contacts_add:
			intent = new Intent(this, ContactsActivity.class);
			startActivity(intent);
			break;

		case R.id.rl_hand_add:
			intent = new Intent(this, SiMiContactsAddActivity.class);
			startActivity(intent);

		}

	}

}
