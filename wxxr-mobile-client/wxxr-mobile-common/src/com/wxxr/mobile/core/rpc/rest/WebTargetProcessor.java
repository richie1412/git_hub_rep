package com.wxxr.mobile.core.rpc.rest;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1.1 $
 */
public interface WebTargetProcessor
{
   WebTarget build(WebTarget target, Object param);
}
