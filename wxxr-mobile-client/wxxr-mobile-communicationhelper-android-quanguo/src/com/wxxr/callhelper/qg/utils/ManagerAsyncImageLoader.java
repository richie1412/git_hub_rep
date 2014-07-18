/*
 * @(#)ManagerAsyncImageLoader.java	 2011-11-29
 *
 * Copyright 2004-2011 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.wxxr.callhelper.qg.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.util.DisplayMetrics;

import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.service.ClientConfigManagerService;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * @class desc A ManagerAsyncImageLoader.
 * 
 * @author liuzhongnan
 * @version $Revision: 1.2 $
 * @created time 2011-11-29 上午11:34:00
 */
public class ManagerAsyncImageLoader {
	private static final int DEFALU_WID = 100;
	private static HashMap<String, SoftReference<Drawable>> imageCache;
	private static HashMap<String, String> atloading;// 记录正在加载的数据，频繁滑动的时候，会这样
	private int imgWid;
	private static String basedir="/bitcache";//缓存图片的位置
	private static final Trace log = Trace
			.register(ManagerAsyncImageLoader.class);
	// private int imgHei;
	/**
	 * 
	 * @param imgwid
	 *            不是0，就是实际大小了，是0，宽度是300
	 * @param imghei
	 */
	public void setImg(int imgwid, int imghei) {
		imgWid = imgwid;
		// imgHei=imghei;
	}

	public ManagerAsyncImageLoader() {
		if (imageCache == null) {
			imageCache = new HashMap<String, SoftReference<Drawable>>();
		}
		if (atloading == null) {
			atloading = new HashMap<String, String>();
		}
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		synchronized (imageCache) {
			if (imageCache.containsKey(imageUrl)) {
				SoftReference<Drawable> softReference = imageCache
						.get(imageUrl);
				Drawable drawable = softReference.get();
				if (drawable != null) {
					return drawable;
				}
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				if (message.obj instanceof Drawable && imageCallback != null) {
					imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
				}
			}
		};
		if (!atloading.containsKey(imageUrl)) {
			synchronized (atloading) {
				atloading.put(imageUrl, "0");
			}
			new Thread() {
				@Override
				public void run() {
					Drawable drawable = loadImageFromUrl(imageUrl);
                    if(drawable!=null){
                    	synchronized (imageCache) {
    						imageCache.put(imageUrl, new SoftReference<Drawable>(
    								drawable));
    					}
                    	Message message = handler.obtainMessage(0, drawable);
    					handler.sendMessage(message);
                    }
					
					synchronized (atloading) {
						atloading.remove(imageUrl);
					}
					
				}
			}.start();
		}
		return null;
	}

