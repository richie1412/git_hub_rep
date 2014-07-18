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
public class SjzxChannelUpdater implements ChannelParamUpdater {

	@Override
	public boolean update(Map<String, String> params, DXHZSetting setting) {
		String val = params.get(DXHZSetting.KEY_USER_DATE);
		String old = setting.getBirthday();
		boolean updated = false;
		if(!Tools.equals(val, old)){
			setting.setBirthday(val);
			updated = true;
		}
		val = params.get(DXHZSetting.KEY_USER_GENDER);
		old = setting.getSex() != null ? setting.getSex().toString() : null;
		if(!Tools.equals(val, old)){
			try {
				setting.setSex(val != null ? Integer.parseInt(val) : null);
				updated = true;
			}catch(Throwable t){}
		}
		return updated;
	}

	@Override
	public void extract(DXHZSetting setting, Map<String, String> params) {
		params.put(DXHZSetting.KEY_USER_DATE, setting.getBirthday());
		params.put(DXHZSetting.KEY_USER_GENDER, setting.getSex() != null ? setting.getSex().toString() : null);
	}


}
