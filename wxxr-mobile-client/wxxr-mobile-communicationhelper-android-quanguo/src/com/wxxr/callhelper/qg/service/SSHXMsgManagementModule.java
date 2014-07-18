/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import com.tencent.mm.sdk.platformtools.Log;
import com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IPiggybackService;
import com.wxxr.callhelper.qg.ISSHXMsgManagementModule;
import com.wxxr.callhelper.qg.IUserActivationService;
import com.wxxr.callhelper.qg.PiggybackPayloadType;
import com.wxxr.callhelper.qg.bean.SSHXMsgLifeStatus;
import com.wxxr.callhelper.qg.bean.SSHXMsgStatus;
import com.wxxr.callhelper.qg.constant.Constant;
import com.wxxr.callhelper.qg.event.UserBoundEvent;
import com.wxxr.callhelper.qg.sync.IDataConsumer;
import com.wxxr.callhelper.qg.sync.IMTreeDataSyncServerConnector;
import com.wxxr.callhelper.qg.utils.GongYiPinDao;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.phone.helper.workstation.SSHXMessageBO;

/**
 * 主要职责： 1.sshx消息同步 2.管理sshx消息对象 4.统计数据 播发消息查询 5.提供已读取的消息历史
 * 
 * @author wangxuyang
 * 
 */
public class SSHXMsgManagementModule extends AbstractModule<ComHelperAppContext> implements ISSHXMsgManagementModule {
	private static final String SSHX_HTML_CONTENT = "sshx_html";
	private static final Trace log = Trace.register(SSHXMsgManagementModule.class);
	int hashGroupSize = 50;
	private final static String NULL_PHONE_NUMBER = "99999999999";
	private final static String TYPE_STATUS_MSG_STATUS_PREFIX = "s1_";
	private final static String TYPE_STATUS_LIFECYCLE_STATUS_PREFIX = "s2_";
	private final static String TYPE_STATUS_FKHTML_STATUS_NAME = "fk_html";
	
	private static class MsgRecord {
		long timestamp;
		long messageId;
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (messageId ^ (messageId >>> 32));
			result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
			return result;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MsgRecord other = (MsgRecord) obj;
			if (messageId != other.messageId)
				return false;
			if (timestamp != other.timestamp)
				return false;
			return true;
		}
		
	}
	// ============================SSHX消息同步相关逻辑===================================
