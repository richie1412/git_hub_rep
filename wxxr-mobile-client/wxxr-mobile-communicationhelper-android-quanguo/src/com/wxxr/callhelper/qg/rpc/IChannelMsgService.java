package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;

@Path("/channelMsgService")
public interface IChannelMsgService {
	/**
	    * 
	     * 功能描述：获取某一页的频道资讯信息（包含历史和正在播发）
	     * 
	    *@param region 地区
	    *@param telecom 运营商
	    *@param channelCd 频道
	    *@param pageSum 每页条数
	    *@param page 某页
	    *@return
	    */
	@GET
@Path("/getChannelMsg")
	    @Produces({MediaType.APPLICATION_JSON})
public ChannelMsgPageVo getChannelMsg(@QueryParam("region") String region,@QueryParam("telecom") String telecom,@QueryParam("channelCd") String channelCd,@QueryParam("pageNum") String pageNum,@QueryParam("page") String page);

}
