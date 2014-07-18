/**
 * 
 */
package com.wxxr.callhelper.qg;

/**
 * @author neillin
 *
 */
public class PiggybackPayload {
	private int payloadId;
	private byte[] data;
	/**
	 * @return the payloadId
	 */
	public int getPayloadId() {
		return payloadId;
	}
	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}
	/**
	 * @param payloadId the payloadId to set
	 */
	public void setPayloadId(int payloadId) {
		this.payloadId = payloadId;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	
	
}
