/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.rule.api.RuleAdministrator;
import com.wxxr.mobile.rule.api.RuleRuntime;
import com.wxxr.mobile.rule.impl.AbstractRuleEngineService;
import com.wxxr.mobile.rule.impl.ConfigurationException;
import com.wxxr.mobile.rule.impl.RuleEngine;

/**
 * @author wangyan
 *
 */
public class RuleEngineService extends AbstractModule<ComHelperAppContext> implements RuleEngine{

	private static Trace log =Trace.register(RuleEngineService.class);
	private RuleEngineServiceimpl ruleEngineServiceImpl;
	
	class RuleEngineServiceimpl extends AbstractRuleEngineService{

		@Override
		protected Element loadRulesetConfig() {
			InputStream is=null;
			try {
				is = context.getApplication().getAndroidApplication().getResources().getAssets().open("rulesets.xml");
				Document document=Jsoup.parse(is, "UTF-8", "");
				return document;
			} catch (IOException e) {
				log.error("加载规则集文件失败",e);
			}finally{
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {}
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see com.wxxr.mobile.rule.impl.AbstractRuleEngineService#startService()
		 */
		@Override
		public void startService() throws Exception {
			super.startService();
		}

		/* (non-Javadoc)
		 * @see com.wxxr.mobile.rule.impl.AbstractRuleEngineService#stopService()
		 */
		@Override
		public void stopService() throws Exception {
			super.stopService();
		}
		
	
	}
	@Override
	public RuleAdministrator getRuleAdministrator() {
		return ruleEngineServiceImpl.getRuleAdministrator();
	}



	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void startService() {
		ruleEngineServiceImpl=new RuleEngineServiceimpl();
		try {
			ruleEngineServiceImpl.startService();
			context.registerService(RuleEngine.class, this);
		} catch (Exception e) {
			log.error("rule engine service start failed");
		}
	}

	@Override
	protected void stopService() {
		try {
			ruleEngineServiceImpl.stopService();
		} catch (Exception e) {
			log.error("rule engine service stop failed");
		}
		ruleEngineServiceImpl=null;
		context.unregisterService(RuleEngine.class, this);
	}



	@Override
	public RuleRuntime getRuleRuntime() throws ConfigurationException {
		return ruleEngineServiceImpl.getRuleRuntime();
	}

}
