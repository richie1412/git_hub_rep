package com.wxxr.mobile.core.rpc.rest;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public interface InvocationProcessor
{
   void process(ClientInvocationBuilder invocation, Object param);
}
