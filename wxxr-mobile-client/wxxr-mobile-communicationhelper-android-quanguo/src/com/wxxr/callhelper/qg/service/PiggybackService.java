/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IPiggybackPayloadProvider;
import com.wxxr.callhelper.qg.IPiggybackService;
import com.wxxr.callhelper.qg.PiggybackPayload;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * 搭载管理服务
 * 负责搭载功能实现，主要包括以下职责：
 * 1.注册/卸载搭载内容提供者皆
 * 2。获取搭载内容
 * @author wangxuyang
 *
 */
public class PiggybackService extends AbstractModule<ComHelperAppContext> implements IPiggybackService {
	private static final Trace log = Trace.register(PiggybackService.class);
	private Map<Integer,IPiggybackPayloadProvider> providers = new ConcurrentHashMap<Integer, IPiggybackPayloadProvider>();
	
	public PiggybackPayload getPiggybackPayload() {		
		PiggybackPayload payload = getNextPiggybackPayload();
		if (log.isDebugEnabled()) {
			log.debug("Generate payload:"+(payload==null?null:payload.toString()));
		}
		return payload;
	}

	private PiggybackPayload getNextPiggybackPayload(){
		for (Map.Entry<Integer, IPiggybackPayloadProvider> providerEntry : providers.entrySet()) {
			byte[] data = null;
			if ((data=providerEntry.getValue().getPayload())!=null) {
				 PiggybackPayload payload = new PiggybackPayload();
				 payload.setPayloadId(providerEntry.getKey());
				 payload.setData(data);				
				 return payload;
			}
		}
		return null;
	}
	public void registerProvider(int type, IPiggybackPayloadProvider provider) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Registering a piggyback provider:[type:%d,provider:%s]", type,provider));
		}
		providers.put(type, provider);
	}

	
	public boolean unregisterProvider(int type, IPiggybackPayloadProvider provider) {
		if (log.isDebugEnabled()) {
			log.debug(String.format("Unregistering a piggyback provider:[type:%d,provider:%s]", type,provider));
		}
		IPiggybackPayloadProvider l_provider = providers.remove(type);
		return l_provider!=null&&l_provider.equals(provider);
	}

	@Override
	protected void initServiceDependency() {
		
	}

	@Override
	protected void startService() {
		context.registerService(IPiggybackService.class, this);		
	}

	@Override
	protected void stopService() {
		context.unregisterService(IPiggybackService.class, this);		
	}

}
