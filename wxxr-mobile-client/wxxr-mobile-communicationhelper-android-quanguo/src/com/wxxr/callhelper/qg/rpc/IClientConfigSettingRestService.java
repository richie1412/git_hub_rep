/*
 * Copyright 2010-2013 WXXR Network Technology
 * Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;

/**
 * @class desc IClientConfigSettingRestService.
 * @author wangxuyang
 * @version $Revision: 1.1 $
 * @created time 2013-9-10 下午6:19:26
 */
@Path("/urlocator")
public interface IClientConfigSettingRestService {
	@GET
	@Path("/serviceUrl")
	@Produces({ "application/json" })
	@Consumes({ "application/json" })
	public UrlLocator getServiceUrl(@QueryParam("id") String userId, @QueryParam("provinceCode") String provinceCode, @QueryParam("test") boolean forTest);
}
