/*
 * @(#)PageBean.java 2013-6-3
 *
 * Copyright 2013-2014 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.wxxr.javax.xml.bind.annotation.XmlElement;
import com.wxxr.javax.xml.bind.annotation.XmlRootElement;



/**
 * 功能描述  分页信息
 * @author maruili
 * @createtime 2013-6-3 下午3:02:15
 */
@XmlRootElement(name = "ChannelMsgPageVo")
public class ChannelMsgPageVo implements Serializable{
	private static final long serialVersionUID = 1L;
	private int totalRecord;//总记录数
	private int curpage;//当前页
	private int pagenum = 10;//每页记录数
	private int firstRecord;//每页的首条记录
	
	private int totalPage;//总共多少页
	private int hasNextPage;//是否有下一页
	@XStreamImplicit(itemFieldName="msgs")
	private ChannelMsg[] msgs;//当前页的记录数
	public ChannelMsg[] getMsgs() {
		return msgs;
	}
	public void setMsgs(ChannelMsg[] msgs) {
		this.msgs = msgs;
	}
	private int hasPrePage;//是否有上一页
	public int getHasPrePage() {
		return hasPrePage;
	}
	public ChannelMsgPageVo() {
//		super();
//		if(pagenum!=0){
//			this.pagenum = pagenum;
//		}
//		this.setTotalPage(totalPage);
	   
		
	}
	
	public int getFirstRecord() {
		return firstRecord;
	}
	public void setFirstRecord(int firstRecord) {
		this.firstRecord = firstRecord;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		
		this.totalRecord = totalRecord;
	}
	
	public int getCurpage() {
		
			
		return curpage;
	}
	public void setCurpage(int curpage) {
	
		
		this.curpage = curpage;
	
	}
	
	public int getPagenum() {
		return pagenum;
	}
	public void setPagenum(int pagenum) {
		
		this.pagenum = pagenum;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public int getHasNextPage() {
		return hasNextPage;
	}
	
	
	
	public void setHasNextPage(int hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
	public void setHasPrePage(int hasPrePage) {
		this.hasPrePage = hasPrePage;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChannelMsgPageVo:")
				.append("totalRecord=").append(this.getTotalRecord())
				.append(", totalPage=").append(this.getTotalPage())
				.append(", hasNextPage =").append(this.getHasNextPage())
				.append(", hasPrePage =").append(this.getHasPrePage())
				.append(", pagenum =").append(this.getPagenum())
				.append(", curpage =").append(this.getCurpage())
				.append("[list=");
//				for(String o:getList()){
//					builder.append(o);
//				}
				
				builder.append("]");
		return builder.toString();
	}
}
