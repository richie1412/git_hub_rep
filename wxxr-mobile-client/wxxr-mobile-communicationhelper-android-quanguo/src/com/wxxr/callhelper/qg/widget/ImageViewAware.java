package com.wxxr.callhelper.qg.widget;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.widget.ImageView;

import com.wxxr.callhelper.qg.ui.gd.FragmentPersonCenter;

/**
 * 解决图片占用内存问题(个人中心)
 * 
 * @see FragmentPersonCenter
 * @author cuizaixi
 * @create 2014-3-18 下午2:24:38
 */
public class ImageViewAware implements IImageView {
	private Reference<ImageView> mReference;
	public ImageViewAware(ImageView imageView) {
		mReference = new WeakReference<ImageView>(imageView);
	}
	@Override
	public ImageView getWrappedImage() {
		return mReference.get();
	}
	@Override
	public int getId() {
		return mReference.get() == null ? super.hashCode() : mReference.get()
				.hashCode();
	}
	@Override
	public void setImageBitmap(Bitmap bitmap) {
		if (isRunOnUIThread()) {
			if (bitmap != null) {
				ImageView imageView = mReference.get();
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	@Override
	public void setImageDrawable(Drawable drawable) {
		if (isRunOnUIThread()) {
			if (drawable != null) {
				ImageView imageView = mReference.get();
				imageView.setImageDrawable(drawable);
			}
		}
	};
	/**
	 * 必须在ui线程中设置图片的背景
	 * 
	 * @return
	 */
	private static boolean isRunOnUIThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}
}
