package com.wxxr.callhelper.qg.service;

import java.util.List;

import com.wxxr.callhelper.qg.intercept.rule.ISmsHandler;

public interface ILouHuaHuizhiService {
	public List<ISmsHandler>  getSmsHandlers();
	public boolean isMatch(String smsContent, String targetnumber);
	
}
