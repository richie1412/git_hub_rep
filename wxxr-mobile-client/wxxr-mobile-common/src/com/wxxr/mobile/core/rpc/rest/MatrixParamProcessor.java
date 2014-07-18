package com.wxxr.mobile.core.rpc.rest;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public class MatrixParamProcessor extends AbstractWebTargetCollectionProcessor
{

   public MatrixParamProcessor(String paramName)
   {
      super(paramName);
   }

   @Override
   protected WebTarget apply(WebTarget target, Object object)
   {
      return target.matrixParam(paramName, object);
   }

}