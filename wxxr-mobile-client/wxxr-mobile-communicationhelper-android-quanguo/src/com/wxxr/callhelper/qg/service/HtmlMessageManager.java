package com.wxxr.callhelper.qg.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.wxxr.callhelper.qg.R;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.bean.DownloadTaskInfo;
import com.wxxr.callhelper.qg.bean.DownloadTaskInfoStatus;
import com.wxxr.callhelper.qg.bean.HtmlMessage;
import com.wxxr.callhelper.qg.utils.BytesUtils;
import com.wxxr.mobile.core.api.IDataExchangeCoordinator;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;
import com.wxxr.mobile.core.util.StringUtils;
import com.wxxr.mobile.web.grabber.api.IWebGrabberService;

public class HtmlMessageManager extends AbstractModule<ComHelperAppContext> implements IHtmlMessageManager {

	private Map<String ,Integer> failedCount=new ConcurrentHashMap<String ,Integer>();
	private static final String DOWN_STATUS = "DownloadStatus";
	private static final String TASKINFO_CONTENT_TYPE = "taskInfo";
	private static final String SEQ_CONTENT_TYPE = "seq";
	private static final String SEQ_ID = "seq";
	private HtmlDownloadTask task=new HtmlDownloadTask();
	private int failedMax=5;
	private static Trace log = Trace.register(HtmlMessageManager.class);
	@Override
	public String download(String url)throws IOException {
		DownloadTaskInfo taskInfo=new DownloadTaskInfo();
		taskInfo.setUrl(url);
		Long seq=1L;
		synchronized (this) {
			byte[] data=context.getService(IContentManager.class).getContent(SEQ_CONTENT_TYPE, SEQ_ID);
			if(data!=null){
				seq=BytesUtils.byteToLong(data)+1;
			}
			context.getService(IContentManager.class).saveContent(SEQ_CONTENT_TYPE, SEQ_ID, BytesUtils.longToByte(seq));
		}
		taskInfo.setId(seq.toString());
		byte[] data=toBytes(taskInfo);
		if(data==null){
			throw new IOException("to bytes exception");
		}
		context.getService(IContentManager.class).saveContent(TASKINFO_CONTENT_TYPE, taskInfo.getId(), data);
		context.getService(IContentManager.class).updateStatus(TASKINFO_CONTENT_TYPE, taskInfo.getId(), DOWN_STATUS, DownloadTaskInfoStatus.NEW.name());
		if(log.isDebugEnabled()){
			log.debug("saved download task:"+taskInfo);
		}
		task.wakeup();
		return taskInfo.getId();
		
	}

	@Override
	public HtmlMessage getHtmlMessage(String id) {
		if(log.isDebugEnabled()){
			log.debug("getHtmlMessage id:"+id);
		}
		return this.context.getService(IContentManager.class).getHtmlMessage(id);
	}

	@Override
	protected void initServiceDependency() {
		addRequiredService(IContentManager.class);
	}

	@Override
	protected void startService() {
		String max=	context.getApplication().getAndroidApplication().getResources().getString(R.string.html_failed_max);
		try {
			failedMax=Integer.parseInt(max);
		} catch (NumberFormatException e) {
			log.error("parse failed max error",e);
		}

		context.registerService(IHtmlMessageManager.class, this);
		task.start();
		if(log.isDebugEnabled()){
			log.debug("startService");
		}
	}

	@Override
	protected void stopService() {

		context.unregisterService(IHtmlMessageManager.class, this);
		task.stop();
		if(log.isDebugEnabled()){
			log.debug("stopService");
		}
	}
	
	class HtmlDownloadTask implements Runnable{

		private Thread currentThread;
		private volatile boolean keepAlive;
		private long sleep=10*60*1000L;
		@Override
		public void run() {
			this.currentThread = Thread.currentThread();
			this.currentThread.setName("HtmlDownloadTask Thread");
			while((this.currentThread != null)&&keepAlive){
				try {
					doHouseKeeping();
				} catch (Throwable e) {
					log.error("Download html failed", e);
				}
			}
		}

