package com.wxxr.callhelper.qg.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.adapter.LeakSessionMsgAdapter;
import com.wxxr.callhelper.qg.widget.MyListView;

public class LeakSessionActivity extends Activity /*implements OnClickListener*/{

//	private LinearLayout ll_body;
//	private TextView tv_ls_msg, tv_ls_count;
//	private RelativeLayout rl_ls_message, rl_ls_count;
//	private PopupWindow pop;
//	private LeakSessionMsgAdapter lsAdapter;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.leak_session);
//
//		findView();
//
//		processLogic();
//
//		setMenu();
//	}
//
//	/**
//	 * menu
//	 */
//	private void setMenu() {
//
//		View view = this.getLayoutInflater().inflate(R.layout.menu, null);
////		pop = getMenu(this, view);
////		
////		view.findViewById(R.id.ll_all_choose).setOnClickListener(this);
////		view.findViewById(R.id.ll_delete).setOnClickListener(this);
////		view.findViewById(R.id.ll_cancle).setOnClickListener(this);
//	}
//
//	public void findView() {
//		ll_body = (LinearLayout) findViewById(R.id.ll_body);
//		rl_ls_message = (RelativeLayout) findViewById(R.id.rl_ls_message);
//		rl_ls_count = (RelativeLayout) findViewById(R.id.rl_ls_count);
//		tv_ls_msg = (TextView) findViewById(R.id.tv_ls_msg);
//		tv_ls_count = (TextView) findViewById(R.id.tv_ls_count);
//	}
//
//	public void processLogic() {
////		rl_ls_message.setOnClickListener(this);
////		rl_ls_count.setOnClickListener(this);
//		ll_body.removeAllViews();
//		ll_body.addView(getLeakSessionMessage());
//	}
//
//	/**
//	 * 漏接电话
//	 */
//	private View getLeakSessionMessage() {
//		View view = View.inflate(this, R.layout.leak_session_message, null);
//		MyListView lv_ls_message_4 = (MyListView) view
//				.findViewById(R.id.lv_ls_message_4);
//		MyListView lv_ls_message_3 = (MyListView) view
//				.findViewById(R.id.lv_ls_message_3);
//		lsAdapter = new LeakSessionMsgAdapter(this);		
//		lv_ls_message_4.setAdapter(lsAdapter);
//		lv_ls_message_3.setAdapter(lsAdapter);
//		return view;
//	}
//
//	/**
//	 * 漏话次数
//	 */
//	private View getLeakSessionCount() {
//		View view = View.inflate(this, R.layout.leak_session_count, null);
//		return view;
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.rl_ls_message:
//			rl_ls_message
//					.setBackgroundResource(R.drawable.leak_session_selected);
//			rl_ls_count.setBackgroundDrawable(null);
//			tv_ls_msg.setTextColor(getResources().getColor(R.color.leak_session_selected));
//			tv_ls_count.setTextColor(getResources().getColor(R.color.white));
//			ll_body.removeAllViews();
//			ll_body.addView(getLeakSessionMessage());
//			break;
//			
//		case R.id.rl_ls_count:
//			rl_ls_count.setBackgroundResource(R.drawable.leak_session_selected);
//			rl_ls_message.setBackgroundDrawable(null);
//			tv_ls_count.setTextColor(getResources().getColor(R.color.leak_session_selected));
//			tv_ls_msg.setTextColor(getResources().getColor(R.color.white));
//			ll_body.removeAllViews();
//			ll_body.addView(getLeakSessionCount());
//			break;
//			
//		case R.id.ll_all_choose:
//			break;
//			
//		case R.id.ll_delete:
//			break;
//			
//		case R.id.ll_cancle:
//			break;
//		}
//	}
//	
//	@Override
//	protected void onDestroy()
//	{
//		super.onDestroy();
//		if (this.pop.isShowing())
//		{
//			this.pop.dismiss();
//		}
//	}
	
}
