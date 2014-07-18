package com.wxxr.callhelper.qg.framework;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.utils.Tools;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;

/**
 * 
 * @author yinzhen
 * 
 */
public class MenuActivity extends Activity
{

	private static PopupWindow pop = null;

	public static PopupWindow getMenu(Context context, View view)
	{
		pop = new PopupWindow(view, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		pop.setAnimationStyle(R.style.pop_anim_style);
		pop.setBackgroundDrawable(context.getResources().getDrawable(android.R.color.transparent));
		pop.setFocusable(true);
		pop.setTouchable(true);
		pop.setOutsideTouchable(true);
		view.setFocusableInTouchMode(true);
		pop.setTouchInterceptor(new OnTouchListener()
		{

			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
				{
					pop.dismiss();
				}
				return false;
			}
		});
		view.setOnKeyListener(new OnKeyListener()
		{

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{

				if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_MENU)
				{
					pop.dismiss();
					return true;
				}
				return false;
			}
		});
		return pop;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (Tools.isPortrait(this))
		{

			if (keyCode == KeyEvent.KEYCODE_MENU)
			{
				if (pop.isShowing())
				{
					pop.dismiss();
				}
				else
				{
					pop.showAtLocation(findViewById(R.id.rl_menu), Gravity.BOTTOM, 0, 0);
				}
			}
		}

		return super.onKeyDown(keyCode, event);

	}

}
