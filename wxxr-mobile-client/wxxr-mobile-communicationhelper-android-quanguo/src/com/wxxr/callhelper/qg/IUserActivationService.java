/**
 * 
 */
package com.wxxr.callhelper.qg;

import com.wxxr.callhelper.qg.rpc.UserDetailVO;
import com.wxxr.javax.ws.rs.Consumes;
import com.wxxr.javax.ws.rs.GET;
import com.wxxr.javax.ws.rs.POST;
import com.wxxr.javax.ws.rs.Path;
import com.wxxr.javax.ws.rs.Produces;
import com.wxxr.javax.ws.rs.QueryParam;
import com.wxxr.javax.ws.rs.core.MediaType;
import com.wxxr.mobile.core.api.IProgressMonitor;

/**
 * @author neillin
 *
 */
public interface IUserActivationService {
	
	/**
	 * 向服务器 申请新密码
	 * @param phoneNumber
	 * @param monitor
	 */
	
	void registerPassword(String phoneNumber, IProgressMonitor monitor);
	
	
	
	
	/**
	 * 向服务器端请求用户激活密码，密码将以短信方式发到用户手机上
	 * @param phoneNumber
	 * @param monitor
	 */
	void requireActivationPassword(long phoneNumber, IProgressMonitor monitor);
	
	/**
	 *  激活或更新用户登入密码
	 * @param phoneNUmber
	 * @param activationPassword
	 * @param newPassword
	 * @param monitor
	 */
	void activateUser(long phoneNUmber, String activationPassword, IProgressMonitor monitor);
	
	/**
	 * 如果该客户端已经激活，返回true
	 * @return
	 */
	boolean isUserActivated();
	
	/**
	 * 返回当前已绑定的用户id，如果还没有绑定，返回NULL
	 * @return
	 */
	String getCurrentUserId();
	/**
	 * 退出激活
	 * @param monitor
	 */
	public void deActivateUser(IProgressMonitor monitor);
	
	
	public void logout( IProgressMonitor monitor);
	
	public void login( String phoneNumber, String newPasswd,  IProgressMonitor monitor) ;
	
	public void updatePwd( String newPasswd,  IProgressMonitor monitor) ;
	//更改昵称
	public void updateNickName( String newPasswd,  IProgressMonitor monitor) ;
	//更新已订购 未订购
	public void addOrderStatus( String status,  IProgressMonitor monitor) ;
	//重置密码
	public void  resetPass( String phoneNumber,  IProgressMonitor monitor) ;

	//用getUserDetail
    //	public void getCurrentUser(IProgressMonitor monitor);
	/**
	 * 同步用户详细信息
	 * @param user
	 * @param monitor
	 */
	public void syncUserDetail(UserDetailVO user,IProgressMonitor monitor);
	
	/**
	 * 获取用户详细信息
	 * @param monitor
	 */
	public void getUserDetail(IProgressMonitor monitor);
	
	
	


}
