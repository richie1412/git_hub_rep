/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import java.util.Map;

/**
 * @author neillin
 *
 */
public interface ChannelParamUpdater {
	boolean update(Map<String, String> params,DXHZSetting setting);
	void extract(DXHZSetting setting,Map<String, String> params);
}
