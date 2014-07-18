/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.xml.bind.annotation.XmlRootElement;



/**
 * @author wangyan
 *
 */
@XmlRootElement(name = "SimpleResult")
public class SimpleResultVo {

	private int result;
	private String message;
	/**
	 * @return the result
	 */
	public int getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(int result) {
		this.result = result;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SimpleResultVo [result=").append(result)
				.append(", message=").append(message).append("]");
		return builder.toString();
	}
}
