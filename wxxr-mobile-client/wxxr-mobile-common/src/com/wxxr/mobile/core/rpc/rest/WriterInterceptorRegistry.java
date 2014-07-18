package com.wxxr.mobile.core.rpc.rest;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class WriterInterceptorRegistry extends JaxrsInterceptorRegistry<WriterInterceptor>
{
   protected LegacyPrecedence precedence;

   public WriterInterceptorRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, WriterInterceptor.class);
      this.precedence = precedence;
   }

   public WriterInterceptorRegistry clone(ResteasyProviderFactory factory)
   {
      WriterInterceptorRegistry clone = new WriterInterceptorRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