//	private MTreeDataSyncClientService syncClient = new MTreeDataSyncClientService();
	private Map<Object, List<Long>> datas = new HashMap<Object, List<Long>>();
	private Set<Object> receiving = new HashSet<Object>();
	private Set<Object> receiveFailed = new HashSet<Object>();
	private String phoneNumber = NULL_PHONE_NUMBER;
	private boolean sshxSyncStarted = false;
	// 用户登录监听处理,用户登录时重启同步组件
	private IEventListener userLoginListener = new IEventListener() {
		public void onEvent(IBroadcastEvent event) {
			if (event instanceof UserBoundEvent) {
				UserBoundEvent loginEvent = (UserBoundEvent) event;
				if (log.isDebugEnabled()) {
					log.debug(String.format("Receiving user bound event:%s,phoneNumber[old:%s,new:%s]", event, phoneNumber, ((UserBoundEvent) event).getUserId()));
				}
				if (sshxSyncStarted && (StringUtils.isNotBlank(loginEvent.getUserId())&&(!loginEvent.getUserId().equals(phoneNumber)))) {
					phoneNumber = loginEvent.getUserId();
					if (StringUtils.isNotBlank(phoneNumber)) {
						context.getExecutor().execute(new Runnable() {
							@Override
							public void run() {
								restartSync();
							}
						});
						
					}
				}
			}

		}
	};
	private Comparator<SSHXMessageBO> msgComparator = new Comparator<SSHXMessageBO>() {
		public int compare(SSHXMessageBO o1, SSHXMessageBO o2) {
			int r = 0;
			r = (int) (o1.getAdsId().longValue() - o2.getAdsId().longValue());
			if (r > 0) {
				r = 1;
			} else if (r < 0) {
				r = -1;
			}
			return r;
		}
	};

	public byte[] toBytes(List<SSHXMessageBO> data) {
		if (data == null) {
			return null;
		}
		if (data != null && !data.isEmpty()) {
			Collections.sort(data, msgComparator);
		}

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		SSHXMessageBO[] infos = data.toArray(new SSHXMessageBO[data.size()]);
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(infos);
			return bos.toByteArray();
		} catch (IOException e) {
			log.warn("Error when serilize the msgs", e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				bos.close();
			} catch (IOException e) {
			}
		}
		return null;
	}

	public List<SSHXMessageBO> fromBytes(byte[] data) throws Exception {
		if (data == null) {
			return null;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			SSHXMessageBO[] msgs = (SSHXMessageBO[]) ois.readObject();
			return Arrays.asList(msgs);
		} catch (Exception e) {
			log.warn("Error when read msg from bytes", e);
			throw e;
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				bis.close();
			} catch (IOException e) {
			}
		}

	}

	private IDataConsumer sshxMsgConsumer = new IDataConsumer() {

		public String[] getAllReceivedDataKeys() {

			return datas.keySet().toArray(new String[datas.size()]);
		}

		public void dataReceived(Object key, byte[] data) {
			try {
				List<SSHXMessageBO> datas = fromBytes(data);
				if (log.isDebugEnabled()) {
					log.debug("Received data for the key:" + key);
					log.debug(String.format("key:[%s]", key));
				}
				processReceivedData(key, datas);
				dataChanged = true;
				receiving.remove(key);
				receiveFailed.remove(key);
			} catch (Exception e) {
				log.warn("Failed to deserilize the data", e);
			}

		}

		public void dateReceiving(Object key) {
			if (log.isDebugEnabled()) {
				log.debug("Receiving data for the key:" + key);
			}
			receiving.add(key);
		}

		public void dataReceivingFailed(Object key) {
			if (log.isDebugEnabled()) {
				log.debug("Failed to Receive data for the key:" + key);
			}
			receiving.remove(key);
			receiveFailed.add(key);
		}

		public void allDataReceived() {
			if (log.isDebugEnabled()) {
				log.debug("All data Received.");
			}
			processAllDataReceived();

		}

		public byte[] removeReceivedData(Object key) {
			List<Long> ids = datas.remove(key);
			return toBytes(getSSHXMessages(ids));
		}

		public byte[] getReceivedData(Object key) {
			List<Long> ids = datas.get(key);
			Set<Long> valid_ids = loadAllValidMsgIds();
			if (ids!=null&&valid_ids!=null) {
				ids.retainAll(valid_ids);
			}
			return toBytes(getSSHXMessages(ids));
		}

		public Object getGroupId(byte[] leafPayload) {
			if (leafPayload == null) {
				return null;
			}
			LongBuffer bf = ByteBuffer.wrap(leafPayload).asLongBuffer();
			Object grpId = bf.get();
			List<Long> list = new ArrayList<Long>();
			while (bf.hasRemaining()) {
				list.add(bf.get());
			}
			datas.put(grpId, list);
			return grpId;
		}
	};

	private void clear() {
		datas.clear();
		receiving.clear();
		receiveFailed.clear();
		if (cache!=null) {
			cache.clear();
		}
		htmlDownloadingMsgs.clear();
	}

	private void processAllDataReceived() {
		Set<Long> all_ids = new HashSet<Long>();
		if (!receiving.isEmpty() || !receiveFailed.isEmpty()) {
			return;
		}
		synchronized (datas) {
			for (Entry<Object, List<Long>> entry : datas.entrySet()) {
				all_ids.addAll(entry.getValue());
			}
		}
		List<Long> list = new ArrayList<Long>();
		list.addAll(all_ids);
		//storeMessageIds(phoneNumber, list);
		Set<Long> local_storage_ids = loadAllValidMsgIds();
		if (local_storage_ids != null) {
			local_storage_ids.removeAll(all_ids);// 获取本地有，服务器端没有的消息集合
			if (!local_storage_ids.isEmpty()) {
				for (Long msgId : local_storage_ids) {
					try {
						dataChanged = true;
						updateStatus(msgId, TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.COMPLETED.name());
					} catch (Exception e) {
						log.warn("Error when update the msg status", e);
					}
				}
			}
		}
		if (dataChanged) {			
			cache  = loadBroadcastableSSHXMsgs();
			dataChanged = false;
		}

		
	}

	private Set<Long> loadAllValidMsgIds() {
		Set<Long> ids = new HashSet<Long>();
		List<Long> tmpList = null;
		tmpList = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.WAITING.name());
		if (tmpList != null && tmpList.size() > 0) {
			ids.addAll(tmpList);
		}
		tmpList = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.PAUSED.name());
		if (tmpList != null && tmpList.size() > 0) {
			ids.addAll(tmpList);
		}
		tmpList = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.ABORTED.name());
		if (tmpList != null && tmpList.size() > 0) {
			ids.addAll(tmpList);
		}
		return ids;
	}

	private List<Long> getSSHXMsgIds(String statusName, String statusValue) {
		List<Long> ids = new ArrayList<Long>();
		try {
			String[] contentIds = getContentManager().queryContentIds(TYPE_CONTENT, statusName, statusValue);
			if (contentIds != null && contentIds.length > 0) {
				for (String contentId : contentIds) {
					if (StringUtils.isNumeric(contentId)) {
						ids.add(Long.valueOf(contentId));
					}
				}
			}
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(String.format("Error when get msgIds, query condition(%s=%s)", statusName, statusValue), e);
			}
		}
		return ids;
	}

	private void processReceivedData(Object key, List<SSHXMessageBO> data) {
		if (data != null) {
			List<Long> ids = new ArrayList<Long>();
			for (SSHXMessageBO sshxMsg : data) {
				ids.add(sshxMsg.getAdsId());
				saveOrUpdateSSHX(sshxMsg);
			}
			datas.put(key, ids);
		} else {
			datas.remove(key);
		}
	}

	private List<SSHXMessageBO> getSSHXMessages(List<Long> ids) {
		
		if (ids != null && ids.size() > 0) {
			List<SSHXMessageBO> ret = new ArrayList<SSHXMessageBO>();
			try {
				for (Long id : ids) {
					SSHXMessageBO msg = getSSHX(id);
					if (msg != null) {
						ret.add(msg);
					}
				}
			} catch (Exception e) {
				log.warn("Get msg error", e);
			}
			return ret;
		}
		return null;
	}
