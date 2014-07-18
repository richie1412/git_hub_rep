package com.wxxr.mobile.core.rpc.rest;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class PathParamProcessor implements WebTargetProcessor
{
   private final String paramName;

   public PathParamProcessor(String paramName)
   {
      this.paramName = paramName;
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      return target.resolveTemplate(paramName, param);
   }
}
