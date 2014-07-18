package com.wxxr.callhelper.qg.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.wxxr.callhelper.qg.ActivityID;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.db.dao.NoticeDao;
import com.wxxr.callhelper.qg.rpc.AbstractMonitorRunnable;
import com.wxxr.callhelper.qg.sync.IDataConsumer;
import com.wxxr.callhelper.qg.utils.Tools;
import com.wxxr.mobile.android.app.AppUtils;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.phone.helper.bo.NotifyMessage;

/**
 * 同步通知的数据
 * 
 * @author yangrunfei
 * 
 */
public class NoticeSyncModule extends AbstractModule<ComHelperAppContext>
		implements IHomeNotice {

	private static final Trace log = Trace.register(NoticeSyncModule.class);

	String SYNC_KEY = "SYNC_NOTIFICATION_MSG";
	// String path = Environment.getExternalStorageDirectory().getPath()
	// + "/sshx/notice/";
    /**
     * 每次省份切换，生成一个新的处理者，上一个没有被及时注销的，依然可以，继续处理消息
     */
	HomenoticeConsumer homenoticeConsumer = null;

	private class HomenoticeConsumer implements IDataConsumer {

		private Map<Object, List<Long>> datas = new HashMap<Object, List<Long>>();
		private Set<Object> receiving = new HashSet<Object>();
		private Set<Object> receiveFailed = new HashSet<Object>();

		@Override
		public String[] getAllReceivedDataKeys() {
			// TODO Auto-generated method stub
			return datas.keySet().toArray(new String[datas.size()]);
		}

		@Override
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

		@Override
		public void dataReceived(Object key, byte[] data) {
			// TODO Auto-generated method stub

			try {
				final NotifyMessage[] ppdatas = fromBytes(data);
				if (ppdatas != null) {
					if (AppUtils.getFramework().getService(
							IUserUsageDataRecorder.class) != null) {
						AppUtils.getFramework()
								.getService(IUserUsageDataRecorder.class)
								.doRecord(ActivityID.SYSNOTICE.ordinal());
					}

					NoticeDao dao = NoticeDao.getInstance(context
							.getApplication().getAndroidApplication()
							.getApplicationContext());

					dao.inserData(ppdatas, mprovince);
					List<Long> ids = new ArrayList<Long>();
					for (NotifyMessage notifyMessage : ppdatas) {
						List<NotifyMessage> list = new ArrayList<NotifyMessage>();
						list.add(notifyMessage);
						ids.add(notifyMessage.getId());
					}

					context.getExecutor().execute(
							new AbstractMonitorRunnable(null, log) {
								@Override
								protected Object executeTask() throws Throwable {
									NoticeDao dao = NoticeDao
											.getInstance(context
													.getApplication()
													.getAndroidApplication()
													.getApplicationContext());
									NotifyMessage msg = dao
											.getTitleNotifyMessDataByType(mprovince);
									if (msg != null && msg.getId() != null) {
										dao.setColseByUser(msg.getId(),
												mprovince);
										Tools.showNitify(context
												.getApplication()
												.getAndroidApplication(), msg);
									}
									return null;
								}
							});
					datas.put(key, ids);
				} else {
					datas.remove(key);
				}
				if (log.isDebugEnabled()) {
					log.debug("Received data for the key:" + key);
					log.debug(String.format("key:[%s]", key));
				}
				// processReceivedData(key, datas);
				// dataChanged = true;
				receiving.remove(key);
				receiveFailed.remove(key);
			} catch (Exception e) {
				log.warn("Failed to deserilize the data", e);
			}
		}

		@Override
		public void dateReceiving(Object key) {
			// TODO Auto-generated method stub

			if (log.isDebugEnabled()) {
				log.debug("Receiving data for the key:" + key);
			}
			receiving.add(key);
		}

		@Override
		public void dataReceivingFailed(Object key) {
			// TODO Auto-generated method stub
			if (log.isDebugEnabled()) {
				log.debug("Failed to Receive data for the key:" + key);
			}
			receiving.remove(key);
			receiveFailed.add(key);
		}

		@Override
		public void allDataReceived() {
			// TODO Auto-generated method stub
			if (log.isDebugEnabled()) {
				log.debug("succful to Receive  alldata for the ");
			}
			processAllDataReceived();
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
			// storeMessageIds(phoneNumber, list);
			List<Long> local_storage_ids = loadAllValidMsgIds();
			if (local_storage_ids != null) {
				local_storage_ids.removeAll(all_ids);// 获取本地有，服务器端没有的消息集合
				if (!local_storage_ids.isEmpty()) {
					for (Long msgId : local_storage_ids) {
						// try {
						// dataChanged = true;
						// updateStatus(msgId,
						// TYPE_STATUS_MSG_STATUS_PREFIX+phoneNumber,
						// SSHXMsgStatus.COMPLETED.name());
						// } catch (Exception e) {
						// log.warn("Error when update the msg status", e);
						// }
					}
				}
			}
			// if (dataChanged) {
			// cache = loadBroadcastableSSHXMsgs();
			// dataChanged = false;
			// }

		}

		@Override
		public byte[] removeReceivedData(Object key) {
			List<Long> ids = datas.remove(key);
			Collections.sort(ids, idcompare);
			return toBytes(getNoticeMessages(ids));
		}

		@Override
		public byte[] getReceivedData(Object key) {
			List<Long> idsnew = new ArrayList<Long>();
			HashMap<Long, String> keys = new HashMap<Long, String>();

			List<Long> ids = datas.get(key);
			List<Long> valid_ids = loadAllValidMsgIds();

			if (ids != null && valid_ids != null) {
				ids.retainAll(valid_ids);
				Collections.sort(ids, idcompare);

				return toBytes(getNoticeMessages(ids));
			}

			return null;
		}

		private Comparator<Long> idcompare = new Comparator<Long>() {
			public int compare(Long o1, Long o2) {

				return (int) (o1 - o2);
			}
		};

		public NotifyMessage getNoticeByID(Long msgId) throws Exception {
			if (msgId == null) {
				return null;
			}

			NotifyMessage bo = null;
			try {
				bo = NoticeDao.getInstance(
						context.getApplication().getAndroidApplication())
						.fetchByID(msgId, mprovince);
				return bo;
			} catch (Exception e) {
				log.error("Read sshx msg error", e);
				throw e;
			}

		}

		private List<NotifyMessage> getNoticeMessages(List<Long> ids) {

			if (ids != null && ids.size() > 0) {
				List<NotifyMessage> ret = new ArrayList<NotifyMessage>();
				try {
					for (Long id : ids) {
						NotifyMessage msg = getNoticeByID(id);
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

		private Comparator<NotifyMessage> msgComparator = new Comparator<NotifyMessage>() {
			public int compare(NotifyMessage o1, NotifyMessage o2) {
				int r = 0;
				r = (int) (o1.getId() - o2.getId());
				if (r > 0) {
					r = 1;
				} else if (r < 0) {
					r = -1;
				}
				return r;
			}
		};
		private String mprovince;

		public byte[] toBytes(List<NotifyMessage> data) {
			if (data == null) {
				return null;
			}
			if (data != null && !data.isEmpty()) {
				Collections.sort(data, msgComparator);
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = null;
			NotifyMessage[] infos = data
					.toArray(new NotifyMessage[data.size()]);
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

		private List<Long> loadAllValidMsgIds() {
			return NoticeDao.getInstance(
					context.getApplication().getAndroidApplication())
					.fetchAllData(mprovince);
		}

		public void setCurProvinceCode(String code) {
			// TODO Auto-generated method stub
			mprovince = code;
		}
	};

	private String curkey = null;

	@Override
	protected void initServiceDependency() {
		// TODO Auto-generated method stub
		addRequiredService(IMTreeSyncDataEngineService.class);

		// addRequiredService(IMTreeDataSyncClientService.class);
	}

	protected NotifyMessage[] fromBytes(byte[] data) throws Exception {
		if (data == null) {
			return null;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		NotifyMessage[] bean;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			bean = (NotifyMessage[]) ois.readObject();

			return bean;
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

	@Override
	protected void startService() {
		// TODO Auto-generated method stub
		context.registerService(IHomeNotice.class, this);

	}

	@Override
	protected void stopService() {
		// TODO Auto-generated method stub
		context.unregisterService(IHomeNotice.class, this);
	}

	boolean needstarted = true;

	@Override
	public void getHomeNotice(final String pprovincecode) {
		needstarted = true;
		// 如何以前注册过其他分省的，则先移除，再添加，不是实时的，可能这个正在处理中
		//，需要在下一轮，处理中，才能停止掉
		if (curkey != null) {
			IMTreeSyncDataEngineService AO = getService(IMTreeSyncDataEngineService.class);
			AO.unregisterConsumer(curkey, homenoticeConsumer);
			needstarted = false;
		}

		curkey = SYNC_KEY + "/" + pprovincecode;
		context.getExecutor().execute(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					IMTreeSyncDataEngineService AO = getService(IMTreeSyncDataEngineService.class);
					homenoticeConsumer = new HomenoticeConsumer();
					homenoticeConsumer.setCurProvinceCode(pprovincecode);
					AO.registerConsumer(curkey, homenoticeConsumer);

					if (needstarted) {
						AO.startSync();
					}
				} catch (Exception ee) {
					if (log.isDebugEnabled()) {
						log.debug("start  NoticeSyncModule  err", ee.toString());
					}
				}
			}
		});

	}

}
