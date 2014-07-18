package com.wxxr.test.business;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

public class BusinessManagerModule extends AbstractModule<ComHelperAppContext> implements IBusinessManager{

	private static final Trace log = Trace.register(BusinessManagerModule.class);
	private List<BusinessManagerBean> lists = new ArrayList<BusinessManagerBean>();
	private LinkedList<Integer> businessIcons = new LinkedList<Integer>();
	private Context mContext;
	private int[] icons;
	
//	public BusinessManagerModule(){}
//	
//	public BusinessManagerModule(Context context){
//		this.mContext = context;
//		initBusinessData();
//		initIconData();
//	}
	
	@Override
	protected void initServiceDependency() {
		
	}

	@Override
	protected void startService() {
		//不注册这个接口，activity的getService里就取不到这个接口
		context.registerService(IBusinessManager.class, this);
		initBusinessData();
		initIconData();
	}

	@Override
	protected void stopService() {
		lists.clear();
		context.unregisterService(IBusinessManager.class, this);

	}

	protected Context getAndroidContext() {
		return context.getApplication().getAndroidApplication();
	}
	
	/**
	 * 初始化数据 
	 */
	private void initBusinessData(){
		InputStream in = null;
		try {
			in = getAndroidContext().getResources().getAssets()
					.open("BusinessForProvinces.xml");
//			in = mContext.getResources().getAssets()
//					.open("BusinessForProvinces.xml");
			BusinessParseUtil bpu = new BusinessParseUtil();
			lists = bpu.doParse(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化标识数据
	 */
	private void initIconData(){
		icons = new int[] {R.drawable.account_remanning, R.drawable.order_service, R.drawable.gprs_remanning, R.drawable.sms_remanning};
	}
	
	/**
	 * 业务名称
	 */
	@Override
	public String[] getBusinessNames(String name) {
		if(!lists.isEmpty()){
			for (BusinessManagerBean bean : lists) {
				if(name.equals(bean.getName())){
					return bean.getBusinessNames();
				}
			}
		}
		return null;
	}

	/**
	 * 业务标识
	 */
	@Override
	public LinkedList<Integer> getBusinessIcon(String name) {
		if(!lists.isEmpty()){
			for (BusinessManagerBean bean : lists) {
				if(name.equals(bean.getName())){
					String[] aIcons = bean.getBusinessIcons();
					for(int i = 0; aIcons != null && i < aIcons.length; i++){
						int value = Integer.parseInt(aIcons[i]);
						businessIcons.add(icons[value]);
					}
				}
			}
		}
		return businessIcons;
	}

	/**
	 * 业务代码
	 */
	@Override
	public String[] getBusinessCodes(String name) {
		if(!lists.isEmpty()){
			for(BusinessManagerBean bean : lists){
				if(name.equals(bean.getName())){
					return bean.getBusinessCode();
				}
			}
		}
		return null;
	}
	
}
