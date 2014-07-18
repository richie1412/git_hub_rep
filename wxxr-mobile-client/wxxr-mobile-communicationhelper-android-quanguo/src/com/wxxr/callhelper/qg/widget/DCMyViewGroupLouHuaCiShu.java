package com.wxxr.callhelper.qg.widget;


import java.util.Hashtable;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.db.dao.LouHuaDao;
import com.wxxr.callhelper.qg.utils.ManagerSP;

public class DCMyViewGroupLouHuaCiShu {
	private Context mContext;
	private DisplayMetrics metric;
	LayoutInflater inflater;
	int item_length;
	int item_length2;
	ImageView twoLL;
	int total_length;
	public BodyBean body;
	public TextView tv_place;

	private final static String[] CONTACT_PROJECTION = new String[] { PhoneLookup.DISPLAY_NAME };
	private final static int DISPLAY_NAME_COLUMN_INDEX = 0;
    private Hashtable<String, String> namecache=new Hashtable<String, String>();
	public LouHuaDao ldao;
	private String count;

	public void setMetric(DisplayMetrics metric) {
		this.metric = metric;
	}

	public DCMyViewGroupLouHuaCiShu(Context context, DisplayMetrics metric,
			BodyBean body,String pcount) {
	//	super(context);
		count=pcount;
		mContext = context;
		this.metric = metric;
		ldao = LouHuaDao.getInstance(context);
		inflater = LayoutInflater.from(context);
		this.body = body;
		item_length = (int) (metric.density * 70);
		item_length2 = (int) (metric.density * 65);

		total_length = 3 * metric.widthPixels;

		init();
	}

	public DCMyViewGroupLouHuaCiShu(Context context, AttributeSet attrs) {
	
		mContext = context;
		init();
	}

	public DCMyViewGroupLouHuaCiShu(Context pcontext, DisplayMetrics metric2) {
		// TODO Auto-generated constructor stub
		metric=metric2;
		mContext=pcontext;
	}

