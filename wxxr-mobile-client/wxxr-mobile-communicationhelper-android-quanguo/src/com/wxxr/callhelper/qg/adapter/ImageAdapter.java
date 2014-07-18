package com.wxxr.callhelper.qg.adapter;

import com.wxxr.callhelper.qg.utils.Tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

public class ImageAdapter extends BaseAdapter {
	
	private int[] images;
	private Context context;
	private DisplayMetrics metrics;

	public ImageAdapter(Context context, int[] images){
		this.context = context;
		this.images = images;
		this.metrics = null;
	}
	
	public ImageAdapter(Context context, int[] images, DisplayMetrics metrics){
		this.context = context;
		this.images = images;
		this.metrics = metrics;
	}
	
	@Override
	public int getCount() {
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ImageView iv = new ImageView(context);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		iv.setLayoutParams(params);
		if(metrics == null){
			iv.setBackgroundDrawable(context.getResources().getDrawable(images[position]));
		}else{
			Bitmap bitmap = Tools.getBitmap(context, params.width, (int)(metrics.density * 200), images[position]);
			iv.setBackgroundDrawable(new BitmapDrawable(bitmap));
		}
		
		return iv;
	}
	
}
