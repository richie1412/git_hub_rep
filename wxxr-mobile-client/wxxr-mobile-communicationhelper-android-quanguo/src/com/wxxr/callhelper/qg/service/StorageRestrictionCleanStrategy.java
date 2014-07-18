/**
 * 
 */
package com.wxxr.callhelper.qg.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;
import android.os.StatFs;

import com.wxxr.callhelper.qg.CleanContext;
import com.wxxr.callhelper.qg.ComHelperAppContext;
import com.wxxr.callhelper.qg.ContentCleanStategy;
import com.wxxr.callhelper.qg.ISSHXMsgManagementModule;
import com.wxxr.mobile.core.log.api.Trace;
import com.wxxr.mobile.core.util.StringUtils;

/**
 * 内容清除策略 当可用空间所占容量小于40%，将清除已读的过期消息
 * 
 * @author wangxuyang
 * 
 */
public class StorageRestrictionCleanStrategy implements ContentCleanStategy {
	private static final Trace log = Trace.register(StorageRestrictionCleanStrategy.class);
	long threshold = 40;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.callhelper.ContentCleanStategy#isCleanable(com.wxxr.callhelper
	 * .CleanContext)
	 */
	public boolean isCleanable(CleanContext context) { // 当可用空间小于40%时，需要进行清除
	/*	long curent_availiable_storage_percent = getAvailiableStoragePercent();
		if (log.isDebugEnabled()) {
			log.debug("Current availiable storage percent:" + curent_availiable_storage_percent);
		}
		return curent_availiable_storage_percent < threshold;*/
		return true;
	}

	private long getAvailiableStoragePercent() {
		File root = Environment.getRootDirectory();
		if (existSDCard()) {
			root = Environment.getExternalStorageDirectory();
			if (log.isDebugEnabled()) {
				log.debug("Exist sdcard:"+root.getAbsolutePath());
			}			
		}else{
			if (log.isDebugEnabled()) {
				log.debug("No sdcard:"+root.getAbsolutePath());
			}	
		}
		StatFs sf = new StatFs(root.getPath());
		// long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();
		return availCount * 100/ blockCount;
	}

	private boolean existSDCard() {
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.wxxr.callhelper.ContentCleanStategy#executeClean(com.wxxr.callhelper
	 * .CleanContext)
	 */
	public void executeClean(CleanContext context) throws IOException {
		ComHelperAppContext ctx = context.getAppContext();
		// List<Long> ids =
		// ctx.getService(IContentManager.class).getSshxMsgs(SSHXMsgStatus.COMPLETED,SSHXMsgLifeStatus.SAVEED);
		Long[] ids = ctx.getService(ISSHXMsgManagementModule.class).getPrepareRemovedMsgs();
		List<Long> deletedMsgs = new ArrayList<Long>();
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d msgs will be prepared to be deleted", ids==null?0:ids.length));
		}
		if (ids != null && ids.length > 0) {
			
			try {
				for (Long id : ids) {
					ctx.getService(ISSHXMsgManagementModule.class).removeSSHX(id);
					/*String htmlId = ctx.getService(ISSHXMsgManagementModule.class).getHtmlIdByMsgId(id);
					if (StringUtils.isNotBlank(htmlId)) {
						ctx.getService(IHtmlMessageManager.class).removeHmtlMessage(htmlId);
					}*/
					deletedMsgs.add(id);
				}
			} catch (Exception e) {
				if (log.isDebugEnabled()) {
					log.debug(String.format("Error when clean messages", deletedMsgs.size()), e);
				}
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(String.format("%d msgs deleted.", deletedMsgs.size()));
		}

	}

}
