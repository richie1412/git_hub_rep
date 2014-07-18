package com.wxxr.mobile.core.rpc.rest;
import javax.ws.rs.container.ContainerRequestFilter;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class ContainerRequestFilterRegistry extends JaxrsInterceptorRegistry<ContainerRequestFilter>
{
   protected LegacyPrecedence precedence;

   public ContainerRequestFilterRegistry(ResteasyProviderFactory providerFactory, LegacyPrecedence precedence)
   {
      super(providerFactory, ContainerRequestFilter.class);
      this.precedence = precedence;
   }

   public ContainerRequestFilterRegistry clone(ResteasyProviderFactory factory)
   {
      ContainerRequestFilterRegistry clone = new ContainerRequestFilterRegistry(factory, precedence);
      clone.interceptors.addAll(interceptors);
      return clone;
   }

}