//	private void saveSSHXMsg(SSHXMessageBO msg) {
//		try {
//			saveOrUpdateSSHX(msg);
//			updateStatus(msg.getAdsId(), TYPE_STATUS_LIFECYCLE_STATUS_NAME, SSHXMsgLifeStatus.SAVEED.name());
//			scheduleHTMLDownloading(msg);
//		} catch (Exception e) {
//			log.warn("Save error", e);
//		}
//	}

	/**
	 * @param msg
	 * @throws IOException
	 * @throws Exception
	 */
	protected void scheduleHTMLDownloading(SSHXMessageBO msg) {
		try {
			String id = context.getService(IHtmlMessageManager.class).download(msg.getMessage());
			if (id != null) {
				updateStatus(msg.getAdsId(), TYPE_STATUS_FKHTML_STATUS_NAME, id);
				htmlDownloadingMsgs.put(msg.getAdsId(), id);
				if(msg.getChannel().equals(Constant.GONG_YI_PIN_DAO)){
					GongYiPinDao.getInstance(AppUtils.getFramework().getAndroidApplication().getBaseContext()).setGYPDKeys(""+id);
				}
			}
		}catch(Exception e){
			log.warn("Failed to schedule html download for sshx message :"+msg.getAdsId()+", will retry later .", e);
		}
	}

	private boolean isNetworkConnected() {
		return context.getService(IDataExchangeCoordinator.class).checkAvailableNetwork() > 0;
	}

	void restartSync() {
		IMTreeSyncDataEngineService  	syncClient=getService(IMTreeSyncDataEngineService.class);
		if (log.isDebugEnabled()) {
			log.debug("Restarting sshx msg sync service ...");
		}
		if (sshxSyncStarted) {
			
			syncClient.stopSync();
		//	service.destory();
			clear();
			sshxSyncStarted = false;
		}
		while (!sshxSyncStarted) {
			try {
				if (!isNetworkConnected()) {
					continue;
				}
				syncClient.registerConsumer("SSHX/" + phoneNumber, sshxMsgConsumer);
			//	syncClient.init(context);
				syncClient.startSync();
				sshxSyncStarted = true;
				if (log.isDebugEnabled()) {
					log.debug("SSHX msg sync service started successfully.");
				}
			} finally {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {

				}
			}

		}

	}

	// ======================== 统计数据相关逻辑 ============================
	private AbstractPigyybackPayloadProvider<MsgRecord> readMsgStatistic_PBProvider = new AbstractPigyybackPayloadProvider<MsgRecord>() {
	
		@Override
		protected MsgRecord loadRecord(DataInput input) throws IOException {
			try {
				MsgRecord rcd = new MsgRecord();
				rcd.timestamp = input.readLong();
				rcd.messageId = input.readLong();
				return rcd;
			}catch(EOFException e){
				return null;
			}
		}

		@Override
		protected void writeRecord(DataOutput output, MsgRecord record)
				throws IOException {
			output.writeLong(record.timestamp);
			output.writeLong(record.messageId);
			
		}

		@Override
		protected void writeBuffer(ByteBuffer buffer, MsgRecord record)
				throws IOException {
			buffer.putLong(record.timestamp);
			buffer.putLong(record.messageId);
		}

		@Override
		protected String getDataStoageFileName() {
			return "sshxMsgs.txt";
		}
	};

