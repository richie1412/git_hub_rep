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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.bean.HtmlMessageBean;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.LRUMap;
import com.wxxr.mobile.web.grabber.model.URLCanonicalizer;

public class MiniInfoPushMsgAdapter extends ArrayAdapter<HtmlMessageBean> {
//
//	private final class RenderingTask implements Runnable {
//		private final ImageView imageView;
//		private final String urlString;
//
//		private RenderingTask(ImageView imageView, String urlString) {
//			this.imageView = imageView;
//			this.urlString = urlString;
//		}
//
//		@Override
//		public void run() {
//		    final Drawable drawable = fetchDrawable(urlString);
//		    if(drawable != null){
//		        activity.runOnUiThread(new Runnable() {
//					
//					@Override
//					public void run() {
//						imageView.setBackgroundDrawable(drawable);
//					}
//				});
//		    }
//		}
//	}

	private static final Trace log = Trace.register(MiniInfoPushMsgAdapter.class);
	private static final LRUMap<String, Drawable> drawableMap = new LRUMap<String, Drawable>(100,10*60);
	private static Executor executor = Executors.newFixedThreadPool(1);
	
	private int totalRecords;
	private int currentPosition;
	private Activity activity;

	public MiniInfoPushMsgAdapter(Activity context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.activity = context;
	}


	public void addAll(List<HtmlMessageBean> lists){
		if((lists == null)||lists.isEmpty()){
			return;
		}
		List<String> list=new ArrayList<String>();
		for (HtmlMessageBean bean : lists) {
			add(bean);
			if(drawableMap.get(bean.getHtmlMessage().getImage()) == null){
				list.add(bean.getHtmlMessage().getImage());
			}
		}
		if(!list.isEmpty()){
			final CountDownLatch latch=new CountDownLatch(list.size());
			for(final String img:list){
				executor.execute(new Runnable() {
					public void run() {
						fetchDrawable(img);
						latch.countDown();
						if(latch.getCount()==0){
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
	
//	public void add(final HtmlMessageBean bean) {
//		super.add(bean);
//		executor.execute(new Runnable() {
//			public void run() {
//				fetchDrawable(bean.getHtmlMessage().getImage());
//			}
//		});
//	} 

	public void updateData(List<HtmlMessageBean> lists) {
		if(log.isDebugEnabled())
			log.debug("Update list data,new size :"+lists.size());
		clear();
		addAll(lists);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		HtmlMessage hMsg = getItem(position).getHtmlMessage();
		ViewHandler vHandler = null;
		if (convertView == null) {
			view = View.inflate(getContext(), R.layout.zhushoubobao_item, null);
			log.debug("Inflat view ...");
		} else {
			view = convertView;
		}

		vHandler = (ViewHandler) view.getTag();
		if (vHandler == null) {
			vHandler = new ViewHandler();
			vHandler.ll_push_msg_bg = (LinearLayout) view
					.findViewById(R.id.ll_push_msg_bg);
			vHandler.tv_date = (TextView) view
					.findViewById(R.id.tv_mini_info_push_date);
			vHandler.tv_contentTitle = (TextView) view
					.findViewById(R.id.tv_push_msg_content_title);
			vHandler.tv_contentAbstrct = (TextView) view
					.findViewById(R.id.tv_push_msg_content_abstrct);
			view.setTag(vHandler);
		}

		vHandler.tv_date.setVisibility(View.VISIBLE);
		vHandler.ll_push_msg_bg.setBackgroundResource(R.drawable.mini_msg_push_bg);

		vHandler.tv_date.setText(Tools.longToString(getItem(position).getReadTime(), "MM月dd日  HH:mm"));
//		vHandler.tv_date.setText(Tools.dateToString(hMsg.getCreateDate(), "MM月dd日  HH:mm"));
//		vHandler.tv_date.setText("7月26日  17：16");
		vHandler.tv_contentTitle.setText(hMsg.getTitle());
		vHandler.tv_contentAbstrct.setText(hMsg.getAbstrct());

		ImageView iv_push_msg_img = (ImageView) view
				.findViewById(R.id.iv_push_msg_img);
		String path = hMsg.getImage();
		if (!TextUtils.isEmpty(path)) {
			Drawable drw = drawableMap.get(path);
			if(drw != null){
				iv_push_msg_img.setBackgroundDrawable(drw);
			}else {
				iv_push_msg_img.setBackgroundResource(R.drawable.push_default_img);
		    	if(log.isDebugEnabled()){
		    		log.debug("Rendering view :"+iv_push_msg_img+", position :"+position);
		    	}

				renderImageView(path,iv_push_msg_img);
			}
		}

		return view;
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
		// op.inJustDecodeBounds = true;
		// bitmap= BitmapFactory.decodeFile(subPath, op);
//		op.inPurgeable = true;// 设置图片可以被回收
//		op.inInputShareable = true;
		op.inSampleSize = 1;
		op.inJustDecodeBounds = false;
		Bitmap bitmap;
		InputStream input=null;

		try {
			input=url.openStream();
			if(input.available()>30000){
				op.inSampleSize = 6;
			}
			bitmap = BitmapFactory.decodeStream(input, null, op);
			
			Drawable drawable =new BitmapDrawable(bitmap);
			drawableMap.put(path, drawable);

			return drawable;
		} catch (IOException e) {
			return null;
		}finally{
			if(input!=null){
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
//        activity.runOnUiThread(new Runnable() {
//			public void run() {
//				log.debug("notifyDataSetChanged");
//				notifyDataSetChanged();
//			}
//		});
//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message message) {
//                imageView.setBackgroundDrawable((Drawable) message.obj);
//            }
//        };

        //executor.execute(new RenderingTask(imageView, urlString));
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
	 * @param totalRecords the totalRecords to set
	 */
	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}



	/**
	 * @param currentPosition the currentPosition to set
	 */
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public boolean hasMore2Load() {
		if(log.isDebugEnabled()){
			log.debug("Total records :"+totalRecords+", records loaded :"+getCount());
		}
		return getCount() < this.totalRecords;
	}
	

}

class ViewHandler {
	TextView tv_contentAbstrct;
	TextView tv_contentTitle;
	TextView tv_date;
	LinearLayout ll_push_msg_bg;
}