		private void doHouseKeeping() {

			try {
				if (!availableNetwork()) {
					sleep = 10 * 60 * 1000L;
				}

				if (log.isDebugEnabled()) {
					log.debug("query not done html task");
				}
				IContentManager contentManager = context
						.getService(IContentManager.class);
				Set<String> ids = new HashSet<String>();
				try {
					String[] ids1 = contentManager.queryContentIds(
							TASKINFO_CONTENT_TYPE, DOWN_STATUS,
							DownloadTaskInfoStatus.NEW.name());
					if (ids1 != null) {
						ids.addAll(Arrays.asList(ids1));
					}
					String[] ids2 = contentManager.queryContentIds(
							TASKINFO_CONTENT_TYPE, DOWN_STATUS,
							DownloadTaskInfoStatus.FAILED.name());

					if (ids2 != null) {
						ids.addAll(Arrays.asList(ids2));
					}
				} catch (IOException e) {
					log.error("queryContentIds error", e);
				}

				if (ids == null || ids.isEmpty()) {

					if (log.isDebugEnabled()) {
						log.debug("Don't have not done status html task");
					}
					sleep = 10 * 60 * 1000L;
				}
				for (String id : ids) {
					try {
						if (log.isDebugEnabled()) {
							log.debug("Down id:" + id + "html task");
						}
						byte[] data = contentManager.getContent(
								TASKINFO_CONTENT_TYPE, id);
						DownloadTaskInfo taskInfo = fromBytes(data);
						boolean download = context.getService(
								IWebGrabberService.class).grabWebPage(
								taskInfo.getUrl(), taskInfo.getId());
						DownloadTaskInfoStatus status;
						if (download == true) {
							if (log.isDebugEnabled()) {
								log.debug("id:" + id
										+ "html task download successd");
							}
							status = DownloadTaskInfoStatus.DONE;
							sleep = 1;
						} else {
							if (log.isDebugEnabled()) {
								log.debug("id:" + id
										+ "html task download failed");
							}
							status = DownloadTaskInfoStatus.FAILED;
							sleep = 60 * 1000L;
							Integer count = failedCount.remove(id);
							if (count == null) {
								count = 0;
							} else {
								count = count + 1;
							}
							if (count >= failedMax) {
								status = DownloadTaskInfoStatus.ABORTED;
							} else {
								failedCount.put(id, count);
							}
						}
						try {
							context.getService(IContentManager.class)
									.updateStatus(TASKINFO_CONTENT_TYPE, id,
											DOWN_STATUS, status.name());
						} catch (IOException e) {
							log.error("更新DownloadTaskInfo Exception", e);
						}
					} catch (Throwable e) {
						log.error("download failed:" + id, e);
					}
				}
			} finally {
				synchronized (this) {
					try {
						wait(sleep);
					} catch (InterruptedException e) {
					}
				}
			}
		}

		public void start() {
			this.keepAlive = true;
			new Thread(this).start();
			if(log.isDebugEnabled()){
				log.debug("html task start");
			}
		}
		
		public void stop() {
			this.keepAlive = false;
			if(this.currentThread != null){
				this.currentThread.interrupt();
				try {
					this.currentThread.join(1000L);
				} catch (InterruptedException e) {
				}
				this.currentThread = null;
			}			if(log.isDebugEnabled()){
				log.debug("html task stop");
			}
			
		}	
		public synchronized void wakeup(){
			notifyAll();
		}
	}

	@Override
	public boolean isDownloaded(String id) {

		try {
			String s = context.getService(IContentManager.class).getStatus(
					TASKINFO_CONTENT_TYPE, id, DOWN_STATUS);
			if (StringUtils.isBlank(s)) {
				return false;
			}
			DownloadTaskInfoStatus status = DownloadTaskInfoStatus.valueOf(s);
			if (log.isDebugEnabled()) {
				log.debug("id:" + id + " status :" + status);
			}
			return DownloadTaskInfoStatus.DONE.equals(status);
		} catch (IOException e) {
			log.error("", e);
		}

		return false;

	}
	
	public DownloadTaskInfo fromBytes(byte[] data) throws Exception{
		if (data==null) {
			return null;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(bis);
			DownloadTaskInfo taskInfo=(DownloadTaskInfo) ois.readObject();
			return taskInfo;
		} catch (Exception e) {
			log.warn("Error when read msg from bytes", e);
			throw e;
		}finally {
			try {
				if (ois != null) {
					ois.close();
				}
				bis.close();
			} catch (IOException e) {
			}
		} 
		
	}
	public byte[] toBytes(DownloadTaskInfo taskInfo) {
		if (taskInfo==null) {
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(taskInfo);
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
	

	@Override
	public void removeHmtlMessage(String id) throws IOException {
		if(log.isDebugEnabled()){
			log.debug("removeHtmlMessage id:"+id);
		}
		IContentManager contentManager=context.getService(IContentManager.class);
		contentManager.removeHtmlMesage(id);
	} 
	private boolean availableNetwork() {
		return context.getService(IDataExchangeCoordinator.class).checkAvailableNetwork() != -1;
	}

	@Override
	public boolean syncDownload(String url, String root) {
		boolean download=context.getService(IExWebGrabberService.class).grabWebPage(url, root);
//		if(download){
//			try {
//				context.getService(IContentManager.class).moveHtmlMessage(root+".tmp", root);
//			} catch (IOException e) {
//				log.error("",e);
//				return false;
//			}
//		}
		return download;
	}
}
