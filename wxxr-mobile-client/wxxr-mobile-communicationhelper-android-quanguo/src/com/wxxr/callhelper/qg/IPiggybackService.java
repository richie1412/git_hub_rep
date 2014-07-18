/**
 * 
 */
package com.wxxr.callhelper.qg;

/**
 * @author neillin
 *
 */
public interface IPiggybackService {
	PiggybackPayload getPiggybackPayload();
	
	void registerProvider(int type, IPiggybackPayloadProvider provider);
	boolean unregisterProvider(int type, IPiggybackPayloadProvider provider);
}
