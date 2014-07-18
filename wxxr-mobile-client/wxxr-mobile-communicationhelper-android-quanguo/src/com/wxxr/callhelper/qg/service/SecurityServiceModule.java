/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.security.auth.login.LoginException;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.event.UserBoundEvent;
import com.wxxr.callhelper.qg.exception.NetworkNotAvailableException;
import com.wxxr.callhelper.qg.exception.UserNotBoundException;
import com.wxxr.callhelper.qg.rpc.AbstractMonitorRunnable;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IUserActivationRestService;
import com.wxxr.callhelper.qg.rpc.SimpleResultVo;
import com.wxxr.callhelper.qg.rpc.UpdatePwdVO;
import com.wxxr.callhelper.qg.rpc.UserDetailVO;
import com.wxxr.callhelper.qg.rpc.UserVO;
import com.wxxr.javax.ws.rs.NotAuthorizedException;
import com.wxxr.mobile.android.preference.DictionaryUtils;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.api.IExchangeHandler;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.api.IUserAuthCredential;
import com.wxxr.mobile.core.api.IUserAuthManager;
import com.wxxr.mobile.core.api.UsernamePasswordCredential;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.HttpRpcService;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
import com.wxxr.mobile.preference.api.IPreferenceManager;

/**
 * @author neillin
 *
 */
