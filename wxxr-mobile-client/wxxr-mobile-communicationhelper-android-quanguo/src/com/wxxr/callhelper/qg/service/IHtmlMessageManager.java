package com.wxxr.callhelper.qg.service;

import java.io.IOException;

import com.wxxr.callhelper.qg.bean.HtmlMessage;

public interface IHtmlMessageManager {
	public String download(String url)throws IOException;
	public boolean syncDownload(String url,String root);
	boolean isDownloaded(String id);
	public HtmlMessage getHtmlMessage(String id);
	public void removeHmtlMessage(String id)throws IOException;
}
