package com.wxxr.callhelper.qg.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.BodyBeanHuiZhi;
import com.wxxr.callhelper.qg.db.dao.HuiZhiBaoGaoDao;
import com.wxxr.callhelper.qg.utils.GDItemPortrait;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;

public class DCMyViewGroupHuiZhiBaoGao {
	private Context mContext;
	private DisplayMetrics metric;
	// public BodyBeanHuiZhi body;
	public int clicktype;
	private final static String[] CONTACT_PROJECTION = new String[] { PhoneLookup.DISPLAY_NAME };
	private final static int DISPLAY_NAME_COLUMN_INDEX = 0;

	public HuiZhiBaoGaoDao hdao;

	// yyyy.MM.dd G 'at' HH:mm:ss
	SimpleDateFormat format = new SimpleDateFormat("dd日HH:mm");
	// ImageView iv_arrow;
	private CheckBox iv_checkbox;
	SimpleDateFormat format_time;
	SimpleDateFormat format_compare;
	SimpleDateFormat format_yearmonth;
	SimpleDateFormat format_today;
	SimpleDateFormat format_date;
	Hashtable<String, String> cacheName = new Hashtable<String, String>();

	Hashtable<String, Integer> cacheCounntAll = new Hashtable<String, Integer>();
	Hashtable<String, Integer> cacheCounntUnArrive = new Hashtable<String, Integer>();
	Hashtable<String, Integer> cacheCounntArrive = new Hashtable<String, Integer>();

	private static DCMyViewGroupHuiZhiBaoGao instance;
	private static final Trace log = Trace.register(DCMyViewGroupHuiZhiBaoGao.class);

	public static DCMyViewGroupHuiZhiBaoGao getInstance(Context context,
			DisplayMetrics metric, BodyBeanHuiZhi body, int clicktype) {
		if (instance == null) {
			instance = new DCMyViewGroupHuiZhiBaoGao(context, metric, body,
					clicktype);
		}
		return instance;
	}

	public DCMyViewGroupHuiZhiBaoGao(Context context, DisplayMetrics metric,
			BodyBeanHuiZhi body, int clicktype) {

		mContext = context;
		this.metric = metric;
		this.hdao = HuiZhiBaoGaoDao.getInstance(context);
		// inflater = LayoutInflater.from(context);
		// this.body = body;
		this.clicktype = clicktype;

		format_time = new SimpleDateFormat("M月dd日");
		format_date = new SimpleDateFormat("dd日");
		format_compare = new SimpleDateFormat("yyyyMMdd");
		format_today = new SimpleDateFormat("HH:mm");
		format_yearmonth = new SimpleDateFormat("yyyyMM");

		gd_portrait = GDItemPortrait.getInstance(mContext);
	}

	// public DCMyViewGroupHuiZhiBaoGao(Context context, AttributeSet attrs) {
	// super(context, attrs);
	// mContext = context;
	// // init();
	// }

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;

	public static int SNAP_VELOCITY = 300;
	private int mTouchSlop = 0;
	private float mLastionMotionX = 0;
	private float mLastMotionY = 0;

	private VelocityTracker mVelocityTracker = null;
	int dis;
	int mOffsetX;
	int mOffsetY;
	private ImageView iv_head_weidu;
	private int lcount;
	private TextView tv_date;
//	private RelativeLayout ll_check;
	private GDItemPortrait gd_portrait;

	// private ImageView iv_icon;

