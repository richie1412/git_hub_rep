/**
 * 
 */
package com.wxxr.callhelper.qg.exception;

/**
 * @author neillin
 *
 */
public class NetworkNotAvailableException extends AppException {

	public NetworkNotAvailableException() {
		super();
	}

	public NetworkNotAvailableException(String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NetworkNotAvailableException(String detailMessage) {
		super(detailMessage);
	}

	public NetworkNotAvailableException(Throwable throwable) {
		super(throwable);
	}

}
