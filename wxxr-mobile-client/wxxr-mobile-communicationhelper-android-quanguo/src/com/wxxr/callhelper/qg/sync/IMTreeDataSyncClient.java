/**
 * 
 */
package com.wxxr.callhelper.qg.sync;

/**
 * @author neillin
 *
 */
public interface IMTreeDataSyncClient {
		
	void clearCache(String key);
	
	void registerConsumer(String key, IDataConsumer provider);
	
	boolean unregisterConsumer(String key, IDataConsumer provider);
}