	public void init(CheckBox iv_checkbox, TextView tv_name, TextView tv_count,
			TextView tv_date, TextView tv_content,/* ImageView iv_head_weidu,*/
			/*RelativeLayout ll_check,*//* LinearLayout llclick,*/ BodyBeanHuiZhi body,
			int clicktype, boolean showsel, boolean issel,
			ImageView gd_iv_item_portrait, RelativeLayout gd_rl_item_portrait,
			TextView gd_tv_item_portrait_text) {

		// RelativeLayout ff2 = new RelativeLayout(mContext);
		//
		// ff2.setLayoutParams(new LayoutParams(metric.widthPixels,
		// item_length));
		// // View view = inflater.inflate(R.layout.dcmain_item_huizhi_main,
		// null);
		// View view = inflater.inflate(R.layout.dcmain_item2, null);
		//
		// // iv_arrow = (ImageView) view.findViewById(R.id.dc_group_arrow);
		// // iv_icon = (ImageView) view.findViewById(R.id.iv_head_icon);
		// iv_checkbox = (CheckBox) view.findViewById(R.id.dc_group_check);
		// TextView tv_name = (TextView) view.findViewById(R.id.tv_head_name);
		// TextView tv_count = (TextView) view.findViewById(R.id.tv_head_count);
		// tv_date = (TextView) view.findViewById(R.id.tv_head_date);
		// TextView tv_content = (TextView)
		// view.findViewById(R.id.tv_head_content);
		// Tools.px2sp
		// if(metric.widthPixels > 600){
		// tv_name.setTextSize(18);
		// tv_count.setTextSize(13);
		// tv_date.setTextSize(12);
		// tv_content.setTextSize(13);
		//
		// }else if(metric.widthPixels > 400){
		// tv_name.setTextSize(17);
		// tv_count.setTextSize(12);
		// tv_date.setTextSize(11);
		// tv_content.setTextSize(12);
		// }
		// iv_head_weidu = (ImageView) view.findViewById(R.id.iv_head_weidu);
		// ll_check = (RelativeLayout) view.findViewById(R.id.check_relative);
		// ll_check.setTag(body.cdate);
		// ll_check.setTag(R.string.huizhi_onclick_bean, body);

//		llclick.setTag(body);
//		llclick.setTag(R.string.huizhi_onclick_type, clicktype);

		if (showsel) {
			iv_checkbox.setVisibility(View.VISIBLE);
//			ll_check.setVisibility(View.VISIBLE);
		} else {
			iv_checkbox.setVisibility(View.INVISIBLE);
//			ll_check.setVisibility(View.GONE);
		}
		iv_checkbox.setTag(body.cdate);
		iv_checkbox.setTag(R.string.huizhi_onclick_bean, body);
		iv_checkbox.setChecked(issel);

		// LinearLayout llclick = (LinearLayout)
		// view.findViewById(R.id.ll_click);

//		llclick.setTag(body);

		// String sms_name = Tools.getContactsName(mContext, body.tosomebody);

		String sms_name = null;
		if (!cacheName.containsKey(body.tosomebody)) {

			Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(body.tosomebody));
			Cursor contactCurosr = mContext.getContentResolver().query(uri,
					CONTACT_PROJECTION, null, null, null);
			if (contactCurosr != null) {
				if (contactCurosr.moveToNext()) {
					sms_name = contactCurosr
							.getString(DISPLAY_NAME_COLUMN_INDEX);
					cacheName.put(body.tosomebody, sms_name);
				}
				contactCurosr.close();
			}
		} else {
			sms_name = cacheName.get(body.tosomebody);
		}

		// 加载头像
		gd_portrait.setItemPortrait(sms_name, body.tosomebody,
				gd_rl_item_portrait, gd_tv_item_portrait_text,
				gd_iv_item_portrait);

//		String key = body.tosomebody + "#" + format_yearmonth.format(new Date(body.cdate));
		String key = body.tosomebody;
		if (clicktype == 0) {
			if (cacheCounntAll.containsKey(key)) {
				lcount = cacheCounntAll.get(key).intValue();
			} else {
				// 查询所有的短信条数
				lcount = hdao.queryAllSizeOFNum(body.tosomebody);
				cacheCounntAll.put(key, lcount);
			}
		} else if (clicktype == 1) {
			if (cacheCounntUnArrive.containsKey(key)) {
				lcount = cacheCounntUnArrive.get(key);
			} else {
				// 查询的未送达短信条数
				lcount = hdao.findHuiZhi_NotArriveCount(body.tosomebody,
						body.amonth, body.cdate);
				cacheCounntUnArrive.put(key, lcount);
			}
		} else {

			if (cacheCounntArrive.containsKey(key)) {
				lcount = cacheCounntArrive.get(key).intValue();
			} else {
				// 查询的已送达短信条数
				lcount = hdao.findHuiZhi_ArriveCount(body.tosomebody,
						body.amonth, body.cdate);
				cacheCounntArrive.put(key, lcount);
			}
		}
		// clicktype = 0;
//		int weidu = hdao.queryAllLouhua_accordingtoNumberAndMonth(
//				body.tosomebody, body.amonth, 1, body.cdate);
//		if (weidu > 0) {
//			iv_head_weidu.setVisibility(View.VISIBLE);
//		} else {
//			iv_head_weidu.setVisibility(View.INVISIBLE);
//		}
		// Bitmap bt_icon = ContactIconUtil.getInstance().NumberForIcon(
		// body.tosomebody, mContext);
		// if (bt_icon != null) {
		// iv_icon.setImageBitmap(bt_icon);
		// }
		if (sms_name != null) {
			tv_name.setText(sms_name);
		} else {
			tv_name.setText(body.tosomebody);

			if (body.tosomebody.length() > 11) {
				String address = body.tosomebody.substring(0, 9) + "..";
				tv_name.setText(address);
			}
		}