//	private byte[] getStatisticsData() {
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		ObjectOutputStream oos = null;
//		try {
//			oos = new ObjectOutputStream(bos);
//			for (Entry<Long, Long> entry : alreadyReadSSHXMsgs.entrySet()) {
//				oos.writeLong(entry.getKey());
//				oos.writeLong(entry.getValue());
//			}
//			return bos.toByteArray();
//		} catch (IOException e) {
//			log.warn("Error when serilize the msgs", e);
//		} finally {
//			try {
//				if (oos != null) {
//					oos.close();
//				}
//				bos.close();
//			} catch (IOException e) {
//			}
//		}
//		return null;
//	}

//	/**
//	 * 已读消息Ids key:msgId value:read time
//	 */
//	private Map<Long, Long> alreadyReadSSHXMsgs = new HashMap<Long, Long>();

	public void addReadSSHXForStatistics(Long msgId, long readTime) {
		try {
			// 更新消息状态
			updateStatus(msgId, TYPE_STATUS_LIFECYCLE_STATUS_PREFIX+phoneNumber, SSHXMsgLifeStatus.READED.name());
			updateStatus(msgId, "readtime", readTime + "");
			MsgRecord rcd = new MsgRecord();
			rcd.timestamp = readTime;
			rcd.messageId = msgId;
			readMsgStatistic_PBProvider.addRecord(rcd);
			//remove msg from cache
			if (cache!=null) {
				synchronized (cache) {
					cache.remove(msgId);
				}
			}
			String htmlId=getHtmlIdByMsgId(msgId);
			recordHtml(htmlId);
//			alreadyReadSSHXMsgs.put(msgId, readTime);
		} catch (Exception e) {
			log.warn("Error when updating sshx msg lifecycle status", e);
		}
	}

	public String[] getMessageHistory() {
		
		try {
			return context.getService(IContentManager.class).queryContentIds(SSHX_HTML_CONTENT, null, null);
		} catch (IOException e) {
			
		}
		return null;
//		try {
//			List<Long> ids = getSSHXMsgIds(TYPE_STATUS_LIFECYCLE_STATUS_PREFIX+phoneNumber, SSHXMsgLifeStatus.READED.name());
//			//消息历史里需要去掉终止和暂停播发的消息
//			List<Long> tmpList = null;
//			tmpList = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.PAUSED.name());
//			if (tmpList != null && tmpList.size() > 0) {
//				ids.removeAll(tmpList);
//			}
//			tmpList = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.ABORTED.name());
//			if (tmpList != null && tmpList.size() > 0) {
//				ids.removeAll(tmpList);
//			}			
//			return ids != null && ids.size() > 0 ? ids.toArray(new Long[ids.size()]) : null;
//		} catch (Exception e) {
//			log.warn("Get message history error", e);
//		}
//		return null;
	}

	// ==================播发控制相关========================================
	private boolean dataChanged = false;
	private Map<Long,String> htmlDownloadingMsgs = new ConcurrentHashMap<Long,String>();
	private Timer downloadChecker = new Timer();
	private Map<Long,SSHXMessageBO> cache = null;
	
	public SSHXMessageBO[] getBroadcastableSSHXMsgs() {	
		return cache!=null && cache.size()>0?  cache.values().toArray(new SSHXMessageBO[cache.size()]):null;
	}

	private Map<Long,SSHXMessageBO> loadBroadcastableSSHXMsgs() {
		long startTime = System.currentTimeMillis();
		List<Long> ids = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.WAITING.name());
		if (ids != null && ids.size() > 0) {
			//移除已读过的消息
			List<Long> readed_ids = getSSHXMsgIds(TYPE_STATUS_LIFECYCLE_STATUS_PREFIX+phoneNumber, SSHXMsgLifeStatus.READED.name());
			if (readed_ids!=null&&readed_ids.size()>0) {
				ids.removeAll(readed_ids);
			}
			Map<Long,SSHXMessageBO> ret = new ConcurrentHashMap<Long, SSHXMessageBO>();
			for (Long id : ids) {
				String htmlId = getHtmlIdByMsgId(id);
				if(htmlId == null){
					try {
						scheduleHTMLDownloading(getSSHX(id));
					} catch (Exception e) {
						log.warn("Failed to load in SSHX message from content manager", e);
					}
				}else if (context.getService(IHtmlMessageManager.class).isDownloaded(htmlId)) {
					SSHXMessageBO msg = null;
					try {
						msg = getSSHX(id);
						if (msg != null) {
							ret.put(id, msg);
						}
					} catch (Exception e) {
						log.warn(String.format("Error when get msg[id=%d]", id), e);
					}
				}

			}
			if (log.isDebugEnabled()) {
				long endTime = System.currentTimeMillis();					
				log.debug(String.format("All Broadcastable msgs size:%d,Read Start time:%d, End time:%d, Total  times:%d",ret.size(),startTime,endTime, endTime-startTime));
			}
			return ret;
		}
		return null;
	}
	// =====================SSHX对象维护相关==============================
	@Override
	public String[] getAllBroadcastableHtmlIds() {
		if (cache!=null&&cache.size()>0) {
			Set<Long> msgIds = cache.keySet();
			List<String> list = new ArrayList<String>();
			for (Long msgId : msgIds) {
				String htmlId = getHtmlIdByMsgId(msgId);
				if (StringUtils.isNotBlank(htmlId)) {
					list.add(htmlId);
				}				
			}
			return list.size()>0 ? list.toArray(new String[list.size()]):null;
		}
		
		return null;
	}
	
	public String getHtmlIdByMsgId(Long msgId) {
		if (msgId == null) {
			return null;
		}
		try {
			String htmlId = getContentManager().getStatus(TYPE_CONTENT, msgId.toString(), TYPE_STATUS_FKHTML_STATUS_NAME);
			if (StringUtils.isNotBlank(htmlId)) {
				return htmlId;
			}
		} catch (IOException e) {
			log.warn("Error when get fk_htmlId for msgId:" + msgId, e);
		}
		return null;
	}

	private byte[] toByteContent(SSHXMessageBO bo) throws Exception {
		if (bo == null) {
			return null;
		}
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);
			oos.writeObject(bo);
			oos.close();
			oos = null;
			bos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			log.warn("Error when seralizable obj", e);
			throw new Exception("Error when seralizable obj", e);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
				bos.close();
			} catch (IOException e) {
			}
		}

	}


	private SSHXMessageBO fromByteContent( byte[] content) throws Exception {
		if (content == null) {
			return null;
		}
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new ByteArrayInputStream(content);
			ois = new ObjectInputStream(bis);
			return (SSHXMessageBO)ois.readObject();
		} catch (Exception e) {
			log.warn("Error when seralizable obj", e);
			throw new Exception("Error when seralizable obj", e);
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				bis.close();
			} catch (IOException e) {
			}
		}

	}

	public void saveOrUpdateSSHX(SSHXMessageBO sshx) {
		byte[] content = null;
		try {
			content = toByteContent(sshx);
			if (content != null) {// 此处把sshx消息的status从字节内容里分离出来进行存储，是为了减少根据状态查询产生的IO
				getContentManager().saveContent(TYPE_CONTENT, sshx.getAdsId().toString(), content);
			
				updateStatus(sshx.getAdsId(), TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, sshx.getStatus());
			}
			String htmlId = getHtmlIdByMsgId(sshx.getAdsId());
			if(htmlId == null){
				scheduleHTMLDownloading(sshx);
			}
			
		} catch (Exception e) {
			log.error(String.format("Save sshx msg error , sshx message :[%s]", sshx), e);
		}
	}

	public SSHXMessageBO getSSHX(Long msgId) throws Exception {
		if (msgId == null) {
			return null;
		}
		byte[] content = getContentManager().getContent(TYPE_CONTENT, msgId.toString());
		if (content != null) {
			SSHXMessageBO bo = null;
			try {
				bo = fromByteContent(content);
				
				return bo;
			} catch (Exception e) {
				log.error("Read sshx msg error", e);
				throw e;
			}
		}
		return null;

	}

	public SSHXMessageBO removeSSHX(Long msgId) throws Exception {
		if (msgId == null) {
			throw new IllegalArgumentException("msgId is null!!!");
		}
		SSHXMessageBO bo = getSSHX(msgId);
		getContentManager().delete(TYPE_CONTENT, msgId.toString());
		return bo;
	}
	@Override
	public Long[] getPrepareRemovedMsgs() {
		List<Long> completedIds = getSSHXMsgIds(TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber, SSHXMsgStatus.COMPLETED.name());
		if (completedIds!=null) {			
			return completedIds.size()>0 ? completedIds.toArray(new Long[completedIds.size()]):null;
		}
		return null;
	}
	

	/**
	 * 更新消息状态
	 * 
	 * @param msgId
	 * @param statusName
	 * @param statusValue
	 * @throws Exception
	 */
	void updateStatus(Long msgId, String statusName, String statusValue) throws Exception {
		if (msgId == null || StringUtils.isBlank(statusName) || StringUtils.isBlank(statusValue)) {
			throw new IllegalArgumentException(String.format("Illegal argument:[msgId=%s,statusName=%s,statusValue=%s]", msgId, statusName, statusValue));
		}
		getContentManager().updateStatus(TYPE_CONTENT, msgId.toString(), statusName, statusValue);
	}
	//========================手机号与sshx消息关系维护=============================
	/*private File getStorageLocation(){
		String dir = context.getApplication().getAndroidApplication()
				.getDir("phmsgs", Context.MODE_PRIVATE).getPath();
		File file = new File(dir);
		if(!file.exists()){
			file.mkdirs();
		}
		return file;
	}
	private File getStorageFile(String phoneNumber){
		if (StringUtils.isBlank(phoneNumber)) {
			throw new IllegalArgumentException("phoneNumber is null!!!");
		}
		File file = new File(getStorageLocation(),phoneNumber);
		return file;
	}
	private List<Long> loadMessageIdsByPhoneNumber(String phoneNumber){
		File file = getStorageFile(phoneNumber);
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			return loadData(dis);
		} catch (Exception e) {
			log.warn("Error when loading msgIds by phoneNumber:"+phoneNumber, e);
		}finally{
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {	}
			}
			if (dis!=null) {
				try {
					dis.close();
				} catch (IOException e) {	}
			}
		}
		return null;
	}
	private List<Long> loadData(DataInput in) throws IOException{
		int size = in.readInt();
		List<Long> data = null;
		if (size>0) {
			data = new ArrayList<Long>();
			while (size-->0) {
				data.add(in.readLong());
			}
		}
		return data;
	}

	
	private void storeMessageIds(String phoneNumber,List<Long> msgIds) {
		if (StringUtils.isBlank(phoneNumber)||msgIds==null||msgIds.size()==0) {
			throw new IllegalArgumentException("phoneNumber or msgIds is null");
		}
		File file = getStorageFile(phoneNumber);
		DataOutputStream out = null;
		FileOutputStream fos = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file, false);
			out = new DataOutputStream(fos);
			writeData(out, msgIds);
		} catch (Exception e) {
			log.warn("Store msgIds by phoneNumber error", e);
		}finally{
			if (fos!=null) {
				try {
					fos.close();
				} catch (IOException e) {	}
			}
			if (out!=null) {
				try {
					out.close();
				} catch (IOException e) {	}
			}
		}
	}
	private void writeData(DataOutput out,List<Long> datas) throws IOException{
		if (datas==null||datas.size()>0) {
			return ;
		}
		out.writeInt(datas.size());
		for (Long data : datas) {
			out.writeLong(data);
		}
	}*/

	// ======================= external service =====================
	protected IEventRouter getEventRouter() {
		return getService(IEventRouter.class);
	}

	protected IContentManager getContentManager() {
		return getService(IContentManager.class);
	}

	// ======================== lifecycle method ====================
	@Override
	protected void initServiceDependency() {
		addRequiredService(IEventRouter.class);
		addRequiredService(IDataExchangeCoordinator.class);
		addRequiredService(IUserActivationService.class);
		addRequiredService(IPiggybackService.class);
		addRequiredService(IContentManager.class);
		addRequiredService(IHtmlMessageManager.class);
		addRequiredService(IMTreeDataSyncServerConnector.class);
		addRequiredService(IMTreeSyncDataEngineService.class);
		
	}

	@Override
	protected void startService() {
		context.registerService(ISSHXMsgManagementModule.class, this);
		readMsgStatistic_PBProvider.start(context);
		context.getService(IPiggybackService.class).registerProvider(PiggybackPayloadType.READ_MSG_STATISTIC.ordinal(), readMsgStatistic_PBProvider);
		getEventRouter().registerEventListener(UserBoundEvent.class, userLoginListener);
		String defaultPhoneNumber = context.getService(IUserActivationService.class).getCurrentUserId();
		if (StringUtils.isNotBlank(defaultPhoneNumber)) {
			phoneNumber = defaultPhoneNumber;
		}
		cache = loadBroadcastableSSHXMsgs();
		context.getExecutor().execute(new Runnable() {
			public void run() {
				restartSync();// 启动sshx同步组件
			}
		});
		
		downloadChecker.schedule(new TimerTask() {
			@Override
			public void run() {
				if (htmlDownloadingMsgs.isEmpty()) {
					return;
				}
				Set<Long> msgIds = htmlDownloadingMsgs.keySet();
				if (cache!=null) {
					synchronized (cache) {
						for (Long msgId : msgIds) {
							if (cache.containsKey(msgId)) {
								htmlDownloadingMsgs.remove(msgId);
								continue;
							}
							String htmlId = htmlDownloadingMsgs.get(msgId);
							if (context.getService(IHtmlMessageManager.class).isDownloaded(htmlId)) {								
								SSHXMessageBO msg;
								try {
									msg = getSSHX(msgId);
									if (msg!=null) {
										cache.put(msgId, msg);
										htmlDownloadingMsgs.remove(msgId);
									}     
								} catch (Exception e) {
									log.warn("Get sshx message error", e);
								}
							}
						}
					}
				}
				
				
			}
		}, 10000,5000);
	}

	@Override
	protected void stopService() {
		if (sshxSyncStarted) {
			getService(IMTreeSyncDataEngineService.class).stopSync();
			getService(IMTreeSyncDataEngineService.class).unregisterConsumer("SSHX/" + phoneNumber, sshxMsgConsumer);
			sshxSyncStarted = false;
		}
		readMsgStatistic_PBProvider.stop();
		context.getService(IPiggybackService.class).unregisterProvider(PiggybackPayloadType.READ_MSG_STATISTIC.ordinal(), readMsgStatistic_PBProvider);
		getEventRouter().unregisterEventListener(UserBoundEvent.class, userLoginListener);
		context.unregisterService(ISSHXMsgManagementModule.class, this);
	}

	protected void recordHtml(String htmlId){
		try {
			context.getService(IContentManager.class).saveContent(SSHX_HTML_CONTENT, htmlId, htmlId.getBytes());
		} catch (IOException e) {
			log.error("record html error",e);
		}
	}

	@Override
	public Long getHtmlRecordLastModified(String id) {
		
		return context.getService(IContentManager.class).getContentLastModified(SSHX_HTML_CONTENT, id);
		
	}


}
