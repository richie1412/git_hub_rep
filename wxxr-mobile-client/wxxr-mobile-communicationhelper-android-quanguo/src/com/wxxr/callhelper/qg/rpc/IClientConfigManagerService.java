package com.wxxr.callhelper.qg.rpc;

public interface IClientConfigManagerService {
	String getURL(String name);	
	//首页微资讯地址
    public String getHomeInfoUrl();
    //微资讯每日悦读地址
    public String getEveryDayInfoUrl();
    //获取rest访问地址
    public String getRestServiceUrl();
    //获取猜你喜欢地址
    public  String getGuessLikeInfoUrl();
    public String getSSHXSyncRestServiceUrl();
//    
    public String getCityCheapHomeUrl();
    
    public String getSquarelUrl();
    public String getApp_RecommendUrl();
    
    public String getUrlOfGetUrls();
    //修改 地区之后，需要立刻获取一次
    public void forceLoadURL();
}
