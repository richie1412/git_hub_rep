/**
 * 
 */
package com.wxxr.mobile.core.rpc.api;


/**
 * @author neillin
 *
 */
public interface Response {
	
	/**
	 * returns status of this response
	 * @return
	 */
	Status getStatus();
		
    /**
     * Obtains the message entity of this response, if any.
     *
     * @return  the response entity, or
     *          <code>null</code> if there is none
     */
    DataEntity getResponseEntity();

}
