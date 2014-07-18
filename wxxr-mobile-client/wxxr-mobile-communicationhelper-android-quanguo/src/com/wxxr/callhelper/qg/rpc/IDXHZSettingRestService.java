/**
 * 
 */
package com.wxxr.callhelper.qg.rpc;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.core.Response;

/**
 * @author neillin
 *
 */
@Path("/customer")
public interface IDXHZSettingRestService {
//	升级 了一个字段，这个不用了
//	@GET
//	@Produces({ "application/json" })
//	@Path("/info")
//	public DXHZSetting getMySetting();
//	
//	@POST
//	@Consumes({ "application/json" })
//	@Path("/sync")
//	void updateMySetting(DXHZSetting setting);
	
	
	
	@GET
	@Produces({ "application/json" })
	@Path("/sxinfo")
	public DXHZSetting getCustomer() throws Exception;
	
	@POST
	@Consumes({ "application/json" })
	@Path("/sxsync")
	public Response sync(DXHZSetting vo) throws Exception;

}
