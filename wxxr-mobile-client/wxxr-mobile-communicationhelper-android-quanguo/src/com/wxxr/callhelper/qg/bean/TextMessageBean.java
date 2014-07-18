/**
 * 
 */
package com.wxxr.callhelper.qg.bean;

import java.io.Serializable;

/**
 * @author neillin
 * 
 * mo, 收到的短信，号码在这里
 * mt，发送的短信，号码在这里
 *
 */
public class TextMessageBean implements Serializable{
	private static final long serialVersionUID = -3115051882110231724L;
	private String number,mo,mt,content;
	private Integer id;
	private long timestamp;
	private String readtext="yes";//导入历史，从手机发出，全是yes ，只有从收件箱拦截的是no
	
	
	public String getReadtext() {
		return readtext;
	}
	public void setReadtext(String readtext) {
		this.readtext = readtext;
	}
	/**
	 * @return the mo
	 */
	public String getMo() {
		return mo;
	}
	/**
	 * @return the mt
	 */
	public String getMt() {
		return mt;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}
	/**
	 * @param mo the mo to set
	 */
	public void setMo(String mo) {
		this.mo = mo;
	}
	/**
	 * @param mt the mt to set
	 */
	public void setMt(String mt) {
		this.mt = mt;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TextMessageBean other = (TextMessageBean) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TextMessageBean [number=" + number + ", mo=" + mo + ", mt="
				+ mt + ", content=" + content + ", id=" + id + ", timestamp="
				+ timestamp + "]";
	}
	
	
}