public class SecurityServiceModule extends AbstractModule<ComHelperAppContext> implements
		IUserActivationService,IUserAuthManager {
	private static final Trace log = Trace.register(SecurityServiceModule.class);
	
	private static final String KEY_USERNAME = "U";
	private static final String KEY_PASSWORD = "P";
	private static final String KEY_UPDATE_DATE = "UD";
	private IPreferenceManager prefManager;
	private RandomString randomPasswd = new RandomString(8);
    private UsernamePasswordCredential UsernamePasswordCredential4Login;
    private UserDetailVO currentUserDetail;
    private IEventListener listener = new IEventListener() {
        
        @Override
        public void onEvent(IBroadcastEvent event) {
            if(event instanceof UserBoundEvent){
                currentUserDetail = null;
            }
            
        }
    };
	private IPreferenceManager getPrefManager() {
		if(this.prefManager == null){
			this.prefManager = context.getService(IPreferenceManager.class);
		}
		return this.prefManager;
	}
	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IUserActivationService#requireActivationPassword(long, com.wxxr.mobile.core.api.IProgressMonitor)
	 */
	@Override
	public void requireActivationPassword(final long phoneNumber,final IProgressMonitor monitor) {
		context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
			
			@Override
			protected Object executeTask() throws Throwable {
				IUserActivationRestService service = context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class);
				SimpleResultVo  vv=	service.requireActivationPassword(String.valueOf(phoneNumber));
			if(vv==null||vv.getResult()==-1){
				throw new Exception("获取激活码失败");
			}
				return null;
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IUserActivationService#activateUser(long, java.lang.String, java.lang.String, com.wxxr.mobile.core.api.IProgressMonitor)
	 */
	@Override
	public void activateUser(final long phoneNumber, final String activationPassword, final IProgressMonitor monitor) {
		context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
			
			@Override
			protected Object executeTask() throws Throwable {
//				String newPasswd = generateRandomPassword(); //不用随机生成的，直接用下发的短信密码作为登录密码
				String newPasswd = activationPassword;
				SimpleResultVo vo=	context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).activateUser(String.valueOf(phoneNumber),activationPassword,newPasswd);
				if (vo==null || vo.getResult()==-1){				
				   throw new Exception("修改密码失败");
		        }
				Dictionary<String, String> pref = getPrefManager().getPreference(getModuleName());
				if(pref == null){
					pref= new Hashtable<String, String>();
					getPrefManager().newPreference(getModuleName(), pref);
				}else{
					pref = DictionaryUtils.clone(pref);
				}
				pref.put(KEY_USERNAME, String.valueOf(phoneNumber));
				pref.put(KEY_PASSWORD, newPasswd);
				pref.put(KEY_UPDATE_DATE, String.valueOf(System.currentTimeMillis()));
				getPrefManager().putPreference(getModuleName(), pref);
				UserBoundEvent evt = new UserBoundEvent();
				evt.setUserId( String.valueOf(phoneNumber));
				context.getService(IEventRouter.class).routeEvent(evt);
				return null;
			}
		});
	}
	public void deActivateUser(final IProgressMonitor monitor) {
		context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
			
			@Override
			protected Object executeTask() throws Throwable {
				getPrefManager().putPreference(getModuleName(), new Hashtable<String, String>());
				return null;
			}
		});
	}
	private String generateRandomPassword() {
		return randomPasswd.nextString();
	}

	
	@Override
	protected void initServiceDependency() {
		addRequiredService(IPreferenceManager.class);
		addRequiredService(IRestProxyService.class);
	    addRequiredService(IDataExchangeCoordinator.class);
	//    addRequiredService(IClientConfigManagerService.class);

	}

	@Override
	protected void startService() {
		context.registerService(IUserAuthManager.class, this);
		context.registerService(IUserActivationService.class, this);
	    context.getService(IDataExchangeCoordinator.class).registerHandler(exchangeHandler);
        context.getService(IEventRouter.class).registerEventListener(UserBoundEvent.class, listener);

	}

	@Override
	protected void stopService() {
		context.unregisterService(IUserAuthManager.class, this);
		context.unregisterService(IUserActivationService.class, this);
		if(context.getService(IDataExchangeCoordinator.class) != null){
            context.getService(IDataExchangeCoordinator.class).unregisterHandler(exchangeHandler);
        }
		if(context.getService(IEventRouter.class) != null){
            context.getService(IEventRouter.class).unregisterEventListener(UserBoundEvent.class, listener);
        }
	}

	@Override
	public IUserAuthCredential getAuthCredential(String host, String realm) {
		IPreferenceManager mgr = getPrefManager();
		if(!mgr.hasPreference(getModuleName()) || mgr.getPreference(getModuleName()).get(KEY_USERNAME)==null ){
		    if (UsernamePasswordCredential4Login!=null){
		        return UsernamePasswordCredential4Login;
		    }
			return null;
		}
		Dictionary<String, String> d = mgr.getPreference(getModuleName());
		String userName = d.get(KEY_USERNAME);
		String passwd = d.get(KEY_PASSWORD);
		return new UsernamePasswordCredential(userName,passwd);
	}
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "SecurityServiceModule";
	}
	
	public void login(final String phoneNumber,final String newPasswd, final IProgressMonitor monitor) {
	    context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
            @Override
            protected Object executeTask() throws Throwable {
                //如果可以根据用户输入的用户名 密码查询到用户信息 就可以认为登录成功
                UserVO vo=null;
                UsernamePasswordCredential4Login=new UsernamePasswordCredential(phoneNumber,newPasswd);
                try {
                     vo=context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).getUser() ;
                }catch ( NotAuthorizedException e){
                    throw new LoginException("用户名或密码错误");
                }finally{
                    UsernamePasswordCredential4Login=null;
                }
                if (vo==null){
                    throw new LoginException("登录异常");
                }
                //根据用户密码登录成功
                Dictionary<String, String> pref = getPrefManager().getPreference(getModuleName());
                if(pref == null){
                    pref= new Hashtable<String, String>();
                    getPrefManager().newPreference(getModuleName(), pref);
                }else{
                    pref = DictionaryUtils.clone(pref);
                }
                pref.put(KEY_USERNAME, phoneNumber);
                pref.put(KEY_PASSWORD, newPasswd);
                pref.put(KEY_UPDATE_DATE, String.valueOf(System.currentTimeMillis()));
                getPrefManager().putPreference(getModuleName(), pref);
                UserBoundEvent evt = new UserBoundEvent();
                evt.setUserId( String.valueOf(phoneNumber));
                context.getService(IEventRouter.class).routeEvent(evt);
                return true;
            }
        });
        
	}
	public void logout(final IProgressMonitor monitor) {
	    deActivateUser(monitor);
	    HttpRpcService httpService = context.getService(HttpRpcService.class);
	    if(httpService != null){
	    	httpService.resetHttpClientContext();
	    }
    }
	
	@Override
	public boolean isUserActivated() {
		return getCurrentUserId() != null;
	}
	@Override
	public String getCurrentUserId() {
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getModuleName());
		String userName = d != null ? d.get(KEY_USERNAME) : null;
		String passwd = d != null ? d.get(KEY_PASSWORD) : null;
		if((userName != null)&&(passwd != null)){
			return userName;
		}else{
			return null;
		}
	}
    @Override
    public void updatePwd(final String newPasswd, IProgressMonitor monitor) {
        context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
            @Override
            protected Object executeTask() throws Throwable {
                UpdatePwdVO para=new UpdatePwdVO();
                para.setPassword(newPasswd);
                SimpleResultVo vo=context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).updatePwd(para);
                if (vo!=null && vo.getResult()==1){
                    Hashtable pref= new Hashtable<String, String>();
                    pref.put(KEY_USERNAME, getCurrentUserId());
                    pref.put(KEY_PASSWORD, newPasswd);
                    pref.put(KEY_UPDATE_DATE, String.valueOf(System.currentTimeMillis()));
                    getPrefManager().putPreference(getModuleName(), pref);
                }else{
                    throw new Exception("更新密码失败");
                }
                return vo;
            }
    });
    }
    @Override
    public void updateNickName(final String name, IProgressMonitor monitor) {
           context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log)  {            
            @Override
            protected Object executeTask() throws Throwable {
                if (name==null){
                    return null;
                }
                if(currentUserDetail == null){
                    doInit();
                }
                boolean flag = false;
               
                //昵称
                String nickName=currentUserDetail.getNickName();
                if(currentUserDetail!=null && !name.equals(nickName) ){
                    currentUserDetail.setNickName(name);
                    flag=true;
                }
                if(flag){
                    currentUserDetail.setUpdated(true);
                    updateLocalPreference(currentUserDetail);
                }
                return null;
            }
        });
    }
    @Override
    public void addOrderStatus(final String status, IProgressMonitor monitor) {
        context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
            @Override
            protected Object executeTask() throws Throwable {
                SimpleResultVo vo= context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).addOrderStatus(getCurrentUserId(), status);
                
                if (vo!=null && vo.getResult()==0){
                    return vo;
                }else if  (vo!=null){
                    throw new Exception(vo.getMessage());
                }else{
                    throw new Exception("更新订购状态失败");
                }
            }
    });
    }
    @Override
    public void resetPass(final String phoneNumber, IProgressMonitor monitor) {
        context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
            @Override
            protected Object executeTask() throws Throwable {
                SimpleResultVo vo= context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).resetPass(phoneNumber);
                if (vo!=null && vo.getResult()==0){
                    return vo;
                }else{
                    throw new Exception("重置密码失败");
                }
                
            }
    });
    }
    

    @Override
    public void syncUserDetail(final UserDetailVO user, IProgressMonitor monitor) {
        //写到本地
        context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log)  {
            
            @Override
            protected Object executeTask() throws Throwable {
                if (user==null){
                    return null;
                }
                if(currentUserDetail == null){
                    doInit();
                }
                boolean flag = false;
                //生日
                int old = currentUserDetail.getBirthday();             
                if(old!= user.getBirthday() ){
                    currentUserDetail.setBirthday(user.getBirthday());
                    flag=true;
                }
                //昵称
                String nickName=currentUserDetail.getNickName();
                if(user.getNickName()!=null && !user.getNickName().equals(nickName) ){
                    currentUserDetail.setNickName(user.getNickName());
                    flag=true;
                }
                //性别
                boolean sex=currentUserDetail.isMale();
                if(sex!= user.isMale()){
                    currentUserDetail.setMale(user.isMale());
                    flag=true;
                }
               //性别
                String proviceCode=currentUserDetail.getProvinceCode();
                if(user.getProvinceCode()!=null && !user.getProvinceCode().equals(proviceCode)){
                    currentUserDetail.setProvinceCode(user.getProvinceCode());
                    flag=true;
                }
                if(flag){
                    currentUserDetail.setUpdated(true);
                    updateLocalPreference(currentUserDetail);
                }
                return null;
            }
        });
    }
    @Override
    public void getUserDetail(IProgressMonitor monitor) {
        context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
            @Override
            protected Object executeTask() throws Throwable {
                if (currentUserDetail==null){
                    doInit();
                }
                return UserDetailVO.clone(currentUserDetail);
            }
    });
    }
    
    protected void clearUpdateFlag() throws UserNotBoundException{
        if(currentUserDetail != null){
            currentUserDetail.setUpdated(false);
            updateLocalPreference(currentUserDetail);
        }
    }
    protected void updateLocalPreference(UserDetailVO setting) throws UserNotBoundException{
        if(setting == null){
            return;
        }
        IPreferenceManager mgr = getPrefManager();
        Dictionary<String, String> d = mgr.getPreference(getPrefrencesName());
        if(d == null){
            d = new Hashtable<String, String>();
            mgr.newPreference(getPrefrencesName(), d);
        }else{
            d = DictionaryUtils.clone(d);
        }
        setting.updateTo(d);
        mgr.putPreference(getPrefrencesName(), d);
    }
    protected void loadLocalPreference() throws UserNotBoundException {
        IPreferenceManager mgr = getPrefManager();
        Dictionary<String, String> d = mgr.getPreference(getPrefrencesName());
        if(d == null){
            return;
        }
        UserDetailVO urerDetail = new UserDetailVO();
        urerDetail.updateFrom(d);
        currentUserDetail = urerDetail;
    }
    
    /**
     * @return
     * @throws UserNotBoundException 
     */
    protected String getPrefrencesName() throws UserNotBoundException {
        IUserActivationService service = context.getService(IUserActivationService.class);
        String userId = service.getCurrentUserId();
        if(userId == null){
            throw new UserNotBoundException("客户未登录，请登录");
        }
        return getModuleName()+"_"+userId;
    }
    private IExchangeHandler exchangeHandler = new IExchangeHandler() {
        
        @Override
        public void startExchange() {
            if((currentUserDetail != null)&&(currentUserDetail.getUpdated() != null)&&currentUserDetail.getUpdated().booleanValue()){
                context.getExecutor().execute(new Runnable() {
                    
                    @Override
                    public void run() {
                        try {
                            UserDetailVO tmp=currentUserDetail;
                            
                            context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).syncUser(tmp);
                            clearUpdateFlag();
                        }catch(Throwable t){
                            log.warn("Failed to update user setting to server", t);
                        }
                    }
                });
            }
        }
    };
    protected void doInit() throws UserNotBoundException, NetworkNotAvailableException {
        IPreferenceManager prefMgr = getPrefManager();
        if(!prefMgr.hasPreference(getPrefrencesName())){
            IDataExchangeCoordinator s = context.getService(IDataExchangeCoordinator.class);
            if(s.checkAvailableNetwork() < 0){
                throw new NetworkNotAvailableException("没有网络连接，无法从服务器个人信息，请配置网络连接");
            }
            UserDetailVO setting = context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).getUserDetail();
            if(setting != null){
                updateLocalPreference(setting);
                currentUserDetail = setting;
            }else{
                currentUserDetail = new UserDetailVO();
            }
        }else{
            loadLocalPreference();
        }
    }
	@Override
	public void registerPassword(final String phoneNumber, IProgressMonitor monitor) {
		  context.getExecutor().execute(new  AbstractMonitorRunnable(monitor,log) {
	            @Override
	            protected Object executeTask() throws Throwable {
	                SimpleResultVo vo= context.getService(IRestProxyService.class).getRestService(IUserActivationRestService.class).registerPassword(phoneNumber);
	                if (vo!=null && vo.getResult()==0){
	                    return vo;
	                }else{
	                    throw new Exception("重置密码失败");
	                }
	                
	            }
	    });
		
	}
	
}
