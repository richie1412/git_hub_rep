/**
 * 
 */
package com.wxxr.callhelper.qg;

import java.io.File;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.webkit.WebView;

import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.service.AdvancedRegionQueryModule;
import com.wxxr.callhelper.qg.service.BDLocationModule;
import com.wxxr.callhelper.qg.service.CallRecorderModule;
import com.wxxr.callhelper.qg.service.ClientConfigManagerService;
import com.wxxr.callhelper.qg.service.ClientCustomModule;
import com.wxxr.callhelper.qg.service.ClientUpdateModule;
import com.wxxr.callhelper.qg.service.ContentCleanModule;
import com.wxxr.callhelper.qg.service.ContentManager;
import com.wxxr.callhelper.qg.service.DXHZSettingModule;
import com.wxxr.callhelper.qg.service.FecthZhuShouBaoboModule;
import com.wxxr.callhelper.qg.service.FeedBackModule;
import com.wxxr.callhelper.qg.service.FetchUrlModul;
import com.wxxr.callhelper.qg.service.GDDownwebServiceModule;
import com.wxxr.callhelper.qg.service.GuessLikeDetailCacheService;
import com.wxxr.callhelper.qg.service.GuiShuDiService;
import com.wxxr.callhelper.qg.service.HtmlMessageManager;
import com.wxxr.callhelper.qg.service.ImportOldDataService;
import com.wxxr.callhelper.qg.service.LouHuaHuizhiModule;
import com.wxxr.callhelper.qg.service.MTreeSyncConnector;
import com.wxxr.callhelper.qg.service.MTreeSyncScoreModule;
import com.wxxr.callhelper.qg.service.MessageProviderModule;
import com.wxxr.callhelper.qg.service.MicoNewsModleService;
import com.wxxr.callhelper.qg.service.MoblieBusinessModule;
import com.wxxr.callhelper.qg.service.NoticeSyncModule;
import com.wxxr.callhelper.qg.service.NotificationService;
import com.wxxr.callhelper.qg.service.NotificationServiceModule;
import com.wxxr.callhelper.qg.service.OfficeLineHtmlProvideService;
import com.wxxr.callhelper.qg.service.PiggybackService;
import com.wxxr.callhelper.qg.service.PrivateSMServiceModule;
import com.wxxr.callhelper.qg.service.PrivateSMServiceNetModule;
import com.wxxr.callhelper.qg.service.RuleEngineService;
import com.wxxr.callhelper.qg.service.SSHXMsgManagementModule;
import com.wxxr.callhelper.qg.service.SecurityServiceModule;
import com.wxxr.callhelper.qg.service.SmsContentParseModule;
import com.wxxr.callhelper.qg.service.UserHeadModule;
import com.wxxr.callhelper.qg.service.UserUsageRecorderModule;
import com.wxxr.callhelper.qg.ui.ApplicationManager;
import com.wxxr.mobile.android.app.AndroidFramework;
import com.wxxr.mobile.android.app.IAndroidFramework;
import com.wxxr.mobile.android.http.HttpRpcServiceModule;
import com.wxxr.mobile.android.network.NetworkManagementModule;
import com.wxxr.mobile.android.preference.DictionaryUtils;
import com.wxxr.mobile.android.preference.PreferenceManagerModule;
import com.wxxr.mobile.android.security.DummySiteSecurityModule;
import com.wxxr.mobile.core.event.api.EventRouterImpl;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.microkernel.api.IServiceAvailableCallback;
import com.wxxr.mobile.core.rest.provider.GSONProvider;
import com.wxxr.mobile.core.rest.provider.XStreamProvider;
import com.wxxr.mobile.core.rpc.http.api.HttpHeaderNames;
import com.wxxr.mobile.core.rpc.rest.RestEasyClientModule;
import com.wxxr.mobile.core.rpc.rest.provider.StringTextStar;
import com.wxxr.mobile.core.util.LRUMap;
import com.wxxr.mobile.preference.api.IPreferenceManager;
import com.wxxr.test.business.BusinessManagerModule;

