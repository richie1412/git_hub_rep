package com.wxxr.callhelper.qg.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.utils.ManagerAsyncImageLoader;
import com.wxxr.callhelper.qg.utils.ManagerAsyncImageLoader.ImageCallback;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * 两种业务adapter
 * @author yinzhen
 *
 */
public class QGBusinessQueryManagerAdapter extends BaseAdapter {

	private List<MoblieBusinessBean> mLists;
	private Context mContext;
	private String mType;
	private Map<Integer, Bitmap> bitmapMaps = new HashMap<Integer, Bitmap>();
	private ManagerAsyncImageLoader loader = new ManagerAsyncImageLoader();
	private Map<String, String> descriptionMaps = new HashMap<String, String>();
	
	public QGBusinessQueryManagerAdapter(Context context) {
		this.mContext = context;
	}

	public void setData(List<MoblieBusinessBean> lists, String type) {
		this.mLists = lists;
		this.mType = type;
	}

	public void clearLists(){
		descriptionMaps.clear();
		descriptionMaps = null;
	}
	
	public void updateData(){
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (null != mLists) {
			return mLists.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		BusinessViewHandler handler = null;
		final MoblieBusinessBean bean = mLists.get(position);
		if (convertView == null) {
			view = View.inflate(mContext,
					R.layout.qg_business_query_manager_item, null);
			handler = new BusinessViewHandler();
			handler.businessName = (TextView) view
					.findViewById(R.id.qg_tv_business_name);
			handler.businessIcon = (ImageView) view
					.findViewById(R.id.qg_iv_business_icon);
			handler.businessDescription = (TextView) view
					.findViewById(R.id.qg_tv_business_item_description);
			handler.businessQuery = (Button) view
					.findViewById(R.id.qg_btn_business);
			view.setTag(handler);
		} else {
			view = convertView;
			handler = (BusinessViewHandler) view.getTag();
		}
		
		
		handler.businessQuery.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String smsContent = bean.getSmscode();
				String smsAddress = bean.getSpNumber();
				if(!TextUtils.isEmpty(smsContent) && !TextUtils.isEmpty(smsAddress)){
					Tools.ensureSIMIsOk(mContext);
					Tools.sendMsg(mContext, smsContent, smsAddress, 0);
				}
			}
		});
		
		handler.businessQuery.setText(mType);
		handler.businessName.setText(bean.getTile());
		
		if(!(bean.getDescription()).equals("null")){
			descriptionMaps.put(bean.getTile(), bean.getDescription());
		}
		
		if(descriptionMaps.containsKey(bean.getTile())){
			handler.businessDescription.setVisibility(View.VISIBLE);
			handler.businessDescription.setText(descriptionMaps.get(bean.getTile()));
		}else{
			handler.businessDescription.setVisibility(View.GONE);
		}
		
		String iconPath = bean.getIcon();
	//	Bitmap bitmap = Tools.getBusinessBitmap(iconPath);
		
		Drawable icon= loader.loadDrawable(iconPath, new ImageCallback(){

			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				updateData();
			}
			
		});
		if(icon!=null){
			handler.businessIcon.setBackgroundDrawable(icon);
		}
		
		return view;
	}

}

class BusinessViewHandler {
	TextView businessName;
	ImageView businessIcon;
	TextView businessDescription;
	Button businessQuery;
}
