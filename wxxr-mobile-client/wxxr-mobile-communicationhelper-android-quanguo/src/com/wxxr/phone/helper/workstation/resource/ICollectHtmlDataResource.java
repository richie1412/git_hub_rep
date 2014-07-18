package com.wxxr.phone.helper.workstation.resource;

import com.wxxr.common.sync.vo.ResultBaseVO;
import com.wxxr.common.sync.vo.UNodeDescriptorVO;
import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.HeaderParam;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

/**
 * 
 * @class desc A ICollectHtmlDataResource
 *
 * @author zhangjq
 * @version v1.0
 * @created 2013-8-19 下午04:11:54
 */
@Path("/phoneclient")
public interface ICollectHtmlDataResource {
   
   @POST
   @Path("/isDataChanged")
   @Consumes(MediaType.WILDCARD)
   public UNodeDescriptorVO isDataChanged(@QueryParam("key") String key , @QueryParam("nodePath") String nodePath, @QueryParam("digest") String digest,  @HeaderParam("ppid") int piggyPayloadId,byte[] payload) throws Exception;//2
   
   @POST
   @Path("/getNodeData")
   @Consumes(MediaType.APPLICATION_JSON)
   public byte[] getNodeData(@QueryParam("key") String key , @QueryParam("nodePath") String nodePath) throws Exception;
   
   @GET
   @Path("/getNodeDescriptor")
   @Consumes( {MediaType.APPLICATION_JSON})
   public UNodeDescriptorVO getNodeDescriptor(@QueryParam("key") String key , @QueryParam("nodePath") String nodePath) throws Exception;
   
   @GET
   @Path("/getDataDigest")
   @Consumes( {MediaType.APPLICATION_JSON})
   public ResultBaseVO getDataDigest(@QueryParam("key") String key , @QueryParam("nodePath") String nodePath) throws Exception;

}

