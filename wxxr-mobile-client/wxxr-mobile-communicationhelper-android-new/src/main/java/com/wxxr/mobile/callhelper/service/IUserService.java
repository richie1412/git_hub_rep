/**
 * 
 */
package com.wxxr.mobile.callhelper.service;

import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;
import com.wxxr.mobile.callhelper.app.model.UserDetail;

/**
 * @author neillin
 *
 */
public interface IUserService {
	
	/**
	 * 获取用户详细信息
	 * @param monitor
	 */
	public UserDetail getUserDetail();
	
	
	/**
	 * 获取用户详细信息
	 * @param monitor
	 */
	public boolean updateUserDetail(UserDetail newValue);
	
	/**
	 * 上传图片
	 * 
	 * @return
	 */
	boolean uploadIcon(String deviceId,byte[] icon);
	
	/**
	 * 下载图片
	 * 
	 * @return
	 */
    public byte[] findIcon(String deviceId);
    
    /**
     * 设置昵称
     * @param name
     */
    public void setNickName(String name);
    
    /**
     * 设置昵称
     * @param name
     */
    public void setGender(String gender);
    
    /**
     * 设置年龄
     * @param age
     */
    public void setAge(int age);
    
    /**
     * 设置省份
     * @param province
     */
    public void setProvince(String province);
    

}
