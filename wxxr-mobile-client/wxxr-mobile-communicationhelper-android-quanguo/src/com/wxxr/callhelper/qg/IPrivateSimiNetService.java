package com.wxxr.callhelper.qg;


public interface IPrivateSimiNetService {

	public boolean addEmailBinding(String deviceId,String email);
	public boolean updateEmailBinding(String deviceId,	String email); 
	public String findEmail(String deviceId); // 取email
	
	 public boolean generateCheckCode(String deviceId,String email) throws Exception;//生成校验码
	
	 public boolean verifyCheckCode(String deviceId,String email, String checkcode); // 验证校验码
	
}
