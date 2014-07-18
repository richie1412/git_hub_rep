/**
 * 
 */
package com.wxxr.callhelper.qg;

/**
 * @author neillin
 *
 */
/**
 * @author neillin
 *
 */
public enum ActivityID {
	 //PERSONCENTER,//个人中心
    //CALLTIP,//来电提醒
    //DXHUIZHI,//短信回执
    //GUISHUIDI,//归属地
    //SIMISUO,//私密锁
    //RECVOICE,//通话录音
    //微资讯||资讯
         REGISTER,                //注册用户
         SETTING,                  //个人中心
         MISSCALL,                 //来电提醒
         DXHZ,                       //短信回执
         PRIVATESM,              //私密短信
         RECORDER,              //通话录音
         NUMBERHOME,       //号码归属地
         MESSAGE,                 //资讯
         SYSCRASH,                //系统异常退出, 这个是老的，不能移动地方
         LOGIN,//登陆 
         LOGOUT,//退出
         TXZHUSHOU,//通信助手     
         SQUARE,//广场        
         YEWUCHAXUN,//移动业务查询
         CHONGZHI,//充值
         HUIZHIDIALOG,//回执拦截对话框
         CALLDIALOG,//来电提醒拦截对话框
         CHONGZHIDIALOG,//充值拦截对话框
         UPDATEDIALOG,//升级对话框
         UPDATENOW,//升级
         LOOKNOTICE,//查看首页公告
         CLOSENOTICE,//关闭首页公告
         SYSNOTICE,//获取系统通知
         LOOKSYSNOTICE,//查看系统通知
         SUBIDEAR,//意见反馈         
         ENTER_CLIENT,//进入客户端
         EXIT_CLIENT,//退出客户端
         SAHRE_FROM_PERSONCENTER,//从个人中心分享
         SAHRE_FROM_ZSBB,//从助手播报分享     
         SAHRE_FROM_YOUHUI,//从地市优惠分享        
         CLOSE_CHONGZHI,//关闭 充值提醒
         DIANHUA_CHONGZHI,// 充值框    电话充值
         YUE_CHAXUN,//充值框   余额查询
         CLOSE_HUIZHI,//关闭 回执提醒
         HUIZHI_SET,//回执框    设置
         HUIZHI_TEL,//回执框  回电话
         HUIZHI_CONTENT,//回执框  点击了内容
         HUIZHI_PICTRUE,//回执框  图文
         CLOSE_LOUHUA,//退出  漏电提醒
         TEL_SMS,//漏接电话框   发短信
         TEL_TEL,//漏接电话框  打电话
         TEL_CONTENT,//漏接电话框   点击内容
         TEL_PICTURE,//漏接电话框   图文
         SIMISUO_OK,//私密锁，第一次使用邮箱 点击了允许
         USER_SETTING,//用户设置
         USER_SETTING_INTERNAL//用户设置内部按钮
}
