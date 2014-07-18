/**
 * 
 */
package com.wxxr.callhelper.qg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;

import android.content.Context;

import com.wxxr.callhelper.qg.event.ApplicationExittingEvent;
import com.wxxr.mobile.android.app.IAndroidAppContext;
import com.wxxr.mobile.core.event.api.IBroadcastEvent;
import com.wxxr.mobile.core.event.api.IEventListener;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;

/**
 * @author neillin
 *
 */
public abstract class AbstractPigyybackPayloadProvider<T> implements IPiggybackPayloadProvider {
	private static final Trace log = Trace.register(AbstractPigyybackPayloadProvider.class);

	private IAndroidAppContext context;
	private LinkedList<T> records = new LinkedList<T>();
	private long lastUpdateTime = System.currentTimeMillis();
	private IEventListener appExitListener = new IEventListener() {

		@Override
		public void onEvent(IBroadcastEvent event) {
			if(event instanceof ApplicationExittingEvent){
				log.warn("Application is exitting, going to save unsaved user records");
				synchronized(records){
					if(records.isEmpty()){
						return;
					}
					String dir = context.getApplication().getAndroidApplication()
							.getDir("statistics", Context.MODE_PRIVATE).getPath();
					File file = new File(dir);
					if(!file.exists()){
						file.mkdirs();
					}
					file = new File(file,getDataStoageFileName());
					RandomAccessFile rf = null;
					try {
						rf = new RandomAccessFile(file, "rw");
						rf.writeInt(records.size());
						for (T rcd : records) {
							writeRecord(rf,rcd);
							//							rf.writeLong(rcd.timestamp);
							//							rf.writeInt(rcd.activityId);
						}
					} catch (IOException e) {
						log.warn("Failed to save unsaved usage data", e);
					}finally {
						if(rf != null){
							try {
								rf.close();
							} catch (IOException e) {
							}
						}
					}

				}
			}
		}
	};
	private int updateIntervalInSeconds = 10*60;
	private int maxPerPayloadSize = 1024*4;		// 4K

	@Override
	public byte[] getPayload() {		
		if(!context.getApplication().isInDebugMode() &&(System.currentTimeMillis() - lastUpdateTime) < updateIntervalInSeconds*1000L){
			return null;
		}
		synchronized(records){
			if(records.isEmpty()){
				return null;
			}
			try {
				ByteBuffer buf = ByteBuffer.allocate(this.maxPerPayloadSize);		// 4k
				String id = context.getApplication().getDeviceId();
				if(id != null){
					byte[] data = id.getBytes("UTF-8");
					buf.putInt(data.length);
					buf.put(data);
				}else{
					buf.putInt(0);
				}
				int recordSize = 0;
				int pos = buf.position();
				while(!records.isEmpty()){
					T rcd = records.removeFirst();
					writeBuffer(buf, rcd);
					if(recordSize == 0){
						recordSize = buf.position()-pos;
					}
					//						buf.putLong(rcd.timestamp);
					//						buf.putInt(rcd.activityId);
					if(buf.position() > (buf.limit() - recordSize)){	// left space is not enough for next record
						break;
					}
				}
				byte[] data = new byte[buf.position()];
				buf.flip();
				buf.get(data);
				lastUpdateTime = System.currentTimeMillis();
				return data;
			} catch (IOException e) {
				// should never happen
				return null;
			}
		}
	}

	private void loadRecord() {
		String dir = context.getApplication().getDataDir("usages", Context.MODE_PRIVATE).getPath();
		File file = new File(dir);
		file = new File(file,"usage.txt");
		if(!file.exists()){
			return;
		}
		RandomAccessFile rf = null;
		try {
			rf = new RandomAccessFile(file, "r");
			int size = rf.readInt();
			if(size > 0){
				T rcd = null;
				while((rcd = loadRecord(rf)) != null){
					//					UsageRecord rcd = new UsageRecord();
					//					rcd.timestamp = rf.readLong();
					//					rcd.activityId = rf.readInt();
					this.records.add(rcd);
				}
				lastUpdateTime = 0L;
			}
			file.delete();
		} catch (IOException e) {
			log.warn("Failed to load unsaved usage data", e);
		}finally {
			if(rf != null){
				try {
					rf.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public void addRecord(T record){
		synchronized(this.records){
			if(!this.records.contains(record)){
				this.records.addLast(record);
			}
		}
	}
	

	public void start(IAndroidAppContext ctx) {
		this.context = ctx;
		loadRecord();
		context.getService(IEventRouter.class).registerEventListener(ApplicationExittingEvent.class, this.appExitListener);
	}

	public void stop() {
		if(context.getService(IEventRouter.class) != null) {
			context.getService(IEventRouter.class).unregisterEventListener(ApplicationExittingEvent.class, this.appExitListener);
		}
		this.context = null;
	}

	
	

	protected abstract T loadRecord(DataInput input) throws IOException;
	protected abstract void writeRecord(DataOutput ouput,T record) throws IOException;
	protected abstract void writeBuffer(ByteBuffer buffer,T record) throws IOException;
	protected abstract String getDataStoageFileName();

	/**
	 * @return the lastUpdateTime
	 */
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	/**
	 * @return the updateIntervalInSeconds
	 */
	public int getUpdateIntervalInSeconds() {
		return updateIntervalInSeconds;
	}

	/**
	 * @param updateIntervalInSeconds the updateIntervalInSeconds to set
	 */
	public void setUpdateIntervalInSeconds(int updateIntervalInSeconds) {
		this.updateIntervalInSeconds = updateIntervalInSeconds;
	}

	/**
	 * @return the maxPerPayloadSize
	 */
	public int getMaxPerPayloadSize() {
		return maxPerPayloadSize;
	}

	/**
	 * @param maxPerPayloadSize the maxPerPayloadSize to set
	 */
	public void setMaxPerPayloadSize(int maxPerPayloadSize) {
		this.maxPerPayloadSize = maxPerPayloadSize;
	}


}
