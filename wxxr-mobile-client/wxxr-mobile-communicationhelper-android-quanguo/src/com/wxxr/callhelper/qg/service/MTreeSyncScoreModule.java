package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.sync.IDataConsumer;
import com.wxxr.callhelper.qg.sync.IMTreeDataSyncServerConnector;
import com.wxxr.callhelper.qg.sync.impl.MTreeDataSyncClientService;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

public class MTreeSyncScoreModule extends AbstractModule<ComHelperAppContext> implements IMTreeSyncDataEngineService{

	MTreeDataSyncClientService client;
	
	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IDataExchangeCoordinator.class);
		addRequiredService(IMTreeDataSyncServerConnector.class);
	
		
	}

	
	
	@Override
	protected void startService() {
		context.registerService(IMTreeSyncDataEngineService.class, this);
		// TODO Auto-generated method stub
		client=new MTreeDataSyncClientService();
		client.init(context);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IMTreeSyncDataEngineService.class, this);
		
	}






	@Override
	public void registerConsumer(String key, IDataConsumer consumer) {
		client.registerConsumer(key, consumer);		
		
	}



	


	@Override
	public boolean unregisterConsumer(String key, IDataConsumer provider) {
		// TODO Auto-generated method stub
		return client.unregisterConsumer(key, provider);
	}


	@Override
	public void startSync() {
		client.start();
	}


	@Override
	public void stopSync() {
	//	client.stop();
		
	}

}