	private static final int TOUCH_STATE_REST = 0;
	public static int SNAP_VELOCITY = 300;
	private int mTouchSlop = 0;
	private float mLastionMotionX = 0;
	private float mLastMotionY = 0;
	int dis;
	int mOffsetX;
	int mOffsetY;
	
	
	public void bindData(TextView tv_name,TextView tv_count,TextView tv_place,BodyBean body,long lcount,String addres){
		

		
		//Tools.px2sp
		if(metric.widthPixels > 600){
			tv_name.setTextSize(18);
			tv_count.setTextSize(18);
			tv_place.setTextSize(13);
		}else if(metric.widthPixels > 400){
			tv_name.setTextSize(17);
			tv_count.setTextSize(17);
			tv_place.setTextSize(12);
		}
		
		int msgFlag = ManagerSP.getInstance(mContext).get(
				Constant.LOCATION_MSG, 0);
		if (msgFlag == 0) {
			tv_place.setVisibility(View.VISIBLE);
		} else {
			tv_place.setVisibility(View.GONE);
		}
		
		if(addres!=null){
			tv_place.setText(addres);
		}else{
			tv_place.setText("");
		}

		String name = null;
		if(namecache.containsKey(body.address)){
			name=namecache.get(body.address);
		}else{
		
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(body.address));
	
		Cursor contactCurosr = mContext.getContentResolver().query(uri,
				CONTACT_PROJECTION, null, null, null);
		if (contactCurosr != null) {
			if (contactCurosr.moveToNext()) {
				name = contactCurosr.getString(DISPLAY_NAME_COLUMN_INDEX);
				namecache.put(body.address, name);
			}
			contactCurosr.close();
		}
		}
//		int lcount=0;
//		if(count.length()>0){
//			lcount=Integer.parseInt(count);
//		}else{
//			lcount = ldao.findLouHuaCount(body.address, body.amonth);
//		}
		
	
		if (name != null) {
			if (lcount > 0) {
				tv_name.setText(name);
				tv_count.setText(lcount + "次");
			} else {
				tv_name.setText(name);
				tv_count.setText("0次");
			}
		} else {
			if (lcount > 0) {
				// tv_count.setText("(" + body.count + ")");
				tv_count.setText(lcount + "次");
			} else {

				tv_count.setText("0次");
			}

			tv_name.setText(body.address);

		}
		
		
	}
	
	
	

	private void init() {

//		RelativeLayout ff2 = new RelativeLayout(mContext);
//		ff2.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, item_length));
//		ff2.setPadding(2, 0, 2, 0);
//		View view = inflater.inflate(R.layout.dc_louhuacishu_item, null);
//
//		TextView tv_name = (TextView) view.findViewById(R.id.tv_head_name);
//		
//		TextView tv_count = (TextView) view.findViewById(R.id.tv_head_count);
//		
//		tv_place = (TextView) view.findViewById(R.id.tv_tel_place);
//		//Tools.px2sp
//		if(metric.widthPixels > 600){
//			tv_name.setTextSize(18);
//			tv_count.setTextSize(18);
//			tv_place.setTextSize(13);
//		}else if(metric.widthPixels > 400){
//			tv_name.setTextSize(17);
//			tv_count.setTextSize(17);
//			tv_place.setTextSize(12);
//		}
		
//		int msgFlag = ManagerSP.getInstance(mContext).get(
//				Constant.LOCATION_MSG, 0);
//		if (msgFlag == 0) {
	//		tv_place.setVisibility(View.VISIBLE);
//		} else {
//			tv_place.setVisibility(View.GONE);
////		}
//
//		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
//				Uri.encode(body.address));
//		String name = null;
//		Cursor contactCurosr = mContext.getContentResolver().query(uri,
//				CONTACT_PROJECTION, null, null, null);
//		if (contactCurosr != null) {
//			if (contactCurosr.moveToNext()) {
//				name = contactCurosr.getString(DISPLAY_NAME_COLUMN_INDEX);
//			}
//			contactCurosr.close();
//		}
//		
//		int lcount=0;
//		if(count.length()>0){
//			lcount=Integer.parseInt(count);
//		}else{
//			lcount = ldao.findLouHuaCount(body.address, body.amonth);
//		}
//		
//	
//		if (name != null) {
//			if (lcount > 0) {
//				tv_name.setText(name);
//				tv_count.setText(lcount + "次");
//			} else {
//				tv_name.setText(name);
//				tv_count.setText("0次");
//			}
//		} else {
//			if (lcount > 0) {
//				// tv_count.setText("(" + body.count + ")");
//				tv_count.setText(lcount + "次");
//			} else {
//
//				tv_count.setText("0次");
//			}
//
//			tv_name.setText(body.address);
//
//		}

//		twoLL = new ImageView(mContext);
//		twoLL.setScaleType(ImageView.ScaleType.FIT_XY);
//		twoLL.setLayoutParams(new LayoutParams(metric.widthPixels, item_length));
//		twoLL.setBackgroundResource(R.drawable.sms_status2);
//		ff2.addView(twoLL);
	//	ff2.addView(view);
	//	twoLL.setVisibility(View.GONE);
	//	addView(view);

	//	mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int width = MeasureSpec.getSize(widthMeasureSpec);
//		setMeasuredDimension(width, item_length);
//		int childCount = getChildCount();
//		for (int i = 0; i < childCount; i++) {
//			View child = getChildAt(i);
//			child.measure(getWidth(), metric.heightPixels);
//		}
//	}

//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//
//		int startLeft = 0;
//		int startTop = 0; //
//		int childCount = getChildCount();
//
//		for (int i = 0; i < childCount; i++) {
//			View child = getChildAt(i);
//			if (child.getVisibility() != View.GONE)
//				child.layout(startLeft, startTop, startLeft + getWidth(),
//						startTop + metric.heightPixels);
//			startLeft = startLeft + getWidth(); //
//		}
//	}
}
