package com.wxxr.callhelper.qg.rpc;

import com.wxxr.common.sync.vo.ResultBaseVO;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

@Path("/usercheckcode")
public interface IUserCheckCodeResource {

	 @POST
	 @Path("/generate")
	 @Produces({MediaType.APPLICATION_JSON})
	 public ResultBaseVO generateCheckCode(@QueryParam("deviceId") String deviceId, @QueryParam("email") String email) throws Exception;//生成校验码

	 @POST
	 @Path("/verify")
	 @Produces({MediaType.APPLICATION_JSON})
	 public ResultBaseVO verifyCheckCode(@QueryParam("deviceId") String deviceId, @QueryParam("email") String email, @QueryParam("checkcode") String checkcode); // 验证校验码
	

}
