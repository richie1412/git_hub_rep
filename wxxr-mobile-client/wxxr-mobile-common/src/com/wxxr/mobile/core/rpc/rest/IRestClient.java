/**
 * 
 */
package com.wxxr.mobile.core.rpc.rest;

import java.util.concurrent.ExecutorService;

import javax.ws.rs.client.Client;

/**
 * @author neillin
 *
 */
public interface IRestClient extends Client {
	ExecutorService asyncInvocationExecutor();
	ClientResponse invoke(ClientInvocation invocation);
}
