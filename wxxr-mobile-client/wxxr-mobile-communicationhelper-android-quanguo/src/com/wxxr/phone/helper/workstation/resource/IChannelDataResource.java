package com.wxxr.phone.helper.workstation.resource;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.core.MediaType;

/**
 * 
 * 
 * @author liyongsheng
 * 
 */
@Path("/channelService")
public interface IChannelDataResource {

	@GET
	@Path("/getChannels")
	@Consumes({ MediaType.WILDCARD })
	public byte[] getChannels();
}
