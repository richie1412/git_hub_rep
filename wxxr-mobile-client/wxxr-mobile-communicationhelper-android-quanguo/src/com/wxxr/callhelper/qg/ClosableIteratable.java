/**
 * 
 */
package com.wxxr.callhelper.qg;

/**
 * @author neillin
 *
 */
public interface ClosableIteratable<T> extends Iterable<T> {
	void close();
}
