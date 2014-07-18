/**
 * 
 */
package com.wxxr.callhelper.qg.bean;

import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * @author wangyan
 *
 */
public class ExSSHXMsg {
	private SSHXMessageBO sshxMsg;
	private SSHXMsgLifeStatus lifeStatus;
	private Long htmlMsgId;
	public SSHXMessageBO getSshxMsg() {
		return sshxMsg;
	}
	public void setSshxMsg(SSHXMessageBO sshxMsg) {
		this.sshxMsg = sshxMsg;
	}
	public SSHXMsgLifeStatus getLifeStatus() {
		return lifeStatus;
	}
	public void setLifeStatus(SSHXMsgLifeStatus lifeStatus) {
		this.lifeStatus = lifeStatus;
	}
	/**
	 * @return the htmlMsgId
	 */
	public Long getHtmlMsgId() {
		return htmlMsgId;
	}
	/**
	 * @param htmlMsgId the htmlMsgId to set
	 */
	public void setHtmlMsgId(Long htmlMsgId) {
		this.htmlMsgId = htmlMsgId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExSSHXMsg [sshxMsg=" + sshxMsg + ", lifeStatus=" + lifeStatus
				+ ", htmlMsgId=" + htmlMsgId + "]";
	}
	
	
	
}
