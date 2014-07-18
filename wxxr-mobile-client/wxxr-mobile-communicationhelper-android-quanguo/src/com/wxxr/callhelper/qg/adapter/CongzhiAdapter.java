package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.BodyBeanCongzhi;
/**
 * 
 * @author cuizaixi
 * 
 */
public class CongzhiAdapter extends AbstractListAdapter<BodyBeanCongzhi> {
	SimpleDateFormat format_time = new SimpleDateFormat("HH:mm");
	SimpleDateFormat format_month_date = new SimpleDateFormat("MM-dd");
	SimpleDateFormat format_month = new SimpleDateFormat("MM");
	SimpleDateFormat format_year = new SimpleDateFormat("yyy");
	SimpleDateFormat format_date = new SimpleDateFormat("dd");
	private Context mContext;
	private Calendar calendar;
	private int year;
	private int today;
	public CongzhiAdapter(Context context) {
		super(context);
		this.mContext = context;
		calendar = Calendar.getInstance();
		year = calendar.get(Calendar.YEAR);
		today = calendar.get(Calendar.DATE);
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LinearLayout.inflate(mContext, R.layout.congzhi_item,
					null);
			holder.head = (ImageView) convertView.findViewById(R.id.iv_type);
			holder.type = (TextView) convertView.findViewById(R.id.tv_type);
			holder.date = (TextView) convertView.findViewById(R.id.tv_date);
			holder.content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.count = (TextView) convertView.findViewById(R.id.tv_count);
			holder.cb = (CheckBox) convertView.findViewById(R.id.dc_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		BodyBeanCongzhi body = mData.get(position);
		int type = body.getType();
		switch (type) {
			case 1 :
				holder.head.setBackgroundResource(R.drawable.congzhi_yuejie);
				holder.type.setText("月结提醒");
				break;
			case 2 :
				holder.head.setBackgroundResource(R.drawable.congzhi_yue);
				holder.type.setText("余额提醒");
				break;
			case 3 :
				holder.head.setBackgroundResource(R.drawable.congzhi_tingji);
				holder.type.setText("停机提醒");
				break;
			default :
				break;
		}
		String text = formatDate(body);
		holder.date.setText(text);
		holder.content.setText(body.content);
		holder.count.setText("");
		return super.getView(position, convertView, parent, holder.cb);
	}
	private class ViewHolder {
		ImageView head;
		TextView type;
		TextView date;
		TextView count;
		TextView content;
		CheckBox cb;
	}
	private String formatDate(BodyBeanCongzhi body) {
		String text;
		long cdate = body.cdate;
		Date date = new Date(cdate);
		String smsTime = format_time.format(date);
		String smsMonth = format_month.format(date);
		String smsYear = format_year.format(date);
		String smsDate = format_date.format(date);
		if (Integer.parseInt(smsDate) == today) {
			text = smsTime;
		} else {
			if (Integer.parseInt(smsYear) != year) {
				text = smsYear + "年" + smsMonth + "月";
			} else {
				text = smsMonth + "月" + smsDate + "日";
			}
		}
		return text;

	}

}
