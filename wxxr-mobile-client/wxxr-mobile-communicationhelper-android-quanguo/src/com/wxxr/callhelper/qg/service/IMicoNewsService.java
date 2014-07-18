package com.wxxr.callhelper.qg.service;

import com.wxxr.callhelper.qg.exception.NetworkNotAvailableException;
import com.wxxr.callhelper.qg.rpc.ChannelMsgPageVo;

public interface IMicoNewsService {
	ChannelMsgPageVo getChanleDetailMessage(String chanalid,
			String countperpage, String curpage)
			throws NetworkNotAvailableException;
}
