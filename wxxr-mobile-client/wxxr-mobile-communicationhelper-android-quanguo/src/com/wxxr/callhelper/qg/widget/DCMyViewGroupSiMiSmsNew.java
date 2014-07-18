package com.wxxr.callhelper.qg.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.R;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.callhelper.qg.utils.GDItemPortrait;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;
/**
 * SiMiSuoHomeActivity 在使用
 * @author yangrunfei
 *
 */
public class DCMyViewGroupSiMiSmsNew {
//	private Context mContext;
	private static final Trace log = Trace.register(DCMyViewGroupSiMiSmsNew.class);


	
//	private String sms_name;
	SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	SimpleDateFormat fmt = new SimpleDateFormat("M月dd日");
//	ImageView iv_arrow;
//	CheckBox iv_checkbox;
//	TextView tv_name;
//	TextView tv_count;
//	TextView tv_date;
//	TextView tv_content;
	String contactName;


	public static int SNAP_VELOCITY = 300;
	int dis;
	int mOffsetX;
	int mOffsetY;



	private Context mContext;
	private GDItemPortrait gd_item_portrait;

//	private ImageView iv_icon;
	
//	public void detachView(Activity activity){
//		if(this.activity == activity){
//			this.activity = null;
//			if(this.vg != null){
//				this.vg.removeView(this.contentView);
//				if(this.vg.getParent() != null){
//					((ViewGroup)this.vg.getParent()).removeView(vg);
//				}
//				this.vg = null;
//			}
//		}
//	}
	

//	public View createView(Activity activity,PrivateSMSummary body){
//		if((this.activity == activity)&&(this.vg != null)){
//			return vg;
//		}
//		this.activity = activity;
//		if(this.contentView.getParent() != null){
//			((ViewGroup)this.contentView.getParent()).removeView(this.contentView);
//		}
//		setCheck("1");
//	//	resetContent(body);
//		this.body = body;
//		ViewGroup vg = new ViewGroup(activity){
//
//			@Override
//			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//				int width = MeasureSpec.getSize(widthMeasureSpec);
//				setMeasuredDimension(width, item_length);
//				int childCount = getChildCount();
//				for (int i = 0; i < childCount; i++) {
//					View child = getChildAt(i);
//					child.measure(getWidth(), metric.heightPixels);
//				}
//			}
//
//			@Override
//			protected void onLayout(boolean changed, int l, int t, int r, int b) {
//				int startLeft = 0;
//				int startTop = 0;
//				int childCount = getChildCount();
//				for (int i = 0; i < childCount; i++) {
//					View child = getChildAt(i);
//					if (child.getVisibility() != View.GONE)
//						child.layout(startLeft, startTop, startLeft + getWidth(),
//								startTop + metric.heightPixels);
//					startLeft = startLeft + getWidth();
//				}
//			}
//		};
//		RelativeLayout ff2 = new RelativeLayout(activity);
//		ff2.setLayoutParams(new LayoutParams(metric.widthPixels, item_length));
//		twoLL = new ImageView(activity);
//		twoLL.setScaleType(ImageView.ScaleType.FIT_XY);
//		twoLL.setLayoutParams(new LayoutParams(metric.widthPixels, item_length));
//	//	twoLL.setBackgroundResource(R.drawable.sms_status2);
//		twoLL.setVisibility(View.GONE);
//		ff2.addView(twoLL);
//		ff2.addView(this.contentView);
//		vg.addView(ff2);
//		return vg;
//
//	}


	public DCMyViewGroupSiMiSmsNew(Context pcontext) {
		mContext=pcontext;
		gd_item_portrait = GDItemPortrait.getInstance(pcontext);
	}

