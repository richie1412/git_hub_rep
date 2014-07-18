package com.wxxr.mobile.core.rpc.rest;
import javax.ws.rs.ext.ReaderInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class ReaderInterceptorRegistry extends JaxrsInterceptorRegistry<ReaderInterceptor>
{
   protected LegacyPrecedence precedence;

   public ReaderInterceptorRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, ReaderInterceptor.class);
      this.precedence = precedence;
   }

   public ReaderInterceptorRegistry clone(ResteasyProviderFactory factory)
   {
      ReaderInterceptorRegistry clone = new ReaderInterceptorRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }
}
