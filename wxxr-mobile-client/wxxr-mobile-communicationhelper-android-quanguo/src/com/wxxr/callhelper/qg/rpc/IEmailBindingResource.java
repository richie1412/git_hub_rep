package com.wxxr.callhelper.qg.rpc;

import com.wxxr.common.sync.vo.ResultBaseVO;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

@Path("/bindingemail")
public interface IEmailBindingResource {

	@POST
	@Path("/add")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResultBaseVO addEmailBinding(@QueryParam("deviceId") String deviceId,
			@QueryParam("email") String email) throws Exception; // add email

	@POST
	@Path("/update")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResultBaseVO updateEmailBinding(@QueryParam("deviceId") String deviceId,	@QueryParam("email") String email); // update email

	@GET
	@Path("/find")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResultBaseVO findEmail(@QueryParam("deviceId") String deviceId); // 取email

	@GET
	@Path("/active")
	@Produces({ MediaType.APPLICATION_JSON })
	public ResultBaseVO activeEmail(@QueryParam("deviceId") String deviceId,
			@QueryParam("email") String email); // 激活email

}
