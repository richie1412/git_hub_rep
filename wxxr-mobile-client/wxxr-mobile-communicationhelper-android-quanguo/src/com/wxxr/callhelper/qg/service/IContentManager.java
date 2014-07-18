/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.IOException;

import com.wxxr.callhelper.qg.bean.HtmlMessage;

/**
 * 
 * @author wangyan
 *
 */
public interface IContentManager {
	
	void saveContent(String type, String id, byte[] content) throws IOException;
	byte[] getContent(String type, String id) throws IOException;
	void delete(String type,String id)throws IOException;
	
	void updateStatus(String type, String id, String statusName, String status) throws IOException;
	String getStatus(String type, String id, String statusName) throws IOException;
	void deleteStatus(String type, String id,String statusName)throws IOException;
	String[] queryContentIds(String type, String statusName, String statusValue) throws IOException;

	boolean isExistContent(String type,String id) ;

	public HtmlMessage getHtmlMessage(String id);
	public HtmlMessage getHtmlMessage(String path,String id);
	public void removeHtmlMesage(String id)throws IOException;
	public void moveHtmlMessage(String oldId,String newId)throws IOException;
	
	public Long getStatusLastModified(String type,String id,String statusName);
	public Long getContentLastModified(String type,String id);
}
