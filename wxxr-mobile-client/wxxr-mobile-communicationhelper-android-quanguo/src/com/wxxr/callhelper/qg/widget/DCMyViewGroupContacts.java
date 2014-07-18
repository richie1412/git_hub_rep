package com.wxxr.callhelper.qg.widget;

import java.util.List;
import java.util.regex.Pattern;
import com.wxxr.callhelper.qg.R;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DCMyViewGroupContacts extends ViewGroup
{
	private Context mContext;
	private DisplayMetrics metric;
	LayoutInflater inflater;
	int item_length;
	int item_length2;
	ImageView twoLL;
	int total_length;
	private static final String NAME = "name", NUMBER = "number", SORT_KEY = "sort_key";
	// yyyy.MM.dd G 'at' HH:mm:ss
	ContentValues cv;
	public CheckBox iv_checkbox;
	TextView alpha;
	TextView name;
	private RelativeLayout rl_check;
	List<ContentValues> list;
	public int position;
	public String telnumber;

	public void setMetric(DisplayMetrics metric)
	{
		this.metric = metric;
	}

	public DCMyViewGroupContacts(Context context, DisplayMetrics metric, ContentValues cv, int position,
			List<ContentValues> list)
	{
		super(context);
		mContext = context;
		this.metric = metric;
		inflater = LayoutInflater.from(context);
		item_length = (int) (metric.density * 70);
		item_length2 = (int) (metric.density * 65);
		total_length = 3 * metric.widthPixels;
		this.cv = cv;
		this.list = list;
		this.position = position;
		init();
	}

	public DCMyViewGroupContacts(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
		init();
	}

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;

	public static int SNAP_VELOCITY = 300;
	private int mTouchSlop = 0;
	private float mLastionMotionX = 0;
	private float mLastMotionY = 0;

	private VelocityTracker mVelocityTracker = null;
	int dis;

	int mOffsetX;
	int mOffsetY;

	private void init()
	{
		RelativeLayout ff2 = new RelativeLayout(mContext);
		ff2.setLayoutParams(new LayoutParams(metric.widthPixels, item_length));
		View view = inflater.inflate(R.layout.selectcity_list_item, null);
		alpha = (TextView) view.findViewById(R.id.alpha); // 首字母,肯定有设置它是否显示的
		name = (TextView) view.findViewById(R.id.name);
		iv_checkbox = (CheckBox) view.findViewById(R.id.dc_group_check);
		name.setText(cv.getAsString(NAME));
		telnumber = cv.get(NUMBER).toString();

		if (telnumber.length() == 13)
		{
			String first = telnumber.substring(0, 3);
			String second = telnumber.substring(4, 8);
			String third = telnumber.substring(9, 13);
			telnumber = first + second + third;
		}
		
		
		rl_check = (RelativeLayout) view.findViewById(R.id.check_relative);
		rl_check.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setCheck("9");
			}
		});

		String currentStr = getAlpha(cv.getAsString(SORT_KEY));
		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(position - 1).getAsString(SORT_KEY)) : " ";
		if (!previewStr.equals(currentStr))
		{// 当前字母和前一个字母相等.那么就隐藏后面
			alpha.setVisibility(View.VISIBLE);// 显示
			alpha.setText(currentStr);
		}
		else
		{
			alpha.setVisibility(View.INVISIBLE);
		}
		ff2.addView(view);
		addView(ff2);
//zjc		int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	// measure
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int width = MeasureSpec.getSize(widthMeasureSpec);
		setMeasuredDimension(width, item_length);
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++)
		{
			View child = getChildAt(i);
			child.measure(getWidth(), metric.heightPixels);
		}
	}

	private int curPage = 0;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{

		int startLeft = 0;
		int startTop = 0; //
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++)
		{
			View child = getChildAt(i);
			if (child.getVisibility() != View.GONE)
				child.layout(startLeft, startTop, startLeft + getWidth(), startTop + metric.heightPixels);
			startLeft = startLeft + getWidth(); //
		}
	}

	private String getAlpha(String str)
	{
		if (str == null)
		{
			return "#";
		}

		if (str.trim().length() == 0)
		{
			return "#";
		}

		char c = str.trim().substring(0, 1).charAt(0);
		// 正则表达式，判断首字母是否是英文字母
		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches())
		{
			return (c + "").toUpperCase();
		}
		else
		{
			return "#";
		}
	}

	public boolean isChecked()
	{
		return iv_checkbox.isChecked();
	}

	public void setCheck(String style)
	{
		if ("cancel".equals(style))
		{
			rl_check.setVisibility(View.VISIBLE);
			
			iv_checkbox.setChecked(false);
		}
		else if ("9".equals(style))
		{
			if (iv_checkbox.isChecked())
			{
				iv_checkbox.setChecked(false);
				
			}
			else
			{
				iv_checkbox.setChecked(true);
			}
		}
	}

}
