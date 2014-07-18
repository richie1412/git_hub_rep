package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

@Path("/articlePathService")
public interface IArticlePathResource {
    @GET
    @Path("/getAritclePath")
    @Produces( {MediaType.APPLICATION_JSON})
    public ArticlePathCollectionVo getArticlePath(@QueryParam("type") String type);
}
