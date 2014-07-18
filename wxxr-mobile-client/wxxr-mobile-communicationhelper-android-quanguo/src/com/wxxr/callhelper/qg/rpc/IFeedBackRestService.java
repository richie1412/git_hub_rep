package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;

@Path("/rest/feedbackMessage")
public interface IFeedBackRestService {
	
    @POST
	@Consumes({ "application/json" })
    @Path("/addFeedBackMessage")
    public void addFeedBackMessage (FeedBackMessage m);
}
