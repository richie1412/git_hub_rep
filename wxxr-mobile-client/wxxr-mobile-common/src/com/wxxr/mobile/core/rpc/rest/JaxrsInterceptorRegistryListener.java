package com.wxxr.mobile.core.rpc.rest;
/**
 * Callback interface for when an interceptor registry changes
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public interface JaxrsInterceptorRegistryListener
{
   void registryUpdated(JaxrsInterceptorRegistry registry);

}
