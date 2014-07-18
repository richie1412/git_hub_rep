package com.wxxr.callhelper.qg.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.framework.BaseActivity;
import com.wxxr.callhelper.qg.rpc.DXHZSetting;
import com.wxxr.callhelper.qg.utils.Tools;

public class YuRRDialogActivity extends BaseActivity
{
	private SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yyyy");
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	static final int DATE_DIALOG_ID = 1;
	static final int TIME_DIALOG_ID = 0;
	TextView yuer_time;
	//ImageView iv_finish;
	Button bt_sure;
	private Button bt_cancle;

//	String username;
//	String channelname;
//	ChannelDao cdao;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pingdao_yuer_dialog);
		yuer_time = (TextView) findViewById(R.id.tv_yuer_riqi);
	//	iv_finish = (ImageView) findViewById(R.id.iv_finish);
		bt_sure = (Button) findViewById(R.id.bt_sure);
		bt_sure.setOnClickListener(this);
		
		bt_cancle = (Button) findViewById(R.id.btn_cancle);
		bt_cancle.setOnClickListener(this);
		
		
		yuer_time.setOnClickListener(this);
	//	iv_finish.setOnClickListener(this);

		Intent intent =  getIntent();
		
//		username =  intent.getStringExtra("username");
//		channelname =  intent.getStringExtra("channelname");
		String currenttime = intent.getStringExtra(DXHZSetting.KEY_FYCHANNEL_DATE);
		Calendar c = Calendar.getInstance();
		if(currenttime != null){
			try {
				Date date = fmt.parse(currenttime);
				c.setTime(date);
			} catch (Throwable e) {
			}
		}		
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		updateDisplay();

	}

	@Override
	protected Dialog onCreateDialog(int id)
	{
		switch (id)
		{
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, false);
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog)
	{
		switch (id)
		{
		case TIME_DIALOG_ID:
			((TimePickerDialog) dialog).updateTime(mHour, mMinute);
			break;
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);
			break;
		}
	}

	private void updateDisplay()
	{
		yuer_time.setText(new StringBuilder()
		// Month is 0 based so add 1
				.append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));

		// yuer_time.setText(new StringBuilder()
		// .append(mYear).append("-").append(mMonth +
		// 1).append("-").append(mDay).append(" ").append(pad(mHour))
		// .append(":").append(pad(mMinute)));

	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
	{

		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
		{
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
			String mdate = yuer_time.getText().toString();
			String[] split = mdate.split("-");
			mdate = split[0];
//		
//			经产品确认，有业务层来判断，这里不需要处理了
//			int nian = Integer.parseInt(mdate);
//			if (nian < 1980)
//			{
//				final Calendar c = Calendar.getInstance();
//				mYear = c.get(Calendar.YEAR);
//				mMonth = c.get(Calendar.MONTH);
//				mDay = c.get(Calendar.DAY_OF_MONTH);
//				mHour = c.get(Calendar.HOUR_OF_DAY);
//				mMinute = c.get(Calendar.MINUTE);
//				updateDisplay();
//				Tools.showToast(YuRRDialogActivity.this, "年份1980年-无限");
//			}

			

		}
	};

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener()
	{

		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			mHour = hourOfDay;
			mMinute = minute;
			updateDisplay();
		}
	};

//	private static String pad(int c)
//	{
//		if (c >= 10)
//			return String.valueOf(c);
//		else
//			return "0" + String.valueOf(c);
//	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.tv_yuer_riqi:
			showDialog(DATE_DIALOG_ID);
			break;

		case R.id.iv_finish:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
		case R.id.bt_sure:
//			cdao.updateSomeBodyYuErTime(username, channelname, yuer_time.getText().toString());
			Intent intent = new Intent();
			String d = new StringBuilder().append(mMonth + 1).append('/').append(mDay).append('/').append(mYear).toString();
			intent.putExtra(DXHZSetting.KEY_FYCHANNEL_DATE, d);
//			intent.setClass(this, PingDaoXinXiActivity.class);
			intent.putExtra("info_msg", "定制成功，您设定的预产期/宝宝生日为"+yuer_time.getText().toString()+"，我们会在每天回执中，提供一条与您的预产期/宝宝生日对应的各类孕期/宝宝年龄的健康知识，祝您轻松孕育宝宝！");
//			intent.putExtra("pingdao_style", 14);
//			startActivity(intent);
			setResult(Activity.RESULT_OK, intent);			
			finish();
			break;
			
		case R.id.btn_cancle:
//			cdao.updateSomeBodyYuErTime(username, channelname, yuer_time.getText().toString());
			Intent intent1 = new Intent();
//			String d = new StringBuilder().append(mMonth + 1).append('/').append(mDay).append('/').append(mYear).toString();
//			intent.putExtra(DXHZSetting.KEY_FYCHANNEL_DATE, d);
//			intent.setClass(this, PingDaoXinXiActivity.class);
//			intent.putExtra("info_msg", "定制成功，您设定的预产期/宝宝生日为"+yuer_time.getText().toString()+"，我们会在每天回执中，提供一条与您的预产期/宝宝生日对应的各类孕期/宝宝年龄的健康知识，祝您轻松孕育宝宝！");
//			intent.putExtra("pingdao_style", 14);
//			startActivity(intent);
			setResult(Activity.RESULT_CANCELED, intent1);			
			finish();
			break;
			
			
		}

	}

}
