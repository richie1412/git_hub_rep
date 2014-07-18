package com.wxxr.callhelper.qg.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.PrivateSMSummary;
import com.wxxr.callhelper.qg.ui.gd.GDSimiContactsActvity;
import com.wxxr.callhelper.qg.utils.Tools;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 私密联系人adapter
 * @author yinzhen
 *
 */
public class GDPrivateContactsAdapter extends BaseAdapter {

	private List<PrivateSMSummary> mData;
	private Context mContext;
	LayoutInflater layoutInflater;

	private SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat fmt = new SimpleDateFormat("M月dd日");
	private Hashtable<String, String> nameCache = new Hashtable<String, String>();
	private PopupWindow pop;
	private GDSimiContactsActvity mActivity;

	public GDPrivateContactsAdapter(Context context, GDSimiContactsActvity activity) {
		this.mContext = context;
		this.mActivity = activity;
		// this.selePerson = selePerson;
		// this.nameCache = nameCache;
	}

	/**
	 * 0,不显示，隐藏， 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据sel来判断被勾选的内容，
	 */
	int sel_state = 0;

	Hashtable<String, String> selbobysid = new Hashtable<String, String>();
	Hashtable<String, String> canlebobysid = new Hashtable<String, String>();

	public void setShowSel() {
		sel_state = 3;
		notifyDataSetChanged();
	}

	public void setHideSel(boolean notify) {
		canlebobysid.clear();
		selbobysid.clear();
		sel_state = 0;
		if (notify) {
			notifyDataSetChanged();
		}
	}

	public void setSelAll() {
		canlebobysid.clear();
		selbobysid.clear();
		sel_state = 1;
		notifyDataSetChanged();
	}

	public void setCancelAll() {
		selbobysid.clear();
		canlebobysid.clear();
		sel_state = 2;
		notifyDataSetChanged();
	}

	public void clearNameCache(){
		nameCache.clear();
	}
	//int toltal = 0;

	@Override
	public int getCount() {
		if(mData!=null){
			return mData.size();
		}
		return 0;
	}
	
	

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.simi_suo_items_contacts, null);
			holder = new ViewHold();
			holder.userName = (TextView) convertView
					.findViewById(R.id.tv_head_name);
			// holder.checkdiv = convertView.findViewById(R.id.check_relative);
			holder.checkbox = (CheckBox) convertView
					.findViewById(R.id.dc_group_check);
			holder.telphone = (TextView) convertView
					.findViewById(R.id.gd_tv_telphone);
			holder.datetime = (TextView) convertView
					.findViewById(R.id.gd_tv_head_date);
			// holder.itemdiv = convertView.findViewById(R.id.simi_suo_itemdiv);
			// holder.botitemdiv = convertView
			// .findViewById(R.id.list_bot_simi_suo_itemdiv);

			holder.checkbox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							if (buttonView.getTag() != null) {
								if (isChecked) {
									mActivity.pressAll();
									if (sel_state == 2) {
										sel_state = 3;
									}
									selbobysid.put(
											(String) buttonView.getTag(), "");
									canlebobysid.remove((String) buttonView
											.getTag());
								} else {
									selbobysid.remove((String) buttonView
											.getTag());
									canlebobysid.put(
											(String) buttonView.getTag(), "");
									if(selbobysid.isEmpty()){
										mActivity.pressCancelAll();
									}
								}
							}
						}
					});

			convertView.setTag(holder);
		} else {
			holder = (ViewHold) convertView.getTag();
		}

		PrivateSMSummary item = mData.get(position);

		String number = item.getPhoneNumber();

		if (sel_state == 0) {
			holder.checkbox.setVisibility(View.GONE);
//			holder.datetime.setVisibility(View.VISIBLE);
		} else {
			holder.checkbox.setVisibility(View.VISIBLE);
//			holder.datetime.setVisibility(View.GONE);
		}

		// if(pop != null && pop.isShowing()){
		// holder.checkbox.setVisibility(View.VISIBLE);
		// holder.checkbox.setTag(item);
		// if(selePerson.containsKey(number)){
		// holder.checkbox.setChecked(true);
		// }else{
		// holder.checkbox.setChecked(false);
		// }
		//
		// }else{
		// holder.checkbox.setVisibility(View.GONE);
		// }

		// 名字
		if (!nameCache.containsKey(number)) {
			String name = Tools.getContactsName(mContext, number);
			if (!TextUtils.isEmpty(name) && !name.equals(number)) {
				nameCache.put(number, name);
			} else {
				nameCache.put(number, "");
			}
		}

		if (!TextUtils.isEmpty(nameCache.get(number))) {
			holder.userName.setText(nameCache.get(number));
		} else {
			holder.userName.setText(number);
			holder.telphone.setVisibility(View.GONE);
		}

		// 号码
		holder.telphone.setText("(" + number + ")");

		// 日期
		long latestMessageTime = item.getLatestMessage() != null ? item.getLatestMessage().getTimestamp() : 0L;

		holder.datetime.setText(getDatetime(latestMessageTime));
		
		//checkbox
		holder.checkbox.setTag(item.getPhoneNumber());
		holder.checkbox.setChecked(getSelstate(number));

		return convertView;
	}

	private String getDatetime(long latestMessageTime) {
		String dateStr = null;
		if (latestMessageTime == 0L) {
			dateStr = "--:--";
		} else {
			Calendar cal = Calendar.getInstance();
			int today = cal.get(Calendar.DAY_OF_YEAR);
			cal.setTimeInMillis(latestMessageTime);
			int theDay = cal.get(Calendar.DAY_OF_YEAR);
			if (today == theDay) {
				// show time
				dateStr = format.format(cal.getTime());
			} else {
				// show date
				dateStr = fmt.format(cal.getTime());
			}
		}
		return dateStr;
	}

	private boolean getSelstate(String phonenum) {
		// 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根
		if (sel_state == 0) {
			return false;
		} else if (sel_state == 1) {
			if (canlebobysid.containsKey(phonenum)) {
				return false;
			}
			return true;
		} else if (sel_state == 2) {
			return false;
		} else if (sel_state == 3) {
			if (selbobysid.containsKey(phonenum)) {
				return true;
			}
		}
		return false;
	}

	public void setData(List<PrivateSMSummary> psimipersons) {
	//	toltal = 0;
		mData = psimipersons;
//		if (mData != null) {
//			toltal = mData.size();
//		}
	}

	public void updateData() {
		notifyDataSetChanged();
	}

	class ViewHold {
		TextView userName;
		TextView telphone;
		TextView datetime;
		// View checkdiv;
		CheckBox checkbox;
		// View itemdiv;
		// View botitemdiv;
	}

	public List<PrivateSMSummary> getSelteItems() {
		ArrayList<PrivateSMSummary> sleitem = new ArrayList<PrivateSMSummary>();
		// 0 不显示，隐藏 1，全选，根据canle掉的内容，来判断是否选中，2，全部取消 3，显示，根据选中的来判断
		if (sel_state == 0) {

		} else if (sel_state == 1) {
			int size = mData.size();
			for (int i = 0; i < size; i++) {
				PrivateSMSummary item = mData.get(i);
				if (!canlebobysid.containsKey(item.getPhoneNumber())) {
					sleitem.add(item);
				}
			}
		} else if (sel_state == 2) {

		} else if (sel_state == 3) {

			int size = mData.size();
			for (int i = 0; i < size; i++) {
				PrivateSMSummary item = mData.get(i);
				if (selbobysid.containsKey(item.getPhoneNumber())) {
					sleitem.add(item);
				}
			}

		}

		return sleitem;
	}
}
