/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.Map;

import com.wxxr.callhelper.qg.rpc.ChannelParamUpdater;
import com.wxxr.callhelper.qg.rpc.DXHZSetting;
import com.wxxr.callhelper.qg.utils.Tools;

/**
 * @author neillin
 *
 */
public class YuerChannel implements ChannelParamUpdater {

	@Override
	public boolean update(Map<String, String> params, DXHZSetting setting) {
		String val = params.get(DXHZSetting.KEY_FYCHANNEL_DATE);
		String old = setting.getFychanneldate();
		boolean updated = false;
		if(!Tools.equals(val, old)){
			setting.setFychanneldate(val);
			updated = true;
		}
		return updated;
	}

	@Override
	public void extract(DXHZSetting setting, Map<String, String> params) {
		params.put(DXHZSetting.KEY_FYCHANNEL_DATE, setting.getFychanneldate());
	}


}
