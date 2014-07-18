/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

import com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.IPiggybackService;
import com.wxxr.callhelper.qg.IUserUsageDataRecorder;
import com.wxxr.callhelper.qg.PiggybackPayloadType;
import com.wxxr.mobile.core.event.api.IEventRouter;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.microkernel.api.AbstractModule;

/**
 * @author neillin
 *
 */
public class UserUsageRecorderModule extends
		AbstractModule<ComHelperAppContext> implements IUserUsageDataRecorder {
	private static final Trace log = Trace.register(UserUsageRecorderModule.class);
	
	private static class UsageRecord {
		long timestamp;
		int activityId;
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + activityId;
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
			UsageRecord other = (UsageRecord) obj;
			if (activityId != other.activityId)
				return false;
			if (timestamp != other.timestamp)
				return false;
			return true;
		}
	}
	
	
	private AbstractPigyybackPayloadProvider<UsageRecord> provider = new AbstractPigyybackPayloadProvider<UsageRecord>() {

		@Override
		protected UsageRecord loadRecord(DataInput input) throws IOException {
			try {
				UsageRecord rcd = new UsageRecord();
				rcd.timestamp = input.readLong();
				rcd.activityId = input.readInt();
				return rcd;
			}catch(EOFException e){
				return null;
			}
		}

		@Override
		protected void writeRecord(DataOutput output, UsageRecord record)
				throws IOException {
			output.writeLong(record.timestamp);
			output.writeInt(record.activityId);
			
		}

		@Override
		protected void writeBuffer(ByteBuffer buffer, UsageRecord record)
				throws IOException {
			buffer.putLong(record.timestamp);
			buffer.putInt(record.activityId);
		}

		@Override
		protected String getDataStoageFileName() {
			return "usage.txt";
		}
	};

	/* (non-Javadoc)
	 * @see com.wxxr.callhelper.IUserUsageDataRecorder#userVisitActivity(int)
	 */
	@Override
	public void doRecord(int activityId) {
		UsageRecord rcd = new UsageRecord();
		rcd.activityId = activityId;
		rcd.timestamp = System.currentTimeMillis();
		this.provider.addRecord(rcd);
	}


	@Override
	protected void initServiceDependency() {
		addRequiredService(IPiggybackService.class);
		addRequiredService(IEventRouter.class);
	}

	@Override
	protected void startService() {
		this.provider.start(context);
		context.getService(IPiggybackService.class).registerProvider(PiggybackPayloadType.USER_USAGE.ordinal(), provider);
		context.registerService(IUserUsageDataRecorder.class, this);
	}

	@Override
	protected void stopService() {
		context.unregisterService(IUserUsageDataRecorder.class, this);
		IPiggybackService service = context.getService(IPiggybackService.class);
		if(service != null){
			service.unregisterProvider(PiggybackPayloadType.USER_USAGE.ordinal(), provider);
		}
		this.provider.stop();
	}


	/**
	 * @return
	 * @see com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider#getLastUpdateTime()
	 */
	public long getLastUpdateTime() {
		return provider.getLastUpdateTime();
	}


	/**
	 * @return
	 * @see com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider#getUpdateIntervalInSeconds()
	 */
	public int getUpdateIntervalInSeconds() {
		return provider.getUpdateIntervalInSeconds();
	}


	/**
	 * @param updateIntervalInSeconds
	 * @see com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider#setUpdateIntervalInSeconds(int)
	 */
	public void setUpdateIntervalInSeconds(int updateIntervalInSeconds) {
		provider.setUpdateIntervalInSeconds(updateIntervalInSeconds);
	}


	/**
	 * @return
	 * @see com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider#getMaxPerPayloadSize()
	 */
	public int getMaxPerPayloadSize() {
		return provider.getMaxPerPayloadSize();
	}


	/**
	 * @param maxPerPayloadSize
	 * @see com.wxxr.callhelper.qg.AbstractPigyybackPayloadProvider#setMaxPerPayloadSize(int)
	 */
	public void setMaxPerPayloadSize(int maxPerPayloadSize) {
		provider.setMaxPerPayloadSize(maxPerPayloadSize);
	}


}
