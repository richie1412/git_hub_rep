package com.wxxr.callhelper.qg.ui;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.SeekedListDataAdapter;
import com.wxxr.callhelper.qg.SeekedListDataAdapter.ListedEntity;
import com.wxxr.callhelper.qg.adapter.AbstractCommonAdapter;
import com.wxxr.callhelper.qg.adapter.SeekedAdapter;
import com.wxxr.callhelper.qg.bean.MoblieBusinessBean;
import com.wxxr.callhelper.qg.bean.OptionalDialogBean;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.service.IClientCustomService;
import com.wxxr.callhelper.qg.service.IGuiShuDiService;
import com.wxxr.callhelper.qg.service.IHomeNotice;
import com.wxxr.callhelper.qg.service.IMoblieBusiness;
import com.wxxr.callhelper.qg.ui.gd.BaseActityExtend;
import com.wxxr.callhelper.qg.ui.gd.GDCMProgressMonitor;
import com.wxxr.callhelper.qg.utils.MobileBusinessStore;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.callhelper.qg.widget.BladeView;
import com.wxxr.callhelper.qg.widget.BladeView.OnTextItemClickListener;

/**
 * 个人中心——选择地区界面
 * 
 * @author cuizaixi
 * 
 */
public class PrivateZooLocationActivity extends BaseActityExtend {

	private ListView lv_cities;
	private BladeView bv;
	private SeekedListDataAdapter mDataModule;
	private View iv_left;
	private View iv_right;
	private TextView tv_title_name;
	private SeekedAdapter mAdapter;
	private ListView lv_hot_cities;
	private List<String> hotCites;
	private String mProviceAccordingNum;
	private String mSelecteProvice;
	private LinearLayout ll_city;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.private_zoo_select_location);
		findView();
		processLogic();

	}
	protected void findView() {
		iv_left = findViewById(R.id.gd_iv_titlebar_left);
		iv_right = findViewById(R.id.gd_iv_titlebar_right);
		tv_title_name = (TextView) findViewById(R.id.tv_titlebar_name);
		tv_title_name.setText("选择号码归属省份");
		iv_right.setVisibility(View.INVISIBLE);
		ll_city = (LinearLayout) LinearLayout.inflate(this, R.layout.hot_cites,
				null);
		lv_cities = (ListView) findViewById(R.id.lv_cities);
		lv_hot_cities = (ListView) ll_city.findViewById(R.id.lv_hot_cities);
		bv = (BladeView) findViewById(R.id.bv);
		bv.setBladeViewTitle("热门");
	};
	protected void processLogic() {
		mDataModule = new SeekedListDataAdapter(getResources().getStringArray(
				R.array.items));
		mDataModule.prepareEntitiesList();
		mAdapter = new SeekedAdapter(R.layout.location_item, R.id.title,
				R.id.content, mDataModule, this);
		lv_cities.addHeaderView(ll_city);
		lv_cities.setAdapter(mAdapter);
		String[] str = getResources().getStringArray(R.array.hot_city);
		hotCites = Arrays.asList(str);
		lv_hot_cities.setAdapter(new HotCitesAdapter(hotCites, this));
		boolean isUserActivated = getService(IUserActivationService.class)
				.isUserActivated();
		if (isUserActivated) {
			String id = getService(IUserActivationService.class)
					.getCurrentUserId();
			if (!StringUtil.isEmpty(id)) {
				Region region = getFramework().getService(
						IGuiShuDiService.class).getRegionByMsisdn(id);
				if (region != null) {
					mProviceAccordingNum = region.getRegionName();
				}
			}

		}
		iv_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		bv.setOnItemClickListener(new OnTextItemClickListener() {

			@Override
			public void onItemClick(String s) {
				if (mDataModule.getIndex().get(s) != null) {
					lv_cities.setSelection(mDataModule.getIndex().get(s) + 1);
				}
			}
		});

		lv_cities.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListedEntity entity = (ListedEntity) mAdapter.getItem(position-1);
				mSelecteProvice = entity.getName();
				if (mSelecteProvice != null) {
					ConfirmProviceCode(mSelecteProvice);
				}
			}
		});
		lv_hot_cities.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mSelecteProvice = hotCites.get(position);
				ConfirmProviceCode(mSelecteProvice);
			}
		});
	}
	class HotCitesAdapter extends AbstractCommonAdapter<String> {

		public HotCitesAdapter(List<String> data, Context context) {
			super(data, context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout view = (LinearLayout) getInflater().inflate(
					R.layout.hot_cites_item, null);
			TextView tv = (TextView) view.findViewById(R.id.tv_hot_city);
			tv.setText(mData.get(position));
			return view;
		}

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
			case RESULT_OK :
				setProviceCode();
				break;
			case RESULT_CANCELED :
				break;
		}
	}
	private void ConfirmProviceCode(String provice) {
		if (mProviceAccordingNum != null
				&& !mSelecteProvice.equals(mProviceAccordingNum)) {
			Intent intent = new Intent(PrivateZooLocationActivity.this,
					OptionalDialogActivity.class);
			intent.putExtra(OptionalDialogActivity.OPTIONAL_BEAN,
					new OptionalDialogBean(null,
							"您的号码不属于该地区，确定选择后可能会给您的使用带来不便（详情请查看个人中心-条款-地区选择.)"));
			startActivityForResult(intent, 1);
		} else {
			setProviceCode();
		}
	}
	private void setProviceCode() {
		
		
		
		GDCMProgressMonitor monitor = new GDCMProgressMonitor(this) {

			@Override
			protected void handleFailed(Throwable cause) {

			}

			@Override
			protected void handleDone(Object returnVal) {
				startActivity(new Intent(PrivateZooLocationActivity.this,
						HomeActivity.class));
				if(getIntent().getStringExtra("from")!=null){
					PrivateZooLocationActivity.this.finish();
				}
			}

//			@Override
//			protected Map<String, Object> getDialogParams() {
//				
//				return super.getDialogParams();
//			}
		};

		monitor.executeOnMonitor(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				String proviceCode = Constant.getProviceCode(mSelecteProvice);
				String old=getService(IClientCustomService.class).getProviceCode();
				getService(IClientCustomService.class).setProviceCode(proviceCode);
				if(!proviceCode.equals(old)){
					getService(IClientConfigManagerService.class).forceLoadURL();
					getService(IHomeNotice.class).getHomeNotice(proviceCode);
					MobileBusinessStore.getInstance(PrivateZooLocationActivity.this).setBusinessCheckText("");
					MobileBusinessStore.getInstance(PrivateZooLocationActivity.this).setBusinessProcessText("");
				}
				return null;
			
			}
		});
	
	}
}