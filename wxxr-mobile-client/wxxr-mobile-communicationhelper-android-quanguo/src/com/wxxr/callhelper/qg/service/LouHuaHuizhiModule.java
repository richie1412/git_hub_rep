package com.wxxr.callhelper.qg.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.LouhuaSmsInterceptRuleDescriptor;
import com.wxxr.callhelper.qg.intercept.rule.CongzhiSmsHandler;
import com.wxxr.callhelper.qg.intercept.rule.HuizhiSmsHandler;
import com.wxxr.callhelper.qg.intercept.rule.ILouhuaSmsInterceptRule;
import com.wxxr.callhelper.qg.intercept.rule.ISmsHandler;
import com.wxxr.callhelper.qg.intercept.rule.ISmsInterceptRule;
import com.wxxr.callhelper.qg.intercept.rule.LouhuaSmsHandler;
import com.wxxr.callhelper.qg.intercept.rule.LouhuaSmsInterceptRule;
import com.wxxr.callhelper.qg.utils.PullParseUtil;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

public class LouHuaHuizhiModule extends AbstractModule<ComHelperAppContext>
		implements
			ILouHuaHuizhiService {
	private static final Trace log = Trace.register(LouHuaHuizhiModule.class);

	private List<ISmsHandler> handlers = new ArrayList<ISmsHandler>();
	private ISmsInterceptRule louhuaDelegate;
	private List<ILouhuaSmsInterceptRule> louhuaRules = new ArrayList<ILouhuaSmsInterceptRule>();

	public List<ISmsHandler> getSmsHandlers() {
		return handlers;

	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IClientCustomService.class);
	}

	@Override
	protected void startService() {
		context.registerService(ILouHuaHuizhiService.class, this);
		initLouhuaRule();
		handlers.add(new LouhuaSmsHandler(louhuaDelegate));
		handlers.add(new HuizhiSmsHandler());
		handlers.add(new CongzhiSmsHandler());
	}

	protected Context getAndroidContext() {
		return context.getApplication().getAndroidApplication();
	}
	// 是否存在该省份的规则
	protected ILouhuaSmsInterceptRule getRule(String provinceCode) {
		if(provinceCode!=null){
		for (ILouhuaSmsInterceptRule r : louhuaRules) {
			if (provinceCode.equals(r.getName())) {
				return r;
			}
		}
		}
		return null;
	}
	public List<ILouhuaSmsInterceptRule> initLouhuaRule() {

		InputStream is = null;
		try {
			is = getAndroidContext().getResources().getAssets()
					.open("ReminderForProvinces.xml");
			PullParseUtil service = new PullParseUtil();
			List<LouhuaSmsInterceptRuleDescriptor> lsrds = service.doParse(is);
			for (LouhuaSmsInterceptRuleDescriptor r : lsrds) {
				LouhuaSmsInterceptRule province = new LouhuaSmsInterceptRule(
						r.getName(), r.getPatterns());
				louhuaRules.add(province);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		log.info("louhuaRules******************", louhuaRules.toString() + "");

		louhuaDelegate = new ISmsInterceptRule() {
			@Override
			public boolean isMatch(String smsContent, String targetnumber) {
				// 获取用户选择的省份代码
				// 判段用户是否选择了省份，如果该省份存在规则，则按照该规则拦截，如果用户没选省份或者省份没有规则则按照所有规则来拦截
				String code = context.getService(IClientCustomService.class)
						.getProviceCode();
				ILouhuaSmsInterceptRule r1 = getRule(code);
				if (r1 != null) {
					return r1.isMatch(smsContent, targetnumber);
				} else {
					for (ILouhuaSmsInterceptRule r : louhuaRules) {
						boolean x = r.isMatch(smsContent, targetnumber);
						if (x) {
							return true;
						}
					}
					return false;
				}
			}
		};
		return louhuaRules;
	}

	@Override
	public boolean isMatch(String smsContent, String targetnumber) {
		return this.louhuaDelegate.isMatch(smsContent, targetnumber);
	}

	@Override
	protected void stopService() {
		context.unregisterService(ILouHuaHuizhiService.class, this);
		handlers.clear();
		louhuaRules.clear();
		handlers = null;
		louhuaDelegate = null;
	}

}
