package com.wxxr.callhelper.qg.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.constant.Constant;

public class MyProcesBar extends View {
	private Paint paint;
	Bitmap barfont, barback;
	private String tiptext="正在导入联系人数据";
	public void setTiptext(String txt) {
		 tiptext =txt;
	}




	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			mtextview.setText( tiptext+ getTip());
			postInvalidate();
		};

	};
	private int clipwidth = 0;
	private int maxvalue;
	private int curvalue;
	private TextView mtextview;

	public MyProcesBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyProcesBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	
		// TODO Auto-generated constructor stub
		paint = new Paint();

		// 绘制矩形区域-实心矩形
		// 设置颜色
		paint.setColor(Color.WHITE);
		// 设置样式-填充
		paint.setStyle(Style.FILL);

		barfont = BitmapFactory.decodeResource(getResources(),
				R.drawable.progressbar_front);
		barfont.setDensity(android.util.DisplayMetrics.DENSITY_HIGH);
		barback = BitmapFactory.decodeResource(getResources(),
				R.drawable.bar_back);
		if(Constant.ScreenWidth<560){
		barfont=resizeImage(barfont, Constant.ScreenWidth-10, barfont.getHeight());
		barback=resizeImage(barback, Constant.ScreenWidth-10, barback.getHeight());
	     }
	
		// 绘图
		// canvas.drawBitmap(bitmap, 10, 10, paint);
	}

	public void setMaxValue(int pmaxvaule) {
		maxvalue = pmaxvaule;

	}

	public void setCurValue(int pcurvaule) {
		curvalue = pcurvaule;
		handler.sendEmptyMessage(0);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int bhei = barback.getHeight();
		int fhei = barfont.getHeight();
		int offser = (bhei - fhei) / 2;
		int wid=this.getWidth();
		int aa=barback.getWidth();
	    int xffset=-10;
//		// // 画左边里边的圆角
//		 canvas.save();// 保存当前状态
//		 canvas.clipRect(0, 0, 30, 100);
//		 canvas.drawBitmap(barfont, 12, offser, paint);
//		 // cavnas.
//		 canvas.restore();

		// 画右边的进度
		// canvas.save();// 保存当前状态
		int width =barback.getWidth();
		int left = 30;
		if (maxvalue != 0) {
			clipwidth = width * curvalue / maxvalue;
			left = 30;
		} else {
			left = 0;
		}
		canvas.clipRect(0, 0, clipwidth, 100);
		canvas.drawBitmap(barfont, -width + clipwidth+xffset, offser, paint);
		// cavnas.
		canvas.restore();
		canvas.drawBitmap(barback, xffset, 0, paint);	
		
	}

	public void setTipTextView(TextView ptextview) {
		mtextview = ptextview;

	}

	// 返回x%
	private String getTip() {
		if (maxvalue != 0) {
			int value = curvalue * 100 / maxvalue;
			return value + "%";
		} else {
			return "";
		}
	}
	
	
	

	Bitmap resizeImage(Bitmap bitmap, int w, int h) {

		// load the origial Bitmap

		Bitmap BitmapOrg = bitmap;

		int width = BitmapOrg.getWidth();

		int height = BitmapOrg.getHeight();

		int newWidth = w;

		int newHeight = h;

		// calculate the scale

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the Bitmap

		matrix.postScale(scaleWidth, scaleHeight);

		// if you want to rotate the Bitmap

		// matrix.postRotate(45);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,

		height, matrix, true);
	//	BitmapOrg.recycle();

		// make a Drawable from Bitmap to allow to set the Bitmap

		// to the ImageView, ImageButton or what ever

		return resizedBitmap;
	}

}
