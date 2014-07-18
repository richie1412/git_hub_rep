/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;



/**
 * @author wangyan
 */
@XmlRootElement(name = "UserParamVO")
public class UserParamVO implements Serializable {


	/**�ǳ�*/
	private String nickName;

	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	
}
