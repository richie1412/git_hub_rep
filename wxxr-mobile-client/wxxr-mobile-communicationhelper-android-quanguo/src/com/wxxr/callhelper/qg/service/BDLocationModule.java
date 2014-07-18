package com.wxxr.callhelper.qg.service;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

public class BDLocationModule extends AbstractModule<ComHelperAppContext>
		implements
			IBDLocationService {

	private LocationClient mLocationClient;
	private Context mContext;
	private CityNameListener mCityNameListener;
	@Override
	public void getCityName(CityNameListener listener, Context context) {
		this.mContext = context;
		this.mCityNameListener = listener;
		mLocationClient = getLocationClient();
		mLocationClient.registerLocationListener(mLocationListener);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
			mLocationClient.requestLocation();
		}
	}

	@Override
	protected void initServiceDependency() {

	}

	@Override
	protected void startService() {
		context.registerService(IBDLocationService.class, this);
	};
	@Override
	protected void stopService() {
	}
	public synchronized LocationClient getLocationClient() {
		if (mLocationClient == null)
			mLocationClient = new LocationClient(mContext,
					getLocationClientOption());
		return mLocationClient;
	}
	private LocationClientOption getLocationClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setPoiExtraInfo(true);
		option.setAddrType("all");
		option.setServiceName(mContext.getPackageName());
		option.setScanSpan(0);
		option.disableCache(true);
		return option;
	}
	BDLocationListener mLocationListener = new BDLocationListener() {

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// do nothing
		}
		@Override
		public void onReceiveLocation(BDLocation location) {
			String cityName = location.getCity();
			mCityNameListener.onCityNameReceive(cityName);
			mLocationClient.stop();
		}
	};
}
