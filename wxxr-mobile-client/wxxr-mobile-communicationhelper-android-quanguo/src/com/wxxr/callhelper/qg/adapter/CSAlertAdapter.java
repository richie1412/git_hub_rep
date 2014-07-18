package com.wxxr.callhelper.qg.adapter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.ComSecretaryBean;
import com.wxxr.callhelper.qg.utils.ContactsUtil;
import com.wxxr.callhelper.qg.utils.Tools;
/**
 * 
 * @author cuizaixi
 * 
 */
public class CSAlertAdapter extends AbstractCommonAdapter<ComSecretaryBean> {
	public CSAlertAdapter(List<ComSecretaryBean> data, Context context) {
		super(data, context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = getInflater().inflate(R.layout.alert_item, null,
					false);
			holder.toSomeBody = (TextView) convertView
					.findViewById(R.id.tv_to_somebody);
			holder.time = (TextView) convertView.findViewById(R.id.tv_date);
			holder.state = (TextView) convertView.findViewById(R.id.tv_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ComSecretaryBean bean = mData.get(position);
		String nameByNumber = Tools.getContactsName(mContext, bean.getTelnum());
		String content = "";
		long type = bean.getSecreType();
		if (nameByNumber != null) {
			content = nameByNumber;
		} else {
			content = bean.getTelnum();
		}
		if (type == 0) {
			holder.toSomeBody.setText("回拨" + '"' + "  " + content + '"');
		} else if (type == 1) {
			holder.toSomeBody.setText("发送短信" + '"' + "  " + content + '"');
		}
		String date = parseDate(bean.getAlertTime());
		holder.time.setText(date);
		int state = bean.getState();
		if (state == 0) {
			holder.state.setBackgroundResource(R.drawable.befor_alert);
			holder.state.setText("未提醒");
			holder.toSomeBody.setTextColor(mContext.getResources().getColor(
					R.color.qg_befor_alert_item));
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.qg_befor_alert_item));
			holder.time.setTextColor(mContext.getResources().getColor(
					R.color.qg_befor_alert_item));
		} else if (state == 1) {
			holder.state.setBackgroundResource(R.drawable.after_alert);
			holder.state.setText("已提醒");
			holder.state.setTextColor(Color.BLACK);
			holder.toSomeBody.setTextColor(Color.BLACK);
			holder.time.setTextColor(Color.BLACK);
		}
		return convertView;
	}
	class ViewHolder {
		TextView toSomeBody;
		TextView time;
		TextView state;
	}
	public static String parseDate(long d) {
		SimpleDateFormat dateFormat = null;
		Date date = new Date(d);
		Date today = new Date(System.currentTimeMillis());
		int year = date.getYear();
		if (year == today.getYear()) {
			dateFormat = new SimpleDateFormat("MM月dd日  HH:mm");
		} else {
			dateFormat = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		}
		return dateFormat.format(date);
	}
}
