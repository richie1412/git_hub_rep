package com.wxxr.callhelper.qg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.AppsRecommend;

public class AppsRecommendAdapter extends BaseAdapter {
	private List<AppsRecommend> apps;
	private Context mContext;
	public AppsRecommendAdapter(Context context) {
		apps = new ArrayList<AppsRecommend>();
		this.mContext = context;
	}
	public void setData(List<AppsRecommend> apps) {
		this.apps = apps;
	}
	@Override
	public int getCount() {
		return apps.size();
	}
	@Override
	public Object getItem(int position) {
		return apps.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LinearLayout.inflate(mContext,
					R.layout.apps_recommend_item, null);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.iv_apps_icon);
			holder.name = (TextView) convertView
					.findViewById(R.id.tv_apps_name);
			holder.description = (TextView) convertView
					.findViewById(R.id.tv_apps_description);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		AppsRecommend app = apps.get(position);
		holder.icon.setBackgroundResource(app.getSourceID());
		holder.name.setText(app.getName());
		holder.description.setText(app.getDescription());
		return convertView;
	}
	private class ViewHolder {
		private ImageView icon;
		private TextView name;
		private TextView description;
	}
}
