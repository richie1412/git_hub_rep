package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;

@Path("/rest/client")
public interface IClientUpdateRestService {
	@GET
	@Produces({ "application/json" })
    @Path("/getClientInfo")
    public ClientInfo getClientInfo();
}

