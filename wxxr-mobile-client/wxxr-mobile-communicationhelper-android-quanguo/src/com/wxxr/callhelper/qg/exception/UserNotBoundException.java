/**
 * 
 */
package com.wxxr.callhelper.qg.exception;

/**
 * @author neillin
 *
 */
public class UserNotBoundException extends AppException {

	/**
	 * 
	 */
	public UserNotBoundException() {
	}

	/**
	 * @param detailMessage
	 */
	public UserNotBoundException(String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public UserNotBoundException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public UserNotBoundException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
