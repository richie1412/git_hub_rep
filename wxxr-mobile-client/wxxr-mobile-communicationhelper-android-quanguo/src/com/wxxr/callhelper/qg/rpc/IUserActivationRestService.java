/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

/**
 * @author neillin
 * 
 */
@Path("/rest/user")
public interface IUserActivationRestService {

	@GET
	@Path("/regUser")
	@Produces({"application/json"})
	SimpleResultVo requireActivationPassword(@QueryParam("phone") String username);
	
	
	@GET
	@Path("/register")
	@Produces({"application/json"})
	SimpleResultVo registerPassword(@QueryParam("phone") String username);
	
	

	@GET
	@Path("/resetPasswd")
	@Produces({"application/json"})
	SimpleResultVo activateUser(@QueryParam("phone") String username,
			@QueryParam("oldpasswd") String oldpassword,
			@QueryParam("passwd") String newpassword);

	// 新增加的注册方法
	/**
	 * 1，注册成功 但不是移动用户 2，注册成功，是移动用户并且属于两千万号码当中的一个 3，注册成功，是移动用户但是不属于两千万号码 -1,
	 * 如果用户手机号已经注册过了 其余的请捕获异常，并输出异常信息
	 * 
	 * @param username
	 */
	@GET
	@Path("/activeReg")
	@Produces({"application/json"})
	SimpleResultVo activateUserNew(@QueryParam("phone") String username,
			@QueryParam("smscode") String smscode,
			@QueryParam("passwd") String passwd);

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	@GET
	@Path("/getUser")
	@Produces({"application/json"})
	UserVO getUser();

	/**
	 * 重置密码
	 * 
	 * @param username
	 */
	@GET
	@Path("/resetPass")
	@Produces({"application/json"})
	SimpleResultVo resetPass(@QueryParam("phone") String username);

	/**
	 * 修改密码
	 * 
	 * @param username
	 */
	@POST
	@Path("/updatePwd")
	@Produces({"application/json"})
	@Consumes({"application/json"})
	SimpleResultVo updatePwd(UpdatePwdVO vo);

	/**
	 * 修改昵称
	 */

	@POST
	@Path("/updateNickName")
	@Produces({"application/json"})
	@Consumes({"application/json"})
	SimpleResultVo updateNickName(UserParamVO vo);

	/**
	 * 已订购 1 /未订购 0
	 */
	@GET
	@Path("/addOrderStatus")
	@Produces({"application/json"})
	SimpleResultVo addOrderStatus(@QueryParam("phone") String username,
			@QueryParam("status") String status);

	@POST
	@Path("/UserDetail")
	@Produces({"application/json"})
	@Consumes({"application/json"})
	SimpleResultVo syncUser(UserDetailVO user);

	/**
	 * 获取用户详细信息
	 * 
	 * @return
	 */
	@GET
	@Path("/UserDetail")
	@Produces({"application/json"})
	UserDetailVO getUserDetail();

	/**
	 * 上传图片
	 * 
	 * @return
	 */
	@POST
	@Path("/uploadIcon")
	@Consumes({MediaType.WILDCARD})
	void uploadIcon(@QueryParam("deviceId") String deviceId,
			byte[] icon);
	
	
	/**
	 * 下载图片
	 * 
	 * @return
	 */
	@GET
    @Path("/findIcon")
	@Produces(MediaType.WILDCARD)
    @Consumes(MediaType.WILDCARD)
    public byte[] findIcon(@QueryParam("deviceId") String deviceId);

	
	

}