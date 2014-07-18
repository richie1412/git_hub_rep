package com.wxxr.callhelper.qg.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.callhelper.qg.utils.WeiboManager;
import com.wxxr.callhelper.qg.wxapi.WXEntryActivity;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.LRUMap;
import com.wxxr.mobile.web.grabber.model.URLCanonicalizer;
/**
 * 广东版——助手播报
 * 
 * @author cuizaixi
 * 
 */
public class GDMiniInfoPushMsgAdapter extends ArrayAdapter<HtmlMessageBean> {
	private static final Trace log = Trace
			.register(MiniInfoPushMsgAdapter.class);
	private static final LRUMap<String, Drawable> drawableMap = new LRUMap<String, Drawable>(
			100, 10 * 60);
	private static Executor executor = Executors.newFixedThreadPool(1);

	private int totalRecords;
	private int currentPosition;
	private Activity activity;
	private int style = 0;// 0 助手播报 1，优惠城市
	private int share_actionid=0;
	public GDMiniInfoPushMsgAdapter(Activity activity, int textViewResourceId) {
		super(activity, textViewResourceId);
		this.activity = activity;
		share_actionid=ActivityID.SAHRE_FROM_ZSBB.ordinal();
	}

	public GDMiniInfoPushMsgAdapter(Activity activity, int textViewResourceId,
			int pstyle) {
		super(activity, textViewResourceId);
		this.activity = activity;
		style = pstyle;
		share_actionid=ActivityID.SAHRE_FROM_YOUHUI.ordinal();
	}
	public void addAll(List<HtmlMessageBean> lists) {
		if ((lists == null) || lists.isEmpty()) {
			return;
		}
		List<String> list = new ArrayList<String>();
		for (HtmlMessageBean bean : lists) {
			add(bean);
			if (drawableMap.get(bean.getHtmlMessage().getImage()) == null
					|| drawableMap.get(bean.getHtmlMessage().getListimageUrl()) == null) {
				list.add(bean.getHtmlMessage().getImage());
				list.add(bean.getHtmlMessage().getListimageUrl());
			}
		}
		if (!list.isEmpty()) {
			final CountDownLatch latch = new CountDownLatch(list.size());
			for (final String img : list) {
				executor.execute(new Runnable() {
					public void run() {
						fetchDrawable(img);
						latch.countDown();
						if (latch.getCount() == 0) {
							activity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									notifyDataSetChanged();
								}
							});
						}
					}
				});
			}
		}

	}

	public void updateData(List<HtmlMessageBean> lists) {
		if (log.isDebugEnabled())
			log.debug("Update list data,new size :" + lists.size());
		clear();
		addAll(lists);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final HtmlMessage hMsg = getItem(position).getHtmlMessage();
		MyViewHolder vHandler = null;
		if (convertView == null) {
			vHandler = new MyViewHolder();
			convertView = View.inflate(getContext(), R.layout.gd_weblistitem,
					null);
			vHandler.icon = (ImageView) convertView
					.findViewById(R.id.gd_weblist_title_icon);
			vHandler.title = (TextView) convertView
					.findViewById(R.id.weblist_title);
			vHandler.tv_date = (TextView) convertView
					.findViewById(R.id.weblist_content_date);
			vHandler.from = (TextView) convertView
					.findViewById(R.id.weblist_from);
			vHandler.tv_contentAbstrct = (TextView) convertView
					.findViewById(R.id.weblist_content_abstract);
			vHandler.content_image = (ImageView) convertView
					.findViewById(R.id.weblist_content_img);
			vHandler.ll_weibo = (LinearLayout) convertView
					.findViewById(R.id.weblist_content_weibo);
			vHandler.ll_weixin = (LinearLayout) convertView
					.findViewById(R.id.weblist_content_weixin);
			vHandler.ll_sms = (LinearLayout) convertView
					.findViewById(R.id.weblist_content_sms);
//			vHandler.ll_weblink = (LinearLayout) convertView
//					.findViewById(R.id.ll_web_link);
			convertView.setTag(vHandler);
		} else {
			vHandler = (MyViewHolder) convertView.getTag();
		}
		// vHandler.ll_weblink.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent(activity,
		// GDPushMsgWebLinkActivity.class);
		// intent.putExtra(Constant.DIALOG_KEY, LOAD_DETAIL);
		// intent.putExtra(Constant.PINGDAO_INFO, hMsg);
		// activity.startActivity(intent);
		// }
		// });
		vHandler.tv_date.setVisibility(View.VISIBLE);
		if (hMsg.getImage() != null) {
			vHandler.icon
					.setBackgroundDrawable(drawableMap.get(hMsg.getImage()));
		}
		if (hMsg.getListimageUrl() != null) {
//			vHandler.content_image.setAdjustViewBounds(true);  
//			vHandler.content_image.setMaxHeight(50);
//			vHandler.content_image.setMaxWidth(50);
			vHandler.content_image.setVisibility(View.VISIBLE);
			vHandler.content_image.setBackgroundDrawable(drawableMap.get(hMsg
					.getListimageUrl()));
			
		} else {
			vHandler.content_image.setVisibility(View.GONE);
		}
		if (style == 0) {
			long readtime=getItem(position).getReadTime();
			if(readtime==0){//0 表示是未读的， 表示这个是默认的助手播报
				vHandler.tv_date.setText(hMsg.getCreatetime());
			}else{			
				vHandler.tv_date.setText(Tools.longToString(readtime, "MM月dd日  HH:mm"));
			}
			
		} else if (style == 1) {
			vHandler.tv_date.setText(hMsg.getCreatetime());
		}

		if(hMsg.getSource()!=null){
		vHandler.from.setText("  "+hMsg.getSource());
		}
		vHandler.title.setText(hMsg.getTitle());
		vHandler.tv_contentAbstrct.setText(hMsg.getAbstrct());
		// 微博分享
		vHandler.ll_weibo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(AppUtils.getService(IUserUsageDataRecorder.class)!=null){
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(share_actionid);
				}
				
				
				WeiboManager wm = new WeiboManager(activity);
				String content = hMsg.getTitle() + hMsg.getOrigUrl()
						+ hMsg.getAbstrct();
				if (wm.isBind()) {
					try {
						wm.share2weibo(content, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					wm.bindAndShare2weibo(content, null);
				}
			}
		});
		// 微信分享
		vHandler.ll_weixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(AppUtils.getService(IUserUsageDataRecorder.class)!=null){
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(share_actionid);
				}
				Intent in = new Intent();
				in.setClass(activity, WXEntryActivity.class);
				in.putExtra("title", hMsg.getTitle());
				in.putExtra("description", hMsg.getAbstrct());
				in.putExtra("pageUrl", hMsg.getOrigUrl());
				in.putExtra("imgUrl", hMsg.getListimageUrl());
				activity.startActivity(in);
			}
		});
		// 短信分享
		vHandler.ll_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(AppUtils.getService(IUserUsageDataRecorder.class)!=null){
					AppUtils.getService(IUserUsageDataRecorder.class).doRecord(share_actionid);
				}
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"smsto", "", null));
				intent.putExtra("sms_body", hMsg.getTitle() + hMsg.getOrigUrl());
				activity.startActivity(intent);
			}
		});
		return convertView;
	}
	/**
	 * @param iv_push_msg_img
	 * @param path
	 */
	protected Drawable fetchDrawable(String path) {

		URL url;
		try {
			url = new URL(URLCanonicalizer.getCanonicalURL(path));
		} catch (MalformedURLException e) {
			return null;
		}
		Options op = new Options();
		op.inSampleSize = 1;
		op.inJustDecodeBounds = false;
		Bitmap bitmap;
		InputStream input = null;

		try {
			input = url.openStream();
			// if (input.available() > 30000) {
			// op.inSampleSize = 6;
			// }
			bitmap = BitmapFactory.decodeStream(input, null, op);

			Drawable drawable = new BitmapDrawable(bitmap);
			drawableMap.put(path, drawable);

			return drawable;
		} catch (IOException e) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public void renderImageView(String urlString, ImageView imageView) {
		if (drawableMap.containsKey(urlString)) {
			imageView.setImageDrawable(drawableMap.get(urlString));
		}
	}

	/**
	 * @return the totalRecords
	 */
	public int getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @return the currentPosition
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * @param totalRecords
	 *            the totalRecords to set
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @param currentPosition
	 *            the currentPosition to set
	 */
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public boolean hasMore2Load() {
		if (log.isDebugEnabled()) {
			log.debug("Total records :" + totalRecords + ", records loaded :"
					+ getCount());
		}
		return getCount() < this.totalRecords;
	}

}

class MyViewHolder {
	ImageView icon;
	TextView title;
	TextView from;
	TextView tv_contentAbstrct;
	TextView tv_date;
	LinearLayout ll_weblink;
	ImageView content_image;
	LinearLayout ll_weibo;
	LinearLayout ll_weixin;
	LinearLayout ll_sms;
}
