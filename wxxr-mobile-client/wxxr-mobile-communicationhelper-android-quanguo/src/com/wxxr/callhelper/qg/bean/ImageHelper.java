package com.wxxr.callhelper.qg.bean;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.wxxr.callhelper.qg.ui.gd.ImageContext;
import com.wxxr.callhelper.qg.utils.ImageUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

public class ImageHelper {
	public final static int REQ_CODE_LOCALE_BG = 202;
	public final static int REQ_CODE_PHOTO_CROP = 102;
	public final static int REQ_CODE_PICTURE_CAMARE = 103;
	public final static String ACTION_CROP_IMAGE = "android.intent.action.CROP";
	public final static String IMAGE_URI = "iamge_uri";
	public final static String CROP_IMAGE_URI = "crop_image_uri";
	private String album_path = ImageUtils.SDCARD;
	private final int ONE_K = 1024;
	private final int ONE_M = ONE_K * ONE_K;
	private final int MAX_AVATAR_SIZE = 2 * ONE_M; // 2M
	private Activity mActivity;
	private Bitmap photo;
	private ImageContext mCxt;
	public ImageHelper(Activity activity, ImageContext cxt) {
		this.mActivity = activity;
		this.mCxt = cxt;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			album_path = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
		}
	}
	public void getLocalImage(int reqCode) {
		try {
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			mActivity.startActivityForResult(intent, reqCode);
		} catch (Exception e1) {
			try {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				mActivity.startActivityForResult(intent, reqCode);
			} catch (Exception e2) {
			}
		}
	}
	public void getCarmreImage(int reqCod) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra("camerasensortype", 2); // 调用前置摄像头
		intent.putExtra("autofocus", true); // 自动对焦
		intent.putExtra("fullScreen", false); // 全屏
		intent.putExtra("showActionIcons", false);
		mActivity.startActivityForResult(intent, reqCod);
	}
	public void readLocalImage(Intent data, int reqCod) {
		if (data == null) {
			return;
		}

		Uri uri = null;
		uri = data.getData();

		if (uri != null) {
			startPhotoCrop(uri, null, reqCod); // ͼƬ�ü�
		}
	}
	public void readCamareImage(Intent data, int reqcode) {
		Bundle extras = data.getExtras();
		Bitmap bmp = (Bitmap) extras.get("data");
		String pathName = "/"+System.currentTimeMillis() + ".jpg";
		try {
			saveFileAndCrop(bmp, pathName);
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mActivity, "请检查SD卡是否可用", 1).show();
		}
	}

	public void startPhotoCrop(Uri uri, String duplicatePath, int reqCode) {
		Intent intent = new Intent(ACTION_CROP_IMAGE);
		intent.putExtra(IMAGE_URI, uri);
		mActivity.startActivityForResult(intent, reqCode);
	}
	private Uri preCrop(Uri uri, String duplicatePath) {
		Uri duplicateUri = null;

		if (duplicatePath == null) {
			duplicateUri = getDuplicateUri(uri);
		} else {
			duplicateUri = getDuplicateUri(uri, duplicatePath);
		}

		// rotateImage();

		return duplicateUri;
	}
	private Uri getDuplicateUri(Uri uri, String uriString) {

		Uri duplicateUri = null;
		String duplicatePath = null;
		duplicatePath = uriString.replace(".", "_duplicate.");

		// cropImagePath = uriString;
		rotateImage(uriString);

		duplicateUri = Uri.fromFile(new File(duplicatePath));

		return duplicateUri;
	}
	private Uri getDuplicateUri(Uri uri) {
		Uri duplicateUri = null;

		String uriString = getUriString(uri);

		duplicateUri = getDuplicateUri(uri, uriString);

		return duplicateUri;
	}
	private void rotateImage(String uriString) {

		try {
			ExifInterface exifInterface = new ExifInterface(uriString);

			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90
					|| orientation == ExifInterface.ORIENTATION_ROTATE_180
					|| orientation == ExifInterface.ORIENTATION_ROTATE_270) {

				String value = String.valueOf(orientation);
				exifInterface
						.setAttribute(ExifInterface.TAG_ORIENTATION, value);
				// exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
				// "no");
				exifInterface.saveAttributes();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @Title: getUriString
	 * @param uri
	 * @return
	 * @return String
	 * @date 2012-11-28 ����1:19:31
	 */
	private String getUriString(Uri uri) {
		String imgPath = null;
		if (uri != null) {
			String uriString = uri.toString();
			if (uriString.startsWith("file")) {
				imgPath = uriString.substring(7, uriString.length());

				return imgPath;
			}
			Cursor cursor = mActivity.getContentResolver().query(uri, null,
					null, null, null);
			cursor.moveToFirst();
			imgPath = cursor.getString(1);
		}
		return imgPath;
	}
	public void readCropImage(Intent data) {
		if (data == null) {
			return;
		}
		Uri uri = data.getParcelableExtra(CROP_IMAGE_URI);
		photo = getBitmap(uri);
		@SuppressWarnings("deprecation")
		Drawable drawable = new BitmapDrawable(photo);
		mCxt.setHead(drawable);
		if (photo != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] datas = null;
			try {
				datas = baos.toByteArray();
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (datasException(datas)) {
				return;
			}

		}

	}
	private boolean datasException(byte[] datas) {
		if (datas == null || datas.length <= 0) {

			return true;
		}

		if (datas.length > MAX_AVATAR_SIZE) {

			return true;
		}

		return false;
	}
	private InputStream getInputStream(Uri mUri) throws IOException {
		try {
			if (mUri.getScheme().equals("file")) {
				return new java.io.FileInputStream(mUri.getPath());
			} else {
				return mActivity.getContentResolver().openInputStream(mUri);
			}
		} catch (FileNotFoundException ex) {
			return null;
		}
	}

	private Bitmap getBitmap(Uri uri) {
		Bitmap bitmap = null;
		InputStream is = null;
		try {

			is = getInputStream(uri);

			bitmap = BitmapFactory.decodeStream(is);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ignored) {
				}
			}
		}
		return bitmap;
	}

	public void saveFileAndCrop(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(album_path);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		File myCaptureFile = new File(album_path + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		bos.flush();
		bos.close();
		Uri uri = Uri.fromFile(myCaptureFile);
		startPhotoCrop(uri, null, REQ_CODE_PHOTO_CROP);
	}
}
