package com.wxxr.mobile.core.rpc.rest;
import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public abstract class AbstractWebTargetCollectionProcessor extends AbstractCollectionProcessor<WebTarget> implements WebTargetProcessor
{
   public AbstractWebTargetCollectionProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      return buildIt(target, param);
   }
}
