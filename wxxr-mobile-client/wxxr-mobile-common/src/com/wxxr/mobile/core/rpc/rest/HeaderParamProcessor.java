package com.wxxr.mobile.core.rpc.rest;
/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class HeaderParamProcessor extends AbstractInvocationCollectionProcessor
{

   public HeaderParamProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   protected ClientInvocationBuilder apply(ClientInvocationBuilder target, Object object)
   {
      return (ClientInvocationBuilder)target.header(paramName, object);
   }

}