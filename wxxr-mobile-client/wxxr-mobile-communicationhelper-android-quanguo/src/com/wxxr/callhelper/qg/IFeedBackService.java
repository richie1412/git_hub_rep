package com.wxxr.callhelper.qg;

import com.wxxr.mobile.core.api.IProgressMonitor;

public interface IFeedBackService {
	
	void addFeedBack(String content,String deviceId,Integer type, IProgressMonitor monitor);
}