		if (lcount > 0) {
			tv_count.setVisibility(View.VISIBLE);
			tv_count.setText("(" + lcount + ")");
		} else {
			tv_count.setVisibility(View.INVISIBLE);
		}

		 String time1 = format_compare.format(new Date());
		 String time2 = format_compare.format(body.cdate);
		 log.info("DCMyViewGroupHuiZhiBaoGao", format_today.format(body.cdate) + format_time.format(body.cdate));
		 if (!TextUtils.isEmpty(time1) && !TextUtils.isEmpty(time2) && time1.equals(time2)) {
			 tv_date.setText(format_today.format(body.cdate));//时间
		 } else {
			 tv_date.setText(format_time.format(body.cdate));//日期
		 }

//		tv_date.setText(format_time.format(body.cdate));
		
		String somebody = "";
		String name = Tools.getContactsName(mContext, body.tosomebody);
		if (name != null) {
			somebody = name;
		} else {
			somebody = body.tosomebody;
		}
		String mstyle = "";
		if (body.mstyle.equals(1)) {
			mstyle = "未送达";
		} else if (body.mstyle.equals(0)) {
			mstyle = "已送达";
		}
		StringBuilder content_builder = new StringBuilder();
		
		String realnum=Tools.getMisdnByContent(body.content);
		
		if(realnum==null){
			String tempcontent=body.content;
			if(tempcontent.length()>15){
				tempcontent=tempcontent.substring(0, 15)+"...";
			}
			
			content_builder.append(format_date.format(body.cdate))
			.append(format_today.format(body.cdate)).append(tempcontent);
		}else{
		
		content_builder.append(format_date.format(body.cdate))
				.append(format_today.format(body.cdate)).append("致")
				.append(somebody).append(mstyle);
		}
		// tv_content.setText(body.content);
		tv_content.setText(content_builder.toString());
		// twoLL = new ImageView(mContext);
		// twoLL.setScaleType(ImageView.ScaleType.FIT_XY);
		// twoLL.setLayoutParams(new LayoutParams(metric.widthPixels,
		// item_length));
		// twoLL.setBackgroundResource(R.drawable.sms_status2);
		// twoLL.setVisibility(View.GONE);
		// ff2.addView(twoLL);
		// ff2.addView(view);
		// addView(ff2);
		// mTouchSlop =
		// ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public boolean isChecked() {
		return iv_checkbox.isChecked();
	}

	public void setCheck(String style) {
		if ("2".equals(style)) {
			// iv_arrow.setVisibility(View.GONE);
//			ll_check.setVisibility(View.VISIBLE);
			// tv_date.setVisibility(View.INVISIBLE);
		} else if ("1".equals(style)) {
			// iv_arrow.setVisibility(View.VISIBLE);
//			ll_check.setVisibility(View.GONE);
			// tv_date.setVisibility(View.VISIBLE);
		} else if ("all".equals(style)) {
//			ll_check.setVisibility(View.VISIBLE);
			// tv_date.setVisibility(View.INVISIBLE);
			iv_checkbox.setChecked(true);

		} else if ("cancel".equals(style)) {
//			ll_check.setVisibility(View.VISIBLE);
			// tv_date.setVisibility(View.INVISIBLE);
			iv_checkbox.setChecked(false);
		} else if ("9".equals(style)) {
			if (iv_checkbox.isChecked()) {
				iv_checkbox.setChecked(false);
			} else {
				iv_checkbox.setChecked(true);
			}
		}
	}

	public void clearCache() {
		cacheCounntAll.clear();
		cacheCounntUnArrive.clear();
		cacheCounntArrive.clear();
		cacheName.clear();
	}

	public void clearPortraitMaps() {
		gd_portrait.clearPortraitMaps();
	}
}
