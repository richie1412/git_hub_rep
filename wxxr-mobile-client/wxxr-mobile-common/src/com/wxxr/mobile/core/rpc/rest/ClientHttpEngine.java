package com.wxxr.mobile.core.rpc.rest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public interface ClientHttpEngine
{
   ClientResponse invoke(ClientInvocation request);
   void close();

}