	private void init(Application app) {

//		RelativeLayout ff2 = new RelativeLayout(mContext);
//		ff2.setLayoutParams(new LayoutParams(metric.widthPixels, item_length));
//	View view = inflater.inflate(R.layout.dcmain_item_not_arrive, null);
//
//		iv_arrow = (ImageView) view.findViewById(R.id.dc_group_arrow);
//		iv_checkbox = (CheckBox) view.findViewById(R.id.dc_group_check);
//		this.tv_name = (TextView) view.findViewById(R.id.tv_head_name);
//		this.tv_count = (TextView) view.findViewById(R.id.tv_head_count);
//		this.tv_date = (TextView) view.findViewById(R.id.tv_head_date);
//		this.tv_content = (TextView) view
//				.findViewById(R.id.tv_head_content);
//		rl_check = (RelativeLayout) view.findViewById(R.id.check_relative);
////		iv_icon = (ImageView) view.findViewById(R.id.iv_head_icon);
//		this.tv_name.setText(this.phoneNumber);
//		this.contactName = phoneNumber;
//
//		rl_check.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				setCheck("9");
//			}
//		});
//		LinearLayout llclick = (LinearLayout) view.findViewById(R.id.ll_click);
//		llclick.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// if (body.getMessageCount() == 0)
//				// {
//				// return;
//				// }
//				
//			}
//		});
//		llclick.setOnLongClickListener(new OnLongClickListener() {
//			@Override
//			public boolean onLongClick(View v) {
////				intent.setClass(mContext, DeleteSiMiPersonDialogActivity.class);
//				Intent intent = new Intent(activity, ConfirmDialogActivity.class);
//				intent.putExtra(Constant.DIALOG_KEY, Constant.DEL_MANY_CHAT_ITME);
//				intent.putExtra(Constant.PHONE_NUMBER, body.getPhoneNumber());
//				activity.startActivityForResult(intent, 100);
//				return true;
//			}
//		});
//		tv_content.setTextSize(15);
//		this.contentView = view;
		// mTouchSlop =
		// ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	/**
	 * @param app
	 */
	public void resetContent(TextView tv_name,TextView tv_count,TextView tv_date,TextView tv_content,CheckBox pcheck,final PrivateSMSummary data,boolean ischeck, ImageView iv, RelativeLayout rl, TextView tv, RelativeLayout gd_rl_pi_count, int count) {
		long time = System.currentTimeMillis();
//		String sms_name = data.getName();
//		int lcount = data.getMessageCount();
		if(count > 0){
			gd_rl_pi_count.setVisibility(View.VISIBLE);
			tv_count.setText(count + "");
		}else{
			gd_rl_pi_count.setVisibility(View.GONE);
		}
		
//		Bitmap bt_icon = ContactIconUtil.getInstance().NumberForIcon(telnumber,
//				app);
		// if (bt_icon != null) {
		// iv_icon.setImageBitmap(bt_icon);
		// }
//		ExecutorService executor = this.appMgr.getFramework().getExecutor();
		if(log.isDebugEnabled()){
			log.debug("Time spent on content updating 1 :"+(System.currentTimeMillis()-time));
		}
		time = System.currentTimeMillis();
//		activity.runOnUiThread(new Runnable() {
//			
//			@Override
//			public void run() {
//				final String telnumber = data.getPhoneNumber();
//
//				final String name = Tools.getContactsName(activity, telnumber);
//				String oldName = contactName;
//				contactName = name != null ? name : telnumber;
////				if(!contactName.equals(oldName)){
////					tv_name.setText(contactName);
////					tv_name.invalidate();						
////				}
//			}
//		});
		if(log.isDebugEnabled()){
			log.debug("Time spent on content updating 2 :"+(System.currentTimeMillis()-time));
		}
		
		String sms_name = Tools.getContactsName(mContext, data.getPhoneNumber());

		//头像
		gd_item_portrait.setItemPortrait(sms_name, data.getPhoneNumber(), rl, tv, iv);
//		if(!contactName.equals(oldName)){
//			tv_name.setText(contactName);
//			tv_name.invalidate();						
//		}
		if (sms_name != null) {
			tv_name.setText(sms_name);
//			tv_count.setText(lcount);
		} else {
//			tv_count.setText(lcount);
			if (data.getPhoneNumber().length() > 11) {
				String address = data.getPhoneNumber().substring(0, 10) + "..";
				 tv_name.setText(address);
			} else {
			     tv_name.setText(data.getPhoneNumber());
			}
		}
		String dateStr = null;
		long latestMessageTime = data.getLatestMessage() != null ? data
				.getLatestMessage().getTimestamp() : 0L;
		if (latestMessageTime == 0L) {
			dateStr = "--:--";
		} else {
			Calendar cal = Calendar.getInstance();
			int today = cal.get(Calendar.DAY_OF_YEAR);
			cal.setTimeInMillis(latestMessageTime);
			int theDay = cal.get(Calendar.DAY_OF_YEAR);
			if (today == theDay) {
				// show time
				dateStr = format.format(cal.getTime());
			} else {
				// show date
				dateStr = fmt.format(cal.getTime());
			}
		}
		tv_date.setText(dateStr);
//		tv_content.setTextSize(15);
		tv_content.setText(data.getLatestMessage() != null ? data
				.getLatestMessage().getContent() : "(没有私密消息)");
		pcheck.setTag(data.getPhoneNumber());
			pcheck.setChecked(ischeck);
		
		
		
		if(log.isDebugEnabled()){
			log.debug("Time spent on content updating :"+(System.currentTimeMillis()-time));
		}
	}


//	public boolean isChecked() {
//		return false;
//	}

	public void setCheck(String style) {
//		if ("2".equals(style)) {
//			iv_arrow.setVisibility(View.GONE);
//			rl_check.setVisibility(View.VISIBLE);
//		} else if ("1".equals(style)) {
//			iv_arrow.setVisibility(View.VISIBLE);
//			rl_check.setVisibility(View.GONE);
//		} else if ("all".equals(style)) {
//			rl_check.setVisibility(View.VISIBLE);
//			iv_checkbox.setChecked(true);
//
//		} else if ("delete".equals(style)) {
//			if (iv_checkbox.isChecked()) {
//				Intent intent2 = new Intent();
//				intent2.setAction("com.wxxr.viewgroup.refreshsimiperson");
//				activity.sendBroadcast(intent2);
//			}
//		} else if ("cancel".equals(style)) {
//			rl_check.setVisibility(View.VISIBLE);
//			iv_checkbox.setChecked(false);
//		} else if ("9".equals(style)) {
//			if (iv_checkbox.isChecked()) {
//				iv_checkbox.setChecked(false);
//			} else {
//				iv_checkbox.setChecked(true);
//			}
//		}
	}

	public void clearPortraitMaps(){
		gd_item_portrait.clearPortraitMaps();
	}
}
