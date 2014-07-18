package com.wxxr.callhelper.qg.widget;

import com.wxxr.callhelper.qg.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BallView extends View
{
	private Paint paint;
	private Bitmap temp;
	private int xSpeed = 2;
	private int ySpeed = 2;
	private int locationX = 50;
	private int locationY = 50;

	public BallView(Context context)
	{
		super(context);
		paint = new Paint();
		temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.big_balloon);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.save();
		paint.setAntiAlias(false);

		try
		{
			if (locationX <= 0)
			{
				xSpeed = 2;
			}
			else if (locationX + 150 >= getWidth()) //locationX + 150 >= getWidth() / 2.0
			{
				xSpeed = -2;

			}
			if (locationY <= 0)
			{
				ySpeed = 2;
			}
			else if (locationY + 150 >= getHeight()) //locationY + 150 >= getHeight() / 2.0
			{
				ySpeed = -2;

			}
//			else if (locationY + 150 <= getHeight() / 3.0)
//			{
//				ySpeed = 5;
//
//			}
			locationX += xSpeed;
			locationY += ySpeed;
			int defaultColor = paint.getColor();
			canvas.drawBitmap(temp, locationX, locationY, paint);
			paint.setColor(defaultColor);
			canvas.restore();
			postInvalidateDelayed(50);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

}
