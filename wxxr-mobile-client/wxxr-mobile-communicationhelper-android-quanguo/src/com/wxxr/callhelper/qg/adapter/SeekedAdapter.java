package com.wxxr.callhelper.qg.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.SeekedListDataAdapter;
import com.wxxr.callhelper.qg.SeekedListDataAdapter.ListedEntity;
/**
 * 拼音搜索通用Adapter
 * 
 * @author cuizaixi
 * 
 */
public class SeekedAdapter extends AbstractListAdapter<ListedEntity>
		implements
			SectionIndexer,
			IFilterExt<ListedEntity> {
	protected SeekedListDataAdapter mDataManager;
	protected Context mContext;
	protected List<ListedEntity> mEntities;
	protected List<ListedEntity> mFilterResult;
	protected int mItemLayoutID;
	protected int mItemTitleID;
	protected int mItemContentID;
	private Map<String, List<ListedEntity>> mMap;
	private List<String> mSections;
	private List<Integer> mPositions;
	/**
	 * 
	 * @param itemLayoutID
	 *            list 资源文件ID;
	 * @param titleID
	 *            list 标题资源文件ID;
	 * @param contentID
	 *            list 内容的资源文件ID;
	 * @param manager
	 *            整合字符窜的类
	 * @param context
	 */
	public SeekedAdapter(int itemLayoutID, int titleID, int contentID,
			SeekedListDataAdapter manager, Context context) {
		super(context);
		this.mItemLayoutID = itemLayoutID;
		this.mItemTitleID = titleID;
		this.mItemContentID = contentID;
		this.mDataManager = manager;
		this.mContext = context;
		this.mEntities = this.mDataManager.getAllEntities();
		mPositions = manager.getPositions();
		mSections = manager.getSelection();
		mMap = manager.getMap();

	}
	public int getCount() {
		return mEntities.size();
	}

	@Override
	public ListedEntity getItem(int position) {
		int section = getSectionForPosition(position);
		return mMap.get(mSections.get(section)).get(
				position - getPositionForSection(section));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int section = getSectionForPosition(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = (LinearLayout) LinearLayout.inflate(mContext,
					mItemLayoutID, null);
			holder.title = (TextView) convertView.findViewById(mItemTitleID);
			holder.content = (TextView) convertView
					.findViewById(mItemContentID);
			holder.divider = convertView.findViewById(R.id.divider);
			holder.cb = (CheckBox) convertView.findViewById(R.id.ch);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.divider.setBackgroundColor(Color.parseColor("#cfcfcf"));
		holder.title.setTextColor(Color.parseColor("#828282"));
		if (getPositionForSection(section) == position) {
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(mSections.get(section));
		} else {
			holder.title.setVisibility(View.GONE);
		}
		holder.content.setText(getItem(position).getName());
		return getView(position, convertView, parent, null);
	}
	class ViewHolder {
		private TextView title;
		private TextView content;
		private View divider;
		private CheckBox cb;
	}
	@Override
	public Object[] getSections() {
		return mPositions.toArray();
	}
	@Override
	public int getPositionForSection(int section) {
		if (section < 0 || section >= getCount()) {
			return -1;
		}
		return mPositions.get(section);
	}
	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				if (results.count > 0) {
					mFilterResult = (List<ListedEntity>) results.values;
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				String upperCase = constraint.toString().toUpperCase();
				FilterResults filterResults = new FilterResults();
				List<ListedEntity> entities = new ArrayList<SeekedListDataAdapter.ListedEntity>();
				for (ListedEntity listedEntity : mEntities) {
					if (listedEntity.getAllPinyin().indexOf(upperCase) > -1
							|| listedEntity.getFirstPingyin()
									.indexOf(upperCase) > -1
							|| listedEntity.getName().indexOf(upperCase) > -1) {
						entities.add(listedEntity);
					}
				}
				filterResults.values = entities;
				filterResults.count = entities.size();
				return filterResults;
			}
		};
		return filter;
	}
	@Override
	public boolean needFilter(boolean b) {
		return false;
	}
	@Override
	public List<ListedEntity> getFilteResult() {
		return mFilterResult;
	}
}
