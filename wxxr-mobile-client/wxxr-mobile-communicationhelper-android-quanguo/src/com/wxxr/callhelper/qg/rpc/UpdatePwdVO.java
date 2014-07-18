package com.wxxr.callhelper.qg.rpc;

import java.io.Serializable;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UpdatePwdVO")
public class UpdatePwdVO implements Serializable{

	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
