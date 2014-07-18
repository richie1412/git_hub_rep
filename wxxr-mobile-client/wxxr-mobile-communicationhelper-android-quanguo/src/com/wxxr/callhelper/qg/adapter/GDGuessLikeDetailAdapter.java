package com.wxxr.callhelper.qg.adapter;

import java.util.ArrayList;
import java.util.List;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.rpc.ChannelMsg;
import com.wxxr.callhelper.qg.rpc.ChannelMsgPageVo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * 详情列表
 * 
 * @author cuizaixi
 * 
 */
public class GDGuessLikeDetailAdapter extends BaseAdapter {
	private Context mContext;
	private List<String> cms;
	public GDGuessLikeDetailAdapter(Context context) {
		this.mContext = context;
		cms = new ArrayList<String>();
	}
	public void setData(List<String> data) {
		this.cms = data;
	}
	public List<String> getData() {
		return this.cms;
	}
	public void removeData(int location) {
		this.cms.remove(location);
	}
	@Override
	public int getCount() {
		return cms.size();
	}

	@Override
	public Object getItem(int position) {
		return cms.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = (LinearLayout) LinearLayout.inflate(this.mContext,
					R.layout.guess_like_detail_item, null);
			holder.name = (TextView) convertView
					.findViewById(R.id.tv_guess_like_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(this.cms.get(position));
		return convertView;
	}
	private class ViewHolder {
		ImageView head;
		TextView name;
	}
}