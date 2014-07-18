package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

@Path("/msgService")
public interface IFetchURL {
	@GET
	@Path("/getMsg")
	@Consumes(MediaType.APPLICATION_XML+";charset=utf-8")
	@Produces(MediaType.APPLICATION_XML+";charset=utf-8")
	public String getMsg(
		@QueryParam("version") 	String version,
		@QueryParam("msgType") 	String msgType,
		@QueryParam("province")   String province,
		@QueryParam("parm1") 	String parm1,
		@QueryParam("parm2") 	String parm2
	);

}





