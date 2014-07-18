package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.rpc.AbstractMonitorRunnable;
import com.wxxr.callhelper.qg.utils.StringUtil;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

public class SmsContentParseModule extends AbstractModule<ComHelperAppContext> implements ISmsContentParseService{
	private static final Trace log = Trace.register(SmsContentParseModule.class);

//	private IGuiShuDiService guiShuDiService=null;
	
//	private IGuiShuDiService getGuiShuDiService() {
//		if(this.guiShuDiService == null){
//			this.guiShuDiService = context.getService(IGuiShuDiService.class);
//		}
//		return this.guiShuDiService;
//	}
	
	@Override
	protected void initServiceDependency() {
//		addRequiredService(IGuiShuDiService.class);
	}

//	private void doinit() {
//		String x=parseSmsContent("致13810052592短信送达");
//		System.out.println(x);
//	}

	@Override
	protected void startService() {
		context.registerService(ISmsContentParseService.class, this);
//		doinit();

	}

	@Override
	protected void stopService() {
		context.unregisterService(ISmsContentParseService.class, this);
	}
	//连续执行几十次会很有问题，可以直接使用 parseSmsContent(String )
	public void parseSmsContent(final String content,IProgressMonitor m) {
		log.info("begin parseSmsContent " + content);
		context.getExecutor().execute(new AbstractMonitorRunnable(m, log) {
			@Override
			protected Object executeTask() throws Throwable {
				
				return parseSmsContent(content);
			}
		});
	}
	//1.获取手机号 
	//2.将手机号  替换为  （联系人）+手机号+归属地
	public String parseSmsContent(String content) {
		
		String msisdn =Tools.getMisdnByContent(content);  
		if (StringUtil.isEmpty(msisdn)){
			return content;
		}
		String replace_src=null;
		//查找通讯录名称
		String name=Tools.getContactsName(context.getApplication().getAndroidApplication(),msisdn);
		if (StringUtil.isNotEmpty(name)){
			replace_src=msisdn+"("+name+")";
		}else{
			replace_src=msisdn;
		}
		//msisdn
//	    Region r=getGuiShuDiService().getRegionByMsisdn(msisdn);
//	    if (r!=null){
//	    	replace_src=replace_src+"("+r.getRegionName()+")";
//	    }
	    return content.replaceFirst(msisdn, replace_src);
		
	}
	
}
