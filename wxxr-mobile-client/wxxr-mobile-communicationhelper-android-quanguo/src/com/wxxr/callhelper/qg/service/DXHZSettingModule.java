/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IDXHZSettingService;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.event.UserBoundEvent;
import com.wxxr.callhelper.qg.exception.NetworkNotAvailableException;
import com.wxxr.callhelper.qg.exception.UserNotBoundException;
import com.wxxr.callhelper.qg.rpc.AbstractMonitorRunnable;
import com.wxxr.callhelper.qg.rpc.ChannelParamUpdater;
import com.wxxr.callhelper.qg.rpc.DXHZSetting;
import com.wxxr.callhelper.qg.rpc.IClientConfigManagerService;
import com.wxxr.callhelper.qg.rpc.IDXHZSettingRestService;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.preference.DictionaryUtils;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.api.IExchangeHandler;
import com.wxxr.mobile.core.api.IProgressMonitor;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.rpc.http.api.IRestProxyService;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.mobile.preference.api.IPreferenceManager;
import com.wxxr.phone.helper.workstation.resource.IChannelDataResource;

/**
 * @author neillin
 * 
 */
public class DXHZSettingModule extends AbstractModule<ComHelperAppContext>
		implements IDXHZSettingService {

	private static final Trace log = Trace.register(DXHZSettingModule.class);

	private IPreferenceManager prefManager;
	private DXHZSetting currentSetting;
	private Map<String, ChannelParamUpdater> pChannels = new HashMap<String, ChannelParamUpdater>();
	private Map<String, String> channels;
	private IEventListener listener = new IEventListener() {

		@Override
		public void onEvent(IBroadcastEvent event) {
			if (event instanceof UserBoundEvent) {
				currentSetting = null;
			}

		}
	};
	private IExchangeHandler exchangeHandler = new IExchangeHandler() {

		@Override
		public void startExchange() {
			if ((currentSetting != null)
					&& (currentSetting.getUpdated() != null)
					&& currentSetting.getUpdated().booleanValue()) {
				context.getExecutor().execute(new Runnable() {

					@Override
					public void run() {
						try {
							// 如果 未选妇幼频道时，妇幼频道的出生日期不传到服务器
							DXHZSetting tmp = currentSetting;
							if (tmp != null
									&& (tmp.getSubscriptions() == null || !tmp
											.getSubscriptions()
											.contains("FYPD"))) {
								tmp.setFychanneldate(null);
							}
							context.getService(IRestProxyService.class)
									.getRestService(
											IDXHZSettingRestService.class)
									.sync(tmp);
							clearUpdateFlag();
						} catch (Throwable t) {
							log.warn("Failed to update dxhz setting to server",
									t);
						}
					}
				});
			}
		}
	};

	public DXHZSettingModule() {
		super();
		this.pChannels.put("FYPD", new YuerChannel());
		this.pChannels.put("HZMS", new SjzxChannelUpdater());
	}

	private IPreferenceManager getPrefManager() {
		if (this.prefManager == null) {
			this.prefManager = context.getService(IPreferenceManager.class);
		}
		return this.prefManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.callhelper.IDXHZSettingService#subscribeChannel(java.lang.String
	 * )
	 */
	@Override
	public void subscribeChannel(final String channel,
			final Map<String, String> params, final IProgressMonitor monitor) {
		context.getExecutor().execute(
				new AbstractMonitorRunnable(monitor, log) {

					@Override
					protected Object executeTask() throws Throwable {
						if (currentSetting == null) {
							doInit();
						}
						boolean flag = false;
						ChannelParamUpdater pCh = pChannels.get(channel);
						if (pCh != null) {
							pCh.update(params, currentSetting);
						}
						List<String> channels = currentSetting.getChannels();
						if (channels == null) {
							channels = new LinkedList<String>();
						}
						if (!channels.contains(channel)) {
							channels.add(channel);
							currentSetting.setSubscriptions(StringUtils.join(
									channels.iterator(), ','));
							flag = true;
						}
						if (flag) {
							currentSetting.setUpdated(true);
							updateLocalPreference(currentSetting);
						}
						return null;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.callhelper.IDXHZSettingService#unsubscriberChannel(java.lang
	 * .String)
	 */
	@Override
	public void unsubscriberChannel(final String channel,
			final IProgressMonitor monitor) {
		context.getExecutor().execute(
				new AbstractMonitorRunnable(monitor, log) {

					@Override
					protected Object executeTask() throws Throwable {
						if (currentSetting == null) {
							doInit();
						}
						List<String> channels = currentSetting.getChannels();
						if ((channels != null) && channels.remove(channel)) {
							if (channels.isEmpty()) {
								currentSetting.setSubscriptions(null);
							} else {
								currentSetting.setSubscriptions(StringUtils
										.join(channels.iterator(), ','));
								currentSetting.setUpdated(true);
							}
							updateLocalPreference(currentSetting);
							return true;
						}
						return false;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.callhelper.IDXHZSettingService#setReceivingMode(java.lang.String
	 * )
	 */
	@Override
	public void setReceivingMode(final Integer mode,
			final IProgressMonitor monitor) {
		context.getExecutor().execute(
				new AbstractMonitorRunnable(monitor, log) {

					@Override
					protected Object executeTask() throws Throwable {
						if (currentSetting == null) {
							doInit();
						}
						Integer old = currentSetting.getReceiveStyle();
						if (!Tools.equals(old, mode)) {
							currentSetting.setReceiveStyle(mode);
							currentSetting.setUpdated(true);
							updateLocalPreference(currentSetting);
						}
						return null;
					}
				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.callhelper.IDXHZSettingService#getSubscribedChannels()
	 */
	@Override
	public List<String> getSubscribedChannels() throws UserNotBoundException,
			NetworkNotAvailableException {
		if (currentSetting == null) {
			doInit();
		}
		return currentSetting.getChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.callhelper.IDXHZSettingService#getReceivingMode()
	 */
	@Override
	public Integer getReceivingMode() throws UserNotBoundException,
			NetworkNotAvailableException {
		if (currentSetting == null) {
			doInit();
		}
		return currentSetting.getReceiveStyle();
	}

	protected Integer getDefaultMode() {
		return 2;
	}

	protected void doInit() throws UserNotBoundException,
			NetworkNotAvailableException {
		IPreferenceManager prefMgr = getPrefManager();
		if (!prefMgr.hasPreference(getPrefrencesName())) {
			IDataExchangeCoordinator s = context
					.getService(IDataExchangeCoordinator.class);
			if (s.checkAvailableNetwork() < 0) {
				throw new NetworkNotAvailableException(
						"没有网络连接，无法从服务器下载你的设置，请配置网络连接");
			}
			DXHZSetting setting = null;
			try {
				setting = context.getService(IRestProxyService.class)
						.getRestService(IDXHZSettingRestService.class)
						.getCustomer();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (setting != null) {
				updateLocalPreference(setting);
				currentSetting = setting;
			} else {
				currentSetting = new DXHZSetting();
			}
		} else {
			loadLocalPreference();
		}
	}

	/**
	 * @return
	 * @throws UserNotBoundException
	 */
	protected String getPrefrencesName() throws UserNotBoundException {
		IUserActivationService service = context
				.getService(IUserActivationService.class);
		String userId = service.getCurrentUserId();
		if (userId == null) {
			throw new UserNotBoundException("客户端未激活，请先激活客户端，再进行设置");
		}
		return getModuleName() + "_" + userId;
	}

	protected void loadLocalPreference() throws UserNotBoundException {
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getPrefrencesName());
		if (d == null) {
			return;
		}
		DXHZSetting setting = new DXHZSetting();
		setting.updateFrom(d);
		currentSetting = setting;
	}

	protected void clearUpdateFlag() throws UserNotBoundException {
		if (currentSetting != null) {
			currentSetting.setUpdated(false);
			updateLocalPreference(currentSetting);
		}
	}

	protected void updateLocalPreference(DXHZSetting setting)
			throws UserNotBoundException {
		if (setting == null) {
			return;
		}
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getPrefrencesName());
		if (d == null) {
			d = new Hashtable<String, String>();
			mgr.newPreference(getPrefrencesName(), d);
		} else {
			d = DictionaryUtils.clone(d);
		}
		setting.updateTo(d);
		mgr.putPreference(getPrefrencesName(), d);
	}

	private void loadLocalAllChannelInfo() {
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getModuleName());
		if (d != null) {
			if (channels == null) {
				channels = new HashMap<String, String>();
			}
			Enumeration<String> keys = d.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				channels.put(key, d.get(key));
			}
		}
	}

	private void storeLocalAllChannelInfo() {
		IPreferenceManager mgr = getPrefManager();
		Dictionary<String, String> d = mgr.getPreference(getModuleName());
		if (d == null) {
			d = new Hashtable<String, String>();
			mgr.newPreference(getModuleName(), d);
		}
		if (channels == null) {
			return;
		}
		for (Entry<String, String> channel : channels.entrySet()) {
			d.put(channel.getKey(), channel.getValue());
		}
		mgr.putPreference(getModuleName(), d);
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IPreferenceManager.class);
		addRequiredService(IRestProxyService.class);
		addRequiredService(IDataExchangeCoordinator.class);
		addRequiredService(IUserActivationService.class);
	}

	@Override
	protected void startService() {
		context.getService(IDataExchangeCoordinator.class).registerHandler(
				exchangeHandler);
		context.registerService(IDXHZSettingService.class,
				DXHZSettingModule.this);
		context.getService(IEventRouter.class).registerEventListener(
				UserBoundEvent.class, listener);
		loadLocalAllChannelInfo();
	}

	@Override
	protected void stopService() {
		if (context.getService(IEventRouter.class) != null) {
			context.getService(IEventRouter.class).unregisterEventListener(
					UserBoundEvent.class, listener);
		}
		if (context.getService(IDataExchangeCoordinator.class) != null) {
			context.getService(IDataExchangeCoordinator.class)
					.unregisterHandler(exchangeHandler);
		}
		storeLocalAllChannelInfo();
		context.unregisterService(IDXHZSettingService.class, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "DXHZSettingModule";
	}

	/**
	 * 
	 */
	protected void rescheduleInit() {
		// if(log.isDebugEnabled()){
		log.debug("reschedule init task ....");
		// Log.d("DXHZSettingModule",
		// "reschedule init task ..."+Log.isLoggable("DXHZSettingModule",
		// Log.DEBUG));
		// }
		context.invokeLater(new Runnable() {

			@Override
			public void run() {
				startService();
			}
		}, 5, TimeUnit.SECONDS);
	}

	@Override
	public Map<String, String> getSubscribedChannelParams(String channel)
			throws UserNotBoundException, NetworkNotAvailableException {
		if (currentSetting == null) {
			doInit();
		}
		ChannelParamUpdater pCh = pChannels.get(channel);
		if (pCh != null) {
			Map<String, String> map = new HashMap<String, String>();
			pCh.extract(currentSetting, map);
			return map;
		}
		return null;
	}

	public Map<String, String> getAllChannels()
			throws NetworkNotAvailableException {
		IDataExchangeCoordinator s = context
				.getService(IDataExchangeCoordinator.class);
		if (s.checkAvailableNetwork() < 0) {
			if (log.isDebugEnabled()) {
				log.debug("没有网络连接，无法从服务器下载你的设置，将采用本地缓存数据");
			}
			return channels;
		}
		Map<String, String> channels = null;
		byte[] channelData = context.getService(IRestProxyService.class)
				.getRestService(IChannelDataResource.class, getRestURL())
				.getChannels();
		if (channelData != null && channelData.length > 0) {
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(new ByteArrayInputStream(
						channelData));
				channels = (Map<String, String>) ois.readObject();
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug("Error when read channels from byte array", e);
				}
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {

					}
				}
			}
		}
		if (channels != null) {
			this.channels = channels;
		}
		return channels;
	}

	private String getRestURL() {
		return context.getService(IClientConfigManagerService.class)
				.getSSHXSyncRestServiceUrl();
	}

	@Override
	public void setNoannoyMode(final Boolean noannoyMode,
			IProgressMonitor monitor) {
		context.getExecutor().execute(
				new AbstractMonitorRunnable(monitor, log) {

					@Override
					protected Object executeTask() throws Throwable {
						if (currentSetting == null) {
							doInit();
						}
						Boolean old = currentSetting.getNoannoy();
						if (!Tools.equals(old, noannoyMode)) {
							currentSetting.setNoannoy(noannoyMode);
							currentSetting.setUpdated(true);
							updateLocalPreference(currentSetting);
						}
						return null;
					}
				});
	}

	@Override
	public void setItms(final Boolean ltmsMode, IProgressMonitor monitor) {
		context.getExecutor().execute(
				new AbstractMonitorRunnable(monitor, log) {

					@Override
					protected Object executeTask() throws Throwable {
						if (currentSetting == null) {
							doInit();
						}
						Boolean	old = currentSetting.getLtms();
						if (!Tools.equals(old, ltmsMode)) {
							currentSetting.setLtms(ltmsMode);
							currentSetting.setUpdated(true);
							updateLocalPreference(currentSetting);
						}

						return null;
					}
				});
	}

	@Override
	public void setReceiptSendStyle(final String receiptSendStyle,
			IProgressMonitor monitor) {
		context.getExecutor().execute(
				new AbstractMonitorRunnable(monitor, log) {

					@Override
					protected Object executeTask() throws Throwable {
						if (currentSetting == null) {
							doInit();
						}
						String old = currentSetting.getReceiptSendStyle();
						if (!Tools.equals(old, receiptSendStyle)) {
							currentSetting
									.setReceiptSendStyle(receiptSendStyle);
							currentSetting.setUpdated(true);
							updateLocalPreference(currentSetting);
						}
						return null;
					}
				});
	}

	@Override
	public Boolean getNoannoyMode() throws UserNotBoundException,
			NetworkNotAvailableException {
		if (currentSetting == null) {
			doInit();
		}
		return currentSetting.getNoannoy();
	}

	@Override
	public Boolean getltms() throws UserNotBoundException,
			NetworkNotAvailableException {
		if (currentSetting == null) {
			doInit();
		}
		return currentSetting.getLtms();
	}

	@Override
	public String getReceiptSendStyle() throws UserNotBoundException,
			NetworkNotAvailableException {
		if (currentSetting == null) {
			doInit();
		}
		return currentSetting.getReceiptSendStyle();
	}
}
