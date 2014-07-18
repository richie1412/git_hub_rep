package com.wxxr.callhelper.qg.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
/**
 * @author cuizaixi
 * @create 2014-3-18 下午2:30:29
 */
public interface IImageView {
	/**
	 * 
	 * @return the Wrapped by WeakReference method view ,if no ,return Null.
	 */
	ImageView getWrappedImage();
	/**
	 * a ImageViewAware identified by hashCode .
	 * 
	 * @return id
	 */
	int getId();
	/**
	 * 
	 * @param bitmap
	 */
	void setImageBitmap(Bitmap bitmap);
	/**
	 * 
	 * @param drawable
	 */
	void setImageDrawable(Drawable drawable);
}
