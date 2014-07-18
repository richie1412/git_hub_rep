package com.wxxr.callhelper.qg.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.Region;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
/**
 * 既可以查归属地又可以查10086的
 * 
 * @author cuizaixi
 * 
 */
public class AdvancedRegionQueryModule
		extends
			AbstractModule<ComHelperAppContext>
		implements
			IAdvaceRegionQueryService {
	@Override
	public String getRegionByDialogNumber(String number) {
		return null;
	}
	@Override
	public String getRegionByNum(String number, Context context) {
		List<ICommonNumberService> handles = new ArrayList<ICommonNumberService>();
		handles.add(new CommonNumberQuery());
		handles.add(new GuishudiServiceWraper());
		for (ICommonNumberService handle : handles) {
			if (handle.isHandle(number)) {
				return handle.getRegion();
			}
		}
		return null;
	}
	public String getRegionWithoutBrand(String number) {
		List<ICommonNumberService> handles = new ArrayList<ICommonNumberService>();
		handles.add(new CommonNumberQuery());
		handles.add(new GuishudiServiceWraper());
		for (ICommonNumberService handle : handles) {
			if (handle.isHandle(number)) {
				return handle.getRegionWithoutBrand();
			}
		}
		return null;
	}
	@Override
	protected void initServiceDependency() {
		addRequiredService(IGuiShuDiService.class);
	}

	@Override
	protected void startService() {
		context.registerService(IAdvaceRegionQueryService.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IAdvaceRegionQueryService.class, this);
	}

}