/**
 * @author neillin
 * 
 */
public class ComHelperFramework
		extends
			AndroidFramework<ComHelperAppContext, AbstractModule<ComHelperAppContext>>
		implements
			IComHelperFramework {

	private static final Trace log = Trace.register(ComHelperFramework.class);

	private final ApplicationManager app;
	private final String userAgent;
	private class ComHelperAppContextImpl extends AbstractContext
			implements
				ComHelperAppContext {

		@Override
		public IAndroidFramework getApplication() {
			return ComHelperFramework.this;
		}

	};

	private ComHelperAppContextImpl context = new ComHelperAppContextImpl();
	private LRUMap<Long, ITaskHandler> taskHandlers = new LRUMap<Long, ITaskHandler>(
			100, 30);
	private AtomicLong taskSeqno = new AtomicLong();
	public ComHelperFramework(ApplicationManager app) {
		this.app = app;
		this.userAgent = new WebView(app).getSettings().getUserAgentString();
		context.setAttribute(HttpHeaderNames.USER_AGENT, this.userAgent);
		if (log.isDebugEnabled()) {
			log.debug("UserAgent:" + this.userAgent);
		}

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.android.app.IAndroidApplication#getAndroidApplication()
	 */
	@Override
	public Application getAndroidApplication() {
		// getDeviceUUID()
		return app;
	}

	@Override
	protected ComHelperAppContext getContext() {
		return context;
	}

	@Override
	protected void initModules() {
		registerKernelModule(new DummySiteSecurityModule<ComHelperAppContext>());
		registerKernelModule(new EventRouterImpl<ComHelperAppContext>());
		registerKernelModule(new NetworkManagementModule<ComHelperAppContext>());
		registerKernelModule(new PreferenceManagerModule<ComHelperAppContext>());
		HttpRpcServiceModule<ComHelperAppContext> m = new HttpRpcServiceModule<ComHelperAppContext>();
		m.setEnablegzip(false);
		m.setConnectionPoolSize(30);
		registerKernelModule(m);

		RestEasyClientModule<ComHelperAppContext> rest = new RestEasyClientModule<ComHelperAppContext>();
		rest.getClient().register(XStreamProvider.class);
		rest.getClient().register(GSONProvider.class);
		rest.getClient().register(StringTextStar.class);
		
		registerKernelModule(rest);

		registerKernelModule(new RestEasyClientModule<ComHelperAppContext>());
		registerKernelModule(new DXHZSettingModule());
		registerKernelModule(new SecurityServiceModule());
		registerKernelModule(new ClientUpdateModule());
		registerKernelModule(new GuiShuDiService());
		registerKernelModule(new FeedBackModule());
		registerKernelModule(new PrivateSMServiceModule());
		registerKernelModule(new NotificationServiceModule());
		registerKernelModule(new NotificationService());
		registerKernelModule(new SmsContentParseModule());
		registerKernelModule(new ClientCustomModule());
		registerKernelModule(new LouHuaHuizhiModule());
		registerKernelModule(new ClientConfigManagerService());
		registerKernelModule(new PiggybackService());
		registerKernelModule(new CallRecorderModule());
		registerKernelModule(new ContentManager());
		registerKernelModule(new RuleEngineService());
		registerKernelModule(new HtmlMessageManager());

		registerKernelModule(new MTreeSyncConnector());
		registerKernelModule(new MTreeSyncScoreModule());
		registerKernelModule(new SSHXMsgManagementModule());
		registerKernelModule(new MessageProviderModule());
		registerKernelModule(new ContentCleanModule());
		registerKernelModule(new UserUsageRecorderModule());
		registerKernelModule(new OfficeLineHtmlProvideService());

		registerKernelModule(new ImportOldDataService());
		registerKernelModule(new PrivateSMServiceNetModule());
		registerKernelModule(new UserHeadModule());
    
		registerKernelModule(new NoticeSyncModule());
		registerKernelModule(new MicoNewsModleService());
		registerKernelModule(new GDDownwebServiceModule());
		registerKernelModule(new AdvancedRegionQueryModule());
		registerKernelModule(new FecthZhuShouBaoboModule());
		registerKernelModule(new GuessLikeDetailCacheService());
		registerKernelModule(new FetchUrlModul());
		registerKernelModule(new MoblieBusinessModule());
		 registerKernelModule(new BDLocationModule());
		try {
			File f = new File(this.context.getApplication()
					.getDataDir(Constant.APP_DATA_BASE, Context.MODE_PRIVATE).getPath());
			if (!f.exists()) {
				f.mkdirs();
			}

			File nomedia = new File(f.getAbsolutePath() + "/.nomedia");
			if (!nomedia.exists()) {
				nomedia.createNewFile();
			}
		} catch (Exception ee) {

		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.android.app.AndroidApplication#start()
	 */
	@Override
	public void start() {
		context.checkServiceAvailable(IPreferenceManager.class,
				new IServiceAvailableCallback<IPreferenceManager>() {

					@Override
					public void serviceAvailable(IPreferenceManager prefMgr) {
						Dictionary<String, String> d = prefMgr
								.getPreference(IPreferenceManager.SYSTEM_PREFERENCE_NAME);
						if (d == null) {
							try {
								InputStream is = app.getAssets().open(
										"server.properties");
								Properties mProps = new Properties();
								mProps.load(is);
								d = DictionaryUtils.clone(mProps);
								prefMgr.newPreference(
										IPreferenceManager.SYSTEM_PREFERENCE_NAME,
										d);
							} catch (Throwable e) {
								log.error(
										"Failed to load server.properties to system preferences",
										e);
							}
						}
					}
				});
		super.start();
	}
	@Override
	public String getApplicationName() {
		return Version.getVersionName();
	}
	@Override
	public String getUserAgentString() {
		return this.userAgent;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.android.app.AndroidFramework#handleSysCrash(java.lang
	 * .Throwable)
	 */
	@Override
	protected void handleSysCrash(Throwable t) {
		if (getService(IUserUsageDataRecorder.class) != null) {
			getService(IUserUsageDataRecorder.class).doRecord(
					ActivityID.SYSCRASH.ordinal());
		}
		super.handleSysCrash(t);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.android.app.AndroidFramework#getDataDir(java.lang.String,
	 * int)
	 */
	@Override
	public File getDataDir(String name, int mode) {
		File dataDir;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			dataDir = new File(Environment.getExternalStorageDirectory(), name);
		} else {
			dataDir = getAndroidApplication().getDir(name, mode);
		}
		return dataDir;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.mobile.android.app.AndroidFramework#getApplicationBuildNnumber()
	 */
	@Override
	public String getApplicationBuildNnumber() {
		return Version.getBuildNumber();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.android.app.AndroidFramework#getApplicationVersion()
	 */
	@Override
	public String getApplicationVersion() {
		return Version.getVersionNumber();
	}
	@Override
	public Long registerTaskHandler(ITaskHandler handler) {
		if (handler == null) {
			throw new IllegalArgumentException("Invalid task handler : NULL");
		}
		long hid = this.taskSeqno.getAndIncrement();
		this.taskHandlers.put(hid, handler);
		return hid;
	}

	@Override
	public ITaskHandler getTaskHandler(Long hid) {
		return this.taskHandlers.remove(hid);
	}

	@Override
	public String getDeviceId() {
		// TODO Auto-generated method stub
		String deviceid= super.getDeviceId();
		if(deviceid==null||deviceid.length()==0){
			deviceid=getDeviceUUID();
		}
		return deviceid;
		// return "20131103";

	}

}
