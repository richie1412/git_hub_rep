/**
 * 
 */
package com.wxxr.callhelper.qg.exception;

/**
 * @author neillin
 *
 */
public class AppException extends Exception {

	/**
	 * 
	 */
	public AppException() {
	}

	/**
	 * @param detailMessage
	 */
	public AppException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public AppException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public AppException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
