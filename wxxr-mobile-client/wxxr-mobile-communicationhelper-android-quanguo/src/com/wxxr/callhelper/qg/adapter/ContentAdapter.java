package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.style;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.BodyBean;
import com.wxxr.callhelper.qg.ui.SmsContentActivity;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * 
 * @author yinzhen
 * 
 */

public class ContentAdapter extends BaseAdapter {
	private static final long SHOW_ITME = 1000 * 60 * 5;
	// TextView tv_time;
	// TextView tv_content;
	private LayoutInflater inflater;
	Context context;
	String n_style;
	// private android.widget.LinearLayout.LayoutParams leftParams;
	// private android.widget.LinearLayout.LayoutParams rightParams;
	private boolean isChecked;
	private List<BodyBean> selectedItems = new ArrayList<BodyBean>();
	private List<BodyBean> arraymain;
	private SimpleDateFormat format_time;
	private SmsContentActivity mActivity;

	public ContentAdapter(Context context, SmsContentActivity activity,
			List<BodyBean> arraymain, SimpleDateFormat format_time) {
		super();
		inflater = LayoutInflater.from(context);
		this.context = context;
		this.arraymain = arraymain;
		this.format_time = new SimpleDateFormat("yyyy-M-d-HH-mm");
		this.mActivity = activity;
		// leftParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// leftParams.gravity = Gravity.LEFT;

		// rightParams = new
		// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// rightParams.gravity = Gravity.RIGHT;

	}

	@Override
	public int getCount() {
		if (arraymain == null) {
			return 0;
		} else {
			return arraymain.size();
		}

	}

	@Override
	public Object getItem(int position) {
		return arraymain.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public void remove(BodyBean bodyBean) {
		arraymain.remove(bodyBean);
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
	
	
public int	getItemViewType(int position){
		
		return  arraymain.get(position).getMstyle();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LeakDetailViewControl control = null;
		int type = getItemViewType(position);
		
		if (convertView == null) {
			if(type==0){				
				convertView = inflater.inflate(
						R.layout.sms_detail_item_left, null);
				control = new LeakDetailViewControl();
				control.root = convertView;
				control.tv_time = (TextView) convertView.findViewById(R.id.tv_detail_time_left);
				control.tv_content = (TextView) convertView
						.findViewById(R.id.tv_detail_content_left);
				control.cb_leak_detail = (CheckBox) convertView
						.findViewById(R.id.cb_leak_detail_left);			
				control.empty_line = convertView.findViewById(R.id.empty_line_left);
				
				convertView.setTag(control);
			}else if(type==1){
				convertView = inflater.inflate(
						R.layout.sms_detail_item_right, null);
				control = new LeakDetailViewControl();
				control.root = convertView;
				control.tv_time = (TextView) convertView.findViewById(R.id.tv_detail_time_right);
				control.tv_content = (TextView) convertView
						.findViewById(R.id.tv_detail_content_right);
				control.cb_leak_detail = (CheckBox) convertView
						.findViewById(R.id.cb_leak_detail_right);			
				control.empty_line = convertView.findViewById(R.id.empty_line_right);
				
				convertView.setTag(control);
			}
			
			
		
		} else {
			control = (LeakDetailViewControl) convertView.getTag();
		}

		
		control.root.setBackgroundColor(context.getResources().getColor(
				R.color.transparent));

		final BodyBean bodyBean = arraymain.get(position);

		control.cb_leak_detail
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							mActivity.pressAll();
							if (!selectedItems.contains(bodyBean)) {
								selectedItems.add(bodyBean);
							}
						} else {
							selectedItems.remove(bodyBean);
							if (selectedItems.isEmpty()) {
								mActivity.pressCancelAll();
							}
						}
					}
				});
		
		
		if(isChecked){
			control.cb_leak_detail.setVisibility(View.VISIBLE);
		}else{
			control.cb_leak_detail.setVisibility(View.GONE);
		}
		
		if (selectedItems.contains(bodyBean)) {
			control.cb_leak_detail.setChecked(true);
		} else {
			control.cb_leak_detail.setChecked(false);
		}

		String tempcontent = arraymain.get(position).getContent();

		String number = Tools.getMisdnByContent(tempcontent);
		if (number != null) {
			String name = Tools.getContactsName(context, number);
			if (name != null) {
				tempcontent = tempcontent.replace("(" + number + ")", "("
						+ name + ")");
			}
		}

		control.tv_content.setText(tempcontent);

		if (position > 0) {
			if (arraymain.get(position).getCdate()
					- arraymain.get(position - 1).getCdate() > SHOW_ITME) {
				control.empty_line.setVisibility(View.GONE);
				control.tv_time.setVisibility(View.VISIBLE);
				String realTime = Tools.getChatTime(format_time
						.format(new Date()), format_time.format(new Date(
						arraymain.get(position).getCdate())));
				control.tv_time.setText(realTime);
			} else {
				control.empty_line.setVisibility(View.VISIBLE);
				control.tv_time.setVisibility(View.GONE);
			}
		} else {
			control.empty_line.setVisibility(View.GONE);
			control.tv_time.setVisibility(View.VISIBLE);
			Date now = new Date();
			Date chat = new Date(arraymain.get(position).getCdate());
			String realTime = Tools.getChatTime(format_time.format(now),
					format_time.format(chat));
			control.tv_time.setText(realTime);
		}

		return convertView;

	}

	public void selectAll() {
		this.selectedItems.clear();
		this.selectedItems.addAll(arraymain);

	}

	public void selectNone() {
		this.selectedItems.clear();
	}

	public List<BodyBean> getSelected() {
		return selectedItems;
	}
}

class LeakDetailViewControl {
	public View root;
	public View empty_line;
	TextView tv_time;
	TextView tv_content;
	CheckBox cb_leak_detail;
	LinearLayout ll_leak_detail_item;
	// int position;
}