	public Drawable loadImageFromUrl(String url) {
		// URL m;
		// InputStream i = null;
		// try {
		// m = new URL(url);
		// i = (InputStream) m.getContent();
		// } catch (MalformedURLException e1) {
		// e1.printStackTrace();
		// } catch (IOEhxception e) {
		// e.printStackTrace();
		// }
		// Drawable d = Drawable.createFromStream(i, "src");
		// return d;

		String filename = saveImgToLocal(url, 1);
		if(filename.length()==0){
			return null;
		}
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			InputStream in = new FileInputStream(filename);
			Bitmap bit = null;
			if (imgWid == 0) {// 使用200的宽度
				// Bitmap bit= BitmapFactory.decodeStream(in);
				options.inJustDecodeBounds = true;
				
				bit = BitmapFactory.decodeStream(in, null, options);
				if(bit!=null){
					bit.recycle();
				}
				int wid = options.outWidth / DEFALU_WID;
				if (wid > 1) {
					options.inJustDecodeBounds = false;
					options.inSampleSize = wid;
					bit = getBitmap(filename, options);
				} else {
					options.inJustDecodeBounds = false;
					options.inSampleSize = 1;
					bit = getBitmap(filename, options);
				}
				if(in!=null){
					in.close();
				}
				in.close();
				
			} else {
				options.inJustDecodeBounds = false;
				options.inSampleSize = 1;
				bit = getBitmap(filename, options);
			bit.setDensity(DisplayMetrics.DENSITY_HIGH);
			if(log.isDebugEnabled()){
				log.debug("ManagerAsyncImageLoader bit wid and hei===", bit.getWidth()+"====="+bit.getHeight());
			}
			}

			return new BitmapDrawable(bit);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(log.isDebugEnabled()){
		log.debug("ManagerAsyncImageLoader", "file err "+url);
		}
		}
		return null;
	}

	private Bitmap getBitmap(String filename, Options options) {
		options.inPreferredConfig=Bitmap.Config.RGB_565;
		try {
			try {
				return BitmapFactory.decodeStream(
						new FileInputStream(filename), null, options);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} catch (java.lang.OutOfMemoryError err) {
			if(log.isDebugEnabled()){
			log.debug("ManagerAsyncImageLoader 1111=====================",
					err.toString());
			}
			clearImage();
			try {
				return BitmapFactory.decodeStream(
						new FileInputStream(filename), null, options);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (java.lang.OutOfMemoryError err0) {
				if(log.isDebugEnabled()){
				log.debug("ManagerAsyncImageLoader 2222=====================",
						err0.toString());
				}
				System.gc();
				return null;
			}
		}
		return null;
	}

	private String saveImgToLocal(String purl, int count) {
		//http://192.168.123.141/cmsFile/goods/4.jpg
		
		boolean isme=false;//isMyImage(purl);
		
		
		String filename = 	getFileName0(purl);
		if(filename.length()==0){
			return "";
		}
		
		
		try {
		
			File dir = new File(Environment.getExternalStorageDirectory()+"/"+Constant.APP_DATA_BASE
					+ basedir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		
			File outcache = new File(filename);
		
			if (outcache.exists()) {
				return filename;
			}

			URL url = new URL(purl);
			InputStream in = url.openStream();
			FileOutputStream out = new FileOutputStream(filename);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

		} catch (Exception e) {
		//	e.printStackTrace();
			if(log.isDebugEnabled()){
			log.debug("ManagerAsyncImageLoader 333=====================",
					e.toString());
			}
			if (count == 1) {
				return saveImgToLocal(purl, 2);
			}
		}
		return filename;
	}


	private String getFileName0(String purl) {
		String filename="";
		
		String prename=new String(purl);
		if(prename.startsWith("http://")){
			prename=prename.substring(8);
			int tindex=prename.indexOf("/");
			prename=prename.substring(tindex+1);		
		}
		int preindex=prename.lastIndexOf("/");
		if(preindex==-1)
		{
			return "";
		}
		prename=prename.substring(0, preindex);
		prename=prename.replace("/", "a");
		
		try {
			int index = purl.lastIndexOf("/");
			File dir = new File(Environment.getExternalStorageDirectory()+"/"+Constant.APP_DATA_BASE
					+ basedir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			filename = dir + "/"+prename+purl.substring(index+1);
			filename = filename.replace("?", "_");
			}catch(Exception ee){
				
			}
		return filename;
	}

	public void setSaveLocaToBiglDir(){
		//basedir=KidConfig.BITMAP_CACHE_PATH_BIG;
	}
	
	public static String getFileName(String purl) {
		String filename="";
	
		String prename=new String(purl);
		if(prename.startsWith("http://")){
			prename=prename.substring(8);
			int tindex=prename.indexOf("/");
			prename=prename.substring(tindex+1);		
		}
		int preindex=prename.lastIndexOf("/");
		if(preindex==-1)
		{
			return "";
		}
		prename=prename.substring(0, preindex);
		prename=prename.replace("/", "a");
		
		try {
			int index = purl.lastIndexOf("/");
			File dir = new File(Environment.getExternalStorageDirectory()+"/"+Constant.APP_DATA_BASE+ basedir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			filename = dir + "/"+prename+purl.substring(index+1);
			filename = filename.replace("?", "_");
			}catch(Exception ee){
				
			}
		return filename;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

	public void clearImage() {
		try {
			synchronized (imageCache) {
				Iterator<String> items = imageCache.keySet().iterator();
				StringBuffer sb = new StringBuffer();
				while (items.hasNext()) {
					String key = items.next();
					sb.append(key).append(",");
				}

				String[] keys = sb.toString().split(",");
				for (int i = 0; i < keys.length; i++) {
					imageCache.get(keys[i]).clear();
					imageCache.remove(keys[i]);
				}

				imageCache.clear();
			
				imageCache = new HashMap<String, SoftReference<Drawable>>();
			}
		} catch (Exception ee) {
			if(log.isDebugEnabled()){
			log.debug("ManagerAsyncImageLoader ooooooooooooo", ee.toString());
			}
		}
		System.gc();
		System.runFinalization();
	}

}